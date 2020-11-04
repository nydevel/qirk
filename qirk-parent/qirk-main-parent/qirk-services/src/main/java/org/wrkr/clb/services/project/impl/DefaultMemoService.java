package org.wrkr.clb.services.project.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.common.jms.message.statistics.NewMemoMessage;
import org.wrkr.clb.common.jms.services.StatisticsSender;
import org.wrkr.clb.common.util.datetime.DateTimeUtils;
import org.wrkr.clb.model.project.Memo;
import org.wrkr.clb.model.project.Project;
import org.wrkr.clb.model.project.ProjectMember;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.repo.project.JDBCProjectMemberRepo;
import org.wrkr.clb.repo.project.MemoRepo;
import org.wrkr.clb.repo.project.ProjectMemberRepo;
import org.wrkr.clb.repo.project.ProjectRepo;
import org.wrkr.clb.services.dto.project.MemoDTO;
import org.wrkr.clb.services.dto.project.MemoReadDTO;
import org.wrkr.clb.services.project.MemoService;
import org.wrkr.clb.services.security.ProjectSecurityService;
import org.wrkr.clb.services.util.exception.ApplicationException;
import org.wrkr.clb.services.util.exception.NotFoundException;

@Validated
@Service
public class DefaultMemoService implements MemoService {

    @Autowired
    private MemoRepo memoRepo;

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private ProjectMemberRepo projectMemberRepo;

    @Autowired
    private JDBCProjectMemberRepo jdbcProjectMemberRepo;

    @Autowired
    private ProjectSecurityService securityService;

    @Autowired
    private StatisticsSender statisticsSender;

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public MemoReadDTO create(User currentUser, MemoDTO memoDTO) throws ApplicationException {
        // security start
        securityService.authzCanCreateMemo(currentUser, memoDTO.project);
        // security finish

        Project project = null;
        if (memoDTO.project.id != null) {
            project = projectRepo.get(memoDTO.project.id);
        } else if (memoDTO.project.uiId != null) {
            project = projectRepo.getByUiId(memoDTO.project.uiId.strip());
        }
        if (project == null) {
            throw new NotFoundException("Project");
        }

        Memo memo = new Memo();
        memo.setProject(project);
        memo.setBody(memoDTO.body.strip());

        ProjectMember author = jdbcProjectMemberRepo.getNotFiredByUserIdAndProjectId(currentUser.getId(), project.getId());
        memo.setAuthor(author);

        memo.setCreatedAt(DateTimeUtils.now());
        memoRepo.persist(memo);

        // statistics
        statisticsSender.send(new NewMemoMessage(currentUser.getId(), memo.getCreatedAt().toInstant().toEpochMilli()));
        // statistics

        return MemoReadDTO.fromEntity(memo, true);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public List<MemoReadDTO> listByProjectId(User currentUser, Long projectId) {
        // security start
        securityService.authzCanReadMemos(currentUser, projectId);
        // security finish

        List<Memo> memoList = memoRepo.listByProjectIdAndFetchAuthor(projectId);

        ProjectMember currentProjectMember = null;
        if (currentUser != null) {
            currentProjectMember = projectMemberRepo.getNotFiredByUserAndProjectId(
                    currentUser, projectId);
        }
        return MemoReadDTO.fromEntities(memoList, currentProjectMember);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true)
    public List<MemoReadDTO> listByProjectUiId(User currentUser, String projectUiId) {
        // security start
        securityService.authzCanReadMemos(currentUser, projectUiId);
        // security finish

        List<Memo> memoList = memoRepo.listByProjectUiIdAndFetchAuthor(projectUiId);

        ProjectMember currentProjectMember = null;
        if (currentUser != null) {
            currentProjectMember = projectMemberRepo.getNotFiredByUserAndProjectUiId(
                    currentUser, projectUiId);
        }
        return MemoReadDTO.fromEntities(memoList, currentProjectMember);
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class)
    public void delete(User currentUser, Long id) throws ApplicationException {
        // security start
        Long memoId = securityService.authzCanDeleteMemo(currentUser, id);
        // security finish

        if (memoId == null) {
            throw new NotFoundException("Memo");
        }
        memoRepo.deleteById(memoId);
    }

}
