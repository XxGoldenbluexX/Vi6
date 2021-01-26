package fr.nekotine.vi6.events;

import java.sql.Date;
import java.sql.Time;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import fr.nekotine.vi6.enums.GameType;

public class GameEndEvent extends Event{
	private final String mapName;
	private final GameType type;
	private final Time duration;
	private final int money;
	private final Date date;
	public GameEndEvent(GameType type, int money, String mapName, Time duration, Date date) {
		this.mapName = mapName;
		this.type = type;
		this.duration = duration;
		this.money = money;
		this.date = date;
		
	}
	public String getMapName() {
		return mapName;
	}
	@Override
	public HandlerList getHandlers() {
		// TODO Auto-generated method stub
		return null;
	}
	public GameType getType() {
		return type;
	}
	public Time getDuration() {
		return duration;
	}
	public int getMoney() {
		return money;
	}
	public Date getDate() {
		return date;
	}

}
