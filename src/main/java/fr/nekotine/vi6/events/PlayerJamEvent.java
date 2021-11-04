package fr.nekotine.vi6.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import fr.nekotine.vi6.Game;

public class PlayerJamEvent extends Event{
	private final Game game;
	private final Player player;
	private static final HandlerList handlers = new HandlerList();
	public static HandlerList getHandlerList() {
	    return handlers;
	}
	public PlayerJamEvent(Game game, Player player) {
		this.game=game;
		this.player=player;
	}
	public Game getGame() {
		return game;
	}
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	public Player getPlayer() {
		return player;
	}
}
