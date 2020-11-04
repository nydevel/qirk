package org.wrkr.clb.model.project;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import org.wrkr.clb.model.BaseIdEntity;

import com.fasterxml.jackson.annotation.JsonProperty;


@Entity
@Table(name = InviteStatusMeta.TABLE_NAME)
public class InviteStatus extends BaseIdEntity {

    public static enum Status {
        PENDING("PENDING"),
        REJECTED("REJECTED"),
        ACCEPTED("ACCEPTED");

        @SuppressWarnings("unused")
        private final String nameCode;

        Status(final String nameCode) {
            this.nameCode = nameCode;
        }
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "name_code", nullable = false, unique = true)
    @JsonProperty(value = "name_code")
    private Status nameCode;

    public Status getNameCode() {
        return nameCode;
    }

    public void setNameCode(Status nameCode) {
        this.nameCode = nameCode;
    }
}
