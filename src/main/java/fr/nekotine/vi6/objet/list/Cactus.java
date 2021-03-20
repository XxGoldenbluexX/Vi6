package fr.nekotine.vi6.objet.list;

import java.util.Map;

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

public class Cactus extends Objet {
	
	private static final ItemStack CATCUS = IsCreator.createItemStack(Material.CACTUS, 1,ChatColor.DARK_GREEN + "Cactus");

	public Cactus(Vi6Main main, ObjetsList objet, ObjetsSkins skin, Game game, Player player, PlayerWrapper wrapper) {
		super(main, objet, skin, game, player, wrapper);
	}

	@EventHandler
	public void onHit(EntityDamageByEntityEvent e) {
		if (!getOwner().equals(e.getDamager()))return;
		PlayerWrapper wrap = (e.getEntity() instanceof Player) ? getGame().getWrapper((Player) e.getEntity()) : null;
		if (wrap != null && wrap.getTeam() == Team.VOLEUR)
			for (Map.Entry<Player, PlayerWrapper> player : getGame().getPlayerMap().entrySet()) {
				if (((PlayerWrapper) player.getValue()).getTeam() == Team.VOLEUR
						&& ((PlayerWrapper) player.getValue()).getState() == PlayerState.INSIDE) {
					Player p = player.getKey();
					p.damage(0.01D);
					Location loc = p.getLocation();
					loc.getWorld().playSound(Sound.sound(Key.key("entity.bee.sting"), Sound.Source.AMBIENT, 1.0F, 1.0F),loc.getX(), loc.getY(), loc.getZ());
				}
			}
	}

	public void setNewOwner(Player p, PlayerWrapper wrapper) {
		super.setNewOwner(p, wrapper);
		getOwner().getInventory().setHelmet(CATCUS);
	}

	public void tick() {
	}

	public void cooldownEnded() {
	}

	public void death() {
		disable();
	}

	public void leaveMap() {
		disable();
	}

	public void action(Action action) {
	}

	public void drop() {
	}

	public void disable() {
		super.disable();
		getOwner().getInventory().remove(CATCUS);
	}
}