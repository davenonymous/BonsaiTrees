package org.dave.bonsaitrees.command;

import org.dave.bonsaitrees.base.CommandBaseMenu;

public class CommandBonsaiTrees extends CommandBaseMenu {

    @Override
    public void initEntries() {
        this.addSubcommand(new CommandListTrees());
        this.addSubcommand(new CommandListIntegrations());
        this.addSubcommand(new CommandSaveShape());
        this.addSubcommand(new CommandGenerateMissingShapes());
        this.addSubcommand(new CommandUpgradeJsonFiles());
        this.addSubcommand(new CommandReloadTrees());
    }

    @Override
    public String getName() {
        return "bonsaitrees";
    }
}
