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
package org.wrkr.clb.services.dto.project.imprt.jira;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class JiraIdAndNameDTO {

    public String id;
    public String name;

    public static JiraIdAndNameDTO fromDOMElement(Element element) {
        JiraIdAndNameDTO dto = new JiraIdAndNameDTO();

        dto.id = element.getAttribute("id");
        dto.name = element.getAttribute("name");

        return dto;
    }

    public static List<JiraIdAndNameDTO> fromDOMNodeList(NodeList nodes) {
        List<JiraIdAndNameDTO> dtoList = new ArrayList<JiraIdAndNameDTO>(nodes.getLength());
        for (int i = 0; i < nodes.getLength(); i++) {
            dtoList.add(fromDOMElement((Element) nodes.item(i)));
        }
        return dtoList;
    }
}
