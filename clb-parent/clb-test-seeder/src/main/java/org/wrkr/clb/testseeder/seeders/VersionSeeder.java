package org.wrkr.clb.testseeder.seeders;

import java.util.Random;

import org.wrkr.clb.model.Project;
import org.wrkr.clb.model.Version;
import org.wrkr.clb.testseeder.generators.props.StringGenerator;

/**
 * @author Denis Bilenko
 */
public class VersionSeeder extends BaseThreadedSeeder<Version> {

    private Random rand = new Random();
    private int projectsAmount = 0;
    private int maxNextTaskNumber = 4;

    public VersionSeeder(int amount, int projectsAmount) {
        super.amount = amount;
        this.projectsAmount = projectsAmount;
    }

    @Override
    public Version generateOneItem() {

        Project p = new Project();
        p.setId((long) (1 + rand.nextInt(projectsAmount)));

        Version v = new Version();
        v.setName(StringGenerator.getInstance().generate());
        v.setProject(p);
        v.setNextTaskNumber((long) (rand.nextInt(maxNextTaskNumber) + 1));

        return v;
    }

}
