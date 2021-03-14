package fr.nekotine.vi6.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.map.Sortie;

public class PlayerEscapeEvent extends Event{
	private final Player player;
	private final Game game;
	private final Sortie sortie;
	private static final HandlerList handlers = new HandlerList();
	public static HandlerList getHandlerList() {
	    return handlers;
	}
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	public PlayerEscapeEvent(Sortie sortie, Player player, Game game) {
		this.player = player;
		this.game = game;
		this.sortie = sortie;
		
	}
	public Sortie getSortie() {
		return sortie;
	}
	public Game getGame() {
		return game;
	}
	public Player getPlayer() {
		return player;
	}
}
