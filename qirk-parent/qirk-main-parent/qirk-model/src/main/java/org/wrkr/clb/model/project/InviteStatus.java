/*
 * This file is part of the Java API to Qirk.
 * Copyright (C) 2020 Memfis Inc.
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
