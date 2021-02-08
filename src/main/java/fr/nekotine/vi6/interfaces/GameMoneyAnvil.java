package fr.nekotine.vi6.interfaces;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.events.GameIsRankedChangeEvent;

public class GameMoneyAnvil extends BaseSharedInventory implements Listener{
	public GameMoneyAnvil(Vi6Main main, Game game) {
		super(game, main);
		inventory = Bukkit.createInventory(null, InventoryType.ANVIL);
		((AnvilInventory)inventory).setFirstItem(createItemStack(Material.GOLD_INGOT,1, ChatColor.GOLD+""+ChatColor.UNDERLINE+String.valueOf(game.getMoney()),""));
		((AnvilInventory)inventory).setSecondItem(createItemStack(Material.BARRIER, 1, "", ""));
		((AnvilInventory)inventory).setResult(createItemStack(Material.GOLD_INGOT,1, ChatColor.GOLD+""+ChatColor.UNDERLINE+String.valueOf(game.getMoney()),""));
	}
	@EventHandler
	public void onAnvilChange(PrepareAnvilEvent e) {
		if(e.getInventory().equals(inventory)) {
			((AnvilInventory)inventory).setResult(createItemStack(Material.GOLD_INGOT,1, ChatColor.GOLD+""+ChatColor.UNDERLINE+((AnvilInventory)inventory).getRenameText(),""));
		}
	}
	@EventHandler
	public void gameIsRankedChange(GameIsRankedChangeEvent e) {
		if(e.getGame().equals(game)&& game.isRanked()) {
			for(HumanEntity p : inventory.getViewers()) {
				game.openSettings((Player)p);
			}
		}
	}
	@Override
	public void itemClicked(Player player, ItemStack itm) {
		if(itm.equals(((AnvilInventory)inventory).getResult())) {
			try {
				int money = Integer.valueOf(((AnvilInventory)inventory).getRenameText());
				if(money<0) {
					throw new NumberFormatException();
				}
				game.setMoney(money);
				for(HumanEntity p : inventory.getViewers()) {
					game.openSettings((Player)p);
				}
			} catch(NumberFormatException error) {
				game.openSettings((player));
			}
		}
	}
}
