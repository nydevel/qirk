package org.wrkr.clb.testseeder.seeders.dependent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.wrkr.clb.model.Organization;
import org.wrkr.clb.model.OrganizationMember;
import org.wrkr.clb.model.OrganizationMemberGroup;
import org.wrkr.clb.testseeder.generators.props.BooleanGenerator;
import org.wrkr.clb.testseeder.generators.props.StringGenerator;
import org.wrkr.clb.testseeder.testrepo.OldRepo;

/**
 * @author Denis Bilenko
 */
public class OrganizationMemberGroupsSeeder extends BaseDependentThreadedSeeder<Organization> {
    private int amountOfOrganizations;

    Random random = new Random();
    
    public OrganizationMemberGroupsSeeder(int amountOfOrganizations) {
        this.amountOfOrganizations = amountOfOrganizations;
        super.totalPages = totalPages();
    }

    @Override
    public void executeOnEachThread(List<Organization> eachThreadList, OldRepo repo) {
        int persistancesToCommitCounter = 0;
        repo.begin();
        for (Organization thatOrganization : eachThreadList) {
            if (BooleanGenerator.getInstance().generate(65)) { // для 65 % организаций ...
                List<OrganizationMember> organizationMembers = thatOrganization.getMembers();
                List<OrganizationMember> exclusionOrganizationMembers = new ArrayList<OrganizationMember>(
                        organizationMembers);
                
                short attemptsToCreateGroup = 0;
                int randomLimitForGroupsAmount = random.nextInt(4) +1 ;
                
                while (exclusionOrganizationMembers.size() > 0  && attemptsToCreateGroup < randomLimitForGroupsAmount ) { 
                    attemptsToCreateGroup++;
                    
                    List<OrganizationMember> omsForGroup = new ArrayList<OrganizationMember>();

                    for (OrganizationMember thatMember : organizationMembers) {
                        if (BooleanGenerator.getInstance().generate()) {
                            omsForGroup.add(thatMember);
                            //exclusionOrganizationMembers.remove(thatMember);
                        }
                    }

                    OrganizationMemberGroup group = new OrganizationMemberGroup();
                    group.setName(StringGenerator.getInstance().generate());
                    group.setOrganization(thatOrganization);
                    group.setOrganizationMembers(omsForGroup);

                    repo.persist(group);
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
                "OrganizationMemberGroupsSeeder: selectPageFromRepo page: " + page + " min: " + min + " max: " + max
                        + " size() = " + items.size());

        return items;
    }

    @Override
    public int totalPages() {
        int pages = (int) Math.ceil((float) amountOfOrganizations / super.selectBy);
        System.out.println("OrganizationMemberGroupsSeeder: totalPages = " + pages+ " ("
                + amountOfOrganizations + "/" + super.selectBy + ")");
        return pages;
    }

}
