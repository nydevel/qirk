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
package org.wrkr.clb.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.wrkr.clb.model.Tag;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.repo.TagRepo;
import org.wrkr.clb.services.TagService;


@Service
public class DefaultTagService implements TagService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultTagService.class);

    @Autowired
    private TagRepo tagRepo;

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true, propagation = Propagation.MANDATORY)
    public List<Tag> getOrCreate(Set<String> tagNames) {
        // TODO: optimize with cash
        List<Tag> tags = new ArrayList<Tag>();
        for (String tagName : tagNames) {
            tagName = tagName.strip();
            if (tagName.isEmpty()) {
                continue;
            }

            Tag tag = tagRepo.getByName(tagName);
            if (tag == null) {
                tag = new Tag();
                tag.setName(tagName);
                tag = tagRepo.save(tag);
            }
            tags.add(tag);
        }
        return tags;
    }

    @Override
    @Transactional(value = "jpaTransactionManager", rollbackFor = Throwable.class, readOnly = true, propagation = Propagation.MANDATORY)
    public List<Tag> setTagsToUser(User user, Set<String> tagNames) {
        long startTime = System.currentTimeMillis();

        List<Tag> tagsToSet = new ArrayList<Tag>();
        List<String> tagNamesToInsert = new ArrayList<String>(tagNames.size());
        for (String tagName : tagNames) {
            Tag tag = tagRepo.getByName(tagName);
            if (tag != null) {
                tagsToSet.add(tag);
            } else {
                tagNamesToInsert.add(tagName);
            }
        }

        if (!tagNamesToInsert.isEmpty()) {
            tagsToSet.addAll(tagRepo.saveBatchByNames(tagNamesToInsert));
        }
        tagRepo.setTagsToUser(user.getId(), tagsToSet);

        long resultTime = System.currentTimeMillis() - startTime;
        LOG.info("processed setTagsToUser in " + resultTime + " ms");

        return tagsToSet;
    }
}
