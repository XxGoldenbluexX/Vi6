package fr.nekotine.vi6.utils;

import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.plugin.PluginManager;

import fr.nekotine.vi6.Vi6Main;

public class ExplosionCanceler implements Listener {

	private static ExplosionCanceler instance;
	
	public ExplosionCanceler(PluginManager pmanager,Vi6Main main) {
		if (instance!=null) HandlerList.unregisterAll(instance);
		instance=this;
		pmanager.registerEvents(this, main);
	}
	
	@EventHandler
	public void onBlockExplode(BlockExplodeEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onExplosionPrime(ExplosionPrimeEvent event) {
		event.setCancelled(true);
	}
	
}
