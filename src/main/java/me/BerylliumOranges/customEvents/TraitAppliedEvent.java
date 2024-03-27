package me.BerylliumOranges.customEvents;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.inventory.ItemStack;

import me.BerylliumOranges.listeners.traits.ItemTrait;

public class TraitAppliedEvent extends EntityEvent implements Cancellable {
	ItemTrait trait;
	ItemStack item;
	int timesToApply;
	boolean isCancelled;
	LivingEntity entity;
	private static final HandlerList handlers = new HandlerList();

	public TraitAppliedEvent(LivingEntity entity, ItemTrait trait, ItemStack item, int timesToApply) {
		super(entity);
		this.entity = entity;
		this.trait = trait;
		this.item = item;
		this.timesToApply = timesToApply;
	}

	@Override
	public LivingEntity getEntity() {
		return entity;
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

	public ItemTrait getTrait() {
		return trait;
	}

	public void setTrait(ItemTrait trait) {
		this.trait = trait;
	}

	public ItemStack getItem() {
		return item;
	}

	public void setItem(ItemStack item) {
		this.item = item;
	}

	public int getTimesToApply() {
		return timesToApply;
	}

	public void setTimesToApply(int timesToApply) {
		this.timesToApply = timesToApply;
	}

}
