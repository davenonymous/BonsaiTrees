package com.davenonymous.bonsaitrees.setup.config;

import com.davenonymous.bonsaitrees.BonsaiTrees;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = BonsaiTrees.MODID)
public class Config {
	public static final ModConfigSpec COMMON_SPEC;
	public static final ModConfigSpec CLIENT_SPEC;
	public static final DebugConfig Debug;
	public static final GameplayConfig Gameplay;
	public static final ClientConfig Client;

	static {
		ModConfigSpec.Builder commonBuilder = new ModConfigSpec.Builder();
		Debug = new DebugConfig(commonBuilder);
		Gameplay = new GameplayConfig(commonBuilder);
		COMMON_SPEC = commonBuilder.build();

		ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
		Client = new ClientConfig(builder);
		CLIENT_SPEC = builder.build();

	}

	@SubscribeEvent
	static void onLoad(final ModConfigEvent event) {
		if(event.getConfig().getSpec() == COMMON_SPEC) {
			Debug.load();
			Gameplay.load();
		} else if(event.getConfig().getSpec() == CLIENT_SPEC) {
			Client.load();
		}
	}
}
