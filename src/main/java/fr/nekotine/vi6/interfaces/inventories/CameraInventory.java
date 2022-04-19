package fr.nekotine.vi6.interfaces.inventories;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.events.PlayerLeaveCamera;
import fr.nekotine.vi6.map.Camera;
import fr.nekotine.vi6.utils.IsCreator;
import net.kyori.adventure.text.Component;

public class CameraInventory extends BaseSharedInventory{

	public CameraInventory(Game game, Vi6Main main) {
		super(game, main);
		this.inventory = Bukkit.createInventory(null, 54,(Component) Component.text("Caméra"));
		for(int i=0; i<=5;i++) {
			inventory.setItem(i*9, IsCreator.createItemStack(Material.BLACK_STAINED_GLASS_PANE, 1, " "));
			inventory.setItem(i*9+8, IsCreator.createItemStack(Material.BLACK_STAINED_GLASS_PANE, 1, " "));
		}
		for(Camera cam : game.getMap().getCameraList()) {
			ItemStack camItem = IsCreator.createItemStack(cam.getMaterial(), 1, ChatColor.GOLD+cam.getDisplayName(), "");
			inventory.setItem(cam.getPosition(), camItem);
		}
	}

	@Override
	public void itemClicked(Player player, ItemStack itm, int slot) {
		for(Camera cam : game.getMap().getCameraList()) {
			if(cam.getPosition()==slot) {
				player.closeInventory();
				for(Camera cam2 : game.getMap().getCameraList()) {
					cam2.removeViewer(player);
				}
				cam.addViewer(player);
				return;
			}
		}
	}
	@EventHandler
	public void onCameraLeave(PlayerLeaveCamera e) {
		if(game.getPlayerMap().containsKey(e.getPlayer())) {
			e.getPlayer().openInventory(inventory);
		}
	}
	@EventHandler
	public void onCrouch(PlayerToggleSneakEvent e) {
		if(e.isSneaking() && inventory.getViewers().contains(e.getPlayer())) {
			e.getPlayer().closeInventory();
		}
	}
}
