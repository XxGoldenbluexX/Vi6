package fr.nekotine.vi6.objet.list;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Silverfish;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.enums.Team;
import fr.nekotine.vi6.events.PlayerJamEvent;
import fr.nekotine.vi6.objet.ObjetsList;
import fr.nekotine.vi6.objet.ObjetsSkins;
import fr.nekotine.vi6.objet.utils.Objet;
import fr.nekotine.vi6.statuseffects.Effects;
import fr.nekotine.vi6.statuseffects.GlowToken;
import fr.nekotine.vi6.utils.Vi6Sound;
import fr.nekotine.vi6.wrappers.PlayerWrapper;

public class Observateur extends Objet{
	
	public static final float RANGE=3;
	
	private static final float SQUARED_RANGE = RANGE*RANGE;
	
	private static final int NB_OBSERVATEUR = 3;
	
	private final List<GlowToken> glowTokens = new LinkedList<>();
	
	private List<Silverfish> observateurs = new LinkedList<>();
	
	public Observateur(Vi6Main main, ObjetsList objet, ObjetsSkins skin, Game game, Player player, PlayerWrapper wrapper) {
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
		if (observateurs.size() < NB_OBSERVATEUR) {
			if(getOwner().getLocation().subtract(0, 0.1, 0).getBlock().getType().isSolid()) {
				var obs = (Silverfish)getOwner().getWorld().spawnEntity(getOwner().getLocation(), EntityType.SILVERFISH);
				obs.setSilent(true);
				obs.setAI(false);
				observateurs.add(obs);
				Vi6Sound.OMNICAPTEUR_POSE.playAtLocation(getOwner().getLocation());
				if (observateurs.size() >= NB_OBSERVATEUR) {
					consume();
				}
			}
		}
	}
	
	@EventHandler
    public void onJam(PlayerJamEvent e) {
        if(e.getPlayer().equals(getOwner())) {
        	for (var tok : glowTokens) {
    			tok.glowed.getGlowTokens().remove(tok);
    		}
    		glowTokens.clear();
        	getGame().getPlayerMap().values().stream().filter(p -> p.getTeam() == Team.GARDE).forEach(p -> {getMain().triggerGlowUpdate(p.getPlayer());});
        }
    }
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		var main = getMain();
		PlayerWrapper w = getGame().getWrapper(e.getPlayer());
		if (w != null && !getOwnerWrapper().haveEffect(Effects.Jammed)) {
			for (var obs : observateurs) {
				if (obs.getLocation().distanceSquared(e.getPlayer().getLocation()) <= SQUARED_RANGE) {
					getGame().getPlayerMap().values().stream().filter(p -> p.getTeam() == Team.VOLEUR).forEach(v -> {
						var gt = new GlowToken();
						gt.glowed = w;
						gt.viewer = v;
						w.getGlowTokens().add(new GlowToken());
						glowTokens.add(gt);
						main.triggerGlowUpdate(e.getPlayer());
					});
				}else {
					for (var tok : glowTokens) {
						if (tok.glowed == w) {
							w.getGlowTokens().remove(tok);
						}
					}
					glowTokens.removeIf(t -> t.glowed == w);
					main.triggerGlowUpdate(e.getPlayer());
				}
			}
		}
	}

	public static float getSquaredBlockRange() {
		return SQUARED_RANGE;
	}
	@Override
	public void destroy() {
		for (var obs : observateurs) {
			obs.remove();
		}
		super.destroy();
	}
	@Override
	public void disable() {
		super.disable();
		for (var tok : glowTokens) {
			tok.glowed.getGlowTokens().remove(tok);
		}
		glowTokens.clear();
	}
}
