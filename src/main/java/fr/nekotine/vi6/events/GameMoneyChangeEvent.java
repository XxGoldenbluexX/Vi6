package fr.nekotine.vi6.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import fr.nekotine.vi6.Game;

public class GameMoneyChangeEvent extends Event{
	private final Game game;
	private static final HandlerList handlers = new HandlerList();
	public static HandlerList getHandlerList() {
	    return handlers;
	}
	@Override
	public HandlerList getHandlers() {
		// TODO Auto-generated method stub
		return null;
	}
	public GameMoneyChangeEvent(Game game) {
		this.game = game;
	}
	public Game getGame() {
		return game;
	}
}
