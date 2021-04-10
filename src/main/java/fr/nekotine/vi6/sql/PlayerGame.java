package fr.nekotine.vi6.sql;

import java.sql.Time;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.enums.PlayerState;
import fr.nekotine.vi6.enums.Team;
import fr.nekotine.vi6.events.GameEndEvent;
import fr.nekotine.vi6.events.PlayerEnterMapEvent;
import fr.nekotine.vi6.events.PlayerEscapeEvent;
import fr.nekotine.vi6.events.PlayerStealEvent;
import fr.nekotine.vi6.objet.ObjetsList;
import fr.nekotine.vi6.objet.utils.Objet;
import fr.nekotine.vi6.wrappers.PlayerWrapper;

/**
 * Made from Game, this class is used to move information to the SQLInterface
 * 
 * @author XxGoldenbluexX
 * @author Hyez
 *
 */

public class PlayerGame implements Listener{
	private final String gameName;
	private final UUID playerUUID;
	private final Team team;
	
	private String entree;
	private String sortie;
	private UUID tueurUUID;
	private String salleMort;
	private HashMap<String, Time> artefactStolen = new HashMap<>();

	public PlayerGame(String gameName, UUID playerUUID, Team team) {
		this.gameName=gameName;
		this.playerUUID=playerUUID;
		this.team = team;
	}
	@EventHandler
	public void onGameEnd(GameEndEvent e) {
		if(e.getGame().getName()==gameName) {
			if(!e.isForced()) {
				int idPartieJoueur = SQLInterface.addPartieJoueur(e.getIdPartie(), playerUUID, team, entree, sortie, salleMort, tueurUUID);
				for(String artefactName : artefactStolen.keySet()) {
					SQLInterface.addStealEntry(artefactName, idPartieJoueur, artefactStolen.get(artefactName));
				}
				HashMap<ObjetsList, Integer> objectUsed = new HashMap<>();
				for(Objet obj : e.getGame().getObjets()) {
					if(obj.getOwner().getUniqueId()==playerUUID) {
						if(objectUsed.containsKey(obj.getObjetType())){
							objectUsed.replace(obj.getObjetType(), objectUsed.get(obj.getObjetType())+1);
						}else {
							objectUsed.put(obj.getObjetType(), 1);
						}
					}
				}
				for(ObjetsList objet : objectUsed.keySet()) {
					SQLInterface.addUtiliseEntry(idPartieJoueur, objet.toString(), objectUsed.get(objet));
				}
			HandlerList.unregisterAll(this);
			}
		}
	}
	@EventHandler
	public void playerStealArtefact(PlayerStealEvent e) {
		if(e.getPlayer().getUniqueId()==playerUUID) {
			artefactStolen.put(e.getArtefact().getName(), new Time(System.currentTimeMillis()-e.getGame().getStartTime()));
		}
	}
	@EventHandler
	public void playerEnterMap(PlayerEnterMapEvent e) {
		if(e.getPlayer().getUniqueId()==playerUUID) {
			entree=e.getEntreeName();
		}
	}
	@EventHandler
	public void playerLeaveMap(PlayerEscapeEvent e) {
		if(e.getGame().getName()==gameName && e.getPlayer().getUniqueId().equals(playerUUID)) sortie=e.getSortie().getDisplayName();
	}
	@EventHandler(priority = EventPriority.LOWEST)
	public void playerDeath(PlayerDeathEvent e) {
		PlayerWrapper wrap = Vi6Main.getGame(gameName).getWrapper(e.getEntity());
		if(e.getEntity().getUniqueId()==playerUUID && wrap.getState()==PlayerState.INSIDE) {
			salleMort=wrap.getCurrentSalle();
			if(e.getEntity().getKiller()!=null) {
				tueurUUID=e.getEntity().getKiller().getUniqueId();
			}
		}
	}
}
