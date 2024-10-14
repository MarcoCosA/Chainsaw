package me.BerylliumOranges.listeners.items.traits.utils;

import java.io.Serializable;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import me.BerylliumOranges.customEvents.TickEvent;
import me.BerylliumOranges.listeners.items.traits.dummyevents.PotionEffectEndEvent;
import me.BerylliumOranges.listeners.items.traits.dummyevents.PotionEffectStartEvent;
import me.BerylliumOranges.listeners.items.traits.dummyevents.PotionEffectTickEvent;
import me.BerylliumOranges.listeners.items.traits.traits.ItemTrait;

public class PotionEffectTicker implements Listener, Serializable {
	private static final long serialVersionUID = -2888714507552446269L;
	private ItemTrait potionEffect;
	private boolean running = false;
	private int potionDurationInTicks;
	private int ticksElapsed = 0;

	public PotionEffectTicker(ItemTrait potionEffect, int potionDuration) {
		this.potionEffect = potionEffect;
		this.potionDurationInTicks = potionDuration * 20;
	}

	@EventHandler
	public void onTick(TickEvent e) {
		if (potionEffect.getConsumer() == null)
			return;

		if (running && ticksElapsed - 1 < potionDurationInTicks) {
			handlePotionEffectTick();
			ticksElapsed++;
			if (ticksElapsed >= potionDurationInTicks) {
				endPotion();
			}
		}
	}

	public void startPotion() {
		startTimer();
		handlePotionEffectStart();
	}

	public void startTimer() {
		running = true;
	}

	public void stopTimer() {
		running = false;
	}

	public boolean isTimerRunning() {
		return running;
	}

	public void setTicksElapsed(int ticks) {
		ticksElapsed = ticks;
	}

	public int getTicksElapsed() {
		return ticksElapsed;
	}

	/** @param duration in ticks **/
	public void setDuration(int duration) {
		potionDurationInTicks = duration;
	}

	/** @return duration in ticks **/
	public int getPotionDuration() {
		return potionDurationInTicks;
	}

	public void endPotion() {
		handlePotionEffectEnd();
	}

	private void handlePotionEffectStart() {
		PotionEffectStartEvent event = new PotionEffectStartEvent(potionEffect);
		Bukkit.getServer().getPluginManager().callEvent(event);
	}

	private void handlePotionEffectTick() {
		PotionEffectTickEvent event = new PotionEffectTickEvent(potionEffect);
		Bukkit.getServer().getPluginManager().callEvent(event);
	}

	private void handlePotionEffectEnd() {
		PotionEffectEndEvent event = new PotionEffectEndEvent(potionEffect);
		Bukkit.getServer().getPluginManager().callEvent(event);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPotionEffectConsume(PotionEffectStartEvent event) {
		if (event.isCancelled())
			return;

		if (event.getPotionEffect().equals(potionEffect))
			potionEffect.handlePotionEffectStart();
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPotionEffectTick(PotionEffectTickEvent event) {
		if (event.isCancelled())
			return;
		if (event.getPotionEffect().equals(potionEffect))
			potionEffect.handlePotionEffectTick();
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPotionEffectTick(PotionEffectEndEvent event) {
		if (event.isCancelled())
			return;
		if (event.getPotionEffect().equals(potionEffect)) {
			potionEffect.handlePotionEffectEnd();
			potionEffect.handlePotionEnd();
		}
	}
}
