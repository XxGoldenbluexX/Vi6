package fr.nekotine.vi6.interfaces;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.events.GameStartEvent;

public class OpenPreparationItem implements Listener{
	private final Player player;
	private final ItemStack itm;
	private final Game game;
	private final Vi6Main main;
	public OpenPreparationItem(Vi6Main main, Game game, Player player) {
		this.main=main;
		this.game=game;
		this.player = player;
		itm = new ItemStack(Material.ANVIL);
		ItemMeta meta = itm.getItemMeta();
		meta.setDisplayName("Partie");
		itm.setItemMeta(meta);
		player.getInventory().setItem(0, itm);
	}
	@EventHandler
	public void onDrop(PlayerDropItemEvent e) {
		if(e.getItemDrop().getItemStack().equals(itm)) {
			e.setCancelled(true);
			new PreparationInventory(main, player, game);
		}
	}
	@EventHandler
	public void onGameStart(GameStartEvent e) {
		if(e.getGame().equals(game)) {
			player.getInventory().remove(itm);
			HandlerList.unregisterAll(this);
		}
	}
}
