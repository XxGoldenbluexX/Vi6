package fr.nekotine.vi6.objet.list;

import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.objet.ObjetsList;
import fr.nekotine.vi6.objet.ObjetsSkins;
import fr.nekotine.vi6.objet.utils.Objet;
import fr.nekotine.vi6.wrappers.PlayerWrapper;

public class Surcharge extends Objet {
	
	private static final int COOLDOWN = 400;
	private static int BUFF_DURATION = 30;

	public Surcharge(Vi6Main main, ObjetsList objet, ObjetsSkins skin, Game game, Player player,PlayerWrapper wrapper) {
		super(main, objet, skin, game, player, wrapper);
	}

	public void action(Action action) {
		if (action != Action.PHYSICAL) cast();
	}

	public void drop() {
		cast();
	}

	public void cooldownEnded() {
	}

	private void cast() {
		getOwner().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, BUFF_DURATION, 9, false, false, true));
		getOwner().addPotionEffect(
				new PotionEffect(PotionEffectType.INCREASE_DAMAGE, BUFF_DURATION, 1, false, false, true));
		getOwner().getWorld().playSound(getOwner().getLocation(), Sound.ENTITY_POLAR_BEAR_WARNING, SoundCategory.MASTER,
				0.5F, 0.5F);
		setCooldown(COOLDOWN);
	}

	public void tick() {
	}

	public void death() {
		disable();
	}

	public void leaveMap() {
		disable();
	}
}