package fr.nekotine.vi6.objet.list;

import java.util.ArrayList;
import java.util.Map.Entry;

import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.scheduler.BukkitRunnable;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.enums.Team;
import fr.nekotine.vi6.objet.ObjetsList;
import fr.nekotine.vi6.objet.ObjetsSkins;
import fr.nekotine.vi6.objet.utils.Objet;
import fr.nekotine.vi6.wrappers.PlayerWrapper;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;

public class BrouilleurRadio extends Objet{
	public BrouilleurRadio(Vi6Main main, ObjetsList objet, ObjetsSkins skin, Game game, Player player,
			PlayerWrapper wrapper) {
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
		disable();
	}

	@Override
	public void leaveMap() {
		disable();
	}

	@Override
	public void action(Action var1) {
		if(var1==Action.RIGHT_CLICK_AIR || var1==Action.RIGHT_CLICK_BLOCK) use();
	}

	@Override
	public void drop() {
		use();
	}
	private void use() {
		super.getOwner().playSound(Sound.sound(Key.key("block.note_block.iron_xylophone"), Sound.Source.VOICE, 1, 1));
		super.getOwner().playSound(Sound.sound(Key.key("block.note_block.iron_xylophone"), Sound.Source.VOICE, 1, 2));
		ArrayList<Player> guards = new ArrayList<>();
		for(Entry<Player, PlayerWrapper> player : getGame().getPlayerMap().entrySet()) {
			if(player.getValue().getTeam()==Team.GARDE) {
				guards.add(player.getKey());
				player.getKey().playSound(Sound.sound(Key.key("entity.creeper.primed"), Sound.Source.VOICE, 1, 2));
				player.getKey().playSound(Sound.sound(Key.key("entity.generic.extinguish_fire"), Sound.Source.VOICE, 1, 0));
			}
		}
		new BukkitRunnable() {
			byte repeatCount=0;
			@Override
			public void run() {
				switch(repeatCount) {
				case 0:
					guards.forEach((p)-> p.playSound(Sound.sound(Key.key("entity.generic.extinguish_fire"), Sound.Source.VOICE, 1, 0.5f)));
					break;
				case 1:
					guards.forEach((p)-> p.playSound(Sound.sound(Key.key("entity.creeper.primed"), Sound.Source.VOICE, 1, 2)));
					break;
				case 2:
					guards.forEach((p)-> p.playSound(Sound.sound(Key.key("entity.generic.extinguish_fire"), Sound.Source.VOICE, 1, 1.5f)));
					break;
				case 3:
					this.cancel();
					break;
				default:
					break;
				}
				repeatCount++;
			}
		}.runTaskTimer(getMain(), 8, 0);
		destroy();
	}
}
