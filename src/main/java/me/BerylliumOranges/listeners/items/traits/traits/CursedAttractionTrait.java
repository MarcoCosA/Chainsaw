package me.BerylliumOranges.listeners.items.traits.traits;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import me.BerylliumOranges.customEvents.TickEvent;
import net.md_5.bungee.api.ChatColor;

public class CursedAttractionTrait extends ItemTraitCursed implements Listener {

	private static final long serialVersionUID = -7709915568319277958L;

	public int stack_max = 4;
	public int damage = 1;
	public int hits = 0;

	public CursedAttractionTrait() {
		super(150);
	}

	@Override
	public String getTraitName() {
		return getTraitColor() + "Curse of Attraction";
	}

	@Override
	public String getPotionDescription() {
		return ChatColor.WHITE + "Monsters are " + getTraitColor() + "pulled " + ChatColor.WHITE + "towards you and " + getTraitColor()
				+ "touching " + ChatColor.WHITE + "them damages you";
	}

	@Override
	public String getToolDescription() {
		return ChatColor.WHITE + "Monsters are " + getTraitColor() + "pulled " + ChatColor.WHITE + "towards you";
	}

	@Override
	public void handlePotionEffectTick() {
		boolean isPlayer = getConsumer() instanceof Player;
		for (Entity ent : getConsumer().getWorld().getNearbyEntities(getConsumer().getEyeLocation(), 15, 15, 15)) {
			if (ent instanceof Monster && ent.getLocation().distance(getConsumer().getLocation()) < 15) {
				if (ent instanceof Monster && isPlayer) {
					Vector direction = ent.getLocation().toVector().subtract(getConsumer().getLocation().toVector()).normalize();
					ent.setVelocity(ent.getVelocity().add(direction.multiply(-0.01)));
				} else if (ent instanceof Player && !isPlayer) {
					Vector direction = ent.getLocation().toVector().subtract(getConsumer().getLocation().toVector()).normalize();
					ent.setVelocity(ent.getVelocity().add(direction.multiply(-0.01)));
				}
				if (ent instanceof Mob && ent.getLocation().distance(getConsumer().getLocation()) < 0.75) {
					getConsumer().damage(2, ent);
				}
			}
		}
	}

	@Override
	public ToolOption getToolOption() {
		return ToolOption.ANY;
	}

	@EventHandler
	public void onTick(TickEvent e) {
		for (World w : Bukkit.getWorlds()) {
			for (Entity possibleHolder : w.getEntities()) {
				if (possibleHolder instanceof LivingEntity) {
					LivingEntity holder = (LivingEntity) possibleHolder;
					if (entityHasTrait(holder, TraitLocation.ON_ARMOR, TraitLocation.IN_MAINHAND, TraitLocation.IN_OFFHAND)) {
						for (Entity ent : holder.getWorld().getNearbyEntities(holder.getEyeLocation(), 15, 15, 15)) {
							if (ent instanceof Monster && ent.getLocation().distance(holder.getLocation()) < 15) {
								Vector direction = ent.getLocation().toVector().subtract(holder.getLocation().toVector()).normalize();
								ent.setVelocity(ent.getVelocity().add(direction.multiply(-0.02)));
							}
						}
					}
				}
			}
		}
	}
}
