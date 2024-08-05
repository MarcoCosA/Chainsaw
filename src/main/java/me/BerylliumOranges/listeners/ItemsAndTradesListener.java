package me.BerylliumOranges.listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import me.BerylliumOranges.listeners.items.traits.utils.TraitCache;
import me.BerylliumOranges.main.PluginMain;

public class ItemsAndTradesListener implements Listener {
	public ItemsAndTradesListener() {
		PluginMain.getInstance().getServer().getPluginManager().registerEvents(this, PluginMain.getInstance());
	}

	@EventHandler
	public void rightClick(PlayerInteractEntityEvent e) {
		Bukkit.broadcastMessage("Here");
		ItemStack item = e.getPlayer().getEquipment().getItem(EquipmentSlot.HAND);
		if (e.getHand().equals(EquipmentSlot.HAND) && e.getPlayer().getGameMode().equals(GameMode.CREATIVE) && item != null) {
			if (e.getRightClicked().getType().equals(EntityType.ZOMBIE)) {
				Zombie z = (Zombie) e.getRightClicked();
				
				if(TraitCache.hasItemId(item))
				
				z.getEquipment().setItemInHand(item);
				
				
			}
		}
	}

//
//	@EventHandler
//	public void potionDrink(PlayerItemConsumeEvent e) {
//		if (ItemBuilder.isTraitPotion(e.getItem())) {
//			ArrayList<PurityItemAbstract> traits = ItemBuilder.getAllItemTraits(e.getItem());
//			for (PurityItemAbstract p : traits) {
//				PurityItemAbstract.addPotionTrait(e.getPlayer(), p);
//			}
//			e.setCancelled(true);
//			e.getPlayer().getInventory().removeItem(e.getItem());
//		}
//	}
//
//	@EventHandler
//	public void onWanderingTraderSpawn(EntitySpawnEvent event) {
//		if (event.getEntityType() == EntityType.WANDERING_TRADER) {
//			WanderingTrader trader = (WanderingTrader) event.getEntity();
//			List<MerchantRecipe> trades = new ArrayList<>(trader.getRecipes());
//
//			if (Math.random() > 0.25) {
//				MerchantRecipe recipe = new MerchantRecipe(PurityItemAbstract.getTraitInstance(RandomTrait.TRAIT_ID).getPotionItem(), 1);
//				recipe.addIngredient(new ItemStack(Material.EMERALD, (int) (32 + Math.random() * 32)));
//				recipe.addIngredient(new ItemStack(Material.DIAMOND, (int) (16 + Math.random() * 16)));
//				recipe.setMaxUses(2);
//				trades.set((int) (Math.random() * trades.size()), recipe);
//
//			}
//			if (Math.random() > 0.80) {
//				MerchantRecipe recipe = new MerchantRecipe(PurityItemAbstract.getTraitInstance(FreeTraitSlot.TRAIT_ID).getPotionItem(), 1);
//				recipe.addIngredient(new ItemStack(Material.EMERALD, (int) (32 + Math.random() * 32)));
//				recipe.addIngredient(new ItemStack(Material.NETHERITE_SCRAP, (int) (2 + Math.random() * 2)));
//				recipe.setMaxUses(2);
//				trades.set((int) (Math.random() * trades.size()), recipe);
//			}
//
//			MerchantRecipe recipe = new MerchantRecipe(BossAbstract.allDescriptions.get((int) (Math.random() * BossAbstract.allDescriptions.size())),
//					1);
//			recipe.addIngredient(new ItemStack(Material.EMERALD, 1));
//			recipe.addIngredient(new ItemStack(Material.PAPER, 1));
//			recipe.setMaxUses(100);
//			trades.add(recipe);
//
//			trader.setRecipes(trades);
//		}
//	}
}