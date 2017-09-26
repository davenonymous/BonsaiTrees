load("config/bonsaitrees/types.d/defaults.js");

var main = function(source) {
    var BlockPlanks = Java.type("net.minecraft.block.BlockPlanks");

    Java.from(BlockPlanks.EnumType.values()).forEach(function(woodType) {
        var meta = woodType.getMetadata();
        var type = woodType.getName();

        var vanillaType = new TreeTypeSimple("minecraft:" + type, "minecraft:sapling", meta);
        vanillaType.setSource(source);

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

        if(type == "dark_oak") {
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

var generateTree = function(type, world, pos, rand) {
    var Blocks = Java.type("net.minecraft.init.Blocks");
    var meta = type.getExampleStack().getMetadata();

    var state = Blocks.SAPLING.getStateFromMeta(meta);

    if(type.typeName == "minecraft:dark_oak") {
        world.setBlockState(pos.west(), state);
        world.setBlockState(pos.south(), state);
        world.setBlockState(pos.west().south(), state);
    }
    world.setBlockState(pos, state);

    Blocks.SAPLING.generateTree(world, pos, state, rand);
};