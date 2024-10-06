package me.BerylliumOranges.listeners.items.traits.dummyevents;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.BerylliumOranges.listeners.items.traits.traits.ItemTrait;

public class PotionEffectTickEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled = false;
	private ItemTrait effect;

	public PotionEffectTickEvent(ItemTrait effect) {
		this.effect = effect;
		effect.handlePotionEffectTick();
	}

	public ItemTrait getPotionEffect() {
		return effect;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		cancelled = cancel;

	}
}
