package fr.nekotine.vi6.interfaces;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.events.GameStartEvent;
import fr.nekotine.vi6.events.PlayerLeaveGameEvent;

public abstract class BaseInventory implements Listener{
	protected Inventory inventory;
	protected final Game game;
	protected final Vi6Main main;
	protected final Player player;
	public BaseInventory(Inventory inventory, Game game, Vi6Main main, Player player) {
		this.inventory=inventory;
		this.game = game;
		this.main = main;
		this.player = player;
		Bukkit.getPluginManager().registerEvents(this, main);
	}
	public abstract void itemClicked(Material m);
	
	protected ItemStack createItemStack(Material mat, int quantity, String name, String... lore) {
		ItemStack item = new ItemStack(mat,quantity);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		List<String> loreList = new ArrayList<>(); 
		for(String line : lore) {
			loreList.add(line);
		}
		meta.setLore(loreList);
		item.setItemMeta(meta);
		return item;
	}
	@EventHandler
	public void onGameStart(GameStartEvent e) {
		if(e.getGame().equals(game)) {
			player.closeInventory();
			HandlerList.unregisterAll(this);
		}
	}
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		if(e.getInventory().equals(inventory)) {
			HandlerList.unregisterAll(this);
		}
	}
	@EventHandler
	public void onGameLeave(PlayerLeaveGameEvent e) {
		if(e.getPlayer().equals(player)&&e.getGame().equals(game)) {
			player.closeInventory();
			HandlerList.unregisterAll(this);
		}
	}
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if(e.getClickedInventory().equals(inventory)) {
			e.setCancelled(true);
			itemClicked(e.getCurrentItem().getType());
		}
	}
}
