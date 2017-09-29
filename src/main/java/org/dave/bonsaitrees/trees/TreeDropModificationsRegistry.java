package org.dave.bonsaitrees.trees;

import net.minecraft.item.ItemStack;
import org.dave.bonsaitrees.api.IBonsaiTreeType;
import org.dave.bonsaitrees.api.TreeTypeDrop;

import java.util.*;

public class TreeDropModificationsRegistry {
    public static Map<String, List<TreeTypeDrop>> additions = new HashMap<>();
    public static Map<String, List<ItemStack>> removals = new HashMap<>();

    public static void add(String treeType, ItemStack stack, float chance) {
        if(!additions.containsKey(treeType)) {
            additions.put(treeType, new ArrayList<>());
        }

        additions.get(treeType).add(new TreeTypeDrop(stack, chance));
    }

    public static void remove(String treeType, ItemStack stack) {
        if(!removals.containsKey(treeType)) {
            removals.put(treeType, new LinkedList<>());
        }

        removals.get(treeType).add(stack);
    }

    public static List<TreeTypeDrop> getModifiedDropList(IBonsaiTreeType type) {
        List<TreeTypeDrop> result = new LinkedList<>(type.getDrops());
        if(removals.containsKey(type.getName())) {
            for(ItemStack toRemove : removals.get(type.getName())) {
                result.removeIf(drop -> drop.stack.isItemEqual(toRemove));
            }
        }

        if(additions.containsKey(type.getName())) {
            result.addAll(TreeDropModificationsRegistry.additions.get(type.getName()));
        }

        return result;
    }


}
