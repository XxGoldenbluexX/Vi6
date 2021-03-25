package fr.nekotine.vi6.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.map.Artefact;

public class PlayerStealEvent extends Event{
	private final Artefact artefact;
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
	public PlayerStealEvent(Player player, Artefact artefact, Game game) {
		this.artefact = artefact;
		this.player = player;
		this.game = game;
	}
	public Artefact getArtefact() {
		return artefact;
	}
	public Player getPlayer() {
		return player;
	}
	public Game getGame() {
		return game;
	}
}
