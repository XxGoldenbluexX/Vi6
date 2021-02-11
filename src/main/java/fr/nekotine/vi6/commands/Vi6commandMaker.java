package fr.nekotine.vi6.commands;

import org.bukkit.entity.Player;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.CommandExecutor;
import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;

public class Vi6commandMaker {
	
	private static Argument gameArgument(String nodeName) {
		return new CustomArgument<Game>(nodeName, (input) -> {
			return Vi6Main.getGame(input);
		}).overrideSuggestions(sender -> {
			return Vi6Main.getGameList().stream().map(Game::getName).toArray(String[]::new);
			});
		
	}
	private static CommandExecutor mainhelp = (sender,args)->{
	};

	private static CommandExecutor gamehelp = (sender,args)->{
	};
	
	public static CommandAPICommand makevi6(Vi6Main main) {
		return new CommandAPICommand("vi6").withPermission("vi6.main")
				.withSubcommand(makehelp())
				.withSubcommand(game(main))
				.executes(mainhelp);
	}
	
	private static CommandAPICommand makehelp() {
		return new CommandAPICommand("help")
				.withPermission("vi6.help")
				.executes(mainhelp);
	}
	
	private static CommandAPICommand game(Vi6Main main) {
		return new CommandAPICommand("game")
				.withSubcommand(gameCreate(main))
				.withSubcommand(gameJoin(main))
				.executes(gamehelp);
	}
	
	private static CommandAPICommand gameCreate(Vi6Main main) {
		return new CommandAPICommand("create")
				.withArguments(new StringArgument("gameName"))
				.executes((sender,args)->{
					main.createGame((String)args[0]);
				});
	}
	
	private static CommandAPICommand gameJoin(Vi6Main main) {
		return new CommandAPICommand("join")
				.withArguments(gameArgument("liste"))
				.executes((sender,args)->{
					((Game)args[0]).addPlayer((Player)sender);
				});
	}
}
