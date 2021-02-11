package fr.nekotine.vi6.interfaces.items;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.nekotine.vi6.Vi6Main;

public abstract class BaseInventoryItem implements Listener{
	protected ItemStack item;
	protected final Vi6Main main;
	public BaseInventoryItem(Vi6Main main) {
		this.main=main;
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
				playerInteract(e.getPlayer());
			}
		}
	}
	@EventHandler
	public void inventoryClick(InventoryClickEvent e) {
		System.out.println(e.getRawSlot());
		if(item.equals(e.getCurrentItem())) {
			e.setCancelled(true);
			playerInteract((Player)e.getWhoClicked());
		}
	}
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
}
