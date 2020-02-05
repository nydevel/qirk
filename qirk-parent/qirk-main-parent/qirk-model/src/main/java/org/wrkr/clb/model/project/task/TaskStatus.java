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
package org.wrkr.clb.model.project.task;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import org.wrkr.clb.model.BaseIdEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "task_status")
public class TaskStatus extends BaseIdEntity {

    public static final Status DEFAULT = Status.OPEN;
    private static final Set<Status> NOT_ALIVE = Collections.unmodifiableSet(new HashSet<Status>(Arrays.asList(
            Status.CLOSED, Status.REJECTED)));

    public static enum Status {
        OPEN("OPEN", "Open"),
        IN_DEVELOPMENT("IN_DEVELOPMENT", "In development"),
        WAITING_FOR_QA("WAITING_FOR_QA", "Waiting for QA"),
        IN_QA_REVIEW("IN_QA_REVIEW", "In QA review"),
        REJECTED("REJECTED", "Rejected"),
        CLOSED("CLOSED", "Closed");

        private final String nameCode;
        private final String humanReadable;

        Status(final String nameCode, final String humanReadable) {
            this.nameCode = nameCode;
            this.humanReadable = humanReadable;
        }

        public boolean isNotAlive() {
            return NOT_ALIVE.contains(this);
        }

        public boolean isAlive() {
            return !isNotAlive();
        }

        public String getValue() {
            return nameCode;
        }

        public String toHumanReadable() {
            return humanReadable;
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

    @JsonIgnore
    public boolean isNotAlive() {
        return nameCode.isNotAlive();
    }

    @JsonIgnore
    public boolean isAlive() {
        return nameCode.isAlive();
    }
}
