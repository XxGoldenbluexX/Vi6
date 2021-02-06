package fr.nekotine.vi6.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import fr.nekotine.vi6.Game;

public class PlayerEnterMapEvent extends Event{
	private final Player player;
	private final Game game;
	private static final HandlerList handlers = new HandlerList();
	public static HandlerList getHandlerList() {
	    return handlers;
	}
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	public PlayerEnterMapEvent(Player player, Game game) {
		this.player = player;
		this.game = game;
		
	}
	public Player getPlayer() {
		return player;
	}
	public Game getGame() {
		return game;
	}
}
