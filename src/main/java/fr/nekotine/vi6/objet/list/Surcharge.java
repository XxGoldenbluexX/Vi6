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
import fr.nekotine.vi6.utils.IsCreator;

public class Surcharge extends Objet{
	private static int cooldownTicks=20*20;
	private static int buffDurationTicks=2*20;
	public Surcharge(Vi6Main main, ObjetsList objet, ObjetsSkins skin, Player player, Game game) {
		super(main, objet, skin, IsCreator.createObjetItemStack(main,objet,1), game, player);
	}

	@Override
	public void gameEnd() {
	}

	@Override
	public void tick() {
	}

	@Override
	public void leaveMap(Player holder) {
	}

	@Override
	public void death(Player holder) {
	}

	@Override
	public void sell(Player holder) {
	}

	@Override
	public void action(Action action, Player holder) {
		if(action!=Action.PHYSICAL) cast(holder);
	}

	@Override
	public void drop(Player holder) {
		cast(holder);
	}

	@Override
	public void cooldownEnded() {
	}
	private void cast(Player holder) {
		holder.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, buffDurationTicks, 9, false, false, true));
		holder.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, buffDurationTicks, 1, false, false, true));
		holder.getWorld().playSound(holder.getLocation(),Sound.ENTITY_POLAR_BEAR_WARNING, SoundCategory.MASTER, 0.5f, 0.5f);
		setCooldown(cooldownTicks);
	}
}
