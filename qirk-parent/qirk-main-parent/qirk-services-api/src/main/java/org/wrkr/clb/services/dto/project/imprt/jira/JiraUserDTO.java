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
package org.wrkr.clb.services.dto.project.imprt.jira;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JiraUserDTO {

    public static final String ID = "id";
    public static final String USERNAME = "lowerUserName";
    public static final String DISPLAY_NAME = "displayName";
    public static final String EMAIL = "lowerEmailAddress";

    public String id;
    public String username;
    @JsonProperty(value = "display_name")
    public String displayName;
    public String email;

    public static JiraUserDTO fromDOMElement(Element element) {
        JiraUserDTO dto = new JiraUserDTO();

        dto.id = element.getAttribute(ID);
        dto.username = element.getAttribute(USERNAME);
        dto.displayName = element.getAttribute(DISPLAY_NAME);
        dto.email = element.getAttribute(EMAIL);

        return dto;
    }

    public static List<JiraUserDTO> fromDOMNodeList(NodeList nodes) {
        List<JiraUserDTO> dtoList = new ArrayList<JiraUserDTO>(nodes.getLength());
        for (int i = 0; i < nodes.getLength(); i++) {
            dtoList.add(fromDOMElement((Element) nodes.item(i)));
        }
        return dtoList;
    }
}
