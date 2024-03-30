package me.BerylliumOranges.listeners.items.traits.traits;

import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;

import me.BerylliumOranges.listeners.items.traits.utils.TraitOperation;
import net.md_5.bungee.api.ChatColor;

public class PlaceholderTrait extends ItemTrait {

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
		// TODO Auto-generated method stub
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
	public BukkitRunnable potionRunnable(LivingEntity consumer) {
		return new BukkitRunnable() {
			@Override
			public void run() {

			}
		};
	}

	@Override
	public int getRarity() {
		return 0;
	}

	@Override
	public ToolOption getToolOption() {
		return ToolOption.ANY;
	}

	@Override
	public boolean executeTrait(TraitOperation op, LivingEntity owner, ItemStack item, boolean victim) {
		return false;
	}

	@Override
	public void toolEffect(LivingEntity center) {
	}
}
