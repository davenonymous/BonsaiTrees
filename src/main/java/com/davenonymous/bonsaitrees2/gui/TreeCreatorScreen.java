package com.davenonymous.bonsaitrees2.gui;

import com.davenonymous.bonsaitrees2.BonsaiTrees2;
import com.davenonymous.bonsaitrees2.gui.widgets.DropsPanel;
import com.davenonymous.bonsaitrees2.gui.widgets.ModelPanel;
import com.davenonymous.bonsaitrees2.gui.widgets.PropertiesPanel;
import com.davenonymous.bonsaitrees2.gui.widgets.SavePanel;
import com.davenonymous.libnonymous.gui.framework.GUI;
import com.davenonymous.libnonymous.gui.framework.WidgetContainerScreen;
import com.davenonymous.libnonymous.gui.framework.WidgetSlot;
import com.davenonymous.libnonymous.gui.framework.widgets.WidgetPanel;
import com.davenonymous.libnonymous.gui.framework.widgets.WidgetTabsPanel;
import com.davenonymous.libnonymous.render.MultiblockBlockModel;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class TreeCreatorScreen extends WidgetContainerScreen<TreeCreatorContainer> {
    public static final ResourceLocation STATE_TREE_ID = new ResourceLocation(BonsaiTrees2.MODID, "id");
    public static final ResourceLocation STATE_SAPLING = new ResourceLocation(BonsaiTrees2.MODID, "stack");
    public static final ResourceLocation STATE_BASETICKS = new ResourceLocation(BonsaiTrees2.MODID, "baseticks");
    public static final ResourceLocation STATE_TAGS = new ResourceLocation(BonsaiTrees2.MODID, "tags");
    public static final ResourceLocation STATE_DROPS = new ResourceLocation(BonsaiTrees2.MODID, "drops");

    public TreeCreatorScreen(TreeCreatorContainer container, PlayerInventory inv, ITextComponent name) {
        super(container, inv, name);
    }
    //TODO Background Layer
    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {

    }

    @Override
    protected GUI createGUI() {
        int width = TreeCreatorContainer.WIDTH;
        int height = TreeCreatorContainer.HEIGHT;

        MultiblockBlockModel model = new MultiblockBlockModel(new ResourceLocation(BonsaiTrees2.MODID, "temporary_tree_model"));
        model.setBlocksByFloodFill(BonsaiTrees2.proxy.getClientWorld(), this.container.getPos());

        GUI gui = new GUI(0, 0, width, height);


        WidgetTabsPanel tabs = new WidgetTabsPanel();
        tabs.setEdge(WidgetTabsPanel.TabDockEdge.WEST);
        tabs.setDimensions(5, 5, width-10, height-10);
        gui.add(tabs);

        ModelPanel modelPanel = new ModelPanel(width, height, model);
        tabs.addPage(modelPanel, new ItemStack(Items.OAK_LOG));

        PropertiesPanel propertiesPanel = new PropertiesPanel(width, height);
        tabs.addPage(propertiesPanel, new ItemStack(Items.REDSTONE));

        DropsPanel dropsPanel = new DropsPanel(width, height);
        dropsPanel.setId(STATE_DROPS);
        tabs.addPage(dropsPanel, new ItemStack(Blocks.DROPPER));

        SavePanel savePanel = new SavePanel(width, height, model);
        tabs.addPage(savePanel, new ItemStack(Items.MUSIC_DISC_CHIRP));

        WidgetPanel buttonPanel = tabs.getButtonsPanel();
        buttonPanel.setPosition(-28, 0);
        buttonPanel.setSize(28, 120);
        gui.add(buttonPanel);


        // Bind all slots to the matching widgets
        for(Slot slot : this.container.inventorySlots) {
            if(!(slot instanceof WidgetSlot)) {
                continue;
            }

            WidgetSlot widgetSlot = (WidgetSlot)slot;
            if(widgetSlot.matches(TreeCreatorContainer.SLOTGROUP_PLAYER)) {
                widgetSlot.bindToWidget(propertiesPanel);
                widgetSlot.bindToWidget(dropsPanel);
            } else if(widgetSlot.matches(TreeCreatorContainer.SLOTGROUP_SETUP)) {
                widgetSlot.bindToWidget(propertiesPanel);
            }
        }

        gui.findValueWidgets();

        return gui;
    }
}
