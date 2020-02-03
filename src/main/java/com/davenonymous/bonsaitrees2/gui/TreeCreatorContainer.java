package com.davenonymous.bonsaitrees2.gui;

import com.davenonymous.bonsaitrees2.BonsaiTrees2;
import com.davenonymous.bonsaitrees2.setup.Registration;
import com.davenonymous.libnonymous.gui.framework.WidgetContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class TreeCreatorContainer extends WidgetContainer {
    public static int WIDTH = 176;
    public static int HEIGHT = 210;

    public static ResourceLocation SLOTGROUP_SETUP = new ResourceLocation(BonsaiTrees2.MODID, "setup_slots");

    private BlockPos pos;

    public TreeCreatorContainer(int id, PlayerInventory inv, BlockPos pos) {
        super(Registration.TREE_CREATOR_CONTAINER.get(), id, inv);

        this.pos = pos;

        this.layoutPlayerInventorySlots(8, HEIGHT-102);
    }

    public BlockPos getPos() {
        return pos;
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return true;
    }

}
