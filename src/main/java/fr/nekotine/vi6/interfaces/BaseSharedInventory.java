package fr.nekotine.vi6.interfaces;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.events.GameStartEvent;
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
	
	public abstract void itemClicked(Player player,ItemStack itm);
	
	protected ItemStack createItemStack(Material mat, int quantity, String name, String... lore) {
		ItemStack item = new ItemStack(mat,quantity);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		List<String> loreList = new ArrayList<>(); 
		for(String line : lore) {
			if(line!="") {
				loreList.add(line);
			}
		}
		meta.setLore(loreList);
		item.setItemMeta(meta);
		return item;
	}
	
	@EventHandler
	public void onGameStart(GameStartEvent e) {
		if(e.getGame().equals(game)) {
			inventory.getViewers().forEach(HumanEntity::closeInventory);
			HandlerList.unregisterAll(this);
		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if(e.getInventory().equals(inventory)) {
			if(e.getCurrentItem()!=null) {
				e.setCancelled(true);
				itemClicked((Player)e.getWhoClicked(),e.getCurrentItem());
			}
		}
	}
	
	@EventHandler
	public void onGameLeave(PlayerLeaveGameEvent e) {
		if(e.getGame().equals(game)&&inventory.getViewers().contains(e.getPlayer())) {
			e.getPlayer().closeInventory();
		}
	}
}