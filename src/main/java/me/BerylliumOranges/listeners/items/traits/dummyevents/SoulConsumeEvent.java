package me.BerylliumOranges.listeners.items.traits.dummyevents;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import me.BerylliumOranges.listeners.items.traits.traits.ItemTrait;
import me.BerylliumOranges.listeners.items.traits.traits.ItemTraitSoulHolder;

public class SoulConsumeEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled = false;

	private LivingEntity user;

	private ItemTrait consumer;
	private ItemTraitSoulHolder holder;

	private ItemStack consumerItem;
	private ItemStack holderItem;
	private int soulsToTake;

	public SoulConsumeEvent(LivingEntity user, ItemStack consumerItem, ItemTrait consumer, ItemStack holderItem, ItemTraitSoulHolder holder,
			int soulsToTake) {
		this.user = user;
		this.consumerItem = consumerItem;
		this.consumer = consumer;
		this.holderItem = holderItem;
		this.holder = holder;
		this.soulsToTake = soulsToTake;
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

	public LivingEntity getUser() {
		return user;
	}

	public int getSoulsToTake() {
		return soulsToTake;
	}

	public void setSoulsToTake(int soulsToTake) {
		this.soulsToTake = soulsToTake;
	}

	public ItemTrait getConsumerTrait() {
		return consumer;
	}

	public ItemTraitSoulHolder getHolderTrait() {
		return holder;
	}

	public ItemStack getConsumerItem() {
		return consumerItem;
	}

	public ItemStack getHolderItem() {
		return holderItem;
	}
}
