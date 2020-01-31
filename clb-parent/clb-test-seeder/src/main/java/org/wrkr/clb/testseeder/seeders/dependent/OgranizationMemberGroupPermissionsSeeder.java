package org.wrkr.clb.testseeder.seeders.dependent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.wrkr.clb.model.Organization;
import org.wrkr.clb.model.OrganizationMemberGroup;
import org.wrkr.clb.model.OrganizationMemberGroupPermissions;
import org.wrkr.clb.model.Project;
import org.wrkr.clb.testseeder.generators.props.BooleanGenerator;
import org.wrkr.clb.testseeder.testrepo.OldRepo;

/**
 * @author Denis Bilenko
 */
public class OgranizationMemberGroupPermissionsSeeder extends BaseDependentThreadedSeeder<Organization> {

    int amountOfOrganizations = 0;
    Random random = new Random();

    public OgranizationMemberGroupPermissionsSeeder(int amountOfOrganizations) {
        this.amountOfOrganizations = amountOfOrganizations;
        super.totalPages = totalPages();
    }

    @Override
    public void executeOnEachThread(List<Organization> eachThreadList, OldRepo repo) {
        int persistancesToCommitCounter = 0;

        repo.begin();
        for (Organization org : eachThreadList) {
            List<Project> orgProjects = org.getProjects();
            List<OrganizationMemberGroup> orgGroups = org.getMemberGroups();
            List<OrganizationMemberGroup> exclusionOrgGroups = new ArrayList<OrganizationMemberGroup>(orgGroups);

            for (Project thatProject : orgProjects) {
                if (exclusionOrgGroups.size() > 0 && BooleanGenerator.getInstance().generate(80)) {

                    OrganizationMemberGroup someGroup = exclusionOrgGroups
                            .get(random.nextInt(exclusionOrgGroups.size()));
                    exclusionOrgGroups.remove(someGroup);

                    OrganizationMemberGroupPermissions orggperm = new OrganizationMemberGroupPermissions();

                    orggperm.setOrganizationMemberGroup(someGroup);
                    orggperm.setProject(thatProject);
                    orggperm.setUpdateProjectAllowed(BooleanGenerator.getInstance().generate(80));
                    orggperm.setCreateTaskAllowed(BooleanGenerator.getInstance().generate(80));
                    orggperm.setUpdateOtherTasksAllowed(BooleanGenerator.getInstance().generate(25));
                    orggperm.setReadProjectAllowed(BooleanGenerator.getInstance().generate(95));

                    repo.persist(orggperm);
                    persistancesToCommitCounter++;
                    if (persistancesToCommitCounter % super.maxCommitSize == 0) {
                        repo.commit();
                        repo.begin();
                    }
                }
            }
        }
        repo.commit();
    }

    @Override
    public List<Organization> selectPageFromRepo(OldRepo repo, int page) {
        int min = calcMinFromPage(page);
        int max = calcMaxFromPage(page);

        List<Organization> items = repo.listOrganizations(min, max);

        System.out.println(
                "OgranizationMemberGroupPermissionsSeeder: selectPageFromRepo page: " + page + " min: " + min + " max: "
                        + max
                        + " size() = " + items.size());

        return items;
    }

    @Override
    public int totalPages() {
        int pages = (int) Math.ceil((float) amountOfOrganizations / super.selectBy);
        System.out.println("OgranizationMemberGroupPermissionsSeeder: totalPages = " + pages + " ("
                + amountOfOrganizations + "/" + super.selectBy + ")");
        return pages;
    }

}
