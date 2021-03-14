package fr.nekotine.vi6.objet.list;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.objet.ObjetsList;
import fr.nekotine.vi6.objet.ObjetsSkins;
import fr.nekotine.vi6.objet.utils.Objet;
import fr.nekotine.vi6.statuseffects.Effects;
import fr.nekotine.vi6.statuseffects.StatusEffect;
import fr.nekotine.vi6.wrappers.PlayerWrapper;

public class BainDeFumee extends Objet {
	
	private final ArrayList<SmokePool> toRemove = new ArrayList<>(2);
	private final ArrayList<SmokePool> pools = new ArrayList<>(2);

	public BainDeFumee(Vi6Main main, ObjetsList objet, ObjetsSkins skin, Game game, Player player,PlayerWrapper wrapper) {
		super(main, objet, skin, game, player, wrapper);
	}

	public void tick() {
		for (SmokePool p : toRemove) {
			pools.remove(p);
		}
		toRemove.clear();
		for (SmokePool p : pools) {
			p.tick();
		}
	}
	
	@Override
	public void disable() {
		super.disable();
		for (SmokePool p : pools) {
			p.destroy();
		}
		pools.clear();
	}
	
	public void addToRemovePool(SmokePool p) {
		toRemove.add(p);
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
		cast();
	}

	public void drop() {
		cast();
	}
	
	private void cast() {
		pools.add(new SmokePool(this,Particle.SMOKE_NORMAL,getOwner().getLocation()));
		setCooldown(400);
	}
	
	private static class SmokePool{
		
		private static final float AIR = 78.5398f;
		private static final float DIAMETER = 31.4159f;
		private static final float RAY = 5f;
		private static final Random rng = new Random();
		private static final int LIFETIME=160;
		private static final StatusEffect INVISIBLE = new StatusEffect(Effects.Invisible);
		private static final StatusEffect NODAMAGE = new StatusEffect(Effects.NoDamage);
		
		static {
			rng.setSeed(System.currentTimeMillis());
		}
		
		private final Particle particle;
		private final Location loc;
		private final StatusEffect[] effects;
		private int life=LIFETIME;
		private final ArrayList<PlayerWrapper> inside = new ArrayList<>();
		private final BainDeFumee parent;
		
		private SmokePool(BainDeFumee bain, Particle prtcl, Location l, StatusEffect...eff) {
			particle=prtcl;
			loc=l;
			effects=eff;
			parent=bain;
		}
		
		private void tick() {
			manageEffects();
			showParticle();
			life--;
			if (life<=0) {parent.addToRemovePool(this);destroy();}
		}
		
		private void manageEffects() {
			for (Entry<Player, PlayerWrapper> e : parent.getGame().getPlayerMap().entrySet()) {
				PlayerWrapper w = e.getValue();
				if (e.getKey().getLocation().distanceSquared(loc)<=25 && Math.abs(e.getKey().getLocation().getY()-loc.getY())<=1) {
					if (!inside.contains(w)) {
						w.addStatusEffect(INVISIBLE);
						w.addStatusEffect(NODAMAGE);
						inside.add(w);
					}
				}else {
					if (inside.contains(w)) {
						w.addStatusEffect(INVISIBLE);
						w.addStatusEffect(NODAMAGE);
						inside.remove(w);
					}
				}
			}
		}

		private void showParticle() {
			Location l = new Location(loc.getWorld(),0,0,0);
			for (int i=0;i<AIR;i++) {
				float angle = rng.nextFloat()*DIAMETER;
				float point = rng.nextFloat()*RAY;
				l.setX(loc.getX()+(Math.cos(angle)*point));
				l.setZ(loc.getZ()+(Math.sin(angle)*point));
				l.setY(loc.getY()-1);
				double maxy = l.getY()+2.5;
				while (l.getY()<maxy) {
					if (l.getBlock().getBoundingBox().contains(l.toVector())) {
						l.add(0, 0.1, 0);
					}else{
						l.getWorld().spawnParticle(particle, l, 1,0,0,0,0);
						break;
					};
				}
			}
		}
		
		@SuppressWarnings("unused")
		private StatusEffect[] getEffects() {
			return effects;//TODO REMOVE THIS
		}
		
		private void destroy() {
			for (PlayerWrapper w : inside) {
				w.removeStatusEffect(INVISIBLE);
				w.removeStatusEffect(NODAMAGE);
			}
		}
	}
}
