package fr.nekotine.vi6.interfaces.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.enums.Team;
import fr.nekotine.vi6.utils.IsCreator;

public class OpenCheckListItem extends BaseInventoryItem{
	private final Game game;
	public OpenCheckListItem(Vi6Main main, Game game) {
		super(main, IsCreator.createItemStack(Material.BOOK, 1, 
				ChatColor.GOLD+"CheckList", ChatColor.LIGHT_PURPLE+"Ouvrez ce livre pour consulter les artéfacts"));
		this.game=game;
	}

	@Override
	public void playerInteract(Player player) {
		if(game.getPlayerMap().keySet().contains(player)) {
			if(game.getPlayerTeam(player)==Team.GARDE) {
				player.openInventory(game.getCheckListGuard().inventory);
			}else {
				player.openInventory(game.getCheckListThief().inventory);
			}
		}else {
			player.getInventory().remove(item);
		}
	}
	public void destroy() {
		for(Player player : game.getPlayerMap().keySet()) {
			player.getInventory().remove(item);
		}
	}
	public void give() {
		for(Player player : game.getPlayerMap().keySet()) {
			player.getInventory().setItem(8, item);
		}
	}
	public void delete() {
		destroy();
		HandlerList.unregisterAll(this);
	}

}
