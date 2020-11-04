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
