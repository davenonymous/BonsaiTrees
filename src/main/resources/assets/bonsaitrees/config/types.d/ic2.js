load("config/bonsaitrees/types.d/defaults.js");

isEnabled = function() {
    var Loader = Java.type("net.minecraftforge.fml.common.Loader");
    return Loader.isModLoaded("ic2");
}

var main = function() {
    var rubberTreeType = new TreeTypeSimple("ic2:rubber", "ic2:sapling", 0);
    rubberTreeType.addDrop("minecraft:stick", 0, stickAmount, stickChance);
    rubberTreeType.addDrop("ic2:rubber_wood", 0, logAmount, logChance);
    rubberTreeType.addDrop("ic2:sapling", 0, saplingAmount, saplingChance);
    rubberTreeType.addDrop("ic2:leaves", 0, leafAmount, leafChance);

    // resin
    rubberTreeType.addDrop("ic2:misc_resource", 4, fruitAmount, fruitChance);

    rubberTreeType.setGrowTime(defaultGrowTime * 1.5);

    TreeTypeRegistry.registerTreeType(rubberTreeType);
};