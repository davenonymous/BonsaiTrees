package com.davenonymous.bonsaitrees2.gui.widgets;

import com.davenonymous.bonsaitrees2.BonsaiTrees2;
import com.davenonymous.bonsaitrees2.gui.TreeCreatorScreen;
import com.davenonymous.bonsaitrees2.registry.sapling.SaplingDrop;
import com.davenonymous.bonsaitrees2.registry.sapling.SaplingInfo;
import com.davenonymous.bonsaitrees2.util.Logz;
import com.davenonymous.libnonymous.gui.framework.GUI;
import com.davenonymous.libnonymous.gui.framework.event.MouseClickEvent;
import com.davenonymous.libnonymous.gui.framework.event.WidgetEventResult;
import com.davenonymous.libnonymous.gui.framework.widgets.WidgetButton;
import com.davenonymous.libnonymous.gui.framework.widgets.WidgetPanel;
import com.davenonymous.libnonymous.gui.framework.widgets.WidgetTextBox;
import com.davenonymous.libnonymous.render.MultiblockBlockModel;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

import java.io.*;
import java.util.List;

public class SavePanel extends WidgetPanel {
    MultiblockBlockModel model;
    WidgetTextBox statusBox;

    public SavePanel(int width, int height, MultiblockBlockModel model) {
        super();
        this.setSize(width, height);
        this.model = model;

        WidgetButton saveToDiskButton = new WidgetButton("Save to disk");
        saveToDiskButton.addListener(MouseClickEvent.class, (event, widget) -> {
            this.saveToDisk();
            return WidgetEventResult.HANDLED;
        });
        this.add(saveToDiskButton);

        statusBox = new WidgetTextBox("", 0xFF008000);
        statusBox.setDimensions(5, 25, width-10, 30);

        this.add(statusBox);
    }

    private void saveToDisk() {
        GUI gui = this.getGUI();
        String treeIdString = (String)gui.getValue(TreeCreatorScreen.STATE_TREE_ID);
        String treeIdPath = treeIdString.replaceAll(":", "/");
        ResourceLocation treeId = ResourceLocation.tryCreate(treeIdString);
        int baseTicks = (Integer) gui.getValue(TreeCreatorScreen.STATE_BASETICKS);
        String tags = (String) gui.getValue(TreeCreatorScreen.STATE_TAGS);
        ItemStack sapling = (ItemStack) gui.getValue(TreeCreatorScreen.STATE_SAPLING);
        List<SaplingDrop> drops = (List<SaplingDrop>) gui.getValue(TreeCreatorScreen.STATE_DROPS);

        File assetPath = new File(Minecraft.getInstance().gameDir, "bonsai-exports/assets/" + BonsaiTrees2.MODID + "/models/tree/" + treeId.getNamespace());
        assetPath.mkdirs();

        File recipePath = new File(Minecraft.getInstance().gameDir, "bonsai-exports/data/" + BonsaiTrees2.MODID + "/recipes/sapling/" + treeId.getNamespace());
        recipePath.mkdirs();

        File modelPath = new File(assetPath, treeId.getPath() + ".json");
        File recipeFilePath = new File(recipePath, treeId.getPath() + ".json");

        model.id = new ResourceLocation(BonsaiTrees2.MODID, "sapling/" + treeIdPath);

        SaplingInfo saplingInfo = new SaplingInfo(model.id, Ingredient.fromStacks(sapling), baseTicks);
        saplingInfo.sapling = sapling.copy();
        drops.stream().forEach(saplingInfo::addDrop);
        for(String tag :tags.split(",")) {
            saplingInfo.addTag(tag);
        }

        saveStringToFile(modelPath, model.serializePretty());
        saveStringToFile(recipeFilePath, saplingInfo.serializePretty());

        statusBox.setText("Saved files in '<mc>/bonsai-exports/' directory!");
    }

    private void saveStringToFile(File file, String string) {
        try {
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file));
            writer.write(string);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
