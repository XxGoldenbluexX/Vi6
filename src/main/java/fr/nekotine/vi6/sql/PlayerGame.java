package fr.nekotine.vi6.sql;

import java.sql.Time;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import fr.nekotine.vi6.enums.Team;
import fr.nekotine.vi6.events.GameEndEvent;

/**
 * Made from Game, this class is used to move information to the SQLInterface
 * 
 * @author XxGoldenbluexX
 * @author Hyez
 *
 */

public class PlayerGame implements Listener{
	private final String gameName;
	private final int idPartie;
	private final UUID playerUUID;
	private final Team team;
	
	private String entree;
	private String sortie;
	private int idPartieTueur;
	private String salleMort;
	
	private HashMap<String, Time> artefactStolen = new HashMap<>();
	private HashMap<String, Time> objectUsed = new HashMap<>();
	
	public PlayerGame(String gameName, UUID playerUUID, int idPartie, Team team) {
		this.gameName=gameName;
		this.playerUUID=playerUUID;
		this.idPartie=idPartie;
		this.team = team;
	}
	@EventHandler
	public void onGameEnd(GameEndEvent e) {
		if(e.getGame().getName()==gameName) {
			int idPartieJoueur = SQLInterface.addPartieJoueur(idPartie, playerUUID, team, entree, sortie, salleMort, idPartieTueur);
			for(String artefactName : artefactStolen.keySet()) {
				SQLInterface.addStealEntry(artefactName, idPartieJoueur, artefactStolen.get(artefactName));
			}
			for(String objetName : objectUsed.keySet()) {
				SQLInterface.addStealEntry(objetName, idPartieJoueur, objectUsed.get(objetName));
			}
			HandlerList.unregisterAll(this);
		}
	}
}
