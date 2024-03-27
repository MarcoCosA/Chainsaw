package me.BerylliumOranges.customEvents;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import me.BerylliumOranges.listeners.traits.ItemTrait;

public class KnockbackEvent extends EntityEvent implements Cancellable {
	boolean isCancelled;
	LivingEntity damager;
	LivingEntity entity;
	Vector direction;
	private static final HandlerList handlers = new HandlerList();

	public KnockbackEvent(LivingEntity damager, LivingEntity entity, Vector direction) {
		super(entity);
		this.entity = entity;
		this.damager = damager;
		this.direction = direction;
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

	public LivingEntity getDamager() {
		return damager;
	}

	public Vector getDirection() {
		return direction;
	}

	public void setDirection(Vector direction) {
		this.direction = direction;
	}
}
