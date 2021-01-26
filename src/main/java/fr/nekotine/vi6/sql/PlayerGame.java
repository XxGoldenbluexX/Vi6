package fr.nekotine.vi6.sql;

import java.sql.Time;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import fr.nekotine.vi6.Artefact;
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
	private int idPartieJoueur;
	private final int idPartieGlobale;
	private final UUID uuid;
	private final Team team;
	
	private String entree;
	private String sortie;
	private UUID uuidTueur;
	private String salleTueur;
	
	private HashMap<Artefact, Time> artefactStolen = new HashMap<>();
	private HashMap<Object, Time> objectUsed = new HashMap<>();
	
	public PlayerGame(UUID uuid, int idPartieGlobale, Team team) {
		this.uuid=uuid;
		this.idPartieGlobale=idPartieGlobale;
		this.team = team;
		//créer table Partie Joueur
	}
	@EventHandler(priority = EventPriority.LOWEST)
	public void onGameEnd(GameEndEvent e) {
		//modifier table idPartieJoueur avec entree,sortie,...
	}
}
