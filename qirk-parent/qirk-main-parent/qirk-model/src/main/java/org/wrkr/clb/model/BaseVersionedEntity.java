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
package org.wrkr.clb.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class BaseVersionedEntity extends BaseIdEntity {

    @Column(name = "record_version", nullable = false)
    private Long recordVersion = 1L;

    public Long getRecordVersion() {
        return recordVersion;
    }

    public void setRecordVersion(Long recordVersion) {
        this.recordVersion = recordVersion;
    }

    @Override
    public String toString() {
        return getClass() + " [id=" + getId() + "; recordVersion=" + recordVersion + "]";
    }
}
