package fr.nekotine.vi6.sql;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.UUID;

import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.enums.GameType;
import fr.nekotine.vi6.enums.Team;

/**
 * This class provide severeal methods to add/remove/modify stats from the SQL database.
 * 
 * @author XxGoldenbluexX
 * @author Hyez
 *
 */

public class SQLInterface {
	private Connection c;
	/**
	 * Add player stats for a specified game to the SQL database.
	 * 
	 * @param gameId ID of the game played.
	 * @param pStats Stats of the player in this game.
	 * @return return whether yes if stats was succefully added, else false.
	 */
	public SQLInterface(Vi6Main main) {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
			return;
		}
		try {
			c = DriverManager.getConnection("jdbc:sqlite:"+main.getDataFolder().getAbsolutePath()+"/Vi6Database.db");
			c.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}
	}
	public void closeConnection() {
		try {
			c.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public int addPartie(Date date, Time duration, int money, GameType type, String mapName) {
		try {
			Statement sttmt = c.createStatement();
			ResultSet rs = sttmt.executeQuery( "SELECT NVL(MAX(Id_Partie),-1) FROM Partie;" );
			int idPartie = rs.getInt("Id_Partie")+1;
			String sql = "INSERT INTO Partie (Id_Partie,Date_Partie,Duree,Argent,Type,Nom_Carte) "+ 
							"VALUES ("+idPartie+","+date+","+duration+","+money+","+type.toString()+","+mapName+")";
			sttmt.executeUpdate(sql);
			sttmt.close();
			c.commit();
			return idPartie;
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}
	public int addPartieJoueur(int gameId, UUID playerUUID, Team team, String entrance, String exit, String killRoom, int killerPlayerGameId) {
		return 0;
	}
	
}
