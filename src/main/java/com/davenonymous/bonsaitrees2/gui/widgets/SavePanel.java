package com.davenonymous.bonsaitrees2.gui.widgets;

import com.davenonymous.bonsaitrees2.BonsaiTrees2;
import com.davenonymous.bonsaitrees2.gui.TreeCreatorScreen;
import com.davenonymous.bonsaitrees2.registry.sapling.SaplingDrop;
import com.davenonymous.bonsaitrees2.registry.sapling.SaplingInfo;
import com.davenonymous.libnonymous.gui.framework.GUI;
import com.davenonymous.libnonymous.gui.framework.event.MouseClickEvent;
import com.davenonymous.libnonymous.gui.framework.event.WidgetEventResult;
import com.davenonymous.libnonymous.gui.framework.util.FontAwesomeIcons;
import com.davenonymous.libnonymous.gui.framework.widgets.*;
import com.davenonymous.libnonymous.render.MultiblockBlockModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;

import java.io.*;
import java.util.List;

public class SavePanel extends WidgetPanel {
    MultiblockBlockModel model;
    WidgetTextBox statusBox;
    WidgetCheckbox overwriteFiles;

    public SavePanel(int width, int height, MultiblockBlockModel model) {
        super();
        this.setSize(width, height);
        this.model = model;

        WidgetButton saveToDiskButton = new WidgetButton(I18n.format("bonsaitrees.gui.tree_creator.button.save_to_disk"));
        saveToDiskButton.addListener(MouseClickEvent.class, (event, widget) -> {
            this.saveToDisk();
            return WidgetEventResult.HANDLED;
        });
        this.add(saveToDiskButton);

        statusBox = new WidgetTextBox("", 0xFF008000);
        statusBox.setDimensions(5, 45, width-10, 30);
        this.add(statusBox);

        overwriteFiles = new WidgetCheckbox();
        overwriteFiles.setValue(false);
        overwriteFiles.setPosition(0, 24);
        this.add(overwriteFiles);

        WidgetTextBox overwriteFilesLabel = new WidgetTextBox(I18n.format("bonsaitrees.gui.tree_creator.checkbox.overwrite_files"), 0xC0000000);
        overwriteFilesLabel.setPosition(12, 25);
        this.add(overwriteFilesLabel);

        WidgetTextBox openloaderHint = new WidgetTextBox(I18n.format("bonsaitrees.gui.tree_creator.hint.openloader"), 0x90000000);
        openloaderHint.setDimensions(0, 80, width-10, height-85-34);
        this.add(openloaderHint);

        final String openLoaderUrl = "https://www.curseforge.com/minecraft/mc-mods/open-loader";
        WidgetButton openOpenLoaderPage = new WidgetButton(I18n.format("bonsaitrees.gui.tree_creator.button.go_to_openloader"));
        openOpenLoaderPage.setTooltipLines(new StringTextComponent(openLoaderUrl));
        openOpenLoaderPage.setDimensions(width-10-120, height-34, 120, 24);
        openOpenLoaderPage.addListener(MouseClickEvent.class, (event, widget) -> {
            Util.getOSType().openURI(openLoaderUrl);
            return WidgetEventResult.HANDLED;
        });
        this.add(openOpenLoaderPage);

        WidgetFontAwesome externalIcon = new WidgetFontAwesome(FontAwesomeIcons.SOLID_ExternalLinkAlt, WidgetFontAwesome.IconSize.MEDIUM);
        externalIcon.setPosition(width-10-120+4, height-34+3);
        this.add(externalIcon);
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

        if(treeIdString.length() == 0) {
            statusBox.setText(I18n.format("bonsaitrees.gui.tree_creator.warning.missing_tree_id"));
            return;
        }

        if(model.blocks.size() >= 4095) {
            statusBox.setText(I18n.format("bonsaitrees.gui.tree_creator.warning.too_many_blocks"));
            return;
        }

        if(model.refMap.size() > 26) {
            statusBox.setText(I18n.format("bonsaitrees.gui.tree_creator.warning.too_many_types"));
            return;
        }

        File assetPath = new File(Minecraft.getInstance().gameDir, "openloader/resources/custombonsais/assets/" + BonsaiTrees2.MODID + "/models/tree/" + treeId.getNamespace());
        assetPath.mkdirs();

        File recipePath = new File(Minecraft.getInstance().gameDir, "openloader/data/custombonsais/data/" + BonsaiTrees2.MODID + "/recipes/sapling/" + treeId.getNamespace());
        recipePath.mkdirs();

        File datapackMetaFile = new File(Minecraft.getInstance().gameDir, "openloader/data/custombonsais/pack.mcmeta");
        File resourcepackMetaFile = new File(Minecraft.getInstance().gameDir, "openloader/resources/custombonsais/pack.mcmeta");

        final String metaData = "{\"pack\":{\"description\":\"Additional Bonsai Trees\",\"pack_format\": 5}}";
        if(!datapackMetaFile.exists()) {
            saveStringToFile(datapackMetaFile, metaData);
        }
        if(!resourcepackMetaFile.exists()) {
            saveStringToFile(resourcepackMetaFile, metaData);
        }

        File modelPath = new File(assetPath, treeId.getPath() + ".json");
        File recipeFilePath = new File(recipePath, treeId.getPath() + ".json");

        if((recipeFilePath.exists() || modelPath.exists()) && !overwriteFiles.getValue()) {
            statusBox.setText(I18n.format("bonsaitrees.gui.tree_creator.warning.files_already_exists"));
            return;
        }

        model.id = new ResourceLocation(BonsaiTrees2.MODID, "sapling/" + treeIdPath);

        SaplingInfo saplingInfo = new SaplingInfo(model.id, Ingredient.fromStacks(sapling), baseTicks);
        saplingInfo.sapling = sapling.copy();
        drops.stream().forEach(saplingInfo::addDrop);
        for(String tag :tags.split(",")) {
            saplingInfo.addTag(tag);
        }

        saveStringToFile(modelPath, model.serializePretty());
        saveStringToFile(recipeFilePath, saplingInfo.serializePretty());

        statusBox.setText(I18n.format("bonsaitrees.gui.tree_creator.label.saved_files"));
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
