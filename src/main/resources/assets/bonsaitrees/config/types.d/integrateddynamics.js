load("config/bonsaitrees/types.d/defaults.js");

isEnabled = function() {
    var Loader = Java.type("net.minecraftforge.fml.common.Loader");
    return Loader.isModLoaded("integrateddynamics");
}

var main = function(source) {
    var menrilTree = new TreeTypeSimple("integrateddynamics:menril", "integrateddynamics:menril_sapling", 0);
    menrilTree.setSource(source);
    menrilTree.addDrop("minecraft:stick", 0, stickAmount, stickChance);
    menrilTree.addDrop("integrateddynamics:menril_log", 0, logAmount, logChance);
    menrilTree.addDrop("integrateddynamics:menril_sapling", 0, saplingAmount, saplingChance);
    menrilTree.addDrop("integrateddynamics:menril_leaves", 0, leafAmount, leafChance);

    menrilTree.addDrop("integrateddynamics:crystalized_menril_chunk", 0, fruitAmount, fruitChance);
    menrilTree.addDrop("integrateddynamics:menril_berries", 0, fruitAmount, fruitChance);

    menrilTree.setGrowTime(defaultGrowTime);

    TreeTypeRegistry.registerTreeType(menrilTree);
};