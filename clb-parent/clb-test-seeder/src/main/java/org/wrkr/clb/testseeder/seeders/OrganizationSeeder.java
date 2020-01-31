package org.wrkr.clb.testseeder.seeders;

import java.util.ArrayList;
import java.util.List;

import org.wrkr.clb.model.Language;
import org.wrkr.clb.model.Organization;
import org.wrkr.clb.testseeder.generators.entities.OrganizationGenerator;
import org.wrkr.clb.testseeder.generators.props.BooleanGenerator;

/**
 * @author Denis Bilenko
 */
public class OrganizationSeeder extends BaseThreadedSeeder<Organization> {

    OrganizationGenerator organizationGenerator = OrganizationGenerator.getInstance();
    private List<Language> langs;

    public OrganizationSeeder(int amount, List<Language> langs) {
        super.amount = amount;
        this.langs = langs;
    }

    @Override
    public Organization generateOneItem() {
        Organization org = organizationGenerator.generate();
        org.setLanguages(generateRandomListOfLangs());
        return org;
    }

    private List<Language> generateRandomListOfLangs() {
        List<Language> thatLangs = new ArrayList<Language>();
        if (BooleanGenerator.getInstance().generate(85)) { // для рандомных организаций, 85% из них...
            for (Language lang : langs) {
                if (BooleanGenerator.getInstance().generate(5)) {
                    thatLangs.add(lang);
                }
            }
        }

        return thatLangs;
    }
}
