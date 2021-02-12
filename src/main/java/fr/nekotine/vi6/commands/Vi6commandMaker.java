package fr.nekotine.vi6.commands;

import org.bukkit.entity.Player;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.CommandExecutor;
import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.map.Carte;
import fr.nekotine.vi6.yml.DisplayTexts;

public class Vi6commandMaker {
	
	private static Argument gameArgument(String nodeName) {
		return new CustomArgument<Game>(nodeName, (input) -> {
			return Vi6Main.getGame(input);
		}).overrideSuggestions(sender -> {
			return Vi6Main.getGameList().stream().map(Game::getName).toArray(String[]::new);
			});
		
	}
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
		return new CommandAPICommand("game")
				.withSubcommand(makeHelp(gameHelp))
				.withSubcommand(gameCreate(main))
				.withSubcommand(gameJoin())
				.withSubcommand(gameLeave())
				.executes(gameHelp);
	}
	
	private static CommandAPICommand gameCreate(Vi6Main main) {
		return new CommandAPICommand("create")
				.withArguments(new StringArgument("gameName"))
				.executes((sender,args)->{
					main.createGame((String)args[0]);
				});
	}
	
	private static CommandAPICommand gameJoin() {
		return new CommandAPICommand("join")
				.withArguments(gameArgument("liste"))
				.executes((sender,args)->{
					((Game)args[0]).addPlayer((Player)sender);
				});
	}
	
	private static CommandAPICommand gameLeave() {
		return new CommandAPICommand("leave")
				.withArguments(gameArgument("liste"))
				.executes((sender,args)->{
					((Game)args[0]).removePlayer((Player)sender);
				});
	}
	
	//----------------------MAP-------------------------\/
	
	private static CommandAPICommand map(Vi6Main main) {
		return new CommandAPICommand("map")
				.withSubcommand(makeHelp(mapHelp))
				.withSubcommand(mapList())
				.withSubcommand(mapCreate())
				.executes(mapHelp);
	}
	
	private static CommandAPICommand mapList() {
		return new CommandAPICommand("list")
				.executes((sender,args)->{
					sender.sendMessage(DisplayTexts.getMessage("map_list"));
					sender.sendMessage(Carte.getMapList().toArray(String[]::new));
				});
	}
	
	public static CommandAPICommand mapCreate() {
		return new CommandAPICommand("create")
				.withPermission("vi6.map.create")
				.withArguments(new StringArgument("mapName"))
				.executes((sender,args)->{
					String name = (String) args[0];
					if (Carte.getMapList().contains(name)) {
						sender.sendMessage(DisplayTexts.getMessage("map_create_exist"));
					}else {
						Carte map = new Carte(name);
						Carte.save(map);
						map.unload();
						sender.sendMessage(DisplayTexts.getMessage("map_create_success"));
					}
				});
	}
}
