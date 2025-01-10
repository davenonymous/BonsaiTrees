package com.davenonymous.bonsaitrees.client;

import com.davenonymous.bonsaitrees.BonsaiTrees;
import com.google.gson.*;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

import java.util.LinkedList;
import java.util.List;

public class PotModelLoader implements IGeometryLoader<PotModelGeometry> {
	public static final PotModelLoader INSTANCE = new PotModelLoader();
	public static final ResourceLocation ID = BonsaiTrees.resource("potmodel");

	private PotModelLoader() {
	}

	@Override
	public PotModelGeometry read(JsonObject jsonObject, JsonDeserializationContext context) throws JsonParseException {
		jsonObject.remove("loader");
		BlockModel base = context.deserialize(jsonObject, BlockModel.class);
		Vec3 offset = new Vec3(0, 0, 0);
		float scale = 1.0f;
		if(jsonObject.has("tree") && jsonObject.get("tree") instanceof JsonObject treeJson) {
			if(treeJson.has("offset") && treeJson.get("offset") instanceof JsonObject offsetJson) {
				float x = offsetJson.has("x") ? offsetJson.get("x").getAsFloat() : 0;
				float y = offsetJson.has("y") ? offsetJson.get("y").getAsFloat() : 0;
				float z = offsetJson.has("z") ? offsetJson.get("z").getAsFloat() : 0;
				offset = new Vec3(x, y, z);
			}
			if(treeJson.has("scale")) {
				scale = treeJson.get("scale").getAsFloat();
			}
		}

		List<PotModelGeometry.SoilElement> soilElements = new LinkedList<>();
		if(jsonObject.has("soil") && jsonObject.get("soil") instanceof JsonObject soilObj) {
			if(soilObj.has("faces") && soilObj.get("faces") instanceof JsonArray soilElementList) {
				for(JsonElement jsonEntry : soilElementList) {
					if(jsonEntry instanceof JsonObject soilElementJson) {
						Vec3 from = getVec3(soilElementJson, "from");
						Vec3 to = getVec3(soilElementJson, "to");

						soilElements.add(new PotModelGeometry.SoilElement(from, to));
					}
				}
			}
		}

		return new PotModelGeometry(base, offset, scale, soilElements);
	}

	private Vec3 getVec3(JsonObject json, String memberName) {
		JsonArray jsonarray = GsonHelper.getAsJsonArray(json, memberName);
		if(jsonarray.size() != 3) {
			throw new JsonParseException("Expected 3 " + memberName + " values, found: " + jsonarray.size());
		} else {
			double[] value = new double[3];

			for(int i = 0; i < value.length; ++i) {
				value[i] = GsonHelper.convertToDouble(jsonarray.get(i), memberName + "[" + i + "]");
			}

			return new Vec3(value[0], value[1], value[2]);
		}
	}
}