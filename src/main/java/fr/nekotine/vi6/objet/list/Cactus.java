package fr.nekotine.vi6.objet.list;

import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.enums.PlayerState;
import fr.nekotine.vi6.enums.Team;
import fr.nekotine.vi6.objet.ObjetsList;
import fr.nekotine.vi6.objet.ObjetsSkins;
import fr.nekotine.vi6.objet.utils.Objet;
import fr.nekotine.vi6.utils.IsCreator;
import fr.nekotine.vi6.wrappers.PlayerWrapper;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;

public class Cactus extends Objet{

	private static final ItemStack CATCUS = IsCreator.createItemStack(Material.CACTUS, 1, ChatColor.DARK_GREEN+"Cactus");
	
	public Cactus(Vi6Main main, ObjetsList objet, ObjetsSkins skin, Player player, Game game) {
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
		holder.getInventory().remove(CATCUS);
	}

	@Override
	public void sell(Player holder) {
		holder.getInventory().remove(CATCUS);
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
					Player p = player.getKey();
					p.damage(0.01);
					Location loc = p.getLocation();
					loc.getWorld().playSound(Sound.sound(Key.key("entity.bee.sting"),Sound.Source.AMBIENT,1f,1f), loc.getX(), loc.getY(), loc.getZ());
				}
			}
		}
	}

	@Override
	public void cooldownEnded() {
	}
}
