package org.wrkr.clb.common.mail;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EmailSentDTO {

    @JsonProperty(value = "email_sent")
    public boolean emailSent;

    public EmailSentDTO(boolean emailSent) {
        this.emailSent = emailSent;
    }
}
