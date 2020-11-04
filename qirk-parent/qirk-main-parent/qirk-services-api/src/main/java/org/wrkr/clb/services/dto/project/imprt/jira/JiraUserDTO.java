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
