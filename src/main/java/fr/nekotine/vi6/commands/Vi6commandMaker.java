package fr.nekotine.vi6.commands;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ChatColorArgument;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.CustomArgument.CustomArgumentException;
import dev.jorel.commandapi.arguments.CustomArgument.MessageBuilder;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import dev.jorel.commandapi.arguments.EntitySelectorArgument.EntitySelector;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.ItemStackArgument;
import dev.jorel.commandapi.arguments.LocationArgument;
import dev.jorel.commandapi.arguments.LocationType;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.CommandExecutor;
import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.map.Artefact;
import fr.nekotine.vi6.map.Camera;
import fr.nekotine.vi6.map.Carte;
import fr.nekotine.vi6.map.Entree;
import fr.nekotine.vi6.map.Gateway;
import fr.nekotine.vi6.map.Passage;
import fr.nekotine.vi6.map.Sortie;
import fr.nekotine.vi6.map.SpawnVoleur;
import fr.nekotine.vi6.utils.DetectionZone;
import fr.nekotine.vi6.utils.MessageFormater;
import fr.nekotine.vi6.wrappers.PlayerWrapper;
import fr.nekotine.vi6.yml.DisplayTexts;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;

public class Vi6commandMaker {
		
	private static CommandExecutor mainHelp = (sender,args)->{
	};

	private static CommandExecutor gameHelp = (sender,args)->{
	};
	
	private static CommandExecutor mapHelp = (sender,args)->{
	};
	
	//----------------------MAIN-------------------------\/
	
	public static CommandAPICommand makevi6(Vi6Main main) {
		return new CommandAPICommand("vi6")
				.withPermission("vi6.main")
				.withSubcommand(makeHelp(mainHelp))
				.withSubcommand(game(main))
				.withSubcommand(map(main))
				.withSubcommand(makeTest(main))
				.executes(mainHelp);
	}
	
	//----------------------TEST-------------------------\/
	//TODO Prone for removal
	private static CommandAPICommand makeTest(Vi6Main main) {
		return new CommandAPICommand("test")
				.executes((sender,args)->{
					if (sender instanceof Player) {
						Player p = (Player) sender;
						p.playSound(Sound.sound(Key.key(Key.MINECRAFT_NAMESPACE, "entity.cow.step"), Sound.Source.MASTER, 1f, 1f), Sound.Emitter.self());
					}
				});
	}

	//----------------------HELP-------------------------\/
	
	private static CommandAPICommand makeHelp(CommandExecutor helpLambda) {
		return new CommandAPICommand("help")
				.executes(helpLambda);
	}
	
	//----------------------GAME-------------------------\/
	
	private static CommandAPICommand game(Vi6Main main) {
		Argument gameArgument = new CustomArgument<Game>("gameList",(info)-> {
			Game g = Vi6Main.getGame(info.input());
			if (g==null) {
				throw new CustomArgumentException(new MessageBuilder("No game with this name: ").appendArgInput().appendHere());
			}else {
				return g;
			}
		}).replaceSuggestions((info) -> {return Vi6Main.getGameList().stream().map(Game::getName).toArray(String[]::new);});
		return new CommandAPICommand("game")
				.withSubcommand(makeHelp(gameHelp))
				.withSubcommand(gameCreate(main))
				.withSubcommand(gameJoin(gameArgument))
				.withSubcommand(gameLeave(main))
				.withSubcommand(gameRemove(main,gameArgument))
				.withSubcommand(gameJoinPlayer(main,gameArgument))
				.withSubcommand(gameStop(gameArgument))
				.withSubcommand(gameLeavePlayer(gameArgument))
				.executes(gameHelp);
	}
	
	private static CommandAPICommand gameCreate(Vi6Main main) {
		return new CommandAPICommand("create")
				.withArguments(new StringArgument("gameName"))
				.executes((sender,args)->{
					main.createGame((String)args[0]);
				});
	}
	
	private static CommandAPICommand gameRemove(Vi6Main main,Argument gameArgument) {
		return new CommandAPICommand("remove")
				.withArguments(gameArgument)
				.executes((sender,args)->{
					main.removeGame((Game)args[0]);
				});
	}
	
	private static CommandAPICommand gameJoin(Argument gameArgument) {
		return new CommandAPICommand("join")
				.withArguments(gameArgument)
				.executesPlayer((sender,args)->{
					((Game)args[0]).addPlayer(sender);
				});
	}
	
	private static CommandAPICommand gameStop(Argument gameArgument) {
		return new CommandAPICommand("stop")
				.withArguments(gameArgument)
				.executes((sender,args)->{
					((Game)args[0]).endGame(true);
				});
	}
	
	private static CommandAPICommand gameJoinPlayer(Vi6Main mainref,Argument gameArgument) {
		return new CommandAPICommand("join")
				.withArguments(gameArgument,new EntitySelectorArgument("players", EntitySelector.MANY_PLAYERS).replaceSuggestions((info)->{return Bukkit.getServer().getOnlinePlayers().stream().filter(e->mainref.getPlayerWrapper(e)==null).map((p)->{return p.getName();}).toArray(String[]::new);}))
				.executes((sender,args)->{
					@SuppressWarnings("unchecked")
					Collection<Player> players = (Collection<Player>) args[1];
					for(Player player : players) {
						((Game)args[0]).addPlayer(player);
					}
				});
	}
	
	private static CommandAPICommand gameLeave(Vi6Main main) {
		return new CommandAPICommand("leave")
				.executesPlayer((sender,args)->{
					PlayerWrapper wrap = main.getPlayerWrapper(sender);
					if(wrap!=null) wrap.getGame().removePlayer(sender);
				});
	}
	
	private static CommandAPICommand gameLeavePlayer(Argument gameArgument) {
		return new CommandAPICommand("leave")
				.withArguments(gameArgument,
				new EntitySelectorArgument("players", EntitySelector.MANY_PLAYERS).replaceSuggestions((info)->{return ((Game)info.previousArgs()[0]).getPlayerMap().keySet().stream().map((Player p)->{return p.getName();}).toArray(String[]::new);}))
				.executesPlayer((sender,args)->{
					@SuppressWarnings("unchecked")
					Collection<Player> players = (Collection<Player>) args[1];
					for(Player player : players) {
						((Game)args[0]).addPlayer(player);
					}
				});
	}
	
	//----------------------MAP-------------------------\/
	
	private static CommandAPICommand map(Vi6Main main) {
		Argument mapArgument = new CustomArgument<Carte>("carteList",(info)-> {
			Carte map = Carte.load(info.input());
			if (map==null) {
				throw new CustomArgumentException(new MessageBuilder("No map with this name: ").appendArgInput().appendHere());
			}else {
				return map;
			}
		}).replaceSuggestions(info -> {return Carte.getMapList().toArray(String[]::new);});
		return new CommandAPICommand("map")
				.withSubcommand(makeHelp(mapHelp))
				.withSubcommand(mapList())
				.withSubcommand(mapCreate())
				.withSubcommand(mapRemove(mapArgument))
				.withSubcommand(mapGuardSpawn(mapArgument))
				.withSubcommand(mapMinimapSpawn(mapArgument))
				.withSubcommand(artefact(mapArgument))
				.withSubcommand(entree(mapArgument))
				.withSubcommand(sortie(mapArgument))
				.withSubcommand(passage(mapArgument))
				.withSubcommand(mapAddThiefSpawn(mapArgument))
				.withSubcommand(mapRemoveThiefSpawn(mapArgument))
				.withSubcommand(camera(mapArgument))
				.executes(mapHelp);
	}
	
	private static CommandAPICommand mapList() {
		return new CommandAPICommand("list")
				.executes((sender,args)->{
					sender.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_list")));
					sender.sendMessage(Carte.getMapList().toArray(String[]::new));
				});
	}
	
	public static CommandAPICommand mapCreate() {
		return new CommandAPICommand("create")
				.withPermission("vi6.map.create")
				.withArguments(new StringArgument("mapName"))
				.executesPlayer((player,args)->{
					String name = (String) args[0];
					if (Carte.getMapList().contains(name)) {
						player.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_create_exist"),new MessageFormater("§v", name)));
					}else {
						Carte map = new Carte(name,new Location(player.getWorld(),0,0,0),new Location(player.getWorld(),0,0,0));
						Carte.save(map);
						map.unload();
						player.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_create_success"),new MessageFormater("§v", name)));
					}
				});
	}
	
	public static CommandAPICommand mapRemove(Argument mapArgument) {
		return new CommandAPICommand("remove")
				.withPermission("vi6.map.remove")
				.withArguments(mapArgument)
				.executes((sender,args)->{
					Carte map = (Carte)args[0];
					if (Carte.remove(map)) {
						sender.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_remove_success"),new MessageFormater("§v", map.getName())));
					}else {
						sender.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_remove_absent"),new MessageFormater("§v", map.getName())));
					}
				});
	}
	
	//-------MAP_EDITION--------\/
	
	public static CommandAPICommand mapGuardSpawn(Argument mapArgument) {
		return new CommandAPICommand("guardSpawn")
				.withPermission("vi6.map.edit")
				.withArguments(mapArgument)
				.executesPlayer((player,args)->{
					Carte map = (Carte)args[0];
					map.setGuardSpawn(player.getLocation());
					Carte.save(map);
					map.unload();
					player.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_guardSpawn_success"),new MessageFormater("§v", map.getName())));
				});
	}
	
	public static CommandAPICommand mapMinimapSpawn(Argument mapArgument) {
		return new CommandAPICommand("minimapSpawn")
				.withPermission("vi6.map.edit")
				.withArguments(mapArgument)
				.executesPlayer((player,args)->{
					Carte map = (Carte)args[0];
					map.setMinimapSpawn(player.getLocation());
					Carte.save(map);
					map.unload();
					player.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_minimapSpawn_success"),new MessageFormater("§v", map.getName())));
				});
	}
	
	//----THIEFSPAWN-----\/
	
	public static CommandAPICommand mapAddThiefSpawn(Argument mapArgument) {
		return new CommandAPICommand("addThiefSpawn")
				.withPermission("vi6.map.edit")
				.withArguments(mapArgument,new StringArgument("name"),new StringArgument("name"),new LocationArgument("minimapLoc", LocationType.BLOCK_POSITION),new LocationArgument("spawnLoc", LocationType.BLOCK_POSITION))
				.executesPlayer((player,args)->{
					Carte map = (Carte)args[0];
					String name = (String)args[1];
					if (map.getThiefSpawn(name)!=null) {
						player.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_addThiefSpawn_exist"),new MessageFormater("§v", map.getName()),new MessageFormater("§p", name)));
					}else {
						String dname = (String)args[2];
						Location minmap = (Location)args[3];
						Location spwn = (Location)args[4];
						map.getThiefSpawnsList().add(new SpawnVoleur(name,dname,spwn.add(0.5, 0, 0.5),minmap.add(0.5, 0, 0.5)));
						Carte.save(map);
						player.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_addThiefSpawn_success"),new MessageFormater("§v", map.getName()),new MessageFormater("§p", name)));
					}
					map.unload();
				});
	}
	
	public static CommandAPICommand mapRemoveThiefSpawn(Argument mapArgument) {
		return new CommandAPICommand("removeThiefSpawn")
				.withPermission("vi6.map.edit")
				.withArguments(mapArgument,new StringArgument("name").replaceSuggestions((info) -> {
					return ((Carte)info.previousArgs()[0]).getThiefSpawnsList().stream().map(SpawnVoleur::getName).toArray(String[]::new);
				}))
				.executesPlayer((player,args)->{
					Carte map = (Carte)args[0];
					SpawnVoleur ts = map.getThiefSpawn((String)args[1]);
					if (ts==null) {
						player.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_removeThiefSpawn_exist"),new MessageFormater("§v", map.getName()),new MessageFormater("§p", (String)args[1])));
					}else {
						map.getThiefSpawnsList().remove(ts);
						Carte.save(map);
						player.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_removeThiefSpawn_success"),new MessageFormater("§v", map.getName()),new MessageFormater("§p", ts.getName())));
					}
					map.unload();
				});
	}
	
	//----ARTEFACT-----\/
	
	public static CommandAPICommand artefact(Argument mapArgument) {
		Argument artefactList = new StringArgument("artefactList").replaceSuggestions((info) -> {
			return ((Carte)info.previousArgs()[0]).getArtefactList().stream().map(Artefact::getName).toArray(String[]::new);
		});
		return new CommandAPICommand("artefact")
				.withPermission("vi6.map.edit")
				.withSubcommand(artefactAdd(mapArgument))
				.withSubcommand(artefactRename(mapArgument,artefactList))
				.withSubcommand(artefactDisplayRename(mapArgument,artefactList))
				.withSubcommand(artefactSetZone(mapArgument,artefactList))
				.withSubcommand(artefactRemove(mapArgument,artefactList))
				.withSubcommand(artefactSetBlock(mapArgument,artefactList));
	}
	
	public static CommandAPICommand artefactAdd(Argument mapArgument) {
		return new CommandAPICommand("add")
				.withArguments(mapArgument,new StringArgument("name"))
				.executesPlayer((player,args)->{
					Carte map = (Carte)args[0];
					String name = (String)args[1];
					if (map.getArtefact(name)!=null){
						player.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_artefact_add_exist"),new MessageFormater("§v", map.getName()),new MessageFormater("§p", name)));
					}else {
						map.getArtefactList().add(new Artefact(name, name, new DetectionZone(0,0,0,0,0,0),Bukkit.createBlockData(Material.AIR),new Location(player.getWorld(),0,0,0)));
						Carte.save(map);
						player.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_artefact_add_success"),new MessageFormater("§v", map.getName()),new MessageFormater("§p", name)));
					}
					map.unload();
				});
	}
	
	public static CommandAPICommand artefactRemove(Argument mapArgument, Argument artefactList) {
		return new CommandAPICommand("remove")
				.withArguments(mapArgument,artefactList)
				.executes((sender,args)->{
					Carte map = (Carte)args[0];
					Artefact a = map.getArtefact((String)args[1]);
					if (a!=null){
						map.getArtefactList().remove(a);
						Carte.save(map);
						sender.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_artefact_remove_success"),new MessageFormater("§v", map.getName()),new MessageFormater("§p", a.getName())));
					}else {
						sender.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_artefact_remove_exist"),new MessageFormater("§v", map.getName()),new MessageFormater("§p", (String)args[1])));
					}
					map.unload();
				});
	}
	
	public static CommandAPICommand artefactRename(Argument mapArgument, Argument artefactList) {
		return new CommandAPICommand("rename")
				.withArguments(mapArgument,artefactList, new StringArgument("newName"))
				.executes((sender,args)->{
					Carte map = (Carte)args[0];
					Artefact a = map.getArtefact((String)args[1]);
					if (a!=null){
						a.setName((String)args[2]);
						Carte.save(map);
						sender.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_artefact_rename_success"),new MessageFormater("§v", map.getName()),new MessageFormater("§p", a.getName())));
					}else {
						sender.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_artefact_rename_exist"),new MessageFormater("§v", map.getName()),new MessageFormater("§p", (String)args[1])));
					}
					map.unload();
				});
	}
	
	public static CommandAPICommand artefactDisplayRename(Argument mapArgument, Argument artefactList) {
		return new CommandAPICommand("displayRename")
				.withArguments(mapArgument,artefactList, new GreedyStringArgument("newDisplayName"))
				.executes((sender,args)->{
					Carte map = (Carte)args[0];
					Artefact a = map.getArtefact((String)args[1]);
					if (a!=null){
						a.setDisplayName((String)args[2]);
						Carte.save(map);
						sender.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_artefact_displayname_success"),new MessageFormater("§v", map.getName()),new MessageFormater("§p", a.getName())));
					}else {
						sender.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_artefact_displayname_exist"),new MessageFormater("§v", map.getName()),new MessageFormater("§p", (String)args[1])));
					}
					map.unload();
				});
	}
	
	public static CommandAPICommand artefactSetZone(Argument mapArgument, Argument artefactList) {
		return new CommandAPICommand("setZone")
				.withArguments(mapArgument,artefactList,new LocationArgument("zone1Location",LocationType.BLOCK_POSITION),new LocationArgument("zone2Location",LocationType.BLOCK_POSITION))
				.executes((sender,args)->{
					Carte map = (Carte)args[0];
					Artefact a = map.getArtefact((String)args[1]);
					if (a!=null){
						Location loc1 = (Location)args[2];
						Location loc2 = (Location)args[3];
						a.setZone(new DetectionZone(loc1.getX(), loc1.getY(), loc1.getZ(), loc2.getX(), loc2.getY(), loc2.getZ()));
						Carte.save(map);
						sender.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_artefact_zone_success"),new MessageFormater("§v", map.getName()),new MessageFormater("§p", a.getName())));
					}else {
						sender.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_artefact_zone_exist"),new MessageFormater("§v", map.getName()),new MessageFormater("§p", (String)args[1])));
					}
					map.unload();
				});
	}
	
	public static CommandAPICommand artefactSetBlock(Argument mapArgument, Argument artefactList) {
		return new CommandAPICommand("setBlock")
				.withArguments(mapArgument,artefactList,new LocationArgument("zone1Location", LocationType.BLOCK_POSITION))
				.executes((sender,args)->{
					Carte map = (Carte)args[0];
					Artefact a = map.getArtefact((String)args[1]);
					if (a!=null){
						Location loc = (Location)args[2];
						a.setBlockLoc(loc);
						a.setBlockData(loc.getBlock().getBlockData());
						Carte.save(map);
						sender.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_artefact_block_success"),new MessageFormater("§v", map.getName()),new MessageFormater("§p", a.getName())));
					}else {
						sender.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_artefact_block_exist"),new MessageFormater("§v", map.getName()),new MessageFormater("§p", (String)args[1])));
					}
					map.unload();
				});
	}
	
	//----ENTREE-----\/
	
	public static CommandAPICommand entree(Argument mapArgument) {
		Argument entranceList = new StringArgument("entranceList").replaceSuggestions((info) -> {
			return ((Carte)info.previousArgs()[0]).getEntreeList().stream().map(Entree::getName).toArray(String[]::new);
		});
		return new CommandAPICommand("entrance")
				.withPermission("vi6.map.edit")
				.withSubcommand(entranceAdd(mapArgument))
				.withSubcommand(entranceRename(mapArgument, entranceList))
				.withSubcommand(entranceDisplayRename(mapArgument, entranceList))
				.withSubcommand(entranceSetZone(mapArgument, entranceList))
				.withSubcommand(entranceRemove(mapArgument, entranceList));
	}
	
	public static CommandAPICommand entranceAdd(Argument mapArgument) {
		return new CommandAPICommand("add")
				.withArguments(mapArgument,new StringArgument("name"))
				.executesPlayer((player,args)->{
					Carte map = (Carte)args[0];
					String name = (String)args[1];
					if (map.getEntrance(name)!=null){
						player.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_entrance_add_exist"),new MessageFormater("§v", map.getName()),new MessageFormater("§p", name)));
					}else {
						map.getEntreeList().add(new Entree(name, name, new DetectionZone(0,0,0,0,0,0)));
						Carte.save(map);
						player.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_entrance_add_success"),new MessageFormater("§v", map.getName()),new MessageFormater("§p", name)));
					}
					map.unload();
				});
	}
	
	public static CommandAPICommand entranceRemove(Argument mapArgument, Argument entranceList) {
		return new CommandAPICommand("remove")
				.withArguments(mapArgument,entranceList)
				.executes((sender,args)->{
					Carte map = (Carte)args[0];
					Entree e = map.getEntrance((String)args[1]);
					if (e!=null){
						map.getEntreeList().remove(e);
						Carte.save(map);
						sender.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_entrance_remove_success"),new MessageFormater("§v", map.getName()),new MessageFormater("§p", e.getName())));
					}else {
						sender.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_entrance_remove_exist"),new MessageFormater("§v", map.getName()),new MessageFormater("§p", (String)args[1])));
					}
					map.unload();
				});
	}
	
	public static CommandAPICommand entranceRename(Argument mapArgument, Argument entranceList) {
		return new CommandAPICommand("rename")
				.withArguments(mapArgument,entranceList, new StringArgument("newName"))
				.executes((sender,args)->{
					Carte map = (Carte)args[0];
					Entree e = map.getEntrance((String)args[1]);
					if (e!=null){
						e.setName((String)args[2]);
						Carte.save(map);
						sender.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_entrance_rename_success"),new MessageFormater("§v", map.getName()),new MessageFormater("§p", e.getName())));
					}else {
						sender.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_entrance_rename_exist"),new MessageFormater("§v", map.getName()),new MessageFormater("§p", (String)args[1])));
					}
					map.unload();
				});
	}
	
	public static CommandAPICommand entranceDisplayRename(Argument mapArgument, Argument entranceList) {
		return new CommandAPICommand("displayname")
				.withArguments(mapArgument,entranceList, new GreedyStringArgument("newDisplayName"))
				.executes((sender,args)->{
					Carte map = (Carte)args[0];
					Entree e = map.getEntrance((String)args[1]);
					if (e!=null){
						e.setDisplayName((String)args[2]);
						Carte.save(map);
						sender.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_entrance_displayname_success"),new MessageFormater("§v", map.getName()),new MessageFormater("§p", e.getName())));
					}else {
						sender.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_entrance_displayname_exist"),new MessageFormater("§v", map.getName()),new MessageFormater("§p", (String)args[1])));
					}
					map.unload();
				});
	}
	
	public static CommandAPICommand entranceSetZone(Argument mapArgument, Argument entranceList) {
		return new CommandAPICommand("setZone")
				.withArguments(mapArgument,entranceList, new LocationArgument("zone1Location",LocationType.BLOCK_POSITION), new LocationArgument("zone2Location",LocationType.BLOCK_POSITION))
				.executes((sender,args)->{
					Carte map = (Carte)args[0];
					Entree e = map.getEntrance((String)args[1]);
					if (e!=null){
						Location loc1 = (Location)args[2];
						Location loc2 = (Location)args[3];
						e.setZone(new DetectionZone(loc1.getX(), loc1.getY(), loc1.getZ(), loc2.getX(), loc2.getY(), loc2.getZ()));
						Carte.save(map);
						sender.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_entrance_setzone_success"),new MessageFormater("§v", map.getName()),new MessageFormater("§p", e.getName())));
					}else {
						sender.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_entrance_setzone_exist"),new MessageFormater("§v", map.getName()),new MessageFormater("§p", (String)args[1])));
					}
					map.unload();
				});
	}
	
	//----SORTIE-----\/
	
		public static CommandAPICommand sortie(Argument mapArgument) {
			Argument exitList = new StringArgument("exitList").replaceSuggestions((info) -> {
				return ((Carte)info.previousArgs()[0]).getSortieList().stream().map(Sortie::getName).toArray(String[]::new);
			});
			return new CommandAPICommand("exit")
					.withPermission("vi6.map.edit")
					.withSubcommand(sortieAdd(mapArgument))
					.withSubcommand(sortieRemove(mapArgument, exitList))
					.withSubcommand(sortieRemove(mapArgument, exitList))
					.withSubcommand(sortieDisplayname(mapArgument, exitList))
					.withSubcommand(sortieSetZone(mapArgument, exitList));
		}
		
		public static CommandAPICommand sortieAdd(Argument mapArgument) {
			return new CommandAPICommand("add")
					.withArguments(mapArgument,new StringArgument("name"))
					.executes((sender,args)->{
						Carte map = (Carte)args[0];
						String name = (String)args[1];
						if (map.getExit(name)!=null){
							sender.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_exit_add_exist"),new MessageFormater("§v", map.getName()),new MessageFormater("§p", name)));
						}else {
							map.getSortieList().add(new Sortie(name,name,new DetectionZone(0,0,0,0,0,0)));
							Carte.save(map);
							sender.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_exit_add_success"),new MessageFormater("§v", map.getName()),new MessageFormater("§p", name)));
						}
						map.unload();
					});
		}
		
		public static CommandAPICommand sortieRemove(Argument mapArgument, Argument exitList) {
			return new CommandAPICommand("remove")
					.withArguments(mapArgument,exitList)
					.executes((sender,args)->{
						Carte map = (Carte)args[0];
						Sortie s = map.getExit((String)args[1]);
						if (s!=null){
							map.getSortieList().remove(s);
							Carte.save(map);
							sender.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_exit_remove_success"),new MessageFormater("§v", map.getName()),new MessageFormater("§p", s.getName())));
						}else {
							sender.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_exit_remove_exist"),new MessageFormater("§v", map.getName()),new MessageFormater("§p", (String)args[1])));
						}
						map.unload();
					});
		}
		
		public static CommandAPICommand sortieRename(Argument mapArgument, Argument exitList) {
			return new CommandAPICommand("rename")
					.withArguments(mapArgument,exitList, new StringArgument("newName"))
					.executes((sender,args)->{
						Carte map = (Carte)args[0];
						Sortie s = map.getExit((String)args[1]);
						if (s!=null){
							s.setName((String)args[2]);
							Carte.save(map);
							sender.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_exit_rename_success"),new MessageFormater("§v", map.getName()),new MessageFormater("§p", s.getName())));
						}else {
							sender.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_exit_rename_exist"),new MessageFormater("§v", map.getName()),new MessageFormater("§p", (String)args[1])));
						}
						map.unload();
					});
		}
		
		public static CommandAPICommand sortieDisplayname(Argument mapArgument, Argument exitList) {
			return new CommandAPICommand("displayname")
					.withArguments(mapArgument,exitList, new GreedyStringArgument("newDisplayName"))
					.executes((sender,args)->{
						Carte map = (Carte)args[0];
						Sortie s = map.getExit((String)args[1]);
						if (s!=null){
							s.setDisplayName((String)args[2]);
							Carte.save(map);
							sender.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_exit_displayname_success"),new MessageFormater("§v", map.getName()),new MessageFormater("§p", s.getName())));
						}else {
							sender.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_exit_displayname_exist"),new MessageFormater("§v", map.getName()),new MessageFormater("§p", (String)args[1])));
						}
						map.unload();
					});
		}
		
		public static CommandAPICommand sortieSetZone(Argument mapArgument, Argument exitList) {
			return new CommandAPICommand("setZone")
					.withArguments(mapArgument,exitList,new LocationArgument("corner1", LocationType.BLOCK_POSITION),new LocationArgument("corner2", LocationType.BLOCK_POSITION))
					.executes((sender,args)->{
						Carte map = (Carte)args[0];
						Sortie s = map.getExit((String)args[1]);
						if (s!=null){
							Location corner1 = (Location)args[2];
							Location corner2 = (Location)args[3];
							s.setZone(new DetectionZone(corner1.getX(),corner1.getY(),corner1.getZ(),corner2.getX(),corner2.getY(),corner2.getZ()));
							Carte.save(map);
							sender.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_exit_setzone_success"),new MessageFormater("§v", map.getName()),new MessageFormater("§p", s.getName())));
						}else {
							sender.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_exit_setzone_exist"),new MessageFormater("§v", map.getName()),new MessageFormater("§p", (String)args[1])));
						}
						map.unload();
					});
		}
	
		//----PASSAGE-----\/
	
		public static CommandAPICommand passage(Argument mapArgument) {
			Argument passageList = new StringArgument("passageList").replaceSuggestions((info) -> {
				return ((Carte)info.previousArgs()[0]).getPassageList().stream().map(Passage::getName).toArray(String[]::new);
			});
			return new CommandAPICommand("passage")
					.withPermission("vi6.map.edit")
					.withSubcommand(passageAdd(mapArgument))
					.withSubcommand(passageRemove(mapArgument,passageList))
					.withSubcommand(passageRename(mapArgument,passageList))
					.withSubcommand(passageSalleA(mapArgument,passageList))
					.withSubcommand(passageSalleB(mapArgument,passageList))
					.withSubcommand(passageSetZoneA(mapArgument,passageList))
					.withSubcommand(passageSetZoneB(mapArgument,passageList))
					.withSubcommand(passageToGateway(mapArgument,passageList))
					.withSubcommand(gatewayToPassage(mapArgument));
		}
		
		public static CommandAPICommand passageAdd(Argument mapArgument) {
			return new CommandAPICommand("add")
					.withArguments(mapArgument,new StringArgument("name"),new StringArgument("roomA"),new StringArgument("roomB"))
					.executes((sender,args)->{
						Carte map = (Carte)args[0];
						String name = (String)args[1];
						String salleA = (String)args[2];
						String salleB = (String)args[3];
						if (map.getPassage(name)!=null){
							sender.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_passage_add_exist"),new MessageFormater("§v", map.getName()),new MessageFormater("§p", name)));
						}else {
							map.getPassageList().add(new Passage(name,salleA,salleB,new DetectionZone(0,0,0,0,0,0),new DetectionZone(0,0,0,0,0,0)));
							Carte.save(map);
							sender.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_passage_add_success"),new MessageFormater("§v", map.getName()),new MessageFormater("§p", name)));
						}
						map.unload();
					});
		}
		
		public static CommandAPICommand passageRemove(Argument mapArgument, Argument exitList) {
			return new CommandAPICommand("remove")
					.withArguments(mapArgument,exitList)
					.executes((sender,args)->{
						Carte map = (Carte)args[0];
						Passage p = map.getPassage((String)args[1]);
						if (p!=null){
							map.getPassageList().remove(p);
							Carte.save(map);
							sender.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_passage_remove_success"),new MessageFormater("§v", map.getName()),new MessageFormater("§p", p.getName())));
						}else {
							sender.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_passage_remove_exist"),new MessageFormater("§v", map.getName()),new MessageFormater("§p", (String)args[1])));
						}
						map.unload();
					});
		}
		
		public static CommandAPICommand passageRename(Argument mapArgument, Argument exitList) {
			return new CommandAPICommand("rename")
					.withArguments(mapArgument,exitList, new StringArgument("newName"))
					.executes((sender,args)->{
						Carte map = (Carte)args[0];
						Passage p = map.getPassage((String)args[1]);
						if (p!=null){
							p.setName((String)args[2]);
							Carte.save(map);
							sender.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_passage_rename_success"),new MessageFormater("§v", map.getName()),new MessageFormater("§p", p.getName())));
						}else {
							sender.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_passage_rename_exist"),new MessageFormater("§v", map.getName()),new MessageFormater("§p", (String)args[1])));
						}
						map.unload();
					});
		}
		
		public static CommandAPICommand passageSalleA(Argument mapArgument, Argument exitList) {
			return new CommandAPICommand("setRoomA")
					.withArguments(mapArgument,exitList, new StringArgument("roomA"))
					.executes((sender,args)->{
						Carte map = (Carte)args[0];
						Passage p = map.getPassage((String)args[1]);
						if (p!=null){
							p.setSalleA((String)args[2]);
							Carte.save(map);
							sender.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_passage_roomA_success"),new MessageFormater("§v", map.getName()),new MessageFormater("§p", p.getName())));
						}else {
							sender.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_passage_roomA_exist"),new MessageFormater("§v", map.getName()),new MessageFormater("§p", (String)args[1])));
						}
						map.unload();
					});
		}
		
		public static CommandAPICommand passageSalleB(Argument mapArgument, Argument exitList) {
			return new CommandAPICommand("setRoomB")
					.withArguments(mapArgument,exitList, new StringArgument("roomB"))
					.executes((sender,args)->{
						Carte map = (Carte)args[0];
						Passage p = map.getPassage((String)args[1]);
						if (p!=null){
							p.setSalleB((String)args[2]);
							Carte.save(map);
							sender.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_passage_roomB_success"),new MessageFormater("§v", map.getName()),new MessageFormater("§p", p.getName())));
						}else {
							sender.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_passage_roomB_exist"),new MessageFormater("§v", map.getName()),new MessageFormater("§p", (String)args[1])));
						}
						map.unload();
					});
		}
		
		public static CommandAPICommand passageSetZoneA(Argument mapArgument, Argument exitList) {
			return new CommandAPICommand("setZoneA")
					.withArguments(mapArgument,exitList,new LocationArgument("corner1", LocationType.BLOCK_POSITION),new LocationArgument("corner2", LocationType.BLOCK_POSITION))
					.executes((sender,args)->{
						Carte map = (Carte)args[0];
						Passage p = map.getPassage((String)args[1]);
						if (p!=null){
							Location corner1 = (Location)args[2];
							Location corner2 = (Location)args[3];
							p.setZoneA(new DetectionZone(corner1.getX(),corner1.getY(),corner1.getZ(),corner2.getX(),corner2.getY(),corner2.getZ()));
							Carte.save(map);
							sender.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_passage_setZoneA_success"),new MessageFormater("§v", map.getName()),new MessageFormater("§p", p.getName())));
						}else {
							sender.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_passage_setZoneA_exist"),new MessageFormater("§v", map.getName()),new MessageFormater("§p", (String)args[1])));
						}
						map.unload();
					});
		}
		
		public static CommandAPICommand passageSetZoneB(Argument mapArgument, Argument exitList) {
			return new CommandAPICommand("setZoneB")
					.withArguments(mapArgument,exitList,new LocationArgument("corner1", LocationType.BLOCK_POSITION),new LocationArgument("corner2", LocationType.BLOCK_POSITION))
					.executes((sender,args)->{
						Carte map = (Carte)args[0];
						Passage p = map.getPassage((String)args[1]);
						if (p!=null){
							Location corner1 = (Location)args[2];
							Location corner2 = (Location)args[3];
							p.setZoneB(new DetectionZone(corner1.getX(),corner1.getY(),corner1.getZ(),corner2.getX(),corner2.getY(),corner2.getZ()));
							Carte.save(map);
							sender.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_passage_setZoneB_success"),new MessageFormater("§v", map.getName()),new MessageFormater("§p", p.getName())));
						}else {
							sender.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_passage_setZoneB_exist"),new MessageFormater("§v", map.getName()),new MessageFormater("§p", (String)args[1])));
						}
						map.unload();
					});
		}
		
		public static CommandAPICommand passageToGateway(Argument mapArgument, Argument exitList) {
			return new CommandAPICommand("passageToGateway")
					.withArguments(mapArgument,exitList,new LocationArgument("corner1", LocationType.BLOCK_POSITION),new LocationArgument("corner2", LocationType.BLOCK_POSITION))
					.executes((sender,args)->{
						Carte map = (Carte)args[0];
						Passage p = map.getPassage((String)args[1]);
						if (p!=null){
							map.getPassageList().remove(p);
							Location corner1 = (Location)args[2];
							Location corner2 = (Location)args[3];
							Gateway g = new Gateway(p.getName(),p.getSalleA(),p.getSalleB(),p.getZoneA(),p.getZoneB(),corner1,corner2);
							map.getPassageList().add(g);
							Carte.save(map);
							sender.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_passage_toGateway_success"),new MessageFormater("§v", map.getName()),new MessageFormater("§p", p.getName())));
						}else {
							sender.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_passage_toGateway_exist"),new MessageFormater("§v", map.getName()),new MessageFormater("§p", (String)args[1])));
						}
						map.unload();
					});
		}
		
		public static CommandAPICommand gatewayToPassage(Argument mapArgument) {
			return new CommandAPICommand("gatewayToPassage")
					.withArguments(mapArgument,new StringArgument("gatewayList").replaceSuggestions((info) -> {
						return ((Carte)info.previousArgs()[0]).getGatewayList().stream().map(Gateway::getName).toArray(String[]::new);
					}))
					.executes((sender,args)->{
						Carte map = (Carte)args[0];
						Gateway g = map.getGateway((String)args[1]);
						if (g!=null){
							map.getPassageList().remove(g);
							Passage p = new Passage(g.getName(),g.getSalleA(),g.getSalleB(),g.getZoneA(),g.getZoneB());
							map.getPassageList().add(g);
							Carte.save(map);
							sender.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_passage_toPassage_success"),new MessageFormater("§v", map.getName()),new MessageFormater("§p", p.getName())));
						}else {
							sender.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_passage_toPassage_exist"),new MessageFormater("§v", map.getName()),new MessageFormater("§p", (String)args[1])));
						}
						map.unload();
					});
		}
		
		//----CAMERAS-----\/
		public static CommandAPICommand camera(Argument mapArgument) {
			Argument camList = new StringArgument("cameraList").replaceSuggestions((info) -> {
				return ((Carte)info.previousArgs()[0]).getCameraList().stream().map(Camera::getName).toArray(String[]::new);
			});
			return new CommandAPICommand("camera")
					.withPermission("vi6.map.edit")
					.withSubcommand(addCamera(mapArgument))
					.withSubcommand(removeCamera(mapArgument, camList))
					.withSubcommand(renameCamera(mapArgument, camList))
					.withSubcommand(displayRenameCamera(mapArgument, camList))
					.withSubcommand(setMaterialCamera(mapArgument, camList))
					.withSubcommand(setPositionCamera(mapArgument, camList))
					.withSubcommand(setLocationCamera(mapArgument, camList));
			
		}
		
		public static CommandAPICommand addCamera(Argument mapArgument) {
			return new CommandAPICommand("add")
					.withArguments(mapArgument,
							new StringArgument("cameraName"), new StringArgument("displayName"), 
							new LocationArgument("location", LocationType.PRECISE_POSITION),
							new IntegerArgument("position"), new ItemStackArgument("item"))
					.executes((sender,args)->{
						Carte map = (Carte)args[0];
						
						String camName = (String)args[1];
						String displayName = (String)args[2];
						Location location = (Location)args[3];
						int position = (int)args[4];
						
						ItemStack item = (ItemStack)args[5];
						System.out.println(item);
						Material mat = item.getType();
						if (map.getCamera(camName)!=null) {
							sender.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_camera_add_exist"),new MessageFormater("§v", map.getName()),new MessageFormater("§p", camName)));	
						}else {
							map.getCameraList().add(new Camera(camName, displayName, location, position, mat));
							
							Carte.save(map);
							
							sender.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_camera_add_success"),new MessageFormater("§v", map.getName()),new MessageFormater("§p", camName)));	
							
							map.unload();
						}	
					});
		}
		
		public static CommandAPICommand removeCamera(Argument mapArgument, Argument camList) {
			return new CommandAPICommand("remove")
					.withArguments(mapArgument, camList)
					.executes((sender,args)->{
						Carte map = (Carte)args[0];
						String camName = (String)args[1];
						Camera cam = map.getCamera(camName);
						if(cam!=null) {
							map.getCameraList().remove(cam);
							Carte.save(map);
							sender.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_camera_remove_success"),new MessageFormater("§v", map.getName()),new MessageFormater("§p", camName)));	
						}else {
							sender.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_camera_not_found"),new MessageFormater("§v", map.getName()),new MessageFormater("§p", camName)));	

						}
						map.unload();
					});
		}
		
		public static CommandAPICommand renameCamera(Argument mapArgument, Argument camList) {
			return new CommandAPICommand("rename")
					.withArguments(mapArgument, camList, new StringArgument("newName"))
					.executes((sender,args)->{
						Carte map = (Carte)args[0];
						String camName = (String)args[1];
						Camera cam = map.getCamera(camName);
						String newName = (String)args[2];
						if(cam!=null) {
							cam.setName(newName);
							Carte.save(map);
							sender.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_camera_rename_success"),new MessageFormater("§v", map.getName()),new MessageFormater("§p", camName)));	

						}else {
							sender.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_camera_not_found"),new MessageFormater("§v", map.getName()),new MessageFormater("§p", camName)));	

						}
						map.unload();
					});
		}
		
		public static CommandAPICommand displayRenameCamera(Argument mapArgument, Argument camList) {
			return new CommandAPICommand("displayRename")
					.withArguments(mapArgument, camList, new StringArgument("newName"))
					.executes((sender,args)->{
						Carte map = (Carte)args[0];
						String camName = (String)args[1];
						Camera cam = map.getCamera(camName);
						String newName = (String)args[2];
						if(cam!=null) {
							cam.setDisplayName(newName);
							Carte.save(map);
							sender.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_camera_rename_success"),new MessageFormater("§v", map.getName()),new MessageFormater("§p", camName)));	

						}else {
							sender.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_camera_not_found"),new MessageFormater("§v", map.getName()),new MessageFormater("§p", camName)));	

						}
						map.unload();
					});
		}
		
		public static CommandAPICommand setMaterialCamera(Argument mapArgument, Argument camList) {
			return new CommandAPICommand("setMaterial")
					.withArguments(mapArgument, camList, new ItemStackArgument("item"))
					.executes((sender,args)->{
						Carte map = (Carte)args[0];
						String camName = (String)args[1];
						Camera cam = map.getCamera(camName);
						ItemStack item = (ItemStack)args[2];
						Material mat = item.getType();
						if(cam!=null) {
							cam.setMaterial(mat);
							Carte.save(map);
							sender.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_camera_material_success"),new MessageFormater("§v", map.getName()),new MessageFormater("§p", camName)));	

						}else {
							sender.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_camera_not_found"),new MessageFormater("§v", map.getName()),new MessageFormater("§p", camName)));	

						}
						map.unload();
					});
		}
		
		public static CommandAPICommand setPositionCamera(Argument mapArgument, Argument camList) {
			return new CommandAPICommand("setPosition")
					.withArguments(mapArgument, camList, new IntegerArgument("position"))
					.executes((sender,args)->{
						Carte map = (Carte)args[0];
						String camName = (String)args[1];
						Camera cam = map.getCamera(camName);
						int position = (int)args[2];
						if(cam!=null) {
							cam.setPosition(position);
							Carte.save(map);
							sender.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_camera_position_success"),new MessageFormater("§v", map.getName()),new MessageFormater("§p", camName)));	

						}else {
							sender.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_camera_not_found"),new MessageFormater("§v", map.getName()),new MessageFormater("§p", camName)));	

						}
						map.unload();
					});
		}
		
		public static CommandAPICommand setLocationCamera(Argument mapArgument, Argument camList) {
			return new CommandAPICommand("setLocation")
					.withArguments(mapArgument, camList, new LocationArgument("location", LocationType.PRECISE_POSITION))
					.executes((sender,args)->{
						Carte map = (Carte)args[0];
						String camName = (String)args[1];
						Camera cam = map.getCamera(camName);
						Location location = (Location)args[2];
						if(cam!=null) {
							cam.setLocation(location);
							Carte.save(map);
							sender.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_camera_location_successs"),new MessageFormater("§v", map.getName()),new MessageFormater("§p", camName)));	

						}else {
							sender.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_camera_not_found"),new MessageFormater("§v", map.getName()),new MessageFormater("§p", camName)));	

						}
						map.unload();
					});
		}
}
