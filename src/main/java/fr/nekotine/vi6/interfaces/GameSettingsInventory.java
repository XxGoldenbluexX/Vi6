package fr.nekotine.vi6.interfaces;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.events.GameIsRankedChangeEvent;

public class GameSettingsInventory extends BaseSharedInventory{
	public GameSettingsInventory(Vi6Main main, Game game) {
		super(game, main);
		
		inventory = Bukkit.createInventory(null, 9*3, "Param�tres");
		for(byte index=1;index<=26;index++) {
			inventory.setItem(index, createItemStack(Material.BLACK_STAINED_GLASS,1,"",""));
		}
		inventory.setItem(0, createItemStack(Material.BARRIER,1,ChatColor.RED+"Retour",""));
		if(game.isRanked()) {
			inventory.setItem(11, createItemStack(Material.EMERALD,1,ChatColor.GREEN+"Class�e",""));
			inventory.setItem(13, createItemStack(Material.TRIPWIRE_HOOK,1,ChatColor.RED+"Bloqu�",ChatColor.GOLD+""+ChatColor.UNDERLINE+game.getMoney()));
		}else {
			inventory.setItem(11, createItemStack(Material.REDSTONE,1,ChatColor.RED+"Non-Class�e",""));
			inventory.setItem(13, createItemStack(Material.GOLD_INGOT,1,ChatColor.GOLD+"Argent",ChatColor.GOLD+""+ChatColor.UNDERLINE+game.getMoney()));
		}
		inventory.setItem(15, createItemStack(Material.PAPER,1,ChatColor.WHITE+"Carte",ChatColor.LIGHT_PURPLE+""+ChatColor.UNDERLINE+game.getMapName()));
	}
	@Override
	public void itemClicked(Player player, ItemStack itm) {
		switch(itm.getType()) {
		case BARRIER:
			new PreparationInventory(main, player, game);
			return;
		case EMERALD:
			game.setRanked(false);
			Bukkit.getPluginManager().callEvent(new GameIsRankedChangeEvent(game));
			inventory.setItem(11, createItemStack(Material.REDSTONE,1,ChatColor.RED+"Non-Class�e",""));
			inventory.setItem(13, createItemStack(Material.GOLD_INGOT,1,ChatColor.GOLD+"Argent",ChatColor.GOLD+""+ChatColor.UNDERLINE+game.getMoney()));
			return;
		case REDSTONE:
			game.setRanked(true);
			Bukkit.getPluginManager().callEvent(new GameIsRankedChangeEvent(game));
			inventory.setItem(11, createItemStack(Material.EMERALD,1,ChatColor.GREEN+"Class�e",""));
			inventory.setItem(13, createItemStack(Material.TRIPWIRE_HOOK,1,ChatColor.RED+"Bloqu�",ChatColor.GOLD+""+ChatColor.UNDERLINE+game.getMoney()));
			return;
		case GOLD_INGOT:
			game.openMoney(player);
			return;
		case PAPER:
			game.openMapSelection(player);
			return;
		default:
			return;
		}
	}
}
