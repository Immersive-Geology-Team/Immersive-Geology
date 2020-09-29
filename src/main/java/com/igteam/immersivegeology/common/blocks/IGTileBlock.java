package com.igteam.immersivegeology.common.blocks;


import com.igteam.immersivegeology.ImmersiveGeology;
import com.igteam.immersivegeology.api.materials.Material;
import com.igteam.immersivegeology.api.materials.MaterialUseType;
import com.igteam.immersivegeology.api.materials.MaterialUtils;
import com.igteam.immersivegeology.client.menu.helper.ItemSubGroup;
import com.igteam.immersivegeology.common.tileentity.IGToolForge;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CampfireCookingRecipe;
import net.minecraft.state.IProperty;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.CampfireTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Optional;

public class IGTileBlock extends IGBaseBlock {

    public IGTileBlock(String sub, MaterialUseType subtype, Material... materials)
    {
        super(sub,
                Properties.create((subtype.getMaterial()==null?net.minecraft.block.material.Material.ROCK: subtype.getMaterial())), null, subtype.getSubGroup());
        this.itemBlock = new IGBlockItem(this, new Item.Properties().group(ImmersiveGeology.IG_ITEM_GROUP), ItemSubGroup.machines);
        this.itemBlock.setRegistryName(this.name);
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new IGToolForge();
    }

    public boolean onBlockActivated(BlockState p_220051_1_, World world, BlockPos p_220051_3_, PlayerEntity p_220051_4_, Hand p_220051_5_, BlockRayTraceResult p_220051_6_) {
        TileEntity lvt_7_1_ = world.getTileEntity(p_220051_3_);
        if (lvt_7_1_ instanceof IGToolForge) {
            IGToolForge forge = (IGToolForge)lvt_7_1_;
            ItemStack item = p_220051_4_.getHeldItem(p_220051_5_);
            if (!world.isRemote) {
                forge.addItem(p_220051_4_.abilities.isCreativeMode ? item.copy() : item);
            }

            return true;
        }

        return false;
    }
}
