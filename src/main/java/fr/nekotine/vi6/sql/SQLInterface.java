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
	public SQLInterface(Vi6Main main) {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
			return;
		}
		try {
			if(!new File(main.getDataFolder(), "Vi6Database.db").exists()) {
				c = DriverManager.getConnection("jdbc:sqlite:"+main.getDataFolder().getAbsolutePath()+"/Vi6Database.db");
				//création toutes les tables
				Statement sttmt = c.createStatement();
				String sql;
				sql = "CREATE TABLE Artefact("
						+ "Nom_Artefact VARCHAR(50),"
						+ "PRIMARY KEY(Nom_Artefact));";
				sttmt.executeUpdate(sql);
				
				sql = "CREATE TABLE Carte("
						+ "Nom_Carte VARCHAR(50),"
						+ "PRIMARY KEY(Nom_Carte));";
				sttmt.executeUpdate(sql);
				
				sql = "CREATE TABLE Joueur("
						+ "UUID_Joueur CHAR(36),"
						+ "PRIMARY KEY(UUID_Joueur));";
				sttmt.executeUpdate(sql);
				
				sql = "CREATE TABLE Salle("
						+ "Nom_Salle VARCHAR(50),"
						+ "PRIMARY KEY(Nom_Salle));";
				sttmt.executeUpdate(sql);
				
				sql = "CREATE TABLE Entree("
						+ "Nom_Entree VARCHAR(50),"
						+ "PRIMARY KEY(Nom_Entree));";
				sttmt.executeUpdate(sql);
				
				sql = "CREATE TABLE Objet("
						+ "Nom_Objet VARCHAR(50),"
						+ "PRIMARY KEY(Nom_Objet));";
				sttmt.executeUpdate(sql);
				
				sql = "CREATE TABLE Sortie("
						+ "Nom_Sortie VARCHAR(50),"
						+ "PRIMARY KEY(Nom_Sortie));";
				sttmt.executeUpdate(sql);
				
				sql = "CREATE TABLE Partie("
						+ "Id_Partie INT AUTO_INCREMENT,"
						+ "Date_Partie DATE NOT NULL,"
						+ "Duree TIME,"
						+ "Argent INT NOT NULL,"
						+ "Type VARCHAR(50) NOT NULL,"
						+ "Nom_Carte VARCHAR(50) NOT NULL,"
						+ "PRIMARY KEY(Id_Partie),"
						+ "FOREIGN KEY(Nom_Carte) REFERENCES Carte(Nom_Carte));";
				sttmt.executeUpdate(sql);
				
				sql = "CREATE TABLE PartieJoueur("
						+ "Id_PartieJoueur INT AUTO_INCREMENT,"
						+ "Id_PartieTueur INT,"
						+ "Nom_Salle VARCHAR(50),"
						+ "Id_Partie INT NOT NULL,"
						+ "UUID_Joueur CHAR(36) NOT NULL,"
						+ "Nom_Equipe VARCHAR(50) NOT NULL,"
						+ "Nom_Entree VARCHAR(50),"
						+ "Nom_Sortie VARCHAR(50),"
						+ "PRIMARY KEY(Id_PartieJoueur),"
						+ "FOREIGN KEY(Id_PartieTueur) REFERENCES PartieJoueur(Id_PartieJoueur),"
						+ "FOREIGN KEY(Nom_Salle) REFERENCES Salle(Nom_Salle),"
						+ "FOREIGN KEY(Id_Partie) REFERENCES Partie(Id_Partie),"
						+ "FOREIGN KEY(UUID_Joueur) REFERENCES Joueur(UUID_Joueur),"
						+ "FOREIGN KEY(Nom_Entree) REFERENCES Entree(Nom_Entree),"
						+ "FOREIGN KEY(Nom_Sortie) REFERENCES Sortie(Nom_Sortie));";
				sttmt.executeUpdate(sql);
				
				sql = "CREATE TABLE Vole("
						+ "Nom_Artefact VARCHAR(50),"
						+ "Id_PartieJoueur INT,"
						+ "Temps TIME NOT NULL,"
						+ "PRIMARY KEY(Nom_Artefact, Id_PartieJoueur),"
						+ "FOREIGN KEY(Nom_Artefact) REFERENCES Artefact(Nom_Artefact),"
						+ "FOREIGN KEY(Id_PartieJoueur) REFERENCES PartieJoueur(Id_PartieJoueur));";
				sttmt.executeUpdate(sql);
				
				sql = "CREATE TABLE Utilise("
						+ "Nom_Objet VARCHAR(50),"
						+ "Id_PartieJoueur INT,"
						+ "Temps TIME NOT NULL,"
						+ "PRIMARY KEY(Nom_Objet, Id_PartieJoueur),"
						+ "FOREIGN KEY(Nom_Objet) REFERENCES Objet(Nom_Objet),"
						+ "FOREIGN KEY(Id_PartieJoueur) REFERENCES PartieJoueur(Id_PartieJoueur));";
				sttmt.executeUpdate(sql);
				c.commit();
			}else {
				c = DriverManager.getConnection("jdbc:sqlite:"+main.getDataFolder().getAbsolutePath()+"/Vi6Database.db");
			}
			c.setAutoCommit(false);
		} catch (SQLException e) {
			System.out.println("Vi6Message: <ERROR WHILE TRYING TO LOAD SQL>");
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
		//tester si la map existe et la créer sinon
		try {
			Statement sttmt = c.createStatement();
			ResultSet rs = sttmt.executeQuery( "SELECT NVL(MAX(Id_Partie),-1) FROM Partie;" );
			int idPartie = rs.getInt("Id_Partie")+1;
			String sql = "INSERT INTO Partie (Id_Partie,Date_Partie,Duree,Argent,Type,Nom_Carte) "+ 
							"VALUES ("+idPartie+","+date+","+duree+","+argent+","+type.toString()+","+nomCarte+");";
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
		//tester si player,entree,sortie,salleMort existent et creer sinon
		try {
			Statement sttmt = c.createStatement();
			ResultSet rs = sttmt.executeQuery( "SELECT NVL(MAX(Id_PartieJoueur),-1) FROM PartieJoueur;" );
			int idPartieJoueur = rs.getInt("Id_PartieJoueur")+1;
			String sql = "INSERT INTO PartieJoueur (Id_PartieJoueur,Id_PartieTueur,Nom_Salle,Id_Partie,UUID_Joueur,Nom_Equipe,Nom_Entree,Nom_Sortie)"+ 
							"VALUES ("+idPartieJoueur+","+idPartieTueur+","+salleMort+","+idPartie+","+playerUUID.toString()+","+team.toString()+","+entree+","+sortie+");";
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
		//tester si artefact éxiste et creer sinon
		try {
			Statement sttmt = c.createStatement();
			String sql = "INSERT INTO Vole (Nom_Artefact,Id_PartieJoueur,Temps) "+ 
							"VALUES ("+artefact.getName()+","+idPartieJoueur+","+temps+");";
			sttmt.executeUpdate(sql);
			sttmt.close();
			c.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void addUtiliseEntry(int idPartieJoueur, String objetName, Time temps) {
		//tester si objet éxiste et creer sinon
		try {
			Statement sttmt = c.createStatement();
			String sql = "INSERT INTO Utilise (Id_PartieJoueur,Nom_Objet,Temps) "+ 
							"VALUES ("+idPartieJoueur+","+objetName+","+temps+");";
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
							" WHERE Id_Partie="+idPartie+";";
			sttmt.executeUpdate(sql);
			sttmt.close();
			c.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void updatePartieJoueur(int idPartieJoueur, String entree, String sortie, String salleMort, int idPartieTueur) {
		//tester si entree,sortie,salleMort existent et creer sinon
		Statement sttmt;
		try {
			sttmt = c.createStatement();
			String sql = "UPDATE PartieJoueur SET Nom_Entree = "+entree+
							" , Nom_Sortie = "+sortie+
							" , Nom_Salle = "+salleMort+
							" , Id_PartieTueur = "+idPartieTueur+
							" WHERE Id_PartieJoueur="+idPartieJoueur+";";
			sttmt.executeUpdate(sql);
			sttmt.close();
			c.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
