package com.davenonymous.bonsaitrees3.client;

import com.davenonymous.libnonymous.serialization.MultiBlockModelSerializer;
import com.davenonymous.libnonymous.serialization.MultiblockBlockModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class TreeModels {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final Gson GSON = new GsonBuilder().setPrettyPrinting().enableComplexMapKeySerialization().registerTypeAdapter(MultiblockBlockModel.class, new MultiBlockModelSerializer()).create();

	private static HashMap<ResourceLocation, MultiblockBlockModel> treeModels;

	public static void init() {
		Collection<Resource> resources = Minecraft.getInstance().getResourceManager().listResources("models/tree", p -> p.getPath().endsWith(".json")).values();

		ArrayList<MultiblockBlockModel> models = new ArrayList<>();
		for(Resource resource : resources) {
			try {
				InputStream is = resource.open();
				MultiblockBlockModel model = GSON.fromJson(new JsonReader(new InputStreamReader(is)), MultiblockBlockModel.class);
				if(model != null) {
					LOGGER.debug("Loaded tree model: {}", model.id);
					models.add(model);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		treeModels = new HashMap<>();
		for(MultiblockBlockModel model : models) {
			if(treeModels.containsKey(model.id)) {
				LOGGER.warn("Duplicate model for tree: {}.", model.id);
			}

			treeModels.put(model.id, model);
		}
		LOGGER.info("Found {} tree models.", models.size());
	}

	@Nullable
	public static MultiblockBlockModel get(ResourceLocation treeId) {
		return treeModels.get(treeId);
	}
}