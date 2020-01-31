package org.wrkr.clb.testseeder.seeders;

import java.util.List;

import org.wrkr.clb.model.Language;
import org.wrkr.clb.model.TechnologyTag;
import org.wrkr.clb.model.user.User;
import org.wrkr.clb.testseeder.generators.entities.UserGenerator;
import org.wrkr.clb.testseeder.generators.props.HashGenarator;

/**
 * @author Denis Bilenko
 */
public class UserSeeder extends BaseThreadedSeeder<User> {

    UserGenerator userGenerator = UserGenerator.getInstance();

    public UserSeeder(int amount, List<Language> languages, List<TechnologyTag> tags) {
        this.amount = amount;
        userGenerator.setLangs(languages);
        userGenerator.setTags(tags);
    }

    @Override
    public User generateOneItem() {
        User user = userGenerator.generate();
        user.setPasswordHash(HashGenarator.getInstance().generate());
        return user;
    }

}
