package org.dave.bonsaitrees.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import org.dave.bonsaitrees.BonsaiTrees;
import org.dave.bonsaitrees.api.IBonsaiIntegration;
import org.dave.bonsaitrees.api.IBonsaiTreeType;
import org.dave.bonsaitrees.base.CommandBaseExt;
import org.dave.bonsaitrees.integration.IntegrationRegistry;
import org.dave.bonsaitrees.utility.Logz;

import java.util.HashMap;
import java.util.Map;

public class CommandListIntegrations extends CommandBaseExt {
    @Override
    public String getName() {
        return "listIntegrations";
    }

    @Override
    public boolean isAllowed(EntityPlayer player, boolean creative, boolean isOp) {
        return creative || isOp;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        Map<IBonsaiIntegration, Integer> countByInt = new HashMap<>();

        for(IBonsaiIntegration integration : IntegrationRegistry.getIntegrations()) {
            countByInt.put(integration, 0);
        }

        for(IBonsaiTreeType type : BonsaiTrees.instance.typeRegistry.getAllTypes()) {
            IBonsaiIntegration integration = BonsaiTrees.instance.typeRegistry.getIntegrationForType(type);

            if(!countByInt.containsKey(integration)) {
                Logz.warn("Registered tree for unregistered integration detected. Something is off!");
                countByInt.put(integration, 0);
            }

            countByInt.put(integration, countByInt.get(integration)+1);
        }

        sender.sendMessage(new TextComponentTranslation("commands.bonsaitrees.listIntegrations.result_title"));
        for(Map.Entry<IBonsaiIntegration, Integer> entry : countByInt.entrySet()) {
            IBonsaiIntegration integration = entry.getKey();
            int count = entry.getValue();

            sender.sendMessage(new TextComponentTranslation("commands.bonsaitrees.listIntegrations.result_entry", integration.getClass().getSimpleName(), count));
        }

        sender.sendMessage(new TextComponentTranslation("commands.bonsaitrees.listIntegrations.result_entry_total", countByInt.values().stream().reduce((integer, integer2) -> integer + integer2).orElse(0)));
    }
}
