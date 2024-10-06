package me.BerylliumOranges.listeners.items.traits.utils;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import me.BerylliumOranges.customEvents.TickEvent;
import me.BerylliumOranges.listeners.items.traits.dummyevents.PotionEffectEndEvent;
import me.BerylliumOranges.listeners.items.traits.dummyevents.PotionEffectStartEvent;
import me.BerylliumOranges.listeners.items.traits.dummyevents.PotionEffectTickEvent;
import me.BerylliumOranges.listeners.items.traits.traits.ItemTrait;

public class PotionEffectTicker implements Listener {
	private ItemTrait potionEffect;
	private boolean running = false;
	private int potionDuration;
	private int timeElapsed;

	public PotionEffectTicker(ItemTrait potionEffect, int potionDuration) {
		this.potionEffect = potionEffect;
		this.potionDuration = potionDuration;
	}

	@EventHandler
	public void onTick(TickEvent e) {
		if (running && timeElapsed - 1 < potionDuration) {
			handlePotionEffectTick();
			timeElapsed++;
			if (timeElapsed >= potionDuration) {
				running = false;
				handlePotionEffectEnd();
			}
		}
	}

	public void start() {
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

	public void setTimeElapsed(int time) {
		timeElapsed = time;
	}

	public void setDuration(int duration) {
		potionDuration = duration;
	}

	public int getPotionDuration() {
		return potionDuration;
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
		potionEffect.handlePotionEffectStart();
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPotionEffectTick(PotionEffectTickEvent event) {
		if (event.isCancelled())
			return;
		potionEffect.handlePotionEffectTick();
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPotionEffectTick(PotionEffectEndEvent event) {
		if (event.isCancelled())
			return;
		potionEffect.handlePotionEffectEnd();
	}
}
