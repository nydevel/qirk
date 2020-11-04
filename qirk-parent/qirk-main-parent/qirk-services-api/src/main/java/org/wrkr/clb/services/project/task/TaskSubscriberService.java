package org.wrkr.clb.services.project.task;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.wrkr.clb.model.user.NotificationSettings;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.services.dto.user.UserIdsDTO;

public interface TaskSubscriberService {

    public void create(Long userId, Long taskId);

    public void create(User currentUser, @NotNull(message = "taskId must not be null") Long taskId);

    public List<Long> list(@NotNull(message = "taskId must not be null") Long taskId);

    public UserIdsDTO list(User currentUser,
            @NotNull(message = "taskId must not be null") Long taskId);

    public List<User> listWithEmail(Long taskId, NotificationSettings.Setting notifSetting);

    public void delete(Long userId, Long taskId);

    public void delete(User currentUser, @NotNull(message = "taskId must not be null") Long taskId);
}
