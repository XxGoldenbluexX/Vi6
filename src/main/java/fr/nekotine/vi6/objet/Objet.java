package fr.nekotine.vi6.objet;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.events.GameEndEvent;
import fr.nekotine.vi6.events.GameStartEvent;
import fr.nekotine.vi6.events.GameTickEvent;
import fr.nekotine.vi6.events.PlayerLeaveMapEvent;
import fr.nekotine.vi6.events.PlayerSellObjetEvent;

public abstract class Objet implements Listener{
	
	protected final ObjetsList objet;
	protected final Game game;
	protected final ItemStack itemStack;
	
	public Objet(ObjetsList objet, ItemStack itemStack, Game game) {
		this.objet = objet;
		this.game = game;
		this.itemStack = itemStack;
	}
	
	public abstract void gameStart();
	public abstract void gameEnd();
	public abstract void tick();
	public abstract void leaveMap();
	public abstract void death();
	public abstract void action(Action action);
	public abstract void sell();
	
	@EventHandler
	public void onGameStart(GameStartEvent e) {
		if(e.getGame().equals(game)) gameStart();
	}
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
		if(e.getItem().equals(itemStack)) action(e.getAction());
	}
	@EventHandler
	public void playerSellObjet(PlayerSellObjetEvent e) {
		if(e.getObjet().equals(objet)) {
			sell();
			e.getPlayer().getInventory().removeItem(itemStack);
			HandlerList.unregisterAll(this);
		}
	}
}
