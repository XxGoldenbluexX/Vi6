package fr.nekotine.vi6.wrappers;

import java.util.ArrayList;
import java.util.Iterator;

import org.bukkit.entity.Player;

import fr.nekotine.vi6.enums.PlayerState;
import fr.nekotine.vi6.enums.Team;
import fr.nekotine.vi6.map.Artefact;
import fr.nekotine.vi6.statuseffects.Effects;
import fr.nekotine.vi6.statuseffects.StatusEffect;

public class PlayerWrapper {
	
	private Team team = Team.GARDE;
	private boolean isReady=false;
	private PlayerState state=PlayerState.WAITING;
	private String currentSalle;
	private int money;
	private final Player player;
	private final ArrayList<StatusEffect> statusEffects = new ArrayList<StatusEffect>();
	private final ArrayList<Artefact> stealedObjects = new ArrayList<>();
	
	public PlayerWrapper(Player player) {
		this.player = player;
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
	
	public void changeTeam(Team team) {
		this.team = team;
	}

	public Player getPlayer() {
		return player;
	};
	
	public boolean isReady() {
		return isReady;
	}
	
	public void setReady(boolean ready) {
		isReady=ready;
	}
	
	public void removeStatusEffect(StatusEffect eff) {
		statusEffects.remove(eff);
	}
	
	public void addStatusEffect(StatusEffect eff) {
		statusEffects.add(eff);
		eff.setWrapper(this);
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
	
}
