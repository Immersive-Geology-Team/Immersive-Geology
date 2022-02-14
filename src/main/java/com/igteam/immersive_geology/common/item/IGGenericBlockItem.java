package com.igteam.immersive_geology.common.item;

import com.igteam.immersive_geology.common.block.IGGenericBlock;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import igteam.immersive_geology.item.IGItemType;
import igteam.immersive_geology.materials.helper.MaterialInterface;
import igteam.immersive_geology.materials.helper.MaterialTexture;
import igteam.immersive_geology.materials.pattern.ItemPattern;
import igteam.immersive_geology.menu.ItemSubGroup;
import igteam.immersive_geology.menu.helper.IGItemGroup;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.*;

public class IGGenericBlockItem extends BlockItem implements IGItemType {

    private final Map<MaterialTexture, MaterialInterface> materialMap = new HashMap<>();

    private final ItemPattern pattern;

    public IGGenericBlockItem(IGGenericBlock b, MaterialInterface m, ItemPattern p) {
        super(b, new Properties().group(IGItemGroup.IGGroup));
        this.pattern = p;
        this.materialMap.put(MaterialTexture.base, m);
    }

    public IGGenericBlock getBlock() {
        return (IGGenericBlock) super.getBlock();
    }

    public ITextComponent getDisplayName(ItemStack stack) {
        List<String> materialList = new ArrayList<>();
        for(MaterialTexture t : MaterialTexture.values()){
            if (materialMap.containsKey(t)) {
                materialList.add(I18n.format("material.immersive_geology." + materialMap.get(t).getName()));
            }
        }

        return new TranslationTextComponent("item.immersive_geology." + pattern.getName(), materialList.toArray());
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
}
