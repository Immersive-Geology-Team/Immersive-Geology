package com.igteam.immersive_geology.api.materials.material_data.bricks;

import com.igteam.immersive_geology.api.materials.MaterialEnum;
import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.api.materials.helper.PeriodicTableElement;
import com.igteam.immersive_geology.api.materials.helper.processing.IGMaterialProcess;
import com.igteam.immersive_geology.api.materials.helper.processing.methods.IGCraftingProcessingMethod;
import com.igteam.immersive_geology.api.materials.material_bases.MaterialBrickBase;
import com.igteam.immersive_geology.api.tags.IGTags;
import com.igteam.immersive_geology.core.registration.IGMultiblockRegistrationHolder;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.LinkedHashSet;

public class MaterialBrickRefractory extends MaterialBrickBase
{
    @Override
    public String getName() { return "refractory"; }

    @Override
    public LinkedHashSet<PeriodicTableElement.ElementProportion> getElements()
    {
        return new LinkedHashSet<>(Arrays.asList(
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.ALUMINIUM, 6),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.SILICON, 2),
                new PeriodicTableElement.ElementProportion(PeriodicTableElement.OXYGEN, 13)
        ));
    }

    @Nonnull
    @Override
    public Rarity getRarity() {
        return Rarity.COMMON;
    }

    @Override
    public int getBoilingPoint() {
        return 3250;
    }

    @Override
    public int getMeltingPoint() {
        return 2327;
    }

    public static int baseColor = 0xE5D2AC;

    @Override
    public int getColor(int temperature) {
        return baseColor;
    }

    @Override
    public float getHardness() {
        return 1.0f;
    }

    @Override
    public float getMiningResistance() {
        return 1.0f;
    }

    @Override
    public float getBlastResistance() {
        return 2.5f;
    }

    @Override
    public float getDensity() {
        return 1.3f;
    }


    @Override
    public IGMaterialProcess getProcessingMethod() {
        IGCraftingProcessingMethod bloomery = new IGCraftingProcessingMethod("create_bloomery", IGTags.getTagsFor(this).brick);
        bloomery.setOutput(new ItemStack(IGMultiblockRegistrationHolder.Multiblock.bloomery));
        bloomery.setPattern("aaa","a a","aaa");
        bloomery.setItemToKey('a', IGRegistrationHolder.getBlockByMaterial(MaterialUseType.BRICKS, MaterialEnum.Refractory.getMaterial()).asItem());

        IGCraftingProcessingMethod reinforced_bricks = new IGCraftingProcessingMethod("create_reinforced_bricks", IGTags.getTagsFor(this).brick);
        reinforced_bricks.setOutput(new ItemStack(IGRegistrationHolder.Blocks.reinforcedRefractoryBrick, 4));
        reinforced_bricks.setPattern("rbr","bpb","rbr");
        reinforced_bricks.setItemToKey('r', IGRegistrationHolder.getItemByMaterial(MaterialEnum.Bronze.getMaterial(), MaterialUseType.ROD));
        reinforced_bricks.setItemToKey('b', IGRegistrationHolder.getBlockByMaterial(MaterialUseType.BRICKS, MaterialEnum.Refractory.getMaterial()).asItem());
        reinforced_bricks.setItemToKey('p', IGRegistrationHolder.getItemByMaterial(MaterialEnum.Bronze.getMaterial(), MaterialUseType.PLATE));

        inheritedProcessingMethods.add(bloomery);
        inheritedProcessingMethods.add(reinforced_bricks);

        return super.getProcessingMethod();
    }
}
