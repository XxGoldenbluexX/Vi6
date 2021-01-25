package fr.nekotine.vi6.sql;

import java.sql.Time;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.event.Listener;

import fr.nekotine.vi6.Artefact;
import fr.nekotine.vi6.enums.Team;

/**
 * Made from Game, this class is used to move information to the SQLInterface
 * 
 * @author XxGoldenbluexX
 * @author Hyez
 *
 */

public class PlayerGame implements Listener{
	private UUID uuid;
	private Team team;
	private String entree;
	private String sortie;
	private UUID uuidTueur;
	private String salleTueur;
	
	//id partie globale
	//id de cette partie
	private HashMap<Artefact, Time> artefactStolen = new HashMap<>();
	private HashMap<Object, Time> objectUsed = new HashMap<>();
	public PlayerGame(UUID uuid) {
		this.uuid=uuid;
	}
	
}
