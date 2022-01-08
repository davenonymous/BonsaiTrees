![Bonsai Trees](https://github.com/thraaawn/BonsaiTrees/blob/1.18.1/assets/readme-logo.png?raw=true)

# Bonsai Trees 3

A Minecraft mod adding a block that grows small trees in a single block space.

## How to get started

1. Craft a Bonsai Pot and place it somewhere nice.
2. Find some soil to use in your pot. Dirt will do for now. Right-click it on the Bonsai Pot.
3. Get a tree sapling and right-click it on the Bonsai Pot. Or open the GUI and insert it into the sapling slot.
4. Watch it grow.
5. You can then optionally harvest the tree by clicking the "Axe" button in the GUI.
6. You can also upgrade your Bonsai Pot with various items. For now, put a wooden axe into one of the upgrade slots -
   your Bonsai will now be cut automatically.
7. Place a hopper below the Bonsai to extract the harvested items. Or insert it into an upgrade slot - the bonsai will
   then automatically export all items on the next harvest.
8. Use dyes to color your pot. Just right-click the pot while holding any dye.

A particular soil might be needed for some of the trees. And the different types of soil provide different boosts to the
growth of your Bonsai. It is recommended to use JEI to look up compatible saplings and available soils. TheOneProbe will
show what sapling is growing on what soil and its progress.

## Upgrades

### Hopper

Insert a hopper into one of the upgrade slots to make the Bonsai Pot automatically export all harvested items into the
inventory below. This is not needed to extract the items in a different way, e.g. you can also place the hopper or some
piping below the pot.

### Axes - or tools that can cut trees

Insert any kind of axe into an upgrade slot to make the Bonsai Bot automatically cut the tree for you once its fully
grown. But be aware that items that do not fit into the output buffer will be voided. You can enchant the axe with
Efficiency, Fortune and/or Silk Touch to get additional upgrade benefits!

### Silk Touch

Some of the things a tree produces requires a Silk Touch upgrade. You can either put an enchanted axe or an
appropriately enchanted book into one of the upgrade slots. This does not prevent you from putting additional Fortune
Upgrades into the Bonsai Pot.

### Fortune

Increases number of rolls and chance for each possible drop. The higher the level the more items you get and the higher
the chance to get uncommon drops.

### Efficiency

Reduces the time it takes for a bonsai to fully grow. The higher the upgrade level the faster the growth.

## Tree Compatibility

Supported Saplings and Soils are completely configurable via .json files. There are a bunch of examples available in the
repository for all the vanilla trees. But to make this more straight forward here's a quick rundown of what is needed
for a tree to be supported:

### Step 0, JSON

Know how JSON works. [Here's](https://cheatography.com/gaston/cheat-sheets/json/) a good quick rundown. Especially don't
include any of the comments from the examples here in the .json files.

### Step 1, Soil

Create the soil recipe first. You can skip this if you don't have custom soil requirements.

```js
{
  "type": "bonsaitrees3:soil",                 // The recipe type. Must be "bonsaitrees3:soil"
  "mod": "minecraft",                          // The mod required for this soil to get loaded
  "tickModifier": 1.0,                         // The time to grow a tree is multiplied by this value
  "soil": {
    "item": "minecraft:grass_block"            // The item that plants this soil in the bonsai pot
  },
  "compatibleSoilTags": [
    "grass"                                    // Only trees also having these tags can grow on this soil
  ],
  "display": {
    "block": "minecraft:grass_block"            // The block that's being used to render the soil
    // "fluid": "minecraft:lava"                // You can also use fluids here!
  }
}
```

### Step 2, Sapling

Create the sapling recipe:

```js
{
  "type": "bonsaitrees3:sapling",               // The recipe type. Must be "bonsaitrees3:sapling"!
  "mod": "minecraft",                           // The mod required for this tree to get loaded
  "sapling": { "item": "minecraft:azalea" },    // The item that grows this tree (or whatever)
  "drops": [                                    // An array of drops this tree produces each harvest
    {
      "rolls": 1, "chance": 0.05,               // The number of rolls and the chance for each roll for this drop
      "result": { "item": "minecraft:azalea" }  // The name of the item for this drop
    },
    {
      "rolls": 1, "chance": 0.75,
      "result": { "item": "minecraft:oak_log" }
    },
    {
      "rolls": 3, "chance": 0.2,
      "result": { "item": "minecraft:stick" }
    },
    {
      "rolls": 2, "chance": 0.15,
      "result": { "item": "minecraft:azalea_leaves" },
      "requiresSilkTouch": true                 // Indicates that this item only drops if a Silk Touch upgrade is present
    },
    {
      "rolls": 2, "chance": 0.05,
      "result": { "item": "minecraft:flowering_azalea_leaves" },
      "requiresSilkTouch": true
    }
  ],
  "compatibleSoilTags": ["dirt", "grass"]       // What types of soil this tree can grow on
}
```

### Step 3, Model

Create the model for the tree or whatever it is that you want to make growable:
That's pretty easy as long as you don't want to write the json file yourself.

a) Start up the game and find the tree, structure that should be grown in the pot.

b) Isolate it from any other blocks touching it. Diagonally also counts! To make sure just add a 2 block margin of air
around the "thing".

c) Run the /bonsai maker X Y Z command by looking at the thing and using tab completion. This will copy the wanted JSON
into your systems clipboard. Paste it into the newly created file.

d) Adjust the `type` property to `bonsaitrees3:sapling/XXX/YYY`, where `XXX` is the mod id and `YYY` is essentially the
name of the sapling file. Following the example above that would
be `"type": "bonsaitrees3:sapling/minecraft/azalea_tree"`.

<details>
  <summary>Do not write these yourself!</summary>

```js
{
  "type": "bonsaitrees3:sapling/minecraft/azalea_tree",
  "version": 3,
  "ref": {
    "a": {
      "block": "minecraft:azalea_leaves",
      "properties": {
        "distance": "1",
        "persistent": "false"
      }
    },
    "b": {
      "block": "minecraft:flowering_azalea_leaves",
      "properties": {
        "distance": "2",
        "persistent": "false"
      }
    },
    "c": {
      "block": "minecraft:azalea_leaves",
      "properties": {
        "distance": "4",
        "persistent": "false"
      }
    },
    "d": {
      "block": "minecraft:oak_log",
      "properties": {
        "axis": "y"
      }
    },
    "e": {
      "block": "minecraft:flowering_azalea_leaves",
      "properties": {
        "distance": "3",
        "persistent": "false"
      }
    },
    "f": {
      "block": "minecraft:azalea_leaves",
      "properties": {
        "distance": "3",
        "persistent": "false"
      }
    },
    "g": {
      "block": "minecraft:azalea_leaves",
      "properties": {
        "distance": "2",
        "persistent": "false"
      }
    },
    "h": {
      "block": "minecraft:flowering_azalea_leaves",
      "properties": {
        "distance": "4",
        "persistent": "false"
      }
    },
    "i": {
      "block": "minecraft:flowering_azalea_leaves",
      "properties": {
        "distance": "5",
        "persistent": "false"
      }
    },
    "j": {
      "block": "minecraft:azalea_leaves",
      "properties": {
        "distance": "5",
        "persistent": "false"
      }
    },
    "k": {
      "block": "minecraft:azalea_leaves",
      "properties": {
        "distance": "6",
        "persistent": "false"
      }
    },
    "l": {
      "block": "minecraft:azalea_leaves",
      "properties": {
        "distance": "7",
        "persistent": "false"
      }
    },
    "m": {
      "block": "minecraft:flowering_azalea_leaves",
      "properties": {
        "distance": "1",
        "persistent": "false"
      }
    },
    "n": {
      "block": "minecraft:flowering_azalea_leaves",
      "properties": {
        "distance": "7",
        "persistent": "false"
      }
    }
  },
  "shape": [
    [
      "     ",
      "     ",
      "  f  ",
      "   ll",
      "  m  ",
      "     ",
      "     "
    ],
    [
      " cf n",
      " fgf ",
      " fgfc",
      " gm  ",
      " ad k",
      "  d  ",
      "  d  "
    ],
    [
      "cfge ",
      "f agf",
      "fgabf",
      "gadm ",
      " gm j",
      "     ",
      "     "
    ],
    [
      "fgab ",
      "badag",
      "badmg",
      "fgagf",
      " f  c",
      "     ",
      "     "
    ],
    [
      " bagf",
      "gadag",
      "fgag ",
      " fg c",
      "ic   ",
      "     ",
      "     "
    ],
    [
      "he   ",
      "egabf",
      "hfge ",
      "  f  ",
      "     ",
      "     ",
      "     "
    ],
    [
      " c c ",
      "   fc",
      " h   ",
      "     ",
      "     ",
      "     ",
      "     "
    ]
  ]
}
```

</details>

### Step 4: Bundle it up

Equipped with these three files you now need to either create a Pull Request to get the support into the mod itself. Or
you create a data and a resource pack to distribute with your modpack. The files for the soil and sapling recipe belong
into a data pack, whereas the model belongs into a resource pack. The easiest way to get going there is to use
the [Open Loader](https://www.curseforge.com/minecraft/mc-mods/open-loader) mod.

Where the files need to get placed depends on the way you want to integrate these changes. For Pull Requests this should
be

- `src/main/resources/assets/bonsaitrees3/models/tree/XXX/YYY.json` for the model
- `src/main/resources/data/bonsaitrees3/recipes/sapling/XXX/YYY.json` for sapling recipes
- `src/main/resources/data/bonsaitrees3/recipes/soil/XXX/YYY.json` for sapling recipes

If you create a datapack or resource pack your paths will be slightly different. Please read the
[Open Loader](https://www.curseforge.com/minecraft/mc-mods/open-loader) description and the wiki pages it links to
([1](https://minecraft.fandom.com/wiki/Data_pack), [2](https://minecraft.fandom.com/wiki/Resource_Pack)) to learn more
about this.

## Contributing to the project

You can easily help in multiple ways:

- Add translations using [POEditor](https://poeditor.com/join/project?hash=suslQZqFoE)
- Create json files to support trees added by other mods
- Create data packs for originally unintended things:
  e.g. create a data+resource pack that allows growing iron/copper/gold nuggets. We are happy to promote those here and
  on the CurseForge project page.
- I am also looking for someone who can deal with Pull Requests for new trees/soils. If you know your way around json
  and git and want to help, please feel free to contact me.