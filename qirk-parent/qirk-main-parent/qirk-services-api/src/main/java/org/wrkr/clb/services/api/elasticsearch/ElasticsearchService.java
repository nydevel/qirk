package org.wrkr.clb.services.api.elasticsearch;

import java.util.List;
import java.util.Set;

import org.elasticsearch.action.get.MultiGetItemResponse;
import org.wrkr.clb.model.BaseIdEntity;

public interface ElasticsearchService<E extends BaseIdEntity> {

    public static enum RequestType {
        INDEX("INDEX"),
        UPDATE("UPDATE"),
        DATASYNC_UPDATE("DATASYNC_UPDATE"),
        JIRA_UPDATE("JIRA_UPDATE");

        @SuppressWarnings("unused")
        private final String type;

        RequestType(final String type) {
            this.type = type;
        }
    }

    public void index(E entity);

    public boolean exists(E entity) throws Exception;

    public void updateOrIndex(E entity) throws Exception;

    public void datasync(E entity) throws Exception;

    public MultiGetItemResponse[] multiGet(List<Long> ids) throws Exception;

    public MultiGetItemResponse[] multiGet(List<Long> ids, String[] includeFields) throws Exception;

    public Set<String> getIds() throws Exception;

    public void delete(String id) throws Exception;
}
