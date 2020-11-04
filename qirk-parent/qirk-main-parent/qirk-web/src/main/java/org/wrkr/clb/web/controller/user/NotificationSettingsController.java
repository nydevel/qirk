package org.wrkr.clb.web.controller.user;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wrkr.clb.model.user.NotificationSettings;
import org.wrkr.clb.services.dto.user.NotificationSettingsDTO;
import org.wrkr.clb.services.user.NotificationSettingsService;
import org.wrkr.clb.web.controller.BaseExceptionHandlerController;
import org.wrkr.clb.web.json.JsonContainer;


@RestController
@RequestMapping(path = "notification-settings", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class NotificationSettingsController extends BaseExceptionHandlerController {

    @Autowired
    private NotificationSettingsService notifSettingsService;

    @PutMapping(value = "/")
    public JsonContainer<NotificationSettings, Void> updateByToken(@SuppressWarnings("unused") HttpSession session,
            @RequestBody NotificationSettingsDTO notifSettingsDTO) throws Exception {
        long startTime = System.currentTimeMillis();
        NotificationSettings notifSettings = notifSettingsService.updateByToken(notifSettingsDTO);
        logProcessingTimeFromStartTime(startTime, "updateByToken");
        return new JsonContainer<NotificationSettings, Void>(notifSettings);
    }
}
