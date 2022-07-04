package com.igteam.immersive_geology.common.item.helper;

import com.igteam.immersive_geology.common.item.distinct.IGBagItem;
import com.igteam.immersive_geology.core.lib.IGLib;
import igteam.immersive_geology.materials.pattern.ItemPattern;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class IGBagUtil
{
    public static boolean isBag(ItemStack bag) { return bag.getItem() instanceof IGBagItem; }

    public static boolean isFull(ItemStack bag) { return getItemCount(bag) >= bag.getMaxDamage(); }

    public static boolean isEmpty(ItemStack bag) { return getItemCount(bag) == 0; }

    public static boolean canAddItemStack(ItemStack bag, ItemStack input)
    {
        if(!isBag(bag)||isFull(bag)||isIgnored(input)) return false;
        return true;
    }

    private static boolean isIgnored(ItemStack stack)
    {
        Item item = stack.getItem();
        if(
                item.getTags().contains(ItemPattern.stone_chunk.getPatternGroup())
                ||item.getTags().contains(ItemPattern.stone_bit.getPatternGroup())
                ||item.getTags().contains(ItemPattern.ore_chunk.getPatternGroup())
                ||item.getTags().contains(ItemPattern.ore_bit.getPatternGroup())
        ) return false;
        return true;
    }

    public static void addItemStack(ItemStack bag, ItemStack input)
    {
        if(!isBag(bag)||isFull(bag)||isBag(input)) return;
        ItemStack stack = input.copy();
        int maxInput = Math.min(stack.getCount(), bag.getMaxDamage() - getItemCount(bag));
        int amount = maxInput;
        CompoundNBT bagTag = bag.getOrCreateTag();
        ListNBT items = bagTag.getList(IGLib.MODID, Constants.NBT.TAG_COMPOUND);
        while(amount != 0)
        {
            stack.setCount(maxInput);
            CompoundNBT itemStackNbt = new CompoundNBT();
            ItemStack existingStack = getNonMaxStack(bag, stack);
            int index = getStackIndex(bag, existingStack);
            if(!existingStack.isEmpty() && stack.getMaxStackSize() > 1)
            {
                stack.setCount(Math.min(stack.getCount(), stack.getMaxStackSize() - existingStack.getCount()));
                existingStack.grow(stack.getCount());

            }
            if(index != -1 && stack.getMaxStackSize() > 1)
            {
                existingStack.write(itemStackNbt);
                items.remove(index);
                items.add(index, itemStackNbt);
                amount -= stack.getCount();
            } else
            {
                stack.write(itemStackNbt);
                items.add(itemStackNbt);
                amount = 0;
            }
        }
        bagTag.put(IGLib.MODID, items);
        bag.setTag(bagTag);
        input.shrink(maxInput);
    }

    public static ItemStack removeItemStack(ItemStack bag)
    {
        if(!isBag(bag)||isEmpty(bag)) return ItemStack.EMPTY;
        List<ItemStack> itemStacks = getItems(bag);
        ItemStack stack = itemStacks.get(itemStacks.size()-1);
        CompoundNBT bagTag = bag.getOrCreateTag();
        ListNBT items = bagTag.getList(IGLib.MODID, Constants.NBT.TAG_COMPOUND);
        items.remove(items.size()-1);
        bagTag.put(IGLib.MODID, items);
        bag.setTag(bagTag);
        return stack;
    }

    public static int getItemCount(ItemStack bag) { return Objects.requireNonNull(getItems(bag).stream().mapToInt(ItemStack::getCount).sum()); }

    public static List<ItemStack> getItems(ItemStack bag)
    {
        if(!isBag(bag)) return Collections.emptyList();
        CompoundNBT bagTag = bag.getOrCreateTag();
        ListNBT items = bagTag.getList(IGLib.MODID, Constants.NBT.TAG_COMPOUND);
        return items.stream().map(x -> ItemStack.read((CompoundNBT) x)).collect(Collectors.toList());
    }

    private static ItemStack getStack(ItemStack bag, ItemStack stack) { return getItems(bag).stream().filter(x -> ItemStack.areItemsEqual(x, stack)).findFirst().orElse(ItemStack.EMPTY); }

    private static ItemStack getNonMaxStack(ItemStack bag, ItemStack stack) { return getItems(bag).stream().filter(x -> ItemStack.areItemsEqual(x, stack) && x.getCount() != x.getMaxStackSize()).findFirst().orElse(ItemStack.EMPTY); }

    private static int getStackIndex(ItemStack bag, ItemStack stack)
    {
        List<ItemStack> items = getItems(bag);
        return IntStream.range(0, items.size()).filter(i -> ItemStack.areItemStacksEqual(stack, items.get(i))).findFirst().orElse(-1);
    }
}
