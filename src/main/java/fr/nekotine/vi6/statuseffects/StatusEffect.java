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
		if (wrapper!=null) wrapper.removeStatusEffect(this);
		wrapper=w;
	}
	
	public void remove() {
		if (wrapper!=null) wrapper.removeStatusEffect(this);
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
