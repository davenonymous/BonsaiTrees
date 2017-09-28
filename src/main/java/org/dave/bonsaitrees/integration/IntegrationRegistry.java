package org.dave.bonsaitrees.integration;

import net.minecraftforge.fml.common.discovery.ASMDataTable;
import org.dave.bonsaitrees.BonsaiTrees;
import org.dave.bonsaitrees.api.IBonsaiIntegration;
import org.dave.bonsaitrees.misc.ConfigurationHandler;
import org.dave.bonsaitrees.utility.AnnotatedInstanceUtil;
import org.dave.bonsaitrees.utility.Logz;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IntegrationRegistry {
    private static List<IBonsaiIntegration> integrations = new ArrayList<>();

    public static void loadBonsaiIntegrations(ASMDataTable asmData) {
        List<String> disabledIntegrations = Arrays.asList(ConfigurationHandler.IntegrationSettings.disabledIntegrations);

        for(IBonsaiIntegration integration : AnnotatedInstanceUtil.getBonsaiIntegrations(asmData)) {
            if(disabledIntegrations.contains(integration.getClass().getName())) {
                Logz.info("Not loading '%s' integration, disabled in config", integration);
                continue;
            }

            integrations.add(integration);
        }
    }

    public static void registerBonsaiIntegrations() {
        for(IBonsaiIntegration integration : integrations) {
            Logz.info("Registering trees from integration: %s", integration.getClass().getName());
            integration.registerTrees(BonsaiTrees.instance.typeRegistry);
        }
    }
}
