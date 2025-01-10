package com.davenonymous.bonsaitrees.setup;


import com.davenonymous.bonsaitrees.BonsaiTrees;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforgespi.locating.IModFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

@EventBusSubscriber(modid = BonsaiTrees.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModDataPacks {
	private static final PackSelectionConfig RESOURCEPACK_SELECTION_CONFIG = new PackSelectionConfig(true, Pack.Position.BOTTOM, false);
	private static final PackSelectionConfig DATAPACK_SELECTION_CONFIG = new PackSelectionConfig(true, Pack.Position.BOTTOM, false);

	@SubscribeEvent
	public static void addPackFinder(AddPackFindersEvent event) {
		if(event.getPackType() == PackType.SERVER_DATA) {
			IModFile bonsaiModFile = ModList.get().getModFileById(BonsaiTrees.MODID).getFile();

			for(var modInfo : ModList.get().getMods()) {
				String modId = modInfo.getModId();

				try {
					Path resourcePath = bonsaiModFile.findResource("datapacks", modId);

					PackLocationInfo packLocationInfo = new PackLocationInfo(modId, Component.literal("Bonsais for: " + modInfo.getDisplayName()), PackSource.BUILT_IN,
						Optional.empty()
					);
					PathPackResources packResources = new PathPackResources(packLocationInfo, resourcePath);
					PackMetadataSection packMeta = packResources.getMetadataSection(MetadataSectionType.fromCodec("pack", PackMetadataSection.CODEC));
					if(packMeta == null) {
						continue;
					}

					PathPackResources.PathResourcesSupplier pathResourcesSupplier = new PathPackResources.PathResourcesSupplier(resourcePath);
					Pack pack = Pack.readMetaAndCreate(packLocationInfo, pathResourcesSupplier, event.getPackType(), DATAPACK_SELECTION_CONFIG);
					event.addRepositorySource(consumer -> {
						consumer.accept(pack);
					});
				} catch (IOException e) {
					throw new RuntimeException(e);
				}

			}

		} else if(event.getPackType() == PackType.CLIENT_RESOURCES) {
			IModFile bonsaiModFile = ModList.get().getModFileById(BonsaiTrees.MODID).getFile();

			for(var modInfo : ModList.get().getMods()) {
				String modId = modInfo.getModId();
				Path resourcePath = bonsaiModFile.findResource("resourcepacks", modId);

				var packLocationInfo = new PackLocationInfo(modId, Component.literal("Bonsais for: " + modInfo.getDisplayName()), PackSource.BUILT_IN, Optional.empty());
				var packInfo = new PathPackResources(packLocationInfo, resourcePath);
				try {
					var packMeta = packInfo.getMetadataSection(MetadataSectionType.fromCodec("pack", PackMetadataSection.CODEC));
					if(packMeta == null) {
						continue;
					}

					event.addRepositorySource(consumer -> {
						consumer.accept(Pack.readMetaAndCreate(packLocationInfo,
							new PathPackResources.PathResourcesSupplier(resourcePath), event.getPackType(), RESOURCEPACK_SELECTION_CONFIG
						));
					});
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}

		}
	}
}
