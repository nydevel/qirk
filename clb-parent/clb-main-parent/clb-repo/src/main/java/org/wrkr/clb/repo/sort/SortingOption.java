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
package org.wrkr.clb.repo.sort;

public class SortingOption {
    
    public enum Order {
        ASC("ASC"),
        DESC("DESC");
        
        @SuppressWarnings("unused")
        private final String order;

        Order(final String order) {
            this.order = order;
        }
    }

    public enum ForTask {
        ASSIGNEE("ASSIGNEE"),
        CREATED_AT("CREATED_AT"),
        NUMBER("NUMBER"),
        PRIORITY("PRIORITY"),
        REPORTER("REPORTER"),
        SUMMARY("SUMMARY"),
        UPDATED_AT("UPDATED_AT");
        
        @SuppressWarnings("unused")
        private final String field;

        ForTask(final String field) {
            this.field = field;
        }
    }
}
