package com.igteam.immersive_geology.common.item;

import com.igteam.immersive_geology.client.menu.helper.IGItemGroup;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import igteam.immersive_geology.block.IGBlockType;
import igteam.immersive_geology.item.IGItemType;
import igteam.immersive_geology.materials.helper.MaterialInterface;
import igteam.immersive_geology.materials.helper.MaterialTexture;
import igteam.immersive_geology.materials.pattern.ItemPattern;
import igteam.immersive_geology.menu.ItemSubGroup;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.*;

public class IGGenericBlockItem extends BlockItem implements IGItemType {

    private final Map<MaterialTexture, MaterialInterface> materialMap = new HashMap<>();

    private boolean useCustomDisplayName;
    private final ItemPattern pattern;
    private final IGBlockType type;

    public IGGenericBlockItem(IGBlockType b, MaterialInterface<?> m, ItemPattern p) {
        super(b.getBlock(), new Properties().group(IGItemGroup.IGGroup));
        this.pattern = p;
        this.materialMap.put(MaterialTexture.base, m);
        this.useCustomDisplayName = true;
        this.type = b;
    }

    public IGGenericBlockItem useDefaultNamingConvention(){
        useCustomDisplayName = false;
        return this;
    }

    public Block getBlock() {
        return super.getBlock();
    }

    public IGBlockType getIGBlockType(){
        return type;
    }

    public ItemPattern getPattern(){
        return this.pattern;
    }

    public String getHolderKey(){
        StringBuilder data = new StringBuilder();

        for(MaterialTexture t : MaterialTexture.values()){
            if (materialMap.containsKey(t)) {
                data.append("_").append(materialMap.get(t).getName());
            }
        }

        return this.pattern + data.toString();
    }

    public void addMaterial(MaterialTexture t, MaterialInterface material){
        materialMap.put(t, material);
    }

    @Override
    public int getColourForIGItem(ItemStack stack, int pass) {
        List<MaterialInterface> materialList = new ArrayList<>();
        for(MaterialTexture t : MaterialTexture.values()) {
            if (materialMap.containsKey(t)) {
                materialList.add(materialMap.get(t));
            }
        }

        return materialList.get(pass).getColor(pattern);
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        if(!useCustomDisplayName) return super.getDisplayName(stack);

        List<String> materialList = new ArrayList<>();
        for(MaterialTexture t : MaterialTexture.values()){
            if (materialMap.containsKey(t)) {
                materialList.add(I18n.format("material.immersive_geology." + materialMap.get(t).getName()));
            }
        }

        return new TranslationTextComponent("block.immersive_geology." + pattern.getName(), materialList.toArray());
    }

    @Override
    public Collection<MaterialInterface> getMaterials() {
        return materialMap.values();
    }

    public void finalizeData(){
        setRegistryName(IGRegistrationHolder.getRegistryKey(this));
    }

    @Override
    public ItemSubGroup getSubGroup() {
        return pattern.getSubGroup();
    }

    @Override
    public boolean hasCustomItemColours() {
        return true;
    }
}
