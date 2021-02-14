package fr.nekotine.vi6.map;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;

@SerializableAs("Carte")
public class Carte implements ConfigurationSerializable {

	private static File mapFolder;
	
	private Game game;
	private final String name;
	private final ArrayList<Entree> entrees = new ArrayList<>();
	private final ArrayList<Sortie> sorties = new ArrayList<>();
	private final ArrayList<Passage> passages = new ArrayList<>();
	private final ArrayList<Gateway> gateways = new ArrayList<>();
	private final ArrayList<Artefact> artefacts = new ArrayList<>();

	public Carte(String name) {
		this.name=name;
	}
	
	public void enable(Vi6Main main) {
		for (Entree e : entrees) {e.enable(main);}
		for (Sortie s : sorties) {s.enable(main);}
		for (Passage p : passages) {p.enable(main);}
		for (Artefact a : artefacts) {a.enable(main);}
	}
	
	public void start() {
		for (Artefact a : artefacts) {a.reset();}
		for (Gateway g : gateways) {g.open();}
	}
	
	public void unload() {
		for (Entree e : entrees) {e.destroy();}
		for (Sortie s : sorties) {s.destroy();}
		for (Passage p : passages) {p.destroy();}
		for (Artefact a : artefacts) {a.destroy();}
	}
	
	public ArrayList<Entree> getEntreeList() {
		return entrees;
	}

	public ArrayList<Sortie> getSortieList() {
		return sorties;
	}

	public ArrayList<Passage> getPassageList() {
		return passages;
	}

	public ArrayList<Gateway> getGatewayList() {
		return gateways;
	}

	public ArrayList<Artefact> getArtefactList() {
		return artefacts;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	//STATIC------------------
	@Override
	public Map<String, Object> serialize() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("nbEntrees", entrees.size());
		map.put("nbSorties", sorties.size());
		map.put("nbPassages", passages.size());
		map.put("nbArtefacts", artefacts.size());
		for (int i=0;i<entrees.size();i++) {
			map.put(Entree.getYamlPrefix()+i, entrees.get(i));
		}
		for (int i=0;i<sorties.size();i++) {
			map.put(Sortie.getYamlPrefix()+i, sorties.get(i));
		}
		for (int i=0;i<passages.size();i++) {
			map.put(Passage.getYamlPrefix()+i, passages.get(i));
		}
		for (int i=0;i<artefacts.size();i++) {
			map.put(Artefact.getYamlPrefix()+i, artefacts.get(i));
		}
		return map;
	}
	
	public static Carte deserialize(Map<String, Object> args) {
		Carte map = new Carte((String)args.get("name"));
		int nb = 0;
		//ADDING ENTREES
		nb=(int)args.get("nbEntrees");
		ArrayList<Entree> entresref = map.getEntreeList();
		for (int i=0;i<nb;i++) {
			Entree e = (Entree) args.get(Entree.getYamlPrefix()+i);
			if (e!=null) entresref.add(e);
		}
		//ADDING SORTIES
		nb=(int)args.get("nbSorties");
		ArrayList<Sortie> sortiesref = map.getSortieList();
		for (int i=0;i<nb;i++) {
			Sortie e = (Sortie) args.get(Sortie.getYamlPrefix()+i);
			if (e!=null) sortiesref.add(e);
		}
		//ADDING PASSAGES/GATEWAYS
		nb=(int)args.get("nbPassages");
		ArrayList<Passage> passagesref = map.getPassageList();
		ArrayList<Gateway> gatewaysref = map.getGatewayList();
		for (int i=0;i<nb;i++) {
			Passage e = (Passage) args.get(Passage.getYamlPrefix()+i);
			if (e!=null) {
				passagesref.add(e);
				if (e instanceof Gateway) {
					gatewaysref.add((Gateway) e);
				}
			};
		}
		//ADDING ARTEFACTS
		nb=(int)args.get("nbArtefacts");
		ArrayList<Artefact> artefactsref = map.getArtefactList();
		for (int i=0;i<nb;i++) {
			Artefact e = (Artefact) args.get(Artefact.getYamlPrefix()+i);
			if (e!=null) artefactsref.add(e);
		}
		return map;
	}

	public static void setMapFolder(File f) {
		mapFolder=f;
	}
	
	public static Carte load(String mapName) {
		if (mapFolder==null || !mapFolder.exists()) return null;
		File f = new File(mapFolder,mapName+".yml");
		if (f.exists()) {
			YamlConfiguration config = new YamlConfiguration();
			try {
				config.load(f);
			} catch (IOException | InvalidConfigurationException e) {
				e.printStackTrace();
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED+"Impossible de charger la carte "+ChatColor.AQUA+mapName);
				return null;
			}
			return (Carte)config.get("map");
		}
		return null;
	}
	
	public static void save(Carte map) {
		if (mapFolder==null || !mapFolder.exists()) return;
		String name = map.getName();
		File f = new File(mapFolder,name+".yml");
		try {
			f.createNewFile();
			YamlConfiguration config = YamlConfiguration.loadConfiguration(f);
			config.set("map", map);
			config.save(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static ArrayList<String> getMapList() {
		ArrayList<String> finalList=new ArrayList<String>();
		if (mapFolder==null || !mapFolder.exists()) return finalList;
		String[] list = mapFolder.list();
		for (String s : list) {
			if (s.contains(".yml")) {
				finalList.add(s.replace(".yml", ""));
			}
		}
		return finalList;
	}

	public String getName() {
		return name;
	}

	public static boolean remove(String mapName) {
		File f = new File(mapFolder,mapName+".yml");
		if (f.exists()) {
			return f.delete();
		}
		return false;
	}
	
	public static boolean remove(Carte map) {
		File f = new File(mapFolder,map.getName()+".yml");
		if (f.exists()) {
			return f.delete();
		}
		map.unload();
		return false;
	}
	
}
