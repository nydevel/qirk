package org.wrkr.clb.services.user.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.common.crypto.TokenGenerator;
import org.wrkr.clb.common.crypto.token.notification.NotificationSettingsTokenData;
import org.wrkr.clb.common.util.strings.JsonUtils;
import org.wrkr.clb.model.user.NotificationSettings;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.repo.user.JDBCUserRepo;
import org.wrkr.clb.repo.user.NotificationSettingsRepo;
import org.wrkr.clb.services.dto.user.NotificationSettingsDTO;
import org.wrkr.clb.services.user.NotificationSettingsService;
import org.wrkr.clb.services.util.exception.BadRequestException;
import org.wrkr.clb.services.util.exception.NotFoundException;
import org.wrkr.clb.services.util.http.JsonStatusCode;

@Validated
@Service
public class DefaultNotificationSettingsService implements NotificationSettingsService {

    @Autowired
    private NotificationSettingsRepo notifSettingsRepo;

    @Autowired
    private JDBCUserRepo userRepo;

    @Autowired
    private TokenGenerator tokenGenerator;

    @Override
    public NotificationSettings updateByToken(NotificationSettingsDTO notifSettingsDTO) throws Exception {
        NotificationSettingsTokenData tokenData;
        try {
            Map<String, Object> mapTokenData = JsonUtils.<Object>convertJsonToMapUsingLongForInts(
                    tokenGenerator.decrypt(notifSettingsDTO.token, notifSettingsDTO.IV));
            tokenData = new NotificationSettingsTokenData(
                    (String) mapTokenData.get(NotificationSettingsTokenData.USER_EMAIL),
                    (String) mapTokenData.get(NotificationSettingsTokenData.TYPE));
        } catch (Exception e) {
            throw new BadRequestException(JsonStatusCode.INVALID_TOKEN, "Token is missing or invalid.", e);
        }

        User user = (tokenData.userEmail == null ? null : userRepo.getByEmail(tokenData.userEmail));
        if (user == null) {
            throw new NotFoundException("User");
        }

        NotificationSettings notifSettings = new NotificationSettings(user.getId());
        notifSettings.setTaskCreated(notifSettingsDTO.taskCreated);
        notifSettings.setTaskUpdated(notifSettingsDTO.taskUpdated);
        notifSettings.setTaskCommented(notifSettingsDTO.taskCommented);
        notifSettingsRepo.update(notifSettings);

        return notifSettings;
    }
}
