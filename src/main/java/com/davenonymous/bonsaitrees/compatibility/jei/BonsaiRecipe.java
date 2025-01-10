package com.davenonymous.bonsaitrees.compatibility.jei;

import com.davenonymous.bonsaitrees.setup.data.BonsaiInfo;
import net.minecraft.world.item.Item;

public record BonsaiRecipe(Item sapling, BonsaiInfo info) {
}