package com.nktfh100.amongus.events;

import java.util.Set;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerChat implements Listener {
	@EventHandler(priority = EventPriority.HIGHEST)
	public void playerChat(AsyncPlayerChatEvent ev) {
		Player player = ev.getPlayer();
		if (ev.getMessage().isEmpty()) {
			return;
		}

		if (ev.isCancelled() && !Main.getIsVentureChat().booleanValue()) {
			return;
		}

		PlayerInfo pInfo = Main.getPlayersManager().getPlayerInfo(player);
		if (pInfo == null) {
			pInfo = Main.getPlayersManager()._addPlayer(player);
		}
		if (!pInfo.getIsIngame().booleanValue()) {
			Set<Player> sentTo = ev.getRecipients();
			for (Arena arena : Main.getArenaManager().getAllArenas()) {
				for (Player player_ : arena.getPlayers()) {
					sentTo.remove(player_);
				}
			}
			return;
		}
		Arena arena = pInfo.getArena();

		if (!arena.getIsInMeeting().booleanValue() && !pInfo.isGhost().booleanValue() && arena.getGameState() == GameState.RUNNING) {
			ev.setCancelled(true);
			String msg = Main.getMessagesManager().getGameMsg("cantTalk", arena, null);
			if (!msg.isEmpty()) {
				player.sendMessage(msg);
			}
			return;
		}
		ev.getRecipients().clear();
		String key = "chat";
		if (pInfo.isGhost().booleanValue() && arena.getGameState() != GameState.FINISHING) {
			key = "ghostsChat";
		}

		String msg = Main.getMessagesManager().getGameMsg(key, arena, "%1\\$s", pInfo.getColor().getChatColor() + "", pInfo.getColor().getName(), "%2\\$s");
		ev.setFormat(msg);
		if (arena.getGameState() == GameState.FINISHING) {
			for (PlayerInfo pInfo1 : arena.getPlayersInfo()) {
				ev.getRecipients().add(pInfo1.getPlayer());
			}
		} else {
			for (PlayerInfo pInfo1 : arena.getPlayersInfo()) {
				if (pInfo1 == null) {
					continue;
				}
				if (pInfo.isGhost().booleanValue() && pInfo1.isGhost().booleanValue()) {

					ev.getRecipients().add(pInfo1.getPlayer());
					continue;
				}
				if (!pInfo.isGhost().booleanValue()) {
					ev.getRecipients().add(pInfo1.getPlayer());
				}
			}
		}
		ev.setCancelled(false);
	}
}
