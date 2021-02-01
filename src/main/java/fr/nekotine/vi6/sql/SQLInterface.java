package fr.nekotine.vi6.sql;

import java.io.File;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.UUID;

import fr.nekotine.vi6.enums.Team;

/**
 * This class provide severeal methods to add/remove/modify stats from the SQL database.
 * 
 * @author XxGoldenbluexX
 * @author Hyez
 *
 */

public class SQLInterface {
	public static String dataFolderURL;
	public static void load(String url) {
		dataFolderURL=url;
		try {
			if(!new File(dataFolderURL, "Vi6Database.db").exists()) {
				Class.forName("org.sqlite.JDBC");
				Connection c = DriverManager.getConnection("jdbc:sqlite:"+dataFolderURL+"/Vi6Database.db");
				//création toutes les tables
				Statement sttmt = c.createStatement();
				String sql;
				sql = "CREATE TABLE Partie("
						+ "Id_Partie INT AUTO_INCREMENT,"
						+ "Date_Partie DATE NOT NULL,"
						+ "Duree TIME,"
						+ "Argent INT NOT NULL,"
						+ "IsRanked BOOLEAN NOT NULL,"
						+ "Nom_Carte VARCHAR(50) NOT NULL,"
						+ "PRIMARY KEY(Id_Partie));";
				sttmt.executeUpdate(sql);
				
				sql = "CREATE TABLE PartieJoueur("
						+ "Id_PartieJoueur INT AUTO_INCREMENT,"
						+ "Id_PartieTueur INT,"
						+ "Nom_Salle_Mort VARCHAR(50),"
						+ "Id_Partie INT NOT NULL,"
						+ "UUID_Joueur CHAR(36) NOT NULL,"
						+ "Nom_Equipe VARCHAR(50) NOT NULL,"
						+ "Nom_Entree VARCHAR(50),"
						+ "Nom_Sortie VARCHAR(50),"
						+ "PRIMARY KEY(Id_PartieJoueur),"
						+ "FOREIGN KEY(Id_PartieTueur) REFERENCES PartieJoueur(Id_PartieJoueur));";
				sttmt.executeUpdate(sql);
				
				sql = "CREATE TABLE Vole("
						+ "Nom_Artefact VARCHAR(50),"
						+ "Id_PartieJoueur INT,"
						+ "Temps TIME NOT NULL,"
						+ "PRIMARY KEY(Nom_Artefact, Id_PartieJoueur, Temps),"
						+ "FOREIGN KEY(Id_PartieJoueur) REFERENCES PartieJoueur(Id_PartieJoueur));";
				sttmt.executeUpdate(sql);
				
				sql = "CREATE TABLE Utilise("
						+ "Nom_Objet VARCHAR(50),"
						+ "Id_PartieJoueur INT,"
						+ "Temps TIME,"
						+ "PRIMARY KEY(Nom_Objet, Id_PartieJoueur, Temps),"
						+ "FOREIGN KEY(Id_PartieJoueur) REFERENCES PartieJoueur(Id_PartieJoueur));";
				sttmt.executeUpdate(sql);
				sttmt.close();
				c.commit();
				c.close();
			}
		} catch (SQLException e) {
			System.out.println("Vi6Message: <ERROR WHILE TRYING TO LOAD SQL>");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}	
	}
	public static int addPartie(Date date, Time duree, int argent, boolean isRanked, String nomCarte) {
		try {
			Class.forName("org.sqlite.JDBC");
			Connection c = DriverManager.getConnection("jdbc:sqlite:"+dataFolderURL+"/Vi6Database.db");
			String sql = "INSERT INTO Partie (Id_Partie,Date_Partie,Duree,Argent,IsRanked,Nom_Carte) "+
							"OUTPUT Id_Partie "+
							"VALUES ("+date+","+duree+","+argent+","+isRanked+","+nomCarte+");";
			Statement sttmt = c.createStatement();
			ResultSet rs = sttmt.executeQuery(sql);
			sttmt.close();
			c.commit();
			c.close();
			return rs.getInt("Id_Partie");
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
			return -1;
		}
	}
	public static int addPartieJoueur(int idPartie, UUID playerUUID, Team team, String entree, String sortie, String salleMort, int idPartieTueur) {
		try {
			Class.forName("org.sqlite.JDBC");
			Connection c = DriverManager.getConnection("jdbc:sqlite:"+dataFolderURL+"/Vi6Database.db");
			String sql = "INSERT INTO PartieJoueur (Id_PartieJoueur,Id_PartieTueur,Nom_Salle_Mort,Id_Partie,UUID_Joueur,Nom_Equipe,Nom_Entree,Nom_Sortie)"+
							"OUTPUT Id_Partie "+
							"VALUES ("+idPartieTueur+","+salleMort+","+idPartie+","+playerUUID.toString()+","+team.toString()+","+entree+","+sortie+");";
			Statement sttmt = c.createStatement();
			ResultSet rs = sttmt.executeQuery(sql);
			sttmt.close();
			c.commit();
			c.close();
			return rs.getInt("Id_PartieJoueur");
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
			return -1;
		}
	}
	public static void addStealEntry(String artefact, int idPartieJoueur, Time temps) {
		try {
			Class.forName("org.sqlite.JDBC");
			Connection c = DriverManager.getConnection("jdbc:sqlite:"+dataFolderURL+"/Vi6Database.db");
			String sql = "INSERT INTO Vole (Nom_Artefact,Id_PartieJoueur,Temps) "+ 
							"VALUES ("+artefact+","+idPartieJoueur+","+temps+");";
			Statement sttmt = c.createStatement();
			sttmt.executeUpdate(sql);
			sttmt.close();
			c.commit();
			c.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void addUtiliseEntry(int idPartieJoueur, String objetName, Time temps) {
		try {
			Class.forName("org.sqlite.JDBC");
			Connection c = DriverManager.getConnection("jdbc:sqlite:"+dataFolderURL+"/Vi6Database.db");
			String sql = "INSERT INTO Utilise (Id_PartieJoueur,Nom_Objet,Temps) "+ 
							"VALUES ("+idPartieJoueur+","+objetName+","+temps+");";
			Statement sttmt = c.createStatement();
			sttmt.executeUpdate(sql);
			sttmt.close();
			c.commit();
			c.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void updatePartie(int idPartie, Time duree) {
		try {
			Class.forName("org.sqlite.JDBC");
			Connection c = DriverManager.getConnection("jdbc:sqlite:"+dataFolderURL+"/Vi6Database.db");
			String sql = "UPDATE Partie SET Duree = "+duree+
							" WHERE Id_Partie="+idPartie+";";
			Statement sttmt = c.createStatement();
			sttmt.executeUpdate(sql);
			sttmt.close();
			c.commit();
			c.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/*public void updatePartieJoueur(int idPartieJoueur, String entree, String sortie, String salleMort, int idPartieTueur) {
		try {
			String sql = "UPDATE PartieJoueur SET Nom_Entree = "+entree+
							" , Nom_Sortie = "+sortie+
							" , Nom_Salle = "+salleMort+
							" , Id_PartieTueur = "+idPartieTueur+
							" WHERE Id_PartieJoueur="+idPartieJoueur+";";
			Statement sttmt = c.createStatement();
			sttmt.executeUpdate(sql);
			sttmt.close();
			c.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}*/
}
