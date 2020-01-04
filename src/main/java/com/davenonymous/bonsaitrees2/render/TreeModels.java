package com.davenonymous.bonsaitrees2.render;

import com.davenonymous.bonsaitrees2.util.Logz;
import com.davenonymous.libnonymous.render.MultiblockBlockModel;
import com.davenonymous.libnonymous.utils.GsonHelper;
import com.google.gson.stream.JsonReader;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class TreeModels {
    private static HashMap<ResourceLocation, MultiblockBlockModel> treeModels;

    public static void init() {
        Collection<ResourceLocation> resources = Minecraft.getInstance().getResourceManager().getAllResourceLocations("models/tree", p -> p.endsWith(".json"));

        ArrayList<MultiblockBlockModel> models = new ArrayList<>();
        for (ResourceLocation resource : resources) {
            try {
                InputStream is = Minecraft.getInstance().getResourceManager().getResource(resource).getInputStream();
                MultiblockBlockModel model = GsonHelper.GSON.fromJson(new JsonReader(new InputStreamReader(is)), MultiblockBlockModel.class);
                if (model != null) {
                    models.add(model);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        treeModels = new HashMap<>();
        for(MultiblockBlockModel model : models) {
            if(treeModels.containsKey(model.id)) {
                Logz.warn("Duplicate model for tree: {}.", model.id);
            }

            treeModels.put(model.id, model);
        }
        Logz.info("Found {} tree models.", models.size());
    }

    @Nullable
    public static MultiblockBlockModel get(ResourceLocation treeId) {
        return treeModels.get(treeId);
    }
}
