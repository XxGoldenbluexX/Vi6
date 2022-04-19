package fr.nekotine.vi6.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import fr.nekotine.vi6.map.Camera;

public class PlayerLeaveCamera extends Event{

	private final Player player;
	private final Camera camera;
	private static final HandlerList handlers = new HandlerList();
	public static HandlerList getHandlerList() {
	    return handlers;
	}
	public PlayerLeaveCamera(Camera camera, Player player) {
		this.player=player;
		this.camera=camera;
	}
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	public Player getPlayer() {
		return player;
	}
	public Camera getCamera() {
		return camera;
	}

}
