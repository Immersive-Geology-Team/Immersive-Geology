package com.igteam.immersive_geology.common.block;

import com.igteam.immersive_geology.client.IGClientRenderHandler;
import com.igteam.immersive_geology.common.item.IGGenericBlockItem;
import com.igteam.immersive_geology.core.registration.IGRegistrationHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

import java.util.*;

public class IGGenericBlock extends Block {
    public IGGenericBlock() {
        super(Properties.of(Material.STONE, MaterialColor.STONE));

        IGClientRenderHandler.setRenderType(this, IGClientRenderHandler.RenderTypeSkeleton.TRANSLUCENT);
    }

}
