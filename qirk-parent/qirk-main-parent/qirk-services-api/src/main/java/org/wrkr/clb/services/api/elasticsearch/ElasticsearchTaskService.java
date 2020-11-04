package org.wrkr.clb.services.api.elasticsearch;

import java.io.IOException;

import org.elasticsearch.search.SearchHits;
import org.wrkr.clb.model.project.task.Task;
import org.wrkr.clb.repo.context.TaskSearchContext;

public interface ElasticsearchTaskService extends ElasticsearchService<Task> {

    public void updateForJira(Task task) throws IOException;

    public void updateCardAndHidden(Task task) throws Exception;

    public SearchHits search(Long projectId, TaskSearchContext searchContext, int size) throws Exception;
}
