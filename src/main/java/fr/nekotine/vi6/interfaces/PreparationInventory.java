package fr.nekotine.vi6.interfaces;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
import fr.nekotine.vi6.enums.Team;
import fr.nekotine.vi6.events.GameStartEvent;
import fr.nekotine.vi6.wrappers.PlayerWrapper;

public class PreparationInventory implements Listener{
	private final PlayerWrapper wrapper;
	private final Vi6Main main;
	private final Player player;
	private final Game game;
	private final Inventory inventory;
	public PreparationInventory(Vi6Main main, Player player, PlayerWrapper wrapper, Game game) {
		this.main=main;
		this.wrapper=wrapper;
		this.player=player;
		this.game=game;
		
		inventory = Bukkit.createInventory(player, 9*3, "PLACEHOLDER");
		if(wrapper.getTeam()==Team.GARDE) {
			for(byte index=1;index<=26;index++) {
				inventory.setItem(index, createItemStack(Material.BLUE_STAINED_GLASS_PANE, 1,"", ""));
			}
			inventory.setItem(16, createItemStack(Material.BLUE_BANNER, 1, ChatColor.BLUE+"Garde", ""));
		}else {
			for(byte index=1;index<=26;index++) {
				inventory.setItem(index, createItemStack(Material.RED_STAINED_GLASS_PANE, 1, "", ""));
			}
			inventory.setItem(16, createItemStack(Material.RED_BANNER, 1, ChatColor.RED+"Voleur", ""));
		}
		inventory.setItem(0, createItemStack(Material.ANVIL, 1, ChatColor.GRAY+"Paramètres", ""));
		inventory.setItem(10, createItemStack(Material.SUNFLOWER, 1, ChatColor.GOLD+"Lançer", ""));
		if(wrapper.isReady()) {
			inventory.setItem(13, createItemStack(Material.EMERALD_BLOCK, 1, ChatColor.GREEN+"Prêt", ""));
		}else{
			inventory.setItem(13, createItemStack(Material.REDSTONE_BLOCK, 1, ChatColor.RED+"En Attente", ""));
		}
		player.openInventory(inventory);
	}
	public ItemStack createItemStack(Material mat, int quantity, String name, String... lore) {
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
	public void onInventoryClick(InventoryClickEvent e) {
		if(e.getInventory().equals(inventory)) {
			switch(e.getCurrentItem().getType()) {
			case ANVIL:
				//open paramètres
				HandlerList.unregisterAll(this);
				return;
			case SUNFLOWER:
				//start game
				return;
			case EMERALD_BLOCK:
				wrapper.setReady(false);
				inventory.setItem(13, createItemStack(Material.REDSTONE_BLOCK, 1, ChatColor.RED+"En Attente", ""));
				return;
			case REDSTONE_BLOCK:
				wrapper.setReady(true);
				inventory.setItem(13, createItemStack(Material.EMERALD_BLOCK, 1, ChatColor.GREEN+"Prêt", ""));
				return;
			case BLUE_BANNER:
				wrapper.changeTeam(Team.VOLEUR);
				inventory.setItem(16, createItemStack(Material.RED_BANNER, 1, ChatColor.RED+"Voleur", ""));
				return;
			case RED_BANNER:
				wrapper.changeTeam(Team.GARDE);
				inventory.setItem(16, createItemStack(Material.BLUE_BANNER, 1, ChatColor.BLUE+"Garde", ""));
				return;
			default:
				return;
			}
		}
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
}
