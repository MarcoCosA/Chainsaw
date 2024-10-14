package me.BerylliumOranges.listeners.items.traits.traits;

import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionType;

import me.BerylliumOranges.listeners.items.traits.utils.TraitCache;
import net.md_5.bungee.api.ChatColor;

public class SoulUserGolem extends ItemTraitSoulUser {

	private static final long serialVersionUID = -7709915568319277958L;

	public SoulUserGolem() {
		super(180, 2);
	}

	@Override
	public String getTraitName() {
		return getTraitColor() + "Golem";
	}

	@Override
	public ChatColor getTraitColor() {
		return ChatColor.GRAY;
	}

	@Override
	public String getPotionDescription() {
		return ChatColor.WHITE + "TBD";
	}

	@Override
	public String getToolDescription() {
		return ChatColor.WHITE + "Launches a golem";
	}

	@Override
	public PotionType getPotionType() {
		return PotionType.STRENGTH;
	}

	@Override
	public void handlePotionEffectStart() {
		// getConsumer().addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH,
		// potionDuration, 1));
	}

	@Override
	public ToolOption getToolOption() {
		return ToolOption.WEAPON_EXCLUSIVE;
	}

	@EventHandler
	public void onRightClick(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if (e.getHand().equals(EquipmentSlot.HAND)) {
			if (TraitCache.getTraitsFromItem(e.getItem()).contains(this)) {
				if (handleSoulUseEvent(p, e.getItem())) {
					if (p.getAttackCooldown() == 1.0) {
						p.playSound(p.getEyeLocation(), Sound.BLOCK_IRON_TRAPDOOR_OPEN, 0.5F, 1.2F);
						p.playSound(p.getEyeLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.1F, 1.5F);
						p.spawnParticle(Particle.CLOUD, p.getEyeLocation().clone().add(p.getLocation().getDirection().clone().multiply(2)),
								10);
						IronGolem golem = (IronGolem) p.getWorld().spawnEntity(
								p.getEyeLocation().clone().subtract(0, 0.3, 0).add(p.getLocation().getDirection().clone()),
								EntityType.IRON_GOLEM);
						golem.setVelocity(p.getLocation().getDirection());
					}
				}
			}
		}

	}
}
