package igteam.immersive_geology.common.block.blocks;

import igteam.api.materials.helper.MaterialInterface;
import igteam.api.materials.helper.MaterialTexture;
import igteam.api.materials.pattern.BlockPattern;
import igteam.api.materials.pattern.ItemPattern;
import igteam.immersive_geology.common.block.IGGenericBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class IGOreBlock extends IGGenericBlock {
    public IGOreBlock(MaterialInterface<?> m, BlockPattern p) {
        super(m, p, Properties.create(Material.ROCK, MaterialColor.STONE).hardnessAndResistance(2f).harvestTool(ToolType.PICKAXE).harvestLevel(1));
    }


    @Override
    public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, ItemStack tool) {
        super.harvestBlock(worldIn, player, pos, state, te, tool);
     /*   if (worldIn instanceof ServerWorld) {
            MaterialInterface<?> oreMaterial = this.getMaterial(MaterialTexture.overlay);
            if(!oreMaterial.instance().isSalt() && tool.canHarvestBlock(state))
            {
                MaterialInterface<?> stoneMaterial = this.getMaterial(MaterialTexture.base);
                Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(Objects.requireNonNull(tool));
                if(enchantments.get(Enchantments.SILK_TOUCH) != null){
                    try {
                        ItemEntity item = new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), getMaterial(MaterialTexture.base).getStack(BlockPattern.ore, getMaterial(MaterialTexture.overlay)));
                        worldIn.addEntity(item);
                    } catch (Exception ignored){}
                }
                else
                {
                    int harvestLevel = tool.getHarvestLevel(ToolType.PICKAXE, null, state);
                    int fortune = Math.min(5,enchantments.getOrDefault(Enchantments.FORTUNE, 0));
                    Random rand = new Random();
                    int oreAmount = Math.min(6, harvestLevel + rand.nextInt(harvestLevel+1));
                    int chunkAmount = 8 - oreAmount;
                    int bitAmount = Math.min(8, harvestLevel + rand.nextInt(harvestLevel+1)) + rand.nextInt((rand.nextInt(fortune+1)+1)*fortune+1);
                    try {
                        ItemEntity itemOre = new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), getMaterial(MaterialTexture.base).getStack(ItemPattern.ore_chunk, getMaterial(MaterialTexture.overlay), oreAmount));
                        ItemEntity itemChunk = new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), getMaterial(MaterialTexture.base).getStack(ItemPattern.stone_chunk, chunkAmount));
                        ItemEntity itemBits = new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), getMaterial(MaterialTexture.base).getStack(ItemPattern.ore_bit, getMaterial(MaterialTexture.overlay), bitAmount));
                        worldIn.addEntity(itemOre);
                        worldIn.addEntity(itemChunk);
                        worldIn.addEntity(itemBits);
                    } catch (Exception ignored){}
                }
            }
        }*/

    }
}
