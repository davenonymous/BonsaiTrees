package com.davenonymous.bonsaitrees3.compat.jade;

import com.davenonymous.bonsaitrees3.blocks.BonsaiPotBlock;
import com.davenonymous.bonsaitrees3.blocks.BonsaiPotBlockEntity;

import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class BonsaiTreesWailaPlugin implements IWailaPlugin {
	@Override
    public void register(IWailaCommonRegistration registration) {
		registration.registerItemStorage(new BonsaiPotProvider(), BonsaiPotBlockEntity.class);
    }
	
	@Override
    public void registerClient(IWailaClientRegistration registration) {
		registration.registerBlockComponent(new BonsaiPotProvider(), BonsaiPotBlock.class);
    }
}
