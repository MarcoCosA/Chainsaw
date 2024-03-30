package me.BerylliumOranges.customEvents;

import java.util.List;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import me.BerylliumOranges.listeners.items.traits.traits.ItemTrait;

public class ItemCombineEvent extends Event implements Cancellable {
	ItemStack item;
	boolean isCancelled;
	LivingEntity entity;
	List<ItemTrait> traits;
	private static final HandlerList handlers = new HandlerList();

	public ItemCombineEvent(ItemStack item, List<ItemTrait> traits) {
		this.item = item;
		this.traits = traits;
	}

	public ItemStack getItem() {
		return item;
	}

	public void setItem(ItemStack item) {
		this.item = item;
	}

	@Override
	public boolean isCancelled() {
		return isCancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.isCancelled = cancel;

	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public List<ItemTrait> getTraits() {
		return traits;
	}

	public void setTraits(List<ItemTrait> traits) {
		this.traits = traits;
	}

}
