package com.nktfh100.amongus.events;

import com.nktfh100.AmongUs.main.Main;

import mineverse.Aust1n46.chat.api.events.VentureChatEvent;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class VentureChatEvent_ implements Listener {
	@EventHandler
	public void ventureChat(VentureChatEvent ev) {
		if (Main.getPlayersManager().getPlayerInfo(ev.getMineverseChatPlayer().getPlayer()).getIsIngame()) {
			ev.getRecipients().clear();
		}
	}
}
