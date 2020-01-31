package org.wrkr.clb.testseeder.utils;

import java.util.ArrayList;
import java.util.List;

import org.wrkr.clb.model.Language;
import org.wrkr.clb.model.TechnologyTag;
import org.wrkr.clb.testseeder.seeders.LanguageSeeder;
import org.wrkr.clb.testseeder.seeders.OrganizationMemberSeeder;
import org.wrkr.clb.testseeder.seeders.OrganizationSeeder;
import org.wrkr.clb.testseeder.seeders.ProjectSeeder;
import org.wrkr.clb.testseeder.seeders.TagSeeder;
import org.wrkr.clb.testseeder.seeders.TaskPrioritySeeder;
import org.wrkr.clb.testseeder.seeders.TaskStatusSeeder;
import org.wrkr.clb.testseeder.seeders.TaskTypeSeeder;
import org.wrkr.clb.testseeder.seeders.UserSeeder;
import org.wrkr.clb.testseeder.seeders.VersionSeeder;
import org.wrkr.clb.testseeder.seeders.dependent.OgranizationMemberGroupPermissionsSeeder;
import org.wrkr.clb.testseeder.seeders.dependent.OrganizationMemberGroupsSeeder;
import org.wrkr.clb.testseeder.seeders.dependent.TaskSeeder;

/**
 * @author Denis Bilenko
 */
public class Main {
    private static final int LANGUAGES_AMOUNT = 30;
    private static final int TAGS_AMOUNT = 500;

    private static final int USERS_AMOUNT = 50 * 1000;
    private static final int ORGANIZATIONS_AMOUNT = USERS_AMOUNT / 5;
    private static final int PROJECTS_AMOUNT = ORGANIZATIONS_AMOUNT * 10;
    private static final int VERSIONS_AMOUNT = PROJECTS_AMOUNT * 100;
    private static final int ORGANIZATION_MEMBERS_AMOUNT = USERS_AMOUNT / 100 * 105;

    public static final int DEPENDENT_SEEDER_PAGE_SIZE = 25000;
    public static final int MAX_COMMIT_SIZE = 10000;
    public static final int THREADS = 4;

    long start = System.currentTimeMillis();

    public static void main(String[] args) {

        Main main = new Main();
        main.seedInitialTaskStuff();

        main.initialEasySeed();
        main.generateOgranizationMemberGroups();
        main.generateOgranizationMemberGroupPermissions();
        main.generateTasksForVersions(1, 20);
        main.printTime();
    }

    private void generateOgranizationMemberGroupPermissions() {

        System.out.println("OgranizationMemberGroupPermissionsSeeder started");
        OgranizationMemberGroupPermissionsSeeder omps = new OgranizationMemberGroupPermissionsSeeder(
                ORGANIZATIONS_AMOUNT);
        omps.insertAll();
        System.out.println("OgranizationMemberGroupPermissionsSeeder finished");

    }

    private void generateOgranizationMemberGroups() {

        System.out.println("OrganizationMemberGroupsSeeder started");
        OrganizationMemberGroupsSeeder organizationMemberGroupsSeeder = new OrganizationMemberGroupsSeeder(
                ORGANIZATIONS_AMOUNT);
        organizationMemberGroupsSeeder.insertAll();
        System.out.println("OrganizationMemberGroupsSeeder finished");

    }

    private void generateTasksForVersions(int min, int max) {

        System.out.println("TaskSeeder started");
        TaskSeeder taskSeeder = new TaskSeeder(VERSIONS_AMOUNT);
        taskSeeder.insertAll();
        System.out.println("TaskSeeder finished");

    }

    private void printTime() {
        Long stop = System.currentTimeMillis();
        System.out.println("Total time in millis: " + (stop - start));
    }

    private void seedInitialTaskStuff() {
        TaskPrioritySeeder taskPrioritySeeder = new TaskPrioritySeeder();
        taskPrioritySeeder.generateAndInsert();
        TaskStatusSeeder taskStatusSeeder = new TaskStatusSeeder();
        taskStatusSeeder.generateAndInsert();
        TaskTypeSeeder taskTypeSeeder = new TaskTypeSeeder();
        taskTypeSeeder.generateAndInsert();
    }

    private void initialEasySeed() {
        LanguageSeeder languageSeeder = new LanguageSeeder(LANGUAGES_AMOUNT);
        languageSeeder.insertAll();

        TagSeeder tagSeeder = new TagSeeder(TAGS_AMOUNT);
        tagSeeder.insertAll();

        List<Language> languages = generateFakeListOfLanguagesOnlyWithIDs();
        List<TechnologyTag> tags = generateFakeListOfTechnologyTagOnlyWithIDs();

        System.out.println("UserSeeder started");
        UserSeeder userSeeder = new UserSeeder(USERS_AMOUNT, languages, tags);
        userSeeder.insertAll();
        System.out.println("UserSeeder finihed");

        System.out.println("OrganizationSeeder started");
        OrganizationSeeder organizationSeeder = new OrganizationSeeder(ORGANIZATIONS_AMOUNT, languages);
        organizationSeeder.insertAll();
        System.out.println("OrganizationSeeder finihed");

        System.out.println("ProjectSeeder started");
        ProjectSeeder projectSeeder = new ProjectSeeder(PROJECTS_AMOUNT, languages, tags, ORGANIZATIONS_AMOUNT);
        projectSeeder.insertAll();
        System.out.println("ProjectSeeder finihed");

        System.out.println("VersionSeeder started");
        VersionSeeder versionSeeder = new VersionSeeder(VERSIONS_AMOUNT, PROJECTS_AMOUNT);
        versionSeeder.insertAll();
        System.out.println("VersionSeeder finihed");

        System.out.println("OrganizationMemberSeeder started");
        OrganizationMemberSeeder organizationMemberSeeder = new OrganizationMemberSeeder(ORGANIZATION_MEMBERS_AMOUNT,
                ORGANIZATIONS_AMOUNT, USERS_AMOUNT);
        organizationMemberSeeder.insertAll();
        System.out.println("OrganizationMemberSeeder finihed");
    }

    private List<TechnologyTag> generateFakeListOfTechnologyTagOnlyWithIDs() {
        List<TechnologyTag> list = new ArrayList<TechnologyTag>();
        for (int i = 0; i < TAGS_AMOUNT; i++) {
            TechnologyTag u = new TechnologyTag();
            u.setId((long) (i + 1));
            list.add(u);
        }
        return list;
    }

    private List<Language> generateFakeListOfLanguagesOnlyWithIDs() {
        List<Language> list = new ArrayList<Language>();
        for (int i = 0; i < LANGUAGES_AMOUNT; i++) {
            Language u = new Language();
            u.setId((long) (i + 1));
            list.add(u);
        }
        return list;
    }

}
