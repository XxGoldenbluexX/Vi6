package fr.nekotine.vi6.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import fr.nekotine.vi6.Game;

public class GameEndEvent extends Event{
	private final int idPartie;
	private final Game game;
	private final boolean forced;
	private static final HandlerList handlers = new HandlerList();
	public static HandlerList getHandlerList() {
	    return handlers;
	}
	public GameEndEvent(Game game, int idPartie, boolean forced) {
		this.idPartie = idPartie;
		this.game=game;
		this.forced = forced;
	}
	public Game getGame() {
		return game;
	}
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	public int getIdPartie() {
		return idPartie;
	}
	public boolean isForced() {
		return forced;
	}
}
