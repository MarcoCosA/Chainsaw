package me.BerylliumOranges.listeners.items.traits.traits;

import java.io.Serializable;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;

import me.BerylliumOranges.listeners.items.traits.dummyevents.PotionConsumeEvent;
import me.BerylliumOranges.listeners.items.traits.utils.ItemBuilder;
import me.BerylliumOranges.listeners.items.traits.utils.PotionEffectTicker;
import me.BerylliumOranges.listeners.items.traits.utils.TraitCache;
import me.BerylliumOranges.listeners.items.traits.utils.TraitOperation;
import me.BerylliumOranges.main.PluginMain;
import net.md_5.bungee.api.ChatColor;

public abstract class ItemTrait implements Cloneable, Serializable {

	protected UUID consumerUUID = null;
	private transient LivingEntity consumerCached = null;
	protected int potionDuration;
	protected boolean active = false;

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

	public boolean locked = false;

	public ItemTrait(int initialDuration) {
		this.potionDuration = initialDuration;
		if (this instanceof Listener)
			Bukkit.getPluginManager().registerEvents((Listener) this, PluginMain.getInstance());
	}

	public abstract String getTraitName();

	public abstract ChatColor getTraitColor();

	public abstract String getPotionDescription();

	public String getFullPotionDescription() {
		return getPotionDescription() + " " + ChatColor.RESET + ChatColor.WHITE + ItemBuilder.getTimeInMinutes(getPotionDuration());
	}

	public abstract String getToolDescription();

	public String getFullToolDescription() {
		return getToolDescription();
	}

	public abstract PotionType getPotionType();

	public abstract ToolOption getToolOption();

	PotionEffectTicker potionEffectTicker = null;

	/** Runnable that executes when a LivingEntity consumes the potion item **/
	public boolean handlePotionConsumption(LivingEntity consumer) {
		for (ItemTrait t : TraitCache.getTraits()) {
			if (t.getTraitName().equals(getTraitName())) {
				if (t.getConsumer() != null && t.getConsumer().equals(consumer)) {
					consumer.getWorld().playSound(consumer, Sound.ENTITY_GHAST_AMBIENT, 0.5F, 2F);
					return false;
				}
			}
		}

		potionEffectTicker = new PotionEffectTicker(this, potionDuration);
		PotionConsumeEvent event = new PotionConsumeEvent(this, consumer, potionEffectTicker);
		Bukkit.getServer().getPluginManager().callEvent(event);

		if (event.isCancelled())
			return false;

		active = true;
		this.consumerUUID = event.getConsumer().getUniqueId();
		alertPlayer(getConsumer(), ChatColor.BOLD + "Active " + ItemBuilder.getTimeInMinutes(potionEffectTicker.getPotionDuration() / 20));
		Bukkit.getPluginManager().registerEvents(potionEffectTicker, PluginMain.getInstance());
		potionEffectTicker.startPotion();
		return true;
	}

	public void handlePotionEffectStart() {
	}

	public void handlePotionEffectTick() {
	}

	public void handlePotionEffectEnd() {
	}

	public void handlePotionEnd() {
		potionEffectTicker.stopTimer();
		HandlerList.unregisterAll(potionEffectTicker);
		alertPlayer(getConsumer(), ChatColor.BOLD + "Expired");
		if (this instanceof Listener)
			HandlerList.unregisterAll((Listener) this);
		active = false;
		potionEffectTicker = null;
		consumerUUID = null;
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
				if (liv.getEquipment().getItemInMainHand() != null
						&& TraitCache.getTraitsFromItem(liv.getEquipment().getItemInMainHand()).contains(this)) {
					return liv.getEquipment().getItemInMainHand();
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
		if (consumerUUID == null)
			return null;

		if (consumerCached != null && consumerCached.isValid()) {
			return consumerCached;
		}
		consumerCached = null;

		Entity entity = Bukkit.getEntity(consumerUUID);

		if (entity instanceof LivingEntity) {
			consumerCached = (LivingEntity) entity;
			return consumerCached;
		}
		return null;
	}

	public PotionEffectTicker getPotionEffectTicker() {
		return potionEffectTicker;
	}

	public void setPotionEffectTicker(PotionEffectTicker potionEffectTicker) {
		this.potionEffectTicker = potionEffectTicker;
	}

	/** This is used exclusively when deserializing **/
	public void registerListeners() {
		if (this instanceof Listener)
			PluginMain.getInstance().getServer().getPluginManager().registerEvents((Listener) this, PluginMain.getInstance());

		if (potionEffectTicker != null)
			PluginMain.getInstance().getServer().getPluginManager().registerEvents(potionEffectTicker, PluginMain.getInstance());
	}

	public boolean isPotionActive() {
		return active;
	}

}
