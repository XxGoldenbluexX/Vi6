package fr.nekotine.vi6.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import fr.nekotine.vi6.Game;

public class PlayerJoinGameEvent extends Event{
	private final Player player;
	private final Game game;
	public PlayerJoinGameEvent(Game game,Player player) {
		this.player = player;
		this.game = game;
	}
	private static final HandlerList handlers = new HandlerList();
	public static HandlerList getHandlerList() {
	    return handlers;
	}
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	public Game getGame() {
		return game;
	}
	public Player getPlayer() {
		return player;
	}
}
