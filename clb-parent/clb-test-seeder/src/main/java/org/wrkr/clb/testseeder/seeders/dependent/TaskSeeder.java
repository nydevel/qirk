package org.wrkr.clb.testseeder.seeders.dependent;

import java.util.List;
import java.util.Random;

import org.wrkr.clb.model.OrganizationMember;
import org.wrkr.clb.model.Version;
import org.wrkr.clb.model.task.Task;
import org.wrkr.clb.testseeder.generators.entities.TaskGenerator;
import org.wrkr.clb.testseeder.generators.props.BooleanGenerator;
import org.wrkr.clb.testseeder.testrepo.OldRepo;

/**
 * @author Denis Bilenko
 */
public class TaskSeeder extends BaseDependentThreadedSeeder<Version> {

    Random random = new Random();

    private int maxTasksPerVersion = 25;
    private int minTasksPerVersion = 2;
    private int probabilityForEachVersionInPercant = 60;

    private int maxCommitSize = super.maxCommitSize;
    private int amountOfVersions;

    public TaskSeeder(int amountOfVersions) {
        this.amountOfVersions = amountOfVersions;
        super.totalPages = totalPages();
    }

    @Override
    public void executeOnEachThread(List<Version> eachThreadList, OldRepo repo) {

        System.out.println("TaskSeeder: executeOnEachThread eachThreadList.size() = " + eachThreadList.size());

        TaskGenerator generator = TaskGenerator.getInstance();

        repo.begin();

        int currentCommitSize = 0;

        for (Version v : eachThreadList) {
            if (BooleanGenerator.getInstance().generate(probabilityForEachVersionInPercant)) {

                List<OrganizationMember> avalibleMembersOfOrganization = v.getProject().getOrganization().getMembers();

                if (avalibleMembersOfOrganization.size() > 0) {

                    int amountOfTasks = randFromTo(minTasksPerVersion, maxTasksPerVersion);

                    for (int i2 = 0; i2 < amountOfTasks; i2++) {

                        Task newTask = generator.generate();
                        newTask.setVersion(v);
                        newTask.setReporter(randomMember(avalibleMembersOfOrganization));
                        newTask.setNumber((long) (i2 + 1));

                        if (BooleanGenerator.getInstance().generate((float) 45)) {
                            newTask.setAssignee(randomMember(avalibleMembersOfOrganization));
                        }

                        repo.persist(newTask);
                        currentCommitSize++;

                        if (currentCommitSize % maxCommitSize == 0) {
                            repo.commit();
                            repo.begin();
                        }
                    }
                }
            }
        }

        repo.commit();
    }

    @Override
    public List<Version> selectPageFromRepo(OldRepo repo, int page) {
        int min = calcMinFromPage(page);
        int max = calcMaxFromPage(page);

        List<Version> versions = repo.listVersions(min, max);

        System.out.println("TaskSeeder: selectPageFromRepo page: " + page + " min: " + min + " max: " + max
                + " size() = " + versions.size());
        return versions;
    }

    @Override
    public int totalPages() {
        int pages = (int) Math.ceil((float) amountOfVersions / super.selectBy);
        System.out.println("TaskSeeder: totalPages = " + pages+ " ("
                + amountOfVersions + "/" + super.selectBy + ")");
        return pages;
    }

    private OrganizationMember randomMember(List<OrganizationMember> avalibleMembersOfOrganization) {
        return avalibleMembersOfOrganization.get(random.nextInt(avalibleMembersOfOrganization.size()));
    }

    private int randFromTo(int min, int max) {
        return min + random.nextInt(max - min);
    }
}
