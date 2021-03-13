package fr.nekotine.vi6.statuseffects;

import fr.nekotine.vi6.wrappers.PlayerWrapper;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class StatusEffect {
	private final Effects effect;

	private PlayerWrapper wrapper;

	public StatusEffect(Effects e) {
		this.effect = e;
	}

	public Effects getEffect() {
		return this.effect;
	}

	public void setWrapper(PlayerWrapper w) {
		if (this.wrapper != null)
			remove();
		this.wrapper = w;
	}

	public void remove() {
		if (this.wrapper != null) {
			this.wrapper.removeStatusEffect(this);
			this.wrapper = null;
		}
	}

	public boolean isSet() {
		return (this.wrapper != null);
	}

	public void autoRemove(Plugin main, long delay) {
		(new BukkitRunnable() {
			public void run() {
				StatusEffect.this.remove();
			}
		}).runTaskLater(main, delay);
	}
}