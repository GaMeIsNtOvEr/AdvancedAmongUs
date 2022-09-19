package com.nktfh100.amongus.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.google.common.collect.Lists;
import com.nktfh100.AmongUs.utils.ScoreboardSign.VirtualTeam;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ScoreboardSign {
	private static final ProtocolManager pm = ProtocolLibrary.getProtocolManager();

	private final Player player;

	private String objectiveName;
	private boolean created;
	private final VirtualTeam[] lines = new VirtualTeam[15];

	private HashMap<String, ArrayList<String>> teamsPlayers;

	private void sendPacket(PacketContainer pc) {
		try {
			pm.sendServerPacket(this.player, pc);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public void create() {
		if (this.created)
			return;
		if (this.objectiveName.isEmpty()) {
			return;
		}

		sendPacket(createObjectivePacket(0, this.objectiveName));
		sendPacket(setObjectiveSlot());

		for (int i = 0; i < this.lines.length; i++) {
			if (this.lines[i] != null) {
				sendLine(i);
			}
		}
		this.created = true;
	}

	public void destroy() {
		if (!this.created) {
			return;
		}
		sendPacket(createObjectivePacket(1, "_"));
		byte b;
		int i;
		VirtualTeam[] arrayOfVirtualTeam;
		for (i = (arrayOfVirtualTeam = this.lines).length, b = 0; b < i;) {
			VirtualTeam team = arrayOfVirtualTeam[b];
			if (team != null)
				sendPacket(team.removeTeam());
			b++;
		}

		for (String key : this.teamsPlayers.keySet()) {
			removePlayersTeam(key);
		}
		this.teamsPlayers.clear();

		this.created = false;
	}

	public void setObjectiveName(String name) {
		this.objectiveName = name;
		if (this.created) {
			sendPacket(createObjectivePacket(2, name));
		}
	}

	public void setLine(int line, String value) {
		VirtualTeam team = getOrCreateTeam(line);
		String old = team.getCurrentPlayer();
		if (value == null) {
			value = Utils.getRandomColors();
		}
		if (value.equals(old)) {
			return;
		}
		if (old != null && this.created) {
			sendPacket(removeLine(old));
		}
		team.setValue(value);
		sendLine(line);
	}

	public void setLines(ArrayList<String> list) {
		int i = 0;
		for (String s : list) {
			if (s == null) {
				s = Utils.getRandomColors();
			}
			setLine(i, s);
			i++;
		}
		try {
			for (int i1 = i; i1 < 15; i1++) {
				if (this.lines[i1] != null) {
					removeLine(i1);
				}
			}

		} catch (Exception exception) {
		}
	}

	public void removeLine(int line) {
		VirtualTeam team = getOrCreateTeam(line);
		String old = team.getCurrentPlayer();

		if (old != null && this.created) {
			sendPacket(removeLine(old));
			sendPacket(team.removeTeam());
		}

		this.lines[line] = null;
	}

	public String getLine(int line) {
		return (line < 0 || line > 14) ? null : getOrCreateTeam(line).getValue();
	}

	public VirtualTeam getTeam(int line) {
		return (line < 0 || line > 14) ? null : getOrCreateTeam(line);
	}

	private void sendLine(int line) {
		if (line < 0 || line > 14 || !this.created) {
			return;
		}
		VirtualTeam team = getOrCreateTeam(line);
		for (PacketContainer pc : team.sendLine()) {
			sendPacket(pc);
		}
		sendPacket(sendScore(team.getCurrentPlayer(), line));
		team.reset();
	}

	private VirtualTeam getOrCreateTeam(int line) {
		if (lines[line] == null)
			lines[line] = new VirtualTeam("__fakeScore" + line, "", "");
		return lines[line];
	}

	private PacketContainer createObjectivePacket(int mode, String displayName) {
		PacketContainer pc = pm.createPacket(PacketType.Play.Server.SCOREBOARD_OBJECTIVE, true);
		if (displayName == null) {
			displayName = "null";
		}
		pc.getIntegers().write(0, Integer.valueOf(mode));
		pc.getStrings().write(0, this.player.getName());

		if (mode == 0 || mode == 2) {
			pc.getChatComponents().write(0, WrappedChatComponent.fromText(displayName));
		}
		return pc;
	}

	private PacketContainer setObjectiveSlot() {
		PacketContainer pc = pm.createPacket(PacketType.Play.Server.SCOREBOARD_DISPLAY_OBJECTIVE);

		pc.getIntegers().write(0, Integer.valueOf(1));
		pc.getStrings().write(0, this.player.getName());

		return pc;
	}

	private PacketContainer sendScore(String line, int score) {
		PacketContainer pc = pm.createPacket(PacketType.Play.Server.SCOREBOARD_SCORE);

		pc.getIntegers().write(0, Integer.valueOf(score));
		pc.getScoreboardActions().write(0, EnumWrappers.ScoreboardAction.CHANGE);
		pc.getStrings().write(0, line).write(1, this.player.getName());

		return pc;
	}

	private PacketContainer removeLine(String line) {
		PacketContainer pc = pm.createPacket(PacketType.Play.Server.SCOREBOARD_SCORE);

		pc.getScoreboardActions().write(0, EnumWrappers.ScoreboardAction.REMOVE);
		pc.getStrings().write(0, line);

		return pc;
	}

	public ScoreboardSign(Player player, String objectiveName) {
		this.teamsPlayers = new HashMap<>();
		this.player = player;
		this.objectiveName = objectiveName;
	}

	public void addTeam(String name, ChatColor color, String nameTag) {
		if (this.teamsPlayers.containsKey(name)) {
			return;
		}
		WrapperPlayServerScoreboardTeam pw = new WrapperPlayServerScoreboardTeam();
		pw.setMode(0);
		pw.setName(name);
		pw.setColor(color);
		pw.setDisplayName(WrappedChatComponent.fromText(String.valueOf(name) + color));
		pw.setCollisionRule("never");
		pw.setNameTagVisibility(nameTag);
		pw.sendPacket(this.player);
		this.teamsPlayers.put(name, new ArrayList<>());
	}

	public void updateTeamNameTag(String name, ChatColor color, String to) {
		WrapperPlayServerScoreboardTeam pw = new WrapperPlayServerScoreboardTeam();
		pw.setMode(2);
		pw.setName(name);
		pw.setColor(color);
		pw.setNameTagVisibility(to);
		pw.setDisplayName(WrappedChatComponent.fromText(String.valueOf(name) + color));
		pw.setCollisionRule("never");
		pw.sendPacket(this.player);
	}

	public void addPlayerToTeam(String name, Player player_) {
		WrapperPlayServerScoreboardTeam pw = new WrapperPlayServerScoreboardTeam();
		pw.setMode(3);
		pw.setName(name);
		((ArrayList<String>) this.teamsPlayers.get(name)).add(player_.getName());
		pw.setPlayers(this.teamsPlayers.get(name));
		pw.sendPacket(this.player);
	}

	public void removePlayerFromTeam(String name, Player player_) {
		WrapperPlayServerScoreboardTeam pw = new WrapperPlayServerScoreboardTeam();
		pw.setMode(4);
		pw.setName(name);
		pw.setDisplayName(WrappedChatComponent.fromText(name));
		((ArrayList) this.teamsPlayers.get(name)).remove(player_.getName());

		pw.setPlayers(this.teamsPlayers.get(name));
		pw.sendPacket(this.player);
	}

	public void removePlayersTeam(String name) {
		PacketContainer pc = pm.createPacket(PacketType.Play.Server.SCOREBOARD_TEAM);

		pc.getIntegers().write(0, Integer.valueOf(1));
		pc.getStrings().write(0, name);
		sendPacket(pc);
	}

	public static class VirtualTeam {
		private final String name;

		private String currentPlayer;

		private String oldPlayer;

		private String prefix;

		private String suffix;
		private boolean playerChanged;
		private boolean prefixChanged;
		private boolean suffixChanged;
		private boolean first = true;

		private VirtualTeam(String name, String prefix, String suffix) {
			this.name = name;
			this.prefix = prefix;
			this.suffix = suffix;
		}

		public void reset() {
			this.prefixChanged = false;
			this.suffixChanged = false;
			this.playerChanged = false;
			this.oldPlayer = null;
		}

		public String getName() {
			return this.name;
		}

		public String getPrefix() {
			return this.prefix;
		}

		public void setPrefix(String prefix) {
			if (this.prefix == null || !this.prefix.equals(prefix))
				this.prefixChanged = true;
			this.prefix = prefix;
		}

		public String getSuffix() {
			return this.suffix;
		}

		public void setSuffix(String suffix) {
			if (this.suffix == null || !this.suffix.equals(this.prefix))
				this.suffixChanged = true;
			this.suffix = suffix;
		}

		private static final WrappedChatComponent emptyWrappedChatComponent = WrappedChatComponent.fromText("");

		private PacketContainer createPacket(int mode) {
			PacketContainer pc = pm.createPacket(PacketType.Play.Server.SCOREBOARD_TEAM, true);

			pc.getStrings().write(0, this.name).write(1, "always");
			pc.getChatComponents().write(0, emptyWrappedChatComponent).write(1, WrappedChatComponent.fromText(this.prefix)).write(2, WrappedChatComponent.fromText(this.suffix));
			pc.getIntegers().write(0, Integer.valueOf(mode));

			return pc;
		}

		public Iterable<PacketContainer> sendLine() {
			List<PacketContainer> packets = new ArrayList<>();

			if (this.first) {
				this.first = false;
				packets.add(createTeam());
			} else if (this.prefixChanged || this.suffixChanged) {
				packets.add(updateTeam());
			}

			if (this.first || this.playerChanged) {
				if (this.oldPlayer != null)
					packets.add(addOrRemovePlayer(4, this.oldPlayer));
				packets.add(changePlayer());
			}

			return packets;
		}

		public PacketContainer createTeam() {
			return createPacket(0);
		}

		public PacketContainer removeTeam() {
			PacketContainer pc = pm.createPacket(PacketType.Play.Server.SCOREBOARD_TEAM);

			pc.getIntegers().write(0, Integer.valueOf(1));
			pc.getStrings().write(0, this.name);

			this.first = true;

			return pc;
		}

		public PacketContainer updateTeam() {
			return createPacket(2);
		}

		public PacketContainer addOrRemovePlayer(int mode, String playerName) {
			PacketContainer pc = pm.createPacket(PacketType.Play.Server.SCOREBOARD_TEAM);

			pc.getIntegers().write(0, Integer.valueOf(mode));
			pc.getSpecificModifier(Collection.class).write(0, Lists.newArrayList((Object[]) new String[] { playerName }));
			pc.getStrings().write(0, this.name);

			return pc;
		}

		public PacketContainer changePlayer() {
			return addOrRemovePlayer(3, this.currentPlayer);
		}

		public String getCurrentPlayer() {
			return this.currentPlayer;
		}

		public void setPlayer(String name) {
			if (this.currentPlayer == null || !this.currentPlayer.equals(name))
				this.playerChanged = true;
			this.oldPlayer = this.currentPlayer;
			this.currentPlayer = name;
		}

		public String getValue() {
			return String.valueOf(getPrefix()) + getCurrentPlayer() + getSuffix();
		}

		public void setValue(String value) {
			if (value.length() <= 64) {
				setPrefix(value);
				setPlayer(Utils.getRandomColors());
				setSuffix("");
			} else {
				Bukkit.getLogger().log(Level.SEVERE, "Scoreboard line: \"" + value + "\" is longer than 64 characters!");
				setPrefix(value.substring(0, 64));
				setPlayer(Utils.getRandomColors());
				setSuffix("");
			}
		}
	}
}
