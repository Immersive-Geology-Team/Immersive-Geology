package com.igteam.immersivegeology.core.material.helper;

public enum ToolTierHelper implements com.igteam.immersivegeology.core.lib.shim.MCShims.ITier
{
	UNOBTANIUM(0, 99999, 2.0F, 0.0F, 15);

	private final int level;
	private final int uses;
	private final float speed;
	private final float damage;
	private final int enchantmentValue;

	ToolTierHelper(int level, int uses, float speed, float damage, int enchantmentValue)
	{
		this.level = level;
		this.uses = uses;
		this.speed = speed;
		this.damage = damage;
		this.enchantmentValue = enchantmentValue;
	}

	public int getLevel()
	{
		return level;
	}

	public int getUses()
	{
		return uses;
	}

	public float getSpeed()
	{
		return speed;
	}

	public float getAttackDamageBonus()
	{
		return damage;
	}

	public int getEnchantmentValue()
	{
		return enchantmentValue;
	}
}
