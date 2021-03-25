package fr.nekotine.vi6.objet.list;

import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.objet.ObjetsList;
import fr.nekotine.vi6.objet.ObjetsSkins;
import fr.nekotine.vi6.objet.utils.Objet;
import fr.nekotine.vi6.statuseffects.Effects;
import fr.nekotine.vi6.statuseffects.StatusEffect;
import fr.nekotine.vi6.wrappers.PlayerWrapper;

public class Dephasage extends Objet{
	private static final int DELAY_BETWEEN_INVISIBILITY_TICKS=400;
	private static final int INVISIBILITY_DURATION_TICKS=40;
	private int delayBetweenInvisibility=DELAY_BETWEEN_INVISIBILITY_TICKS;
	private StatusEffect invisibilityEffect = new StatusEffect(Effects.Invisible);
	public Dephasage(Vi6Main main, ObjetsList objet, ObjetsSkins skin, Game game, Player player,
			PlayerWrapper wrapper) {
		super(main, objet, skin, game, player, wrapper);
	}

	@Override
	public void tick() {
		delayBetweenInvisibility--;
		ItemStack item = super.getDisplayedItem().clone();
		item.setAmount(delayBetweenInvisibility/20+1);
		setItem(item);
		if(delayBetweenInvisibility==0) {
			delayBetweenInvisibility=DELAY_BETWEEN_INVISIBILITY_TICKS;
			getOwnerWrapper().addStatusEffect(invisibilityEffect);
			invisibilityEffect.autoRemove(super.getMain(), INVISIBILITY_DURATION_TICKS);
		}
	}

	@Override
	public void cooldownEnded() {
	}

	@Override
	public void death() {
		disable();
	}

	@Override
	public void leaveMap() {
		disable();
	}

	@Override
	public void action(Action var1) {
	}

	@Override
	public void drop() {
	}
	public static int getDelay() {
		return DELAY_BETWEEN_INVISIBILITY_TICKS;
	}
	public static int getDuration() {
		return INVISIBILITY_DURATION_TICKS;
	}
}
