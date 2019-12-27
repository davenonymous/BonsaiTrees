package com.davenonymous.bonsaitrees2.util;

import com.davenonymous.libnonymous.render.MultiblockBlockModel;
import com.davenonymous.bonsaitrees2.render.TreeModelSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonHelper {
    public static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .enableComplexMapKeySerialization()
            .registerTypeAdapter(MultiblockBlockModel.class, new TreeModelSerializer())
            .create();
}
