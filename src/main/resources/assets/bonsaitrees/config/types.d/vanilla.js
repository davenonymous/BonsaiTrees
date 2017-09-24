load("config/bonsaitrees/types.d/defaults.js");

var main = function() {
    var treeTypes = {
        'oak': 0,
        'spruce': 1,
        'birch': 2,
        'jungle': 3,
        'acacia': 4,
        'darkoak': 5
    };

    Object.keys(treeTypes).forEach(function(type) {
        var meta = treeTypes[type];
        var vanillaType = new TreeTypeSimple("minecraft:" + type, "minecraft:sapling", meta);

        vanillaType.addDrop("minecraft:stick", 0, stickAmount, stickChance);
        vanillaType.addDrop("minecraft:sapling", meta, saplingAmount, saplingChance);

        if(type == "oak") {
            vanillaType.addDrop("minecraft:log", meta, logAmount, logChance);
            vanillaType.addDrop("minecraft:apple", 0, fruitAmount, fruitChance);
            vanillaType.addDrop("minecraft:leaves", meta, leafAmount, leafChance);
        }

        if(type == "birch" || type == "spruce") {
            vanillaType.addDrop("minecraft:log", meta, logAmount, logChance);
            vanillaType.addDrop("minecraft:leaves", meta, leafAmount, leafChance);
        }

        if(type == "jungle") {
            vanillaType.addDrop("minecraft:log", meta, logAmount, logChance);
            vanillaType.addDrop("minecraft:leaves", meta, leafAmount, leafChance);
        }

        if(type == "darkoak") {
            vanillaType.addDrop("minecraft:log2", 1, logAmount, logChance);
            vanillaType.addDrop("minecraft:leaves2", 1, leafAmount, leafChance);
            vanillaType.setGrowTime(defaultGrowTime * 1.5);
        }

        if(type == "acacia") {
            vanillaType.setGrowTime(defaultGrowTime * 0.75);
            vanillaType.addDrop("minecraft:log2", 0, logAmount, logChance);
            vanillaType.addDrop("minecraft:leaves2", 0, leafAmount, leafChance);
        }

        TreeTypeRegistry.registerTreeType(vanillaType);
    });
};