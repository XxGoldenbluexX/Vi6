
package fr.nekotine.vi6.interfaces.inventories;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.events.PlayerLeaveGameEvent;

public abstract class BaseSharedInventory implements Listener{
	
	public Inventory inventory;
	protected final Game game;
	protected final Vi6Main main;
	
	public BaseSharedInventory(Game game, Vi6Main main) {
		this.game = game;
		this.main = main;
		Bukkit.getPluginManager().registerEvents(this, main);
	}
	
	public abstract void itemClicked(Player player,ItemStack itm, int slot);
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if(inventory.equals(e.getClickedInventory())) {
			if(e.getCurrentItem()!=null) {
				e.setCancelled(true);
				itemClicked((Player)e.getWhoClicked(),e.getCurrentItem(),e.getRawSlot());
			}
		}
	}
	
	@EventHandler
	public void onGameLeave(PlayerLeaveGameEvent e) {
		if(e.getGame().equals(game)&&inventory.getViewers().contains(e.getPlayer())) {
			e.getPlayer().closeInventory();
		}
	}
	public void destroy() {
		HandlerList.unregisterAll(this);
	}
}