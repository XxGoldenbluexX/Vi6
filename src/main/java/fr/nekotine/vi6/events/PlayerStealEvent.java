package fr.nekotine.vi6.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import fr.nekotine.vi6.Artefact;

public class PlayerStealEvent extends Event{
	private final Artefact artefact;
	private final Player player;
	private static final HandlerList handlers = new HandlerList();
	public static HandlerList getHandlerList() {
	    return handlers;
	}
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	public PlayerStealEvent(Player player, Artefact artefact) {
		this.artefact = artefact;
		this.player = player;
		
	}
	public Artefact getArtefact() {
		return artefact;
	}
	public Player getPlayer() {
		return player;
	}
}
