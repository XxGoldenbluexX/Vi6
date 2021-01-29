package fr.nekotine.vi6.events;

import java.sql.Date;
import java.sql.Time;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import fr.nekotine.vi6.Game;

public class GameEndEvent extends Event{
	private final Game game;
	private final Time duration;
	private final Date date;
	public GameEndEvent(Game game, Time duration, Date date) {
		this.game=game;
		this.duration=duration;
		this.date=date;
	}
	public Game getGame() {
		return game;
	}
	public Time getDuration() {
		return duration;
	}
	public Date getDate() {
		return date;
	}
	@Override
	public HandlerList getHandlers() {
		return null;
	}
}
