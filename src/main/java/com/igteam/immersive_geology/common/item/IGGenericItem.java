package com.igteam.immersive_geology.common.item;

import igteam.immersive_geology.item.IGItemType;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import igteam.immersive_geology.materials.helper.MaterialInterface;
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

    private final Set<MaterialInterface> materialSet = new LinkedHashSet<>();
    private final ItemPattern pattern;

    public IGGenericItem(MaterialInterface m, ItemPattern p) {
        super(new Properties().tab(IGItemGroup.IGGroup));
        this.pattern = p;
        this.materialSet.add(m);
    }

    public @NotNull Component getName(@NotNull ItemStack stack) {
        List<String> materialList = new ArrayList<>();
        for (MaterialInterface m : materialSet) {
            materialList.add(I18n.get("material.immersive_geology." + m.getName()));
        }

        return new TranslatableComponent("item.immersive_geology." + pattern.getName(), materialList.toArray());
    }

    public ItemPattern getPattern(){
        return this.pattern;
    }

    public String getHolderKey(){
        StringBuilder data = new StringBuilder();
        for(MaterialInterface m : materialSet){
            data.append("_").append(m.getName());
        }

        return this.pattern + data.toString();
    }

    public void addMaterial(MaterialInterface material){
        materialSet.add(material);
    }

    @Override
    public int getColourForIGItem(ItemStack stack, int pass) {
        List<MaterialInterface> materialList = new ArrayList<>(materialSet);
        Collections.reverse(materialList);
        return materialList.get(pass).getColor();
    }

    @Override
    public Set<MaterialInterface> getMaterials() {
        return materialSet;
    }


    public void finalizeData(){
        setRegistryName(IGRegistrationHolder.getRegistryKey(this));
    }

    @Override
    public ItemSubGroup getSubGroup() {
        return pattern.getSubGroup();
    }
}
