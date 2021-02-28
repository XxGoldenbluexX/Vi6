package fr.nekotine.vi6.objet.list;

import java.util.Map.Entry;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.enums.PlayerState;
import fr.nekotine.vi6.enums.Team;
import fr.nekotine.vi6.objet.ObjetsList;
import fr.nekotine.vi6.objet.ObjetsSkins;
import fr.nekotine.vi6.objet.utils.Objet;
import fr.nekotine.vi6.utils.IsCreator;
import fr.nekotine.vi6.wrappers.PlayerWrapper;

public class Cactus extends Objet{

	public Cactus(Vi6Main main, ObjetsList objet, ObjetsSkins skin, Player player, Game game) {
		super(main, objet, skin, IsCreator.createItemStack(ObjetsList.CACTUS.getInShopMaterial(), 1, ObjetsList.CACTUS.getInShopName(),
				ObjetsList.CACTUS.getInShopLore()), game, player);
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
	}

	@Override
	public void drop(Player holder) {
	}
	@EventHandler
	public void onHit(EntityDamageByEntityEvent e) {
		if(e.getDamager() instanceof Player &&
		((Player)e.getDamager()).getInventory().contains(itemStack) &&
		e.getEntity() instanceof Player &&
		game.getWrapper((Player)e.getEntity()).getTeam()==Team.VOLEUR) {
			for(Entry<Player, PlayerWrapper> player : game.getPlayerMap().entrySet()) {
				if(player.getValue().getTeam()==Team.VOLEUR && player.getValue().getState()==PlayerState.INSIDE) {
					player.getKey().damage(0.01);
				}
			}
		}
	}

	@Override
	public void cooldownEnded() {
	}
}
