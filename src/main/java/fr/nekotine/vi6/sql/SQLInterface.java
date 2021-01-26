package fr.nekotine.vi6.sql;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.UUID;

import fr.nekotine.vi6.Artefact;
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
	public int addPartie(Date date, Time duree, int argent, GameType type, String nomCarte) {
		try {
			Statement sttmt = c.createStatement();
			ResultSet rs = sttmt.executeQuery( "SELECT NVL(MAX(Id_Partie),-1) FROM Partie;" );
			int idPartie = rs.getInt("Id_Partie")+1;
			String sql = "INSERT INTO Partie (Id_Partie,Date_Partie,Duree,Argent,Type,Nom_Carte) "+ 
							"VALUES ("+idPartie+","+date+","+duree+","+argent+","+type.toString()+","+nomCarte+")";
			sttmt.executeUpdate(sql);
			sttmt.close();
			c.commit();
			return idPartie;
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}
	public int addPartieJoueur(int idPartie, UUID playerUUID, Team team, String entree, String sortie, String salleMort, int idPartieTueur) {
		try {
			Statement sttmt = c.createStatement();
			ResultSet rs = sttmt.executeQuery( "SELECT NVL(MAX(Id_PartieJoueur),-1) FROM PartieJoueur;" );
			int idPartieJoueur = rs.getInt("Id_PartieJoueur")+1;
			String sql = "INSERT INTO PartieJoueur (Id_PartieJoueur,Id_PartieTueur,Nom_Salle,Id_Partie,UUID_Joueur,Nom_Equipe,Nom_Entree,Nom_Sortie) "+ 
							"VALUES ("+idPartieJoueur+","+idPartieTueur+","+salleMort+","+idPartie+","+playerUUID.toString()+","+team.toString()+","+entree+","+sortie+")";
			sttmt.executeUpdate(sql);
			sttmt.close();
			c.commit();
			return idPartieJoueur;
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}
	public void addStealEntry(Artefact artefact, int idPartieJoueur, Time temps) {
		try {
			Statement sttmt = c.createStatement();
			String sql = "INSERT INTO Vole (Nom_Artefact,Id_PartieJoueur,Temps) "+ 
							"VALUES ("+artefact.getName()+","+idPartieJoueur+","+temps+")";
			sttmt.executeUpdate(sql);
			sttmt.close();
			c.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void addUtiliseEntry(int idPartieJoueur, String objetName, Time temps) {
		try {
			Statement sttmt = c.createStatement();
			String sql = "INSERT INTO Utilise (Id_PartieJoueur,Nom_Objet,Temps) "+ 
							"VALUES ("+idPartieJoueur+","+objetName+","+temps+")";
			sttmt.executeUpdate(sql);
			sttmt.close();
			c.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void updatePartie(int idPartie, Time duree) {
		Statement sttmt;
		try {
			sttmt = c.createStatement();
			String sql = "UPDATE Partie SET Duree = "+duree+
							" WHERE Id_Partie="+idPartie;
			sttmt.executeUpdate(sql);
			sttmt.close();
			c.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void updatePartieJoueur(int idPartieJoueur, String entree, String sortie, String salleMort, int idPartieTueur) {
		Statement sttmt;
		try {
			sttmt = c.createStatement();
			String sql = "UPDATE PartieJoueur SET Nom_Entree = "+entree+
							" , Nom_Sortie = "+sortie+
							" , Nom_Salle = "+salleMort+
							" , Id_PartieTueur = "+idPartieTueur+
							" WHERE Id_PartieJoueur="+idPartieJoueur;
			sttmt.executeUpdate(sql);
			sttmt.close();
			c.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
