package com.igteam.immersive_geology.common.block;

import com.igteam.immersive_geology.ImmersiveGeology;
import com.igteam.immersive_geology.api.materials.MaterialEnum;
import com.igteam.immersive_geology.api.materials.MaterialUseType;
import com.igteam.immersive_geology.client.menu.helper.ItemSubGroup;
import com.igteam.immersive_geology.common.block.helpers.BlockMaterialType;
import com.igteam.immersive_geology.common.block.helpers.IGBlockType;
import com.igteam.immersive_geology.common.item.IGBlockItem;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.Item;

public class IGStaticBlock extends Block implements IGBlockType {

    //Also known as Registry Name
    private final String holder_name;
    private final Item itemBlock;

    public IGStaticBlock(String registryName, Material material, MaterialColor color){
        this(AbstractBlock.Properties.create(material, color), registryName);
    }

    public IGStaticBlock(String registryName, Material blockMaterial) {
        this(AbstractBlock.Properties.create(blockMaterial, MaterialColor.STONE), registryName);
    }

    private IGStaticBlock(Properties prop, String registryName){
        super(prop);
        this.setRegistryName(registryName.toLowerCase());
        this.holder_name = registryName.toLowerCase();

        this.itemBlock = new IGBlockItem(this, this, ItemSubGroup.misc, MaterialEnum.Empty.getMaterial());
        itemBlock.setRegistryName(registryName.toLowerCase());

        if(IGRegistrationHolder.registeredIGBlocks.putIfAbsent(registryName.toLowerCase(), this) != null) {
            ImmersiveGeology.getNewLogger().error("Duplicate key used to register static block");
        }

        if(IGRegistrationHolder.registeredIGItems.putIfAbsent(registryName.toLowerCase(), this.itemBlock) != null){
            ImmersiveGeology.getNewLogger().error("Duplicate key used to register static item block");
        }
    }

    @Override
    public Item asItem() {
        return this.itemBlock;
    }

    @Override
    public String getHolderName() {
        return holder_name;
    }

    @Override
    public MaterialUseType getBlockUseType() {
        return MaterialUseType.STATIC_BLOCK;
    }

    /**
     * @description used for sorting items and blocks in the IG Creative menu tab
     * @param type
     * @return material used
     */
    @Override
    public com.igteam.immersive_geology.api.materials.Material getMaterial(BlockMaterialType type) {
        return MaterialEnum.Empty.getMaterial();
    }

    /**
     * @description used for getting the materialtype which is used to sort items in the creative menu tab
     * @return
     */
    @Override
    public MaterialUseType getDropUseType() {
        return MaterialUseType.STATIC_BLOCK;
    }

    @Override
    public float maxDrops() {
        return 1;
    }

    @Override
    public float minDrops() {
        return 1;
    }
}
