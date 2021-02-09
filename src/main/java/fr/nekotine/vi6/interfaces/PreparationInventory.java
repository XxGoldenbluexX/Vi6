package fr.nekotine.vi6.interfaces;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.enums.Team;

public class PreparationInventory extends BasePersonalInventory{
	public PreparationInventory(Vi6Main main, Player player, Game game) {
		super(game, main, player);
		
		inventory = Bukkit.createInventory(player, 9*3, "PLACEHOLDER");
		if(game.getWrapper(player).getTeam()==Team.GARDE) {
			for(byte index=1;index<=26;index++) {
				inventory.setItem(index, createItemStack(Material.BLUE_STAINED_GLASS_PANE, 1," ", ""));
			}
			inventory.setItem(16, createItemStack(Material.BLUE_BANNER, 1, ChatColor.BLUE+"Garde", ""));
		}else {
			for(byte index=1;index<=26;index++) {
				inventory.setItem(index, createItemStack(Material.RED_STAINED_GLASS_PANE, 1, " ", ""));
			}
			inventory.setItem(16, createItemStack(Material.RED_BANNER, 1, ChatColor.RED+"Voleur", ""));
		}
		inventory.setItem(0, createItemStack(Material.ANVIL, 1, ChatColor.GRAY+"Paramètres", ""));
		inventory.setItem(10, createItemStack(Material.SUNFLOWER, 1, ChatColor.GOLD+"Lançer", ""));
		if(game.getWrapper(player).isReady()) {
			inventory.setItem(13, createItemStack(Material.EMERALD_BLOCK, 1, ChatColor.GREEN+"Prêt", ""));
		}else{
			inventory.setItem(13, createItemStack(Material.REDSTONE_BLOCK, 1, ChatColor.RED+"En Attente", ""));
		}
		player.openInventory(inventory);
	}
	@Override
	public void itemClicked(ItemStack itm) {
		switch(itm.getType()) {
		case ANVIL:
			game.openSettings(player);
			HandlerList.unregisterAll(this);
			return;
		case SUNFLOWER:
			//start game
			return;
		case EMERALD_BLOCK:
			game.getWrapper(player).setReady(false);
			inventory.setItem(13, createItemStack(Material.REDSTONE_BLOCK, 1, ChatColor.RED+"En Attente", ""));
			return;
		case REDSTONE_BLOCK:
			game.getWrapper(player).setReady(true);
			inventory.setItem(13, createItemStack(Material.EMERALD_BLOCK, 1, ChatColor.GREEN+"Prêt", ""));
			return;
		case BLUE_BANNER:
			game.getWrapper(player).changeTeam(Team.VOLEUR);
			inventory.setItem(16, createItemStack(Material.RED_BANNER, 1, ChatColor.RED+"Voleur", ""));
			for(byte index=1;index<=26;index++) {
				if(index==10||index==13||index==16) {
					continue;
				}
				inventory.setItem(index, createItemStack(Material.RED_STAINED_GLASS_PANE, 1, " ", ""));
			}
			return;
		case RED_BANNER:
			game.getWrapper(player).changeTeam(Team.GARDE);
			for(byte index=1;index<=26;index++) {
				if(index==10||index==13||index==16) {
					continue;
				}
				inventory.setItem(index, createItemStack(Material.BLUE_STAINED_GLASS_PANE, 1," ", ""));
			}
			inventory.setItem(16, createItemStack(Material.BLUE_BANNER, 1, ChatColor.BLUE+"Garde", ""));
			return;
		default:
			return;
		}
	}
}
