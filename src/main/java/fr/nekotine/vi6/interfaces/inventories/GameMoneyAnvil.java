package fr.nekotine.vi6.interfaces.inventories;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.events.IsRankedChangeEvent;
import net.wesjd.anvilgui.AnvilGUI;

public class GameMoneyAnvil implements Listener{
	private final Game game;
	private final Player player;
	public GameMoneyAnvil(Vi6Main main, Game game, Player Player) {
		this.game = game;
		this.player = Player;
		ItemStack itemLeft = new ItemStack(Material.GOLD_INGOT);
		ItemMeta meta = itemLeft.getItemMeta();
		meta.setDisplayName(String.valueOf(game.getMoney()));
		itemLeft.setItemMeta(meta);
		new AnvilGUI.Builder()
			.onLeftInputClick(player->game.openSettings(player))
			.onComplete((player,text)->{
				try {
					int money = Integer.valueOf(text);
					if(money<0) {
						throw new NumberFormatException();
					}
					game.setMoney(money);
					game.openSettings(player);
					AnvilGUI.Response.close();
					HandlerList.unregisterAll(this);
					}catch(NumberFormatException error) {
						game.openSettings(player);
					}
				return AnvilGUI.Response.close();
			})
			.text(String.valueOf(game.getMoney()))
			.itemLeft(itemLeft)
			.title("Argent")
			.plugin(main)
			.open(Player);
		Bukkit.getPluginManager().registerEvents(this, main);
	}
	@EventHandler
	public void isRankedChange(IsRankedChangeEvent e) {
		if(e.isRanked() && e.getGame().equals(game)) {
			game.openSettings(player);
			AnvilGUI.Response.close();
			HandlerList.unregisterAll(this);
		}
	}
}

