# BonsaiTrees :deciduous_tree: :palm_tree: :evergreen_tree:

A Minecraft mod adding a block that grows small trees in two block spaces.

## Changes to Bonsai Trees 1:

- Soils, Saplings and Drops are now stored as recipes (Thanks @Darkhax!)
- This and changes in 1.14.* require some changes in the old config jsons,
  which sadly means old trees are not always straight forward to port.
- Reworked the way drops are being specified and calculated
- Bonsai Pots can now get waterlogged, what happens then can be configured
  in the mod options.
- Removed multi-shape rendering of trees for performance reasons.
  Instead they are now being randomly rotated.
- Removed some of the commands that are not needed anymore
- The save tree shape command is now "/bonsai model save" and copies
  the resulting tree model json to your clipboard.
- Split off some of the code into a library mod: libnonymous
  This includes some base things I do with all my blocks, but also for
  example the MultiBlockModel renderer, thats used in both Compact
  Machines and Bonsai Trees.
- Removed Bonsai Advancements (will probably come back in a future update)
- Removed all parts of the never used API. People usually add trees via
  json files. If anyone needs a specific functionality, e.g. some event
  when a bonsai is fully grown or something please don't hesitate to ask
  or create a pull request.
- Moved build pipeline from Travis and Dropbox to be fully contained on
  github. Currently a self-hosted maven is necessary since public
  packages on github still require authentication for some reason.
