package org.wrkr.clb.testseeder.seeders;

import java.util.ArrayList;
import java.util.List;

import org.wrkr.clb.model.task.TaskStatus;
import org.wrkr.clb.model.task.TaskStatus.Status;
import org.wrkr.clb.testseeder.testrepo.OldRepo;
import org.wrkr.clb.testseeder.utils.TaskFakeFieldsHolder;

/**
 * @author Denis Bilenko
 */
public class TaskStatusSeeder {
    OldRepo repo = OldRepo.getInstance();

    public void generateAndInsert() {
        repo.begin();
        List<TaskStatus> newItems = gen();
        for (TaskStatus item : newItems) {
            repo.persist(item);
        }
        repo.commit();
    }

    private List<TaskStatus> gen() {

        List<TaskStatus> newList = new ArrayList<TaskStatus>();
        List<TaskStatus> fakeList = new ArrayList<TaskStatus>();

        long genID = 1;
        for (Status p : Status.values()) {
            TaskStatus newItem = new TaskStatus();
            TaskStatus fakseItem = new TaskStatus();
            newItem.setNameCode(p);
            newList.add(newItem);
            
            fakseItem.setNameCode(p);
            fakseItem.setId(genID++);
            fakeList.add(fakseItem);
        }

        TaskFakeFieldsHolder.getInstance().setStatuses(fakeList);
        return newList;
    }
}
