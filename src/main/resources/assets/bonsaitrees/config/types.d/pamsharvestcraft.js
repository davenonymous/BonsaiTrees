load("config/bonsaitrees/types.d/defaults.js");

isEnabled = function() {
    var Loader = Java.type("net.minecraftforge.fml.common.Loader");
    return Loader.isModLoaded("harvestcraft");
}

var main = function(source) {
    var FruitRegistry = Java.type("com.pam.harvestcraft.blocks.FruitRegistry");
    var BlockPamSapling = Java.type("com.pam.harvestcraft.blocks.growables.BlockPamSapling");

    var ItemStack = Java.type("net.minecraft.item.ItemStack");
    var PamHelper = Java.type("org.dave.bonsaitrees.compat.PamHelper");

    FruitRegistry.getSaplings().forEach(function(sapling) {
        var name = sapling.getRegistryName().toString().replace("_sapling", "");

        var pamFruitBlock = sapling.getFruit();
        var pamFruitItem = pamFruitBlock.getFruitItem();

        var pamTreeType = new TreeTypeSimple(name, new ItemStack(sapling, 1, 0));
        pamTreeType.setSource(source);
        pamTreeType.addDrop("minecraft:stick", 0, stickAmount, stickChance);
        pamTreeType.addDrop(new ItemStack(sapling, saplingAmount, 0), saplingChance);

        var logState = PamHelper.reflectPamSapling(sapling, "logState");
        pamTreeType.addDrop(new ItemStack(logState.getBlock(), logAmount, logState.getBlock().getMetaFromState(logState)), logChance);

        pamTreeType.addDrop(new ItemStack(pamFruitItem, fruitAmount, 0), fruitChance);

        var leafState = PamHelper.reflectPamSapling(sapling, "leavesState");
        pamTreeType.addDrop(new ItemStack(leafState.getBlock(), leafAmount, leafState.getBlock().getMetaFromState(leafState)), leafChance);

        TreeTypeRegistry.registerTreeType(pamTreeType);
    });
};

var generateTree = function(type, world, pos, rand) {
    var FruitRegistry = Java.type("com.pam.harvestcraft.blocks.FruitRegistry");
    var BlockPamSapling = Java.type("com.pam.harvestcraft.blocks.growables.BlockPamSapling");

    FruitRegistry.getSaplings().forEach(function(sapling) {
        var name = sapling.getRegistryName().toString().replace("_sapling", "");
        if(name != type.typeName) {
            return;
        }

        world.setBlockState(pos, sapling.getDefaultState());
        sapling.grow(world, rand, pos, sapling.getDefaultState());
    });
};