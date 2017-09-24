package org.dave.bonsaitrees.command;

import net.minecraftforge.fml.common.Loader;
import org.dave.bonsaitrees.base.CommandBaseMenu;

public class CommandBonsaiTrees extends CommandBaseMenu {

    @Override
    public void initEntries() {
        this.addSubcommand(new CommandSaveShape());
        if(Loader.isModLoaded("forestry")) {
            this.addSubcommand(new CommandGenerateMissingForestryShapes());
        }
    }

    @Override
    public String getName() {
        return "bonsaitrees";
    }
}
