package igteam.immersive_geology.common.block.blocks;

import igteam.api.materials.helper.MaterialInterface;
import igteam.api.materials.pattern.BlockPattern;
import igteam.immersive_geology.common.block.IGGenericBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;

public class IGOreBlock extends IGGenericBlock {
    public IGOreBlock(MaterialInterface<?> m, BlockPattern p) {
        super(m, p, Properties.create(Material.ROCK, MaterialColor.STONE).hardnessAndResistance(2f).harvestTool(ToolType.PICKAXE).harvestLevel(1));
    }


    @Override
    public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, ItemStack stack) {
        super.harvestBlock(worldIn, player, pos, state, te, stack);

    }
}
