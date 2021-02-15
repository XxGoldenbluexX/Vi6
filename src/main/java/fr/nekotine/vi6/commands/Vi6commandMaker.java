package fr.nekotine.vi6.commands;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.CustomArgument.CustomArgumentException;
import dev.jorel.commandapi.arguments.CustomArgument.MessageBuilder;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.arguments.LocationArgument;
import dev.jorel.commandapi.arguments.LocationType;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.CommandExecutor;
import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.map.Artefact;
import fr.nekotine.vi6.map.Carte;
import fr.nekotine.vi6.utils.DetectionZone;
import fr.nekotine.vi6.utils.MessageFormater;
import fr.nekotine.vi6.yml.DisplayTexts;

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
				.executes(mainHelp);
	}

	//----------------------HELP-------------------------\/
	
	private static CommandAPICommand makeHelp(CommandExecutor helpLambda) {
		return new CommandAPICommand("help")
				.executes(helpLambda);
	}
	
	//----------------------GAME-------------------------\/
	
	private static CommandAPICommand game(Vi6Main main) {
		Argument gameArgument = new CustomArgument<Game>("gameList",(input)-> {
			Game g = Vi6Main.getGame(input);
			if (g==null) {
				throw new CustomArgumentException(new MessageBuilder("No game with this name: ").appendArgInput().appendHere());
			}else {
				return g;
			}
		}).overrideSuggestions((sender) -> {return Vi6Main.getGameList().stream().map(Game::getName).toArray(String[]::new);});
		return new CommandAPICommand("game")
				.withSubcommand(makeHelp(gameHelp))
				.withSubcommand(gameCreate(main))
				.withSubcommand(gameJoin(gameArgument))
				.withSubcommand(gameLeave(gameArgument))
				.withSubcommand(gameRemove(main,gameArgument))
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
				.executes((sender,args)->{
					((Game)args[0]).addPlayer((Player)sender);
				});
	}
	
	private static CommandAPICommand gameLeave(Argument gameArgument) {
		return new CommandAPICommand("leave")
				.withArguments(gameArgument)
				.executes((sender,args)->{
					((Game)args[0]).removePlayer((Player)sender);
				});
	}
	
	//----------------------MAP-------------------------\/
	
	private static CommandAPICommand map(Vi6Main main) {
		Argument mapArgument = new CustomArgument<Carte>("carteList",(input)-> {
			Carte map = Carte.load(input);
			if (map==null) {
				throw new CustomArgumentException(new MessageBuilder("No map with this name: ").appendArgInput().appendHere());
			}else {
				return map;
			}
		}).overrideSuggestions(sender -> {return Carte.getMapList().toArray(String[]::new);});
		return new CommandAPICommand("map")
				.withSubcommand(makeHelp(mapHelp))
				.withSubcommand(mapList())
				.withSubcommand(mapCreate())
				.withSubcommand(mapRemove(mapArgument))
				.withSubcommand(mapGuardSpawn(mapArgument))
				.withSubcommand(mapMinimapSpawn(mapArgument))
				.withSubcommand(artefact(mapArgument))
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
	
	//----ARTEFACT-----\/
	
	public static CommandAPICommand artefact(Argument mapArgument) {
		Argument artefactList = new StringArgument("artefactList").overrideSuggestions((sender, args) -> {
			return ((Carte)args[0]).getArtefactList().stream().map(Artefact::getName).toArray(String[]::new);
		});
		return new CommandAPICommand("artefact")
				.withPermission("vi6.map.edit")
				.withSubcommand(artefactAdd(mapArgument))
				.withSubcommand(artefactRename(mapArgument,artefactList))
				.withSubcommand(artefactDisplayRename(mapArgument,artefactList))
				.withSubcommand(artefactSetZone(mapArgument,artefactList))
				.withSubcommand(artefactRemove(mapArgument,artefactList));
	}
	
	public static CommandAPICommand artefactAdd(Argument mapArgument) {
		return new CommandAPICommand("add")
				.withArguments(mapArgument,new StringArgument("name"))
				.executes((player,args)->{
					Carte map = (Carte)args[0];
					String name = (String)args[1];
					if (map.getArtefact(name)!=null){
						player.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_artefact_add_exist"),new MessageFormater("§v", map.getName()),new MessageFormater("§p", name)));
					}else {
						map.getArtefactList().add(new Artefact(name, name, new DetectionZone(0,0,0,0,0,0)));
						Carte.save(map);
						map.unload();
						player.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_artefact_add_success"),new MessageFormater("§v", map.getName()),new MessageFormater("§p", name)));
					}
				});
	}
	
	public static CommandAPICommand artefactRemove(Argument mapArgument, Argument artefactList) {
		return new CommandAPICommand("remove")
				.withArguments(mapArgument,artefactList)
				.executes((player,args)->{
					Carte map = (Carte)args[0];
					Artefact a = map.getArtefact((String)args[1]);
					if (a!=null){
						map.getArtefactList().remove(a);
						Carte.save(map);
						map.unload();
						player.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_artefact_remove_success"),new MessageFormater("§v", map.getName()),new MessageFormater("§p", a.getName())));
					}else {
						player.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("map_artefact_remove_exist"),new MessageFormater("§v", map.getName()),new MessageFormater("§p", (String)args[1])));
					}
				});
	}
	
	public static CommandAPICommand artefactRename(Argument mapArgument, Argument artefactList) {
		return new CommandAPICommand("rename")
				.withArguments(mapArgument,artefactList, new StringArgument("newName"))
				.executes((player,args)->{
					Carte map = (Carte)args[0];
					Artefact a = map.getArtefact((String)args[1]);
					if (a!=null){
						a.setName((String)args[2]);
						Carte.save(map);
						map.unload();
						// message artefact rename
					}else {
						// message artefact n'existe pas
					}
				});
	}
	
	public static CommandAPICommand artefactDisplayRename(Argument mapArgument, Argument artefactList) {
		return new CommandAPICommand("displayRename")
				.withArguments(mapArgument,artefactList, new GreedyStringArgument("newDisplayName"))
				.executes((player,args)->{
					Carte map = (Carte)args[0];
					Artefact a = map.getArtefact((String)args[1]);
					if (a!=null){
						a.setDisplayName((String)args[2]);
						Carte.save(map);
						map.unload();
						// message artefact diplayReName
					}else {
						// message artefact n'existe pas
					}
				});
	}
	
	public static CommandAPICommand artefactSetZone(Argument mapArgument, Argument artefactList) {
		return new CommandAPICommand("setZone")
				.withArguments(mapArgument,artefactList,new LocationArgument("zone1Location", LocationType.PRECISE_POSITION),new LocationArgument("zone2Location", LocationType.BLOCK_POSITION))
				.executes((player,args)->{
					Carte map = (Carte)args[0];
					Artefact a = map.getArtefact((String)args[1]);
					if (a!=null){
						Location loc1 = (Location)args[2];
						Location loc2 = (Location)args[3];
						a.setZone(new DetectionZone(loc1.getBlockX(), loc1.getBlockY(), loc1.getBlockZ(), loc2.getBlockX(), loc2.getBlockY(), loc2.getBlockZ()));
						Carte.save(map);
						map.unload();
						//message artefact zone set
					}else {
						// message artefact n'existe pas
					}
				});
	}
}
