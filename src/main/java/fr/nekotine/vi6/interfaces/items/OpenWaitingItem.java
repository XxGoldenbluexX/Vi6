package fr.nekotine.vi6.interfaces.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.events.GameEndEvent;
import fr.nekotine.vi6.events.GameEnterPreparationPhaseEvent;
import fr.nekotine.vi6.events.PlayerJoinGameEvent;
import fr.nekotine.vi6.events.PlayerLeaveGameEvent;
import fr.nekotine.vi6.interfaces.inventories.WaitingInventory;
import fr.nekotine.vi6.utils.IsCreator;

public class OpenWaitingItem extends BaseInventoryItem implements Listener{
	private final Game game;
	public OpenWaitingItem(Vi6Main main, Game game) {
		super(main, IsCreator.createItemStack(Material.BEACON, 1, ChatColor.GOLD+game.getName(), ChatColor.LIGHT_PURPLE+"Intéragir pour ouvrir la partie"));
		this.game=game;
	}
	@EventHandler
	public void onGameStart(GameEnterPreparationPhaseEvent e) {
		if(e.getGame().equals(game)) {
			for(Player player : game.getPlayerList().keySet()) {
				player.getInventory().remove(item);
			}
			HandlerList.unregisterAll(this);
		}
	}
	@EventHandler
	public void playerJoin(PlayerJoinGameEvent e) {
		if(e.getGame().equals(game)) {
			e.getPlayer().getInventory().setItem(0, item);
		}
	}
	@EventHandler
	public void playerLeave(PlayerLeaveGameEvent e) {
		if(e.getGame().equals(game)) {
			e.getPlayer().getInventory().remove(item);
		}
	}
	@Override
	public void playerInteract(Player player) {
		if(game.getPlayerList().keySet().contains(player)) {
			new WaitingInventory(main, player, game);
		}else {
			player.getInventory().remove(item);
		}
	}
	@EventHandler
	public void gameEndEvent(GameEndEvent e) {
		if(e.getGame().equals(game)) {
			for(Player p : game.getPlayerList().keySet()) {
				p.getInventory().setItem(0, item);
			}
		}
	}
}
