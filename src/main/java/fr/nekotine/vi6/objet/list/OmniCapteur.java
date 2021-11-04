package fr.nekotine.vi6.objet.list;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.enums.PlayerState;
import fr.nekotine.vi6.enums.Team;
import fr.nekotine.vi6.events.PlayerJamEvent;
import fr.nekotine.vi6.objet.ObjetsList;
import fr.nekotine.vi6.objet.ObjetsSkins;
import fr.nekotine.vi6.objet.utils.Objet;
import fr.nekotine.vi6.statuseffects.Effects;
import fr.nekotine.vi6.statuseffects.StatusEffect;
import fr.nekotine.vi6.utils.MessageFormater;
import fr.nekotine.vi6.utils.Vi6Sound;
import fr.nekotine.vi6.wrappers.PlayerWrapper;
import fr.nekotine.vi6.yml.DisplayTexts;

public class OmniCapteur extends Objet{
	private static final float RANGE=3;
	public static final float SQUARED_RANGE = RANGE*RANGE;
	private final ArrayList<Player> glowed = new ArrayList<>();
	private final StatusEffect glowEffect = new StatusEffect(Effects.Glow);
	private ArmorStand omni;
	public OmniCapteur(Vi6Main main, ObjetsList objet, ObjetsSkins skin, Game game, Player player, PlayerWrapper wrapper) {
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
		if(e.getAction()==Action.RIGHT_CLICK_AIR || e.getAction()==Action.RIGHT_CLICK_BLOCK) use();
	}

	@Override
	public void drop() {
		use();
	}
	private void use() {
		if(getOwner().getLocation().subtract(0, 1, 0).getBlock().getType().isSolid()) {
			omni = (ArmorStand)getOwner().getWorld().spawnEntity(getOwner().getLocation(), EntityType.ARMOR_STAND);
			omni.setArms(false);
			omni.setMarker(true);
			omni.setBasePlate(false);
			omni.setSmall(true);
			omni.getEquipment().setHelmet(new ItemStack(Material.REDSTONE_BLOCK));
			Vi6Sound.OMNICAPTEUR_POSE.playAtLocation(getOwner().getLocation());
			consume();
		}
	}
	
	@EventHandler
    public void onJam(PlayerJamEvent e) {
        if(glowed.contains(e.getPlayer())) {
            getGame().getWrapper(e.getPlayer()).removeStatusEffect(glowEffect);
            glowed.remove(e.getPlayer());
        }
    }
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		PlayerWrapper w = getGame().getWrapper(e.getPlayer());
		if(omni!=null) {
			if (w!=null && !w.haveStatusEffect(new StatusEffect(Effects.Jammed))) {
				if(w.getTeam()==Team.VOLEUR && w.getState()==PlayerState.INSIDE) {
					boolean glowable = omni.getLocation().distanceSquared(e.getTo())<=SQUARED_RANGE && !w.haveEffect(Effects.Fantomatique);
					if(glowed.contains(e.getPlayer())) {
						if(!glowable) {
							getGame().getWrapper(e.getPlayer()).removeStatusEffect(glowEffect);
							glowed.remove(e.getPlayer());
						}
					}else if(glowable){
						PlayerWrapper thief = getGame().getWrapper(e.getPlayer());
						thief.addStatusEffect(glowEffect);
						Vi6Sound.OMNICAPTEUR_DETECT.playAtLocation(getOwner().getLocation());
						getOwner().sendActionBar(MessageFormater.formatWithColorCodes('§',
						DisplayTexts.getMessage("objet_omni_thiefDetected")));
						e.getPlayer().sendMessage(MessageFormater.formatWithColorCodes('§',
						DisplayTexts.getMessage("objet_omni_selfDetected")));
						glowed.add(e.getPlayer());
					}
				}
			}
		}else {
			if (e.getPlayer() == getOwner() && getOwner().isSneaking()) {
				double fullCircle = Math.PI*2*RANGE;
				Location l = getOwner().getLocation();
				for (double i = 0;i<fullCircle;i+=0.5) {
					Location locIte = new Location(l.getWorld(),l.getX()+(Math.cos(i)*RANGE),l.getY(),l.getZ()+(Math.sin(i)*RANGE));
					l.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, locIte, 1,0,0,0,0);
				}
			}
		}
	}

	public static float getSquaredBlockRange() {
		return SQUARED_RANGE;
	}
	public void destroy() {
		super.destroy();
		if(omni!=null) omni.remove();
	}
	public void disable() {
		super.disable();
		for(Player guard : glowed) {
			getGame().getWrapper(guard).removeStatusEffect(glowEffect);
		}
	}
}
