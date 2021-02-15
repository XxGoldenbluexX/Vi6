package fr.nekotine.vi6.enums;

import org.bukkit.ChatColor;

public enum Team {
	
	GARDE(ChatColor.BLUE),
	VOLEUR(ChatColor.RED);
	
	private final ChatColor chatColor;
	
	private Team(ChatColor chatColor) {
		this.chatColor=chatColor;
	}

	public ChatColor getChatColor() {
		return chatColor;
	}
	
}
