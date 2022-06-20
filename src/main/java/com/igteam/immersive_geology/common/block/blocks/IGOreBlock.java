package com.igteam.immersive_geology.common.block.blocks;

import com.igteam.immersive_geology.common.block.IGGenericBlock;
import igteam.immersive_geology.materials.data.metal.MaterialBaseMetal;
import igteam.immersive_geology.materials.helper.MaterialInterface;
import igteam.immersive_geology.materials.helper.MaterialTexture;
import igteam.immersive_geology.materials.pattern.BlockPattern;
import igteam.immersive_geology.materials.pattern.ItemPattern;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SixWayBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameter;
import net.minecraft.loot.LootParameters;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class IGOreBlock extends IGGenericBlock {
    public IGOreBlock(MaterialInterface<?> m, BlockPattern p) {
        super(m, p, Properties.create(Material.ROCK, MaterialColor.STONE).hardnessAndResistance(2f).harvestTool(ToolType.PICKAXE).harvestLevel(1));
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        //TODO Crimson - Basic Setup here for block drops
        boolean hasSilk = false;
        List<ItemStack> dropItems = new ArrayList<>();
        int level = builder.get(LootParameters.TOOL).getHarvestLevel(ToolType.PICKAXE, null, state);
        if(level != -1) {
            ItemStack ore_chunk = getMaterial(MaterialTexture.base).getStack(ItemPattern.ore_chunk, getMaterial(MaterialTexture.overlay), 1 + level);
            if (!hasSilk) {
                dropItems.add(ore_chunk);
            } else {
                dropItems.add(new ItemStack(this));
            }
        } else {
            dropItems.add(new ItemStack(this));
        }

        return dropItems;
    }

    @Override
    public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, ItemStack stack) {
        super.harvestBlock(worldIn, player, pos, state, te, stack);
    }
}
