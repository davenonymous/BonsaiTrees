package org.dave.bonsaitrees.command;

import org.dave.bonsaitrees.base.CommandBaseMenu;

public class CommandBonsaiTrees extends CommandBaseMenu {

    @Override
    public void initEntries() {
        this.addSubcommand(new CommandListTrees());
        this.addSubcommand(new CommandSaveShape());
        this.addSubcommand(new CommandGenerateMissingShapes());
    }

    @Override
    public String getName() {
        return "bonsaitrees";
    }
}
