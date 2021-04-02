package fr.nekotine.vi6.interfaces.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.interfaces.inventories.WaitingInventory;
import fr.nekotine.vi6.utils.IsCreator;

public class OpenWaitingItem extends BaseInventoryItem implements Listener{
	private final Game game;
	public OpenWaitingItem(Vi6Main main, Game game) {
		super(main, IsCreator.createItemStack(Material.BEACON, 1, ChatColor.GOLD+game.getName(), ChatColor.LIGHT_PURPLE+"Intéragir pour ouvrir la partie"));
		this.game=game;
	}
	@Override
	public void playerInteract(Player player) {
		if(game.getPlayerMap().keySet().contains(player)) {
			new WaitingInventory(main, player, game);
		}else {
			player.getInventory().remove(item);
		}
	}
	public void destroy() {
		for(Player player : game.getPlayerMap().keySet()) {
			player.getInventory().remove(item);
		}
	}
	public void remove(Player player ) {
		player.getInventory().remove(item);
	}
	public void add(Player player) {
		player.getInventory().setItem(0, item);
	}
	public void give() {
		for(Player player : game.getPlayerMap().keySet()) {
			player.getInventory().setItem(0, item);
		}
	}
	public void delete() {
		destroy();
		HandlerList.unregisterAll(this);
	}
}
