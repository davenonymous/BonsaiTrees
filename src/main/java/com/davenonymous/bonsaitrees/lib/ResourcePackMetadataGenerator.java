package com.davenonymous.bonsaitrees.lib;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.DetectedVersion;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class ResourcePackMetadataGenerator implements DataProvider {
	private final PackOutput output;
	private final Map<String, Supplier<JsonElement>> elements = new HashMap<>();

	private ResourcePackMetadataGenerator(PackOutput output) {
		this.output = output;
	}

	public <T> ResourcePackMetadataGenerator add(MetadataSectionType<T> type, T value) {
		this.elements.put(type.getMetadataSectionName(), () -> type.toJson(value));
		return this;
	}

	@Override
	public CompletableFuture<?> run(CachedOutput output) {
		JsonObject jsonobject = new JsonObject();
		this.elements.forEach((p_249290_, p_251317_) -> jsonobject.add(p_249290_, p_251317_.get()));
		return DataProvider.saveStable(output, jsonobject, this.output.getOutputFolder().resolve("pack.mcmeta"));
	}

	@Override
	public final String getName() {
		return "Resource Pack Metadata";
	}

	public static ResourcePackMetadataGenerator forResourcePack(PackOutput output, Component description) {
		return new ResourcePackMetadataGenerator(output)
			.add(PackMetadataSection.TYPE, new PackMetadataSection(description, DetectedVersion.BUILT_IN.getPackVersion(
				PackType.CLIENT_RESOURCES), Optional.empty()));
	}
}