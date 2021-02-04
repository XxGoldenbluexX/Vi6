package fr.nekotine.vi6.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import fr.nekotine.vi6.Game;

public class GameTickEvent extends Event{
	private final Game game;
	private static final HandlerList handlers = new HandlerList();
	public static HandlerList getHandlerList() {
	    return handlers;
	}
	@Override
	public HandlerList getHandlers() {
		return null;
	}
	public GameTickEvent(Game game) {
		this.game = game;
		
	}
	public Game getGame() {
		return game;
	}
}
