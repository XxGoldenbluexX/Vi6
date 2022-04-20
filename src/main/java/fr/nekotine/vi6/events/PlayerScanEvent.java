package fr.nekotine.vi6.events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import fr.nekotine.vi6.Game;

public class PlayerScanEvent extends Event{
	private Location pLoc;
	private final Player player;
	private final Game game;
	private static final HandlerList handlers = new HandlerList();
	public static HandlerList getHandlerList() {
	    return handlers;
	}
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	public PlayerScanEvent(Player player, Location pLoc, Game game) {
		this.player = player;
		this.game = game;
		this.pLoc=pLoc;
	}
	public Location getLocation() {
		return pLoc;
	}
	public void setLocation(Location pLoc) {
		this.pLoc=pLoc;
	}
	public Player getPlayer() {
		return player;
	}
	public Game getGame() {
		return game;
	}
}
