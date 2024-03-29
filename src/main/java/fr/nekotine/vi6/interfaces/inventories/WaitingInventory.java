package fr.nekotine.vi6.interfaces.inventories;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.enums.Team;
import fr.nekotine.vi6.events.GameEnterPreparationPhaseEvent;
import fr.nekotine.vi6.utils.IsCreator;
import fr.nekotine.vi6.utils.MessageFormater;
import fr.nekotine.vi6.utils.Vi6Sound;
import fr.nekotine.vi6.yml.DisplayTexts;
import net.kyori.adventure.text.Component;

public class WaitingInventory extends BasePersonalInventory{
	public WaitingInventory(Vi6Main main, Player player, Game game) {
		super(game, main, player);
		
		inventory = Bukkit.createInventory(player, 9*3, Component.text(game.getName()));
		if(game.getWrapper(player).getTeam()==Team.GARDE) {
			for(byte index=1;index<=26;index++) {
				inventory.setItem(index, IsCreator.createItemStack(Material.BLUE_STAINED_GLASS_PANE, 1," ", ""));
			}
			inventory.setItem(16, IsCreator.createItemStack(Material.BLUE_BANNER, 1, ChatColor.BLUE+"Garde", ""));
		}else {
			for(byte index=1;index<=26;index++) {
				inventory.setItem(index, IsCreator.createItemStack(Material.RED_STAINED_GLASS_PANE, 1, " ", ""));
			}
			inventory.setItem(16, IsCreator.createItemStack(Material.RED_BANNER, 1, ChatColor.RED+"Voleur", ""));
		}
		inventory.setItem(0, IsCreator.createItemStack(Material.ANVIL, 1, ChatColor.GRAY+"Paramètres", ""));
		inventory.setItem(10, IsCreator.createItemStack(Material.SUNFLOWER, 1, ChatColor.GOLD+"Lancer", ""));
		if(game.getWrapper(player).isReady()) {
			inventory.setItem(13, IsCreator.createItemStack(Material.EMERALD_BLOCK, 1, ChatColor.GREEN+"Prêt", ""));
		}else{
			inventory.setItem(13, IsCreator.createItemStack(Material.REDSTONE_BLOCK, 1, ChatColor.RED+"En attente", ""));
		}
		player.openInventory(inventory);
	}
	@Override
	public void itemClicked(ItemStack itm, int slot) {
		switch(itm.getType()) {
		case ANVIL:
			game.openSettings(player);
			HandlerList.unregisterAll(this);
			break;
		case SUNFLOWER:
			game.enterPreparationPhase();
			break;
		case EMERALD_BLOCK:
			game.setReady(player, false);
			inventory.setItem(13, IsCreator.createItemStack(Material.REDSTONE_BLOCK, 1, ChatColor.RED+"En Attente", ""));
			break;
		case REDSTONE_BLOCK:
			game.setReady(player, true);
			inventory.setItem(13, IsCreator.createItemStack(Material.EMERALD_BLOCK, 1, ChatColor.GREEN+"Prêt", ""));
			break;
		case BLUE_BANNER:
			if(!game.getWrapper(player).isReady()) {
				game.getWrapper(player).changeTeam(Team.VOLEUR);
				inventory.setItem(16, IsCreator.createItemStack(Material.RED_BANNER, 1, ChatColor.RED+"Voleur", ""));
				for(byte index=1;index<=26;index++) {
					if(index==10||index==13||index==16) {
						continue;
					}
					inventory.setItem(index, IsCreator.createItemStack(Material.RED_STAINED_GLASS_PANE, 1, " ", ""));
				}
			}else {
				Vi6Sound.ERROR.playForPlayer(player);
				player.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("game_shouldBeUnready")));
			}
			break;
		case RED_BANNER:
			if(game.getWrapper(player).isReady()) return;
			game.getWrapper(player).changeTeam(Team.GARDE);
			for(byte index=1;index<=26;index++) {
				if(index==10||index==13||index==16) {
					continue;
				}
				inventory.setItem(index, IsCreator.createItemStack(Material.BLUE_STAINED_GLASS_PANE, 1," ", ""));
			}
			inventory.setItem(16, IsCreator.createItemStack(Material.BLUE_BANNER, 1, ChatColor.BLUE+"Garde", ""));
			break;
		default:
			break;
		}
	}
	@EventHandler
	public void onGameStart(GameEnterPreparationPhaseEvent e) {
		if(e.getGame().equals(game)) {
			player.closeInventory();
			HandlerList.unregisterAll(this);
		}
	}
}
