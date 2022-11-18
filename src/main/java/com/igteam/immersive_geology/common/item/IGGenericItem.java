package com.igteam.immersive_geology.common.item;

import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import igteam.api.item.IGItemType;
import igteam.api.materials.helper.MaterialInterface;
import igteam.api.materials.helper.MaterialTexture;
import igteam.api.materials.pattern.ItemFamily;
import igteam.immersive_geology.item.IGItemType;
import igteam.immersive_geology.materials.helper.MaterialInterface;
import igteam.immersive_geology.materials.helper.MaterialTexture;
import igteam.immersive_geology.materials.pattern.ItemPattern;
import igteam.immersive_geology.menu.ItemSubGroup;
import igteam.immersive_geology.menu.helper.IGItemGroup;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class IGGenericItem extends Item implements IGItemType {

    private final Map<MaterialTexture, MaterialInterface> materialMap = new HashMap<>();

    private final ItemFamily pattern;

    public IGGenericItem(MaterialInterface m, ItemFamily p) {
        super(new Properties().tab(IGItemGroup.IGGroup));
        this.pattern = p;
        this.materialMap.put(MaterialTexture.base, m);
    }

    public @NotNull Component getName(@NotNull ItemStack stack) {
        List<String> materialList = new ArrayList<>();
        for(MaterialTexture t : MaterialTexture.values()){
            if (materialMap.containsKey(t)) {
                materialList.add(I18n.get("material.immersive_geology." + materialMap.get(t).getName()));
            }
        }

        switch(pattern){
            case ore_chunk -> Collections.reverse(materialList); //Doing this for Display Purpose
            default -> {

            }
        }

        return new TranslatableComponent("item.immersive_geology." + pattern.getName(), materialList.toArray());
    }

    public ItemPattern getPattern(){
        return this.pattern;
    }

    public String getHolderKey(){
        StringBuilder data = new StringBuilder();
        for(MaterialTexture t : MaterialTexture.values()) {
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

    public MaterialInterface getMaterial(MaterialTexture t){
        return materialMap.get(t);
    }

    public void finalizeData(){
        setRegistryName(IGRegistrationHolder.getRegistryKey(this));
    }

    @Override
    public ItemSubGroup getSubGroup() {
        return pattern.getSubGroup();
    }
}
