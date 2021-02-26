package fr.nekotine.vi6.objet.utils;

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
import fr.nekotine.vi6.objet.ObjetsList;
import fr.nekotine.vi6.objet.ObjetsSkins;

public abstract class Objet implements Listener{
	
	protected final ObjetsList objet;
	protected final ObjetsSkins skin;
	protected final Game game;
	protected final ItemStack itemStack;
	
	public Objet(Vi6Main main, ObjetsList objet, ObjetsSkins skin, ItemStack itemStack, Game game, Player player) {
		this.objet = objet;
		this.skin=skin;
		this.game = game;
		this.itemStack = itemStack;
		ItemMeta meta = this.itemStack.getItemMeta();
		meta.getPersistentDataContainer().set(new NamespacedKey(main, game.getName()+"ObjetNBT"), PersistentDataType.INTEGER, game.getNBT());
		this.itemStack.setItemMeta(meta);
		player.getInventory().addItem(itemStack);
		Bukkit.getPluginManager().registerEvents(this, main);
	}
	
	public abstract void gameEnd();
	public abstract void tick();
	public abstract void leaveMap(Player holder);
	public abstract void death(Player holder);
	public abstract void sell(Player holder);
	public abstract void action(Action action, Player holder);
	public abstract void drop(Player holder);

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
	public void onPlayerDeath(PlayerDeathEvent e) {
		if(e.getEntity().getInventory().contains(itemStack)) {
			death(e.getEntity());
			e.getEntity().getInventory().removeItem(itemStack);
			HandlerList.unregisterAll(this);
		}
	}
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		if(itemStack.equals(e.getItem())) {
			if(game.getPlayerTeam(e.getPlayer())==Team.VOLEUR){
				if(game.getState()==GameState.Ingame) {
					action(e.getAction(),e.getPlayer());
				}
			}else {
				action(e.getAction(),e.getPlayer());
			}
		}
	}
	@EventHandler
	public void onPlayerDrop(PlayerDropItemEvent e) {
		if(itemStack.equals(e.getItemDrop().getItemStack())) {
			e.setCancelled(true);
			if(game.getPlayerTeam(e.getPlayer())==Team.VOLEUR){
				if(game.getState()==GameState.Ingame) {
					drop(e.getPlayer());
				}
			}else {
				drop(e.getPlayer());
			}
		}
	}
	public void vendre(Player player) {
		sell(player);
		player.getInventory().removeItem(itemStack);
		game.removeObjet(this);
		HandlerList.unregisterAll(this);
	}

	public ObjetsList getObjet() {
		return objet;
	}

	public ObjetsSkins getSkin() {
		return skin;
	}
	public ItemStack getItemStack() {
		return itemStack;
	}
}
