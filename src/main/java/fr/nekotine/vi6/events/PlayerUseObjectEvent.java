package fr.nekotine.vi6.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import fr.nekotine.vi6.objet.ObjetsList;

public class PlayerUseObjectEvent extends Event{
	private final Player player;
	private final ObjetsList objet;
	private static final HandlerList handlers = new HandlerList();
	public static HandlerList getHandlerList() {
	    return handlers;
	}
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	public PlayerUseObjectEvent(Player player, ObjetsList objet) {
		this.player = player;
		this.objet = objet;
		
	}
	public Player getPlayer() {
		return player;
	}
	public ObjetsList getObjet() {
		return objet;
	}
}
