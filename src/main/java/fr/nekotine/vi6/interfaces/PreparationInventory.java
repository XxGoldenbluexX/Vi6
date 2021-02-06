package fr.nekotine.vi6.interfaces;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.enums.Team;
import fr.nekotine.vi6.wrappers.PlayerWrapper;

public class PreparationInventory extends BaseInventory{
	private final PlayerWrapper wrapper;
	public PreparationInventory(Vi6Main main, Player player, PlayerWrapper wrapper, Game game) {
		super(null, game, main, player);
		
		this.wrapper=wrapper;
		
		inventory = Bukkit.createInventory(player, 9*3, "PLACEHOLDER");
		if(wrapper.getTeam()==Team.GARDE) {
			for(byte index=1;index<=26;index++) {
				inventory.setItem(index, createItemStack(Material.BLUE_STAINED_GLASS_PANE, 1,"", ""));
			}
			inventory.setItem(16, createItemStack(Material.BLUE_BANNER, 1, ChatColor.BLUE+"Garde", ""));
		}else {
			for(byte index=1;index<=26;index++) {
				inventory.setItem(index, createItemStack(Material.RED_STAINED_GLASS_PANE, 1, "", ""));
			}
			inventory.setItem(16, createItemStack(Material.RED_BANNER, 1, ChatColor.RED+"Voleur", ""));
		}
		inventory.setItem(0, createItemStack(Material.ANVIL, 1, ChatColor.GRAY+"Paramètres", ""));
		inventory.setItem(10, createItemStack(Material.SUNFLOWER, 1, ChatColor.GOLD+"Lançer", ""));
		if(wrapper.isReady()) {
			inventory.setItem(13, createItemStack(Material.EMERALD_BLOCK, 1, ChatColor.GREEN+"Prêt", ""));
		}else{
			inventory.setItem(13, createItemStack(Material.REDSTONE_BLOCK, 1, ChatColor.RED+"En Attente", ""));
		}
		player.openInventory(inventory);
	}
	@Override
	public void itemClicked(Material m) {
		switch(m) {
		case ANVIL:
			new GameSettingsInventory(main, player, wrapper, game);
			HandlerList.unregisterAll(this);
			return;
		case SUNFLOWER:
			//start game
			return;
		case EMERALD_BLOCK:
			wrapper.setReady(false);
			inventory.setItem(13, createItemStack(Material.REDSTONE_BLOCK, 1, ChatColor.RED+"En Attente", ""));
			return;
		case REDSTONE_BLOCK:
			wrapper.setReady(true);
			inventory.setItem(13, createItemStack(Material.EMERALD_BLOCK, 1, ChatColor.GREEN+"Prêt", ""));
			return;
		case BLUE_BANNER:
			wrapper.changeTeam(Team.VOLEUR);
			inventory.setItem(16, createItemStack(Material.RED_BANNER, 1, ChatColor.RED+"Voleur", ""));
			return;
		case RED_BANNER:
			wrapper.changeTeam(Team.GARDE);
			inventory.setItem(16, createItemStack(Material.BLUE_BANNER, 1, ChatColor.BLUE+"Garde", ""));
			return;
		default:
			return;
		}
	}
}
