package com.igteam.immersive_geology.core.data.generators.helpers;

import com.igteam.immersive_geology.core.lib.IGLib;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.versions.forge.ForgeVersion;

public class TagsIG {

    public static ITag.INamedTag<Block> blockTagForge(String name) {
        return blockTag(ForgeVersion.MOD_ID, name);
    }

    public static ITag.INamedTag<Block> blockTag(String mod_id, String name) {
        return BlockTags.makeWrapperTag(mod_id + ":" + name);
    }

    public static ITag.INamedTag<Item> itemTagForge(String name) {
        return itemTag(ForgeVersion.MOD_ID, name);
    }

    public static ITag.INamedTag<Item> itemTag(String mod_id, String name) {
        return ItemTags.makeWrapperTag(mod_id + ":" + name);
    }
}
