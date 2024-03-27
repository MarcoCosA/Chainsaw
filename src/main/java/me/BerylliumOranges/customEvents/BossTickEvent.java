package me.BerylliumOranges.customEvents;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BossTickEvent extends Event implements Cancellable {
	boolean isCancelled;
	private static final HandlerList handlers = new HandlerList();

	public BossTickEvent() {

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

}
