package org.wrkr.clb.testseeder.seeders;

import org.wrkr.clb.model.Language;
import org.wrkr.clb.testseeder.generators.props.StringGenerator;

/**
 * @author Denis Bilenko
 */
public class LanguageSeeder extends BaseThreadedSeeder<Language> {
    StringGenerator stringGenerator = StringGenerator.getInstance();

    public LanguageSeeder(int amount) {
        super.amount = amount;
    }

    @Override
    public Language generateOneItem() {
        Language newItem = new Language();
        newItem.setNameCode(stringGenerator.generate());
        return newItem;
    }
}
