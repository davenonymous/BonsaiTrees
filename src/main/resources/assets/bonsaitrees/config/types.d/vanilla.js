var TreeType = Java.type("org.dave.bonsaitrees.trees.TreeType");
var TreeTypeRegistry = Java.type("org.dave.bonsaitrees.trees.TreeTypeRegistry");

var main = function() {
    var treeTypes = {
        'oak': 0,
        'spruce': 1,
        'birch': 2,
        'jungle': 3,
        'acacia': 4,
        'darkoak': 5
    };

    var leaveDropChance = 10;
    var logDropChance = 15;

    Object.keys(treeTypes).forEach(function(type) {
        var meta = treeTypes[type];
        var vanillaType = new TreeType("minecraft:sapling", meta);

        vanillaType.addDrop("minecraft:stick", 0, 3, 50);
        vanillaType.addDrop("minecraft:planks", meta, 1, 25);
        vanillaType.addDrop("minecraft:sapling", meta, 1, 5);

        if(type == "oak") {
            vanillaType.addDrop("minecraft:log", meta, 2, logDropChance);
            vanillaType.addDrop("minecraft:apple", 0, 1, 10);
            vanillaType.addDrop("minecraft:leaves", meta, 1, leaveDropChance);
        }

        if(type == "birch" || type == "spruce") {
            vanillaType.addDrop("minecraft:log", meta, 2, logDropChance);
            vanillaType.addDrop("minecraft:leaves", meta, 1, leaveDropChance);
        }

        if(type == "jungle") {
            vanillaType.addDrop("minecraft:log", meta, 2, logDropChance);
            vanillaType.addDrop("minecraft:leaves", meta, 1, leaveDropChance);
        }

        if(type == "darkoak") {
            vanillaType.addDrop("minecraft:log2", 1, 2, logDropChance);
            vanillaType.addDrop("minecraft:leaves2", 1, 1, leaveDropChance);
            vanillaType.setGrowTime(900);
        }

        if(type == "acacia") {
            vanillaType.setGrowTime(450);
            vanillaType.addDrop("minecraft:log2", 0, 2, logDropChance);
            vanillaType.addDrop("minecraft:leaves2", 0, 1, leaveDropChance);
        }

        TreeTypeRegistry.registerTreeType(type, vanillaType);
    });
};