package me.BerylliumOranges.listeners.items.traits.traits;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Listener;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;

import me.BerylliumOranges.listeners.items.traits.dummyevents.PotionConsumeEvent;
import me.BerylliumOranges.listeners.items.traits.utils.PotionEffectTicker;
import me.BerylliumOranges.listeners.items.traits.utils.TraitCache;
import me.BerylliumOranges.listeners.items.traits.utils.TraitOperation;
import me.BerylliumOranges.main.PluginMain;
import net.md_5.bungee.api.ChatColor;

public abstract class ItemTrait implements Cloneable, Serializable {

	public static List<ItemTrait> activePotions = new ArrayList<>();

	protected LivingEntity consumer = null;

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

	public enum TraitLocation {
		IN_INVENTORY, ON_ARMOR, IN_MAINHAND, IN_OFFHAND;
	}

	private static final long serialVersionUID = -6612535046499897801L;
	public final static String LOCKED_INDICATOR = ChatColor.RED + "[Locked]";

	public int potionDuration = 180;

	public boolean locked = false;

	public ItemTrait() {

	}

	public abstract String getTraitName();

	public abstract ChatColor getTraitColor();

	public abstract String getPotionDescription();

	public abstract String getToolDescription();

	public abstract PotionType getPotionType();

	public abstract ToolOption getToolOption();

	PotionEffectTicker potionEffectTicker = null;

	/** Runnable that executes when a LivingEntity consumes the potion item **/
	public boolean handlePotionConsumption(LivingEntity consumer) {
		for (ItemTrait t : activePotions) {
			if (t.getTraitName().equals(getTraitName())) {
				if (t.getConsumer().equals(consumer)) {
					return false;
				}
			}
		}

		potionEffectTicker = new PotionEffectTicker(this, getPotionDuration());
		PotionConsumeEvent event = new PotionConsumeEvent(this, consumer, potionEffectTicker);
		Bukkit.getServer().getPluginManager().callEvent(event);

		if (event.isCancelled())
			return false;

		this.consumer = event.getConsumer();
		potionEffectTicker.start();

		return true;
	}

	public void handlePotionEffectStart() {
	}

	public void handlePotionEffectTick() {
	}

	public boolean handlePotionEffectEnd() {
		if (potionEffectTicker != null) {
			boolean running = potionEffectTicker.isTimerRunning();
			potionEffectTicker.setTimeElapsed(potionEffectTicker.getPotionDuration());
			return running;
		}
		return false;

	}

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

	public boolean executeTrait(TraitOperation op, LivingEntity owner, ItemStack item, boolean victim) {
		return false;
	}

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

	public ItemStack getTraitItemFromEntity(LivingEntity liv, TraitLocation[] locations) {
		for (TraitLocation location : locations) {
			switch (location) {
			case IN_INVENTORY:
				if (liv instanceof InventoryHolder) {
					InventoryHolder holder = (InventoryHolder) liv;
					for (ItemStack item : holder.getInventory().getContents()) {
						if (item != null && TraitCache.getTraitsFromItem(item).contains(this)) {
							return item;
						}
					}
				}
				break;
			case ON_ARMOR:
				for (ItemStack item : liv.getEquipment().getArmorContents()) {
					if (item != null && TraitCache.getTraitsFromItem(item).contains(this)) {
						return item;
					}
				}
				break;
			case IN_MAINHAND:
				if (liv.getEquipment().getItemInOffHand() != null
						&& TraitCache.getTraitsFromItem(liv.getEquipment().getItemInOffHand()).contains(this)) {
					return liv.getEquipment().getItemInOffHand();
				}
				break;
			case IN_OFFHAND:
				if (liv.getEquipment().getItemInOffHand() != null
						&& TraitCache.getTraitsFromItem(liv.getEquipment().getItemInOffHand()).contains(this)) {
					return liv.getEquipment().getItemInOffHand();
				}
				break;
			default:
				throw new IllegalArgumentException("Unsupported location: " + location);
			}
		}
		return null;
	}

	public boolean entityHasTrait(LivingEntity liv, TraitLocation... locations) {
		return getTraitItemFromEntity(liv, locations) != null;
	}

	public LivingEntity getConsumer() {
		return consumer;
	}

	/** This is used exclusively when deserializing **/
	public void registerListeners() {
		if (this instanceof Listener)
			PluginMain.getInstance().getServer().getPluginManager().registerEvents((Listener) this, PluginMain.getInstance());

		if (potionEffectTicker != null)
			PluginMain.getInstance().getServer().getPluginManager().registerEvents(potionEffectTicker, PluginMain.getInstance());
	}
}
