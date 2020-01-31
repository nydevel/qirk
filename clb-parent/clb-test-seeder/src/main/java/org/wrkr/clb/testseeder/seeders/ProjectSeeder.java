package org.wrkr.clb.testseeder.seeders;

import java.util.List;

import org.wrkr.clb.model.Language;
import org.wrkr.clb.model.Project;
import org.wrkr.clb.model.TechnologyTag;
import org.wrkr.clb.testseeder.generators.entities.ProjectGenerator;

/**
 * @author Denis Bilenko
 */
public class ProjectSeeder extends BaseThreadedSeeder<Project> {
    ProjectGenerator projectGenerator = ProjectGenerator.getInstance();

    public ProjectSeeder() {
    }

    public ProjectSeeder(int amount, List<Language> languages, List<TechnologyTag> tags, int amountOfOrganizations) {
        super.amount = amount;
        projectGenerator.setLangs(languages);
        projectGenerator.setTags(tags);
        projectGenerator.setAmountOfOrganizations(amountOfOrganizations);
    }

    @Override
    public Project generateOneItem() {
        return projectGenerator.generate();
    }
}
