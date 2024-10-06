package me.BerylliumOranges.listeners.items.traits.traits;

import org.bukkit.potion.PotionType;

import net.md_5.bungee.api.ChatColor;

public abstract class ItemTraitCursed extends ItemTrait {
	private static final long serialVersionUID = -4291429898837986560L;

	@Override
	public ChatColor getTraitColor() {
		return ChatColor.DARK_RED;
	}

	@Override
	public PotionType getPotionType() {
		return PotionType.WEAKNESS;
	}

}
