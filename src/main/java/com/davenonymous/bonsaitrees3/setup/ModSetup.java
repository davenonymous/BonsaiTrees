package com.davenonymous.bonsaitrees3.setup;

import com.davenonymous.bonsaitrees3.BonsaiTrees3;
import com.davenonymous.bonsaitrees3.network.Networking;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class ModSetup {
	public static final String TAB_NAME = BonsaiTrees3.MODID;

	public static final CreativeModeTab ITEM_GROUP = new CreativeModeTab(TAB_NAME) {
		@Override
		public ItemStack makeIcon() {
			return new ItemStack(Items.DIAMOND);
		}
	};

	public static void init(FMLCommonSetupEvent event) {
		Networking.registerMessages();
	}
}
