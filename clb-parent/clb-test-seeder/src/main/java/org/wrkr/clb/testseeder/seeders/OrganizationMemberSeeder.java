package org.wrkr.clb.testseeder.seeders;

import java.util.Random;

import org.wrkr.clb.model.Organization;
import org.wrkr.clb.model.OrganizationMember;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.testseeder.generators.props.BooleanGenerator;
import org.wrkr.clb.testseeder.generators.props.StringGenerator;

/**
 * @author Denis Bilenko
 */
public class OrganizationMemberSeeder extends BaseThreadedSeeder<OrganizationMember> {

    private int organizationAmount = 0;
    Random rand = new Random();
    private int usersAmount;

    public OrganizationMemberSeeder(int amount, int organizationAmount, int usersAmount) {
        super.amount = amount;
        this.organizationAmount = organizationAmount;
        this.usersAmount = usersAmount;
    }

    @Override
    public OrganizationMember generateOneItem() {

        Organization org = new Organization();
        org.setId((long) (rand.nextInt(organizationAmount) + 1));

        User user = new User();
        user.setId((long) (rand.nextInt(usersAmount) + 1));

        OrganizationMember m = new OrganizationMember();
        m.setOrganization(org);
        m.setUser(user);

        m.setManager(BooleanGenerator.getInstance().generate());
        m.setEnabled(BooleanGenerator.getInstance().generate(90));
        m.setRole(StringGenerator.getInstance().generate());
        m.setRecordVersion(1L);
        
        return m;
    }
}
