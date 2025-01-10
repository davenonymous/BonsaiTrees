package com.davenonymous.bonsaitrees.compatibility.jade;

import com.davenonymous.bonsaitrees.BonsaiTrees;
import com.davenonymous.bonsaitrees.blocks.BonsaiPotBlock;
import com.davenonymous.bonsaitrees.blocks.BonsaiPotSmallBlock;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class JadePlugin implements IWailaPlugin {
	public static final ResourceLocation BONSAI_POT = BonsaiTrees.resource("jade/bonsai_pot");

	@Override
	public void registerClient(IWailaClientRegistration registration) {
		registration.registerBlockComponent(
			new JadeBonsaiPot(),
			BonsaiPotBlock.class
		);

		registration.registerBlockComponent(
			new JadeBonsaiPot(),
			BonsaiPotSmallBlock.class
		);

		registration.registerBlockIcon(new JadeBonsaiPot(), BonsaiPotBlock.class);
		registration.registerBlockIcon(new JadeBonsaiPot(), BonsaiPotSmallBlock.class);
	}
}
