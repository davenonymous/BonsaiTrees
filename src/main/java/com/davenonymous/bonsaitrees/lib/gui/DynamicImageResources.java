package com.davenonymous.bonsaitrees.lib.gui;

import com.davenonymous.bonsaitrees.BonsaiTrees;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.IoSupplier;
import net.neoforged.neoforge.resource.ResourcePackLoader;
import net.neoforged.neoforgespi.language.IModInfo;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DynamicImageResources {
	public record DynTexture(ResourceLocation resource, NativeImage image) {
	}

	private static Map<String, DynTexture> modLogos = new HashMap<>();

	public static Optional<DynTexture> getImage(String path, byte[] bytes) {
		try {
			var bb = MemoryUtil.memAlloc(bytes.length);
			bb.put(bytes);
			bb.rewind();

			var logo = NativeImage.read(bb);
			TextureManager tm = Minecraft.getInstance().getTextureManager();
			ResourceLocation resource = tm.register(
				"modimage_" + path.hashCode(), new DynamicTexture(logo) {
					public void upload() {
						this.bind();
						NativeImage td = this.getPixels();
						this.getPixels().upload(0, 0, 0, 0, 0, td.getWidth(), td.getHeight(), false, false, false, false);
						//this.getPixels().close();
					}
				}
			);

			return Optional.of(new DynTexture(resource, logo));
		} catch (Exception e) {
			BonsaiTrees.LOGGER.warn("Failed to read image from {}: {}", path, e);
		}

		return Optional.empty();
	}


	public static Optional<DynTexture> getImage(Path path, InputStream inputStream) {
		try {
			var logo = NativeImage.read(inputStream);
			TextureManager tm = Minecraft.getInstance().getTextureManager();
			ResourceLocation resource = tm.register(
				"modimage_" + path.toString(), new DynamicTexture(logo) {
					public void upload() {
						this.bind();
						NativeImage td = this.getPixels();
						this.getPixels().upload(0, 0, 0, 0, 0, td.getWidth(), td.getHeight(), false, false, false, false);
					}
				}
			);

			return Optional.of(new DynTexture(resource, logo));
		} catch (IOException e) {
			BonsaiTrees.LOGGER.warn("Failed to read image from {}: {}", path, e);
		}

		return Optional.empty();
	}

	public static Optional<DynTexture> getModLogo(IModInfo modInfo) {
		String modId = modInfo.getModId();
		if(modLogos.containsKey(modId)) {
			return Optional.of(modLogos.get(modId));
		}

		Optional<String> logoFile = modInfo.getLogoFile();
		if(logoFile.isEmpty()) {
			return Optional.empty();
		}

		Optional<Pack.ResourcesSupplier> optPack = ResourcePackLoader.getPackFor(modInfo.getModId());
		if(optPack.isEmpty()) {
			return Optional.empty();
		}

		Pack.ResourcesSupplier resourcePack = optPack.get();
		TextureManager tm = Minecraft.getInstance().getTextureManager();

		try(PackResources packResources = resourcePack.openPrimary(new PackLocationInfo("mod/" + modInfo.getModId(), Component.empty(), PackSource.BUILT_IN, Optional.empty()))) {
			NativeImage logo = null;
			IoSupplier<InputStream> logoResource = packResources.getRootResource(logoFile.get().split("[/\\\\]"));
			if(logoResource != null) {
				logo = NativeImage.read(logoResource.get());
			}

			if(logo != null) {
				ResourceLocation resource = tm.register(
					"modlogo_" + modInfo.getModId(), new DynamicTexture(logo) {
						public void upload() {
							this.bind();
							NativeImage td = this.getPixels();
							this.getPixels().upload(0, 0, 0, 0, 0, td.getWidth(), td.getHeight(), modInfo.getLogoBlur(), false, false, false);
						}
					}
				);

				modLogos.put(modId, new DynTexture(resource, logo));
			}
		} catch (IllegalArgumentException | IOException var11) {
		}

		return Optional.ofNullable(modLogos.get(modId));
	}
}
