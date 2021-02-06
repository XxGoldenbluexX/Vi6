package fr.nekotine.vi6.utils;

import org.bukkit.entity.Player;

public interface ZoneDetectionListener {
	
	public boolean playerEnterZone(Player player,DetectionZone zone);
	public boolean playerLeaveZone(Player player,DetectionZone zone);
	
}
