# BonsaiTrees :deciduous_tree: :palm_tree: :evergreen_tree:
A Minecraft mod adding a block that grows small trees in two block spaces.

You can grab the latest Travis build from [here](https://www.dropbox.com/sh/7yyd9dgii1epibu/AADARWLwgjOBu9wU-zZVHp-ra?dl=0).

## How to get started

1. Craft a Bonsai Pot and place it somewhere with at least one block of air above it
2. Get a compatible sapling and right-click it on the Bonsai Pot
3. Watch it grow.
4. You can then optionally harvest the tree by right clicking it with an axe or breaking the block.
   The Bonsai Pot itself only breaks when there is no sapling growing at the moment.
5. You can also upgrade your Bonsai Pot to a Hopping Bonsai Pot which automatically
   drops the items into inventories below the Pot. This might be disabled in the config
   though!

It is recommended to use JEI to look up compatible saplings and what they drop.
Waila and TheOneProbe will show what sapling is growing and its progress.


## Sapling compatibility

### Integrated support @ 2017-10-03:
- All Vanilla trees
- All Forestry trees incl. mods utilizing the Forestry Tree system (i.e. ExtraTrees)
- All Pam's Harvestcraft trees

And in no particular order:

IndustrialCraft 2, Integrated Dynamics, Totemic, Rustic, PrimalCore, Traverse,
Tinkers Construct, Natura, AbyssalCraft, Twilight Forest, Thaumcraft

### Generating Missing Shapes
Newer versions of Forestry or Pams might add more trees that are invisible when planted
in a Bonsai Pot. This is because there are two parts required for a tree being compatible:
- A TreeType definition, this can either be Java code or JSON
- Some shape descriptions for that tree type, these are all JSON
In case of these mods their tree types are dynamically added via Java, so they are simply
missing the corresponding shape files. The server log notifies you about these.

But it is relatively easy to auto-generate those shape files (and I appreciate pull requests
for them so others can benefit of your work) in most cases:
1. Create a new super-flat world, the following step will erase a 32x32 block area!
2. Look somewhere on the ground and enter the command:
   `/bonsaitrees generateMissingShapes yes`
3. This will generate all the missing shapes in the `configs/bonsaitrees/shapes.d` folder,
   make sure to have them on both client and server.
4. Restart the game and continue playing your normal world

If there is no way for Bonsai Trees to know how to actually grow a tree, you will need
to create the shape JSON yourself. There is another command helping you with this:
 `/bonsaitrees saveTreeShape <tree-type-name>`.
So your process in case you want to define some shapes for `mytreemod:redstone_tree`
would be something like this:
1. Grow a few of your trees or build a structure resembling your tree (maybe you want
   to grow bonsai redstone crystals?). Make sure all they are touching is dirt and air.
   This is really important or your shape will include whatever they were touching.
2. Look at it directly and enter the command `/bonsaitrees saveTreeShape mytreemod:redstone_tree`
3. Repeat step 2 for the other shapes of your tree so you have more than one tree shape
   growing in your bonsai pots.
4. After making sure you have the shape configs on the client as well as the server,
   restart the game and continue playing your normal world

### Making a Sapling compatible yourself

#### Simple trees via JSON files
Example: [config/types.d/ic2_rubber.json](https://github.com/thraaawn/BonsaiTrees/blob/master/src/main/resources/assets/bonsaitrees/config/types.d/ic2_rubber.json)

Fields:
- `name`: The name of the tree type, this must be unique and generally follows the
  ResourceLocation naming scheme. Only the first tree type with a given name will
  be loaded, all following tree types with the same name will be skipped.
- `mod`: Specify this if this tree type is only available if the given mod is loaded.
- `sapling`: Used to describe the sapling that grows into this tree type. This is
  simply the item registry name and the meta data.
- `growTimeMultiplier`: A factor the default grow time (configureable via config) will
  be multiplied with. Use this to change the time required for a tree of this type to grow.
- `drops`: Is an array of items being dropped. Each entry requires an item registry name
  and its metadata, but also a `type` it belongs to. You can think of this as a category
  of which there are 5: `WOOD`, `SAPLING`, `LEAVES`, `FRUIT` and `STICK`. These are used
  to determine the chance and amounts for this item to drop. You can also specify `CUSTOM`,
  but then you'd also have to specify a `count` property between 1 and 64 and a `chance`
  between 0.0 and 1.0.
- `worldgen`: Allows specifying a class extending WorldGenerator to be used when generating
  shapes. This can be used if the modded sapling does not extend BlockSapling or does not
  properly override the generateTree method of it.

Please consider creating a Pull Request for these, so other users can benefit from your
work as well!

#### Saplings with NBT data
These currently require Java code to be integrated. There is a work-in-progress API, but I'm
very certain that i'll change it again and it is not yet final. Please don't use it at this point.


## Configuration Options
There is a settings.conf in your configs/bonsaitrees/ folder that can be used to tweak
some of the gameplay settings of this mod:
- `*Chance` fields allow setting the default drop chances of items.
- `*Amount` fields allow setting the default drop amount of items.
- `maxTreeScale` is a client-side setting that applies this as a multiplier to the size of the
  bonsai trees. This is used to make them a little bit smaller than an actual block by default.
- `showChanceInJEI` can be used to prevent players from looking up drop chances in JEI
- `disabledIntegrations` can be used to disable individual Java based integrations, e.g. to disable
  Pam's Harvestcraft, you'd add `org.dave.bonsaitrees.integration.mods.PamsHarvestcraft`. You can
  get a list of integrations [here](https://github.com/thraaawn/BonsaiTrees/tree/master/src/main/java/org/dave/bonsaitrees/integration/mods).
- `disabledTreeTypes` can be used to disable individual tree types, e.g. to prevent users from
  growing Forestry's Hill Cherries in Bonsai Pots you'd add `forestry:hillCherry`. To list all
  registered tree types you can use the command `/bonsaitrees listTrees`.
- `disableHoppingBonsaiPot`: Disable Hopping Bonsai Pots functionality.


## CraftTweaker2 integration
You might need more precise control over which drops are being generated by the individual tree
types. Bonsai Trees add a few methods to CraftTweaker allowing you to remove and add drops however
you like. It also allows modifying the ticks it takes for a tree to fully grow.

The following zenscript example makes Vanilla Oak Trees drop buckets, but no leaves or sticks. It
also makes it so it only takes 10% of the usual time to grow a Bonsai Oak Tree.
```
mods.bonsaitrees.TreeDrops.addTreeDrop("minecraft:oak", <minecraft:bucket>, 0.01);
mods.bonsaitrees.TreeDrops.removeTreeDrop("minecraft:oak", <minecraft:leaves>);
mods.bonsaitrees.TreeDrops.removeTreeDrop("minecraft:oak", <minecraft:stick>);
mods.bonsaitrees.TreeGrowth.setTickMultiplier("minecraft:oak", 0.1);
```

If you want to change the drops of a Bonsai Tree, you have to remove the original drop first and
then re-add it with the new amount and chance, for example to make Sky Orchard Iron Trees drop more
Resin (4) with a higher chance (50%) use this:
```
mods.bonsaitrees.TreeDrops.removeTreeDrop("sky_orchard:iron", <sky_orchards:resin_iron>);
mods.bonsaitrees.TreeDrops.addTreeDrop("sky_orchard:iron", <sky_orchards:resin_iron> * 4, 0.5);
```
