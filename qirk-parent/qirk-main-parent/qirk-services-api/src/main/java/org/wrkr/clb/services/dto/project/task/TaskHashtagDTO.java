package org.wrkr.clb.services.dto.project.task;

import java.util.ArrayList;
import java.util.List;

import org.wrkr.clb.model.project.task.TaskHashtag;
import org.wrkr.clb.services.dto.IdDTO;

public class TaskHashtagDTO extends IdDTO {

    public String name;

    public static TaskHashtagDTO fromEntity(TaskHashtag hashtag) {
        TaskHashtagDTO dto = new TaskHashtagDTO();

        dto.id = hashtag.getId();
        dto.name = hashtag.getName();

        return dto;
    }

    public static List<TaskHashtagDTO> fromEntities(List<TaskHashtag> hashtagList) {
        List<TaskHashtagDTO> dtoList = new ArrayList<TaskHashtagDTO>(hashtagList.size());
        for (TaskHashtag hashtag : hashtagList) {
            dtoList.add(fromEntity(hashtag));
        }
        return dtoList;
    }
}
