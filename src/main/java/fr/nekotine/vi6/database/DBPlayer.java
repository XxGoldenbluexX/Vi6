package fr.nekotine.vi6.database;

import java.nio.ByteBuffer;
import java.util.UUID;

import org.bukkit.entity.Player;

import fr.nekotine.vi6.enums.Team;
import fr.nekotine.vi6.utils.MessageFormater;
import fr.nekotine.vi6.yml.DisplayTexts;

public class DBPlayer {

	private ByteBuffer uuidBuffer;
	
	private int lpGarde = -1;
	
	private int lpVoleur = -1;
	
	private Player player;
	
	private Team team = Team.GARDE;
	
	private int nbVole = -1;
	
	private int nbSecu = -1;
	
	private int lpGain = 0;
	
	public DBPlayer(Player player) {
		this.player = player;
		UUID playerUUID = player.getUniqueId();
		uuidBuffer = ByteBuffer.allocate(16);
		uuidBuffer.putLong(playerUUID.getMostSignificantBits());
		uuidBuffer.putLong(playerUUID.getLeastSignificantBits());
	}
	
	public void notifyLpGain() {
		if (player.isOnline()) {
			player.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage(lpGain<0?"lp_loss":"lp_gain"),
					new MessageFormater("§v", Integer.toString(lpGain))
					));
		}
	}
	
	public int getLpGain() {
		return lpGain;
	}
	
	public void setLpGain(int lpGain) {
		this.lpGain = lpGain;
	}
	
	public byte[] getUUIDBytes() {
		return uuidBuffer.array();
	}

	public int getLpGarde() {
		return lpGarde;
	}

	public void setLpGarde(int lpGarde) {
		this.lpGarde = lpGarde;
	}

	public int getLpVoleur() {
		return lpVoleur;
	}

	public void setLpVoleur(int lpVoleur) {
		this.lpVoleur = lpVoleur;
	}

	public Team getTeam() {
		return team;
	}

	public void setTeam(Team team) {
		this.team = team;
	}

	public int getNbVole() {
		return nbVole;
	}

	public void setNbVole(int nbVole) {
		this.nbVole = nbVole;
	}

	public int getNbSecu() {
		return nbSecu;
	}

	public void setNbSecu(int nbSecu) {
		this.nbSecu = nbSecu;
	}
	
}
