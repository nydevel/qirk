package org.wrkr.clb.testseeder.seeders;

import java.util.ArrayList;
import java.util.List;

import org.wrkr.clb.model.task.TaskType;
import org.wrkr.clb.model.task.TaskType.Type;
import org.wrkr.clb.testseeder.testrepo.OldRepo;
import org.wrkr.clb.testseeder.utils.TaskFakeFieldsHolder;

/**
 * @author Denis Bilenko
 */
public class TaskTypeSeeder {
    OldRepo repo = OldRepo.getInstance();

    public void generateAndInsert() {
        repo.begin();
        List<TaskType> newItems = gen();
        for (TaskType item : newItems) {
            repo.persist(item);
        }
        repo.commit();
    }

    private List<TaskType> gen() {

        List<TaskType> newList = new ArrayList<TaskType>();
        List<TaskType> fakeList = new ArrayList<TaskType>();

        long genID = 1;
        for (Type p : Type.values()) {
            TaskType newItem = new TaskType();
            TaskType fakseItem = new TaskType();
            
            newItem.setNameCode(p);
            newList.add(newItem);
            
            fakseItem.setNameCode(p);
            fakseItem.setId(genID++);
            fakeList.add(fakseItem);
            
        }
        
        TaskFakeFieldsHolder.getInstance().setTypes(fakeList);

        return newList;
    }
}
