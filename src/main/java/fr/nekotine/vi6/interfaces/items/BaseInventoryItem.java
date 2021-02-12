package fr.nekotine.vi6.interfaces.items;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import fr.nekotine.vi6.Vi6Main;

public abstract class BaseInventoryItem implements Listener{
	protected final ItemStack item;
	protected final Vi6Main main;
	public BaseInventoryItem(Vi6Main main, ItemStack item) {
		this.main=main;
		this.item=item;
		Bukkit.getPluginManager().registerEvents(this, main);
	}
	public abstract void playerInteract(Player player);
	@EventHandler
	public void onPlayerDrop(PlayerDropItemEvent e) {
		if(e.getItemDrop().getItemStack().equals(item)) {
			e.setCancelled(true);
			playerInteract(e.getPlayer());
		}
	}
	@EventHandler
	public void onPlayerClickItem(PlayerInteractEvent e) {
		if(item.equals(e.getItem())) {
			if(e.getAction()==Action.LEFT_CLICK_AIR
					||e.getAction()==Action.LEFT_CLICK_BLOCK
					||e.getAction()==Action.RIGHT_CLICK_AIR
					||e.getAction()==Action.RIGHT_CLICK_BLOCK) {
				e.setCancelled(true);
				if(e.getPlayer().getOpenInventory().getType()==InventoryType.CRAFTING||e.getPlayer().getOpenInventory().getType()==InventoryType.CREATIVE) {
					playerInteract(e.getPlayer());
				}
			}
		}
	}
	@EventHandler
	public void inventoryClick(InventoryClickEvent e) {
		if(item.equals(e.getCurrentItem())) {
			e.setCancelled(true);
			playerInteract((Player)e.getWhoClicked());
		}
	}
}
