load("config/bonsaitrees/types.d/defaults.js");

isEnabled = function() {
    var Loader = Java.type("net.minecraftforge.fml.common.Loader");
    return Loader.isModLoaded("forestry");
}

var main = function() {
    var TreeTypeForestry = Java.type("org.dave.bonsaitrees.trees.TreeTypeForestry");

    var defaultDrops = [
        { name: "minecraft:stick", meta: 0, amount: stickAmount, chance: stickChance }
    ];

    var forestryTrees = {
        appleOak: {
            drops: defaultDrops.concat([
                { name: "minecraft:planks", meta: 0, amount: plankAmount, chance: plankChance },
                { name: "minecraft:log", meta: 0, amount: logAmount, chance: logChance },
                { name: "forestry:leaves.decorative.0", meta: 0, amount: leafAmount, chance: leafChance },
                { name: "minecraft:apple", meta: 0, amount: fruitAmount, chance: fruitChance }
            ]),
            growTime: defaultGrowTime
        },
        darkOak: {
            drops: defaultDrops.concat([
                { name: "minecraft:planks", meta: 5, amount: plankAmount, chance: plankChance },
                { name: "minecraft:log2", meta: 1, amount: logAmount, chance: logChance },
                { name: "forestry:leaves.decorative.0", meta: 1, amount: leafAmount, chance: leafChance }
            ]),
            growTime: defaultGrowTime
        },
        silverBirch: {
            drops: defaultDrops.concat([
                { name: "minecraft:planks", meta: 2, amount: plankAmount, chance: plankChance },
                { name: "minecraft:log", meta: 2, amount: logAmount, chance: logChance },
                { name: "forestry:leaves.decorative.0", meta: 2, amount: leafAmount, chance: leafChance }
            ]),
            growTime: defaultGrowTime
        },
        silverLime: {
            drops: defaultDrops.concat([
                { name: "forestry:planks.0", meta: 3, amount: plankAmount, chance: plankChance },
                { name: "forestry:logs.0", meta: 3, amount: logAmount, chance: logChance },
                { name: "forestry:leaves.decorative.0", meta: 3, amount: leafAmount, chance: leafChance }
            ]),
            growTime: defaultGrowTime
        },
        commonWalnut: {
            drops: defaultDrops.concat([
                { name: "forestry:planks.0", meta: 13, amount: plankAmount, chance: plankChance },
                { name: "forestry:logs.3", meta: 1, amount: logAmount, chance: logChance },
                { name: "forestry:leaves.decorative.0", meta: 4, amount: leafAmount, chance: leafChance },
                { name: "forestry:fruits", meta: 1, amount: fruitAmount, chance: 5 }
            ]),
            growTime: defaultGrowTime
        },
        sweetChestnut: {
            drops: defaultDrops.concat([
                { name: "forestry:planks.0", meta: 4, amount: plankAmount, chance: plankChance },
                { name: "forestry:logs.1", meta: 0, amount: logAmount, chance: logChance },
                { name: "forestry:leaves.decorative.0", meta: 5, amount: leafAmount, chance: leafChance },
                { name: "forestry:fruits", meta: 2, amount: 1, chance: 5 }
            ]),
            growTime: defaultGrowTime
        },
        hillCherry: {
            drops: defaultDrops.concat([
                { name: "forestry:planks.0", meta: 15, amount: plankAmount, chance: plankChance },
                { name: "forestry:logs.3", meta: 3, amount: logAmount, chance: logChance },
                { name: "forestry:leaves.decorative.0", meta: 6, amount: leafAmount, chance: leafChance },
                { name: "forestry:fruits", meta: 0, amount: fruitAmount, chance: fruitChance }
            ]),
            growTime: defaultGrowTime
        },
        lemon: {
            drops: defaultDrops.concat([
                { name: "forestry:planks.1", meta: 7, amount: plankAmount, chance: plankChance },
                { name: "forestry:logs.5", meta: 3, amount: logAmount, chance: logChance },
                { name: "forestry:leaves.decorative.0", meta: 7, amount: leafAmount, chance: leafChance },
                { name: "forestry:fruits", meta: 3, amount: fruitAmount, chance: fruitChance }
            ]),
            growTime: defaultGrowTime
        },
        plum: {
            drops: defaultDrops.concat([
                { name: "forestry:planks.1", meta: 5, amount: plankAmount, chance: plankChance },
                { name: "forestry:logs.5", meta: 1, amount: logAmount, chance: logChance },
                { name: "forestry:leaves.decorative.0", meta: 8, amount: leafAmount, chance: leafChance },
                { name: "forestry:fruits", meta: 4, amount: fruitAmount, chance: fruitChance }
            ]),
            growTime: defaultGrowTime
        },
        sugarMaple: {
            drops: defaultDrops.concat([
                { name: "forestry:planks.1", meta: 6, amount: plankAmount, chance: plankChance },
                { name: "forestry:logs.5", meta: 2, amount: logAmount, chance: logChance },
                { name: "forestry:leaves.decorative.0", meta: 9, amount: leafAmount, chance: leafChance }
            ]),
            growTime: defaultGrowTime
        },
        redSpruce: {
            drops: defaultDrops.concat([
                { name: "minecraft:planks", meta: 1, amount: plankAmount, chance: plankChance },
                { name: "minecraft:log", meta: 1, amount: logAmount, chance: logChance },
                { name: "forestry:leaves.decorative.0", meta: 10, amount: leafAmount, chance: leafChance }
            ]),
            growTime: defaultGrowTime
        },
        mundaneLarch: {
            drops: defaultDrops.concat([
                { name: "forestry:planks.0", meta: 0, amount: plankAmount, chance: plankChance },
                { name: "forestry:logs.0", meta: 0, amount: logAmount, chance: logChance },
                { name: "forestry:leaves.decorative.0", meta: 11, amount: leafAmount, chance: leafChance }
            ]),
            growTime: defaultGrowTime
        },
        bullPine: {
            drops: defaultDrops.concat([
                { name: "forestry:planks.1", meta: 4, amount: plankAmount, chance: plankChance },
                { name: "forestry:logs.5", meta: 0, amount: logAmount, chance: logChance },
                { name: "forestry:leaves.decorative.0", meta: 12, amount: leafAmount, chance: leafChance }
            ]),
            growTime: defaultGrowTime
        },
        jungle: {
            drops: defaultDrops.concat([
                { name: "minecraft:planks", meta: 3, amount: plankAmount, chance: plankChance },
                { name: "minecraft:log", meta: 3, amount: logAmount, chance: logChance },
                { name: "forestry:leaves.decorative.0", meta: 15, amount: leafAmount, chance: leafChance }
            ]),
            growTime: defaultGrowTime
        },
        teak: {
            drops: defaultDrops.concat([
                { name: "forestry:planks.0", meta: 1, amount: plankAmount, chance: plankChance },
                { name: "forestry:logs.0", meta: 1, amount: logAmount, chance: logChance },
                { name: "forestry:leaves.decorative.1", meta: 0, amount: leafAmount, chance: leafChance }
            ]),
            growTime: defaultGrowTime
        },
        ipe: {
            drops: defaultDrops.concat([
                { name: "forestry:planks.1", meta: 9, amount: plankAmount, chance: plankChance },
                { name: "forestry:logs.6", meta: 1, amount: logAmount, chance: logChance },
                { name: "forestry:leaves.decorative.1", meta: 1, amount: leafAmount, chance: leafChance }
            ]),
            growTime: defaultGrowTime
        },
        kapok: {
            drops: defaultDrops.concat([
                { name: "forestry:planks.0", meta: 8, amount: plankAmount, chance: plankChance },
                { name: "forestry:logs.2", meta: 0, amount: logAmount, chance: logChance },
                { name: "forestry:leaves.decorative.1", meta: 2, amount: leafAmount, chance: leafChance }
            ]),
            growTime: defaultGrowTime
        },
        myrtleEbony: {
            drops: defaultDrops.concat([
                { name: "forestry:planks.0", meta: 9, amount: plankAmount, chance: plankChance },
                { name: "forestry:logs.2", meta: 1, amount: logAmount, chance: logChance },
                { name: "forestry:leaves.decorative.1", meta: 3, amount: leafAmount, chance: leafChance }
            ]),
            growTime: defaultGrowTime
        },
        zebrawood: {
            drops: defaultDrops.concat([
                { name: "forestry:planks.1", meta: 12, amount: plankAmount, chance: plankChance },
                { name: "forestry:logs.7", meta: 0, amount: logAmount, chance: logChance },
                { name: "forestry:leaves.decorative.1", meta: 4, amount: leafAmount, chance: leafChance }
            ]),
            growTime: defaultGrowTime
        },
        yellowMeranti: {
            drops: defaultDrops.concat([
                { name: "forestry:planks.0", meta: 10, amount: plankAmount, chance: plankChance },
                { name: "forestry:logs.2", meta: 2, amount: logAmount, chance: logChance },
                { name: "forestry:leaves.decorative.1", meta: 5, amount: leafAmount, chance: leafChance }
            ]),
            growTime: defaultGrowTime
        },
        acacia: {
            drops: defaultDrops.concat([
                { name: "minecraft:planks", meta: 4, amount: plankAmount, chance: plankChance },
                { name: "minecraft:log2", meta: 0, amount: logAmount, chance: logChance },
                { name: "forestry:leaves.decorative.1", meta: 6, amount: leafAmount, chance: leafChance }
            ]),
            growTime: defaultGrowTime
        },
        desertAcacia: {
            drops: defaultDrops.concat([
                { name: "forestry:planks.0", meta: 2, amount: plankAmount, chance: plankChance },
                { name: "forestry:logs.0", meta: 2, amount: logAmount, chance: logChance },
                { name: "forestry:leaves.decorative.1", meta: 7, amount: leafAmount, chance: leafChance }
            ]),
            growTime: defaultGrowTime
        },
        padauk: {
            drops: defaultDrops.concat([
                { name: "forestry:planks.1", meta: 10, amount: plankAmount, chance: plankChance },
                { name: "forestry:logs.6", meta: 2, amount: logAmount, chance: logChance },
                { name: "forestry:leaves.decorative.1", meta: 8, amount: leafAmount, chance: leafChance }
            ]),
            growTime: defaultGrowTime
        },
        balsa: {
            drops: defaultDrops.concat([
                { name: "forestry:planks.0", meta: 11, amount: plankAmount, chance: plankChance },
                { name: "forestry:logs.2", meta: 3, amount: logAmount, chance: logChance },
                { name: "forestry:leaves.decorative.1", meta: 9, amount: leafAmount, chance: leafChance }
            ]),
            growTime: defaultGrowTime
        },
        cocobolo: {
            drops: defaultDrops.concat([
                { name: "forestry:planks.1", meta: 11, amount: plankAmount, chance: plankChance },
                { name: "forestry:logs.6", meta: 3, amount: logAmount, chance: logChance },
                { name: "forestry:leaves.decorative.1", meta: 10, amount: leafAmount, chance: leafChance }
            ]),
            growTime: defaultGrowTime
        },
        wenge: {
            drops: defaultDrops.concat([
                { name: "forestry:planks.0", meta: 5, amount: plankAmount, chance: plankChance },
                { name: "forestry:logs.1", meta: 1, amount: logAmount, chance: logChance },
                { name: "forestry:leaves.decorative.1", meta: 11, amount: leafAmount, chance: leafChance }
            ]),
            growTime: defaultGrowTime
        },
        grandidierBaobab: {
            drops: defaultDrops.concat([
                { name: "forestry:planks.0", meta: 6, amount: plankAmount, chance: plankChance },
                { name: "forestry:logs.1", meta: 2, amount: logAmount, chance: logChance },
                { name: "forestry:leaves.decorative.1", meta: 12, amount: leafAmount, chance: leafChance }
            ]),
            growTime: defaultGrowTime
        },
        blueMahoe: {
            drops: defaultDrops.concat([
                { name: "forestry:planks.1", meta: 0, amount: plankAmount, chance: plankChance },
                { name: "forestry:logs.4", meta: 0, amount: logAmount, chance: logChance },
                { name: "forestry:leaves.decorative.1", meta: 13, amount: leafAmount, chance: leafChance }
            ]),
            growTime: defaultGrowTime
        },
        whiteWillow: {
            drops: defaultDrops.concat([
                { name: "forestry:planks.0", meta: 12, amount: plankAmount, chance: plankChance },
                { name: "forestry:logs.3", meta: 0, amount: logAmount, chance: logChance },
                { name: "forestry:leaves.decorative.1", meta: 14, amount: leafAmount, chance: leafChance }
            ]),
            growTime: defaultGrowTime
        },
        sipiri: {
            drops: defaultDrops.concat([
                { name: "forestry:planks.0", meta: 14, amount: plankAmount, chance: plankChance },
                { name: "forestry:logs.3", meta: 2, amount: logAmount, chance: logChance },
                { name: "forestry:leaves.decorative.1", meta: 15, amount: leafAmount, chance: leafChance }
            ]),
            growTime: defaultGrowTime
        },
        papaya: {
            drops: defaultDrops.concat([
                { name: "forestry:planks.1", meta: 3, amount: plankAmount, chance: plankChance },
                { name: "forestry:logs.4", meta: 3, amount: logAmount, chance: logChance },
                { name: "forestry:leaves.decorative.2", meta: 0, amount: leafAmount, chance: leafChance },
                { name: "forestry:fruits", meta: 6, amount: fruitAmount, chance: fruitChance }
            ]),
            growTime: defaultGrowTime
        },
        datePalm: {
            drops: defaultDrops.concat([
                { name: "forestry:planks.1", meta: 2, amount: plankAmount, chance: plankChance },
                { name: "forestry:logs.4", meta: 2, amount: logAmount, chance: logChance },
                { name: "forestry:leaves.decorative.2", meta: 1, amount: leafAmount, chance: leafChance },
                { name: "forestry:fruits", meta: 5, amount: fruitAmount, chance: fruitChance }
            ]),
            growTime: defaultGrowTime
        },
        whitePoplar: {
            drops: defaultDrops.concat([
                { name: "forestry:planks.1", meta: 1, amount: plankAmount, chance: plankChance },
                { name: "forestry:logs.4", meta: 1, amount: logAmount, chance: logChance },
                { name: "forestry:leaves.decorative.2", meta: 2, amount: leafAmount, chance: leafChance }
            ]),
            growTime: defaultGrowTime
        }
    };

    Object.keys(forestryTrees).forEach(function(typeName) {
        var data = forestryTrees[typeName];

        var treeType = new TreeTypeForestry("forestry:" + typeName, saplingAmount, saplingChance);
        if(data.growTime != null) {
            treeType.setGrowTime(data.growTime);
        }
        if(data.drops != null && data.drops.length > 0) {
            data.drops.forEach(function(drop) {
                treeType.addDrop(drop.name, drop.meta, drop.amount, drop.chance);
            });
        }

        TreeTypeRegistry.registerTreeType(treeType);
    });
};