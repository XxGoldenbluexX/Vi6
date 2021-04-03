package fr.nekotine.vi6.objet.list;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.enums.PlayerState;
import fr.nekotine.vi6.objet.ObjetsList;
import fr.nekotine.vi6.objet.ObjetsSkins;
import fr.nekotine.vi6.objet.utils.Objet;
import fr.nekotine.vi6.statuseffects.Effects;
import fr.nekotine.vi6.statuseffects.StatusEffect;
import fr.nekotine.vi6.wrappers.PlayerWrapper;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;

public class Dephasage extends Objet{
	private static final int DELAY_BETWEEN_INVISIBILITY_TICKS=400;
	private static final int INVISIBILITY_DURATION_TICKS=40;
	private static final int DELAY_BETWEEN_WARNING_SOUND=10;
	private int delayBetweenInvisibility=DELAY_BETWEEN_INVISIBILITY_TICKS-INVISIBILITY_DURATION_TICKS-1;
	private StatusEffect invisibilityEffect = new StatusEffect(Effects.Invisible);
	public Dephasage(Vi6Main main, ObjetsList objet, ObjetsSkins skin, Game game, Player player,
			PlayerWrapper wrapper) {
		super(main, objet, skin, game, player, wrapper);
	}

	@Override
	public void tick() {
		if(getOwnerWrapper().getState()==PlayerState.ENTERING || getOwnerWrapper().getState()==PlayerState.INSIDE) {
			delayBetweenInvisibility--;
			ItemStack item = super.getDisplayedItem().clone();
			item.setAmount(delayBetweenInvisibility/20+1);
			setItem(item);
			switch(delayBetweenInvisibility) {
			case -INVISIBILITY_DURATION_TICKS+DELAY_BETWEEN_INVISIBILITY_TICKS+DELAY_BETWEEN_WARNING_SOUND*2:
				super.getOwner().playSound(Sound.sound(Key.key("block.note_block.chime"), Sound.Source.VOICE, 1, 2));
				break;
			case -INVISIBILITY_DURATION_TICKS+DELAY_BETWEEN_INVISIBILITY_TICKS+DELAY_BETWEEN_WARNING_SOUND:
				super.getOwner().playSound(Sound.sound(Key.key("block.note_block.chime"), Sound.Source.VOICE, 1, 1.5f));
				break;
			case -INVISIBILITY_DURATION_TICKS+DELAY_BETWEEN_INVISIBILITY_TICKS:
				super.getOwner().playSound(Sound.sound(Key.key("block.note_block.chime"), Sound.Source.VOICE, 1, 1));
				super.getOwner().playSound(Sound.sound(Key.key("block.beacon.deactivate"), Sound.Source.VOICE, 1, 1.5f));
				break;
			case DELAY_BETWEEN_WARNING_SOUND*2:
				super.getOwner().playSound(Sound.sound(Key.key("block.note_block.chime"), Sound.Source.VOICE, 1, 1));
				break;
			case DELAY_BETWEEN_WARNING_SOUND:
				super.getOwner().playSound(Sound.sound(Key.key("block.note_block.chime"), Sound.Source.VOICE, 1, 1.5f));
				break;
			case 0:
				super.getOwner().playSound(Sound.sound(Key.key("block.note_block.chime"), Sound.Source.VOICE, 1, 2));
				super.getOwner().playSound(Sound.sound(Key.key("block.beacon.activate"), Sound.Source.VOICE, 1, 1.5f));
				super.getOwner().playSound(Sound.sound(Key.key("entity.illusioner.prepare_blindness"), Sound.Source.VOICE, 1, 1));
				delayBetweenInvisibility=DELAY_BETWEEN_INVISIBILITY_TICKS;
				getOwnerWrapper().addStatusEffect(invisibilityEffect);
				invisibilityEffect.autoRemove(super.getMain(), INVISIBILITY_DURATION_TICKS);
				break;
			default:
				break;
			}
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
	public void action(PlayerInteractEvent e) {
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
