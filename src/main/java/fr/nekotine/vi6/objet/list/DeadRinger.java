package fr.nekotine.vi6.objet.list;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.objet.ObjetsList;
import fr.nekotine.vi6.objet.ObjetsSkins;
import fr.nekotine.vi6.objet.utils.Objet;
import fr.nekotine.vi6.statuseffects.Effects;
import fr.nekotine.vi6.statuseffects.StatusEffect;
import fr.nekotine.vi6.wrappers.PlayerWrapper;

public class DeadRinger extends Objet{
	private static int INVISIBILITY_DURATION_TICK=60;
	public DeadRinger(Vi6Main main, ObjetsList objet, ObjetsSkins skin, Game game, Player player,
			PlayerWrapper wrapper) {
		super(main, objet, skin, game, player, wrapper);
		// TODO Auto-generated constructor stub
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
	}

	@Override
	public void drop() {
	}
	/*@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if(getOwner().equals(e.getEntity())
		&& (getDisplayedItem().equals(getOwner().getInventory().getItemInMainHand()) || getDisplayedItem().equals(getOwner().getInventory().getItemInOffHand())) 
		&& getOwner().getHealth()-e.getFinalDamage()<=0) {
			e.setCancelled(true);
			for (Map.Entry<Player, PlayerWrapper> p : getGame().getPlayerMap().entrySet()) {
				if(p.getValue().getTeam()==Team.GARDE) {
					p.getKey().sendMessage(MessageFormater.formatWithColorCodes('§', DisplayTexts.getMessage("game_death"),
					new MessageFormater("§p", String.valueOf(getOwner().getName())),
					new MessageFormater("§n", String.valueOf(getOwnerWrapper().getStealedArtefactList().size()))));
				}
			}
			StatusEffect se = new StatusEffect(Effects.Invisible);
			getOwnerWrapper().addStatusEffect(se);
			se.autoRemove(getMain(), INVISIBILITY_DURATION_TICK);
			destroy();
		}
	}*/
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		System.out.println("Player ded");
		if(getOwner().equals(e.getEntity())
		&& (getDisplayedItem().equals(getOwner().getInventory().getItemInMainHand()) || getDisplayedItem().equals(getOwner().getInventory().getItemInOffHand()))) {
			System.out.println("He had item");
			e.setCancelled(true);
			StatusEffect se = new StatusEffect(Effects.Invisible);
			getOwnerWrapper().addStatusEffect(se);
			se.autoRemove(getMain(), INVISIBILITY_DURATION_TICK);
			destroy();
		}
	}
}
