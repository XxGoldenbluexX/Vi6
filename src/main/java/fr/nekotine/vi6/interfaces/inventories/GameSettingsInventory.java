package fr.nekotine.vi6.interfaces.inventories;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.events.GameEnterPreparationPhaseEvent;
import fr.nekotine.vi6.events.MapChangeEvent;
import fr.nekotine.vi6.events.MoneyChangedEvent;
import fr.nekotine.vi6.utils.IsCreator;
import net.kyori.adventure.text.Component;

public class GameSettingsInventory extends BaseSharedInventory{
	public GameSettingsInventory(Vi6Main main, Game game) {
		super(game, main);
		inventory = Bukkit.createInventory(null, 9*3, Component.text("Paramètres"));
		for(byte index=1;index<=26;index++) {
			inventory.setItem(index, IsCreator.createItemStack(Material.BLACK_STAINED_GLASS_PANE,1," ",""));
		}
		inventory.setItem(0, IsCreator.createItemStack(Material.BARRIER,1,ChatColor.RED+"Retour",""));
		if(game.isRanked()) {
			inventory.setItem(11, IsCreator.createItemStack(Material.EMERALD,1,ChatColor.GREEN+"Classée",""));
			inventory.setItem(13, IsCreator.createItemStack(Material.IRON_INGOT,1,ChatColor.RED+"Bloqué",ChatColor.LIGHT_PURPLE+""+ChatColor.UNDERLINE+game.getMoney()));
		}else {
			inventory.setItem(11, IsCreator.createItemStack(Material.REDSTONE,1,ChatColor.RED+"Non-Classée",""));
			inventory.setItem(13, IsCreator.createItemStack(Material.GOLD_INGOT,1,ChatColor.GOLD+"Argent",ChatColor.LIGHT_PURPLE+""+ChatColor.UNDERLINE+game.getMoney()));
		}
		inventory.setItem(15, IsCreator.createItemStack(Material.PAPER,1,ChatColor.WHITE+"Carte",ChatColor.LIGHT_PURPLE+""+ChatColor.UNDERLINE+game.getMapName()));
	}
	@Override
	public void itemClicked(Player player, ItemStack itm, int slot) {
		switch(itm.getType()) {
		case BARRIER:
			new WaitingInventory(main, player, game);
			break;
		case EMERALD:
			game.setRanked(false);
			inventory.setItem(11, IsCreator.createItemStack(Material.REDSTONE,1,ChatColor.RED+"Non-Classée",""));
			inventory.setItem(13, IsCreator.createItemStack(Material.GOLD_INGOT,1,ChatColor.GOLD+"Argent",ChatColor.LIGHT_PURPLE+""+ChatColor.UNDERLINE+game.getMoney()));
			break;
		case REDSTONE:
			game.setRanked(true);
			inventory.setItem(11, IsCreator.createItemStack(Material.EMERALD,1,ChatColor.GREEN+"Classée",""));
			inventory.setItem(13, IsCreator.createItemStack(Material.IRON_INGOT,1,ChatColor.RED+"Bloqué",ChatColor.LIGHT_PURPLE+""+ChatColor.UNDERLINE+game.getMoney()));
			break;
		case GOLD_INGOT:
			game.openMoney(player);
			return;
		case PAPER:
			game.openMapSelection(player);
			break;
		default:
			break;
		}
	}
	@EventHandler
	public void mapChange(MapChangeEvent e) {
		inventory.setItem(15, IsCreator.createItemStack(Material.PAPER,1,ChatColor.WHITE+"Carte",ChatColor.LIGHT_PURPLE+""+ChatColor.UNDERLINE+e.getMapName()));
	}
	@EventHandler
	public void moneyChange(MoneyChangedEvent e) {
		inventory.setItem(13, IsCreator.createItemStack(Material.GOLD_INGOT,1,ChatColor.GOLD+"Argent",ChatColor.LIGHT_PURPLE+""+ChatColor.UNDERLINE+game.getMoney()));
	}
	@EventHandler
	public void onGameStart(GameEnterPreparationPhaseEvent e) {
		if(e.getGame().equals(game)) {
			inventory.getViewers().forEach(HumanEntity::closeInventory);
		}
	}
}
