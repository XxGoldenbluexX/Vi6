package fr.nekotine.vi6.interfaces;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.nekotine.vi6.Game;
import net.wesjd.anvilgui.AnvilGUI;

public class GameMoneyAnvil {
	private final AnvilGUI.Builder gui;
	public GameMoneyAnvil(Game game) {
		
		ItemStack itemLeft = new ItemStack(Material.GOLD_INGOT);
		ItemMeta meta = itemLeft.getItemMeta();
		meta.setDisplayName(String.valueOf(game.getMoney()));
		itemLeft.setItemMeta(meta);
		
		gui = new AnvilGUI.Builder()
				.onComplete((player,text)->{
					try {
						int money = Integer.valueOf(text);
						if(money<0) {
							throw new NumberFormatException();
						}
						game.setMoney(money);
						player.sendMessage("Argent modifiée");
					}catch(NumberFormatException error) {
						game.openSettings(player);
					}
					return AnvilGUI.Response.close();
				})
				.text(String.valueOf(game.getMoney()))
				.itemLeft(itemLeft)
				.title("Argent");
	}
	public void openGUI(Player player) {
		gui.open(player);
	}
}

