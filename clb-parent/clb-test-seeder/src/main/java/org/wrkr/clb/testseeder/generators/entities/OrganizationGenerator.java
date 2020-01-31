package org.wrkr.clb.testseeder.generators.entities;

import org.wrkr.clb.model.Organization;
import org.wrkr.clb.testseeder.generators.BaseGenerator;
import org.wrkr.clb.testseeder.generators.props.BooleanGenerator;
import org.wrkr.clb.testseeder.generators.props.StringGenerator;

/**
 * @author Denis Bilenko
 */
public class OrganizationGenerator implements BaseGenerator<Organization> {

    BooleanGenerator booleanGenerator = BooleanGenerator.getInstance();
    StringGenerator stringGenerator = StringGenerator.getInstance();

    private static OrganizationGenerator instance = null;

    private OrganizationGenerator() {
    }

    public static OrganizationGenerator getInstance() {
        if (instance == null) {
            instance = new OrganizationGenerator();
        }
        return instance;
    }

    public Organization generate() {
        Organization organization = new Organization();
        organization.setName(stringGenerator.generate());
        organization.setPrivate(booleanGenerator.generate());
        organization.setUiId(stringGenerator.generate());
        return organization;
    }
}
