load("config/bonsaitrees/types.d/defaults.js");

isEnabled = function() {
    var Loader = Java.type("net.minecraftforge.fml.common.Loader");
    return Loader.isModLoaded("forestry");
}

var main = function() {
    var TreeTypeForestry = Java.type("org.dave.bonsaitrees.trees.TreeTypeForestry");
    var TreeManager = Java.type("forestry.api.arboriculture.TreeManager");

    var defaultDrops = [
        { name: "minecraft:stick", meta: 0, amount: stickAmount, chance: stickChance }
    ];

    var blockedTrees = [
        "forestry:giantSequoia",
        "forestry:coastSequoia"
    ];

    TreeManager.treeRoot.getIndividualTemplates().forEach(function(tree) {
        var label = tree.getDisplayName();
        var id = tree.getGenome().getPrimary().getUnlocalizedName();
        var modId = tree.getGenome().getPrimary().getModID();

        if(modId == "forestry") {
            id = id.replace('for.trees.species.', '');
        } else if(modId == "extratrees") {
            id = id.replace('extratrees.species.', '');
            id = id.replace('.name', '');
        }

        var fullId = modId + ":" + id;
        if(blockedTrees.indexOf(fullId) != -1) {
            return;
        }

        var treeType = new TreeTypeForestry(fullId, saplingAmount, saplingChance);

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