package fr.nekotine.vi6.statuseffects;

public class StatusEffect {
	private final Effects effect;

	public StatusEffect(Effects e) {
		this.effect = e;
	}

	public Effects getEffect() {
		return this.effect;
	}
}