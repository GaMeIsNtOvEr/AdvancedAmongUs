package com.nktfh100.amongus.managers;

import com.nktfh100.AmongUs.enums.GameState;
import com.nktfh100.AmongUs.enums.SabotageType;
import com.nktfh100.AmongUs.enums.StatInt;
import com.nktfh100.AmongUs.info.Arena;
import com.nktfh100.AmongUs.info.PlayerInfo;
import com.nktfh100.AmongUs.info.Task;
import com.nktfh100.AmongUs.info.TaskPlayer;
import com.nktfh100.AmongUs.main.Main;
import com.nktfh100.AmongUs.utils.Utils;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public class MessagesManager {
	private HashMap<String, String> msgsGame = new HashMap<>();
	private HashMap<String, String> scoreboard = new HashMap<>();
	private HashMap<String, String> tasks = new HashMap<>();
	private HashMap<String, String> sabotagesTitles = new HashMap<>();
	private HashMap<String, ArrayList<String>> scoreboardLines = new HashMap<>();
	private ArrayList<String> signLines = new ArrayList<>();
	private HashMap<String, ArrayList<String>> holograms = new HashMap<>();
	private ArrayList<String> estimatedTimes = new ArrayList<>();
	private HashMap<GameState, String> gameStates = new HashMap<>();

	public void loadAll() {
		File msgsConfigFIle = new File(Main.getPlugin().getDataFolder(), "messages.yml");
		if (!msgsConfigFIle.exists()) {
			try {
				Main.getPlugin().saveResource("messages.yml", false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		YamlConfiguration msgsConfig = YamlConfiguration.loadConfiguration(msgsConfigFIle);
		try {
			this.msgsGame = new HashMap<>();
			this.scoreboard = new HashMap<>();
			this.tasks = new HashMap<>();
			this.scoreboardLines = new HashMap<>();
			this.signLines = new ArrayList<>();
			this.holograms = new HashMap<>();
			this.estimatedTimes = new ArrayList<>();
			this.sabotagesTitles = new HashMap<>();

			ConfigurationSection gameMsgsSC = msgsConfig.getConfigurationSection("game");
			Set<String> gameMsgsKeys = gameMsgsSC.getKeys(false);
			for (String key : gameMsgsKeys) {
				this.msgsGame.put(key, ChatColor.translateAlternateColorCodes('&', gameMsgsSC.getString(key).replaceAll("%prefix%", Main.getConfigManager().getPrefix())));
			}

			ConfigurationSection scoreboardSC = msgsConfig.getConfigurationSection("scoreboard");
			Set<String> scoreboardKeys = scoreboardSC.getKeys(false);
			for (String key : scoreboardKeys) {
				this.scoreboard.put(key, ChatColor.translateAlternateColorCodes('&', scoreboardSC.getString(key).replaceAll("%prefix%", Main.getConfigManager().getPrefix())));
			}

			this.scoreboardLines.put("main-lobby", new ArrayList<>());
			for (String line : scoreboardSC.getStringList("main-lobby-lines")) {
				((ArrayList<String>) this.scoreboardLines.get("main-lobby")).add(ChatColor.translateAlternateColorCodes('&', line.replaceAll("%prefix%", Main.getConfigManager().getPrefix())));
			}
			if (Main.getConfigManager().getScoreboardUsePackets().booleanValue()) {
				Collections.reverse(this.scoreboardLines.get("main-lobby"));
			}

			this.scoreboardLines.put("waiting-lobby", new ArrayList<>());
			for (String line : scoreboardSC.getStringList("waiting-lobby-lines")) {
				((ArrayList<String>) this.scoreboardLines.get("waiting-lobby")).add(ChatColor.translateAlternateColorCodes('&', line.replaceAll("%prefix%", Main.getConfigManager().getPrefix())));
			}
			if (Main.getConfigManager().getScoreboardUsePackets().booleanValue()) {
				Collections.reverse(this.scoreboardLines.get("waiting-lobby"));
			}

			this.scoreboardLines.put("crewmate", new ArrayList<>());
			for (String line : scoreboardSC.getStringList("crewmate-lines")) {
				((ArrayList<String>) this.scoreboardLines.get("crewmate")).add(ChatColor.translateAlternateColorCodes('&', line.replaceAll("%prefix%", Main.getConfigManager().getPrefix())));
			}
			if (Main.getConfigManager().getScoreboardUsePackets().booleanValue()) {
				Collections.reverse(this.scoreboardLines.get("crewmate"));
			}

			this.scoreboardLines.put("imposter", new ArrayList<>());
			for (String line : scoreboardSC.getStringList("imposter-lines")) {
				((ArrayList<String>) this.scoreboardLines.get("imposter")).add(ChatColor.translateAlternateColorCodes('&', line.replaceAll("%prefix%", Main.getConfigManager().getPrefix())));
			}
			if (Main.getConfigManager().getScoreboardUsePackets().booleanValue()) {
				Collections.reverse(this.scoreboardLines.get("imposter"));
			}

			this.scoreboardLines.put("dead-crewmate", new ArrayList<>());
			for (String line : scoreboardSC.getStringList("dead-crewmate-lines")) {
				((ArrayList<String>) this.scoreboardLines.get("dead-crewmate")).add(ChatColor.translateAlternateColorCodes('&', line.replaceAll("%prefix%", Main.getConfigManager().getPrefix())));
			}
			if (Main.getConfigManager().getScoreboardUsePackets().booleanValue()) {
				Collections.reverse(this.scoreboardLines.get("dead-crewmate"));
			}

			this.scoreboardLines.put("dead-imposter", new ArrayList<>());
			for (String line : scoreboardSC.getStringList("dead-imposter-lines")) {
				((ArrayList<String>) this.scoreboardLines.get("dead-imposter")).add(ChatColor.translateAlternateColorCodes('&', line.replaceAll("%prefix%", Main.getConfigManager().getPrefix())));
			}
			if (Main.getConfigManager().getScoreboardUsePackets().booleanValue()) {
				Collections.reverse(this.scoreboardLines.get("dead-imposter"));
			}

			ConfigurationSection tasksSC = msgsConfig.getConfigurationSection("tasks");
			Set<String> tasksKeys = tasksSC.getKeys(false);
			for (String key : tasksKeys) {
				String name = tasksSC.getConfigurationSection(key).getString("name");
				this.tasks.put(key, ChatColor.translateAlternateColorCodes('&', name));
				String title = tasksSC.getConfigurationSection(key).getString("title");
				if (title != null) {
					this.sabotagesTitles.put(key, title);
				}
			}

			for (String line : msgsConfig.getStringList("arenaSignsLines")) {
				this.signLines.add(ChatColor.translateAlternateColorCodes('&', line));
			}

			for (String line : msgsConfig.getStringList("estimatedTimes")) {
				this.estimatedTimes.add(ChatColor.translateAlternateColorCodes('&', line));
			}

			ConfigurationSection hologramsSC = msgsConfig.getConfigurationSection("holograms");
			Set<String> hologramsKeys = hologramsSC.getKeys(false);
			for (String key : hologramsKeys) {
				this.holograms.put(key, new ArrayList<>());
				for (String line : hologramsSC.getStringList(key)) {
					((ArrayList<String>) this.holograms.get(key)).add(ChatColor.translateAlternateColorCodes('&', line));
				}
			}

			ConfigurationSection gameStatesSC = msgsConfig.getConfigurationSection("gameStates");
			if (gameStatesSC != null) {
				Set<String> gameStatesKeys = gameStatesSC.getKeys(false);
				for (String key : gameStatesKeys) {
					GameState gm = GameState.valueOf(key);
					if (gm != null) {
						this.gameStates.put(gm, gameStatesSC.getString(key, gm.toString()));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Bukkit.getLogger().log(Level.SEVERE, "Something is wrong with your messages.yml file!");
			Main.getPlugin().getPluginLoader().disablePlugin(Main.getPlugin());
		}
	}

	private String replaceExtra(String line, String extra, String extra1, String extra2, String extra3, String extra4) {
		if (line == null) {
			return "";
		}
		if (extra != null) {
			line = line.replaceAll("%value%", extra);
		}
		if (extra1 != null) {
			line = line.replaceAll("%value1%", extra1);
		}
		if (extra2 != null) {
			line = line.replaceAll("%value2%", extra2);
		}
		if (extra3 != null) {
			line = line.replaceAll("%value3%", extra3);
		}
		if (extra4 != null) {
			line = line.replaceAll("%value4%", extra4);
		}
		return line;
	}

	private String replaceArena(String output, Arena arena) {
		if (output == null) {
			return "";
		}
		if (arena != null) {
			output = output.replaceAll("%players%", (new StringBuilder(String.valueOf(arena.getPlayersInfo().size()))).toString());
			output = output.replaceAll("%arena%", arena.getName());
			output = output.replaceAll("%arena1%", arena.getDisplayName());
			output = output.replaceAll("%state%", Main.getMessagesManager().getGameState(arena.getGameState()));
			output = output.replaceAll("%maxplayers%", "" + arena.getMaxPlayers());
			output = output.replaceAll("%statecolor%", "" + Utils.getStateColor(arena.getGameState()));
		}
		return output;
	}

	public String getSignLine(int i, Arena arena) {
		String line = this.signLines.get(i);
		if (line == null) {
			return "";
		}
		if (i != 0 && arena != null) {
			line = replaceArena(line, arena);
		}
		return line;
	}

	public String getGameMsg(String key, Arena arena, String extra, String extra1, String extra2, String extra3, String extra4) {
		if (this.msgsGame == null) {
			return "";
		}
		String output = this.msgsGame.get(key);
		if (output == null) {
			Main.getPlugin().getLogger().warning("Game msg '" + key + "' is missing from your messages.yml file!");
			return "";
		}
		if (arena != null) {
			output = output.replaceAll("%arena%", arena.getDisplayName());
		}
		return replaceExtra(output, extra, extra1, extra2, extra3, extra4);
	}

	public String getGameMsg(String key, Arena arena, String extra, String extra1, String extra2, String extra3) {
		String output = getGameMsg(key, arena, extra, extra1, extra2, extra3, null);
		return output;
	}

	public String getGameMsg(String key, Arena arena, String extra) {
		String output = getGameMsg(key, arena, extra, null, null, null, null);
		return output;
	}

	public String getGameMsg(String key, Arena arena, String extra, String extra1) {
		String output = getGameMsg(key, arena, extra, extra1, null, null, null);
		return output;
	}

	public String getTaskName(String task) {
		if (task == null) {
			return "";
		}
		String key = task.toLowerCase();
		if (this.tasks.get(key) == null) {
			return task.toLowerCase();
		}
		String name = this.tasks.get(key);
		if (name == null) {
			return task.toLowerCase();
		}
		return name;
	}

	public String getSabotageTitle(SabotageType st) {
		if (st == null) {
			return "";
		}
		String out = this.sabotagesTitles.get(st.toString().toLowerCase());
		if (out == null) {
			return "";
		}
		return out;
	}

	public String getScoreboard(String key) {
		String output = this.scoreboard.get(key);
		if (output == null) {
			return "";
		}
		return output;
	}

	public String getScoreboardTaskLine(Arena arena, TaskPlayer tp) {
		String output = this.scoreboard.get(tp.getIsDone().booleanValue() ? "taskDoneLine" : "taskLine");
		if (output == null) {
			return "";
		}
		if (arena != null) {
			output = output.replaceAll("%arena%", arena.getDisplayName());
		}
		if (tp != null) {
			Task task = tp.getActiveTask();
			output = output.replaceAll("%task%", getTaskName(task.getTaskType().toString()));
			output = output.replaceAll("%taskloc%", task.getLocationName().getName());
			output = output.replaceAll("%taskcolor%", "" + tp.getColor());
			if (tp.getTasks().size() > 1) {
				output = output.replaceAll("%state%", "(" + tp.getState() + "/" + tp.getTasks().size() + ")");
			} else {
				output = output.replaceAll("%state%", "");
			}
		}
		return output;
	}

	public String getScoreboardLine(String team, int i, PlayerInfo pInfo) {
		String line = ((ArrayList<String>) this.scoreboardLines.get(team)).get(i);
		line = line.replaceAll("%emptyline%", Utils.getRandomColors());
		if (pInfo != null) {
			if (pInfo.getArena() != null) {
				line = line.replaceAll("%arena%", pInfo.getArena().getDisplayName());

				line = line.replaceAll("%player%", pInfo.getPlayer().getName());
				line = line.replaceAll("%playercolor%", "" + pInfo.getColor().getChatColor());
				line = line.replaceAll("%playercolorname%", (new StringBuilder(String.valueOf(pInfo.getColor().toString().toLowerCase()))).toString());
				line = line.replaceAll("%coins%", "" + pInfo.getStatsManager().getCoins());
				if (pInfo.getArena().getGameState() == GameState.RUNNING || pInfo.getArena().getGameState() == GameState.FINISHING) {
					line = line.replaceAll("%playerteam%", pInfo.getIsImposter().booleanValue() ? "imposter" : "crewmate");
				} else {
					line = line.replaceAll("%minplayers%", (new StringBuilder(String.valueOf(pInfo.getArena().getMinPlayers()))).toString());
					line = line.replaceAll("%maxplayers%", "" + pInfo.getArena().getMaxPlayers());
					line = line.replaceAll("%players%", (new StringBuilder(String.valueOf(pInfo.getArena().getPlayers().size()))).toString());
					line = line.replaceAll("%gamestate%", getGameState(pInfo.getArena().getGameState()));
					if (pInfo.getArena().getGameState() == GameState.WAITING) {
						line = line.replaceAll("%gamestarttime%", "" + pInfo.getArena().getGameTimer());
					} else {
						line = line.replaceAll("%gamestarttime%", "" + pInfo.getArena().getGameTimerActive());
					}
				}
			} else if (Main.getConfigManager().getEnableLobbyScoreboard().booleanValue()) {
				byte b;
				int j;
				StatInt[] arrayOfStatInt;
				for (j = (arrayOfStatInt = StatInt.values()).length, b = 0; b < j;) {
					StatInt statIntE = arrayOfStatInt[b];
					Integer stat_ = pInfo.getStatsManager().getStatInt(statIntE);
					if (stat_ == null) {
						stat_ = Integer.valueOf(0);
					}
					line = line.replaceAll("%" + statIntE.getName() + "%", "" + stat_);
					b++;
				}

				line = line.replaceAll("%coins%", "" + pInfo.getStatsManager().getCoins());
			}
		}
		if (Main.getIsPlaceHolderAPI().booleanValue()) {
			line = PlaceholderAPI.setPlaceholders(pInfo.getPlayer(), line);
		}
		return line;
	}

	public ArrayList<String> getScoreBoardLines(String key) {
		ArrayList<String> out = this.scoreboardLines.get(key);
		return (ArrayList<String>) out.clone();
	}

	public ArrayList<String> getHologramLines(String key, String value, String value1, String value2) {
		ArrayList<String> lines = this.holograms.get(key);
		ArrayList<String> out = new ArrayList<>();
		if (lines == null) {
			Main.getPlugin().getLogger().warning("Hologram '" + key + "' is missing from your messages.yml file!");
			return out;
		}
		for (String line : lines) {
			out.add(replaceExtra(line, value, value1, value2, null, null));
		}
		return out;
	}

	public ArrayList<String> getHologramLines(String key, String value, String value1) {
		return getHologramLines(key, value, value1, null);
	}

	public ArrayList<String> getHologramLines(String key, String value) {
		return getHologramLines(key, value, null, null);
	}

	public ArrayList<String> getEstimatedTimes() {
		return this.estimatedTimes;
	}

	public String getGameState(GameState gm) {
		if (this.gameStates == null || this.gameStates.get(gm) == null) {
			return gm.toString();
		}
		return this.gameStates.get(gm);
	}

	public void delete() {
		this.msgsGame = null;
		this.scoreboard = null;
		this.tasks = null;
		this.sabotagesTitles = null;
		this.scoreboardLines = null;
		this.signLines = null;
		this.holograms = null;
		this.estimatedTimes = null;
		this.gameStates = null;
	}
}
