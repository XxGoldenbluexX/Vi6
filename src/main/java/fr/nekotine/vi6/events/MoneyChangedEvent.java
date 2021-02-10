package fr.nekotine.vi6.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import fr.nekotine.vi6.Game;

public class MoneyChangedEvent extends Event{
	private final Game game;
	private final int money;
	private static final HandlerList handlers = new HandlerList();
	public static HandlerList getHandlerList() {
	    return handlers;
	}
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	public MoneyChangedEvent(Game game, int money) {
		this.game = game;
		this.money = money;
		
	}
	public Game getGame() {
		return game;
	}
	public int getMoney() {
		return money;
	}
}
