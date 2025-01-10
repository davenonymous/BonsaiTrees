![Bonsai Trees](https://github.com/thraaawn/BonsaiTrees/blob/1.18.1/assets/readme-logo.png?raw=true)

# Bonsai Trees 4

A Minecraft mod adding a block that grows small trees in a single block space.

For older versions of the documentation, see the other branches:
- [1.20.1](https://github.com/davenonymous/BonsaiTrees/tree/1.20.1)
- [1.19.2](https://github.com/davenonymous/BonsaiTrees/tree/1.19.2)
- [1.18.1](https://github.com/davenonymous/BonsaiTrees/tree/1.18.1)
- [1.15.2](https://github.com/davenonymous/BonsaiTrees/tree/1.15.2)
- [1.14.4](https://github.com/davenonymous/BonsaiTrees/tree/1.14.4)
- [1.12.2](https://github.com/davenonymous/BonsaiTrees/tree/master)


## How to get started

1. Craft a Bonsai Pot and place it somewhere nice.
2. Find some soil to use in your pot. Dirt will do for now. Right-click it on the Bonsai Pot.
3. Get a tree sapling and right-click it on the Bonsai Pot. Or open the GUI and insert it into the
   sapling slot.
4. Watch it grow.
5. Open the GUI and insert an axe to automatically harvest the tree when it's ready.

A particular soil might be needed for some of the trees. And the different types of soil may provide
different boosts to the growth of your Bonsai. It is recommended to use JEI to look up compatible
saplings and available soils.

## Changes from Bonsai Trees 3

- The mod has been mostly rewritten from scratch. Old libnonymous code has been integrated into the
  mod itself.
- The mod customization options have been changed. See the [guide](docs/README.md) for more info.
- Upgrades like Bee Hives have been replaced by switching the drops of bonsais to Loot Tables.
  This allows for better tool compatibility checks and more flexibility for drops in general.
- Pots are now enchantable and apply their enchantments to the tool used to harvest the tree.
- The rendering system is now using VBOs to render the trees. This improves performance dramatically.
- A new auto-generation system in a separate mod. This allows for easier integration of new
  trees from other mods. See the [guide](docs/README.md) for more info.
- There is now a small pot variant that grows smaller trees. Cute!
  Convert between the two in a crafting table.
- You can paint the pots with any other solid block. A good one is putting the logs or planks of the
  tree you're growing on the pot.
- And finally and most importantly: Pots can now be worn as a hat.

That's all for now. More documentation will follow once the mod is more stable.
