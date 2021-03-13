package fr.nekotine.vi6.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import fr.nekotine.vi6.Game;

public class PlayerChangeRoomEvent extends Event{
	private final Player player;
	private final String room;
	private final Game game;
	private static final HandlerList handlers = new HandlerList();
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	public static HandlerList getHandlerList() {
	    return handlers;
	}
	public PlayerChangeRoomEvent(Player player, String room, Game game) {
		this.player = player;
		this.room = room;
		this.game = game;
		
	}
	public Player getPlayer() {
		return player;
	}
	public String getRoom() {
		return room;
	}
	public Game getGame() {
		return game;
	}
}
