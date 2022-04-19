package fr.nekotine.vi6.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import fr.nekotine.vi6.Game;

public class RankedChangedEvent extends Event{
	private final Game game;
	private final boolean ranked;
	private static final HandlerList handlers = new HandlerList();
	public static HandlerList getHandlerList() {
	    return handlers;
	}
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	public RankedChangedEvent(Game game, boolean ranked) {
		this.game = game;
		this.ranked = ranked;
		
	}
	public Game getGame() {
		return game;
	}
	public boolean isRanked() {
		return ranked;
	}
}
