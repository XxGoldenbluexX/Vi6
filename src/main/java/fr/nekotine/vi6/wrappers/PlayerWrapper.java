package fr.nekotine.vi6.wrappers;

import java.util.ArrayList;

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
	
	private static final String READY_PREFIX = ChatColor.GREEN + "☑ ";
	private static final String NOT_READY_PREFIX = ChatColor.RED + "☐ ";
	private String currentScoreboardName = "";
	private Team team = Team.GARDE;
	private boolean isReady = false;
	private PlayerState state = PlayerState.WAITING;
	private String currentSalle;
	private int money;
	private final Player player;
	private final ArrayList<StatusEffect> statusEffects = new ArrayList<>();
	private final ArrayList<Artefact> stealedObjects = new ArrayList<>();
	private Location thiefSpawnPoint;
	private final Game game;
	private boolean canCapture = false;
	private boolean canEscape = false;
	private ArrayList<ObjetsSkins> selectedSkins = new ArrayList<>();

	public PlayerWrapper(Game game, Player player) {
		this.game = game;
		this.player = player;
		updateScoreboard();
	}

	public void destroy() {
		clearStatusEffects();
		this.player.getScoreboard().resetScores(this.currentScoreboardName);
	}

	public Team getTeam() {
		return this.team;
	}

	public void setMoney(int money) {
		this.money = money;
	}

	public int getMoney() {
		return this.money;
	}

	public boolean isSkinsSelected(ObjetsSkins skin) {
		return this.selectedSkins.contains(skin);
	}

	public ObjetsSkins getSelectedSkin(ObjetsList obj) {
		for (ObjetsSkins skin : this.selectedSkins) {
			if (skin.getObjet() == obj)
				return skin;
		}
		return null;
	}

	public void clearAllSkinsForObjet(ObjetsList obj) {
		for (ObjetsSkins skin : this.selectedSkins) {
			if (skin.getObjet() == obj)
				this.selectedSkins.remove(skin);
		}
	}

	public boolean flipSelected(ObjetsSkins skin) {
		if (this.selectedSkins.contains(skin)) {
			this.selectedSkins.remove(skin);
			return false;
		}
		this.selectedSkins.add(skin);
		return true;
	}

	public void changeTeam(Team team) {
		this.team = team;
		updateScoreboard();
	}

	public void updateScoreboard() {
		Scoreboard sc = this.player.getScoreboard();
		sc.resetScores(this.currentScoreboardName);
		this.currentScoreboardName = (this.isReady ? READY_PREFIX : NOT_READY_PREFIX)+this.team.getChatColor()+player.getName();
		sc.getObjective(DisplaySlot.SIDEBAR).getScore(this.currentScoreboardName).setScore(0);
	}

	public Player getPlayer() {
		return this.player;
	}

	public boolean isReady() {
		return this.isReady;
	}

	public void setReady(boolean ready) {
		this.isReady = ready;
		updateScoreboard();
	}

	public String getCurrentSalle() {
		return this.currentSalle;
	}

	public void setCurrentSalle(String currentSalle) {
		this.currentSalle = currentSalle;
	}

	public PlayerState getState() {
		return this.state;
	}

	public void setState(PlayerState state) {
		this.state = state;
	}

	public ArrayList<Artefact> getStealedArtefactList() {
		return this.stealedObjects;
	}

	public Location getThiefSpawnPoint() {
		return this.thiefSpawnPoint;
	}

	public void setThiefSpawnPoint(Location thiefSpawnPoint) {
		this.thiefSpawnPoint = thiefSpawnPoint;
	}

	public Game getGame() {
		return this.game;
	}

	public boolean isCanCapture() {
		return this.canCapture;
	}

	public void setCanCapture(boolean canCapture) {
		this.canCapture = canCapture;
	}

	public boolean isCanEscape() {
		return this.canEscape;
	}

	public void setCanEscape(boolean canEscape) {
		this.canEscape = canEscape;
	}

	public void clearStatusEffect(Effects e) {
		ArrayList<StatusEffect> eff = new ArrayList<>(this.statusEffects);
		for (StatusEffect ef : eff) {
			if (ef.getEffect() == e)
				ef.remove();
		}
	}

	public void clearStatusEffects() {
		ArrayList<StatusEffect> temp = new ArrayList<>(this.statusEffects);
		for (StatusEffect e : temp)
			e.remove();
	}

	public boolean haveEffect(Effects effect) {
		if (Effects.isCounterable(effect) && haveEffect(Effects.getCounter(effect)))
			return false;
		for (StatusEffect e : this.statusEffects) {
			if (e.getEffect() == effect)
				return true;
		}
		return false;
	}

	public boolean haveStatusEffect(StatusEffect eff) {
		return this.statusEffects.contains(eff);
	}

	public void removeStatusEffect(StatusEffect eff) {
		this.statusEffects.remove(eff);
		eff.setWrapper(null);
		updateRemoveEffect(eff.getEffect());
	}

	public void addStatusEffect(StatusEffect eff) {
		updateAddEffect(eff.getEffect());
		this.statusEffects.add(eff);
		eff.setWrapper(this);
	}

	private void updateRemoveEffect(Effects e) {
		if (haveEffect(e))
			return;
		if (Effects.isCounterable(e) && haveEffect(Effects.getCounter(e)))
			return;
		e.disable(this.player, this);
		if (Effects.isCountering(e)) {
			Effects countered = Effects.getCountered(e);
			if (haveEffect(countered))
				countered.enable(this.player, this);
		}
	}

	private void updateAddEffect(Effects e) {
		if (haveEffect(e))
			return;
		if (Effects.isCounterable(e) && haveEffect(Effects.getCounter(e)))
			return;
		e.enable(this.player, this);
		if (Effects.isCountering(e)) {
			Effects countered = Effects.getCountered(e);
			if (haveEffect(countered))
				countered.disable(this.player, this);
		}
	}
}