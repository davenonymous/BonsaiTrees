var TreeType = Java.type("org.dave.bonsaitrees.trees.TreeType");
var TreeTypeRegistry = Java.type("org.dave.bonsaitrees.trees.TreeTypeRegistry");

var main = function() {
    var rubberTreeType = new TreeType("ic2:sapling", 0);
    rubberTreeType.addDrop("minecraft:stick", 0, 3, 50);
    rubberTreeType.addDrop("ic2:rubber_wood", 0, 1, 20);
    rubberTreeType.addDrop("ic2:sapling", 0, 1, 5);
    rubberTreeType.addDrop("ic2:leaves", 0, 1, 10);
    rubberTreeType.addDrop("ic2:misc_resource", 4, 3, 20);
    rubberTreeType.setGrowTime(900);

    TreeTypeRegistry.registerTreeType("ic2_rubber", rubberTreeType);
};