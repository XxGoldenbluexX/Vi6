package fr.nekotine.vi6.interfaces;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.wrappers.PlayerWrapper;

public class GameSettingsInventory extends BaseInventory{
	private final PlayerWrapper wrapper;
	public GameSettingsInventory(Vi6Main main, Player player, PlayerWrapper wrapper, Game game) {
		super(null, game, main, player);
		
		this.wrapper=wrapper;
		
		inventory = Bukkit.createInventory(player, 9*3, "Paramètres");
		for(byte index=1;index<=26;index++) {
			inventory.setItem(index, createItemStack(Material.BLACK_STAINED_GLASS,1,"",""));
		}
		inventory.setItem(0, createItemStack(Material.BARRIER,1,ChatColor.RED+"Retour",""));
		if(game.isRanked()) {
			inventory.setItem(11, createItemStack(Material.EMERALD,1,ChatColor.GREEN+"Classée",""));
		}else {
			inventory.setItem(11, createItemStack(Material.REDSTONE,1,ChatColor.RED+"Non-Classée",""));
		}
		inventory.setItem(13, createItemStack(Material.GOLD_INGOT,1,ChatColor.GOLD+"Argent",ChatColor.GOLD+""+ChatColor.UNDERLINE+game.getMoney()));
		inventory.setItem(15, createItemStack(Material.PAPER,1,ChatColor.WHITE+"Carte",ChatColor.LIGHT_PURPLE+""+ChatColor.UNDERLINE+game.getMapName()));
		player.openInventory(inventory);
	}
	@Override
	public void itemClicked(Material m) {
		switch(m) {
		case BARRIER:
			new PreparationInventory(main, player, wrapper, game);
			HandlerList.unregisterAll(this);
			return;
		case EMERALD:
			game.setRanked(false);
			inventory.setItem(11, createItemStack(Material.REDSTONE,1,ChatColor.RED+"Non-Classée",""));
			return;
		case REDSTONE:
			game.setRanked(true);
			inventory.setItem(11, createItemStack(Material.EMERALD,1,ChatColor.GREEN+"Classée",""));
			return;
		case GOLD_INGOT:
			new GameMoneyAnvil(main, game, player, wrapper);
			HandlerList.unregisterAll(this);
			return;
		case PAPER:
			//modifier la map
			HandlerList.unregisterAll(this);
			return;
		default:
			return;
		}
	}
}
