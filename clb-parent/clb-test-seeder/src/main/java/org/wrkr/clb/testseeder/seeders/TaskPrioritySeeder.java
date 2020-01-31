package org.wrkr.clb.testseeder.seeders;

import java.util.ArrayList;
import java.util.List;

import org.wrkr.clb.model.task.TaskPriority;
import org.wrkr.clb.model.task.TaskPriority.Priority;
import org.wrkr.clb.testseeder.testrepo.OldRepo;
import org.wrkr.clb.testseeder.utils.TaskFakeFieldsHolder;

/**
 * @author Denis Bilenko
 */
public class TaskPrioritySeeder {
    OldRepo repo = OldRepo.getInstance();
    List<TaskPriority> fakeList;

    public void generateAndInsert() {
        repo.begin();
        List<TaskPriority> newItems = gen();
        
        for (TaskPriority item : newItems) {
            repo.persist(item);
        }
        repo.commit();
        
    }

    private List<TaskPriority> gen() {

        List<TaskPriority> newList = new ArrayList<TaskPriority>();
        List<TaskPriority> fakeList = new ArrayList<TaskPriority>();
        long generatedId = 1;
        for (Priority p : Priority.values()) {
            TaskPriority newItem = new TaskPriority();
            TaskPriority newFakeItem = new TaskPriority();
            newItem.setNameCode(p);
            newList.add(newItem);
            
            newFakeItem.setNameCode(p);
            newFakeItem.setId(generatedId++);
            fakeList.add(newFakeItem);
        }
        
        TaskFakeFieldsHolder.getInstance().setPrios(fakeList);
        
        return newList;
    }
}
