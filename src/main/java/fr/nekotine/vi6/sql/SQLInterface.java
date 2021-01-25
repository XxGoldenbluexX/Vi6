package fr.nekotine.vi6.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import fr.nekotine.vi6.Vi6Main;

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
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}
	}
	public boolean addPlayerGameStats(int gameId, PlayerGame pStats) {
		return false;
	}
	
}
