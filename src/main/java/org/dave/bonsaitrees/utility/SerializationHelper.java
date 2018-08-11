package org.dave.bonsaitrees.utility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.dave.bonsaitrees.api.TreeTypeSimple;
import org.dave.bonsaitrees.soils.BonsaiSoil;
import org.dave.bonsaitrees.soils.BonsaiSoilSerializer;
import org.dave.bonsaitrees.trees.TreeShapeSerializer;
import org.dave.bonsaitrees.trees.TreeShape;
import org.dave.bonsaitrees.trees.TreeTypeSimpleSerializer;

import java.util.List;

public class SerializationHelper {
    public static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .enableComplexMapKeySerialization()
            .registerTypeAdapter(TreeShape.class, new TreeShapeSerializer())
            .registerTypeAdapter(TreeTypeSimple.class, new TreeTypeSimpleSerializer())
            .registerTypeAdapter(new TypeToken<List<BonsaiSoil>>(){}.getType(), new BonsaiSoilSerializer())
            .create();

}
