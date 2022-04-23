package fr.nekotine.vi6.objet.list;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.enums.GameState;
import fr.nekotine.vi6.enums.Team;
import fr.nekotine.vi6.objet.ObjetsList;
import fr.nekotine.vi6.objet.ObjetsSkins;
import fr.nekotine.vi6.objet.utils.Objet;
import fr.nekotine.vi6.utils.Vi6Sound;
import fr.nekotine.vi6.wrappers.PlayerWrapper;

public class Pecheur2 extends Objet {
	public Pecheur2(Vi6Main main, ObjetsList objet, ObjetsSkins skin, Game game, Player player, PlayerWrapper wrapper) {
		super(main, objet, skin, game, player, wrapper);
	}
	private static final int WAIT_TIME_TICKS_GUARD = 600;
	private static final int WAIT_TIME_TICKS_THIEF = 500;
	private int delay_left = getFishingTime();
	private boolean fishing = false;
	private static final ObjetsList[] THIEF_FISHABLE = {
			ObjetsList.OMBRE, 
			ObjetsList.GPS,
			ObjetsList.BROUILLEUR_RADIO,
			ObjetsList.IEM,
			ObjetsList.DEADRINGER,
			ObjetsList.ARMURE};
	private static final ObjetsList[] GUARD_FISHABLE = {
			ObjetsList.CHAMP_DE_FORCE,
			ObjetsList.TELEPORTEUR,
			ObjetsList.OMNICAPTEUR,
			ObjetsList.PIEGE_A_DENTS,
			ObjetsList.GLOBE_VOYANT,
			ObjetsList.PIEGE_CAPTEUR,
			ObjetsList.PIEGE_COLLANT,
			ObjetsList.BARRICADE,
			ObjetsList.CAPTEURSISMIQUE};
	private static final Material[] FISHABLE = {
			Material.WATER,
			Material.WATER_CAULDRON
	};
	@Override
	public void tick() {
		if(fishing && getGame().getState()==GameState.Ingame) {
			delay_left--;
			if(delay_left % 20 == 0) Vi6Sound.SPLASH.playForPlayer(getOwner());
			if(delay_left<=0) {
				delay_left = getFishingTime();
				Vi6Sound.SUCCESS.playForPlayer(getOwner());
				ObjetsList objet;
				if(getOwnerWrapper().getTeam()==Team.GARDE) {
					objet = GUARD_FISHABLE[(int)Math.floor(Math.random()*GUARD_FISHABLE.length)];
				}else {
					objet = THIEF_FISHABLE[(int)Math.floor(Math.random()*THIEF_FISHABLE.length)];
				}
				ObjetsList.createObjet(super.getMain(), objet, super.getGame(), super.getOwner(), super.getOwnerWrapper());
			}
		}
	}
	@Override
	public void cooldownEnded() {
	}
	@Override
	public void death() {
		disable();
	}
	@Override
	public void leaveMap() {
		disable();
	}
	@Override
	public void action(PlayerInteractEvent e) {
	}
	@Override
	public void drop() {
	}
	public boolean canFish() {
		for(Material fishable : FISHABLE) {
			if(getOwner().getLocation().getBlock().getType()==fishable) {
				return true;
			}
		}
		BlockData bd = getOwner().getLocation().getBlock().getBlockData();
		if(bd instanceof Waterlogged) {
			Waterlogged wl = (Waterlogged)bd;
			if(wl.isWaterlogged()) {
				return true;
			}
		}
		return false;
	}
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		if(e.getPlayer().equals(getOwner()) && getOwner().getGameMode()!=GameMode.SPECTATOR) {
			
			if(fishing) {
				if(!canFish()) {
					fishing=false;
					delay_left = getFishingTime();
				}
			}else if(canFish()) {
				fishing=true;
			}
		}
	}
	public int getFishingTime() {
		if(getOwnerWrapper().getTeam()==Team.GARDE) {
			return WAIT_TIME_TICKS_GUARD;
		}else {
			return WAIT_TIME_TICKS_THIEF;
		}
	}
}
