package me.BerylliumOranges.listeners.items.traits.traits;

import org.bukkit.potion.PotionType;

import net.md_5.bungee.api.ChatColor;

public class ItemTraitPlaceholder extends ItemTrait {

	public ItemTraitPlaceholder() {
		super(0);
	}

	private static final long serialVersionUID = 1318520862884228707L;

	@Override
	public String getTraitName() {
		return getTraitColor() + "Empty Trait Slot";
	}

	@Override
	public ChatColor getTraitColor() {
		return ChatColor.WHITE;
	}

	@Override
	public String getPotionDescription() {
		return ChatColor.WHITE + "";
	}

	@Override
	public String getToolDescription() {
		return ChatColor.WHITE + "";
	}

	@Override
	public PotionType getPotionType() {
		return PotionType.WATER;
	}

	@Override
	public ToolOption getToolOption() {
		return ToolOption.ANY;
	}
}
