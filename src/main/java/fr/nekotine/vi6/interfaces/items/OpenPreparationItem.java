package fr.nekotine.vi6.interfaces.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.events.GameEnterInGamePhaseEvent;
import fr.nekotine.vi6.events.GameEnterPreparationPhaseEvent;
import fr.nekotine.vi6.interfaces.inventories.PreparationInventory;
import fr.nekotine.vi6.utils.IsCreator;

public class OpenPreparationItem extends BaseInventoryItem{
	private final Game game;
	public OpenPreparationItem(Vi6Main main, Game game) {
		super(main, IsCreator.createItemStack(Material.NETHERITE_INGOT, 1, ChatColor.GOLD+"Magasin", ChatColor.LIGHT_PURPLE+"Intéragir pour ouvrir le magasin"));
		this.game = game;
	}
	@Override
	public void playerInteract(Player player) {
		if(game.getPlayerList().contains(player)) {
			new PreparationInventory(main, game, player, 1);
		}else {
			player.getInventory().remove(item);
		}
	}
	@EventHandler
	public void onGameStart(GameEnterInGamePhaseEvent e) {
		if(e.getGame().equals(game)) {
			for(Player player : game.getPlayerList()) {
				player.getInventory().remove(item);
			}
			HandlerList.unregisterAll(this);
		}
	}
	@EventHandler
	public void onGameStart(GameEnterPreparationPhaseEvent e) {
		if(e.getGame().equals(game)) {
			for(Player player : game.getPlayerList()) {
				player.getInventory().setItem(8, item);
			}
		}
	}
}
