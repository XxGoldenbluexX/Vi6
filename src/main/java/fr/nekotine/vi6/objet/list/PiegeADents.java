package fr.nekotine.vi6.objet.list;

import java.util.Map.Entry;
import java.util.function.Predicate;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.enums.PlayerState;
import fr.nekotine.vi6.enums.Team;
import fr.nekotine.vi6.objet.ObjetsList;
import fr.nekotine.vi6.objet.ObjetsSkins;
import fr.nekotine.vi6.objet.utils.Objet;
import fr.nekotine.vi6.wrappers.PlayerWrapper;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;

public class PiegeADents extends Objet {

	private static final double SQUARED_TRIGGER_RANGE = 0.7;
	private static final double BITE_DAMAGES = 6;
	
	private Player victim;
	private boolean armed = false;
	private boolean triggered = false;
	private Location loc;
	private Entity fang;
	
	public PiegeADents(Vi6Main main, ObjetsList objet, ObjetsSkins skin, Game game, Player player,PlayerWrapper wrapper) {
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
		cast();
	}

	@Override
	public void drop() {
		cast();
	}
	
	@Override
	public void disable() {
		super.disable();
		armed=false;
		triggered=false;
		victim=null;
	}
	
	private void cast() {
		if (!onGround()) {
			getOwner().playSound(Sound.sound(Key.key("entity.villager.no"), Sound.Source.AMBIENT, 1.0F, 1.0F));
			return;
		}else {
			loc = getOwner().getLocation();
			armed=true;
			consume();
		}
	}
	
	private boolean onGround() {
		return (!getOwner().isFlying()
				&& getOwner().getLocation().subtract(0.0D, 0.1D, 0.0D).getBlock().getType().isSolid());
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		if (armed) {
			if (getGame().getPlayerMap().entrySet().stream().anyMatch(new Predicate<Entry<Player,PlayerWrapper>>() {
				@Override
				public boolean test(Entry<Player, PlayerWrapper> t) {
					PlayerWrapper w = t.getValue();
					return t.getKey().equals(event.getPlayer()) && w.getTeam()==Team.VOLEUR && w.getState()==PlayerState.INSIDE;
				}
			}
			)) {
				if (event.getFrom().distanceSquared(loc)<=SQUARED_TRIGGER_RANGE) {
					victim = event.getPlayer();
					armed=false;
					triggered=true;
					fang = loc.getWorld().spawnEntity(loc, EntityType.EVOKER_FANGS, SpawnReason.TRAP);
					new BukkitRunnable() {
						@Override
						public void run() {
							triggered=false;
							disable();
						}
					}.runTaskLater(getMain(), 20);
				}
			}
		}else {
			if (triggered) {
				if (event.getPlayer().equals(victim)) event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onEntityDamageAnOther(EntityDamageByEntityEvent event) {
		LivingEntity victime = (LivingEntity) event.getEntity();
		Entity attacker = event.getDamager();
		if (victime.equals(victim) && attacker.equals(fang)) {
			System.out.println("valid 1");
			System.out.println(event.getCause());
			if (event.getCause()==DamageCause.MAGIC) {
				System.out.println("valid 2");
				event.setCancelled(true);
				victime.setNoDamageTicks(0);
				victime.damage(BITE_DAMAGES, attacker);
				return;
			}
		}
	}

}
