package me.BerylliumOranges.listeners.purityItems.traits.dummyevents;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityRegainHealthEvent;

public class DummyHealEvent extends Event {
	EntityRegainHealthEvent event;
	double originalAmount;

	public DummyHealEvent(EntityRegainHealthEvent event) {
		this.event = event;
		originalAmount = event.getAmount();
	}

	public EntityRegainHealthEvent getHealEvent() {
		return event;
	}

	public double getOriginalAmount() {
		return originalAmount;
	}

	public void setOriginalAmount(double originalAmount) {
		this.originalAmount = originalAmount;
	}

	@Override
	public HandlerList getHandlers() {
		return null;
	}
}