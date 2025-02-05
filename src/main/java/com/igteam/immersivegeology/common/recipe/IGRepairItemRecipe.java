package com.igteam.immersivegeology.common.recipe;

import com.igteam.immersivegeology.common.item.IGGenericItem;
import com.igteam.immersivegeology.core.registration.IGRecipeSerializers;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.Optional;

public class IGRepairItemRecipe extends CustomRecipe
{
	public IGRepairItemRecipe(ResourceLocation name)
	{
		super(name, CraftingBookCategory.MISC);
	}

	@Override
	public boolean matches(@Nonnull CraftingContainer inv, @Nonnull Level worldIn)
	{
		return findInputSlots(inv).isPresent();
	}

	@Nonnull
	@Override
	public ItemStack assemble(@Nonnull CraftingContainer inv, RegistryAccess access)
	{
		return findInputSlots(inv)
				.map(p -> combineStacks(p.getFirst(), p.getSecond()))
				.orElse(ItemStack.EMPTY);
	}

	@Override
	public boolean canCraftInDimensions(int width, int height)
	{
		return width*height >= 2;
	}

	@Nonnull
	@Override
	public NonNullList<ItemStack> getRemainingItems(CraftingContainer inv)
	{
		return NonNullList.withSize(inv.getHeight()*inv.getWidth(), ItemStack.EMPTY);
	}

	@Nonnull
	@Override
	public RecipeSerializer<IGRepairItemRecipe> getSerializer()
	{
		return Objects.requireNonNull(IGRecipeSerializers.IG_REPAIR_SERIALIZER.get());
	}

	private Optional<Pair<ItemStack, ItemStack>> findInputSlots(CraftingContainer inv)
	{
		Optional<ItemStack> first = Optional.empty();
		Optional<ItemStack> second = Optional.empty();
		for(int slot = 0; slot < inv.getContainerSize(); ++slot)
		{
			ItemStack stack = inv.getItem(slot);
			if(!stack.isEmpty())
			{
				if(!isValidInput(stack))
					return Optional.empty();
				else if(first.isPresent()&&second.isPresent())
					return Optional.empty();
				else if(first.isPresent())
				{
					ItemStack existing = first.get();
					if(existing.getItem()!=stack.getItem())
						return Optional.empty();
					second = Optional.of(stack);
				}
				else
					first = Optional.of(stack);
			}
		}
		if(first.isPresent()&&second.isPresent())
			return Optional.of(Pair.of(first.get(), second.get()));
		else
			return Optional.empty();
	}

	private boolean isValidInput(ItemStack in)
	{
		return in.getItem() instanceof IGGenericItem&&((IGGenericItem)in.getItem()).isIGRepairable(in);
	}

	// Copy of the vanilla logic
	private ItemStack combineStacks(ItemStack a, ItemStack b)
	{
		int remainingA = a.getMaxDamage()-a.getDamageValue();
		int remainingB = a.getMaxDamage()-b.getDamageValue();
		int remainingResult = remainingA+remainingB+a.getMaxDamage()*5/100;
		int damageResult = a.getMaxDamage()-remainingResult;
		if(damageResult < 0)
		{
			damageResult = 0;
		}

		ItemStack result = new ItemStack(a.getItem());
		result.setDamageValue(damageResult);
		return result;
	}
}
