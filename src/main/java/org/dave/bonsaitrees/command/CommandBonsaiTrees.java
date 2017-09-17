package org.dave.bonsaitrees.command;

import org.dave.bonsaitrees.base.CommandBaseMenu;

public class CommandBonsaiTrees extends CommandBaseMenu {

    @Override
    public void initEntries() {
        this.addSubcommand(new CommandSaveShape());
    }

    @Override
    public String getName() {
        return "bonsaitrees";
    }
}
