package org.wrkr.clb.testseeder.generators.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.wrkr.clb.model.Language;
import org.wrkr.clb.model.Organization;
import org.wrkr.clb.model.Project;
import org.wrkr.clb.model.TechnologyTag;
import org.wrkr.clb.testseeder.generators.BaseGenerator;
import org.wrkr.clb.testseeder.generators.props.BooleanGenerator;
import org.wrkr.clb.testseeder.generators.props.StringGenerator;

/**
 * @author Denis Bilenko
 */
public class ProjectGenerator implements BaseGenerator<Project> {

    BooleanGenerator bg = BooleanGenerator.getInstance();
    static ProjectGenerator instance = new ProjectGenerator();
    List<Language> langs = new ArrayList<Language>();
    List<TechnologyTag> tags = new ArrayList<TechnologyTag>();
    Random rand = new Random();
    private int amountOfOrganizations = 0;

    private ProjectGenerator() {
    }

    public void setLangs(List<Language> langs) {
        this.langs = langs;
    }

    public void setTags(List<TechnologyTag> tags) {
        this.tags = tags;
    }

    public static ProjectGenerator getInstance() {
        return instance;
    }

    public Project generate() {
        
        Organization organization = new Organization();
        Long orgId = (long) (rand.nextInt(amountOfOrganizations) + 1);
        organization.setId( orgId );
        
        Project project = new Project();
        
        project.setOrganization(organization);
        project.setDescription(StringGenerator.getInstance().generate());
        project.setPrivate(BooleanGenerator.getInstance().generate());
        project.setUiId(StringGenerator.getInstance().generate());
        project.setName(StringGenerator.getInstance().generate());
        project.setRecordVersion(1L);

        if (bg.generate(80)) {
            List<Language> currentLangs = randLangsList();
            project.setLanguages(currentLangs);
        }

        if (bg.generate(80)) {
            List<TechnologyTag> currentTags = randTagsList();
            project.setTags(currentTags);
        }

        return project;
    }

    private List<TechnologyTag> randTagsList() {
        List<TechnologyTag> newList = new ArrayList<TechnologyTag>();
        for (TechnologyTag l : tags) {
            if (bg.generate(1)) {
                newList.add(l);
            }
        }
        return newList;
    }

    private List<Language> randLangsList() {
        List<Language> currentLangs = new ArrayList<Language>();
        for (Language l : langs) {
            if (bg.generate(8)) {
                currentLangs.add(l);
            }
        }
        return currentLangs;
    }

    public void setAmountOfOrganizations(int amountOfOrganizations) {
        this.amountOfOrganizations = amountOfOrganizations;
    }

}
