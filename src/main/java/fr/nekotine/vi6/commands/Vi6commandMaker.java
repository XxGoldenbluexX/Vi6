package fr.nekotine.vi6.commands;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.CustomArgument.CustomArgumentException;
import dev.jorel.commandapi.arguments.CustomArgument.MessageBuilder;
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
		Argument gameArgument = new CustomArgument<Game>("game",(input)-> {
			Game g = Vi6Main.getGame(input);
			if (g==null) {
				throw new CustomArgumentException(new MessageBuilder("No game with this name: ").appendArgInput().appendHere());
			}else {
				return g;
			}
		}).overrideSuggestions((sender) -> {return Vi6Main.getGameList().stream().map(Game::getName).toArray(String[]::new);});
		Argument mapArgument = new CustomArgument<Carte>("game",(input)-> {
			Carte map = Carte.load(input);
			if (map==null) {
				throw new CustomArgumentException(new MessageBuilder("No map with this name: ").appendArgInput().appendHere());
			}else {
				return map;
			}
		}).overrideSuggestions(sender -> {return Carte.getMapList().toArray(String[]::new);});
		return new CommandAPICommand("vi6")
				.withPermission("vi6.main")
				.withSubcommand(makeHelp(mainHelp))
				.withSubcommand(game(main,gameArgument))
				.withSubcommand(map(main,mapArgument))
				.executes(mainHelp);
	}

	//----------------------HELP-------------------------\/
	
	private static CommandAPICommand makeHelp(CommandExecutor helpLambda) {
		return new CommandAPICommand("help")
				.executes(helpLambda);
	}
	
	//----------------------GAME-------------------------\/
	
	private static CommandAPICommand game(Vi6Main main, Argument gameArgument) {
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
	
	private static CommandAPICommand map(Vi6Main main, Argument mapArgument) {
		return new CommandAPICommand("map")
				.withSubcommand(makeHelp(mapHelp))
				.withSubcommand(mapList())
				.withSubcommand(mapCreate())
				.withSubcommand(mapRemove(mapArgument))
				.withSubcommand(mapGuardSpawn(mapArgument))
				.withSubcommand(mapMinimapSpawn(mapArgument))
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
	
	public static CommandAPICommand artefact(Argument mapArgument) {
		return new CommandAPICommand("artefact")
				.withPermission("vi6.map.edit")
				.withSubcommand(makeHelp(mapHelp));
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
}
