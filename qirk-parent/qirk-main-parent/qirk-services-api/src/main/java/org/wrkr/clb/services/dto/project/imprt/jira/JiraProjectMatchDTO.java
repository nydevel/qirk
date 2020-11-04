package org.wrkr.clb.services.dto.project.imprt.jira;

import java.util.List;

import org.wrkr.clb.services.dto.project.imprt.QirkDataDTO;

public class JiraProjectMatchDTO {

    public List<JiraProjectDTO> projects;
    public List<JiraUserDTO> users;
    public List<JiraIdAndNameDTO> types;
    public List<JiraIdAndNameDTO> priorities;
    public List<JiraIdAndNameDTO> statuses;

    public QirkDataDTO qirk;

    public JiraProjectMatchDTO(List<JiraProjectDTO> projects, List<JiraUserDTO> users,
            List<JiraIdAndNameDTO> types, List<JiraIdAndNameDTO> priorities, List<JiraIdAndNameDTO> statuses,
            QirkDataDTO qirk) {
        this.projects = projects;
        this.users = users;
        this.types = types;
        this.priorities = priorities;
        this.statuses = statuses;
        this.qirk = qirk;
    }
}
