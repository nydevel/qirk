package org.wrkr.clb.testseeder.generators.entities;

import java.util.List;
import java.util.Random;

import org.wrkr.clb.model.task.Task;
import org.wrkr.clb.model.task.TaskPriority;
import org.wrkr.clb.model.task.TaskStatus;
import org.wrkr.clb.model.task.TaskType;
import org.wrkr.clb.testseeder.generators.BaseGenerator;
import org.wrkr.clb.testseeder.generators.props.LongGenerator;
import org.wrkr.clb.testseeder.generators.props.StringGenerator;
import org.wrkr.clb.testseeder.generators.props.OffsetDateTimeGenerator;
import org.wrkr.clb.testseeder.utils.TaskFakeFieldsHolder;

/**
 * @author Denis Bilenko
 */
public class TaskGenerator implements BaseGenerator<Task> {

    private static TaskGenerator instance = null;

    private List<TaskPriority> prios = TaskFakeFieldsHolder.getInstance().getPrios();
    private List<TaskStatus> statuses = TaskFakeFieldsHolder.getInstance().getStatuses();
    private List<TaskType> types = TaskFakeFieldsHolder.getInstance().getTypes();
    private Random rand = new Random();

    private TaskGenerator() {
    }
    
    public static TaskGenerator getInstance() {
        if (instance == null) {
            instance = new TaskGenerator();
        }
        return instance;
    }

    public Task generate() {
        Task task = new Task();

        task.setNumber(LongGenerator.getInstance().generate());
        task.setCreatedAt(OffsetDateTimeGenerator.getInstance().generate());
        task.setDescription(StringGenerator.getInstance().generate());
        task.setUpdatedAt(OffsetDateTimeGenerator.getInstance().generate());

        task.setPriority(prios.get(rand.nextInt(prios.size())));
        task.setStatus(statuses.get(rand.nextInt(statuses.size())));
        task.setType(types.get(rand.nextInt(types.size())));

        task.setRecordVersion(1L);
        
        return task;
    }
}
