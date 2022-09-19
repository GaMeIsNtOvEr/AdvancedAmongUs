package com.nktfh100.amongus.info;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.handler.TouchHandler;
import com.gmail.filoghost.holographicdisplays.api.line.ItemLine;
import com.gmail.filoghost.holographicdisplays.api.line.TouchableLine;
import com.nktfh100.AmongUs.api.events.AUArenaGameStateChange;
import com.nktfh100.AmongUs.api.events.AUArenaPlayerDeath;
import com.nktfh100.AmongUs.api.events.AUArenaPlayerJoin;
import com.nktfh100.AmongUs.api.events.AUArenaPlayerLeave;
import com.nktfh100.AmongUs.api.events.AUArenaStart;
import com.nktfh100.AmongUs.enums.GameState;
import com.nktfh100.AmongUs.enums.SabotageLength;
import com.nktfh100.AmongUs.enums.SabotageType;
import com.nktfh100.AmongUs.enums.StatInt;
import com.nktfh100.AmongUs.enums.TaskLength;
import com.nktfh100.AmongUs.enums.TaskType;
import com.nktfh100.AmongUs.inventory.ColorSelectorInv;
import com.nktfh100.AmongUs.inventory.MeetingBtnInv;
import com.nktfh100.AmongUs.inventory.TaskInvHolder;
import com.nktfh100.AmongUs.main.Main;
import com.nktfh100.AmongUs.main.Renderer;
import com.nktfh100.AmongUs.managers.CamerasManager;
import com.nktfh100.AmongUs.managers.DeadBodiesManager;
import com.nktfh100.AmongUs.managers.DoorsManager;
import com.nktfh100.AmongUs.managers.ItemsManager;
import com.nktfh100.AmongUs.managers.MeetingManager;
import com.nktfh100.AmongUs.managers.SabotageManager;
import com.nktfh100.AmongUs.managers.TasksManager;
import com.nktfh100.AmongUs.managers.VentsManager;
import com.nktfh100.AmongUs.managers.VisibilityManager;
import com.nktfh100.AmongUs.managers.VitalsManager;
import com.nktfh100.AmongUs.utils.Packets;
import com.nktfh100.AmongUs.utils.Utils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Lightable;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

public class Arena {
	private String name;
	private String displayName;
	private Integer minPlayers;
	private Integer maxPlayers;
	private ArrayList<Location> playersSpawns = new ArrayList<>();
	private HashMap<String, PlayerInfo> ingamePlayers = new HashMap<>();
	private ArrayList<PlayerInfo> gameImposters = new ArrayList<>();
	private ArrayList<PlayerInfo> impostersAlive = new ArrayList<>();
	private ArrayList<PlayerInfo> ghosts = new ArrayList<>();
	private HashMap<Short, Boolean> mapIds = new HashMap<>();
	private BossBar bossBar;
	private World world;
	private Location waitingLobby;
	private Location mapCenter;
	private Location meetingButton;
	private Location camerasLoc = null;
	private Location vitalsLoc = null;

	private GameState gameState = GameState.WAITING;
	private Boolean isInMeeting = Boolean.valueOf(false);
	private HashMap<String, Task> tasks = new HashMap<>();
	private ArrayList<SabotageArena> sabotages = new ArrayList<>();

	private HashMap<String, LocationName> locations = new HashMap<>();

	private ArrayList<PlayerInfo> scanQueue = new ArrayList<>();

	private Integer commonTasks = Integer.valueOf(3);
	private Integer longTasks = Integer.valueOf(2);
	private Integer shortTasks = Integer.valueOf(1);
	private Integer gameTimer = Integer.valueOf(30);
	private Integer votingTime = Integer.valueOf(30);
	private Integer discussionTime = Integer.valueOf(30);
	private Integer proceedingTime = Integer.valueOf(5);
	private Integer numImposters = Integer.valueOf(2);
	private Integer meetingsPerPlayer = Integer.valueOf(1);
	private Integer killCooldown = Integer.valueOf(30);
	private Integer meetingCooldown = Integer.valueOf(10);
	private Integer sabotageCooldown = Integer.valueOf(17);
	private Double reportDistance = Double.valueOf(3.5D);
	private Integer imposterVision = Integer.valueOf(15);
	private Integer crewmateVision = Integer.valueOf(10);

	private Integer doorCloseTime = Integer.valueOf(10);
	private Integer doorCooldown = Integer.valueOf(30);

	private Boolean enableReducedVision = Boolean.valueOf(true);
	private Boolean hideHologramsOutOfView = Boolean.valueOf(false);
	private Boolean disableSprinting = Boolean.valueOf(true);
	private Boolean disableJumping = Boolean.valueOf(true);
	private Boolean disableMap = Boolean.valueOf(false);
	private Boolean enableVisualTasks = Boolean.valueOf(true);
	private Boolean confirmEjects = Boolean.valueOf(true);
	private Boolean moveMapWithPlayer = Boolean.valueOf(false);
	private Boolean dynamicImposters = Boolean.valueOf(false);
	private Boolean enableRedstone = false;

	private BukkitTask gameTimerRunnable = null;
	private BukkitTask secondRunnable = null;

	private ArrayList<Hologram> holograms = new ArrayList<>();

	private Hologram btnHolo;
	private Boolean enableCameras = Boolean.valueOf(false);

	private ArrayList<ColorInfo> colors_ = Utils.getPlayersColors();

	private TasksManager taskManager = new TasksManager(this);
	private SabotageManager sabotageManager = new SabotageManager(this);
	private MeetingManager meetingManager = new MeetingManager(this);
	private DeadBodiesManager deadBodiesManager = new DeadBodiesManager(this);
	private VentsManager ventsManager = new VentsManager(this);
	private CamerasManager camerasManager = new CamerasManager(this);
	private VisibilityManager visibilityManager = new VisibilityManager(this);
	private DoorsManager doorsManager = new DoorsManager(this);
	private VitalsManager vitalsManager = new VitalsManager(this);

	private ArrayList<JoinSign> joinSigns = new ArrayList<>();

	private ColorSelectorInv colorSelectorInv = new ColorSelectorInv(this);

	private ArrayList<Block> primeShieldsBlocks = new ArrayList<>();

	private long asteroidsLastTime = System.currentTimeMillis();

	private Integer gameTimerActive = Integer.valueOf(30);

	private ArrayList<Player> _playersToDelete = new ArrayList<>();

	public Boolean _isTesting;

	public void giveGameInventory(PlayerInfo pInfo) {
		pInfo.getPlayer().getInventory().clear();
		if (!pInfo.getIsInVent().booleanValue() && !pInfo.getIsInCameras().booleanValue()) {
			pInfo.giveArmor();
		}
		ItemsManager itemsManager = Main.getItemsManager();
		if (!this.disableMap.booleanValue()) {

			if (pInfo.getMapId() == -1) {
				Short id = Short.valueOf((short) 0);
				for (Short id_ : this.mapIds.keySet()) {
					if (!((Boolean) this.mapIds.get(id_)).booleanValue()) {
						id = id_;
						break;
					}
				}
				this.mapIds.put(Short.valueOf(id.shortValue()), Boolean.valueOf(true));
				pInfo.setMapId(id.shortValue());
			}
			if (pInfo.getMapId() != -1) {
				giveGameMap(pInfo, itemsManager.getItem("map").getItem().getSlot());
			}
		}
		if (pInfo.getIsImposter().booleanValue()) {
			if (pInfo.getIsInVent().booleanValue()) {
				if (pInfo.getVent().getId() > 0 || (pInfo.getVentGroup().getLoop().booleanValue() && pInfo.getVent().getId() == 0)) {
					ItemInfo ventLeft = itemsManager.getItem("vent_left").getItem();
					pInfo.getPlayer().getInventory().setItem(ventLeft.getSlot(), ventLeft.getItem());
				}
				ItemInfo leaveVent = itemsManager.getItem("vent_leave").getItem();
				pInfo.getPlayer().getInventory().setItem(leaveVent.getSlot(), leaveVent.getItem());
				if (pInfo.getVent().getId() < pInfo.getVentGroup().getVents().size() - 1
						|| (pInfo.getVentGroup().getLoop().booleanValue() && pInfo.getVent().getId() == pInfo.getVentGroup().getVents().size() - 1)) {
					ItemInfo ventRight = itemsManager.getItem("vent_right").getItem();
					pInfo.getPlayer().getInventory().setItem(ventRight.getSlot(), ventRight.getItem());
				}
			} else if (!pInfo.getIsInCameras().booleanValue()) {
				for (SabotageArena sa : getSabotages()) {
					String name = Main.getMessagesManager().getTaskName(sa.getType().toString());
					ItemInfoContainer saboItemInfo = getSabotageManager().getSabotageItemInfo(sa.getType());
					ItemStack saboItem = getSabotageManager().getSabotageItem(sa.getType(), name, getSabotageManager().getSabotageCoolDownTimer(pInfo.getPlayer()));
					pInfo.getPlayer().getInventory().setItem(saboItemInfo.getSlot(), saboItem);
				}
				int s_ = 9;
				for (DoorGroup dg : getDoorsManager().getDoorGroups()) {
					pInfo.getPlayer().getInventory().setItem(s_, getDoorsManager().getSabotageDoorItem(pInfo.getPlayer(), dg.getId()));
					s_++;
				}
				if (!pInfo.isGhost().booleanValue()) {
					pInfo.giveKillItem(pInfo.getKillCoolDown());
				}
			}
		}
		if (pInfo.getIsInCameras().booleanValue()) {
			ItemInfo camerasLeft = itemsManager.getItem("cameras_left").getItem();
			ItemInfo camerasLeave = itemsManager.getItem("cameras_leave").getItem();
			ItemInfo camerasRight = itemsManager.getItem("cameras_right").getItem();

			pInfo.getPlayer().getInventory().setItem(camerasLeft.getSlot(), camerasLeft.getItem());
			pInfo.getPlayer().getInventory().setItem(camerasLeave.getSlot(), camerasLeave.getItem());
			pInfo.getPlayer().getInventory().setItem(camerasRight.getSlot(), camerasRight.getItem());
		}
		if (!pInfo.isGhost().booleanValue() && !pInfo.getIsInVent().booleanValue() && !pInfo.getIsInCameras().booleanValue()) {
			ItemInfo reportItem = itemsManager.getItem("report").getItem();
			pInfo.getPlayer().getInventory().setItem(reportItem.getSlot(), reportItem.getItem());
		}
		if (!getIsInMeeting().booleanValue() && !pInfo.getIsInCameras().booleanValue() && !pInfo.getIsInVent().booleanValue()) {
			pInfo.setUseItemState(Integer.valueOf(0), Boolean.valueOf(true));
			pInfo.updateUseItemState(pInfo.getPlayer().getLocation());
		}
	}

	public void startSecondRunnable() {
		if (this.secondRunnable != null) {
			this.secondRunnable.cancel();
		}

		Boolean arenaHasInspectSample1 = Boolean.valueOf(false);
		for (Task task : getAllTasks()) {
			if (task.getTaskType() == TaskType.INSPECT_SAMPLE || task.getTaskType() == TaskType.REBOOT_WIFI) {
				arenaHasInspectSample1 = Boolean.valueOf(true);
				break;
			}
			if (arenaHasInspectSample1.booleanValue()) {
				break;
			}
			for (QueuedTasksVariant qtv : task.getQueuedTasksVariants()) {
				for (Task t1 : qtv.getQueuedTasksTasks()) {
					if (t1.getTaskType() == TaskType.INSPECT_SAMPLE || t1.getTaskType() == TaskType.REBOOT_WIFI) {
						arenaHasInspectSample1 = Boolean.valueOf(true);

						break;
					}
				}
			}
		}
		final Boolean arenaHasInspectSample = arenaHasInspectSample1;
		final Arena arena = this;
		this.secondRunnable = (new BukkitRunnable() {
			Boolean sendDamageAnim = Boolean.valueOf(true);

			public void run() {
				if (arena.getGameState() == GameState.RUNNING) {
					if (arena.getMeetingManager().getMeetingCooldownTimer() > 0) {
						arena.getMeetingManager().setMeetingCooldownTimer(Integer.valueOf(arena.getMeetingManager().getMeetingCooldownTimer() - 1));

						for (Player player : arena.getPlayers()) {
							if (player.getOpenInventory().getTopInventory().getHolder() instanceof MeetingBtnInv) {
								((MeetingBtnInv) player.getOpenInventory().getTopInventory().getHolder()).update();
							}
						}
					}

					if (arena.getIsInMeeting().booleanValue() && !arena.getMeetingManager().getIsSendingTitle().booleanValue()) {
						Integer timer = arena.getMeetingManager().getActiveTimer();

						String msgKey = "votingBeginsIn";
						if (arena.getMeetingManager().getState() == MeetingManager.meetingState.VOTING) {
							msgKey = "votingEndsIn";
						} else if (arena.getMeetingManager().getState() == MeetingManager.meetingState.VOTING_RESULTS) {
							msgKey = "proceedingIn";
						}

						String msg = Main.getMessagesManager().getGameMsg(String.valueOf(msgKey) + "Msg", arena, "" + timer);
						String actionBar = Main.getMessagesManager().getGameMsg(String.valueOf(msgKey) + "ActionBar", arena, "" + timer);
						if (!actionBar.isEmpty()) {
							for (Player p : arena.getPlayers()) {
								Utils.sendActionBar(p, actionBar);
							}
						}
						if (!msg.isEmpty() && ((timer > 0 && timer <= 5) || timer == 10)) {
							arena.sendMessage(msg);

						}
					} else if (!arena.getIsInMeeting().booleanValue()) {
						if (!arena.getSabotageManager().getIsSabotageActive().booleanValue()) {
							for (PlayerInfo pInfo : arena.getGameImposters()) {
								Integer saboCooldown = arena.getSabotageManager().getSabotageCoolDownTimer(pInfo.getPlayer());
								if (saboCooldown > 0) {
									arena.getSabotageManager().setSabotageCoolDownTimer(pInfo.getPlayer().getUniqueId().toString(), Integer.valueOf(saboCooldown - 1));
								}
								int s_ = 9;
								String uuid = pInfo.getPlayer().getUniqueId().toString();
								for (DoorGroup dg : arena.getDoorsManager().getDoorGroups()) {
									Integer doorCooldown = dg.getCooldownTimer(uuid);
									if (doorCooldown > 0) {
										dg.setCooldownTimer(uuid, Integer.valueOf(doorCooldown - 1));
									}
									ItemStack item = arena.getDoorsManager().getSabotageDoorItem(pInfo.getPlayer(), dg.getId());
									pInfo.getPlayer().getInventory().setItem(s_, item);
									s_++;
								}
							}
						} else {
							if (Main.getConfigManager().getDamageOnSabotage().booleanValue()) {
								if (this.sendDamageAnim.booleanValue()) {
									for (PlayerInfo pInfo : arena.getPlayersInfo()) {
										PacketContainer damagePacket = new PacketContainer(PacketType.Play.Server.ANIMATION);
										damagePacket.getIntegers().write(1, Integer.valueOf(1));
										damagePacket.getIntegers().write(0, Integer.valueOf(pInfo.getPlayer().getEntityId()));
										Packets.sendPacket(pInfo.getPlayer(), damagePacket);
										Packets.sendPacket(pInfo.getPlayer(), Packets.NAMED_SOUND(pInfo.getPlayer().getLocation(), Sound.ENTITY_PLAYER_HURT));
									}
									this.sendDamageAnim = Boolean.valueOf(false);
								} else {
									this.sendDamageAnim = Boolean.valueOf(true);
								}
							}

							if (arena.getSabotageManager().getActiveSabotage().getType() == SabotageType.REACTOR_MELTDOWN
									|| arena.getSabotageManager().getActiveSabotage().getType() == SabotageType.OXYGEN) {
								for (PlayerInfo pInfo : arena.getPlayersInfo()) {
									Main.getSoundsManager().playSound("sabotageAlarm", pInfo.getPlayer(), pInfo.getPlayer().getLocation());
								}
							}
						}

						if (Main.getConfigManager().getParticlesOnTasks().booleanValue()) {
							TasksManager tasksManager = arena.getTasksManager();
							for (PlayerInfo pInfo : arena.getPlayersInfo()) {
								if (pInfo == null || pInfo.getIsImposter() == null) {
									continue;
								}
								if (!pInfo.getIsImposter().booleanValue()) {
									for (TaskPlayer tp : tasksManager.getTasksForPlayer(pInfo.getPlayer())) {
										if (!tp.getIsDone().booleanValue() && tp.getActiveTask().getHolo().getVisibilityManager().isVisibleTo(pInfo.getPlayer())
												&& (!arena.getEnableReducedVision().booleanValue()
														|| Utils.isInsideCircle(pInfo.getPlayer().getLocation(), Double.valueOf(pInfo.getVision()), tp.getActiveTask().getLocation()) != 2)) {
											Packets.sendPacket(pInfo.getPlayer(), Packets.PARTICLES(tp.getActiveTask().getHolo().getLocation().add(0.0D, -0.3D, 0.0D),
													Main.getConfigManager().getParticlesOnTasksType(), null, Integer.valueOf(8), 0.4F, 0.3F, 0.4F));
										}
									}
								}
							}
						}

						if (arenaHasInspectSample.booleanValue()) {
							for (PlayerInfo pInfo : arena.getPlayersInfo()) {

								if (!pInfo.getIsImposter().booleanValue()) {
									for (TaskPlayer tp : arena.getTasksManager().getTasksForPlayer(pInfo.getPlayer())) {
										if (tp.getActiveTask().getTaskType() == TaskType.INSPECT_SAMPLE) {
											if (tp.getInspectIsRunning_().booleanValue() && tp.getInspectTimer_() > 0)
												tp.setInspectTimer_(Integer.valueOf(tp.getInspectTimer_() - 1));
											continue;
										}
										if (tp.getActiveTask().getTaskType() == TaskType.REBOOT_WIFI && tp.getRebootIsRunning_().booleanValue() && tp.getRebootTimer_() > 0) {
											tp.setRebootTimer_(Integer.valueOf(tp.getRebootTimer_() - 1));
										}
									}
								}
							}
						}

						for (DoorGroup dg : arena.getDoorsManager().getDoorGroups()) {
							Integer doorCloseTimer = dg.getCloseTimer();
							if (doorCloseTimer > 0) {
								dg.setCloseTimer(Integer.valueOf(doorCloseTimer - 1));
								if (doorCloseTimer - 1 <= 0) {
									dg.openDoors(Boolean.valueOf(true));
								}
							}
						}

						String imposters_ = "";
						for (PlayerInfo pInfo : arena.getGameImposters()) {
							imposters_ = String.valueOf(imposters_) + pInfo.getColor().getChatColor() + ChatColor.BOLD + pInfo.getPlayer().getName() + " ";
						}
						String impostersActionBar = Main.getMessagesManager().getGameMsg("impostersActionBar", arena, imposters_);

						for (PlayerInfo pInfo : arena.getGameImposters()) {
							if (pInfo.getIsInVent().booleanValue()) {
								Utils.sendActionBar(pInfo.getPlayer(), arena.getVentsManager().getVentActionBar(pInfo.getVent()));
							} else if (!pInfo.getIsInCameras().booleanValue()) {
								Utils.sendActionBar(pInfo.getPlayer(), impostersActionBar);
							}

							if (!pInfo.isGhost().booleanValue() && !pInfo.getIsInVent().booleanValue() && !pInfo.getIsInCameras().booleanValue() && !pInfo.getKillCoolDownPaused().booleanValue()
									&& pInfo.getKillCoolDown() > 0) {
								pInfo.setKillCoolDown(Integer.valueOf(pInfo.getKillCoolDown() - 1));
								if (pInfo.getKillCoolDown() == 0) {
									pInfo.giveKillItem(pInfo.getKillCoolDown());
								}
							}
						}

						for (PlayerInfo pInfo : arena.getPlayersInfo()) {
							if (pInfo.getIsInCameras().booleanValue()) {
								Utils.sendActionBar(pInfo.getPlayer(), arena.getCamerasManager().getCameraActionBar(pInfo.getActiveCamera()));
							}
						}
					}
				} else if (arena.getGameState() != GameState.FINISHING) {
					Integer players_ = Integer.valueOf(arena.getPlayersInfo().size());
					if (players_ > 0) {
						if (Main.getConfigManager().getEnableDoubleImposterChance().booleanValue()) {
							for (Player player : arena.getPlayers()) {
								if (player.hasPermission("amongus.perk.double-imposter-chance")) {
									players_ = Integer.valueOf(players_ + 1);
								}
							}
							Double imposterChance = Double.valueOf(1.0D / players_ * 100.0D);
							Double imposterChance1 = Double.valueOf(2.0D / players_ * 100.0D);
							if (imposterChance.doubleValue() > 100.0D) {
								imposterChance = Double.valueOf(100.0D);
							} else if (imposterChance.doubleValue() < 0.0D) {
								imposterChance = Double.valueOf(0.0D);
							}
							if (imposterChance1.doubleValue() > 100.0D) {
								imposterChance1 = Double.valueOf(100.0D);
							} else if (imposterChance1.doubleValue() < 0.0D) {
								imposterChance1 = Double.valueOf(0.0D);
							}
							String msg = Main.getMessagesManager().getGameMsg("lobbyActionBar", arena, "" + players_, "" + arena.getMaxPlayers(),
									(new StringBuilder(String.valueOf(imposterChance))).toString(), null);
							String msg1 = Main.getMessagesManager().getGameMsg("lobbyActionBar", arena, "" + players_, "" + arena.getMaxPlayers(),
									(new StringBuilder(String.valueOf(imposterChance1))).toString(), null);

							for (Player player : arena.getPlayers()) {
								if (player.hasPermission("amongus.perk.double-imposter-chance")) {
									Utils.sendActionBar(player, msg1);
									continue;
								}
								Utils.sendActionBar(player, msg);
							}
						} else {

							Double imposterChance = Double.valueOf(arena.getNumImposters() / players_ * 100.0D);
							if (imposterChance.doubleValue() > 100.0D) {
								imposterChance = Double.valueOf(100.0D);
							} else if (imposterChance.doubleValue() < 0.0D) {
								imposterChance = Double.valueOf(0.0D);
							}
							Integer playersCount = 0;
							ArrayList<Player> players__ = arena.getPlayers();
							for (Player p : players__) {
								if (p.isOnline() && arena.isPlayerInArena(p)) {
									playersCount++;
								}
							}
							String msg = Main.getMessagesManager().getGameMsg("lobbyActionBar", arena, playersCount + "", "" + arena.getMaxPlayers(),
									(new StringBuilder(String.valueOf(imposterChance))).toString(), null);
							for (PlayerInfo pInfo : arena.getPlayersInfo()) {
								Utils.sendActionBar(pInfo.getPlayer(), msg);
							}
						}
					}
				}
				if ((arena.getGameState() == GameState.RUNNING || arena.getGameState() == GameState.FINISHING) && arena.getPlayersInfo() != null && !arena.getPlayersInfo().isEmpty()) {
					for (PlayerInfo pInfo : arena.getPlayersInfo()) {
						if (pInfo != null && pInfo.getStatsManager() != null) {
							pInfo.getStatsManager().plusOneStatInt(StatInt.TIME_PLAYED);
						}
					}

				}
			}
		}).runTaskTimer(Main.getPlugin(), 20L, 20L);
	}

	public void stopSecondRunnable() {
		if (this.secondRunnable != null) {
			this.secondRunnable.cancel();
			this.secondRunnable = null;
		}
	}

	public void updatePlayerColor(PlayerInfo pInfo, ColorInfo color) {
		if (this.colors_.contains(color)) {
			this.colors_.add(pInfo.getColor());
			this.colors_.removeIf(n -> (n == color));
			Collections.sort(this.colors_);
			pInfo.setColor(color);
			pInfo.giveArmor();
			ItemInfo colorSelectorItem = Main.getItemsManager().getItem("colorSelector").getItem();
			pInfo.getPlayer().getInventory().setItem(colorSelectorItem.getSlot(), Utils.createItem(pInfo.getColor().getWool(), colorSelectorItem.getTitle(), 1, colorSelectorItem.getLore()));
			PacketContainer packet = Packets.UPDATE_DISPLAY_NAME(pInfo.getPlayer().getUniqueId(), pInfo.getPlayer().getName(), pInfo.getCustomName());
			for (Player player : getPlayers()) {
				Packets.sendPacket(player, packet);
			}
			pInfo.updateScoreBoard();
		}
	}

	public void giveGameMap(PlayerInfo pInfo, int slot) {
		if (!this.disableMap.booleanValue()) {
			ItemInfoContainer mapInfo = Main.getItemsManager().getItem("map");
			short id = pInfo.getMapId();
			ItemStack mapItem = Utils.createItem(Material.FILLED_MAP, mapInfo.getItem().getTitle(), 1, mapInfo.getItem().getLore());
			MapMeta meta = (MapMeta) mapItem.getItemMeta();
			meta.setMapView(Bukkit.getMap(id));
			if (!meta.hasMapView()) {
				return;
			}
			meta.getMapView().setScale(MapView.Scale.CLOSEST);
			meta.getMapView().setWorld(getWorld());
			int i__ = 1;
			for (MapRenderer r : meta.getMapView().getRenderers()) {
				if (i__ == 2) {
					meta.getMapView().removeRenderer(r);
				}
				i__++;
			}
			meta.getMapView().setUnlimitedTracking(false);
			meta.getMapView().addRenderer((MapRenderer) new Renderer());
			mapItem.setItemMeta((ItemMeta) meta);
			if (pInfo.getIsMapInOffHand().booleanValue()) {
				pInfo.getPlayer().getInventory().setItemInOffHand(mapItem);
				pInfo.getPlayer().getInventory().setItem(slot, Utils.createItem(mapInfo.getItem2().getMat(), mapInfo.getItem2().getTitle(), 1, mapInfo.getItem2().getLore()));
			} else {
				pInfo.getPlayer().getInventory().setItem(slot, mapItem);
			}
		}
	}

	public ArrayList<String> getTasksLength(TaskLength l) {
		ArrayList<String> out = new ArrayList<>();
		for (Task task : this.tasks.values()) {
			if (task.getTaskType().getTaskLength() == l) {
				out.add(task.getId());
			}
		}
		return out;
	}

	public void addSign(Location loc) {
		this.joinSigns.add(new JoinSign(this, loc));
	}

	public void setMapIds(ArrayList<Short> ids) {
		for (Short i : ids) {
			this.mapIds.put(i, Boolean.valueOf(false));
		}
	}

	public void addPlayerSpawn(Location loc) {
		this.playersSpawns.add(loc);
		if (loc.getWorld() == null || loc == null) {
			return;
		}
		if (loc.getWorld().getName() != this.world.getName()) {
			this.world = loc.getWorld();
			Bukkit.getLogger().info("Your config file for arena " + getName() + " is wrong!");
			Bukkit.getLogger().info("You should change world to: " + loc.getWorld().getName());
		}
	}

	public void removePlayerSpawn(int index) {
		this.playersSpawns.remove(index);
	}

	public ArrayList<Player> getPlayers() {
		ArrayList<Player> players = new ArrayList<>();
		for (PlayerInfo info : this.ingamePlayers.values()) {
			if (info != null) {
				players.add(info.getPlayer());
			}
		}
		return players;
	}

	public Boolean isPlayerInArena(Player player) {
		if (this.ingamePlayers.get(player.getUniqueId().toString()) != null) {
			return Boolean.valueOf(true);
		}
		return Boolean.valueOf(false);
	}

	public void sendMessage(String message) {
		byte b;
		int i;
		String[] arrayOfString;
		for (i = (arrayOfString = message.split("/n")).length, b = 0; b < i;) {
			String str = arrayOfString[b];
			for (Player player : getPlayers()) {
				player.sendMessage(str);
			}
			b++;
		}

	}

	public void playerJoin(final Player player) {
		if (player == null || this.maxPlayers == null || this.ingamePlayers == null) {
			return;
		}
		if (!isPlayerInArena(player).booleanValue() && getPlayers().size() < this.maxPlayers) {
			if (this.gameState == GameState.STARTING || this.gameState == GameState.WAITING) {
				final PlayerInfo pInfo = Main.getPlayersManager().getPlayerInfo(player);

				AUArenaPlayerJoin ev = new AUArenaPlayerJoin(this, player);
				Bukkit.getPluginManager().callEvent((Event) ev);

				if (ev.isCancelled()) {
					return;
				}

				if (this.playersSpawns.size() == 0) {
					Bukkit.getLogger().log(Level.SEVERE, "Arena " + getDisplayName() + " has no spawns!");

					return;
				}
				if (this.colors_.size() == 0) {
					Bukkit.getLogger().log(Level.SEVERE, "There are not enough colors!");
					Bukkit.getLogger().log(Level.SEVERE,
							"Number of colors: " + Main.getConfigManager().getAllColors().size() + ", Number of players in '" + getDisplayName() + "': " + getMaxPlayers());
					return;
				}
				if (pInfo.getPreferredColor() != null) {
					if (this.colors_.contains(pInfo.getPreferredColor())) {
						pInfo.setColor(pInfo.getPreferredColor());
						this.colors_.remove(pInfo.getPreferredColor());
					} else {
						pInfo.setColor(this.colors_.get(0));
						this.colors_.remove(0);
					}
				} else {
					pInfo.setColor(this.colors_.get(0));
					pInfo.setPreferredColor(this.colors_.get(0));
					this.colors_.remove(0);
				}

				this.ingamePlayers.put(player.getUniqueId().toString(), pInfo);
				Collections.sort(this.colors_);

				pInfo.setMeetingsLeft(this.meetingsPerPlayer);
				pInfo.initGame(this, Integer.valueOf(this.ingamePlayers.keySet().size() - 1));

				player.teleport(this.waitingLobby);
				player.setHealth(player.getMaxHealth());
				player.setGameMode(GameMode.ADVENTURE);
				player.getInventory().clear();
				player.setAllowFlight(false);
				player.setExp(0.0F);
				player.setLevel(0);
				player.setFoodLevel(20);

				for (PotionEffect pe : player.getActivePotionEffects()) {
					player.removePotionEffect(pe.getType());
				}

				pInfo.giveArmor();

				ItemInfo leaveItem = Main.getItemsManager().getItem("leave").getItem();
				player.getInventory().setItem(leaveItem.getSlot(), leaveItem.getItem());
				ItemInfo colorSelectorItem = Main.getItemsManager().getItem("colorSelector").getItem();
				player.getInventory().setItem(colorSelectorItem.getSlot(), Utils.createItem(pInfo.getColor().getWool(), colorSelectorItem.getTitle(), 1, colorSelectorItem.getLore()));

				if (this.ingamePlayers.size() >= this.minPlayers && this.gameState != GameState.STARTING) {
					startGameTimer();
				}

				sendMessage(Main.getMessagesManager().getGameMsg("playerJoin", this, player.getName(), "" + pInfo.getColor().getChatColor(), pInfo.getColor().getName(),
						(new StringBuilder(String.valueOf(this.ingamePlayers.size()))).toString(), "" + this.maxPlayers));
				updateScoreBoard();
				updateSigns();

				updatePlayersJoinedID();
				Main.getArenaManager().updateArenaSelectorInv();
				if (this.secondRunnable == null || this.secondRunnable.isCancelled()) {
					startSecondRunnable();
				}

				if (!Main.getConfigManager().getBungeecord().booleanValue() && Main.getConfigManager().getHidePlayersOutSideArena().booleanValue()) {
					PacketContainer packet1 = Packets.REMOVE_PLAYER(pInfo.getPlayer().getUniqueId(), pInfo.getPlayer().getName(), pInfo.getPlayer().getName());
					PacketContainer packet2 = Packets.ADD_PLAYER(pInfo.getPlayer().getUniqueId(), pInfo.getPlayer().getName(), pInfo.getCustomName(), pInfo.getTextureValue(),
							pInfo.getTextureSignature());
					for (PlayerInfo pInfo_ : Main.getPlayersManager().getPlayers()) {
						if (pInfo != pInfo_) {
							if (!pInfo_.getIsIngame().booleanValue()) {
								PacketContainer packet = Packets.REMOVE_PLAYER(pInfo_.getPlayer().getUniqueId(), pInfo_.getPlayer().getName(), pInfo_.getPlayer().getName());
								Packets.sendPacket(player, packet);

								Packets.sendPacket(pInfo_.getPlayer(), packet1);
								continue;
							}
							if (pInfo_.getArena() == this) {
								PacketContainer packet = Packets.ADD_PLAYER(pInfo_.getPlayer().getUniqueId(), pInfo_.getPlayer().getName(), pInfo_.getCustomName(), pInfo_.getTextureValue(),
										pInfo_.getTextureSignature());
								Packets.sendPacket(player, packet);
							}
						}
					}
					for (PlayerInfo pInfo_ : getPlayersInfo()) {
						if (pInfo_ != pInfo) {
							Packets.sendPacket(pInfo_.getPlayer(), packet2);
						}
					}
				}

				for (PlayerInfo pInfo1 : getPlayersInfo()) {
					if (pInfo != pInfo1) {
						pInfo1.updateScoreBoard();
						getVisibilityManager().showPlayer(pInfo, pInfo1, Boolean.valueOf(true));
						getVisibilityManager().showPlayer(pInfo1, pInfo, Boolean.valueOf(true));
					}
				}

				if (Main.getConfigManager().getBungeecord().booleanValue() || !Main.getConfigManager().getHidePlayersOutSideArena().booleanValue()) {
					final Arena arena = this;
					(new BukkitRunnable() {
						public void run() {
							if (arena == null || pInfo == null || !pInfo.getPlayer().isOnline() || !pInfo.getIsIngame().booleanValue()) {
								return;
							}
							PacketContainer packet_ = Packets.UPDATE_DISPLAY_NAME(pInfo.getPlayer().getUniqueId(), pInfo.getPlayer().getName(), pInfo.getCustomName());
							Packets.sendPacket(player, packet_);
							for (PlayerInfo pInfo1 : arena.getPlayersInfo()) {
								if (pInfo1 == null || !pInfo1.getPlayer().isOnline() || !pInfo1.getIsIngame().booleanValue()) {
									continue;
								}
								if (pInfo1 != pInfo) {
									PacketContainer packet = Packets.UPDATE_DISPLAY_NAME(pInfo1.getPlayer().getUniqueId(), pInfo1.getPlayer().getName(), pInfo1.getCustomName());
									Packets.sendPacket(pInfo.getPlayer(), packet);
									Packets.sendPacket(pInfo1.getPlayer(), packet_);
								}
							}
						}
					}).runTaskLater(Main.getPlugin(), 15L);
				}
				this.colorSelectorInv.update();

				if (this.dynamicImposters) {
					if (this.getPlayersInfo().size() <= 7) {
						this.numImposters = 1;
					} else if (this.getPlayersInfo().size() <= 10) {
						this.numImposters = 2;
					} else {
						this.numImposters = 3;
					}
				}
			} else {
				byte b;
				int i;
				String[] arrayOfString;
				for (i = (arrayOfString = Main.getMessagesManager().getGameMsg("arenaInGame", null, getDisplayName()).split("/n")).length, b = 0; b < i;) {
					String str = arrayOfString[b];
					player.sendMessage(str);
					b++;
				}

			}
		} else if (getPlayers().size() >= this.maxPlayers) {
			byte b;
			int i;
			String[] arrayOfString;
			for (i = (arrayOfString = Main.getMessagesManager().getGameMsg("arenaFull", null, getDisplayName()).split("/n")).length, b = 0; b < i;) {
				String str = arrayOfString[b];
				player.sendMessage(str);
				b++;
			}

		}
	}

	public void playerLeave(Player player, Boolean endGame, Boolean isLeaving, Boolean shouldSendToLobby) {
		if (isPlayerInArena(player).booleanValue()) {
			AUArenaPlayerLeave ev = new AUArenaPlayerLeave(this, player);
			Bukkit.getPluginManager().callEvent((Event) ev);

			PlayerInfo pInfo = Main.getPlayersManager().getPlayerInfo(player);
			sendMessage(Main.getMessagesManager().getGameMsg("playerLeave", this, player.getName(), "" + pInfo.getColor().getChatColor(), pInfo.getColor().getName(),
					(new StringBuilder(String.valueOf(this.ingamePlayers.size() - 1))).toString(), "" + this.maxPlayers));

			if (!endGame.booleanValue() && this.gameState == GameState.RUNNING && this.vitalsManager != null) {
				VitalsPlayerInfo vpi = this.vitalsManager.getVitalsPInfo(player);
				if (vpi != null) {
					if (!pInfo.isGhost().booleanValue()) {
						vpi.setIsDC(Boolean.valueOf(true));
						vpi.setIsDead(Boolean.valueOf(true));
					}
					this.vitalsManager.updateInventory();
				}
			}

			if (pInfo.getIsInCameras().booleanValue()) {
				getCamerasManager().playerLeaveCameras(pInfo, Boolean.valueOf(true));
			}
			if (pInfo.getIsInVent().booleanValue()) {
				getVentsManager().playerLeaveVent(pInfo, Boolean.valueOf(true), Boolean.valueOf(false));
			}
			getVisibilityManager().resetPlayersHidden(pInfo);

			if (!this.disableMap.booleanValue() && pInfo.getMapId() != -1) {
				this.mapIds.put(Short.valueOf(pInfo.getMapId()), Boolean.valueOf(false));
			}

			if (!endGame.booleanValue()) {
				pInfo.getStatsManager().saveStats(Boolean.valueOf(true));
			}

			if (pInfo.getIsImposter().booleanValue()) {
				this.sabotageManager.removeImposter(player.getUniqueId().toString());
			}

			this.ingamePlayers.remove(player.getUniqueId().toString());
			updatePlayersJoinedID();
			this.bossBar.removePlayer(player);
			this.sabotageManager.removePlayerFromBossBar(player);
			player.setHealth(player.getMaxHealth());
			if (!Main.getConfigManager().getSaveInventory().booleanValue()) {
				player.setGameMode(GameMode.ADVENTURE);
				player.setExp(0.0F);
			}
			player.getInventory().clear();
			player.setLevel(0);
			player.setFoodLevel(20);
			player.setFlySpeed(0.2F);
			player.setWalkSpeed(0.2F);
			player.setAllowFlight(false);

			this.colors_.add(pInfo.getColor());
			Collections.sort(this.colors_);

			if (this.gameState == GameState.WAITING || this.gameState == GameState.STARTING) {
				this.colorSelectorInv.update();
			}

			if (pInfo.isGhost().booleanValue()) {
				this.ghosts.remove(pInfo);
			}
			if (pInfo.getIsImposter().booleanValue()) {
				this.impostersAlive.remove(pInfo);
				this.gameImposters.remove(pInfo);
				if (this.sabotageManager.getSabotageCooldownBossBar(player) != null) {
					this.sabotageManager.getSabotageCooldownBossBar(player).removePlayer(player);
				}
				this.sabotageManager.removeImposter(player.getUniqueId().toString());
				pInfo.getKillCooldownBossBar().removePlayer(pInfo.getPlayer());
			}
			pInfo.leaveGame();
			for (PotionEffect effect : player.getActivePotionEffects()) {
				player.removePotionEffect(effect.getType());
			}
			if (!Main.getConfigManager().getBungeecord().booleanValue() && Main.getConfigManager().getMainLobby() != null
					&& (Main.getConfigManager().getGameEndSendToLobby().booleanValue() || shouldSendToLobby.booleanValue())) {
				player.teleport(Main.getConfigManager().getMainLobby());
			}

			pInfo.removeVisionBlocks();
			updateScoreBoard();

			PacketContainer tabNamePacket = Packets.ADD_PLAYER(pInfo.getPlayer().getUniqueId(), player.getName(), pInfo.getOriginalPlayerListName(), pInfo.getTextureValue(),
					pInfo.getTextureSignature());
			for (PlayerInfo pInfo1 : getPlayersInfo()) {
				if (pInfo.getPlayer() != null) {
					if (pInfo1.getFakePlayer() != null) {
						pInfo1.getFakePlayer().hidePlayerFrom(pInfo.getPlayer(), Boolean.valueOf(true));
					}
					if (pInfo.getFakePlayer() != null) {
						pInfo.getFakePlayer().hidePlayerFrom(pInfo1.getPlayer(), Boolean.valueOf(true));
					}
					Packets.sendPacket(pInfo1.getPlayer(), tabNamePacket);
					if (pInfo != pInfo1) {
						Packets.sendPacket(player, Packets.ADD_PLAYER(pInfo1.getPlayer().getUniqueId(), pInfo1.getPlayer().getName(), pInfo1.getOriginalPlayerListName(), pInfo1.getTextureValue(),
								pInfo1.getTextureSignature()));
					}
					if (!endGame.booleanValue()) {
						pInfo1.updateScoreBoard();
					}
				}
			}
			Packets.sendPacket(player, Packets.UPDATE_DISPLAY_NAME(player.getUniqueId(), player.getName(), pInfo.getOriginalPlayerListName()));

			getTasksManager().removeTasksForPlayer(player);
			if (!endGame.booleanValue()) {
				getTasksManager().updateTasksDoneBar(Boolean.valueOf(true));
			}

			if (this._isTesting.booleanValue() && player.getName().equals("nktfh100")) {
				this._isTesting = Boolean.valueOf(false);
			}

			updateSigns();
			if (!Main.getConfigManager().getSaveInventory().booleanValue() && Main.getConfigManager().getGiveLobbyItems().booleanValue() && !Main.getConfigManager().getBungeecord().booleanValue()) {
				ItemInfo item = Main.getItemsManager().getItem("arenasSelector").getItem();
				pInfo.getPlayer().getInventory().setItem(Main.getConfigManager().getLobbyItemSlot("arenasSelector"), item.getItem());
				if (Main.getIsPlayerPoints().booleanValue()) {
					player.getInventory().setItem(Main.getConfigManager().getLobbyItemSlot("cosmeticsSelector"), Main.getItemsManager().getItem("cosmeticsSelector").getItem().getItem());
				}
			}

			Main.getArenaManager().updateArenaSelectorInv();
			if (this.isInMeeting.booleanValue()) {
				this.meetingManager.updateInv();
			}
			if (getPlayers().size() == 0) {
				stopSecondRunnable();
			}

			PacketContainer packet1 = Packets.ADD_PLAYER(player.getUniqueId(), player.getName(), player.getName(), pInfo.getTextureValue(), pInfo.getTextureSignature());
			PacketContainer packet2 = Packets.REMOVE_PLAYER(player.getUniqueId(), player.getName(), player.getName());

			if (!isLeaving.booleanValue() && Main.getConfigManager().getBungeecord().booleanValue()
					&& (Main.getConfigManager().getGameEndSendToLobby().booleanValue() || shouldSendToLobby.booleanValue())) {
				Main.sendPlayerToLobby(player);
			}

			if (!Main.getConfigManager().getBungeecord().booleanValue() && Main.getConfigManager().getHidePlayersOutSideArena().booleanValue()) {
				for (PlayerInfo pInfo_ : Main.getPlayersManager().getPlayers()) {
					if (!pInfo_.getIsIngame().booleanValue()) {
						PacketContainer packet = Packets.ADD_PLAYER(pInfo_.getPlayer().getUniqueId(), pInfo_.getPlayer().getName(), pInfo_.getCustomName(), pInfo_.getTextureValue(),
								pInfo_.getTextureSignature());
						Packets.sendPacket(player, packet);

						Packets.sendPacket(pInfo_.getPlayer(), packet1);
					}
				}

				for (PlayerInfo pInfo_ : getPlayersInfo()) {
					PacketContainer packet = Packets.REMOVE_PLAYER(pInfo_.getPlayer().getUniqueId(), pInfo_.getPlayer().getName(), pInfo_.getCustomName());
					Packets.sendPacket(player, packet);

					Packets.sendPacket(pInfo_.getPlayer(), packet2);
				}
			}

			if (this.isInMeeting.booleanValue()) {
				getMeetingManager().didEveryoneVote();
			}

			if ((getGameState() == GameState.WAITING || getGameState() == GameState.WAITING) && this.dynamicImposters.booleanValue()) {
				if (getPlayersInfo().size() <= 7) {
					this.numImposters = Integer.valueOf(1);
				} else {
					this.numImposters = Integer.valueOf(2);
				}
			}

			if (this.gameState == GameState.RUNNING) {
				if (!endGame.booleanValue()) {
					getWinState(Boolean.valueOf(true));
				}
			} else if (this.gameState == GameState.STARTING) {

				Boolean stopTimer = Boolean.valueOf(false);
				if (this.ingamePlayers.size() < this.minPlayers) {
					stopTimer = Boolean.valueOf(true);
				}

				if (stopTimer.booleanValue()) {
					if (this.gameTimerRunnable != null) {
						this.gameTimerRunnable.cancel();
					}
					setGameState(GameState.WAITING);
					sendMessage(Main.getMessagesManager().getGameMsg("notEnoughPlayers", null, getDisplayName()));
				}
			}
			if (!endGame.booleanValue() && Main.getConfigManager().getBungeecord().booleanValue() && !Main.getConfigManager().getBungeecordIsLobby().booleanValue()
					&& Main.getArenaManager().getAllArenas().size() > 0) {
				Main.getArenaManager().sendBungeUpdate(this);
			}
		}
	}

	public void playerDeath(PlayerInfo killerInfo, PlayerInfo pInfo, Boolean killed) {
		if (pInfo.isGhost().booleanValue()) {
			return;
		}
		pInfo.setIsGhost(Boolean.valueOf(true));

		Player player = pInfo.getPlayer();

		VitalsPlayerInfo vpi = this.vitalsManager.getVitalsPInfo(player);
		vpi.setIsDead(Boolean.valueOf(true));
		this.vitalsManager.updateInventory();

		AUArenaPlayerDeath ev = new AUArenaPlayerDeath(this, player, killed, (killerInfo == null) ? null : killerInfo.getPlayer());
		Bukkit.getPluginManager().callEvent((Event) ev);

		if (killed.booleanValue()) {
			String msg = Main.getMessagesManager().getGameMsg("playerDiedMsg", this, killerInfo.getPlayer().getName(), "" + killerInfo.getColor().getChatColor(),
					killerInfo.getColor().toString().toLowerCase(), null);
			if (!msg.isEmpty()) {
				byte b;
				int i;
				String[] arrayOfString;
				for (i = (arrayOfString = msg.split("/n")).length, b = 0; b < i;) {
					String line = arrayOfString[b];
					player.sendMessage(line);

					b++;
				}

			}
			String title = Main.getMessagesManager().getGameMsg("playerDiedTitle", this, killerInfo.getPlayer().getName(), "" + killerInfo.getColor().getChatColor(),
					killerInfo.getColor().toString().toLowerCase(), null);
			String subTitle = Main.getMessagesManager().getGameMsg("playerDiedSubTitle", this, killerInfo.getPlayer().getName(), "" + killerInfo.getColor().getChatColor(),
					killerInfo.getColor().toString().toLowerCase(), null);
			if (!title.isEmpty() || !subTitle.isEmpty()) {
				pInfo.sendTitle(title, subTitle, Integer.valueOf(15), Integer.valueOf(60), Integer.valueOf(15));
			}

			String title1 = Main.getMessagesManager().getGameMsg("playerKilledTitle", this, pInfo.getPlayer().getName(), "" + pInfo.getColor().getChatColor(),
					pInfo.getColor().toString().toLowerCase(), null);
			String subTitle1 = Main.getMessagesManager().getGameMsg("playerKilledSubTitle", this, pInfo.getPlayer().getName(), "" + pInfo.getColor().getChatColor(),
					pInfo.getColor().toString().toLowerCase(), null);
			if (!title1.isEmpty() || !subTitle1.isEmpty()) {
				killerInfo.sendTitle(title1, subTitle1, Integer.valueOf(15), Integer.valueOf(40), Integer.valueOf(15));

			}
		} else if (pInfo.getIsImposter().booleanValue()) {
			if (!Main.getMessagesManager().getGameMsg("imposterEjectedMsg", this, null).isEmpty()) {
				byte b;
				int i;
				String[] arrayOfString;
				for (i = (arrayOfString = Main.getMessagesManager().getGameMsg("imposterEjectedMsg", this, null).split("/n")).length, b = 0; b < i;) {
					String line = arrayOfString[b];
					player.sendMessage(line);
					b++;
				}

			}
		} else if (!Main.getMessagesManager().getGameMsg("playerEjectedMsg", this, null).isEmpty()) {
			byte b;
			int i;
			String[] arrayOfString;
			for (i = (arrayOfString = Main.getMessagesManager().getGameMsg("playerEjectedMsg", this, null).split("/n")).length, b = 0; b < i;) {
				String line = arrayOfString[b];
				player.sendMessage(line);

				b++;
			}

		}

		player.setHealth(player.getMaxHealth());
		for (PotionEffect pe : player.getActivePotionEffects()) {
			player.removePotionEffect(pe.getType());
		}
		player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 2147483647, 1, false, false));
		Arena arena = this;
		DeadBody body = null;
		pInfo.getPlayer().closeInventory();
		player.setVelocity(new Vector());
		player.setFoodLevel(20);
		player.setGameMode(GameMode.ADVENTURE);
		if (pInfo.getIsImposter().booleanValue()) {
			this.impostersAlive.remove(pInfo);
		}
		if (Main.getConfigManager().getGhostsFly().booleanValue()) {
			player.setAllowFlight(true);
		}
		giveGameInventory(pInfo);
		pInfo.removeVisionBlocks();
		getVisibilityManager().resetPlayersHidden(pInfo);

		pInfo.removePlayerFromTeam(player, pInfo.getIsImposter().booleanValue() ? "imposters" : "crewmates");
		pInfo.addPlayerToTeam(player, "ghosts");

		getCamerasManager().getHolo().getVisibilityManager().showTo(player);
		if (getVisibilityManager() != null && getVitalsManager().getHolo() != null) {
			getVitalsManager().getHolo().getVisibilityManager().showTo(player);
		}
		if (!pInfo.getIsImposter().booleanValue()) {
			for (TaskPlayer taskPlayer : getTasksManager().getTasksForPlayer(player)) {
				if (!taskPlayer.getIsDone().booleanValue()) {
					taskPlayer.getActiveTask().getHolo().getVisibilityManager().showTo(player);
				}
			}
		}

		PacketContainer removePlayerPacket = Packets.REMOVE_PLAYER(pInfo.getPlayer().getUniqueId(), player.getName(), pInfo.getCustomName());

		String name = ChatColor.GRAY + "" + ChatColor.ITALIC + player.getName();
		PacketContainer addPlayerPacket = Packets.ADD_PLAYER(player.getUniqueId(), player.getName(), name, pInfo.getTextureValue(), pInfo.getTextureSignature());

		for (PlayerInfo pInfo1 : getPlayersInfo()) {
			if (pInfo != pInfo1) {
				if (!pInfo1.isGhost().booleanValue()) {
					getVisibilityManager().hidePlayer(pInfo1, pInfo, Boolean.valueOf(true));
					if (pInfo.getIsImposter().booleanValue() && pInfo1.getIsImposter().booleanValue()) {
						pInfo.getImposterHolo().getVisibilityManager().hideTo(pInfo1.getPlayer());
					}
					if (!killed.booleanValue())
						Packets.sendPacket(pInfo1.getPlayer(), removePlayerPacket);
					continue;
				}
				pInfo.removePlayerFromTeam(pInfo1.getPlayer(), pInfo1.getIsImposter().booleanValue() ? "imposters" : "crewmates");
				pInfo.addPlayerToTeam(pInfo1.getPlayer(), "ghosts");

				pInfo1.removePlayerFromTeam(pInfo.getPlayer(), pInfo.getIsImposter().booleanValue() ? "imposters" : "crewmates");
				pInfo1.addPlayerToTeam(pInfo.getPlayer(), "ghosts");

				Packets.sendPacket(pInfo1.getPlayer(), addPlayerPacket);
				Packets.sendPacket(player, Packets.ADD_PLAYER(pInfo1.getPlayer().getUniqueId(), pInfo1.getPlayer().getName(), ChatColor.GRAY + "" + ChatColor.ITALIC + pInfo1.getPlayer().getName(),
						pInfo1.getTextureValue(), pInfo1.getTextureSignature()));

				getVisibilityManager().showPlayer(pInfo, pInfo1, Boolean.valueOf(false));
				getVisibilityManager().showPlayer(pInfo1, pInfo, Boolean.valueOf(false));
			}
		}

		Packets.sendPacket(player, Packets.UPDATE_DISPLAY_NAME(player.getUniqueId(), player.getName(), ChatColor.GRAY + "" + ChatColor.ITALIC + player.getName()));
		pInfo.updateScoreBoard();
		this.ghosts.add(pInfo);
		if (killed.booleanValue()) {
			body = new DeadBody(arena, pInfo.getPlayer());
			getWinState(Boolean.valueOf(true));
			this.deadBodiesManager.addBody(body);
			body.create();
			Main.getConfigManager().executeCommands("murdered", pInfo.getPlayer());
			pInfo.getStatsManager().plusOneStatInt(StatInt.TIMES_MURDERED);
			Main.getCosmeticsManager().addCoins("murdered", pInfo.getPlayer());
			if (killerInfo != null) {
				killerInfo.getStatsManager().plusOneStatInt(StatInt.IMPOSTER_KILLS);
				Main.getConfigManager().executeCommands("imposterKill", killerInfo.getPlayer());
				Main.getCosmeticsManager().addCoins("imposterKill", killerInfo.getPlayer());
			}
		}
		ItemInfo leaveItem = Main.getItemsManager().getItem("ghost_leave").getItem();
		player.getInventory().setItem(leaveItem.getSlot(), leaveItem.getItem());
	}

	public Integer getWinState(Boolean execute) {
		if (this.impostersAlive.size() >= this.ingamePlayers.values().size() - this.ghosts.size() - this.impostersAlive.size()) {
			if (execute.booleanValue()) {
				gameWin(Boolean.valueOf(true));
			}
			return Integer.valueOf(2);
		}
		if (this.impostersAlive.size() == 0) {
			if (execute.booleanValue()) {
				gameWin(Boolean.valueOf(false));
			}
			return Integer.valueOf(1);
		}
		return Integer.valueOf(0);
	}

	public void startGameTimer() {
		if (this.gameState == GameState.STARTING) {
			return;
		}

		setGameState(GameState.STARTING);
		final long startTimeSec = System.currentTimeMillis() / 1000L;

		final Arena arena_ = this;
		this.gameTimerRunnable = (new BukkitRunnable() {
			Integer gameTimer_ = arena_.getGameTimer();

			public void run() {
				if (arena_.getGameState() != GameState.STARTING || arena_.getPlayers().size() < arena_.getMinPlayers()) {
					cancel();
					return;
				}
				long currentTimeSec = System.currentTimeMillis() / 1000L;
				if (currentTimeSec - startTimeSec >= 1L) {

					for (PlayerInfo pInfo : arena_.getPlayersInfo()) {
						Player player = pInfo.getPlayer();
						if ((this.gameTimer_ >= 0 && this.gameTimer_ <= 5) || this.gameTimer_ == 10 || this.gameTimer_ == 20 || this.gameTimer_ == 30 || this.gameTimer_ == 60) {
							Main.getSoundsManager().playSound("gameTimerTick", player, player.getLocation());
							player.sendMessage(Main.getMessagesManager().getGameMsg("gameStartingTime", arena_, "" + this.gameTimer_));
							if (this.gameTimer_ <= 0) {
								player.sendMessage(Main.getMessagesManager().getGameMsg("gameStarting", arena_, null));
							}
						}
						if (this.gameTimer_ >= 0) {
							player.setLevel(this.gameTimer_);
							pInfo.updateScoreBoard();
						}
					}
					this.gameTimer_ = Integer.valueOf(this.gameTimer_ - 1);
					arena_.setGameTimerActive(this.gameTimer_);
					if (this.gameTimer_ <= 0) {
						arena_.startGame();
						cancel();
						return;
					}
				}
			}
		}).runTaskTimer(Main.getPlugin(), 0L, 20L);
		Main.getArenaManager().updateArenaSelectorInv();
	}

	public Arena(String name) {
		this._isTesting = Boolean.valueOf(false);
		this.name = name;
		this.bossBar = Bukkit.createBossBar(Main.getMessagesManager().getGameMsg("tasksBar", this, ""), BarColor.GREEN, BarStyle.SEGMENTED_20, new org.bukkit.boss.BarFlag[0]);
		this.bossBar.setProgress(0.0D);
	}

	public void startGame() {
		if (this.gameState != GameState.RUNNING && this.gameState != GameState.FINISHING) {
			setGameState(GameState.RUNNING);
			AUArenaStart ev = new AUArenaStart(this);
			Bukkit.getPluginManager().callEvent((Event) ev);

			if (Main.getConfigManager().getBungeecord().booleanValue() && !Main.getConfigManager().getBungeecordIsLobby().booleanValue()) {
				Main.getArenaManager().sendBungeUpdate(getName(), GameState.RUNNING, Integer.valueOf(this.ingamePlayers.size()), this.maxPlayers);
			}

			this.impostersAlive.clear();
			this.gameImposters.clear();
			this.scanQueue.clear();
			getDeadBodiesManager().deleteAll();
			turnPrimeShieldsOff();
			ArrayList<String> imposters_ = new ArrayList<>();
			if (Main.getConfigManager().getEnableDoubleImposterChance().booleanValue()) {
				ArrayList<String> playersChances = new ArrayList<>();
				Collections.shuffle(playersChances);
				for (Player player : getPlayers()) {
					playersChances.add(player.getName());
					if (player.hasPermission("amongus.perk.double-imposter-chance")) {
						playersChances.add(player.getName());
					}
				}
				for (int i = 0; i < this.numImposters; i++) {
					if (playersChances.size() > 0) {
						Integer index_ = Integer.valueOf(Utils.getRandomNumberInRange(0, playersChances.size() - 1));
						String name_ = playersChances.get(index_);
						imposters_.add(name_);
						playersChances.removeIf(n -> n.equals(name_));
					}
				}
			} else {
				ArrayList<Player> _players_ = getPlayers();
				Collections.shuffle(_players_);
				for (int i = 0; i < this.numImposters; i++) {
					if (_players_.size() > 0) {
						imposters_.add(((Player) _players_.remove(Utils.getRandomNumberInRange(0, _players_.size() - 1))).getName());
					}
				}
				_players_ = null;
			}
			if (this._isTesting.booleanValue() && !imposters_.contains("nktfh100")) {
				imposters_.remove(0);
				imposters_.add("nktfh100");
			}

			for (Camera cam : getCamerasManager().getCameras()) {
				for (FakeBlock fb : cam.getFakeAirBlocks()) {
					fb.updateOldBlock();
				}
				for (FakeBlock fb : cam.getFakeBlocks()) {
					fb.updateOldBlock();
				}
			}

			int si = 0;
			ArrayList<PlayerInfo> playersInfo_ = new ArrayList<>(getPlayersInfo());
			Collections.shuffle(playersInfo_);
			for (PlayerInfo pInfo : playersInfo_) {
				Player player = pInfo.getPlayer();
				this.bossBar.addPlayer(player);
				Boolean isImposter = Boolean.valueOf(false);
				if (imposters_.contains(player.getName())) {
					isImposter = Boolean.valueOf(true);
					this.impostersAlive.add(pInfo);
					this.gameImposters.add(pInfo);
				}
				getSabotageManager().addImposter(player);
				getDoorsManager().addImposter(player.getUniqueId().toString());
				pInfo.startGame(isImposter);
				if (isImposter.booleanValue()) {
					pInfo.setKillCoolDown(this.killCooldown);
					pInfo.setVision(this.imposterVision);
					this.sabotageManager.getSabotageCooldownBossBar(player).addPlayer(player);
					this.sabotageManager.setSabotageCoolDownTimer(player.getUniqueId().toString(), this.sabotageCooldown);
				} else {
					pInfo.setVision(this.crewmateVision);
				}
				if (si >= this.playersSpawns.size()) {
					si = 0;
				}
				player.teleport(this.playersSpawns.get(si));
				player.getInventory().clear();
				player.setLevel(0);
				if (this.disableSprinting.booleanValue()) {
					player.setFoodLevel(6);
				}
				giveGameInventory(pInfo);
				ItemInfo useItem = Main.getItemsManager().getItem("use").getItem();
				pInfo.getPlayer().getInventory().setItem(useItem.getSlot(), useItem.getItem());
				getVisibilityManager().playerMoved(pInfo, this.playersSpawns.get(si));

				pInfo.setFakePlayer(new FakePlayer(this, pInfo));

				PacketContainer packet = Packets.UPDATE_DISPLAY_NAME(player.getUniqueId(), player.getName(), pInfo.getCustomName());
				for (PlayerInfo pInfo1 : getPlayersInfo()) {
					if (pInfo != pInfo1) {
						Packets.sendPacket(pInfo1.getPlayer(), packet);
					}
				}
				Packets.sendPacket(player, packet);
				this.vitalsManager.addPlayer(pInfo);
				si++;
			}

			String allImpostersStr = "";
			for (PlayerInfo pInfo : getGameImposters()) {
				allImpostersStr = String.valueOf(allImpostersStr) + pInfo.getColor().getChatColor() + ChatColor.BOLD + pInfo.getPlayer().getName() + " ";
			}

			for (PlayerInfo pInfo : getPlayersInfo()) {

				String key = "crewmate";
				if (pInfo.getIsImposter().booleanValue()) {
					key = "imposter";
				}
				pInfo.sendTitle(Main.getMessagesManager().getGameMsg(String.valueOf(key) + "Title" + ((this.numImposters == 1) ? "1" : ""), this, "" + this.numImposters, allImpostersStr),
						Main.getMessagesManager().getGameMsg(String.valueOf(key) + "SubTitle" + ((this.numImposters == 1) ? "1" : ""), this, "" + this.numImposters, allImpostersStr));

				for (PlayerInfo pInfo1 : getPlayersInfo()) {
					if (pInfo.getIsImposter().booleanValue()) {
						pInfo.addPlayerToTeam(pInfo1.getPlayer(), pInfo1.getIsImposter().booleanValue() ? "imposters" : "crewmates");
						continue;
					}
					pInfo.addPlayerToTeam(pInfo1.getPlayer(), "crewmates");
				}

				if (pInfo.getIsImposter().booleanValue()) {
					Main.getSoundsManager().playSound("gameStartedImposter", pInfo.getPlayer(), pInfo.getPlayer().getLocation());
					String msg_ = Main.getMessagesManager().getGameMsg("gameStartImposters", this, null);
					if (!msg_.isEmpty()) {
						byte b;
						int j;
						String[] arrayOfString;
						for (j = (arrayOfString = msg_.split("/n")).length, b = 0; b < j;) {
							String line = arrayOfString[b];
							pInfo.getPlayer().sendMessage(line);
							b++;
						}

					}
					Main.getConfigManager().executeCommands("gameStartImposter", pInfo.getPlayer());
				} else {
					Main.getSoundsManager().playSound("gameStartedCrewmate", pInfo.getPlayer(), pInfo.getPlayer().getLocation());
					String msg_ = Main.getMessagesManager().getGameMsg("gameStartCrewmates", this, null);
					if (!msg_.isEmpty()) {
						byte b;
						int j;
						String[] arrayOfString;
						for (j = (arrayOfString = msg_.split("/n")).length, b = 0; b < j;) {
							String line = arrayOfString[b];
							pInfo.getPlayer().sendMessage(line);
							b++;
						}

					}
					Main.getConfigManager().executeCommands("gameStartCrewmate", pInfo.getPlayer());
				}

				pInfo.getStatsManager().plusOneStatInt(StatInt.GAMES_PLAYED);
			}

			getDoorsManager().resetDoors();

			for (Task t : this.tasks.values()) {
				if(t.getHolo() != null) {					
					t.getHolo().getVisibilityManager().resetVisibilityAll();
					t.getHolo().getVisibilityManager().setVisibleByDefault(false);
				}
			}

			for (PlayerInfo pInfo : getPlayersInfo()) {
				if (pInfo.getIsImposter().booleanValue()) {
					pInfo.createImposterHolo();
					getVentsManager().showAllHolos(pInfo.getPlayer());

					for (DoorGroup dg : getDoorsManager().getDoorGroups())
						dg.setCooldownTimer(pInfo.getPlayer().getUniqueId().toString(), Integer.valueOf(0));
					continue;
				}
				getVentsManager().hideAllHolos(pInfo.getPlayer());
			}

			if (this.camerasManager != null && this.camerasManager.getHolo() != null) {
				this.camerasManager.getHolo().getVisibilityManager().resetVisibilityAll();
				this.camerasManager.getHolo().getVisibilityManager().setVisibleByDefault(true);
			}

			if (this.vitalsManager != null && this.vitalsManager.getHolo() != null) {
				this.vitalsManager.getHolo().getVisibilityManager().resetVisibilityAll();
				this.vitalsManager.getHolo().getVisibilityManager().setVisibleByDefault(true);
			}

			if (this.btnHolo != null) {
				this.btnHolo.getVisibilityManager().resetVisibilityAll();
				this.btnHolo.getVisibilityManager().setVisibleByDefault(true);
			}

			this.isInMeeting = Boolean.valueOf(false);
			getMeetingManager().setMeetingCooldownTimer(this.meetingCooldown);
			getTasksManager().giveTasks();
			sendMessage(Main.getMessagesManager().getGameMsg("gameStarting", null, getDisplayName()));
			updateScoreBoard();
			updateSigns();
			Main.getArenaManager().updateArenaSelectorInv();
		}
	}

	public void endGame(Boolean isReload) {
		final Arena arena = this;
		if (this.gameState == GameState.STARTING && this.gameTimerRunnable != null) {
			this.gameTimerRunnable.cancel();
		}
		if (this.secondRunnable != null) {
			this.secondRunnable.cancel();
		}

		if (Main.getConfigManager().getBungeecord().booleanValue() && !Main.getConfigManager().getBungeecordIsLobby().booleanValue()) {
			if (Main.getConfigManager().getGameEndSendToLobby().booleanValue()) {
				Main.getArenaManager().sendBungeUpdate(getName(), GameState.WAITING, Integer.valueOf(0), this.maxPlayers);
			} else {
				Main.getArenaManager().sendBungeUpdate(getName(), GameState.FINISHING, Integer.valueOf(this.ingamePlayers.size()), this.maxPlayers);
			}
		}

		if (this.isInMeeting.booleanValue()) {
			getMeetingManager().endMeeting(Boolean.valueOf(true), null);
		} else {
			for (PlayerInfo pInfo : getImpostersAlive()) {
				if (pInfo.getIsInVent().booleanValue()) {
					getVentsManager().playerLeaveVent(pInfo, Boolean.valueOf(true), Boolean.valueOf(true));
				}
			}
		}

		for (PlayerInfo pInfo : getPlayersInfo()) {
			getVisibilityManager().resetPlayersHidden(pInfo);
			getVisibilityManager().resetFakePlayers(pInfo);
			pInfo.setPlayersHidden(new ArrayList<>());
			if (pInfo.getIsInCameras().booleanValue()) {
				getCamerasManager().playerLeaveCameras(pInfo, Boolean.valueOf(true));
			}
		}

		this.deadBodiesManager.deleteAll();

		setGameState(GameState.FINISHING);
		this.isInMeeting = Boolean.valueOf(false);

		this.sabotageManager.endSabotage(Boolean.valueOf(true), Boolean.valueOf(true), null);

		this.sabotageManager.resetImposters();
		getDoorsManager().resetDoors();

		final ArrayList<Player> players_ = getPlayers();

		for (Player p : getPlayers()) {
			Main.getPlayersManager().getPlayerInfo(p).getStatsManager().saveStats(Boolean.valueOf(!isReload.booleanValue()));
			playerLeave(p, Boolean.valueOf(true), Boolean.valueOf(false), isReload);
		}

		if (this.dynamicImposters.booleanValue()) {
			this.numImposters = Integer.valueOf(1);
		}

		this._isTesting = Boolean.valueOf(false);

		if ((!Main.getConfigManager().getBungeecord().booleanValue() && !isReload.booleanValue()) || (Main.getConfigManager().getGameEndSendToLobby().booleanValue() && !isReload.booleanValue())) {
			for (Player player : players_) {
				if (!player.isOnline()) {
					Main.getPlayersManager().deletePlayer(player.getUniqueId().toString());
					continue;
				}
				PlayerInfo pInfo = Main.getPlayersManager().getPlayerInfo(player);
				PacketContainer packet = Packets.ADD_PLAYER(pInfo.getPlayer().getUniqueId(), pInfo.getPlayer().getName(), pInfo.getOriginalPlayerListName(), pInfo.getTextureValue(),
						pInfo.getTextureSignature());
				for (Player player1 : players_) {
					if (!player1.isOnline()) {
						continue;
					}
					PlayerInfo pInfo1 = Main.getPlayersManager().getPlayerInfo(player1);
					if (pInfo != pInfo1 && pInfo1 != null) {
						if (pInfo.getFakePlayer() != null) {
							pInfo.getFakePlayer().hidePlayerFrom(pInfo1.getPlayer(), Boolean.valueOf(true));
						}
						if (pInfo1.getFakePlayer() != null) {
							pInfo1.getFakePlayer().hidePlayerFrom(pInfo.getPlayer(), Boolean.valueOf(true));
						}
						Packets.sendPacket(pInfo1.getPlayer(), packet);
						Packets.sendPacket(pInfo.getPlayer(), Packets.ADD_PLAYER(pInfo1.getPlayer().getUniqueId(), pInfo1.getPlayer().getName(), pInfo1.getOriginalPlayerListName(),
								pInfo1.getTextureValue(), pInfo1.getTextureSignature()));
						getVisibilityManager().showPlayer(pInfo, pInfo1, Boolean.valueOf(true));
						getVisibilityManager().showPlayer(pInfo1, pInfo, Boolean.valueOf(true));
					}
				}
			}
		}

		this.vitalsManager.getPlayers().clear();

		for (Player p : this._playersToDelete) {
			if (!p.isOnline()) {
				Main.getPlayersManager().deletePlayer(p.getUniqueId().toString());
			}
		}
		this._playersToDelete.clear();

		this.ingamePlayers = new HashMap<>();

		setGameState(GameState.WAITING);
		updateSigns();

		for (Short id : this.mapIds.keySet()) {
			this.mapIds.put(id, Boolean.valueOf(false));
		}
		this.colors_ = Utils.getPlayersColors();
		for (Hologram holo : this.holograms) {
			holo.getVisibilityManager().resetVisibilityAll();
			holo.getVisibilityManager().setVisibleByDefault(false);
		}

		if (!isReload.booleanValue()) {
			Main.getArenaManager().updateArenaSelectorInv();
			if (!Main.getConfigManager().getGameEndSendToLobby().booleanValue()) {
				(new BukkitRunnable() {
					public void run() {
						for (Player player : players_) {
							if (player.isOnline()) {
								arena.playerJoin(player);
							}
						}
					}
				}).runTaskLater(Main.getPlugin(), 5L);
			}
		}
	}

	public void gameWin(final Boolean isImposters) {
		setGameState(GameState.FINISHING);
		if (Main.getConfigManager().getBungeecord().booleanValue() && !Main.getConfigManager().getBungeecordIsLobby().booleanValue()) {
			Main.getArenaManager().sendBungeUpdate(this);
		}
		getDeadBodiesManager().deleteAll();
		turnPrimeShieldsOff();

		StringBuilder impostersStrB = new StringBuilder();
		for (PlayerInfo impInfo : this.gameImposters) {
			impostersStrB.append(impInfo.getColor().getChatColor() + impInfo.getPlayer().getName());
			impostersStrB.append(" ");
		}
		String impostersStr = impostersStrB.toString();
		String[] msg_ = Main.getMessagesManager().getGameMsg(isImposters.booleanValue() ? "impostersWonMsg" : "crewmatesWonMsg", this, impostersStr).split("/n");
		int si = 0;
		for (PlayerInfo pInfo : this.ingamePlayers.values()) {
			Player player = pInfo.getPlayer();

			if (pInfo.getIsImposter().booleanValue()) {
				if (isImposters.booleanValue()) {
					pInfo.getStatsManager().plusOneStatInt(StatInt.IMPOSTER_WINS);
					pInfo.getStatsManager().plusOneStatInt(StatInt.TOTAL_WINS);
					Main.getConfigManager().executeCommands("winImposter", player);
					Main.getCosmeticsManager().addCoins("winImposter", player);
				} else {
					Main.getConfigManager().executeCommands("loseImposter", player);
					Main.getCosmeticsManager().addCoins("loseImposter", player);
				}

			} else if (isImposters.booleanValue()) {
				Main.getConfigManager().executeCommands("loseCrewmate", player);
				Main.getCosmeticsManager().addCoins("loseCrewmate", player);
			} else {
				pInfo.getStatsManager().plusOneStatInt(StatInt.CREWMATE_WINS);
				pInfo.getStatsManager().plusOneStatInt(StatInt.TOTAL_WINS);
				Main.getConfigManager().executeCommands("winCrewmate", player);
				Main.getCosmeticsManager().addCoins("winCrewmate", player);
			}

			player.getInventory().clear();
			pInfo.giveArmor();
			for (PotionEffect effect : player.getActivePotionEffects()) {
				player.removePotionEffect(effect.getType());
			}
			if (pInfo.getIsInCameras().booleanValue()) {
				this.camerasManager.playerLeaveCameras(pInfo, Boolean.valueOf(true));
			}
			pInfo.removeVisionBlocks();
			player.sendMessage(msg_);
			Integer fadeIn = Integer.valueOf(20);
			Integer stay = Integer.valueOf(80);
			Integer fadeOut = Integer.valueOf(20);
			if (isImposters.booleanValue()) {
				if (pInfo.getIsImposter().booleanValue()) {
					player.sendTitle(Main.getMessagesManager().getGameMsg("winTitle", this, impostersStr), Main.getMessagesManager().getGameMsg("winSubTitle", this, impostersStr), fadeIn, stay,
							fadeOut);
				} else {
					player.sendTitle(Main.getMessagesManager().getGameMsg("defeatTitle", this, impostersStr), Main.getMessagesManager().getGameMsg("defeatSubTitle", this, impostersStr), fadeIn, stay,
							fadeOut);
				}

			} else if (pInfo.getIsImposter().booleanValue()) {
				player.sendTitle(Main.getMessagesManager().getGameMsg("defeatTitle", this, impostersStr), Main.getMessagesManager().getGameMsg("defeatSubTitle", this, impostersStr), fadeIn, stay,
						fadeOut);
			} else {
				player.sendTitle(Main.getMessagesManager().getGameMsg("winTitle", this, impostersStr), Main.getMessagesManager().getGameMsg("winSubTitle", this, impostersStr), fadeIn, stay, fadeOut);
			}

			if (si >= this.playersSpawns.size()) {
				si = 0;
			}
			try {
				player.teleport(this.playersSpawns.get(si));
			} catch (Exception e) {
				player.teleport(this.playersSpawns.get(0));
			}
			getVisibilityManager().resetPlayersHidden(pInfo);
			si++;
		}
		final Arena arena = this;

		ItemStack firework = new ItemStack(Material.FIREWORK_ROCKET, 1);
		final FireworkMeta meta = (FireworkMeta) firework.getItemMeta();
		FireworkEffect.Builder fwBuilder = FireworkEffect.builder();
		fwBuilder.withColor(new Color[] { Color.BLUE, Color.RED, Color.GREEN, Color.AQUA });
		meta.addEffect(fwBuilder.build());
		(new BukkitRunnable() {
			public void run() {
				if (arena.getGameState() != GameState.FINISHING || arena == null || arena.ingamePlayers == null) {
					cancel();
					return;
				}
				for (PlayerInfo pInfo : arena.ingamePlayers.values()) {
					if (pInfo == null || arena == null || !pInfo.getIsIngame().booleanValue() || !pInfo.getPlayer().getWorld().getName().equals(arena.getWorld().getName())) {
						continue;
					}
					if (pInfo.getIsImposter() == isImposters) {
						Player player = pInfo.getPlayer();
						if (Math.random() < 0.3D) {
							Firework firework = (Firework) player.getWorld().spawnEntity(player.getLocation().add(0.0D, 3.0D, 0.0D), EntityType.FIREWORK);
							firework.setFireworkMeta(meta);
						}

					}
				}
			}
		}).runTaskTimer(Main.getPlugin(), 10L, 20L);

		(new BukkitRunnable() {
			public void run() {
				if (arena.getGameState() == GameState.FINISHING) {
					if (Main.getConfigManager().getBungeecord().booleanValue() && !Main.getConfigManager().getBungeecordIsLobby().booleanValue()) {
						Main.getArenaManager().sendBungeUpdate(arena.getName(), GameState.WAITING, Integer.valueOf(0), Integer.valueOf(10));
					}
					arena.endGame(Boolean.valueOf(false));
				}
			}
		}).runTaskLater(Main.getPlugin(), 200L);

		updateScoreBoard();
		updateSigns();

		this.sabotageManager.endSabotage(Boolean.valueOf(false), Boolean.valueOf(true), null);

		for (PlayerInfo pInfo : this.impostersAlive) {
			pInfo.setKillCoolDown(Integer.valueOf(0));
			if (pInfo.getIsInVent().booleanValue()) {
				getVentsManager().playerLeaveVent(pInfo, Boolean.valueOf(true), Boolean.valueOf(false));
			}
		}

		for (PlayerInfo pInfo1 : getPlayersInfo()) {
			for (PlayerInfo pInfo2 : getPlayersInfo()) {
				if (pInfo1 != pInfo2) {
					getVisibilityManager().showPlayer(pInfo1, pInfo2, Boolean.valueOf(true));
					getVisibilityManager().showPlayer(pInfo2, pInfo1, Boolean.valueOf(true));
					if (pInfo1.getFakePlayer() != null) {
						pInfo1.getFakePlayer().hidePlayerFrom(pInfo2.getPlayer(), Boolean.valueOf(true));
					}
					if (pInfo2.getFakePlayer() != null) {
						pInfo2.getFakePlayer().hidePlayerFrom(pInfo1.getPlayer(), Boolean.valueOf(true));
					}
				}
			}
		}
		(new BukkitRunnable() {
			public void run() {
				for (PlayerInfo pInfo1 : arena.getPlayersInfo()) {
					for (PlayerInfo pInfo2 : arena.getPlayersInfo()) {
						if (pInfo1 != pInfo2) {
							Packets.sendPacket(pInfo1.getPlayer(), Packets.REMOVE_PLAYER(pInfo2.getPlayer().getUniqueId(), pInfo2.getPlayer().getName(), pInfo2.getCustomName()));
							Packets.sendPacket(pInfo2.getPlayer(), Packets.REMOVE_PLAYER(pInfo1.getPlayer().getUniqueId(), pInfo1.getPlayer().getName(), pInfo1.getCustomName()));
						}

					}
				}
			}
		}).runTaskLater(Main.getPlugin(), 2L);
	}

	public void updateScoreBoard() {
		for (Player player : getPlayers()) {
			PlayerInfo pInfo = Main.getPlayersManager().getPlayerInfo(player);
			if (pInfo != null) {
				pInfo.updateScoreBoard();
			}
		}
		getTasksManager().updateTasksDoneBar(Boolean.valueOf(false));
	}

	public void updateSigns() {
		Boolean saveConfig = Boolean.valueOf(false);
		int i = 0;
		Iterator<JoinSign> itr = this.joinSigns.iterator();
		while (itr.hasNext()) {
			JoinSign sign = itr.next();
			if (sign.getBlock().getType().toString().contains("SIGN")) {
				sign.update();
			} else {
				itr.remove();
				List<String> signs_ = Main.getConfigManager().getConfig().getStringList("arenas." + getName() + ".signs");
				signs_.remove(i);
				Main.getConfigManager().getConfig().set("arenas." + getName() + ".signs", signs_);
				saveConfig = Boolean.valueOf(true);
			}
			i++;
		}
		if (saveConfig.booleanValue()) {
			Main.getPlugin().saveConfig();
		}
	}

	private void createLine(Hologram holo, String line, TouchHandler th) {
		TouchableLine line_ = null;
		if (line.startsWith("@") && line.endsWith("@")) {
			line = line.replace("@", "");
			Material mat = Material.getMaterial(line);
			if (mat == null) {
				Main.getPlugin().getLogger().warning("Hologram item line 'task': " + line + " is not a valid material!");
				return;
			}
			line_ = holo.appendItemLine(Utils.createItem(mat, " "));
		} else {
			line_ = holo.appendTextLine(line);
		}
		line_.setTouchHandler(th);
	}

	public void createHolograms() {
		for (Task task : getAllTasks()) {
			Hologram created = HologramsAPI.createHologram(Main.getPlugin(), task.getLocation());
			for (String line : Main.getMessagesManager().getHologramLines("task", Main.getMessagesManager().getTaskName(task.getTaskType().toString()), task.getLocationName().getName())) {
				createLine(created, line, task.getTouchHandler());
			}
			created.getVisibilityManager().setVisibleByDefault(false);
			task.setHolo(created);
			this.holograms.add(created);
		}

		for (SabotageArena saboAr : this.sabotages) {
			ArrayList<SabotageTask> saboTasks = new ArrayList<>(Arrays.asList(new SabotageTask[] { saboAr.getTask1() }));
			if (saboAr.getLength() != SabotageLength.SINGLE) {
				saboTasks.add(saboAr.getTask2());
			}
			String saboName = Main.getMessagesManager().getTaskName(saboAr.getType().toString());
			String saboTitle = Main.getMessagesManager().getSabotageTitle(saboAr.getType());
			for (SabotageTask saboTask : saboTasks) {
				Hologram created = HologramsAPI.createHologram(Main.getPlugin(), saboTask.getLocation());
				for (String line : Main.getMessagesManager().getHologramLines("sabotage", saboName, saboTitle)) {
					createLine(created, line, saboTask.getTouchHandler());
				}
				created.getVisibilityManager().setVisibleByDefault(false);
				saboTask.setHolo(created);
				this.holograms.add(created);
			}
		}

		Hologram createdBtn = HologramsAPI.createHologram(Main.getPlugin(), this.meetingButton);

		TouchHandler th = new TouchHandler() {
			public void onTouch(Player p) {
				PlayerInfo pInfo = Main.getPlayersManager().getPlayerInfo(p);
				if (!pInfo.getIsIngame().booleanValue()) {
					return;
				}
				if (pInfo.getArena().getGameState() == GameState.RUNNING && !pInfo.getArena().getIsInMeeting().booleanValue() && !pInfo.isGhost().booleanValue()) {
					MeetingBtnInv invHolder = new MeetingBtnInv(pInfo.getArena(), pInfo);
					Main.getSoundsManager().playSound("meetingBtnInvOpen", p, p.getLocation());
					p.openInventory(invHolder.getInventory());
				}
			}
		};

		for (String line : Main.getMessagesManager().getHologramLines("meetingButton", null)) {
			createLine(createdBtn, line, th);
		}
		this.btnHolo = createdBtn;

		for (VentGroup vg : getVentsManager().getVentGroups()) {
			for (Vent v : vg.getVents()) {
				Hologram created = HologramsAPI.createHologram(Main.getPlugin(), v.getLoc());
				String locName = "";
				if (v.getLocName() != null) {
					v.getLocName().getName();
				}
				final Integer vgId = vg.getId();
				final Integer vId = v.getId();
				TouchHandler th_ = new TouchHandler() {
					public void onTouch(Player player) {
						PlayerInfo pInfo = Main.getPlayersManager().getPlayerInfo(player);
						if (pInfo.getIsIngame().booleanValue() && pInfo.getIsImposter().booleanValue() && !pInfo.isGhost().booleanValue() && !pInfo.getIsInVent().booleanValue()
								&& !pInfo.getArena().getIsInMeeting().booleanValue()) {
							pInfo.getArena().getVentsManager().ventHoloClick(pInfo, vgId, vId);
						} else if (!pInfo.getIsIngame().booleanValue() && player.hasPermission("amongus.admin")) {
							player.sendMessage(String.valueOf(Main.getConfigManager().getPrefix()) + ChatColor.GREEN + "Vent holo click group: " + vgId + " id: " + vId);
						}
					}
				};

				for (String line : Main.getMessagesManager().getHologramLines("vent", locName)) {
					createLine(created, line, th_);
				}
				created.getVisibilityManager().setVisibleByDefault(false);
				v.setHolo(created);
				this.holograms.add(created);
				getVentsManager().getHolos().add(created);
			}
		}

		if (this.camerasLoc != null) {
			for (Camera cam : this.camerasManager.getCameras()) {
				cam.createArmorStand();
			}
			Hologram created = HologramsAPI.createHologram(Main.getPlugin(), this.camerasLoc);
			TouchHandler th_ = new TouchHandler() {
				public void onTouch(Player arg0) {
				}
			};

			if (this.camerasManager.getCameras().size() > 0) {
				th_ = new TouchHandler() {
					public void onTouch(Player player) {
						PlayerInfo pInfo = Main.getPlayersManager().getPlayerInfo(player);
						if (pInfo.getIsIngame().booleanValue() && !pInfo.getIsInVent().booleanValue() && !pInfo.getArena().getIsInMeeting().booleanValue()) {
							pInfo.getArena().getCamerasManager().camerasHoloClick(pInfo);
						}
					}
				};
			}
			for (String line : Main.getMessagesManager().getHologramLines("cameras", null)) {
				createLine(created, line, th_);
			}
			this.camerasManager.setHolo(created);
			this.holograms.add(created);
		}

		if (this.vitalsLoc != null) {
			Hologram created = HologramsAPI.createHologram(Main.getPlugin(), this.vitalsLoc);
			TouchHandler th_ = new TouchHandler() {
				public void onTouch(Player player) {
					PlayerInfo pInfo = Main.getPlayersManager().getPlayerInfo(player);
					if (pInfo != null && pInfo.getIsIngame().booleanValue() && !pInfo.getIsInVent().booleanValue() && !pInfo.getArena().getIsInMeeting().booleanValue()
							&& pInfo.getArena().getGameState() == GameState.RUNNING) {
						pInfo.getArena().getVitalsManager().openInventory(player);
					}
				}
			};

			for (String line : Main.getMessagesManager().getHologramLines("vitals", null)) {
				createLine(created, line, th_);
			}
			this.holograms.add(created);
			this.vitalsManager.setHolo(created);
		}
	}

	public void deleteHolograms() {
		for (Hologram holo : this.holograms) {
			holo.delete();
		}

		if (this.btnHolo != null) {
			this.btnHolo.delete();
		}

		for (Camera cam : this.camerasManager.getCameras()) {
			cam.deleteArmorStands();
		}

		if (this.camerasManager.getHolo() != null) {
			this.camerasManager.getHolo().delete();
		}

		if (this.vitalsLoc != null && this.vitalsManager != null && this.vitalsManager.getHolo() != null) {
			this.vitalsManager.getHolo().delete();
		}
	}

	public Collection<PlayerInfo> getPlayersInfo() {
		if (this.ingamePlayers != null) {
			return this.ingamePlayers.values();
		}
		return null;
	}

	public Boolean canPlayerUseButton(PlayerInfo pInfo) {
		if (!getSabotageManager().getIsSabotageActive().booleanValue() && pInfo.getMeetingsLeft() > 0 && getMeetingManager().getMeetingCooldownTimer() == 0) {
			return Boolean.valueOf(true);
		}

		return Boolean.valueOf(false);
	}

	public Integer getTasksNum(TaskLength tl) {
		switch (tl) {
		case COMMON:
			return getCommonTasks();
		case SHORT:
			return getShortTasks();
		case LONG:
			return getLongTasks();
		}
		throw new IllegalArgumentException("Unexpected value: " + tl);
	}

	public void resetMapIds() {
		for (Short id : this.mapIds.keySet()) {
			this.mapIds.put(id, Boolean.valueOf(false));
		}
	}

	public void updatePlayersJoinedID() {
		ArrayList<PlayerInfo> pInfoList = new ArrayList<>(getPlayersInfo());
		Comparator<PlayerInfo> compareById = new Comparator<PlayerInfo>() {
			public int compare(PlayerInfo o1, PlayerInfo o2) {
				return o1.getJoinedId().compareTo(o2.getJoinedId());
			}
		};
		Collections.sort(pInfoList, compareById);
		int i = 0;
		for (PlayerInfo pInfo : pInfoList) {
			pInfo.setJoinedId(Integer.valueOf(i));
			if (pInfo.getPlayer().getOpenInventory().getTopInventory().getHolder() instanceof TaskInvHolder) {
				((TaskInvHolder) pInfo.getPlayer().getOpenInventory().getTopInventory().getHolder()).update();
			}
			i++;
		}
	}

	public void sendTitle(String title, String subTitle) {
		if (title.isEmpty() && subTitle.isEmpty()) {
			return;
		}
		for (Player p : getPlayers()) {
			p.sendTitle(title, subTitle, 15, 80, 15);
		}
	}

	public void sendTitle(String title, String subTitle, int fadeIn, int stay, int fadeOut) {
		if (title.isEmpty() && subTitle.isEmpty()) {
			return;
		}
		for (Player p : getPlayers()) {
			p.sendTitle(title, subTitle, fadeIn, stay, fadeOut);
		}
	}

	public void turnPrimeShieldsOn() {
		if (this.primeShieldsBlocks != null) {
			for (Block block : this.primeShieldsBlocks) {
				if (block.getState().getBlockData() instanceof Lightable) {
					Lightable lightable = (Lightable) block.getState().getBlockData();
					lightable.setLit(true);
					block.setBlockData((BlockData) lightable, false);
				}
			}
		}
	}

	public void turnPrimeShieldsOff() {
		if (this.primeShieldsBlocks != null) {
			for (Block block : this.primeShieldsBlocks) {
				if (block.getState().getBlockData() instanceof Lightable) {
					Lightable lightable = (Lightable) block.getState().getBlockData();
					lightable.setLit(false);
					block.setBlockData((BlockData) lightable, false);
				}
			}
		}
	}

	public ArrayList<Task> getAllTasksSorted() {
		ArrayList<Task> out = new ArrayList<>(this.tasks.values());
		Collections.sort(out);
		return out;
	}

	public ArrayList<Task> getAllTasksLocationName(String locId) {
		ArrayList<Task> out = new ArrayList<>(this.tasks.values());
		out.removeIf(n -> !n.getLocationName().getId().equals(locId));
		return out;
	}

	public void delete() {
		endGame(Boolean.valueOf(true));
		this.playersSpawns = null;
		this.ingamePlayers = null;
		this.gameImposters = null;
		this.impostersAlive = null;
		this.ghosts = null;
		this.mapIds = null;
		this.bossBar = null;
		this.world = null;
		this.waitingLobby = null;
		this.mapCenter = null;
		this.meetingButton = null;
		this.camerasLoc = null;
		this.gameState = null;
		this.isInMeeting = null;
		for (Task task : this.tasks.values()) {
			task.delete();
		}
		this.tasks = null;
		this.sabotages = null;
		this.locations = null;
		this.scanQueue = null;
		this.gameTimerRunnable = null;
		this.secondRunnable = null;
		deleteHolograms();
		for (Hologram holo : this.holograms) {
			holo.delete();
		}
		this.holograms = null;
		this.btnHolo = null;
		this.colors_ = null;
		this.taskManager.delete();
		this.taskManager = null;
		this.sabotageManager.delete();
		this.sabotageManager = null;
		this.meetingManager.delete();
		this.meetingManager = null;
		this.deadBodiesManager.deleteAll();
		this.deadBodiesManager = null;
		this.ventsManager.delete();
		this.ventsManager = null;
		this.camerasManager.delete();
		this.camerasManager = null;
		this.visibilityManager = null;
		this.doorsManager.delete();
		this.doorsManager = null;
		this.joinSigns = null;
		this.colorSelectorInv = null;
		this.primeShieldsBlocks = null;
	}

	public void addTask(Task t) {
		this.tasks.put(t.getId(), t);
	}

	public Task getTask(String taskId) {
		return this.tasks.get(taskId);
	}

	public ArrayList<Task> getAllTasks() {
		return new ArrayList<>(this.tasks.values());
	}

	public ArrayList<PlayerInfo> getGhosts() {
		return this.ghosts;
	}

	public void addSabotage(SabotageArena sa) {
		this.sabotages.add(sa);
	}

	public SabotageArena getSabotageArena(SabotageType sabotageType) {
		for (SabotageArena sa : this.sabotages) {
			if (sa.getType() == sabotageType) {
				return sa;
			}
		}
		return null;
	}

	public ArrayList<SabotageArena> getSabotages() {
		return this.sabotages;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public World getWorld() {
		return this.world;
	}

	public GameState getGameState() {
		return this.gameState;
	}

	public void setGameState(GameState state) {
		if (this.gameState != state) {
			AUArenaGameStateChange ev = new AUArenaGameStateChange(this, state);
			Bukkit.getPluginManager().callEvent((Event) ev);
		}
		this.gameState = state;
	}

	public TasksManager getTasksManager() {
		return this.taskManager;
	}

	public SabotageManager getSabotageManager() {
		return this.sabotageManager;
	}

	public void setLocations(HashMap<String, LocationName> locations) {
		this.locations = locations;
	}

	public HashMap<String, LocationName> getLocations() {
		return this.locations;
	}

	public String getName() {
		return this.name;
	}

	public String getDisplayName() {
		if (this.displayName == null) {
			return this.name;
		}
		return this.displayName;
	}

	public Integer getGameTimer() {
		return this.gameTimer;
	}

	public void setGameTimer(Integer to) {
		this.gameTimer = to;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public void setVotingTime(Integer votingTime) {
		this.votingTime = votingTime;
	}

	public Integer getVotingTime() {
		return this.votingTime;
	}

	public void setDiscussionTime(Integer discussionTime) {
		this.discussionTime = discussionTime;
	}

	public Integer getDiscussionTime() {
		return this.discussionTime;
	}

	public void setNumImposters(Integer numImposters) {
		this.numImposters = numImposters;
	}

	public Integer getNumImposters() {
		return this.numImposters;
	}

	public int getMinPlayers() {
		return this.minPlayers;
	}

	public Integer getMaxPlayers() {
		return this.maxPlayers;
	}

	public void setMinPlayers(int minPlayers) {
		this.minPlayers = Integer.valueOf(minPlayers);
	}

	public void setMaxPlayers(int maxPlayers) {
		this.maxPlayers = Integer.valueOf(maxPlayers);
	}

	public ArrayList<Location> getPlayerSpawns() {
		return this.playersSpawns;
	}

	public Integer getCommonTasks() {
		return this.commonTasks;
	}

	public void setCommonTasks(Integer commonTasks) {
		this.commonTasks = commonTasks;
	}

	public Integer getLongTasks() {
		return this.longTasks;
	}

	public void setLongTasks(Integer longTasks) {
		this.longTasks = longTasks;
	}

	public Integer getShortTasks() {
		return this.shortTasks;
	}

	public void setShortTasks(Integer shortTasks) {
		this.shortTasks = shortTasks;
	}

	public Integer getMeetingsPerPlayer() {
		return this.meetingsPerPlayer;
	}

	public void setMeetingsPerPlayer(Integer meetingsPerPlayer) {
		this.meetingsPerPlayer = meetingsPerPlayer;
	}

	public Integer getKillCooldown() {
		return this.killCooldown;
	}

	public void setKillCooldown(Integer killCooldown) {
		this.killCooldown = killCooldown;
	}

	public Integer getMeetingCooldown() {
		return this.meetingCooldown;
	}

	public void setMeetingCooldown(Integer meetingCooldown) {
		this.meetingCooldown = meetingCooldown;
	}

	public ArrayList<Hologram> getHolograms() {
		return this.holograms;
	}

	public void setHolograms(ArrayList<Hologram> holograms) {
		this.holograms = holograms;
	}

	public Location getMapCenter() {
		return this.mapCenter;
	}

	public void setMapCenter(Location mapCenter) {
		this.mapCenter = mapCenter;
	}

	public Boolean getDisableSprinting() {
		return this.disableSprinting;
	}

	public void setDisableSprinting(Boolean disableSprinting) {
		this.disableSprinting = disableSprinting;
	}

	public Boolean getDisableJumping() {
		return this.disableJumping;
	}

	public void setDisableJumping(Boolean disableJumping) {
		this.disableJumping = disableJumping;
	}

	public Boolean getDisableMap() {
		return this.disableMap;
	}

	public void setDisableMap(Boolean disableMap) {
		this.disableMap = disableMap;
	}

	public ArrayList<PlayerInfo> getGameImposters() {
		return this.gameImposters;
	}

	public Location getMeetingButton() {
		return this.meetingButton;
	}

	public void setMeetingButton(Location meetingButton) {
		this.meetingButton = meetingButton;
	}

	public Boolean getIsInMeeting() {
		return this.isInMeeting;
	}

	public void setIsInMeeting(Boolean isInMeeting) {
		this.isInMeeting = isInMeeting;
	}

	public MeetingManager getMeetingManager() {
		return this.meetingManager;
	}

	public Hologram getBtnHolo() {
		return this.btnHolo;
	}

	public ArrayList<PlayerInfo> getImpostersAlive() {
		return this.impostersAlive;
	}

	public DeadBodiesManager getDeadBodiesManager() {
		return this.deadBodiesManager;
	}

	public Integer getSabotageCooldown() {
		return this.sabotageCooldown;
	}

	public void setSabotageCooldown(Integer sabotageCooldown) {
		this.sabotageCooldown = sabotageCooldown;
	}

	public VentsManager getVentsManager() {
		return this.ventsManager;
	}

	public CamerasManager getCamerasManager() {
		return this.camerasManager;
	}

	public Location getCamerasLoc() {
		return this.camerasLoc;
	}

	public void setCamerasLoc(Location camerasLoc) {
		this.camerasLoc = camerasLoc;
	}

	public Boolean getEnableCameras() {
		return this.enableCameras;
	}

	public void setEnableCameras(Boolean enableCameras) {
		this.enableCameras = enableCameras;
	}

	public VisibilityManager getVisibilityManager() {
		return this.visibilityManager;
	}

	public Integer getImposterVision() {
		return this.imposterVision;
	}

	public void setImposterVision(Integer imposterVision) {
		this.imposterVision = imposterVision;
	}

	public Integer getCrewmateVision() {
		return this.crewmateVision;
	}

	public void setCrewmateVision(Integer crewmateVision) {
		this.crewmateVision = crewmateVision;
	}

	public BossBar getTasksBossBar() {
		return this.bossBar;
	}

	public Double getReportDistance() {
		return this.reportDistance;
	}

	public void setReportDistance(Double reportDistance) {
		this.reportDistance = reportDistance;
	}

	public Boolean getEnableReducedVision() {
		return this.enableReducedVision;
	}

	public void setEnableReducedVision(Boolean enableReducedVision) {
		this.enableReducedVision = enableReducedVision;
	}

	public ArrayList<JoinSign> getJoinSigns() {
		return this.joinSigns;
	}

	public Integer getProceedingTime() {
		return this.proceedingTime;
	}

	public void setProceedingTime(Integer proceedingTime) {
		this.proceedingTime = proceedingTime;
	}

	public Boolean getHideHologramsOutOfView() {
		return this.hideHologramsOutOfView;
	}

	public void setHideHologramsOutOfView(Boolean hideHologramsOutOfView) {
		this.hideHologramsOutOfView = hideHologramsOutOfView;
	}

	public DoorsManager getDoorsManager() {
		return this.doorsManager;
	}

	public Integer getDoorCloseTime() {
		return this.doorCloseTime;
	}

	public void setDoorCloseTime(Integer doorCloseTime) {
		this.doorCloseTime = doorCloseTime;
	}

	public Integer getDoorCooldown() {
		return this.doorCooldown;
	}

	public void setDoorCooldown(Integer doorCooldown) {
		this.doorCooldown = doorCooldown;
	}

	public ArrayList<ColorInfo> getColors_() {
		return this.colors_;
	}

	public ColorSelectorInv getColorSelectorInv() {
		return this.colorSelectorInv;
	}

	public ArrayList<PlayerInfo> getScanQueue() {
		return this.scanQueue;
	}

	public Boolean getEnableVisualTasks() {
		return this.enableVisualTasks;
	}

	public void setEnableVisualTasks(Boolean enableVisualTasks) {
		this.enableVisualTasks = enableVisualTasks;
	}

	public ArrayList<Block> getPrimeShieldsBlocks() {
		return this.primeShieldsBlocks;
	}

	public long getAsteroidsLastTime() {
		return this.asteroidsLastTime;
	}

	public void setAsteroidsLastTime(long asteroidsLastTime) {
		this.asteroidsLastTime = asteroidsLastTime;
	}

	public Boolean getConfirmEjects() {
		return this.confirmEjects;
	}

	public void setConfirmEjects(Boolean confirmEjects) {
		this.confirmEjects = confirmEjects;
	}

	public Location getWaitingLobby() {
		return this.waitingLobby;
	}

	public void setWaitingLobby(Location waitingLobby) {
		this.waitingLobby = waitingLobby;
	}

	public Boolean getMoveMapWithPlayer() {
		return this.moveMapWithPlayer;
	}

	public void setMoveMapWithPlayer(Boolean moveMapWithPlayer) {
		this.moveMapWithPlayer = moveMapWithPlayer;
	}

	public Integer getGameTimerActive() {
		return this.gameTimerActive;
	}

	public void setGameTimerActive(Integer gameTimerActive) {
		this.gameTimerActive = gameTimerActive;
	}

	public ArrayList<Player> get_playersToDelete() {
		return this._playersToDelete;
	}

	public VitalsManager getVitalsManager() {
		return this.vitalsManager;
	}

	public Location getVitalsLoc() {
		return this.vitalsLoc;
	}

	public void setVitalsLoc(Location vitalsLoc) {
		this.vitalsLoc = vitalsLoc;
	}

	public Boolean getDynamicImposters() {
		return this.dynamicImposters;
	}

	public void setDynamicImposters(Boolean dynamicImposters) {
		this.dynamicImposters = dynamicImposters;
	}

	public Boolean getEnableRedstone() {
		return enableRedstone;
	}

	public void setEnableRedstone(Boolean enableRedstone) {
		this.enableRedstone = enableRedstone;
	}
}
