package igteam.immersive_geology.common.block.blocks;

import igteam.immersive_geology.common.block.IGGenericBlock;
import igteam.api.materials.helper.MaterialInterface;
import igteam.api.materials.helper.MaterialTexture;
import igteam.api.materials.pattern.BlockPattern;
import igteam.api.materials.pattern.ItemPattern;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.item.ItemEntity;
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

        try {
            int amount = worldIn.getRandom().nextInt(4);
            ItemEntity item = new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), getMaterial(MaterialTexture.base).getStack(ItemPattern.ore_bit, getMaterial(MaterialTexture.overlay), amount));
            worldIn.addEntity(item);
        } catch (Exception ignored){};
    }
}
