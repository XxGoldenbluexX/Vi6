package fr.nekotine.vi6.statuseffects;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import fr.nekotine.vi6.wrappers.PlayerWrapper;

public class StatusEffect {
	
	private final Effects effect;
	private PlayerWrapper wrapper;
	
	public StatusEffect(Effects e) {
		effect=e;
	}

	public Effects getEffect() {
		return effect;
	}
	
	public void setWrapper(PlayerWrapper w) {
		if (wrapper!=null) remove();
		wrapper=w;
	}
	
	public void remove() {
		if (wrapper!=null) {wrapper.removeStatusEffect(this);wrapper=null;}
	}
	
	public void autoRemove(Plugin main,long delay) {
		new BukkitRunnable() {
			@Override
			public void run() {
				remove();
			}
		}.runTaskLater(main, delay);
	}
	
}
