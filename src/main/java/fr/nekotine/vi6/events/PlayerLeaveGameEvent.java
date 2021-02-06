package fr.nekotine.vi6.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import fr.nekotine.vi6.Game;

public class PlayerLeaveGameEvent extends Event{
	private static final HandlerList handlers = new HandlerList();
	public static HandlerList getHandlerList() {
	    return handlers;
	}
	@Override
	public HandlerList getHandlers() {
		// TODO Auto-generated method stub
		return null;
	}
	private final Game game;
	private final Player player;
	public PlayerLeaveGameEvent(Player player, Game game) {
		this.game = game;
		this.player = player;
		
	}
	public Game getGame() {
		return game;
	}
	public Player getPlayer() {
		return player;
	}
}
