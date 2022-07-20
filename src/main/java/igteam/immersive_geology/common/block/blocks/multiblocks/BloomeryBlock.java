package igteam.immersive_geology.common.block.blocks.multiblocks;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.common.IEContent;
import blusunrize.immersiveengineering.common.blocks.BlockItemIE;
import blusunrize.immersiveengineering.common.blocks.IETileProviderBlock;
import igteam.api.IGApi;
import igteam.immersive_geology.ImmersiveGeology;
import igteam.immersive_geology.common.block.tileentity.BloomeryTileEntity;
import igteam.immersive_geology.common.item.IGGenericBlockItem;
import igteam.immersive_geology.common.item.multiblock.IGBloomeryBlockItem;
import igteam.immersive_geology.core.lib.IGLib;
import igteam.immersive_geology.core.registration.IGRegistrationHolder;
import igteam.immersive_geology.core.registration.IGTileTypes;
import igteam.api.block.IGBlockType;
import igteam.api.materials.StoneEnum;
import igteam.api.main.IGRegistryProvider;
import igteam.api.materials.helper.MaterialInterface;
import igteam.api.materials.pattern.BlockPattern;
import igteam.api.materials.pattern.ItemPattern;
import igteam.api.materials.pattern.MaterialPattern;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLLoader;

import java.util.*;

public class BloomeryBlock extends IETileProviderBlock<BloomeryTileEntity> implements IGBlockType {

    protected final IGGenericBlockItem itemBlock;
    public BloomeryBlock() {
        super("bloomery", () -> IGTileTypes.BLOOMERY.get(), Properties.create(Material.ROCK).sound(SoundType.STONE));
        ImmersiveGeology.getNewLogger().info("Setting up Bloomery");

        if(!FMLLoader.isProduction()) { // apparently this fixes the concurrent exception and it works for IP so hopefully this works for us. ~Muddykat and thanks again Twisted for your code!
            try {
                IEContent.registeredIEBlocks.remove(this);
                Iterator<Item> it = IEContent.registeredIEItems.iterator();
                while (it.hasNext()) {
                    Item item = it.next();
                    if (item instanceof BlockItemIE && ((BlockItemIE) item).getBlock() == this) {
                        it.remove();
                        break;
                    }
                }
            } catch (ConcurrentModificationException exception) {
                IGApi.getNewLogger().error("Concurrent Modification Error - Essentially you'll need to restart, this issue is erratic and it's due to IG using IE internal Classes. Don't bother Immersive Engineering about this. (sorry Blu, I'll fix it sometime in the future ~Muddykat)");
                IGApi.getNewLogger().error(exception.getMessage());
                throw exception;
            }
        }

        this.itemBlock = new IGBloomeryBlockItem(this, StoneEnum.Stone, ItemPattern.block_item);
        this.itemBlock.setRegistryName(new ResourceLocation(IGLib.MODID, ItemPattern.block_item.getName() + "_" + getHolderKey()));
;
        IGRegistryProvider.IG_BLOCK_REGISTRY.putIfAbsent(IGRegistrationHolder.getRegistryKey(this), this);
        IGRegistryProvider.IG_ITEM_REGISTRY.putIfAbsent(new ResourceLocation(IGLib.MODID, ItemPattern.block_item.getName() + "_" + getHolderKey()), itemBlock);

    }
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    @Override
    public ResourceLocation createRegistryName(){
        return IGRegistrationHolder.getRegistryKey(this);
    }

    @Override
    public Item asItem() {
        return itemBlock;
    }

    @Override
    public boolean canIEBlockBePlaced(BlockState newState, BlockItemUseContext context)
    {
        BlockPos start = context.getPos();
        World w = context.getWorld();
        return areAllReplaceable(start, start.up(), context);
    }

    @Override
    public StateContainer<Block, BlockState> getStateContainer() {
        return super.getStateContainer();
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(IEProperties.FACING_HORIZONTAL, IEProperties.MULTIBLOCKSLAVE, LIT);
    }

    @Override
    protected BlockState getInitDefaultState() {
        BlockState state = this.stateContainer.getBaseState();
        return state.with(IEProperties.FACING_HORIZONTAL, getDefaultFacing()).with(LIT, false).getBlockState();
    }

    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        if (stateIn.get(LIT)) {
            double d0 = (double)pos.getX() + 0.5D;
            double d1 = (double)pos.getY();
            double d2 = (double)pos.getZ() + 0.5D;
            if (rand.nextDouble() < 0.1D) {
                worldIn.playSound(d0, d1, d2, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
            }

            Direction direction = stateIn.get(IEProperties.FACING_HORIZONTAL).getOpposite();
            Direction.Axis direction$axis = direction.getAxis();
            double d3 = 0.52D;
            double d4 = rand.nextDouble() * 0.6D - 0.3D;
            double d5 = direction$axis == Direction.Axis.X ? (double)direction.getXOffset() * 0.52D : d4;
            double d6 = rand.nextDouble() * 6.0D / 16.0D;
            double d7 = direction$axis == Direction.Axis.Z ? (double)direction.getZOffset() * 0.52D : d4;
            worldIn.addParticle(ParticleTypes.SMOKE, d0 + d5, d1 + d6, d2 + d7, 0.0D, 0.0D, 0.0D);
            worldIn.addParticle(ParticleTypes.FLAME, d0 + d5, d1 + d6, d2 + d7, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public int getColourForIGBlock(int pass) {
        return 0;
    }

    @Override
    public Collection<MaterialInterface> getMaterials() {
        return Collections.singleton(StoneEnum.Stone);
    }

    @Override
    public MaterialPattern getPattern() {
        return BlockPattern.machine;
    }

    @Override
    public String getHolderKey() {
        StringBuilder data = new StringBuilder();

        data.append("_").append(StoneEnum.Stone.getName());

        return getPattern() + data.toString() + "_" + name;
    }

    @Override
    public Block getBlock() {
        return this;
    }
}
