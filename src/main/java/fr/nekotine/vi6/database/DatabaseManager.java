package fr.nekotine.vi6.database;

import java.io.File;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.mariadb.jdbc.MariaDbPoolDataSource;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.enums.Team;
import fr.nekotine.vi6.wrappers.PlayerWrapper;

public class DatabaseManager {
	
	private static String connectionURL;
	private static String username;
	private static String password;
	private static int MAXIMAL_LP_CHANGE = 100;
	private static int MINIMAL_LP_CHANGE = 25;
	
	public static void sendPreparationEndData(Game g) {
			//Retrieve data here to avoid sync issues
			Timestamp datetime = new Timestamp(System.currentTimeMillis());
			boolean isTest = g.isTest();
			boolean isRanked = g.isRanked();
			String mapName = g.getMapName();
			int mapNbArtefact = g.getMap().getArtefactList().size();
			@SuppressWarnings("unchecked")
			Set<Entry<Player,PlayerWrapper>> playerSet = ((HashMap<Player,PlayerWrapper>)g.getPlayerMap().clone()).entrySet();
			new BukkitRunnable() {
				@Override
				public void run() {
					try (
							MariaDbPoolDataSource source = new MariaDbPoolDataSource();
							){
						source.setUser(username);
						source.setPassword(password);
						source.setUrl(connectionURL);
					try (
							Connection connection = source.getConnection();
							PreparedStatement st_checkMap = connection.prepareStatement(
									"SELECT ID, nbArtefact FROM map WHERE label = (?)"
									);
							PreparedStatement st_addMap = connection.prepareStatement(
									"INSERT INTO map(label,nbArtefact) values (?,?)",Statement.RETURN_GENERATED_KEYS
									);
							PreparedStatement st_updateMap = connection.prepareStatement(
									"UPDATE map SET nbArtefact=? WHERE ID=?"
									);
							PreparedStatement st_addRound = connection.prepareStatement(
									"INSERT INTO round(StartAt,IsTest,IsRanked,ID_Map) values (?,?,?,?)",Statement.RETURN_GENERATED_KEYS
									);
							PreparedStatement st_chkPlayer = connection.prepareStatement(
									"SELECT Username FROM player WHERE UUID = (?)"
									);
							PreparedStatement st_addPlayer = connection.prepareStatement(
									"INSERT INTO player(UUID,Username) values (?,?)"
									);
							PreparedStatement st_modifPlayer = connection.prepareStatement(
									"UPDATE player SET Username = (?) WHERE UUID = (?)"
									);
							PreparedStatement st_addParticipation = connection.prepareStatement(
									"INSERT INTO participation(ID_Round,UUID_Player,IsThief) values (?,?,?)"
									);
							PreparedStatement st_addPurchase = connection.prepareStatement(
									"INSERT INTO purchase(ID_Participation,ID_Item,NumberPurchased) values (?,?,?)"
									);
							){
						///MAP ROUTINE
						//Check existing
						st_checkMap.setString(1, mapName);
						ResultSet set = st_checkMap.executeQuery();
						int id = -1;
						int nbArtefact = 0;
						while (set.next()) {
							id = set.getInt(1);
							nbArtefact = set.getInt(2);
						}
						if (id==-1) {
							st_addMap.setString(1, mapName);
							st_addMap.setInt(2, mapNbArtefact);
							st_addMap.execute();
							set = st_addMap.getGeneratedKeys();
							while (set.next()) {
								id = set.getInt(1);
							}
						}else {
							if (mapNbArtefact!=nbArtefact) {
								st_updateMap.setInt(1, mapNbArtefact);
								st_updateMap.setInt(2, id);
								st_updateMap.execute();
							}
						}
						///ADD ROUND
						st_addRound.setTimestamp(1, datetime);
						st_addRound.setBoolean(2, isTest);
						st_addRound.setBoolean(3, isRanked);
						st_addRound.setInt(4, id);//attention à la variable id
						st_addRound.execute();
						set = st_addRound.getGeneratedKeys();
						id = -1;
						while (set.next()) {
							id = set.getInt(1);
						}
						g.setIdPartie(id);
						for (Entry<Player, PlayerWrapper> entry : playerSet) {
							//ADD PLAYERS
							Player p = entry.getKey();
							UUID playerUUID = p.getUniqueId();
							ByteBuffer uuidbuffer = ByteBuffer.allocate(16);
							uuidbuffer.putLong(playerUUID.getMostSignificantBits());
							uuidbuffer.putLong(playerUUID.getLeastSignificantBits());
							st_chkPlayer.setBytes(1,uuidbuffer.array());
							set = st_chkPlayer.executeQuery();
							boolean iterated = false;
							while (set.next()) {
								iterated = true;
								if (!p.getName().contentEquals(set.getString(1))) {
									st_modifPlayer.setString(1, p.getName());
									st_modifPlayer.setBytes(2,uuidbuffer.array());
									st_modifPlayer.execute();
								};
							}
							if (!iterated) {
								st_addPlayer.setBytes(1,uuidbuffer.array());
								st_addPlayer.setString(2, p.getName());
								st_addPlayer.execute();
							}
							//ADD Participation
							st_addParticipation.setInt(1, id);
							st_addParticipation.setBytes(2, uuidbuffer.array());
							st_addParticipation.setBoolean(3, entry.getValue().getTeam()==Team.VOLEUR);
							st_addParticipation.execute();
						}
				    }catch (SQLException e) {
				    	new BukkitRunnable() {
							@Override
							public void run() {
								Vi6Main.log.warning("Asynchronous database exception when sending preparation phase data:"+e.getMessage());
							}
				    	}.runTask(Vi6Main.main);
				    }
				}catch(SQLException e) {
					Vi6Main.log.warning("Synchronous database exception when sending preparation phase data:"+e.getMessage());
				}
			}
		}.runTaskAsynchronously(Vi6Main.main);
	}
	
	public static void sendRoundEndData(Game g,boolean forced) {
		//Retrieve data here to avoid sync issues
		Timestamp datetime = new Timestamp(System.currentTimeMillis());
		int gameId = g.getIdPartie();
		String mapName = g.getMapName();
		int mapNbArtefact = g.getMap().getArtefactList().size();
		boolean isTest = g.isTest();
		boolean isRanked = g.isRanked();
		@SuppressWarnings("unchecked")
		Set<Entry<Player,PlayerWrapper>> playerSet = ((HashMap<Player, PlayerWrapper>) g.getPlayerMap().clone()).entrySet();
		new BukkitRunnable() {
			@Override
			public void run() {
				try (
						MariaDbPoolDataSource source = new MariaDbPoolDataSource();
						){
					source.setUser(username);
					source.setPassword(password);
					source.setUrl(connectionURL);
				try (
						Connection connection = source.getConnection();
						PreparedStatement st_getMapId = connection.prepareStatement(
								"SELECT ID FROM map WHERE label = (?)"
								);
						PreparedStatement st_getLpgarde = connection.prepareStatement(
								"SELECT lpGarde FROM player WHERE UUID = ?"
								);
						PreparedStatement st_setLpgarde = connection.prepareStatement(
								"UPDATE player SET lpGarde = ? WHERE UUID = ?"
								);
						PreparedStatement st_getLpvoleur = connection.prepareStatement(
								"SELECT lpVoleur FROM player WHERE UUID = ?"
								);
						PreparedStatement st_setLpvoleur = connection.prepareStatement(
								"UPDATE player SET lpVoleur = ? WHERE UUID = ?"
								);
						PreparedStatement st_getMoySecuPerMap = connection.prepareStatement(
								"SELECT SUM(nbSecuredArtefact)/(map.nbArtefact * COUNT(DISTINCT round.ID)) FROM participation,round,map WHERE ID_round = round.ID and"
								+ " round.ID_map=map.ID and map.ID = ? and round.isFinished=true and round.isRanked=true and round.isAborted=false and round.isTest=false"
								);
						PreparedStatement st_updtRound = connection.prepareStatement(
								"UPDATE round SET isFinished=?, isAborted=?, stopAt=? WHERE ID = ?"
								);
						PreparedStatement st_updtParticipation = connection.prepareStatement(
								"UPDATE participation SET nbStolenArtefact=?, nbSecuredArtefact=?, nbKill=? WHERE UUID_player = ? and ID_round = ?"
								);
						){
					int mapid = -1;
					double moySecu = -1d;
					int totalSecu = 0;
					st_getMapId.setString(1, mapName);
					ResultSet set = st_getMapId.executeQuery();
					while(set.next()) {
						mapid = set.getInt(1);
					}
					Vi6Main.log.warning("mapid="+mapid);
					if (mapid!=-1) {
						st_getMoySecuPerMap.setInt(1, mapid);
						set = st_getMoySecuPerMap.executeQuery();
						while (set.next()) {
							moySecu = set.getDouble(1);
						}
					}
					Vi6Main.log.warning("mySecu="+moySecu);
					int nbgarde = 0;
					int nbvoleur = 0;
					int lpgarde = 0;
					int lpvoleur = 0;
					int lpdiff = 0;
					int lp = -1;
					Map<Player,Integer> lpindex = new HashMap<>();
					//UPDATE PARTICIPATION AND MODIFY LP
					for (Entry<Player, PlayerWrapper> entry : playerSet) {
						Player p = entry.getKey();
						PlayerWrapper w = entry.getValue();
						int vole = w.getStealedArtefactList().size();
						int secu = w.getSecuredArtefactList().size();
						totalSecu += secu;
						UUID playerUUID = p.getUniqueId();
						ByteBuffer uuidbuffer = ByteBuffer.allocate(16);
						uuidbuffer.putLong(playerUUID.getMostSignificantBits());
						uuidbuffer.putLong(playerUUID.getLeastSignificantBits());
						byte[] array = uuidbuffer.array();
						if (w.getTeam()==Team.GARDE) {
							nbgarde++;
							st_getLpgarde.setBytes(1,array);
							set = st_getLpgarde.executeQuery();
							while (set.next()) {
								Vi6Main.log.warning("getting lp garde");
								lp = set.getInt(1);
							}
							lp = lp<0?0:lp;
							lpindex.put(p, lp);
							lpgarde += lp;
						}else {
							nbvoleur++;
							st_getLpvoleur.setBytes(1,array);
							set = st_getLpvoleur.executeQuery();
							while (set.next()) {
								lp = set.getInt(1);
							}
							lp = lp<0?0:lp;
							lpindex.put(p, lp);
							lpvoleur += lp;
						}
						st_updtParticipation.setInt(1, vole);
						st_updtParticipation.setInt(2, secu);
						st_updtParticipation.setInt(3, 0);
						st_updtParticipation.setBytes(4,array);
						st_updtParticipation.setInt(5, gameId);
						st_updtParticipation.execute();
					}
					lpgarde/=nbgarde;
					lpvoleur/=nbvoleur;
					lpdiff = lpgarde-lpvoleur; //+garde -voleur
					double ratioSecu = ((double)totalSecu)/((double)mapNbArtefact);
					double temp = (lpdiff<0?-lpdiff:lpdiff)/250d;
					double ratioAntiSmurf = 1/(temp==0?1:temp);
					ratioAntiSmurf = ratioAntiSmurf>1?1:ratioAntiSmurf;
					int lpChange = 0;
					Vi6Main.log.warning("ratioSecu="+ratioSecu);
					Vi6Main.log.warning("ratioAntiSmurf="+ratioAntiSmurf);
					if (moySecu>=0 && !isTest && !forced && isRanked) {
						if (ratioSecu>moySecu) {
							lpChange = lerp(MINIMAL_LP_CHANGE,(ratioSecu-moySecu)/(1-moySecu),MAXIMAL_LP_CHANGE);
						}else if (ratioSecu<moySecu) {
							lpChange = lerp(MINIMAL_LP_CHANGE,(moySecu-ratioSecu)/(moySecu),MAXIMAL_LP_CHANGE);
						}
						Vi6Main.log.warning("lpchange="+lpChange);
						for (Entry<Player,PlayerWrapper> e : playerSet) {
							Player p = e.getKey();
							lp = lpindex.get(p);
							UUID playerUUID = p.getUniqueId();
							ByteBuffer uuidbuffer = ByteBuffer.allocate(16);
							uuidbuffer.putLong(playerUUID.getMostSignificantBits());
							uuidbuffer.putLong(playerUUID.getLeastSignificantBits());
							Vi6Main.log.warning("routine for "+p.getName());
							if (e.getValue().getTeam()==Team.GARDE) {
								if (ratioSecu>moySecu) {
									if (lpdiff<0){
										lp -= lpChange*ratioAntiSmurf;
									}else {
										lp -= lpChange;
									}
									st_setLpgarde.setInt(1, lp<0?0:lp);
									st_setLpgarde.setBytes(2, uuidbuffer.array());
									st_setLpgarde.execute();
									Vi6Main.log.warning("lp remove "+lp);
								}else if (ratioSecu<moySecu) {
									if (lpdiff>0){
										lp += lpChange*ratioAntiSmurf;
									}else {
										lp += lpChange;
									}
									st_setLpgarde.setInt(1, lp<0?0:lp);
									st_setLpgarde.setBytes(2, uuidbuffer.array());
									st_setLpgarde.execute();
									Vi6Main.log.warning("lp gain "+lp);
								}
							}else {
								if (ratioSecu>moySecu) {
									if (lpdiff<0){
										lp += lpChange*ratioAntiSmurf;
									}else {
										lp += lpChange;
									}
									st_setLpvoleur.setInt(1, lp<0?0:lp);
									st_setLpvoleur.setBytes(2, uuidbuffer.array());
									st_setLpvoleur.execute();
									Vi6Main.log.warning("lp gain "+lp);
								}else if (ratioSecu<moySecu) {
									if (lpdiff>0){
										lp -= lpChange*ratioAntiSmurf;
									}else {
										lp -= lpChange;
									}
									st_setLpvoleur.setInt(1, lp<0?0:lp);
									st_setLpvoleur.setBytes(2, uuidbuffer.array());
									st_setLpvoleur.execute();
									Vi6Main.log.warning("lp remove "+lp);
								}
							}
						}
					}
					//UPDATE ROUND
					st_updtRound.setBoolean(1, true);
					st_updtRound.setBoolean(2, forced);
					st_updtRound.setTimestamp(3, datetime);
					st_updtRound.setInt(4, gameId);
					st_updtRound.execute();
			    }catch (SQLException e) {
			    	new BukkitRunnable() {
						@Override
						public void run() {
							Vi6Main.log.warning("Asynchronous database exception when sending preparation phase data:"+e.getMessage());
						}
			    	}.runTask(Vi6Main.main);
			    }
			}catch(SQLException e) {
				Vi6Main.log.warning("Synchronous database exception when sending preparation phase data:"+e.getMessage());
			}
		}
	}.runTaskAsynchronously(Vi6Main.main);
	}
	
	private static int lerp(int min, double ratio, int max) {
		return (int) (min + ((max-min)*ratio));
	}

	public static void loadConfig(File dbfile) {
		if (dbfile.exists()) {
			YamlConfiguration config = YamlConfiguration.loadConfiguration(dbfile);
			MariaDbPoolDataSource source = new MariaDbPoolDataSource();
			String host = config.getString("host","");
			String port = config.getString("port","");
			String dbname = config.getString("database","");
			String usrnm = config.getString("username","");
			String pwd = config.getString("password","");
			String url = "jdbc:mariadb://"+host+":"+port+"/"+dbname;
			try {
				source.setUser(usrnm);
			} catch (SQLException e) {
				Vi6Main.log.warning("Invalid username to connect to database:"+usrnm);
				source.close();
				return;
			}
			try {
				source.setPassword(pwd);
			} catch (SQLException e) {
				Vi6Main.log.warning("Invalid password to connect to database:"+pwd);
				source.close();
				return;
			}
			try {
				source.setUrl(url);
			} catch (SQLException e) {
				Vi6Main.log.warning("Invalid url to connect to database:"+url);
				source.close();
				return;
			}
			source.close();
			new BukkitRunnable() {
				@Override
				public void run() {
					try (MariaDbPoolDataSource src = new MariaDbPoolDataSource();) {
						src.setUser(usrnm);
						src.setPassword(pwd);
						src.setUrl(url);
						try(Connection conn = src.getConnection()){
					        if (!conn.isValid(1)) {
					        	throw new SQLException("Could not establish a valid database connection.");
					        }else {
					        	new BukkitRunnable() {
									@Override
									public void run() {
										Vi6Main.log.finest("Database connection test worked succefully");
									}
						    	}.runTask(Vi6Main.main);
					        }
						}
				    }catch (SQLException e) {
				    	new BukkitRunnable() {
							@Override
							public void run() {
								Vi6Main.log.warning("Unable to connect to database:"+e.getMessage());
							}
				    	}.runTask(Vi6Main.main);
					}
				}
			}.runTaskAsynchronously(Vi6Main.main);
			Vi6Main.log.info("Database connection settings succefully imported from "+dbfile.getName());
			connectionURL = url;
			username = usrnm;
			password = pwd;
			return;
		}
	}
	
	
	
}
