package org.dave.bonsaitrees.compat.CraftTweaker2.registries;

import org.dave.bonsaitrees.api.IBonsaiSoil;
import org.dave.bonsaitrees.api.IBonsaiTreeType;

import java.util.*;

public class TagModificationsRegistry {
    public static Map<String, List<String>> soilAdditions = new HashMap<>();
    public static Map<String, List<String>> soilRemovals = new HashMap<>();
    public static Map<String, List<String>> treeAdditions = new HashMap<>();
    public static Map<String, List<String>> treeRemovals = new HashMap<>();

    public static void addCompatibleTagToTree(String treeName, String tag) {
        if(!treeAdditions.containsKey(treeName)) {
            treeAdditions.put(treeName, new ArrayList<>());
        }

        treeAdditions.get(treeName).add(tag);
    }

    public static void removeCompatibleTagFromTree(String treeName, String tag) {
        if(!treeRemovals.containsKey(treeName)) {
            treeRemovals.put(treeName, new LinkedList<>());
        }

        treeRemovals.get(treeName).add(tag);
    }

    public static void addProvidedTagToSoil(String soilName, String tag) {
        if(!soilAdditions.containsKey(soilName)) {
            soilAdditions.put(soilName, new ArrayList<>());
        }

        soilAdditions.get(soilName).add(tag);
    }

    public static void removeProvidedTagFromSoil(String soilName, String tag) {
        if(!soilRemovals.containsKey(soilName)) {
            soilRemovals.put(soilName, new LinkedList<>());
        }

        soilRemovals.get(soilName).add(tag);
    }

    public static Set<String> getModifiedTagList(IBonsaiTreeType tree) {
        Set<String> result = new HashSet<>(tree.getCompatibleSoilTags());
        if(treeAdditions.containsKey(tree.getName())) {
            result.addAll(treeAdditions.get(tree.getName()));
        }

        if(treeRemovals.containsKey(tree.getName())) {
            result.removeAll(treeRemovals.get(tree.getName()));
        }

        return result;
    }

    public static Set<String> getModifiedTagList(IBonsaiSoil soil) {
        Set<String> result = new HashSet<>(soil.getProvidedTags());
        if(soilAdditions.containsKey(soil.getName())) {
            result.addAll(soilAdditions.get(soil.getName()));
        }

        if(soilRemovals.containsKey(soil.getName())) {
            result.removeAll(soilRemovals.get(soil.getName()));
        }

        return result;
    }
}
