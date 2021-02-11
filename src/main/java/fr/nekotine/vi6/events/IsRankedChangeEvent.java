package fr.nekotine.vi6.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import fr.nekotine.vi6.Game;

public class IsRankedChangeEvent extends Event{
	private final Game game;
	private final boolean isRanked;
	private static final HandlerList handlers = new HandlerList();
	public static HandlerList getHandlerList() {
	    return handlers;
	}
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	public IsRankedChangeEvent(Game game, boolean isRanked) {
		this.game = game;
		this.isRanked = isRanked;
		
	}
	public Game getGame() {
		return game;
	}
	public boolean isRanked() {
		return isRanked;
	}
}
