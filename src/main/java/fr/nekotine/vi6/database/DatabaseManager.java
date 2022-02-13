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
	
	public static void sendPreparationEndData(Game g) {
			//Retrieve data here to avoid sync issues
			Timestamp datetime = new Timestamp(System.currentTimeMillis());
			boolean isTest = g.isTest();
			boolean isRanked = g.isRanked();
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
						//ADD ROUND
						st_addRound.setTimestamp(1, datetime);
						st_addRound.setBoolean(2, isTest);
						st_addRound.setBoolean(3, isRanked);
						st_addRound.setInt(4, 1);
						st_addRound.execute();
						ResultSet set = st_addRound.getGeneratedKeys();
						int id = -1;
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
						PreparedStatement st_updtRound = connection.prepareStatement(
								"UPDATE round SET isFinished=?, isAborted=?, stopAt=?, nbStolenArtefact=?, nbSecuredArtefact=? WHERE ID = ?"
								);
						PreparedStatement st_updtParticipation = connection.prepareStatement(
								"UPDATE participation SET nbStolenArtefact=?, nbSecuredArtefact=? WHERE UUID_player = ? and ID_round = ?"
								);
						){
					//UPDATE PARTICIPATION
					int totalVole = 0;
					int totalSecu = 0;
					for (Entry<Player, PlayerWrapper> entry : playerSet) {
						Player p = entry.getKey();
						PlayerWrapper w = entry.getValue();
						int vole = w.getStealedArtefactList().size();
						int secu = w.isEscaped()?vole:0;
						totalVole += vole;
						totalSecu += secu;
						UUID playerUUID = p.getUniqueId();
						ByteBuffer uuidbuffer = ByteBuffer.allocate(16);
						uuidbuffer.putLong(playerUUID.getMostSignificantBits());
						uuidbuffer.putLong(playerUUID.getLeastSignificantBits());
						st_updtParticipation.setInt(1, vole);
						st_updtParticipation.setInt(2, secu);
						st_updtParticipation.setBytes(3,uuidbuffer.array());
						st_updtParticipation.setInt(4, gameId);
						st_updtParticipation.execute();
					}
					//UPDATE ROUND
					st_updtRound.setBoolean(1, true);
					st_updtRound.setBoolean(2, forced);
					st_updtRound.setTimestamp(3, datetime);
					st_updtRound.setInt(4, totalVole);
					st_updtRound.setInt(5, totalSecu);
					st_updtRound.setInt(6, gameId);
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
	
	public static void addTestItem() {
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
