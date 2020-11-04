package org.wrkr.clb.services.api.elasticsearch;

import java.util.List;

import org.elasticsearch.search.SearchHits;
import org.wrkr.clb.model.project.ProjectMember;
import org.wrkr.clb.model.user.User;

public interface ElasticsearchUserService extends ElasticsearchService<User> {

    public void setProjects(User user) throws Exception;

    public void addProject(Long userId, ProjectMember member) throws Exception;

    public void removeProject(Long userId, ProjectMember member) throws Exception;

    public void setInvitedProjects(Long userId, List<Long> projectIds) throws Exception;

    public void addInvitedProject(Long userId, Long projectId) throws Exception;

    public void removeInvitedProject(Long userId, Long projectId) throws Exception;

    public SearchHits searchByName(String prefix) throws Exception;

    public SearchHits searchByNameAndExcludeProject(String prefix, Long projectId) throws Exception;

    public SearchHits searchByNameAndProject(String prefix, Long projectId) throws Exception;
}
