package org.dave.bonsaitrees.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import org.dave.bonsaitrees.BonsaiTrees;
import org.dave.bonsaitrees.api.IBonsaiIntegration;
import org.dave.bonsaitrees.api.IBonsaiSoil;
import org.dave.bonsaitrees.api.IBonsaiTreeType;
import org.dave.bonsaitrees.base.CommandBaseExt;
import org.dave.bonsaitrees.integration.IntegrationRegistry;
import org.dave.bonsaitrees.utility.Logz;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
        Map<IBonsaiIntegration, Integer> countTypes = new HashMap<>();
        Map<IBonsaiIntegration, Integer> countSoils = new HashMap<>();

        for(IBonsaiIntegration integration : IntegrationRegistry.getIntegrations()) {
            countTypes.put(integration, 0);
            countSoils.put(integration, 0);
        }

        // Count tree types
        for(IBonsaiTreeType type : BonsaiTrees.instance.typeRegistry.getAllTypes()) {
            IBonsaiIntegration integration = BonsaiTrees.instance.typeRegistry.getIntegrationForType(type);

            if(!countTypes.containsKey(integration)) {
                Logz.warn("Registered tree for unregistered integration detected. Something is off!");
                countTypes.put(integration, 0);
            }

            countTypes.put(integration, countTypes.get(integration)+1);
        }

        // Count soil types
        for(IBonsaiSoil soil : BonsaiTrees.instance.soilRegistry.getAllSoils()) {
            IBonsaiIntegration integration = BonsaiTrees.instance.soilRegistry.getIntegrationForSoil(soil);
            if(!countSoils.containsKey(integration)) {
                Logz.warn("Registered soil for unregistered integration detected. Something is off!");
                countSoils.put(integration, 0);
            }

            countSoils.put(integration, countSoils.get(integration)+1);
        }

        Set<IBonsaiIntegration> integrationsToList = new HashSet<>();
        integrationsToList.addAll(countSoils.keySet());
        integrationsToList.addAll(countTypes.keySet());

        int totalTrees = 0;
        int totalSoils = 0;
        sender.sendMessage(new TextComponentTranslation("commands.bonsaitrees.listIntegrations.result_title"));
        for(IBonsaiIntegration integration : integrationsToList) {
            int trees = countTypes.containsKey(integration) ? countTypes.get(integration) : 0;
            int soils = countSoils.containsKey(integration) ? countSoils.get(integration) : 0;

            sender.sendMessage(new TextComponentTranslation("commands.bonsaitrees.listIntegrations.result_entry", integration.getClass().getSimpleName(), trees, soils));
            totalTrees += trees;
            totalSoils += soils;
        }

        sender.sendMessage(new TextComponentTranslation("commands.bonsaitrees.listIntegrations.result_entry_total", totalTrees, totalSoils, totalSoils + totalTrees));
    }
}
