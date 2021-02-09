package fr.nekotine.vi6.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import fr.nekotine.vi6.Game;

public class GameMapChangeEvent extends Event{
	private static final HandlerList handlers = new HandlerList();
	public static HandlerList getHandlerList() {
	    return handlers;
	}
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	private final Game game;
	private final String mapName;
	public GameMapChangeEvent(String mapName, Game game) {
		this.game = game;
		this.mapName = mapName;
		
	}
	public Game getGame() {
		return game;
	}
	public String getMapName() {
		return mapName;
	}
}
