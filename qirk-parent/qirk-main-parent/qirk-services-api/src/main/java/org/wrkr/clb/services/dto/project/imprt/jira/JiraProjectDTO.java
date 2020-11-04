package org.wrkr.clb.services.dto.project.imprt.jira;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class JiraProjectDTO {

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String KEY = "key";

    public String id;
    public String name;
    public String key;

    public static JiraProjectDTO fromDOMElement(Element element) {
        JiraProjectDTO dto = new JiraProjectDTO();

        dto.id = element.getAttribute("id");
        dto.name = element.getAttribute("name");
        dto.key = element.getAttribute("key");

        return dto;
    }

    public static List<JiraProjectDTO> fromDOMNodeList(NodeList nodes) {
        List<JiraProjectDTO> dtoList = new ArrayList<JiraProjectDTO>(nodes.getLength());
        for (int i = 0; i < nodes.getLength(); i++) {
            dtoList.add(fromDOMElement((Element) nodes.item(i)));
        }
        return dtoList;
    }
}
