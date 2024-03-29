package fr.nekotine.vi6.objet.list;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.destroystokyo.paper.event.entity.ProjectileCollideEvent;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.enums.Team;
import fr.nekotine.vi6.objet.ObjetsList;
import fr.nekotine.vi6.objet.ObjetsSkins;
import fr.nekotine.vi6.objet.utils.Objet;
import fr.nekotine.vi6.utils.Vi6Sound;
import fr.nekotine.vi6.wrappers.PlayerWrapper;

public class Tazer extends Objet{
	private static final int COOLDOWN=200;
	private ArrayList<Snowball> projectileList = new ArrayList<>();
	public Tazer(Vi6Main main, ObjetsList objet, ObjetsSkins skin, Game game, Player player, PlayerWrapper wrapper) {
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
	public void action(PlayerInteractEvent e) {
		if(e.getAction()==Action.RIGHT_CLICK_AIR || e.getAction()==Action.RIGHT_CLICK_BLOCK) {
			Snowball projectile = getOwner().launchProjectile(Snowball.class, getOwner().getEyeLocation().getDirection());
			projectile.setVelocity(projectile.getVelocity().multiply(5));
			projectileList.add(projectile);
			setCooldown(COOLDOWN);
		}
	}

	@Override
	public void drop() {
	}
	public static int getCooldown() {
		return COOLDOWN;
	}
	@EventHandler
	public void projectileCollide(ProjectileCollideEvent e) {
		if(projectileList.contains(e.getEntity())) {
			if(e.getCollidedWith() instanceof Player 
			&& getGame().getPlayerTeam((Player)e.getCollidedWith())==Team.VOLEUR) {
				projectileList.remove(e.getEntity());
				e.getEntity().remove();
				Player hit = (Player)e.getCollidedWith();
				hit.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,60,200,false,false,true));
				hit.addPotionEffect(new PotionEffect(PotionEffectType.JUMP,60,200,false,false,true));
				new BukkitRunnable() {
					private int nbHit=0;
					@Override
					public void run() {
						nbHit++;
						//hit.setNoDamageTicks(0);
						//hit.damage(0.001);
						PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_STATUS);
						packet.getIntegers().write(0, hit.getEntityId());
						//packet.getBytes().write(0, (byte) 1);//1 is the value for the damage animation.
						packet.getBytes().write(0,(byte) 2);
						ProtocolLibrary.getProtocolManager().broadcastServerPacket(packet);
						if (nbHit<20) {
							Vi6Sound.TAZER_SHOCKING.playAtLocation(hit.getLocation());
						}
						if (nbHit>30) {
							this.cancel();
						}
					}
				}.runTaskTimer(getMain(), 1, 2);
			}
		}
	}
	@EventHandler
	public void projectileHit(ProjectileHitEvent e) {
		if(projectileList.contains(e.getEntity()) && e.getHitBlock()!=null) {
			projectileList.remove(e.getEntity());
		}
	}
}
