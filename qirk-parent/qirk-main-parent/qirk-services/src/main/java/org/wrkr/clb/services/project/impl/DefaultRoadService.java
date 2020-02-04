/*
 * This file is part of the Java API to Qirk.
 * Copyright (C) 2020 Memfis LLC, Russia
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.wrkr.clb.services.project.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.model.project.Road;
import org.wrkr.clb.model.project.task.TaskCard;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.repo.project.JDBCProjectRepo;
import org.wrkr.clb.repo.project.RoadRepo;
import org.wrkr.clb.repo.project.task.TaskRepo;
import org.wrkr.clb.repo.project.task.TaskCardRepo;
import org.wrkr.clb.services.dto.MoveDTO;
import org.wrkr.clb.services.dto.project.RoadDTO;
import org.wrkr.clb.services.dto.project.RoadReadDTO;
import org.wrkr.clb.services.dto.project.task.TaskCardReadDTO;
import org.wrkr.clb.services.impl.BaseVersionedEntityService;
import org.wrkr.clb.services.project.RoadService;
import org.wrkr.clb.services.project.task.TaskCardService;
import org.wrkr.clb.services.security.ProjectSecurityService;
import org.wrkr.clb.services.util.exception.ApplicationException;
import org.wrkr.clb.services.util.exception.ConflictException;
import org.wrkr.clb.services.util.exception.NotFoundException;


@Validated
@Service
public class DefaultRoadService extends BaseVersionedEntityService implements RoadService {

    @Autowired
    private RoadRepo roadRepo;

    @Autowired
    private JDBCProjectRepo projectRepo;

    @Autowired
    private TaskCardRepo cardRepo;

    @Autowired
    private TaskCardService cardService;

    @Autowired
    private TaskRepo taskRepo;

    @Autowired
    private ProjectSecurityService securityService;

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public RoadReadDTO create(User currentUser, RoadDTO roadDTO) throws ApplicationException {
        // security
        securityService.authzCanModifyRoads(currentUser, roadDTO.projectId);
        // security

        Long organizationId = projectRepo.getOrganizationIdById(roadDTO.projectId);
        if (organizationId == null) {
            throw new NotFoundException("Project");
        }

        Long previousId = roadRepo.getLastIdByProjectId(roadDTO.projectId);

        Road road = new Road();
        road.setOrganizationId(organizationId);
        road.setProjectId(roadDTO.projectId);
        road.setName(roadDTO.name);
        road.setPreviousId(previousId);

        roadRepo.save(road);

        return RoadReadDTO.fromEntity(road);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public RoadReadDTO update(User currentUser, RoadDTO roadDTO) throws ApplicationException {
        // security
        securityService.authzCanModifyRoad(currentUser, roadDTO.id);
        // security

        Road road = roadRepo.getRecordVersionById(roadDTO.id);
        if (road == null) {
            throw new NotFoundException("Road");
        }
        road = checkRecordVersion(road, roadDTO.recordVersion);

        road.setName(roadDTO.name);
        roadRepo.updateRecordVersionAndName(road);
        return RoadReadDTO.fromEntity(road);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public void move(User currentUser, MoveDTO moveDTO) throws ApplicationException {
        // security
        securityService.authzCanModifyRoad(currentUser, moveDTO.id);
        // security

        if (moveDTO.id.equals(moveDTO.previousId)) {
            return; // no move needed
        }
        if (Objects.equals(moveDTO.oldPreviousId, moveDTO.previousId)) {
            return; // no move needed
        }

        Road road = roadRepo.getForMoveById(moveDTO.id);
        if (road == null) {
            throw new NotFoundException("Road");
        }
        if (!Objects.equals(moveDTO.oldPreviousId, road.getPreviousId())
                || !Objects.equals(moveDTO.oldNextId, road.getNextId())) {
            throw new ConflictException("Concurrent moving occured.");
        }

        if (moveDTO.previousId == null) {
            if (road.getNextId() != null) {
                roadRepo.updatePreviousId(road.getPreviousId(), road.getNextId());
            }

            Long newNextId = roadRepo.getFirstIdByProjectId(road.getProjectId());
            if (!Objects.equals(moveDTO.nextId, newNextId)) {
                throw new ConflictException("Concurrent moving occured.");
            }

            roadRepo.updatePreviousId(road.getId(), newNextId);
            roadRepo.setPreviousIdToNull(road.getId());
            return;

        } else {
            Road newPreviousRoad = roadRepo.getForMoveByIdAndProjectId(moveDTO.previousId, road.getProjectId());
            if (newPreviousRoad == null) {
                throw new NotFoundException("Road");
            }
            if (!Objects.equals(moveDTO.nextId, newPreviousRoad.getNextId())) {
                throw new ConflictException("Concurrent moving occured.");
            }

            if (road.getNextId() != null) {
                roadRepo.updatePreviousId(road.getPreviousId(), road.getNextId());
            }
            if (newPreviousRoad.getNextId() != null) {
                roadRepo.updatePreviousId(road.getId(), newPreviousRoad.getNextId());
            }
            roadRepo.updatePreviousId(newPreviousRoad.getId(), road.getId());
        }
    }

    private TaskCardReadDTO getCardDTO(TaskCard card) {
        return TaskCardReadDTO.fromEntityWithTasks(card);
    }

    private RoadReadDTO getDTO(Road road) {
        RoadReadDTO dto = RoadReadDTO.fromEntity(road);
        List<TaskCard> cardList = road.getCards();

        TaskCard lastCard = null;
        Map<Long, TaskCard> cardIdToNextCard = new HashMap<Long, TaskCard>(cardList.size());
        List<TaskCardReadDTO> dtoList = new ArrayList<TaskCardReadDTO>(cardList.size());
        for (TaskCard card : cardList) {
            if (card.getPreviousId() == null) {
                lastCard = card;
                dtoList.add(getCardDTO(lastCard));
            } else {
                cardIdToNextCard.put(card.getPreviousId(), card);
            }
        }

        while (lastCard != null) {
            // remove is a safety measure against recursion if data is corrupted
            lastCard = cardIdToNextCard.remove(lastCard.getId());
            if (lastCard != null) {
                dtoList.add(getCardDTO(lastCard));
            }
        }

        dto.cards = dtoList;
        return dto;
    }

    private List<RoadReadDTO> getDTOList(List<Road> roadList) {
        Road lastRoad = null;
        Map<Long, Road> roadIdToNextRoad = new HashMap<Long, Road>(roadList.size());
        List<RoadReadDTO> dtoList = new ArrayList<RoadReadDTO>(roadList.size());
        for (Road road : roadList) {
            if (road.getPreviousId() == null) {
                lastRoad = road;
                dtoList.add(getDTO(lastRoad));
            } else {
                roadIdToNextRoad.put(road.getPreviousId(), road);
            }
        }

        while (lastRoad != null) {
            // remove is a safety measure against recursion if data is corrupted
            lastRoad = roadIdToNextRoad.remove(lastRoad.getId());
            if (lastRoad != null) {
                dtoList.add(getDTO(lastRoad));
            }
        }

        return dtoList;
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public List<RoadReadDTO> list(User currentUser, Long projectId) {
        // security
        securityService.authzCanModifyRoads(currentUser, projectId);
        // security

        List<Road> roadList = roadRepo.listByProjectIdAndFetchCardsAndTasks(projectId);
        return getDTOList(roadList);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public List<RoadReadDTO> list(User currentUser, String projectUiId) {
        // security
        securityService.authzCanModifyRoads(currentUser, projectUiId);
        // security

        List<Road> roadList = roadRepo.listByProjectUiIdAndFetchCardsAndTasks(projectUiId);
        return getDTOList(roadList);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public void delete(User currentUser, Long id) throws ApplicationException {
        // security
        securityService.authzCanModifyRoad(currentUser, id);
        // security

        Road road = roadRepo.getForMoveById(id);
        if (road == null) {
            throw new NotFoundException("Road");
        }

        if (road.getNextId() != null) {
            roadRepo.updatePreviousId(road.getPreviousId(), road.getNextId());
        }

        if (cardRepo.existsByRoadId(id)) {
            roadRepo.setDeletedToTrue(id);
            cardService.archiveBatchByRoadId(id);
            taskRepo.setCardIdToNullAndHiddenToFalseByRoadId(id); // temporary measure
        } else {
            roadRepo.delete(id);
        }
    }
}
