package org.wrkr.clb.testseeder.generators.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.wrkr.clb.model.Language;
import org.wrkr.clb.model.TechnologyTag;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.testseeder.generators.BaseGenerator;
import org.wrkr.clb.testseeder.generators.props.BooleanGenerator;
import org.wrkr.clb.testseeder.generators.props.EmailGenerator;
import org.wrkr.clb.testseeder.generators.props.StringGenerator;
import org.wrkr.clb.testseeder.generators.props.OffsetDateTimeGenerator;

/**
 * @author Denis Bilenko
 */
public class UserGenerator implements BaseGenerator<User> {

    StringGenerator sg = StringGenerator.getInstance();
    BooleanGenerator bg = BooleanGenerator.getInstance();
    EmailGenerator eg = EmailGenerator.getInstance();
    OffsetDateTimeGenerator ztdg = OffsetDateTimeGenerator.getInstance();

    List<Language> langs = new ArrayList<Language>();
    List<TechnologyTag> tags = new ArrayList<TechnologyTag>();
    Random rand = new Random();

    private static UserGenerator instance = new UserGenerator();

    private UserGenerator() {
    }

    public void setLangs(List<Language> langs) {
        this.langs = langs;
    }

    public void setTags(List<TechnologyTag> tags) {
        this.tags = tags;
    }

    public static UserGenerator getInstance() {
        return instance;
    }

    public User generate() {
        User user = new User();

        String emailAndUsername = eg.generate();

        user.setEmailAddress(emailAndUsername);
        user.setUsername(emailAndUsername);
        user.setFirstName(sg.generate());
        user.setLastName(sg.generate());
        user.setAlias(sg.generate());
        user.setEnabled(bg.generate());
        user.setDontRecommend(bg.generate(15));
        user.setEnabled(bg.generate(95));
        user.setCreatedAt(ztdg.generate());

        user.setInterfaceLanguage(langs.get(
                rand.nextInt(
                        langs.size())));
        if (bg.generate(80)) {
            List<Language> currentLangs = randLangsList();
            user.setLanguages(currentLangs);
        }

        if (bg.generate(80)) {
            List<TechnologyTag> currentTags = randTagsList();
            user.setTags(currentTags);
        }

        return user;
    }

    private List<TechnologyTag> randTagsList() {
        List<TechnologyTag> newList = new ArrayList<TechnologyTag>();
        for (TechnologyTag l : tags) {
            if (bg.generate(2)) {
                newList.add(l);
            }
        }
        return newList;
    }

    private List<Language> randLangsList() {
        List<Language> currentLangs = new ArrayList<Language>();
        for (Language l : langs) {
            if (bg.generate(1)) {
                currentLangs.add(l);
            }
        }
        return currentLangs;
    }
}
