package org.wrkr.clb.testseeder.seeders;

import org.wrkr.clb.model.TechnologyTag;
import org.wrkr.clb.testseeder.generators.props.StringGenerator;

/**
 * @author Denis Bilenko
 */
public class TagSeeder extends BaseThreadedSeeder<TechnologyTag> {
    StringGenerator stringGenerator = StringGenerator.getInstance();
    
    public TagSeeder(int amount) {
        super.amount = amount;
    }

    @Override
    public TechnologyTag generateOneItem() {
        TechnologyTag tag = new TechnologyTag();
        tag.setName(stringGenerator.generate());
        return tag;
    }
}
