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
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.events.GameMoneyChangeEvent;
import fr.nekotine.vi6.events.PlayerLeaveGameEvent;
import fr.nekotine.vi6.wrappers.PlayerWrapper;

public class GameMoneyAnvil implements Listener{
	private final Vi6Main main;
	private final AnvilInventory anvilInv;
	private final Player player;
	private final PlayerWrapper wrapper;
	private final Game game;
	public GameMoneyAnvil(Vi6Main main, Game game, Player player, PlayerWrapper wrapper) {
		this.main = main;
		this.player=player;
		this.game=game;
		this.wrapper=wrapper;
		anvilInv = (AnvilInventory)Bukkit.createInventory(player, InventoryType.ANVIL);
		anvilInv.setFirstItem(createItemStack(Material.GOLD_INGOT,1, ChatColor.GOLD+""+ChatColor.UNDERLINE+String.valueOf(game.getMoney()),""));
		anvilInv.setSecondItem(createItemStack(Material.BARRIER, 1, "", ""));
		anvilInv.setResult(createItemStack(Material.GOLD_INGOT,1, ChatColor.GOLD+""+ChatColor.UNDERLINE+String.valueOf(game.getMoney()),""));
		player.openInventory(anvilInv);
		Bukkit.getPluginManager().registerEvents(this, main);
	}
	@EventHandler
	public void onAnvilChange(PrepareAnvilEvent e) {
		if(e.getInventory().equals(anvilInv)) {
			anvilInv.setResult(createItemStack(Material.GOLD_INGOT,1, ChatColor.GOLD+""+ChatColor.UNDERLINE+anvilInv.getRenameText(),""));
		}
	}
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if(e.getWhoClicked().equals(player)) {
			if(e.getCurrentItem().equals(anvilInv.getResult())) {
				try {
					int money = Integer.valueOf(anvilInv.getResult().getItemMeta().getDisplayName());
					if(money<0) {
						throw new NumberFormatException();
					}
					game.setMoney(money);
					Bukkit.getPluginManager().callEvent(new GameMoneyChangeEvent(game));
				} catch(NumberFormatException error) {
				}
				new GameSettingsInventory(main, player, wrapper, game);
				HandlerList.unregisterAll(this);
			}
		}
	}
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		if(e.getInventory().equals(anvilInv)) {
			new GameSettingsInventory(main, player, wrapper, game);
			HandlerList.unregisterAll(this);
		}
	}
	@EventHandler
	public void onMoneyChange(GameMoneyChangeEvent e) {
		if(e.getGame().equals(game)) {
			new GameSettingsInventory(main, player, wrapper, game);
			HandlerList.unregisterAll(this);
		}
	}
	@EventHandler
	public void onGameLeave(PlayerLeaveGameEvent e) {
		if(e.getGame().equals(game) && e.getPlayer().equals(player)) {
			player.closeInventory();
			HandlerList.unregisterAll(this);
		}
	}
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
}
