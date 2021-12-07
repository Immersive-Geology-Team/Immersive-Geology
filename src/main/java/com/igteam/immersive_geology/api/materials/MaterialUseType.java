package com.igteam.immersive_geology.api.materials;

import com.igteam.immersive_geology.client.menu.helper.ItemSubGroup;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;

public enum MaterialUseType {
    //Remember Naming convention is to add a type in the Singular, so Crystal not Crystals, this is due to how we generate tags! (we add an s to the end)
    STATIC_ITEM(false, ItemSubGroup.misc),
    STATIC_BLOCK(true, ItemSubGroup.misc),

    //Blocks
    STORAGE_BLOCK(true, ItemSubGroup.processed),
    DUST_BLOCK(true, ItemSubGroup.natural),     //Returns Self
    SHEETMETAL(true, ItemSubGroup.processed),     //Returns Self
    SHEETMETAL_STAIRS(true, ItemSubGroup.processed), //Review ME!!
    ORE_STONE(true, ItemSubGroup.natural),
    COBBLESTONE(true, ItemSubGroup.natural),    //Returns Self
    STONE(true, ItemSubGroup.natural),          //Gives Chunks
    GEODE(true, ItemSubGroup.natural),          //Gives Crystals
    BRICKS(true, ItemSubGroup.processed),
    SLAB(true, ItemSubGroup.processed), //Gives slab
    //Items
    CHUNK(false, ItemSubGroup.natural),
    ROCK_BIT(false, ItemSubGroup.natural),
    ORE_CHUNK(false, ItemSubGroup.natural),
    ORE_BIT(false, ItemSubGroup.natural),
    CLAY(false, ItemSubGroup.natural),

    DIRTY_CRUSHED_ORE(false, ItemSubGroup.processed),
    CRUSHED_ORE(false, ItemSubGroup.processed),
    METAL_OXIDE(false, ItemSubGroup.processed),

    RAW_CRYSTAL(false, ItemSubGroup.natural),
    CUT_CRYSTAL(false, ItemSubGroup.processed),

    DUST(false, ItemSubGroup.processed),
    BUCKET(false, ItemSubGroup.processed),
    FLASK(false, ItemSubGroup.processed),
    ROD(false, ItemSubGroup.processed),
    INGOT(false, ItemSubGroup.processed),
    PLATE(false, ItemSubGroup.processed),
    NUGGET(false, ItemSubGroup.processed),
    GEAR(false, ItemSubGroup.processed),
    WIRE(false, ItemSubGroup.processed),
    TINY_DUST(false, ItemSubGroup.processed),
    BRICK(false, ItemSubGroup.processed),

    //Liquid Types
    FLUIDS(false, ItemSubGroup.misc),
    SLURRY(false, ItemSubGroup.misc),

    //Multiblock type
    MACHINE(false, ItemSubGroup.machines);

    public String getName()
    {
        return this.name().toLowerCase();
    }

    public String tagName()
    {
        return getName();
    }

    private final boolean isBlock;
    private final ItemSubGroup subGroup;

    MaterialUseType(boolean isBlock, ItemSubGroup subGroup){
        this.isBlock = isBlock;
        this.subGroup = subGroup;
    }

    public ItemSubGroup getSubgroup(){
        return subGroup;
    }

    public boolean isBlock(){
        return isBlock;
    }

    public Item getItem(Material material) {
        return IGRegistrationHolder.getItemByMaterial(material, this);
    }

    public Item getItem(Material stone_base, Material material) {
        return IGRegistrationHolder.getItemByMaterial(stone_base, material, this);
    }

    public Block getBlock(Material material) {
        return IGRegistrationHolder.getBlockByMaterial(this, material);
    }

    public Fluid getFluid(Material material, boolean isFlowing) {
        return IGRegistrationHolder.getFluidByMaterial(material, isFlowing);
    }

    public Fluid getSlurry(Material solute, Material fluid, boolean isFlowing) {
        return IGRegistrationHolder.getSlurryByMaterials(solute, fluid, isFlowing);
    }
}
