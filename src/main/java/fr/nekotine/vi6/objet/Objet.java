package fr.nekotine.vi6.objet;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.enums.GameState;
import fr.nekotine.vi6.enums.Team;
import fr.nekotine.vi6.events.GameEndEvent;
import fr.nekotine.vi6.events.GameTickEvent;
import fr.nekotine.vi6.events.PlayerLeaveMapEvent;

public abstract class Objet implements Listener{
	
	public final ObjetsList objet;
	protected final Game game;
	public final ItemStack itemStack;
	
	public Objet(Vi6Main main, ObjetsList objet, ItemStack itemStack, Game game) {
		this.objet = objet;
		this.game = game;
		this.itemStack = itemStack;
		ItemMeta meta = this.itemStack.getItemMeta();
		meta.getPersistentDataContainer().set(new NamespacedKey(main, game.getName()+"ObjetNBT"), PersistentDataType.INTEGER, game.getNBT());
		this.itemStack.setItemMeta(meta);
		Bukkit.getPluginManager().registerEvents(this, main);
	}
	
	public abstract void gameEnd();
	public abstract void tick();
	public abstract void leaveMap();
	public abstract void death();
	public abstract void sell();
	public abstract void action(Action action);
	public abstract void drop();

	@EventHandler
	public void onGameEnd(GameEndEvent e) {
		if(e.getGame().equals(game)) {
			gameEnd();
			for(Player player : game.getPlayerList()) {
				player.getInventory().removeItem(itemStack);
			}
			HandlerList.unregisterAll(this);
		}
	}
	@EventHandler
	public void onGameTick(GameTickEvent e) {
		if(e.getGame().equals(game)) tick();
	}
	@EventHandler
	public void onPlayerLeaveMap(PlayerLeaveMapEvent e) {
		if(e.getGame().equals(game)&&e.getPlayer().getInventory().contains(itemStack)) {
			leaveMap();
			e.getPlayer().getInventory().removeItem(itemStack);
			HandlerList.unregisterAll(this);
		}
	}
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		if(e.getEntity().getInventory().contains(itemStack)) {
			death();
			e.getEntity().getInventory().removeItem(itemStack);
			HandlerList.unregisterAll(this);
		}
	}
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		if(itemStack.equals(e.getItem())) {
			if(game.getPlayerTeam(e.getPlayer())==Team.VOLEUR){
				if(game.getState()==GameState.Ingame) {
					action(e.getAction());
				}
			}else {
				action(e.getAction());
			}
		}
	}
	@EventHandler
	public void onPlayerDrop(PlayerDropItemEvent e) {
		if(itemStack.equals(e.getItemDrop().getItemStack())) {
			e.setCancelled(true);
			if(game.getPlayerTeam(e.getPlayer())==Team.VOLEUR){
				if(game.getState()==GameState.Ingame) {
					drop();
				}
			}else {
				drop();
			}
		}
	}
	public void vendre(Player player) {
		sell();
		player.getInventory().removeItem(itemStack);
		game.removeObjet(this);
		HandlerList.unregisterAll(this);
	}
}
