package me.BerylliumOranges.listeners.items.traits.dummyevents;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.BerylliumOranges.listeners.items.traits.traits.ItemTrait;
import me.BerylliumOranges.listeners.items.traits.utils.PotionEffectTicker;

public class PotionConsumeEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled = false;
	private ItemTrait effect;
	private LivingEntity consumer;
	private PotionEffectTicker potionEffectTicker;

	public PotionConsumeEvent(ItemTrait effect, LivingEntity consumer, PotionEffectTicker potionEffectTicker) {
		this.effect = effect;
		this.potionEffectTicker = potionEffectTicker;
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

	public LivingEntity getConsumer() {
		return consumer;
	}

	public void setConsumer(LivingEntity consumer) {
		this.consumer = consumer;
	}

	public PotionEffectTicker getPotionEffectTicker() {
		return potionEffectTicker;
	}

	public void setPotionEffectTicker(PotionEffectTicker potionEffectTicker) {
		this.potionEffectTicker = potionEffectTicker;
	}
}
