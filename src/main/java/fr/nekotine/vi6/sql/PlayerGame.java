package fr.nekotine.vi6.sql;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import fr.nekotine.vi6.enums.Team;
import fr.nekotine.vi6.events.PlayerStealEvent;

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
	
	private ArrayList<Object> artefactStolen = new ArrayList<>();
	public PlayerGame(UUID uuid) {
		this.uuid=uuid;
	}
	@EventHandler
	public void playerSteal(PlayerStealEvent e) {
		
	}
}
