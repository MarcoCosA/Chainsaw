package me.BerylliumOranges.listeners.items.traits.globallisteners;

import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import me.BerylliumOranges.listeners.items.traits.dummyevents.DummyHealEvent;
import me.BerylliumOranges.listeners.items.traits.traits.ItemTrait;
import me.BerylliumOranges.listeners.items.traits.traits.ItemTraitSoulHolder;
import me.BerylliumOranges.listeners.items.traits.utils.TraitCache;
import me.BerylliumOranges.listeners.items.traits.utils.TraitOperation;
import me.BerylliumOranges.main.PluginMain;
import net.md_5.bungee.api.ChatColor;

public class GlobalTraitListener implements Listener {

	public GlobalTraitListener() {
		PluginMain.getInstance().getServer().getPluginManager().registerEvents(this, PluginMain.getInstance());
	}

	@EventHandler
	public void potionDrink(PlayerItemConsumeEvent e) {
		if (TraitCache.hasItemId(e.getItem())) {
			List<ItemTrait> traits = TraitCache.getTraitsFromItem(e.getItem());

			boolean found = false;
			for (ItemTrait trait : traits) {
				if (trait.handlePotionConsumption(e.getPlayer()))
					found = true;
			}
			if (found)
				e.getPlayer().getInventory().removeItem(e.getItem());
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onDeath(EntityDeathEvent e) {
		for (ItemTrait t : TraitCache.getTraits()) {
			if (t.getConsumer() != null && t.getConsumer().equals(e.getEntity())) {
				t.handlePotionEnd();
			}
		}

		if (e.getEntity().getKiller() != null) {
			Player p = e.getEntity().getKiller();
			for (Entry<ItemStack, List<ItemTrait>> entry : TraitCache.getItemTraitMapFromEntity(p).entrySet()) {
				for (ItemTrait t : entry.getValue()) {
					if (t instanceof ItemTraitSoulHolder) {
						ItemTraitSoulHolder h = (ItemTraitSoulHolder) t;
						if (h.handleSoulAbsorption(e.getEntity())) {
							p.updateInventory();
							break;
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onHit(EntityDamageByEntityEvent e) {

		if ((e.getDamager() instanceof LivingEntity || e.getDamager() instanceof Projectile) && e.getEntity() instanceof LivingEntity) {
			LivingEntity victim = (LivingEntity) e.getEntity();
			LivingEntity damager = null;
			if (e.getDamager() instanceof LivingEntity)
				damager = (LivingEntity) e.getDamager();
			else if (((Projectile) e.getDamager()).getShooter() instanceof LivingEntity)
				damager = (LivingEntity) ((Projectile) e.getDamager()).getShooter();
			else
				return;

			TraitOperation op = new TraitOperation(e, damager, victim);
			op.processOperation();
		}

		e.getDamager().sendMessage(ChatColor.RED + "[" + ChatColor.DARK_RED + "" + e.getDamage() + ChatColor.WHITE + " damage, "
				+ ChatColor.DARK_RED + e.getFinalDamage() + ChatColor.RED + " final damage DEALT]");
		e.getEntity().sendMessage(ChatColor.RED + "[" + ChatColor.DARK_RED + "" + e.getDamage() + ChatColor.WHITE + " damage, "
				+ ChatColor.DARK_RED + e.getFinalDamage() + ChatColor.RED + " final damage RECIEVED]");
	}

	@EventHandler
	public void onHeal(EntityRegainHealthEvent e) {
		if (e.getEntity() instanceof LivingEntity) {
			LivingEntity liv = (LivingEntity) e.getEntity();
			TraitOperation op = new TraitOperation(new DummyHealEvent(e), liv, null);
			op.processOperation();
		}
	}

	@EventHandler
	public void onClick(PlayerInteractEvent e) {
		TraitOperation op = new TraitOperation(e, e.getPlayer(), null);
		op.processOperation();
	}

//
//	@EventHandler
//	public void onPlayerAttack(EntityDamageByEntityEvent event) {
//		if (event.getDamager() instanceof Player) {
//			Player player = (Player) event.getDamager();
//			ItemStack item = player.getInventory().getItemInMainHand();
//			List<ItemTrait> traits = TraitCache.getTraitsFromItem(item);
//			for (ItemTrait trait : traits) {
//				// Execute code based on the trait
//				trait.executeTraitAction(player, event); // You'd need to add this method to your traits
//			}
//		}
//	}
//	
	public void playSpiralEffect(Location loc, Particle particle, double radius, long duration) {
		double increment = (2 * Math.PI) / 30;

		new BukkitRunnable() {
			double t = 0;
			double count = 0;

			@Override
			public void run() {
				count++;
				t += increment + t / 20.0;
				double x = radius * Math.cos(t);
				double y = t / 8.0;
				double z = radius * Math.sin(t);

				loc.getWorld().spawnParticle(particle, loc.clone().add(x, y, z), 0);

				if (count > duration) {
					this.cancel();
				}
			}
		}.runTaskTimer(PluginMain.getInstance(), 0, 0); // Start immediately and
														// repeat every tick
	}

}
