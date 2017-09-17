package org.dave.bonsaitrees.base;

import net.minecraft.item.Item;
import org.dave.bonsaitrees.BonsaiTrees;

public class BaseItem extends Item {
    public BaseItem() {
        super();
    }

    @Override
    public Item setUnlocalizedName(String name) {
        if (!name.startsWith(BonsaiTrees.MODID + ".")) {
            name = BonsaiTrees.MODID + "." + name;
        }
        return super.setUnlocalizedName(name);
    }
}