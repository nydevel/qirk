package org.wrkr.clb.services.user;

import javax.validation.Valid;

import org.springframework.validation.annotation.Validated;
import org.wrkr.clb.model.user.NotificationSettings;
import org.wrkr.clb.services.dto.user.NotificationSettingsDTO;


@Validated
public interface NotificationSettingsService {

    public NotificationSettings updateByToken(@Valid NotificationSettingsDTO notifSettingsDTO) throws Exception;
}
