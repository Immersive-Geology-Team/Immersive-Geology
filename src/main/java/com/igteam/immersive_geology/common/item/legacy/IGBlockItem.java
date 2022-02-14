package com.igteam.immersive_geology.common.item.legacy;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.common.items.IEItemInterfaces;
import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.legacy_api.materials.Material;
import com.igteam.immersive_geology.legacy_api.materials.MaterialUseType;
import com.igteam.immersive_geology.client.menu.helper.IGSubGroup;
import com.igteam.immersive_geology.client.menu.helper.ItemSubGroup;
import com.igteam.immersive_geology.common.block.legacy.IGOreBlock;
import com.igteam.immersive_geology.common.block.blocks.BloomeryBlock;
import com.igteam.immersive_geology.common.block.helpers.BlockMaterialType;
import com.igteam.immersive_geology.common.block.helpers.IGBlockType;
import com.igteam.immersive_geology.core.lib.IGLib;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Locale;

public class IGBlockItem extends BlockItem implements IGSubGroup, IEItemInterfaces.IColouredItem {

    private final ItemSubGroup subGroup;
    private final String holder_name;
    private final Material itemMaterial;
    private final MaterialUseType useType;

    public IGBlockItem(Block blockIn, IGBlockType blockType, ItemSubGroup subGroup, Material material){
        super(blockIn, new Item.Properties().group(ImmersiveGeology.IGGroup).rarity(material.getRarity()));
        this.subGroup = subGroup;
        this.holder_name = blockType.getHolderName();
        this.itemMaterial = material;
        this.useType = blockType.getBlockUseType();
    }

    public IGBlockItem useDefaultNamingConvention(){
        useCustomDisplayName = false;
        return this;
    }

    public String getHolderName() {
        return holder_name;
    }

    @Override
    public ItemSubGroup getSubGroup(){
        return this.subGroup;
    }

    @Override
    public boolean hasCustomItemColours()
    {
        return true;
    }

    @Override
    public int getColourForIEItem(ItemStack stack, int pass)
    {
        Material[] materials = new Material[2];
        if((getBlock() instanceof IGOreBlock)) {
            IGOreBlock oreBlock = (IGOreBlock) getBlock();
            materials[0] = oreBlock.getMaterial(BlockMaterialType.ORE_MATERIAL);
            materials[1] = itemMaterial;

            return materials[MathHelper.clamp(pass,0,materials.length-1)].getColor(1);
        } else {

            return itemMaterial.getColor(0);
        }
    }

    public boolean useCustomDisplayName = true;

    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        if(!useCustomDisplayName) return super.getDisplayName(stack);

        ArrayList<String> localizedNames = new ArrayList<>();

        if(getBlock() instanceof IGOreBlock) {
            IGOreBlock oreBlock = (IGOreBlock) getBlock();
            localizedNames.add(I18n.format("material." + IGLib.MODID + "." + oreBlock.getMaterial(BlockMaterialType.ORE_MATERIAL).getName()));
        }
        if(useType == MaterialUseType.MACHINE) {
            localizedNames.add(I18n.format("machine." + IGLib.MODID + "." + this.getHolderName()));
        } else if(useType == MaterialUseType.STATIC_BLOCK) {
            localizedNames.add(I18n.format("static." + IGLib.MODID + "." + this.getHolderName()));
        } else {
            localizedNames.add(I18n.format("material." + IGLib.MODID + "." + itemMaterial.getName()));
        }
        String base_name = "block."+IGLib.MODID+"."+useType.getName().toLowerCase(Locale.ENGLISH);
        return new TranslationTextComponent(base_name, (Object[]) localizedNames.toArray(new String[localizedNames.size()]));
    }

    public MaterialUseType getUseType(){
        return useType;
    }

    @Override
    protected boolean placeBlock(BlockItemUseContext context, BlockState state) {
        Block b = state.getBlock();
        if(b instanceof BloomeryBlock){
            BloomeryBlock bloomeryBlock = (BloomeryBlock) b;
            if(!bloomeryBlock.canIEBlockBePlaced(state, context))
                return false;
            boolean ret = super.placeBlock(context, state);
            if(ret) bloomeryBlock.onIEBlockPlacedBy(context, state);
            return ret;
        }
        return super.placeBlock(context, state);
    }

    @Override
    protected boolean onBlockPlaced(BlockPos pos, World worldIn, @Nullable PlayerEntity player, ItemStack stack, BlockState state) {
        if(state.hasProperty(IEProperties.MULTIBLOCKSLAVE))
            return false;
        return super.onBlockPlaced(pos, worldIn, player, stack, state);
    }
}
