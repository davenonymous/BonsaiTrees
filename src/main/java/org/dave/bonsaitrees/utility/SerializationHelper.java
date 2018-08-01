package org.dave.bonsaitrees.utility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.dave.bonsaitrees.api.TreeTypeSimple;
import org.dave.bonsaitrees.soils.BonsaiSoil;
import org.dave.bonsaitrees.soils.BonsaiSoilSerializer;
import org.dave.bonsaitrees.trees.TreeShapeSerializer;
import org.dave.bonsaitrees.trees.TreeShape;
import org.dave.bonsaitrees.trees.TreeTypeSimpleSerializer;

public class SerializationHelper {
    public static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .enableComplexMapKeySerialization()
            .registerTypeAdapter(TreeShape.class, new TreeShapeSerializer())
            .registerTypeAdapter(TreeTypeSimple.class, new TreeTypeSimpleSerializer())
            .registerTypeAdapter(BonsaiSoil.class, new BonsaiSoilSerializer())
            .create();

}
