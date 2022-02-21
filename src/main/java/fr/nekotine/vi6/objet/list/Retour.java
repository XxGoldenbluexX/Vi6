package fr.nekotine.vi6.objet.list;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.objet.ObjetsList;
import fr.nekotine.vi6.objet.ObjetsSkins;
import fr.nekotine.vi6.objet.utils.Objet;
import fr.nekotine.vi6.wrappers.PlayerWrapper;

public class Retour extends Objet{
	private static final int TELEPORT_DELAY_TICKS = 20*4;
	private static final int COOLDOWN_DELAY_TICKS = 20*4;
	private static final int PARTICLE_NUMBER = 5;
	
	private BukkitTask runnable;
	private Location playerLoc;
	private boolean isRunning = false;
	
	public Retour(Vi6Main main, ObjetsList objet, ObjetsSkins skin, Game game, Player player, PlayerWrapper wrapper) {
		super(main, objet, skin, game, player, wrapper);
		
	}
	
	public static int getTeleportDelay(){
		return TELEPORT_DELAY_TICKS;
	}
	
	@Override
	public void tick() {
		if(isRunning) {
			getOwner().getWorld().spawnParticle(Particle.GLOW, playerLoc, PARTICLE_NUMBER, 0.2, 0, 0.2, 0);
		}
	}
	
	@Override
	public void cooldownEnded() {
	}
	
	@Override
	public void death() {
		if(isRunning) {
			runnable.cancel();
			isRunning=false;
		}
	}
	
	@Override
	public void leaveMap() {
		if(isRunning) {
			runnable.cancel();
			isRunning=false;
		}
	}
	@Override
	public void action(PlayerInteractEvent e) {
		if(e.getAction()==Action.RIGHT_CLICK_AIR || e.getAction()==Action.RIGHT_CLICK_BLOCK) use();
		
	}
	
	@Override
	public void drop() {
		use();
		
	}
	
	private void use() {
		if(!isRunning) {
			playerLoc = getOwner().getLocation();
			runnable = new BukkitRunnable() {
				@Override
				public void run() {
					getOwner().teleport(playerLoc);
					isRunning = false;
				}
			}.runTaskLater(getMain(), COOLDOWN_DELAY_TICKS);
			isRunning = true;
			setCooldown(COOLDOWN_DELAY_TICKS);
		}
	}
	
}
