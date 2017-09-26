load("config/bonsaitrees/types.d/defaults.js");

isEnabled = function() {
    var Loader = Java.type("net.minecraftforge.fml.common.Loader");
    return Loader.isModLoaded("forestry");
}

var main = function(source) {
    var TreeTypeForestry = Java.type("org.dave.bonsaitrees.trees.TreeTypeForestry");
    var TreeManager = Java.type("forestry.api.arboriculture.TreeManager");

    var defaultDrops = [
        { name: "minecraft:stick", meta: 0, amount: stickAmount, chance: stickChance }
    ];

    var blockedTrees = [
        "for.trees.species.giantSequoia",
        "for.trees.species.coastSequoia"
    ];

    TreeManager.treeRoot.getIndividualTemplates().forEach(function(tree) {
        var label = tree.getDisplayName();
        var id = tree.getGenome().getPrimary().getUnlocalizedName();

        if(blockedTrees.indexOf(id) != -1) {
            return;
        }

        var treeType = new TreeTypeForestry(tree, saplingAmount, saplingChance);
        treeType.setSource(source);

        var woodStack = tree.getGenome().getPrimary().getWoodProvider().getWoodStack();
        woodStack.setCount(logAmount);
        treeType.addDrop(woodStack, logChance);

        var leafStack = tree.getGenome().getPrimary().getLeafProvider().getDecorativeLeaves();
        leafStack.setCount(leafAmount);
        treeType.addDrop(leafStack, leafChance);

        tree.getProducts().entrySet().forEach(function(entry) {
            var fruitStack = entry.getKey();
            var chance = entry.getValue() * fruitChance;
            fruitStack.setCount(fruitAmount);

            treeType.addDrop(fruitStack, chance);
        });

        defaultDrops.forEach(function(drop) {
            treeType.addDrop(drop.name, drop.meta, drop.amount, drop.chance);
        });

        TreeTypeRegistry.registerTreeType(treeType);
    });
};

var generateTree = function(type, world, pos, rand) {
    var TreeManager = Java.type("forestry.api.arboriculture.TreeManager");

    var tree = type.getForestryTreeType();
    for (var x = 0; x < tree.getGirth(); x++) {
        for (var z = 0; z < tree.getGirth(); z++) {
            TreeManager.treeRoot.plantSapling(world, tree, null, pos.add(x, 0.0, z));
        }
    }

    var treeGen = tree.getTreeGenerator(world, pos, true);
    treeGen.generate(world, rand, pos);
};

var modifyTreeShape = function(type, blocks) {
    var ItemBlock = Java.type("net.minecraft.item.ItemBlock");

    var tree = type.getForestryTreeType();
    blocks.entrySet().forEach(function(entry) {
        if (entry.getValue().getBlock().getRegistryName().toString() == "forestry:leaves") {
            var leafStack = tree.getGenome().getPrimary().getLeafProvider().getDecorativeLeaves();
            var leafItem = leafStack.getItem();
            if (leafItem instanceof ItemBlock) {
                blocks.put(entry.getKey(), leafItem.getBlock().getStateFromMeta(leafStack.getMetadata()));
            }
        }
    });
};