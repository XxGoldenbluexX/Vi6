package fr.nekotine.vi6.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandExecutor;

public class Vi6commandMaker {
	
	private static CommandExecutor mainhelp = (sender,args)->{
		sender.sendMessage("");
	};

	public static CommandAPICommand makevi6() {
		return new CommandAPICommand("vi6").withPermission("vi6.main").withSubcommand(makehelp()).executes(mainhelp);
		
	}
	
	private static CommandAPICommand makehelp() {
		return new CommandAPICommand("help").withPermission("vi6.help").executes(mainhelp);
	}
	
}
