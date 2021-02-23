package fr.nekotine.vi6.statuseffects;

import org.bukkit.entity.Player;

import fr.nekotine.vi6.wrappers.PlayerWrapper;

@FunctionalInterface
public interface StatusEffectUpdate {

	public void run(Player p,PlayerWrapper w);
	
}
