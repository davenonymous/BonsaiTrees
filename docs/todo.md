# Before release

- Fix custom resource and data pack loading
- Improve datapack loading (not mod-reliant, not always marked as required)
- Fix item visibility in GUI slots
- Finish documentation
- Add The End trees (and those from mods extending The End)
- GitHub Actions and Gradle integration fot auto-generating integrations
- Try/catch soil and camouflage rendering and disable that particular block if it fails
- Add a tick rate game server test, i.e. place n^2 pots with random saplings and check if the server
  can handle it
- Fix some camouflages not rendering at all
- Why are there two pots per fluid in the soil tests?

# Optional

- Add a pack creator tool, i.e. screen with stored state of the new pack
- Check for additionally possible config options
- Re-add ability to only render the item in the pot
- Wall-mounted pots
- Pot-Attachments (like a glass dome, or a chain for hanging pots)

## DONE

- Ability to right-click sapling and soils into the pots
- Validate that the camouflage renderer can handle all valid camouflage blocks (Write a test for
  that)
- Add a model generation command
- Create a separate project for the model generator, many mixins are not needed for gameplay,
  but only for the pack generator
