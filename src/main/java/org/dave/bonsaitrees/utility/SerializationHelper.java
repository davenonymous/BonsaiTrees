package org.dave.bonsaitrees.utility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.dave.bonsaitrees.trees.TreeShapeSerializer;
import org.dave.bonsaitrees.trees.TreeShape;

public class SerializationHelper {
    public static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .enableComplexMapKeySerialization()
            .registerTypeAdapter(TreeShape.class, new TreeShapeSerializer())
            .create();

}
