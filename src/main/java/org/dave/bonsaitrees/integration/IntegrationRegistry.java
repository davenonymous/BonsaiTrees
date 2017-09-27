package org.dave.bonsaitrees.integration;

import net.minecraftforge.fml.common.discovery.ASMDataTable;
import org.dave.bonsaitrees.BonsaiTrees;
import org.dave.bonsaitrees.api.IBonsaiIntegration;
import org.dave.bonsaitrees.utility.AnnotatedInstanceUtil;
import org.dave.bonsaitrees.utility.Logz;

import java.util.ArrayList;
import java.util.List;

public class IntegrationRegistry {
    private static List<IBonsaiIntegration> integrations = new ArrayList<>();

    public static void loadBonsaiIntegrations(ASMDataTable asmData) {
        integrations.addAll(AnnotatedInstanceUtil.getBonsaiIntegrations(asmData));
    }

    public static void registerBonsaiIntegrations() {
        for(IBonsaiIntegration integration : integrations) {
            Logz.info("Registering trees from integration: %s", integration.getClass().getName());
            integration.registerTrees(BonsaiTrees.instance.typeRegistry);
        }
    }
}
