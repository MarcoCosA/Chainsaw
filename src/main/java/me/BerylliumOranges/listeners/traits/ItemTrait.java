package me.BerylliumOranges.listeners.traits;

import java.io.Serializable;
import java.util.HashMap;

import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;

import me.BerylliumOranges.listeners.purityItems.traits.utils.TraitOperation;
import net.md_5.bungee.api.ChatColor;

public abstract class ItemTrait implements Cloneable, Serializable {

	public static HashMap<BukkitRunnable, LivingEntity> activePotions = new HashMap<>();

	public enum ToolOption {
		ARMOR_EXCLUSIVE("Armor"), WEAPON_EXCLUSIVE("Weapon"), ANY("Item");

		private final String description;

		ToolOption(String description) {
			this.description = description;
		}

		public String getDescription() {
			return description;
		}
	}

	private static final long serialVersionUID = -6612535046499897801L;
	public final static String LOCKED_INDICATOR = ChatColor.RED + "[Locked]";

	public int potionDuration = 180;

	public boolean locked = false;
	public boolean curse = false;

	public ItemTrait() {

	}

	public abstract String getTraitName();

	public abstract ChatColor getTraitColor();

	public abstract String getPotionDescription();

	public abstract String getToolDescription();

	public abstract PotionType getPotionType();

	public abstract int getRarity();

	public abstract ToolOption getToolOption();

	/** Runnable that executes when a LivingEntity consumes the potion item **/
	public abstract BukkitRunnable potionRunnable(LivingEntity consumer);

	// Accessor methods for properties
	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	/** Potion duration in seconds **/
	public int getPotionDuration() {
		return potionDuration;
	}

	/** Potion duration in seconds **/
	public void setPotionDuration(int potionDuration) {
		this.potionDuration = potionDuration;
	}

	boolean isCurse() {
		return curse;
	}

	public void setCurse(boolean curse) {
		this.curse = curse;
	}

	public abstract boolean executeTrait(TraitOperation op, LivingEntity owner, ItemStack item, boolean victim);

	public abstract void toolEffect(LivingEntity owner);

	public void alertPlayer(LivingEntity p, String text) {
		p.sendMessage("[" + getTraitName() + ChatColor.RESET + "] " + text);
	}

	@Override
	public ItemTrait clone() {
		try {
			return (ItemTrait) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new AssertionError();
		}
	}
}
