package fr.nekotine.vi6.objet.list;

import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.enums.Team;
import fr.nekotine.vi6.objet.ObjetsList;
import fr.nekotine.vi6.objet.ObjetsSkins;
import fr.nekotine.vi6.objet.utils.Objet;
import fr.nekotine.vi6.statuseffects.Effects;
import fr.nekotine.vi6.statuseffects.StatusEffect;
import fr.nekotine.vi6.utils.MessageFormater;
import fr.nekotine.vi6.wrappers.PlayerWrapper;
import fr.nekotine.vi6.yml.DisplayTexts;

public class IEM extends Objet{
	private static final int JAM_DURATION_TICKS = 100;
	public IEM(Vi6Main main, ObjetsList objet, ObjetsSkins skin, Game game, Player player, PlayerWrapper wrapper) {
		super(main, objet, skin, game, player, wrapper);
	}

	@Override
	public void tick() {
	}

	@Override
	public void cooldownEnded() {
	}

	@Override
	public void death() {
	}

	@Override
	public void leaveMap() {
	}

	@Override
	public void action(Action var1) {
		if(var1==Action.RIGHT_CLICK_AIR || var1==Action.RIGHT_CLICK_BLOCK) use();
	}

	@Override
	public void drop() {
		use();
	}

	public static int getJamDurationTicks() {
		return JAM_DURATION_TICKS;
	}
	private void use() {
		getOwner().sendMessage(MessageFormater.formatWithColorCodes('§',
		DisplayTexts.getMessage("objet_iem_jammed")));
		
		StatusEffect jam = new StatusEffect(Effects.Jammed);
		for(PlayerWrapper wrapper : getGame().getPlayerMap().values()) {
			if(wrapper.getTeam()==Team.GARDE) {
				wrapper.getPlayer().sendMessage(MessageFormater.formatWithColorCodes('§',
				DisplayTexts.getMessage("objet_iem_jammed")));
				wrapper.addStatusEffect(jam);
			}
		}
		jam.autoRemove(getMain(), JAM_DURATION_TICKS);
		destroy();
	}
}
