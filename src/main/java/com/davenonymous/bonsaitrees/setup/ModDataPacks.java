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
import net.neoforged.neoforgespi.language.IModFileInfo;
import net.neoforged.neoforgespi.locating.IModFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Optional;

@EventBusSubscriber(modid = BonsaiTrees.MODID)
public class ModDataPacks {
	private static final PackSelectionConfig REQUIRED_SELECTION_CONFIG = new PackSelectionConfig(true, Pack.Position.BOTTOM, false);
	private static final PackSelectionConfig OPTIONAL_SELECTION_CONFIG = new PackSelectionConfig(false, Pack.Position.BOTTOM, false);

	@SubscribeEvent
	public static void addPackFinder(AddPackFindersEvent event) {
		IModFile bonsaiModFile = ModList.get().getModFileById(BonsaiTrees.MODID).getFile();
		if(event.getPackType() == PackType.SERVER_DATA) {
			Path datapacksPath = bonsaiModFile.findResource("datapacks");
			try(var paths = Files.walk(datapacksPath)) {
				for(var path : paths.filter(Files::isDirectory).sorted(Comparator.naturalOrder()).toList()) {
					String modId = path.getFileName().toString();
					IModFileInfo modFile = ModList.get().getModFileById(modId);
					PackSelectionConfig selectionConfig = modFile == null ? OPTIONAL_SELECTION_CONFIG : REQUIRED_SELECTION_CONFIG;
					PackLocationInfo packLocationInfo = new PackLocationInfo(modId, Component.literal("Bonsais: " + modId), PackSource.BUILT_IN,
						Optional.empty()
					);
					PathPackResources packResources = new PathPackResources(packLocationInfo, path);
					PackMetadataSection packMeta = packResources.getMetadataSection(MetadataSectionType.fromCodec("pack", PackMetadataSection.CODEC));
					if(packMeta == null) {
						continue;
					}

					PathPackResources.PathResourcesSupplier pathResourcesSupplier = new PathPackResources.PathResourcesSupplier(path);
					Pack pack = Pack.readMetaAndCreate(packLocationInfo, pathResourcesSupplier, event.getPackType(), selectionConfig);
					event.addRepositorySource(consumer -> {
						consumer.accept(pack);
					});

				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		} else if(event.getPackType() == PackType.CLIENT_RESOURCES) {
			Path resourcePath = bonsaiModFile.findResource("resourcepacks");
			try(var paths = Files.walk(resourcePath)) {
				for(var path : paths.filter(Files::isDirectory).sorted(Comparator.naturalOrder()).toList()) {
					String modId = path.getFileName().toString();
					IModFileInfo modFile = ModList.get().getModFileById(modId);
					PackSelectionConfig selectionConfig = modFile == null ? OPTIONAL_SELECTION_CONFIG : REQUIRED_SELECTION_CONFIG;
					var packLocationInfo = new PackLocationInfo(modId, Component.literal("Bonsais: " + modId), PackSource.BUILT_IN, Optional.empty());
					var packInfo = new PathPackResources(packLocationInfo, path);
					try {
						var packMeta = packInfo.getMetadataSection(MetadataSectionType.fromCodec("pack", PackMetadataSection.CODEC));
						if(packMeta == null) {
							continue;
						}

						event.addRepositorySource(consumer -> {
							consumer.accept(Pack.readMetaAndCreate(packLocationInfo,
								new PathPackResources.PathResourcesSupplier(path), event.getPackType(), selectionConfig
							));
						});
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}
	}
}
