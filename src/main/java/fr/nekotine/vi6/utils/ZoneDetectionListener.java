package fr.nekotine.vi6.utils;

import org.bukkit.entity.Player;

public interface ZoneDetectionListener {
	
	public boolean playerEnterZone(Player player);
	public boolean playerLeaveZone(Player player);
	
}
