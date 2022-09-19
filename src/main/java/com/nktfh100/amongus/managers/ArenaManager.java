package com.nktfh100.amongus.managers;

import com.comphenix.protocol.wrappers.WrappedBlockData;
import com.google.common.collect.Iterables;
import com.nktfh100.AmongUs.enums.GameState;
import com.nktfh100.AmongUs.enums.SabotageLength;
import com.nktfh100.AmongUs.enums.SabotageType;
import com.nktfh100.AmongUs.enums.TaskType;
import com.nktfh100.AmongUs.info.Arena;
import com.nktfh100.AmongUs.info.Camera;
import com.nktfh100.AmongUs.info.Door;
import com.nktfh100.AmongUs.info.DoorGroup;
import com.nktfh100.AmongUs.info.LocationName;
import com.nktfh100.AmongUs.info.PlayerInfo;
import com.nktfh100.AmongUs.info.QueuedTasksVariant;
import com.nktfh100.AmongUs.info.SabotageArena;
import com.nktfh100.AmongUs.info.SabotageTask;
import com.nktfh100.AmongUs.info.Task;
import com.nktfh100.AmongUs.info.Vent;
import com.nktfh100.AmongUs.info.VentGroup;
import com.nktfh100.AmongUs.inventory.ArenaSelectorInv;
import com.nktfh100.AmongUs.main.Main;
import com.nktfh100.AmongUs.main.Renderer;
import com.nktfh100.AmongUs.utils.Utils;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class ArenaManager {
	private HashMap<String, Arena> arenas = new HashMap<>();

	private ArenaSelectorInv arenaSelectorInv;

	public void openArenaSelector(PlayerInfo pInfo) {
		pInfo.getPlayer().openInventory(this.arenaSelectorInv.getInventory());
	}

	public void updateArenaSelectorInv() {
		this.arenaSelectorInv.update();
	}

	public Arena createArena(String name) {
		this.arenas.put(name, new Arena(name));
		return getArenaByName(name);
	}

	public void deleteArena(String name) {
		this.arenas.remove(name);
	}

	public Arena getArenaByName(String name) {
		if (this.arenas.size() >= 1) {
			return this.arenas.get(name);
		}
		return null;
	}

	public Arena getArenaByDisplayName(String name) {
		for (Arena arena : this.arenas.values()) {
			if (ChatColor.stripColor(arena.getDisplayName()).equalsIgnoreCase(name)) {
				return arena;
			}
		}
		return null;
	}

	public Collection<Arena> getAllArenas() {
		return this.arenas.values();
	}

	public HashMap<String, Arena> getArenas_() {
		return this.arenas;
	}

	public ArrayList<String> getAllArenasNames() {
		return new ArrayList<>(this.arenas.keySet());
	}

	public Arena getArenaWithMostPlayers() {
		if (this.arenas.size() == 0) {
			return null;
		}
		if (this.arenas.size() == 1) {
			return this.arenas.values().iterator().next();
		}
		ArrayList<Arena> arenas_ = new ArrayList<>(getAllArenas());
		Arena arena = null;

		for (Arena arena_ : arenas_) {
			if (arena_.getGameState() == GameState.RUNNING || arena_.getGameState() == GameState.FINISHING) {
				continue;
			}
			if (arena_.getPlayers().size() == arena_.getMaxPlayers()) {
				continue;
			}
			if (arena == null) {
				arena = arena_;
				continue;
			}
			if (arena_.getPlayersInfo().size() > arena.getPlayersInfo().size()) {
				arena = arena_;
			}
		}

		return arena;
	}

	public Arena getRandomArena() {
		if (this.arenas.size() == 0) {
			return null;
		}
		if (this.arenas.size() == 1) {
			return this.arenas.values().iterator().next();
		}
		ArrayList<Arena> arenas_ = new ArrayList<>();
		for (Arena ba : getAllArenas()) {
			if (ba.getGameState() == GameState.RUNNING || ba.getGameState() == GameState.FINISHING) {
				continue;
			}
			if (ba.getPlayersInfo().size() == ba.getMaxPlayers()) {
				continue;
			}
			arenas_.add(ba);
		}
		if (arenas_.size() == 0) {
			return null;
		}
		if (arenas_.size() == 1) {
			return arenas_.get(0);
		}
		return arenas_.get(Utils.getRandomNumberInRange(0, arenas_.size() - 1));
	}

	public void sendBungeUpdate(String name, GameState gm, Integer currentPlayers, Integer maxPlayers) {
		try {
			if (!Main.getPlugin().isEnabled()) {
				return;
			}
			if (getAllArenas().size() == 0 || !Main.getConfigManager().getBungeecord().booleanValue() || Main.getConfigManager().getBungeecordIsLobby().booleanValue()) {
				return;
			}
			if (Bukkit.getOnlinePlayers().size() == 0) {
				return;
			}
			Player player = (Player) Iterables.getFirst(Bukkit.getOnlinePlayers(), null);

			ByteArrayOutputStream b = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(b);

			String str = String.valueOf(Main.getConfigManager().getServerName()) + "," + name + "," + gm + "," + currentPlayers + "," + maxPlayers;

			out.writeUTF("Forward");
			out.writeUTF(Main.getConfigManager().getBungeecordLobbyServer());
			out.writeUTF("AmongUs");
			byte[] data = str.getBytes();
			out.writeShort(data.length);
			out.write(data);

			player.sendPluginMessage(Main.getPlugin(), "BungeeCord", b.toByteArray());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void sendBungeUpdate(Arena arena) {
		sendBungeUpdate(arena.getName(), arena.getGameState(), Integer.valueOf(arena.getPlayers().size()), arena.getMaxPlayers());
	}

	public void loadArenas() {
		for (Arena arena : this.arenas.values()) {
			arena.endGame(Boolean.valueOf(true));
			arena.delete();
		}
		this.arenaSelectorInv = new ArenaSelectorInv();
		this.arenas.clear();
		ConfigurationSection arenasSection = Main.getConfigManager().getConfig().getConfigurationSection("arenas");
		if (arenasSection == null) {
			return;
		}

		try {
			Set<String> arenasKeys = arenasSection.getKeys(false);
			for (String key : arenasKeys) {
				ConfigurationSection arenaSC = arenasSection.getConfigurationSection(key);
				Arena created = createArena(key);
				if (Bukkit.getWorld(arenaSC.getString("world")) == null) {
					Bukkit.getLogger().log(Level.SEVERE, "World " + arenaSC.getString("world") + " Doesn't exists for arena " + key);
					continue;
				}
				created.setWorld(Bukkit.getWorld(arenaSC.getString("world")));
				created.setDisplayName(arenaSC.getString("displayname", key));
				created.setMinPlayers(arenaSC.getInt("minplayers", 5));
				created.setMaxPlayers((arenaSC.getInt("maxplayers", 10) >= 16) ? 16 : arenaSC.getInt("maxplayers", 10));
				created.setDisableSprinting(Boolean.valueOf(arenaSC.getBoolean("disablesprinting", false)));
				created.setDisableJumping(Boolean.valueOf(arenaSC.getBoolean("disablejumping", false)));
				created.setDisableMap(Boolean.valueOf(arenaSC.getBoolean("disablemap", false)));
				created.setVotingTime(Integer.valueOf(arenaSC.getInt("votingtime", 30)));
				created.setDiscussionTime(Integer.valueOf(arenaSC.getInt("discussiontime", 30)));
				created.setNumImposters(Integer.valueOf(arenaSC.getInt("imposters", 2)));
				created.setCommonTasks(Integer.valueOf(arenaSC.getInt("commontasks", 3)));
				created.setLongTasks(Integer.valueOf(arenaSC.getInt("longtasks", 2)));
				created.setShortTasks(Integer.valueOf(arenaSC.getInt("shorttasks", 1)));
				created.setMeetingsPerPlayer(Integer.valueOf(arenaSC.getInt("mettingsperplayer", 1)));
				created.setKillCooldown(Integer.valueOf(arenaSC.getInt("killcooldown", 30)));
				created.setMeetingCooldown(Integer.valueOf(arenaSC.getInt("meetingcooldown", 10)));
				created.setSabotageCooldown(Integer.valueOf(arenaSC.getInt("sabotagecooldown", 17)));
				created.setReportDistance(Double.valueOf(arenaSC.getDouble("reportdistance", 3.5D)));
				created.setEnableReducedVision(Boolean.valueOf(arenaSC.getBoolean("enablereducedvision", true)));
				created.setImposterVision(Integer.valueOf(arenaSC.getInt("impostervision", 15)));
				created.setCrewmateVision(Integer.valueOf(arenaSC.getInt("crewmatevision", 10)));
				created.setGameTimer(Integer.valueOf(arenaSC.getInt("gametimer", 30)));
				created.setProceedingTime(Integer.valueOf(arenaSC.getInt("proceedingtime", 5)));
				created.setHideHologramsOutOfView(Boolean.valueOf(arenaSC.getBoolean("hidehologramsoutofview", false)));
				created.setEnableVisualTasks(Boolean.valueOf(arenaSC.getBoolean("enablevisualtasks", true)));
				created.setConfirmEjects(Boolean.valueOf(arenaSC.getBoolean("confirmejects", true)));
				created.setMoveMapWithPlayer(Boolean.valueOf(arenaSC.getBoolean("movemapwithplayer", false)));
				created.setDynamicImposters(Boolean.valueOf(arenaSC.getBoolean("dynamicimposters", false)));
				created.setEnableRedstone(arenaSC.getBoolean("enableredstone", false));

				String[] locCenterInfo = arenaSC.getString("mapcenter").split(",");
				Location locCenter = new Location(Bukkit.getWorld(locCenterInfo[0]), Double.valueOf(locCenterInfo[1]).doubleValue(), Double.valueOf(locCenterInfo[2]).doubleValue(),
						Double.valueOf(locCenterInfo[3]).doubleValue());

				created.setMapCenter(locCenter);

				String[] locMeetingBtnInfo = arenaSC.getString("meetingbtn").split(",");
				Location locMeetingBtn = new Location(Bukkit.getWorld(locMeetingBtnInfo[0]), Double.valueOf(locMeetingBtnInfo[1]).doubleValue(), Double.valueOf(locMeetingBtnInfo[2]).doubleValue(),
						Double.valueOf(locMeetingBtnInfo[3]).doubleValue());

				created.setMeetingButton(locMeetingBtn);

				String[] locWaitingLobbyInfo = arenaSC.getString("waitinglobby").split(",");
				Location locWaitingLobby = new Location(Bukkit.getWorld(locWaitingLobbyInfo[0]), Double.valueOf(locWaitingLobbyInfo[1]).doubleValue(),
						Double.valueOf(locWaitingLobbyInfo[2]).doubleValue(), Double.valueOf(locWaitingLobbyInfo[3]).doubleValue(), Double.valueOf(locWaitingLobbyInfo[4]).floatValue(),
						Double.valueOf(locWaitingLobbyInfo[5]).floatValue());

				created.setWaitingLobby(locWaitingLobby);

				HashMap<String, LocationName> locations = new HashMap<>();
				Set<String> locsKeys = arenaSC.getConfigurationSection("locations").getKeys(false);
				for (String lKey : locsKeys) {
					ConfigurationSection locSec = arenaSC.getConfigurationSection("locations." + lKey);
					String locName = locSec.getString("name");

					String[] locInfo = locSec.getString("location").split(",");
					Location loc = new Location(Bukkit.getWorld(locInfo[0]), Double.valueOf(locInfo[1]).doubleValue(), Double.valueOf(locInfo[2]).doubleValue(),
							Double.valueOf(locInfo[3]).doubleValue());
					locations.put(lKey, new LocationName(lKey, locName, loc));
				}
				created.setLocations(locations);

				Set<String> tasksKeys = arenaSC.getConfigurationSection("tasks").getKeys(false);
				for (String tKey : tasksKeys) {
					ConfigurationSection taskSec = arenaSC.getConfigurationSection("tasks." + tKey);
					Task createdTask = new Task(tKey);
					TaskType createdType = TaskType.valueOf(taskSec.getString("type"));
					String[] locInfo = taskSec.getString("location").split(",");
					Location loc = new Location(Bukkit.getWorld(locInfo[0]), Double.valueOf(locInfo[1]).doubleValue(), Double.valueOf(locInfo[2]).doubleValue(),
							Double.valueOf(locInfo[3]).doubleValue());
					Boolean isEnabled_ = Boolean.valueOf(taskSec.getBoolean("isenabled", true));

					Boolean enableVisual = Boolean.valueOf(taskSec.getBoolean("enablevisuals", true));

					ConfigurationSection queuedTasksSection = taskSec.getConfigurationSection("queuedtasks");
					if (queuedTasksSection != null) {
						Set<String> queuedTasksVarientsKeys = queuedTasksSection.getKeys(false);
						Integer qtI = Integer.valueOf(0);
						for (String qtKey : queuedTasksVarientsKeys) {
							createdTask.addQueuedTasksVariant(new QueuedTasksVariant(created, new ArrayList(queuedTasksSection.getStringList(qtKey)), qtKey, qtI));
							qtI = Integer.valueOf(qtI + 1);
						}
					}

					LocationName locName = locations.get(taskSec.getString("locationid"));
					createdTask.setInfo(createdType, loc, locName, created, isEnabled_, enableVisual);
					if (locName != null) {
						created.addTask(createdTask);
						if (createdType == TaskType.CLEAR_ASTEROIDS) {
							String[] locInfo1 = taskSec.getString("cannon1", "world,0,0,0,0,0").split(",");
							Location loc1 = new Location(Bukkit.getWorld(locInfo1[0]), Double.valueOf(locInfo1[1]).doubleValue(), Double.valueOf(locInfo1[2]).doubleValue(),
									Double.valueOf(locInfo1[3]).doubleValue(), Double.valueOf(locInfo1[4]).floatValue(), Double.valueOf(locInfo1[5]).floatValue());

							String[] locInfo2 = taskSec.getString("cannon2", "world,0,0,0,0,0").split(",");
							Location loc2 = new Location(Bukkit.getWorld(locInfo2[0]), Double.valueOf(locInfo2[1]).doubleValue(), Double.valueOf(locInfo2[2]).doubleValue(),
									Double.valueOf(locInfo2[3]).doubleValue(), Double.valueOf(locInfo2[4]).floatValue(), Double.valueOf(locInfo2[5]).floatValue());
							createdTask.setAsteroidsInfo(loc1, loc2);
							continue;
						}
						if (createdType == TaskType.RECORD_TEMPERATURE) {
							createdTask.setIsHot(Boolean.valueOf(taskSec.getBoolean("hot", false)));
						}
					}
				}

				Set<String> sabotagesKeys = arenaSC.getConfigurationSection("sabotages").getKeys(false);
				for (String sKey : sabotagesKeys) {
					ConfigurationSection sabotageSec = arenaSC.getConfigurationSection("sabotages." + sKey);
					SabotageType createdType = SabotageType.valueOf(sKey);
					Integer timer = Integer.valueOf(sabotageSec.getInt("timer", 0));
					SabotageTask createdSabo1 = new SabotageTask(createdType, Integer.valueOf(0), timer);

					String[] locInfo1 = sabotageSec.getString("location1").split(",");
					Location loc1 = new Location(Bukkit.getWorld(locInfo1[0]), Double.valueOf(locInfo1[1]).doubleValue(), Double.valueOf(locInfo1[2]).doubleValue(),
							Double.valueOf(locInfo1[3]).doubleValue());

					createdSabo1.setInfo(loc1, created);

					SabotageTask createdSabo2 = null;
					if (createdType.getSabotageLength() != SabotageLength.SINGLE) {
						String[] locInfo2 = sabotageSec.getString("location2").split(",");
						Location loc2 = new Location(Bukkit.getWorld(locInfo2[0]), Double.valueOf(locInfo2[1]).doubleValue(), Double.valueOf(locInfo2[2]).doubleValue(),
								Double.valueOf(locInfo2[3]).doubleValue());
						createdSabo2 = new SabotageTask(createdType, Integer.valueOf(1), timer);
						createdSabo2.setInfo(loc2, created);
					}

					created.addSabotage(new SabotageArena(created, createdSabo1, createdSabo2));
				}

				for (String str : arenaSC.getStringList("spawnpoints")) {
					String[] locs = str.split(",");
					World world = Bukkit.getServer().getWorld(locs[0]);
					if (world == null) {
						Bukkit.getLogger().info("World: " + world + " doesn't exists!");
						continue;
					}
					Location loc = new Location(world, Double.valueOf(locs[1]).doubleValue(), Double.valueOf(locs[2]).doubleValue(), Double.valueOf(locs[3]).doubleValue(),
							Double.valueOf(locs[4]).floatValue(), Double.valueOf(locs[5]).floatValue());
					created.addPlayerSpawn(loc);
				}

				for (String locStr : arenaSC.getStringList("signs")) {
					String[] locInfo = locStr.split(",");
					Location loc = new Location(Bukkit.getWorld(locInfo[0]), Double.valueOf(locInfo[1]).doubleValue(), Double.valueOf(locInfo[2]).doubleValue(),
							Double.valueOf(locInfo[3]).doubleValue());
					created.addSign(loc);
				}
				created.updateSigns();

				Set<String> ventGroupsKeys = arenaSC.getConfigurationSection("ventgroups").getKeys(false);
				for (String vgKey : ventGroupsKeys) {
					ConfigurationSection ventGroupSec = arenaSC.getConfigurationSection("ventgroups." + vgKey);
					ConfigurationSection ventsSec = ventGroupSec.getConfigurationSection("vents");

					VentGroup createdVentGroup = new VentGroup(created, vgKey, Integer.valueOf(ventGroupSec.getInt("id")), Boolean.valueOf(ventGroupSec.getBoolean("loop")));
					for (String ventKey : ventsSec.getKeys(false)) {
						ConfigurationSection ventSec = ventsSec.getConfigurationSection(ventKey);

						String[] locInfo = ventSec.getString("location").split(",");
						Location loc = new Location(Bukkit.getWorld(locInfo[0]), Double.valueOf(locInfo[1]).doubleValue(), Double.valueOf(locInfo[2]).doubleValue(),
								Double.valueOf(locInfo[3]).doubleValue(), Double.valueOf(locInfo[4]).floatValue(), Double.valueOf(locInfo[5]).floatValue());

						String locId = ventSec.getString("locationid");
						LocationName locName = null;
						if (locId != "----") {
							locName = locations.get(locId);
						}
						createdVentGroup.addVent(new Vent(created, createdVentGroup, loc, locName, Integer.valueOf(ventSec.getInt("id")), ventKey));
					}
					created.getVentsManager().addVentGroup(createdVentGroup);
				}

				ConfigurationSection camerasTopSec = arenaSC.getConfigurationSection("cameras");
				if (camerasTopSec != null) {
					Boolean enableCams = Boolean.valueOf(camerasTopSec.getBoolean("enable"));
					created.setEnableCameras(enableCams);

					String[] camerasLocInfo = camerasTopSec.getString("hologramloc").split(",");
					created.setCamerasLoc(new Location(Bukkit.getWorld(camerasLocInfo[0]), Double.valueOf(camerasLocInfo[1]).doubleValue(), Double.valueOf(camerasLocInfo[2]).doubleValue(),
							Double.valueOf(camerasLocInfo[3]).doubleValue()));

					ConfigurationSection camerasSec = camerasTopSec.getConfigurationSection("cams");
					for (String camKey : camerasSec.getKeys(false)) {
						ConfigurationSection cameraSec = camerasSec.getConfigurationSection(camKey);

						String[] viewlocInfo = cameraSec.getString("viewlocation").split(",");
						Location loc1 = new Location(Bukkit.getWorld(viewlocInfo[0]), Double.valueOf(viewlocInfo[1]), Double.valueOf(viewlocInfo[2]), Double.valueOf(viewlocInfo[3]),
								Double.valueOf(viewlocInfo[4]).floatValue(), Double.valueOf(viewlocInfo[5]).floatValue());

						String[] camlocInfo = cameraSec.getString("camlocation").split(",");
						Location loc2 = new Location(Bukkit.getWorld(camlocInfo[0]), Double.valueOf(camlocInfo[1]), Double.valueOf(camlocInfo[2]), Double.valueOf(camlocInfo[3]),
								Double.valueOf(camlocInfo[4]).floatValue(), Double.valueOf(camlocInfo[5]).floatValue());

						String[] lamplocInfo = cameraSec.getString("lamplocation").split(",");
						Location loc3 = new Location(Bukkit.getWorld(lamplocInfo[0]), Double.valueOf(lamplocInfo[1]), Double.valueOf(lamplocInfo[2]), Double.valueOf(lamplocInfo[3]),
								Double.valueOf(lamplocInfo[4]).floatValue(), Double.valueOf(lamplocInfo[5]).floatValue());

						String locId = cameraSec.getString("locationid");
						LocationName locName = null;
						if (locId != "----") {
							locName = locations.get(locId);
						}
						Camera camera = new Camera(created, Integer.valueOf(cameraSec.getInt("id")), loc1, loc2, loc3, locName, camKey);

						List<String> fakeBlocksLocs = cameraSec.getStringList("fakeblocks");
						for (String strLoc : fakeBlocksLocs) {
							String[] locInfo = strLoc.split(",");
							Location loc = new Location(created.getWorld(), Double.valueOf(locInfo[1]).doubleValue(), Double.valueOf(locInfo[2]).doubleValue(),
									Double.valueOf(locInfo[3]).doubleValue());
							camera.addFakeBlock(loc, loc.getBlock().getType(), Material.valueOf(locInfo[0]), WrappedBlockData.createData(loc.getBlock().getBlockData()));
						}

						List<String> fakeAirBlocksLocs = cameraSec.getStringList("fakeairblocks");
						for (String strLoc : fakeAirBlocksLocs) {
							String[] locInfo = strLoc.split(",");
							Location loc = new Location(created.getWorld(), Double.valueOf(locInfo[0]).doubleValue(), Double.valueOf(locInfo[1]).doubleValue(),
									Double.valueOf(locInfo[2]).doubleValue());
							camera.addFakeAirBlock(loc);
						}
						created.getCamerasManager().addCamera(camera);
					}
				}

				if (arenaSC.getString("vitalsloc") != null) {
					String[] vitalsLocInfo = arenaSC.getString("vitalsloc").split(",");
					created.setVitalsLoc(new Location(Bukkit.getWorld(vitalsLocInfo[0]), Double.valueOf(vitalsLocInfo[1]).doubleValue(), Double.valueOf(vitalsLocInfo[2]).doubleValue(),
							Double.valueOf(vitalsLocInfo[3]).doubleValue()));
				}

				ConfigurationSection doorGroupsTopSec = arenaSC.getConfigurationSection("doorgroups");
				if (doorGroupsTopSec != null) {
					for (String doorGroupKey : doorGroupsTopSec.getKeys(false)) {
						ConfigurationSection doorGroupSec = doorGroupsTopSec.getConfigurationSection(doorGroupKey);

						String locId = doorGroupSec.getString("locationid");
						LocationName locName = locations.get(locId);
						DoorGroup createdDoorGroup = new DoorGroup(created, locName, doorGroupKey, Integer.valueOf(doorGroupSec.getInt("id")));

						ConfigurationSection doorsSec = doorGroupSec.getConfigurationSection("doors");
						if (doorsSec != null) {
							for (String doorKey : doorsSec.getKeys(false)) {
								ConfigurationSection doorSec = doorsSec.getConfigurationSection(doorKey);

								String corner1Str = doorSec.getString("corner1", "world,0,0,0");
								String[] corner1Split = corner1Str.split(",");
								Location corner1 = new Location(Bukkit.getWorld(corner1Split[0]), Integer.parseInt(corner1Split[1]), Integer.parseInt(corner1Split[2]),
										Integer.parseInt(corner1Split[3]));

								String corner2Str = doorSec.getString("corner2", "world,0,0,0");
								String[] corner2Split = corner2Str.split(",");
								Location corner2 = new Location(Bukkit.getWorld(corner2Split[0]), Integer.parseInt(corner2Split[1]), Integer.parseInt(corner2Split[2]),
										Integer.parseInt(corner2Split[3]));

								createdDoorGroup.addDoor(new Door(created, createdDoorGroup, corner1, corner2, Integer.valueOf(doorSec.getInt("id")), doorKey));
							}
						}

						created.getDoorsManager().addDoorGroup(createdDoorGroup);
					}
				}

				List<String> primeShieldsBlocksStr = arenaSC.getStringList("primeshieldsblocks");
				created.getPrimeShieldsBlocks().clear();
				for (String locStr : primeShieldsBlocksStr) {
					String[] locStrSplit = locStr.split(",");
					Location loc = new Location(Bukkit.getWorld(locStrSplit[0]), Integer.parseInt(locStrSplit[1]), Integer.parseInt(locStrSplit[2]), Integer.parseInt(locStrSplit[3]));
					created.getPrimeShieldsBlocks().add(loc.getBlock());
				}

				if (!created.getDisableMap().booleanValue()) {
					String idsStr_ = arenaSC.getString("mapids");
					ArrayList<Short> mapIds = new ArrayList<>();
					if (idsStr_ != null && !arenaSC.getString("mapids").isEmpty()) {
						String[] idsStr = arenaSC.getString("mapids").split(",");
						byte b;
						int i;
						String[] arrayOfString;
						for (i = (arrayOfString = idsStr).length, b = 0; b < i;) {
							String id_ = arrayOfString[b];
							if (!id_.isEmpty())
								mapIds.add(Short.valueOf(Integer.valueOf(id_).shortValue()));
							b++;
						}

					}
					if (mapIds.size() < created.getMaxPlayers()) {
						int id = 1;
						while (mapIds.size() < created.getMaxPlayers()) {
							if (Bukkit.getMap((short) id) == null && !mapIds.contains(Short.valueOf((short) id))) {
								MapView view = Bukkit.createMap(created.getWorld());
								view.setScale(MapView.Scale.CLOSEST);
								view.setUnlimitedTracking(false);
								view.addRenderer((MapRenderer) new Renderer());
								mapIds.add(Short.valueOf((short) view.getId()));
							}

							id++;
						}
						StringJoiner strJoiner = new StringJoiner(",");
						for (Iterator<Short> iterator = mapIds.iterator(); iterator.hasNext();) {
							short id_ = ((Short) iterator.next()).shortValue();
							strJoiner.add(String.valueOf(id_));
						}

						Main.getPlugin().getConfig().set("arenas." + key + "." + "mapids", strJoiner.toString());
						Main.getPlugin().saveConfig();
					}
					created.setMapIds(mapIds);
				}
				created.createHolograms();
			}
			updateArenaSelectorInv();
		} catch (Exception e) {
			e.printStackTrace();
			Bukkit.getLogger().info("ERROR! Something is wrong with your config file!");
		}
	}
}
