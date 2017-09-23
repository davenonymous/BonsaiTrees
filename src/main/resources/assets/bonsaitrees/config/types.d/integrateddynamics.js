load("config/bonsaitrees/types.d/defaults.js");

var main = function() {
    var menrilTree = new TreeTypeSimple("integrateddynamics:menril", "integrateddynamics:menril_sapling", 0);
    menrilTree.addDrop("minecraft:stick", 0, stickAmount, stickChance);
    menrilTree.addDrop("integrateddynamics:menril_log", 0, logAmount, logChance);
    menrilTree.addDrop("integrateddynamics:menril_sapling", 0, saplingAmount, saplingChance);
    menrilTree.addDrop("integrateddynamics:menril_leaves", 0, leafAmount, leafChance);

    menrilTree.addDrop("integrateddynamics:crystalized_menril_chunk", 0, fruitAmount, fruitChance);
    menrilTree.addDrop("integrateddynamics:menril_berries", 0, fruitAmount, fruitChance);

    menrilTree.setGrowTime(defaultGrowTime);

    TreeTypeRegistry.registerTreeType(menrilTree);
};