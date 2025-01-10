package com.davenonymous.bonsaitrees.setup;

import com.davenonymous.bonsaitrees.BonsaiTrees;
import com.davenonymous.bonsaitrees.blocks.BonsaiPotContainer;
import com.davenonymous.bonsaitrees.blocks.BonsaiPotScreen;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

@EventBusSubscriber(modid = BonsaiTrees.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModContainers {
	public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(Registries.MENU, "bonsaitrees");

	public static final Supplier<MenuType<BonsaiPotContainer>> BONSAI_POT = CONTAINERS.register(
		"bonsai_pot", resourceLocation -> IMenuTypeExtension.create(
			(i, inventory, registryFriendlyByteBuf) -> new BonsaiPotContainer(i, registryFriendlyByteBuf.readBlockPos(), inventory, inventory.player)
		)
	);

	@SubscribeEvent
	public static void attachScreens(RegisterMenuScreensEvent event) {
		event.register(BONSAI_POT.get(), BonsaiPotScreen::new);
	}
}