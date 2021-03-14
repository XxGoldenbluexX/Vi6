package fr.nekotine.vi6.objet.list;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.objet.ObjetsList;
import fr.nekotine.vi6.objet.ObjetsSkins;
import fr.nekotine.vi6.objet.utils.Objet;
import fr.nekotine.vi6.wrappers.PlayerWrapper;

public class Campeur extends Objet{
	private static int DELAY_BEFORE_REGENERATING_TICKS = 100;
	private static int DELAY_BETWEEN_HEALING_TICKS = 20;
	private static int  REGENERATION_AMOUNT=1;
	private int delayBeforeRegeneration=DELAY_BEFORE_REGENERATING_TICKS;
	private int delayBeforeHealing=DELAY_BETWEEN_HEALING_TICKS;
	
	public Campeur(Vi6Main main, ObjetsList objet, ObjetsSkins skin, Game game, Player player, PlayerWrapper wrapper) {
		super(main, objet, skin, game, player, wrapper);
	}

	@Override
	public void tick() {
		if(delayBeforeRegeneration==0) {
			delayBeforeHealing--;
			if(delayBeforeHealing==0) {
				delayBeforeHealing=DELAY_BETWEEN_HEALING_TICKS;
				if(getOwner().getHealth()+REGENERATION_AMOUNT<=getOwner().getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()) {
					getOwner().setHealth(getOwner().getHealth()+REGENERATION_AMOUNT);	
				}
			}
		}else {
			delayBeforeRegeneration--;
			getItem().setAmount(Math.floorDiv(delayBeforeRegeneration, 20)+1);
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
	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if(getOwner().equals(e.getEntity())) {
			delayBeforeRegeneration=DELAY_BEFORE_REGENERATING_TICKS;
			delayBeforeHealing=DELAY_BETWEEN_HEALING_TICKS;
		}
	}
}
