package fr.nekotine.vi6.wrappers;

import java.util.ArrayList;
import java.util.Iterator;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.enums.PlayerState;
import fr.nekotine.vi6.enums.Team;
import fr.nekotine.vi6.map.Artefact;
import fr.nekotine.vi6.objet.ObjetsList;
import fr.nekotine.vi6.objet.ObjetsSkins;
import fr.nekotine.vi6.statuseffects.Effects;
import fr.nekotine.vi6.statuseffects.StatusEffect;

public class PlayerWrapper {
	
	private static final String READY_PREFIX=ChatColor.GREEN+"☑ ";
	private static final String NOT_READY_PREFIX=ChatColor.RED+"☐ ";
	
	private String currentScoreboardName="";
	private Team team = Team.GARDE;
	private boolean isReady=false;
	private PlayerState state=PlayerState.WAITING;
	private String currentSalle;
	private int money;
	private final Player player;
	private final ArrayList<StatusEffect> statusEffects = new ArrayList<StatusEffect>();
	private final ArrayList<Artefact> stealedObjects = new ArrayList<>();
	private Location thiefSpawnPoint;
	private final Game game;
	private boolean canCapture = false;
	private boolean canEscape = false;
	private ArrayList<ObjetsSkins> selectedSkins = new ArrayList<>();
	public PlayerWrapper(Game game, Player player) {
		this.game=game;
		this.player = player;
		updateScoreboard();
	}
	
	public void destroy() {
		clearStatusEffects();
		player.getScoreboard().resetScores(currentScoreboardName);
	}

	public Team getTeam() {
		return team;
	}
	
	public void setMoney(int money) {
		this.money=money;
	}
	
	public int getMoney() {
		return money;
	}
	
	public boolean isSkinsSelected(ObjetsSkins skin) {
		return selectedSkins.contains(skin);
	}
	
	public ObjetsSkins getSelectedSkin(ObjetsList obj) {
		for(ObjetsSkins skin : selectedSkins) {
			if(skin.getObjet()==obj) return skin;
		}
		return null;
	}
	
	public void clearAllSkinsForObjet(ObjetsList obj) {
		for(ObjetsSkins skin : selectedSkins) {
			if(skin.getObjet()==obj) selectedSkins.remove(skin);
		}
	}
	
	public boolean flipSelected(ObjetsSkins skin) {
		if(selectedSkins.contains(skin)) {
			selectedSkins.remove(skin);
			return false;
		}
		selectedSkins.add(skin);
		return true;
	}
	
	public void changeTeam(Team team) {
		this.team = team;
		updateScoreboard();
	}
	
	public void updateScoreboard() {
		Scoreboard sc = player.getScoreboard();
		sc.resetScores(currentScoreboardName);
		currentScoreboardName=(isReady?READY_PREFIX:NOT_READY_PREFIX)+team.getChatColor()+player.getName();
		sc.getObjective(DisplaySlot.SIDEBAR).getScore(currentScoreboardName).setScore(0);
	}

	public Player getPlayer() {
		return player;
	};
	
	public boolean isReady() {
		return isReady;
	}
	
	public void setReady(boolean ready) {
		isReady=ready;
		updateScoreboard();
	}

	public String getCurrentSalle() {
		return currentSalle;
	}

	public void setCurrentSalle(String currentSalle) {
		this.currentSalle = currentSalle;
	}

	public PlayerState getState() {
		return state;
	}

	public void setState(PlayerState state) {
		this.state = state;
	}

	public ArrayList<Artefact> getStealedArtefactList() {
		return stealedObjects;
	}

	public Location getThiefSpawnPoint() {
		return thiefSpawnPoint;
	}

	public void setThiefSpawnPoint(Location thiefSpawnPoint) {
		this.thiefSpawnPoint = thiefSpawnPoint;
	}

	public Game getGame() {
		return game;
	}

	public boolean isCanCapture() {
		return canCapture;
	}

	public void setCanCapture(boolean canCapture) {
		this.canCapture = canCapture;
	}

	public boolean isCanEscape() {
		return canEscape;
	}

	public void setCanEscape(boolean canEscape) {
		this.canEscape = canEscape;
	}
	
	//STATUS EFFECTS-------------
	
	public void clearStatusEffect(Effects e) {
		Iterator<StatusEffect> ite = statusEffects.iterator();
		while (ite.hasNext()) {
			StatusEffect ef = ite.next();
			if (ef.getEffect()==e) {
				ef.remove();
			}
		}
	}
	
	public void clearStatusEffects() {
		Iterator<StatusEffect> ite = statusEffects.iterator();
		while (ite.hasNext()) {
			ite.next().remove();
		}
	}
	
	public boolean haveEffect(Effects effect) {
		if (Effects.isCounterable(effect)) {
			if (haveEffect(Effects.getCounter(effect))) return false;
		}
		for (StatusEffect e : statusEffects) {
			if (e.getEffect()==effect) return true;
		}
		return false;
	}
	
	public void removeStatusEffect(StatusEffect eff) {
		statusEffects.remove(eff);
		updateEffect(eff.getEffect());
	}
	
	public void addStatusEffect(StatusEffect eff) {
		statusEffects.add(eff);
		updateEffect(eff.getEffect());
	}
	
	public void updateEffect(Effects e) {
		if (haveEffect(e)) return;
	}
	
	//---------------------------
	
}
