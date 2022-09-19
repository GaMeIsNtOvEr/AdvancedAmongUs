package com.nktfh100.amongus.commands;

import com.nktfh100.AmongUs.enums.GameState;
import com.nktfh100.AmongUs.info.Arena;
import com.nktfh100.AmongUs.info.PlayerInfo;
import com.nktfh100.AmongUs.inventory.ArenaSetupGui;
import com.nktfh100.AmongUs.main.Main;
import com.nktfh100.AmongUs.utils.Utils;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class AdminCommand implements CommandExecutor {
	public static final ArrayList<String> settings = new ArrayList<>(Arrays.asList(new String[] { "minplayers", "maxplayers", "gametimer", "votingtime", "discussiontime", "imposters", "commontasks",
			"longtasks", "shorttasks", "meetingsperplayer", "killcooldown", "meetingcooldown", "sabotagecooldown", "reportdistance", "impostervision", "crewmatevision" }));

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 0 && (sender.hasPermission("amongus.admin") || sender.hasPermission("amongus.admin.setup"))) {
			sender.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "------------------------------------------");
			sender.sendMessage("       " + ChatColor.GOLD + ChatColor.BOLD + "Among Us V" + Main.getPlugin().getDescription().getVersion());
			sender.sendMessage("         " + ChatColor.GOLD + ChatColor.BOLD + "by nktfh100");
			sender.sendMessage(ChatColor.YELLOW + "/aua reload" + ChatColor.WHITE + " - " + ChatColor.GOLD + "Reload the config");
			sender.sendMessage(ChatColor.YELLOW + "/aua listarenas" + ChatColor.WHITE + " - " + ChatColor.GOLD + "List all created arenas");
			sender.sendMessage(ChatColor.YELLOW + "/aua createarena <Arena Name> <Min players> <Max players> <Imposters>" + ChatColor.WHITE + " - " + ChatColor.GOLD + "Create a new arena");
			sender.sendMessage(ChatColor.YELLOW + "/aua setup" + ChatColor.WHITE + " - " + ChatColor.GOLD + "Open arena setup menu");
			sender.sendMessage(ChatColor.YELLOW + "/aua addlocation <Arena name> <Location Name>" + ChatColor.WHITE + " - " + ChatColor.GOLD + "Add location name to arena");
			sender.sendMessage(ChatColor.YELLOW + "/aua setsetting <Arena name> <Setting To Change> <Target Value>" + ChatColor.WHITE + " - " + ChatColor.GOLD + "Change various arena settings");
			sender.sendMessage(ChatColor.YELLOW + "/aua setmainlobby" + ChatColor.WHITE + " - " + ChatColor.GOLD + "Set the main lobby location");
			sender.sendMessage(ChatColor.YELLOW + "/aua start <Arena Name>" + ChatColor.WHITE + " - " + ChatColor.GOLD + "Start game");
			sender.sendMessage(ChatColor.YELLOW + "/aua endgame <Arena Name>" + ChatColor.WHITE + " - " + ChatColor.GOLD + "End game");
			sender.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "------------------------------------------");
		} else if (args.length == 0 && sender.hasPermission("amongus.admin.startgame") && !sender.hasPermission("amongus.admin.endgame")) {
			sender.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "------------------------------------------");
			sender.sendMessage("       " + ChatColor.GOLD + ChatColor.BOLD + "Among Us V" + Main.getPlugin().getDescription().getVersion());
			sender.sendMessage("         " + ChatColor.GOLD + ChatColor.BOLD + "by nktfh100");
			sender.sendMessage(ChatColor.YELLOW + "/aua start <Arena Name>" + ChatColor.WHITE + " - " + ChatColor.GOLD + "Start game");
			sender.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "------------------------------------------");
		} else if (args.length == 0 && sender.hasPermission("amongus.admin.endgame") && !sender.hasPermission("amongus.admin.startgame")) {
			sender.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "------------------------------------------");
			sender.sendMessage("       " + ChatColor.GOLD + ChatColor.BOLD + "Among Us V" + Main.getPlugin().getDescription().getVersion());
			sender.sendMessage("         " + ChatColor.GOLD + ChatColor.BOLD + "by nktfh100");
			sender.sendMessage(ChatColor.YELLOW + "/aua endgame <Arena Name>" + ChatColor.WHITE + " - " + ChatColor.GOLD + "End game");
			sender.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "------------------------------------------");
		} else if (args.length == 0 && sender.hasPermission("amongus.admin.endgame") && sender.hasPermission("amongus.admin.startgame")) {
			sender.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "------------------------------------------");
			sender.sendMessage("       " + ChatColor.GOLD + ChatColor.BOLD + "Among Us V" + Main.getPlugin().getDescription().getVersion());
			sender.sendMessage("         " + ChatColor.GOLD + ChatColor.BOLD + "by nktfh100");
			sender.sendMessage(ChatColor.YELLOW + "/aua start <Arena Name>" + ChatColor.WHITE + " - " + ChatColor.GOLD + "Start game");
			sender.sendMessage(ChatColor.YELLOW + "/aua endgame <Arena Name>" + ChatColor.WHITE + " - " + ChatColor.GOLD + "End game");
			sender.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "------------------------------------------");
		} else if (args.length != 0) {

			if (args[0].equalsIgnoreCase("reload") && (sender.hasPermission("amongus.admin.setup") || sender.hasPermission("amongus.admin"))) {
				Main.reloadConfigs();
				sender.sendMessage(String.valueOf(Main.getConfigManager().getPrefix()) + ChatColor.GREEN + "Successfully loaded the config");

			} else if (args[0].equalsIgnoreCase("setup") && sender instanceof Player && (sender.hasPermission("amongus.admin.setup") || sender.hasPermission("amongus.admin"))) {
				if (args.length == 1) {
					ArenaSetupGui.openArenaSetupSelector((Player) sender);
				} else {
					Player player = (Player) sender;
					Arena arena = Main.getArenaManager().getArenaByName(args[1]);
					if (arena != null) {
						if (args.length == 2) {
							ArenaSetupGui.openArenaEditor(player, arena);
						} else if (args.length == 3) {
							String str;
							switch ((str = args[2].toLowerCase()).hashCode()) {
							case -1197189282:
								if (!str.equals("locations")) {
									break;
								}

								ArenaSetupGui.openLocationSelector(player, arena, Integer.valueOf(1));
								return true;
							case -896172968:
								if (!str.equals("spawns"))
									break;
								ArenaSetupGui.openSpawnsEditor(player, arena);
								return true;
							case -80951411:
								if (!str.equals("sabotages"))
									break;
								ArenaSetupGui.openSabotageSelector(player, arena);
								return true;
							case 95769221:
								if (!str.equals("doors"))
									break;
								ArenaSetupGui.openDoorGroupSelector(player, arena);
								return true;
							case 110132110:
								if (!str.equals("tasks"))
									break;
								ArenaSetupGui.openTasksSelectLocation(player, arena, Integer.valueOf(1));
								return true;
							case 112093790:
								if (!str.equals("vents"))
									break;
								ArenaSetupGui.openVentsGroupsSelector(player, arena);
								return true;
							case 549364206:
								if (!str.equals("cameras"))
									break;
								ArenaSetupGui.openCamerasEdit(player, arena);
								return true;
							}
							ArenaSetupGui.openArenaEditor(player, arena);
							return true;
						}
					} else {

						sender.sendMessage(String.valueOf(Main.getConfigManager().getPrefix()) + ChatColor.RED + "Arena doesn't exist!");

					}

				}

			} else if (args[0].equalsIgnoreCase("addlocation") && sender instanceof Player && (sender.hasPermission("amongus.admin.setup") || sender.hasPermission("amongus.admin"))) {
				if (args.length <= 2 || args[1] == null || args[2] == null) {
					sender.sendMessage(String.valueOf(Main.getConfigManager().getPrefix()) + ChatColor.RED + "/aua addlocation <Arena Name> <Location Name>");
				} else if (Main.getArenaManager().getArenaByName(args[1]) != null) {
					Arena arena = Main.getArenaManager().getArenaByName(args[1]);
					Player player = (Player) sender;
					Location loc = player.getLocation();
					String locStr = String.valueOf(loc.getWorld().getName()) + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ();
					ConfigurationSection locationsSC = Main.getConfigManager().getConfig().getConfigurationSection("arenas." + arena.getName() + ".locations");

					ConfigurationSection locSC = locationsSC.createSection(Utils.getRandomString(4));

					locSC.set("name", args[2]);
					locSC.set("location", locStr);

					Main.getPlugin().saveConfig();
					Main.getArenaManager().loadArenas();

					sender.sendMessage(String.valueOf(Main.getConfigManager().getPrefix()) + ChatColor.GREEN + "Added location successfully!");
				} else {
					sender.sendMessage(String.valueOf(Main.getConfigManager().getPrefix()) + ChatColor.RED + "Arena doesn't exist!");

				}

			} else if (args[0].equalsIgnoreCase("setsetting") && sender instanceof Player && (sender.hasPermission("amongus.admin.setup") || sender.hasPermission("amongus.admin"))) {
				if (args.length == 4) {
					if (Main.getArenaManager().getArenaByName(args[1]) != null) {
						Integer value1;
						Double value;
						Arena arena = Main.getArenaManager().getArenaByName(args[1]);

						args[2] = args[2].toLowerCase();
						String settingSelected = null;
						for (String ss : settings) {
							if (ss.equals(args[2])) {
								settingSelected = ss;
								break;
							}
						}
						if (settingSelected == null) {
							sender.sendMessage(String.valueOf(Main.getConfigManager().getPrefix()) + ChatColor.RED + "Invalid setting type!");
							sender.sendMessage(String.valueOf(Main.getConfigManager().getPrefix()) + ChatColor.RED + settings.toString());
							return false;
						}
						ConfigurationSection arenaSC = Main.getConfigManager().getConfig().getConfigurationSection("arenas." + arena.getName());

						try {
							value = Double.valueOf(args[3]);
							value1 = Integer.valueOf(args[3]);
						} catch (Exception e) {
							sender.sendMessage(String.valueOf(Main.getConfigManager().getPrefix()) + ChatColor.RED + "Target value must be a valid number!");
							return false;
						}
						String str;
						switch ((str = args[2]).hashCode()) {
						case -2055936346:
							if (!str.equals("meetingcooldown")) {
								break;
							}

							arena.setMeetingCooldown(value1);
							arenaSC.set(args[2], value1);
							break;
						case -1905931133:
							if (!str.equals("commontasks"))
								break;
							arena.setCommonTasks(value1);
							arenaSC.set(args[2], value1);
							break;
						case -1539347159:
							if (!str.equals("reportdistance"))
								break;
							arena.setReportDistance(value);
							arenaSC.set(args[2], value);
							break;

						case -1074954418:
							if (!str.equals("crewmatevision")) {
								break;
							}
							arena.setCrewmateVision(value1);
							arenaSC.set(args[2], value1);
							break;
						case -1000667959:
							if (!str.equals("killcooldown"))
								break;
							arena.setKillCooldown(value1);
							arenaSC.set(args[2], value1);
							break;
						case -984284210:
							if (!str.equals("maxplayers"))
								break;
							arena.setMaxPlayers(value1);
							arenaSC.set(args[2], value1);
							break;
						case -779686127:
							if (!str.equals("sabotagecooldown"))
								break;
							arena.setSabotageCooldown(value1);
							arenaSC.set(args[2], value1);
							break;
						case -207597790:
							if (!str.equals("imposters"))
								break;
							arena.setNumImposters(value1);
							arenaSC.set(args[2], value1);
							break;
						case 146956946:
							if (!str.equals("longtasks"))
								break;
							arena.setLongTasks(value1);
							arenaSC.set(args[2], value1);
							break;
						case 395382694:
							if (!str.equals("meetingsperplayer"))
								break;
							arena.setMeetingsPerPlayer(value1);
							arenaSC.set(args[2], value1);
							break;
						case 1020224147:
							if (!str.equals("gametimer"))
								break;
							arena.setGameTimer(value1);
							arenaSC.set(args[2], value1);
							break;
						case 1487715104:
							if (!str.equals("minplayers"))
								break;
							arena.setMinPlayers(value1);
							arenaSC.set(args[2], value1);
							break;
						case 1584817298:
							if (!str.equals("shorttasks"))
								break;
							arena.setShortTasks(value1);
							arenaSC.set(args[2], value1);
							break;
						case 1683612313:
							if (!str.equals("impostervision"))
								break;
							arena.setImposterVision(value1);
							arenaSC.set(args[2], value1);
							break;
						case 1771291764:
							if (!str.equals("votingtime"))
								break;
							arena.setVotingTime(value1);
							arenaSC.set(args[2], value1);
							break;
						case 2016491253:
							if (!str.equals("discussiontime"))
								break;
							arena.setDiscussionTime(value1);
							arenaSC.set(args[2], value1);
							break;
						}
						Main.getPlugin().saveConfig();

						sender.sendMessage(String.valueOf(Main.getConfigManager().getPrefix()) + ChatColor.GREEN + "Successfully changed " + args[2] + " to " + args[3]);
					} else {
						sender.sendMessage(String.valueOf(Main.getConfigManager().getPrefix()) + ChatColor.RED + "Arena doesn't exist!");
					}
				} else {
					sender.sendMessage(String.valueOf(Main.getConfigManager().getPrefix()) + ChatColor.RED + "/aua setsetting <Arena name> <Setting To Change> <Target Value>");

				}

			} else if (args[0].equalsIgnoreCase("start") && (sender.hasPermission("amongus.admin.startgame") || sender.hasPermission("amongus.admin"))) {
				if (args.length == 2) {
					if (Main.getArenaManager().getArenaByName(args[1]) != null) {
						Arena arena = Main.getArenaManager().getArenaByName(args[1]);
						if (arena.getPlayersInfo().size() >= arena.getMinPlayers()) {
							sender.sendMessage(String.valueOf(Main.getConfigManager().getPrefix()) + ChatColor.GREEN + "Starting game!");
							arena.startGame();
						} else {
							sender.sendMessage(String.valueOf(Main.getConfigManager().getPrefix()) + ChatColor.RED + "Not enough players!");
						}
					} else {
						sender.sendMessage(String.valueOf(Main.getConfigManager().getPrefix()) + ChatColor.RED + "Arena doesn't exist!");
					}

				} else if (sender instanceof Player) {
					Player player = (Player) sender;
					PlayerInfo pInfo = Main.getPlayersManager().getPlayerInfo(player);
					if (pInfo.getIsIngame().booleanValue()) {
						if (pInfo.getArena().getGameState() == GameState.WAITING || pInfo.getArena().getGameState() == GameState.STARTING) {
							Main.getPlayersManager().getPlayerInfo(player).getArena().startGame();
							sender.sendMessage(String.valueOf(Main.getConfigManager().getPrefix()) + ChatColor.GREEN + "Starting game!");
						} else {
							sender.sendMessage(String.valueOf(Main.getConfigManager().getPrefix()) + ChatColor.RED + "Arena already in-game!");
						}
					} else {
						sender.sendMessage(String.valueOf(Main.getConfigManager().getPrefix()) + ChatColor.RED + "Either be in-game or specify arena!");

					}

				}

			} else if (args[0].equalsIgnoreCase("endgame") && (sender.hasPermission("amongus.admin.endgame") || sender.hasPermission("amongus.admin"))) {
				if (args.length == 2) {
					if (Main.getArenaManager().getArenaByName(args[1]) != null) {
						Main.getArenaManager().getArenaByName(args[1]).endGame(Boolean.valueOf(true));
						sender.sendMessage(String.valueOf(Main.getConfigManager().getPrefix()) + ChatColor.GREEN + "Ending game!");
					} else {
						sender.sendMessage(String.valueOf(Main.getConfigManager().getPrefix()) + ChatColor.RED + "Arena doesn't exist!");
					}

				} else if (sender instanceof Player) {
					Player player = (Player) sender;
					PlayerInfo pInfo = Main.getPlayersManager().getPlayerInfo(player);
					if (pInfo.getIsIngame().booleanValue()) {
						if (pInfo.getArena().getGameState() != GameState.WAITING) {
							Main.getPlayersManager().getPlayerInfo(player).getArena().endGame(Boolean.valueOf(true));
							sender.sendMessage(String.valueOf(Main.getConfigManager().getPrefix()) + ChatColor.GREEN + "Ending game!");
						} else {
							sender.sendMessage(String.valueOf(Main.getConfigManager().getPrefix()) + ChatColor.RED + "Arena is not playing!");
						}
					} else {
						sender.sendMessage(String.valueOf(Main.getConfigManager().getPrefix()) + ChatColor.RED + "Either be in-game or specify arena!");

					}

				}

			} else if (args[0].equalsIgnoreCase("createarena") && sender instanceof Player && (sender.hasPermission("amongus.admin.setup") || sender.hasPermission("amongus.admin"))) {
				if (args.length == 5) {
					if (Main.getArenaManager().getArenaByName(args[1]) == null) {
						Integer minPlayers = Integer.valueOf(args[2]);
						Integer maxPlayers = Integer.valueOf(args[3]);
						Integer imposters = Integer.valueOf(args[4]);
						if (maxPlayers > 16) {
							sender.sendMessage(String.valueOf(Main.getConfigManager().getPrefix()) + ChatColor.RED + "Maximum players is 16!");
							return true;
						}
						if (imposters >= maxPlayers / 2 || imposters >= minPlayers) {
							sender.sendMessage(String.valueOf(Main.getConfigManager().getPrefix()) + ChatColor.RED + "Too many imposters!");
							return true;
						}
						Main.getArenaManager().createArena(args[1]);
						FileConfiguration config = Main.getConfigManager().getConfig();
						String prefix = "arenas." + args[1];
						config.set(String.valueOf(prefix) + ".displayname", args[1]);
						config.set(String.valueOf(prefix) + ".spawnpoints", new ArrayList());
						config.set(String.valueOf(prefix) + ".minplayers", minPlayers);
						config.set(String.valueOf(prefix) + ".maxplayers", maxPlayers);
						Location loc = ((Player) sender).getLocation();
						config.set(String.valueOf(prefix) + ".mapcenter", String.valueOf(loc.getWorld().getName()) + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ());
						config.set(String.valueOf(prefix) + ".meetingbtn", String.valueOf(loc.getWorld().getName()) + ",0,0,0");
						config.createSection(String.valueOf(prefix) + ".tasks");
						config.createSection(String.valueOf(prefix) + ".sabotages");
						config.createSection(String.valueOf(prefix) + ".locations");
						config.createSection(String.valueOf(prefix) + ".ventgroups");
						config.createSection(String.valueOf(prefix) + ".doorgroups");
						config.createSection(String.valueOf(prefix) + ".cameras");
						config.set(String.valueOf(prefix) + ".cameras.hologramloc", String.valueOf(loc.getWorld().getName()) + ",0,0,0");
						config.set(String.valueOf(prefix) + ".cameras.enable", Boolean.valueOf(false));
						config.createSection(String.valueOf(prefix) + ".cameras.cams");
						config.set(String.valueOf(prefix) + ".disablesprinting", Boolean.valueOf(false));
						config.set(String.valueOf(prefix) + ".disablejumping", Boolean.valueOf(false));
						config.set(String.valueOf(prefix) + ".disablemap", Boolean.valueOf(false));
						config.set(String.valueOf(prefix) + ".gametimer", Integer.valueOf(30));
						config.set(String.valueOf(prefix) + ".votingtime", Integer.valueOf(45));
						config.set(String.valueOf(prefix) + ".discussiontime", Integer.valueOf(30));
						config.set(String.valueOf(prefix) + ".proceedingtime", Integer.valueOf(5));
						config.set(String.valueOf(prefix) + ".enablevisualtasks", Boolean.valueOf(true));
						config.set(String.valueOf(prefix) + ".confirmejects", Boolean.valueOf(true));
						config.set(String.valueOf(prefix) + ".imposters", imposters);
						config.set(String.valueOf(prefix) + ".commontasks", Integer.valueOf(1));
						config.set(String.valueOf(prefix) + ".longtasks", Integer.valueOf(1));
						config.set(String.valueOf(prefix) + ".shorttasks", Integer.valueOf(2));
						config.set(String.valueOf(prefix) + ".mettingsperplayer", Integer.valueOf(1));
						config.set(String.valueOf(prefix) + ".killcooldown", Integer.valueOf(30));
						config.set(String.valueOf(prefix) + ".meetingcooldown", Integer.valueOf(10));
						config.set(String.valueOf(prefix) + ".sabotagecooldown", Integer.valueOf(25));
						config.set(String.valueOf(prefix) + ".reportdistance", Double.valueOf(3.5D));
						config.set(String.valueOf(prefix) + ".enablereducedvision", Boolean.valueOf(true));
						config.set(String.valueOf(prefix) + ".hidehologramsoutofview", Boolean.valueOf(false));
						config.set(String.valueOf(prefix) + ".dynamicimposters", Boolean.valueOf(false));
						config.set(String.valueOf(prefix) + ".impostervision", Integer.valueOf(15));
						config.set(String.valueOf(prefix) + ".crewmatevision", Integer.valueOf(10));
						config.set(prefix + ".enableredstone", false);
						config.set(String.valueOf(prefix) + ".primeshieldsblocks", new ArrayList());
						config.set(String.valueOf(prefix) + ".waitinglobby",
								String.valueOf(loc.getWorld().getName()) + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ() + "," + loc.getYaw() + "," + loc.getPitch());
						config.set(String.valueOf(prefix) + ".movemapwithplayer", Boolean.valueOf(false));
						config.set(String.valueOf(prefix) + ".world", ((Player) sender).getWorld().getName());
						config.set(String.valueOf(prefix) + ".mapids", "");

						Main.getPlugin().saveConfig();
						Main.getArenaManager().loadArenas();
						sender.sendMessage(String.valueOf(Main.getConfigManager().getPrefix()) + ChatColor.GREEN + "Created arena successfully!");
					} else {
						sender.sendMessage(String.valueOf(Main.getConfigManager().getPrefix()) + ChatColor.RED + "Arena already exists!");
					}
				} else {
					sender.sendMessage(String.valueOf(Main.getConfigManager().getPrefix()) + ChatColor.RED + "/aua createarena <Arena Name> <Min players> <Max players> <Imposters>");
				}
			}
			if (args.length == 1) {

				if (args[0].equalsIgnoreCase("listarenas") && (sender.hasPermission("amongus.admin.setup") || sender.hasPermission("amongus.admin"))) {
					ArrayList<String> arenas = Main.getConfigManager().getBungeecord().booleanValue() ? Main.getBungeArenaManager().getAllArenasServerNames()
							: Main.getArenaManager().getAllArenasNames();
					if (arenas.size() == 0) {
						sender.sendMessage(String.valueOf(Main.getConfigManager().getPrefix()) + ChatColor.RED + "No arenas found!");
					} else {
						sender.sendMessage(String.valueOf(Main.getConfigManager().getPrefix()) + StringUtils.join(arenas, ", "));

					}

				} else if (args[0].equalsIgnoreCase("setmainlobby") && sender instanceof Player && (sender.hasPermission("amongus.admin.setup") || sender.hasPermission("amongus.admin"))) {
					Main.getConfigManager().setMainLobby(((Player) sender).getLocation());
					FileConfiguration config = Main.getConfigManager().getConfig();
					sender.sendMessage(String.valueOf(Main.getConfigManager().getPrefix()) + ChatColor.YELLOW + "Main lobby is set.");
					Location lobby_ = ((Player) sender).getLocation();
					config.set("mainLobby.world", lobby_.getWorld().getName());
					config.set("mainLobby.x", Double.valueOf(lobby_.getBlockX()));
					config.set("mainLobby.y", Double.valueOf(lobby_.getBlockY()));
					config.set("mainLobby.z", Double.valueOf(lobby_.getBlockZ()));
					Main.getPlugin().saveConfig();
				}
			}
		}

		return true;
	}
}
