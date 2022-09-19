package com.nktfh100.amongus.info;

import com.comphenix.protocol.wrappers.Vector3F;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.VisibilityManager;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.nktfh100.AmongUs.enums.CosmeticType;
import com.nktfh100.AmongUs.enums.GameState;
import com.nktfh100.AmongUs.enums.SabotageType;
import com.nktfh100.AmongUs.main.Main;
import com.nktfh100.AmongUs.managers.MessagesManager;
import com.nktfh100.AmongUs.managers.StatsManager;
import com.nktfh100.AmongUs.utils.ScoreboardSign;
import com.nktfh100.AmongUs.utils.Utils;

import net.minecraft.server.v1_16_R3.EntityPlayer;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;

import java.util.ArrayList;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class PlayerInfo {
	private Player player;
	private Arena arena = null;
	private Integer joinedId = Integer.valueOf(0);
	private Boolean isInGame = Boolean.valueOf(false);
	private Boolean isGhost = Boolean.valueOf(false);

	private ColorInfo color;
	private ColorInfo preferredColor = null;
	private Boolean isImposter = Boolean.valueOf(false);
	private Integer meetingsLeft = Integer.valueOf(1);
	private Integer killCoolDown = Integer.valueOf(0);
	private Integer sabotageCoolDown = Integer.valueOf(30);
	private Boolean canSabotage = Boolean.valueOf(false);
	private Boolean canReportBody = Boolean.valueOf(false);
	private Integer vision = Integer.valueOf(10);

	private Boolean isMapInOffHand = Boolean.valueOf(true);

	private Boolean isInVent = Boolean.valueOf(false);
	private VentGroup ventGroup = null;
	private Vent vent = null;

	private Boolean isInCameras = Boolean.valueOf(false);
	private Camera activeCamera = null;

	private DeadBody playerDiedTemp = null;
	private Location playerCamLocTemp = null;

	private ScoreboardSign scoreboard;

	private Scoreboard board;
	private Objective objective;
	private short currentMapId = -1;

	private int outOfAreaTimeOut;

	private String originalPlayerListName;

	private Hologram imposterHolo = null;

	private BossBar killCooldownBossBar = null;

	private Boolean killCoolDownPaused = Boolean.valueOf(false);

	private ArrayList<FakeBlock> tempReducedVisBlocks = new ArrayList<>();
	private ArrayList<Player> playersHidden = new ArrayList<>();

	private Integer fakePlayerId = Integer.valueOf((int) (Math.random() * 2.147483647E9D));
	private UUID fakePlayerUUID = UUID.randomUUID();

	private String textureValue = "";
	private String textureSignature = "";

	private ItemStack head;

	private FakePlayer fakePlayer;

	private Integer useItemState = Integer.valueOf(0);
	private TaskPlayer useItemTask = null;
	private SabotageTask useItemSabotage = null;
	private Vent useItemVent = null;
	private Double useDistance = Double.valueOf(2.5D);

	private Boolean isScanning = Boolean.valueOf(false);
	private ArrayList<FakeArmorStand> scanArmorStands = new ArrayList<>();

	private StatsManager statsManager = null;

	private long portalCooldown = System.currentTimeMillis();

	private GameMode gameModeBefore = GameMode.SURVIVAL;
	private Float expBefore = Float.valueOf(0.0F);
	private ItemStack[] inventoryBefore = null;
	private ItemStack[] inventoryExtraBefore = null;
	private ItemStack[] inventoryArmorBefore = null;

	private Boolean isAlreadyRunning;

	private String activeKey;

	public PlayerInfo(Player player) {
		this.isAlreadyRunning = false;

		this.activeKey = "";
		this.player = player;
		this.originalPlayerListName = player.getPlayerListName();
		final PlayerInfo pInfo = this;
		new BukkitRunnable() {
			public void run() {
				try {
					pInfo.setHead(Utils.getHead(pInfo.getPlayer().getName()));
					if (Main.getPlugin().getServer().getOnlineMode()) {
						EntityPlayer playerNMS = ((CraftPlayer) player).getHandle();
						GameProfile profile = playerNMS.getProfile();
						Property property = profile.getProperties().get("textures").iterator().next();

						String texture = property.getValue();
						String signature = property.getSignature();
						pInfo.setTextureValue(texture);
						pInfo.setTextureSignature(signature);
						return;
					}

					String[] textures = Utils.getSkinData(pInfo.getPlayer().getName());
					pInfo.setTextureValue(textures[0]);
					pInfo.setTextureSignature(textures[1]);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.runTaskAsynchronously(Main.getPlugin());
		this.scanArmorStands = new ArrayList<>();
		for (int i = 0; i < 4; i++)
			this.scanArmorStands.add(new FakeArmorStand(pInfo, player.getLocation(), new Vector3F(90.0F, 0.0F, 0.0F), null));
		this.statsManager = new StatsManager(this);
		if (!Main.getConfigManager().getBungeecord() && Main.getConfigManager().getEnableLobbyScoreboard())
			new BukkitRunnable() {
				public void run() {
					if (pInfo != null && !pInfo.getIsIngame().booleanValue())
						pInfo._setMainLobbyScoreboard();
				}
			}.runTaskLater(Main.getPlugin(), 20L);
	}

	public void _setPlayer(Player p) {
		this.player = p;
	}

	public void _setMainLobbyScoreboard() {
		if (Main.getConfigManager().getScoreboardUsePackets().booleanValue()) {
			if (this.scoreboard != null) {
				this.scoreboard.destroy();
			}
			this.scoreboard = new ScoreboardSign(this.player, Main.getMessagesManager().getScoreboard("title"));
			this.scoreboard.create();
			setScoreBoard();
		} else {
			this.board = null;
			this.objective = null;
			this.board = Bukkit.getScoreboardManager().getNewScoreboard();
			this.objective = this.board.registerNewObjective(this.player.getName(), "dummy", Main.getMessagesManager().getScoreboard("title"));
			this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
			this.player.setScoreboard(this.board);
			setScoreBoard();
		}
	}

	public String getCustomName() {
		if (this.color != null && this.arena != null) {
			return Main.getMessagesManager().getGameMsg("tabName", getArena(), this.player.getName(), "" + this.color.getChatColor(), this.color.getName(), null);
		}
		return this.player.getName();
	}

	public void initGame(Arena arena, Integer joinedId) {
		this.arena = arena;
		this.joinedId = joinedId;
		this.isImposter = Boolean.valueOf(false);
		this.isInGame = Boolean.valueOf(true);
		this.killCoolDownPaused = Boolean.valueOf(false);
		this.isScanning = Boolean.valueOf(false);
		if (Main.getConfigManager().getSaveInventory().booleanValue()) {
			setGameModeBefore(this.player.getGameMode());
			setExpBefore(Float.valueOf(this.player.getExp()));
			setInventoryBefore((ItemStack[]) this.player.getInventory().getStorageContents().clone());
			setInventoryExtraBefore((ItemStack[]) this.player.getInventory().getExtraContents().clone());
			setInventoryArmorBefore((ItemStack[]) this.player.getInventory().getArmorContents().clone());
		}
		for (FakeArmorStand fakeArmorStand : this.scanArmorStands) {
			fakeArmorStand.resetAllShownTo();
		}
		String title = Main.getMessagesManager().getScoreboard("title");
		if (Main.getConfigManager().getScoreboardUsePackets().booleanValue()) {
			if (this.scoreboard != null) {
				this.scoreboard.destroy();
			}
			this.scoreboard = new ScoreboardSign(this.player, title);
			this.scoreboard.create();
			setScoreBoard();
		} else {
			this.board = null;
			this.board = Bukkit.getScoreboardManager().getNewScoreboard();
			this.objective = this.board.registerNewObjective(this.player.getName(), "dummy", title);
			this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
			setScoreBoard();
			this.player.setScoreboard(this.board);
		}
	}

	public void updateVisionBlocks(Location newLoc) {
		if (this.isAlreadyRunning.booleanValue() || getIsInCameras().booleanValue())
			return;
		if (this.arena != null && this.arena.getEnableReducedVision().booleanValue() && Main.getConfigManager() != null && Main.getConfigManager().getViewGlassMat() != Material.AIR) {
			this.isAlreadyRunning = Boolean.valueOf(true);
			Location loc = new Location(newLoc.getWorld(), newLoc.getBlockX(), newLoc.getBlockY(), newLoc.getBlockZ());
			loc.add(0.0D, -1.0D, 0.0D);
			int height = 5;
			if (loc.getBlock().isEmpty())
				loc.add(0.0D, -1.0D, 0.0D);
			ArrayList<Location> newLocs = Utils.generateHollowCircle(loc, this.vision, height);
			ArrayList<FakeBlock> newFakeBlocks = new ArrayList<>();
			for (Location loc_ : newLocs) {
				if (loc_.getBlock().getType() == Material.AIR) {
					FakeBlock fb = new FakeBlock(loc_, loc_.getBlock().getType(), Main.getConfigManager().getViewGlassMat(), WrappedBlockData.createData(loc_.getBlock().getBlockData()));
					newFakeBlocks.add(fb);
					fb.sendNewBlock(this.player);
				}
			}
			for (FakeBlock oldFB : this.tempReducedVisBlocks) {
				Boolean isOk = Boolean.valueOf(true);
				for (FakeBlock newFB : newFakeBlocks) {
					if (newFB.getLoc().getBlockX() == oldFB.getLoc().getBlockX() && newFB.getLoc().getBlockY() == oldFB.getLoc().getBlockY()
							&& newFB.getLoc().getBlockZ() == oldFB.getLoc().getBlockZ()) {
						isOk = Boolean.valueOf(false);
						break;
					}
				}
				if (isOk.booleanValue()) {
					Boolean send = Boolean.valueOf(true);
					for (DoorGroup dg : this.arena.getDoorsManager().getDoorGroups()) {
						if (dg.getCloseTimer() > 0)
							for (Door door : dg.getDoors()) {
								if (door.getBlocks_().contains(oldFB.getBlock())) {
									send = Boolean.valueOf(false);
									break;
								}
							}
					}
					if (send.booleanValue())
						oldFB.sendOldBlock(this.player);
				}
			}
			this.tempReducedVisBlocks = newFakeBlocks;
			this.isAlreadyRunning = Boolean.valueOf(false);
		}
	}

	public void removeVisionBlocks() {
		for (FakeBlock oldFB : this.tempReducedVisBlocks)
			oldFB.sendOldBlock(this.player);
		this.tempReducedVisBlocks.clear();
	}

	public void startGame(Boolean isImposter) {
		this.isImposter = isImposter;
		if (isImposter.booleanValue()) {
			this.killCooldownBossBar = Bukkit.createBossBar(Main.getMessagesManager().getGameMsg("killCooldownBossBar", getArena(), ""), BarColor.RED, BarStyle.SOLID, new org.bukkit.boss.BarFlag[0]);
			this.killCooldownBossBar.setProgress(1.0D);
			this.killCooldownBossBar.addPlayer(this.player);
		}
		if (Main.getConfigManager().getScoreboardUsePackets().booleanValue()) {
			if (this.scoreboard != null)
				this.scoreboard.destroy();
			this.scoreboard = new ScoreboardSign(this.player, Main.getMessagesManager().getScoreboard("title"));
			this.scoreboard.create();
			setScoreBoard();
			this.scoreboard.addTeam("crewmates", ChatColor.WHITE, "never");
			this.scoreboard.addTeam("imposters", ChatColor.RED, "never");
			this.scoreboard.addTeam("ghosts", ChatColor.GRAY, "for_other_teams");
			addPlayerToTeam(getPlayer(), isImposter.booleanValue() ? "imposters" : "crewmates");
		} else {
			this.board = null;
			this.objective = null;
			this.board = Bukkit.getScoreboardManager().getNewScoreboard();
			this.objective = this.board.registerNewObjective(this.player.getName(), "dummy", Main.getMessagesManager().getScoreboard("title"));
			this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
			setScoreBoard();
			Team crewmates = this.board.registerNewTeam("crewmates");
			crewmates.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
			crewmates.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
			crewmates.setAllowFriendlyFire(false);
			crewmates.setCanSeeFriendlyInvisibles(false);
			crewmates.setColor(ChatColor.WHITE);
			crewmates.setPrefix("");
			Team imposters = this.board.registerNewTeam("imposters");
			imposters.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
			imposters.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
			imposters.setAllowFriendlyFire(false);
			imposters.setCanSeeFriendlyInvisibles(false);
			imposters.setColor(ChatColor.RED);
			imposters.setPrefix("");
			Team ghosts = this.board.registerNewTeam("ghosts");
			ghosts.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OTHER_TEAMS);
			ghosts.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
			ghosts.setAllowFriendlyFire(false);
			ghosts.setCanSeeFriendlyInvisibles(true);
			ghosts.setColor(ChatColor.GRAY);
			ghosts.setPrefix("");
			addPlayerToTeam(getPlayer(), isImposter.booleanValue() ? "imposters" : "crewmates");
			this.player.setScoreboard(this.board);
		}
		this.canReportBody = Boolean.valueOf(false);
	}

	public void meetingStarted() {
		if (Main.getConfigManager().getScoreboardUsePackets().booleanValue()) {
			this.scoreboard.updateTeamNameTag("crewmates", ChatColor.WHITE, "always");
			this.scoreboard.updateTeamNameTag("imposters", ChatColor.RED, "always");
		} else {
			if (this.board == null)
				return;
			Team crewmates = this.board.getTeam("crewmates");
			if (crewmates == null) {
				crewmates = this.board.registerNewTeam("crewmates");
				crewmates.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
				crewmates.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
				crewmates.setAllowFriendlyFire(false);
				crewmates.setCanSeeFriendlyInvisibles(false);
				crewmates.setColor(ChatColor.WHITE);
				crewmates.setPrefix("");
				return;
			}
			crewmates.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
			Team imposters = this.board.getTeam("imposters");
			if (imposters == null) {
				imposters = this.board.registerNewTeam("imposters");
				imposters.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
				imposters.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
				imposters.setAllowFriendlyFire(false);
				imposters.setCanSeeFriendlyInvisibles(false);
				imposters.setColor(ChatColor.RED);
				imposters.setPrefix("");
				return;
			}
			imposters.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
		}
	}

	private void setScoreBoard() {
		MessagesManager messagesManager = Main.getMessagesManager();
		this.activeKey = getScoreBoardKey();
		Boolean usePackets = Main.getConfigManager().getScoreboardUsePackets();

		ArrayList<String> lines = new ArrayList<>();
		int i = 0;
		int score = 99;
		for (String line : messagesManager.getScoreBoardLines(this.activeKey)) {
			if (line.contains("%tasks%")) {
				for (TaskPlayer tp : getArena().getTasksManager().getTasksForPlayer(this.player)) {
					String line_ = messagesManager.getScoreboardTaskLine(this.arena, tp);
					if (usePackets.booleanValue()) {
						lines.add(line_);
						continue;
					}
					Team team_ = registerTeam(score);
					team_.setPrefix(line_);
					score--;
				}

				i++;
				continue;
			}
			String line_ = messagesManager.getScoreboardLine(getScoreBoardKey(), i, this);
			if (usePackets.booleanValue()) {
				lines.add(line_);
			} else {
				Team team_ = registerTeam(score);
				team_.setPrefix(line_);
				score--;
			}
			i++;
		}

		if (usePackets.booleanValue())
			this.scoreboard.setLines(lines);
	}

	public void meetingEnded() {
		if (Main.getConfigManager().getScoreboardUsePackets().booleanValue()) {
			this.scoreboard.updateTeamNameTag("imposters", ChatColor.RED, "never");
			this.scoreboard.updateTeamNameTag("crewmates", ChatColor.WHITE, "never");
		} else {
			Team crewmates = this.board.getTeam("crewmates");
			if (crewmates == null) {
				crewmates = this.board.registerNewTeam("crewmates");
				crewmates.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
				crewmates.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
				crewmates.setAllowFriendlyFire(false);
				crewmates.setCanSeeFriendlyInvisibles(false);
				crewmates.setColor(ChatColor.WHITE);
				crewmates.setPrefix("");
				return;
			}
			crewmates.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
			Team imposters = this.board.getTeam("imposters");
			if (imposters == null) {
				imposters = this.board.registerNewTeam("imposters");
				imposters.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
				imposters.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
				imposters.setAllowFriendlyFire(false);
				imposters.setCanSeeFriendlyInvisibles(false);
				imposters.setColor(ChatColor.RED);
				imposters.setPrefix("");
				return;
			}
			imposters.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
		}
	}

	public void createImposterHolo() {
		this.imposterHolo = HologramsAPI.createHologram(Main.getPlugin(), getPlayer().getLocation().add(0.0D, 2.8D, 0.0D));
		this.imposterHolo.appendItemLine(Utils.createItem(Material.RED_CONCRETE, ""));
		VisibilityManager visManager = this.imposterHolo.getVisibilityManager();
		visManager.setVisibleByDefault(false);
		for (PlayerInfo pInfo1 : this.arena.getGameImposters()) {
			if (pInfo1 != this)
				visManager.showTo(pInfo1.getPlayer());
		}
	}

	public void addPlayerToTeam(Player player, String team) {
		if (player == null)
			return;
		if (Main.getConfigManager().getScoreboardUsePackets().booleanValue()) {
			if (this.scoreboard != null)
				this.scoreboard.addPlayerToTeam(team, player);
		} else if (this.board != null && this.board.getTeam(team) != null) {
			this.board.getTeam(team).addPlayer((OfflinePlayer) player);
		}
	}

	public void removePlayerFromTeam(Player player, String team) {
		if (player == null)
			return;
		if (Main.getConfigManager().getScoreboardUsePackets().booleanValue()) {
			if (this.scoreboard != null)
				this.scoreboard.removePlayerFromTeam(team, player);
		} else if (this.board != null && this.board.getTeam(team) != null) {
			this.board.getTeam(team).removePlayer((OfflinePlayer) player);
		}
	}

	public void updateScoreBoard() {
		Boolean usePackets = Main.getConfigManager().getScoreboardUsePackets();
		if (this.scoreboard == null && usePackets.booleanValue())
			return;
		if (this.board == null && !usePackets.booleanValue()) {
			return;
		}
		Boolean isCommsDisabled = Boolean.valueOf(false);
		if (getIsIngame().booleanValue() && (this.arena.getGameState() == GameState.RUNNING || this.arena.getGameState() == GameState.FINISHING)
				&& this.arena.getSabotageManager().getIsSabotageActive().booleanValue() && this.arena.getSabotageManager().getActiveSabotage().getType() == SabotageType.COMMUNICATIONS) {
			isCommsDisabled = Boolean.valueOf(true);
			if (!usePackets.booleanValue()) {
				for (Team team : this.board.getTeams()) {
					if (team.getName().startsWith("team")) {
						for (String entry : team.getEntries()) {
							team.removeEntry(entry);
							this.board.resetScores(entry);
						}
						team.unregister();
					}
				}
			}
		}

		ArrayList<String> lines = new ArrayList<>();
		int i = 0;
		int score = 99;
		MessagesManager messagesManager = Main.getMessagesManager();
		for (String line : messagesManager.getScoreBoardLines(getScoreBoardKey())) {
			if (line.contains("%tasks%")) {
				if (!isCommsDisabled.booleanValue()) {
					for (TaskPlayer tp : getArena().getTasksManager().getTasksForPlayer(getPlayer())) {
						String line_ = messagesManager.getScoreboardTaskLine(getArena(), tp);
						if (usePackets.booleanValue()) {
							lines.add(line_);
							continue;
						}
						if (this.board.getTeam("team" + score) == null) {
							registerTeam(score);
						}
						this.board.getTeam("team" + score).setPrefix(line_);
						score--;
					}
				} else {

					String line_ = ChatColor.RED + "" + ChatColor.BOLD + Main.getMessagesManager().getSabotageTitle(SabotageType.COMMUNICATIONS);
					if (usePackets.booleanValue()) {
						lines.add(line_);
					} else {
						if (this.board.getTeam("team" + score) == null) {
							registerTeam(score);
						}
						this.board.getTeam("team" + score).setPrefix(line_);
						score--;
					}
				}
				i++;
				continue;
			}
			String line_ = messagesManager.getScoreboardLine(getScoreBoardKey(), i, this);
			if (usePackets.booleanValue()) {
				lines.add(line_);
			} else {
				if (this.board.getTeam("team" + score) == null) {
					registerTeam(score);
				}
				this.board.getTeam("team" + score).setPrefix(line_);
				score--;
			}
			i++;
		}

		if (usePackets.booleanValue()) {
			this.scoreboard.setLines(lines);
		}
		if (this.activeKey != getScoreBoardKey()) {
			if (!usePackets.booleanValue()) {
				for (Team team : this.board.getTeams()) {
					if (team.getName().startsWith("team")) {
						for (String entry : team.getEntries()) {
							team.removeEntry(entry);
							this.board.resetScores(entry);
						}
						team.unregister();
					}
				}
			}
			this.activeKey = getScoreBoardKey();
			updateScoreBoard();
		}
	}

	private String getScoreBoardKey() {
		if (getIsIngame().booleanValue()) {
			if (getArena().getGameState() == GameState.RUNNING || getArena().getGameState() == GameState.FINISHING) {
				String linesKey = getIsImposter().booleanValue() ? "imposter" : "crewmate";
				if (isGhost().booleanValue()) {
					linesKey = getIsImposter().booleanValue() ? "dead-imposter" : "dead-crewmate";
				}
				return linesKey;
			}
			return "waiting-lobby";
		}

		return "main-lobby";
	}

	private Team registerTeam(int score) {
		Team team_ = this.board.registerNewTeam("team" + score);
		String entry = Utils.getRandomColors();
		team_.addEntry(entry);
		this.objective.getScore(entry).setScore(score);
		return team_;
	}

	public void giveArmor() {
		this.player.getEquipment().setHelmet(getHelmet());
		this.player.getEquipment().setChestplate(getChestplate());
		this.player.getEquipment().setLeggings(getLeggings());
		this.player.getEquipment().setBoots(getBoots());
	}

	public ItemStack getHelmet() {
		if (Main.getConfigManager().getEnableGlassHelmet().booleanValue()) {
			return new ItemStack(this.color.getGlass(), 1);
		}
		return Utils.getArmorColor(this.color, Material.LEATHER_HELMET);
	}

	public ItemStack getChestplate() {
		return Utils.getArmorColor(this.color, Material.LEATHER_CHESTPLATE);
	}

	public ItemStack getLeggings() {
		return Utils.getArmorColor(this.color, Material.LEATHER_LEGGINGS);
	}

	public ItemStack getBoots() {
		return Utils.getArmorColor(this.color, Material.LEATHER_BOOTS);
	}

	public void setUseItemState(Integer useItemState, Boolean updateItem) {
		this.useItemState = useItemState;
		if (updateItem.booleanValue()) {
			ItemInfoContainer useItem = Main.getItemsManager().getItem("use");
			if (useItemState == 0) {
				this.player.getInventory().setItem(useItem.getSlot(), useItem.getItem().getItem());
			} else {
				this.player.getInventory().setItem(useItem.getSlot(), useItem.getItem2().getItem());
			}
		}
	}

	public void updateUseItemState(Location loc) {
		if (this.arena == null) {
			return;
		}

		if (this.arena.getMeetingButton() != null && !isGhost().booleanValue() && Utils.isInsideCircle(this.arena.getMeetingButton(), this.useDistance, loc) != 2) {
			setUseItemState(Integer.valueOf(1), Boolean.valueOf(true));

			return;
		}

		if (this.arena.getCamerasLoc() != null && Utils.isInsideCircle(this.arena.getCamerasLoc(), this.useDistance, loc) != 2) {
			setUseItemState(Integer.valueOf(5), Boolean.valueOf(true));

			return;
		}

		if (this.arena.getVitalsLoc() != null && Utils.isInsideCircle(this.arena.getVitalsLoc(), this.useDistance, loc) != 2) {
			setUseItemState(Integer.valueOf(6), Boolean.valueOf(true));

			return;
		}

		if (!getIsImposter().booleanValue()) {

			for (TaskPlayer task : this.arena.getTasksManager().getTasksForPlayer(getPlayer())) {
				if (!task.getIsDone().booleanValue() && Utils.isInsideCircle(task.getActiveTask().getLocation(), this.useDistance, loc) != 2) {
					setUseItemTask(task);
					setUseItemState(Integer.valueOf(2), Boolean.valueOf(true));

					return;
				}
			}
		} else {
			for (VentGroup vg : this.arena.getVentsManager().getVentGroups()) {
				for (Vent v : vg.getVents()) {
					if (Utils.isInsideCircle(v.getLoc(), this.useDistance, loc) != 2) {
						setUseItemVent(v);
						setUseItemState(Integer.valueOf(4), Boolean.valueOf(true));

						return;
					}
				}
			}
		}

		if (this.arena.getSabotageManager().getIsSabotageActive().booleanValue() && !isGhost().booleanValue()) {
			SabotageArena activeSabotage = this.arena.getSabotageManager().getActiveSabotage();
			if (Utils.isInsideCircle(activeSabotage.getTask1().getLocation(), this.useDistance, loc) != 2) {
				setUseItemSabotage(activeSabotage.getTask1());
				setUseItemState(Integer.valueOf(3), Boolean.valueOf(true));
				return;
			}
			if (activeSabotage.getTask2() != null && Utils.isInsideCircle(activeSabotage.getTask2().getLocation(), this.useDistance, loc) != 2) {
				setUseItemSabotage(activeSabotage.getTask2());
				setUseItemState(Integer.valueOf(3), Boolean.valueOf(true));

				return;
			}
		}

		if (getUseItemState() != 0) {
			setUseItemState(Integer.valueOf(0), Boolean.valueOf(true));
		}
	}

	public void setCanReportBody(Boolean is, DeadBody db) {
		if (this.isInGame.booleanValue() && !this.isGhost.booleanValue()) {
			ItemInfoContainer reportItem = Main.getItemsManager().getItem("report");
			if (is.booleanValue()) {
				this.playerDiedTemp = db;
				String playerName = db.getPlayer().getName();
				String playerColorName = db.getColor().getName();
				ChatColor chatColor = db.getColor().getChatColor();
				this.player.getInventory().setItem(1,
						Utils.setItemName(Utils.getHead(db.getPlayer().getName()), reportItem.getItem2().getTitle(playerName, playerColorName, "" + chatColor, null, null),
								reportItem.getItem2().getLore(playerName, playerColorName, "" + chatColor, null, null)));
			} else {
				this.playerDiedTemp = null;
				this.player.getInventory().setItem(1, reportItem.getItem().getItem());
			}
		}
		this.canReportBody = is;
	}

	public void leaveGame() {
		if (this.isImposter.booleanValue()) {
			this.killCooldownBossBar.removePlayer(this.player);
			this.killCooldownBossBar = null;
		}
		if (this.imposterHolo != null) {
			this.imposterHolo.delete();
			this.imposterHolo = null;
		}
		this.arena = null;
		this.isInGame = Boolean.valueOf(false);
		this.canReportBody = Boolean.valueOf(false);
		this.currentMapId = -1;
		this.joinedId = Integer.valueOf(0);
		setIsGhost(Boolean.valueOf(false));
		this.isScanning = Boolean.valueOf(false);
		for (FakeArmorStand fakeArmorStand : this.scanArmorStands) {
			fakeArmorStand.resetAllShownTo();
		}
		this.useItemState = Integer.valueOf(0);
		if (Main.getConfigManager().getScoreboardUsePackets().booleanValue()) {
			this.scoreboard.destroy();
		} else {
			if (this.board != null) {
				for (Team team : this.board.getTeams()) {
					for (String entry : team.getEntries()) {
						team.removeEntry(entry);
						this.board.resetScores(entry);
					}
					team.unregister();
				}
			}
			if (this.objective != null) {
				this.objective.unregister();
			}
			this.board = null;
			this.objective = null;
			this.player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
		}
		if (!Main.getConfigManager().getBungeecord().booleanValue() && Main.getConfigManager().getEnableLobbyScoreboard().booleanValue()) {
			_setMainLobbyScoreboard();
			updateScoreBoard();
		}
		if (Main.getConfigManager().getSaveInventory().booleanValue()) {
			this.player.setGameMode(this.gameModeBefore);
			this.player.setExp(this.expBefore.floatValue());
			this.player.getInventory().setContents(this.inventoryBefore);
			this.player.getInventory().setExtraContents(this.inventoryExtraBefore);
			this.player.getInventory().setArmorContents(this.inventoryArmorBefore);
		}
	}

	public void teleportImposterHolo() {
		if (getImposterHolo() != null) {
			getImposterHolo().teleport(getPlayer().getLocation().add(0.0D, 2.8D, 0.0D));
		}
	}

	public void setKillCoolDown(Integer killCoolDown) {
		if (this.killCoolDown > 0 && killCoolDown == 0) {
			this.killCooldownBossBar.removePlayer(this.player);
		} else if (this.killCoolDown == 0 && killCoolDown > 0) {
			this.killCooldownBossBar.addPlayer(this.player);
		}
		Integer maxSecs = this.arena.getKillCooldown();
		double progress = killCoolDown / maxSecs;

		if (progress >= 0.0D && progress <= 1.0D) {
			this.killCooldownBossBar.setProgress(progress);
			this.killCooldownBossBar.setTitle(Main.getMessagesManager().getGameMsg("killCooldownBossBar", this.arena, "" + killCoolDown));
		}
		if (!this.arena.getIsInMeeting().booleanValue() && !isGhost().booleanValue() && !getIsInVent().booleanValue() && !getIsInCameras().booleanValue()) {
			giveKillItem(killCoolDown);
		}

		this.killCoolDown = killCoolDown;
	}

	public void sendTitle(String title, String subTitle) {
		if (title.isEmpty() && subTitle.isEmpty()) {
			return;
		}
		this.player.sendTitle(title, subTitle, 15, 80, 15);
	}

	public void sendTitle(String title, String subTitle, Integer fadeIn, Integer stay, Integer fadeOut) {
		if (title.isEmpty() && subTitle.isEmpty()) {
			return;
		}
		this.player.sendTitle(title, subTitle, fadeIn, stay, fadeOut);
	}

	public void giveKillItem(Integer killCoolDown_) {
		String killCoolDownStr = killCoolDown_.toString();
		ItemInfoContainer killItem = Main.getItemsManager().getItem("kill");
		ItemStack item_ = (killCoolDown_ == 0) ? killItem.getItem2().getItem(killCoolDownStr, "") : killItem.getItem().getItem(killCoolDownStr, "");
		if (killCoolDown_ == 0 && Main.getIsPlayerPoints().booleanValue()) {
			if (getStatsManager().getSelectedCosmetic(CosmeticType.KILL_SWORD) != null && !getStatsManager().getSelectedCosmetic(CosmeticType.KILL_SWORD).isEmpty()) {
				item_.setType(Main.getCosmeticsManager().getCosmeticItem(CosmeticType.KILL_SWORD, getStatsManager().getSelectedCosmetic(CosmeticType.KILL_SWORD)).getMat());
			} else {
				item_.setType(Main.getCosmeticsManager().getCosmeticItem(CosmeticType.KILL_SWORD, Main.getCosmeticsManager().getDefaultCosmetic(CosmeticType.KILL_SWORD)).getMat());
			}
		}

		this.player.getInventory().setItem(killItem.getSlot(), item_);
	}

	public void delete() {
		this.arena = null;
		this.joinedId = null;
		this.isInGame = null;
		this.isGhost = null;
		this.color = null;
		this.isImposter = null;
		this.meetingsLeft = null;
		this.killCoolDown = null;
		this.sabotageCoolDown = null;
		this.canSabotage = null;
		this.canReportBody = null;
		this.vision = null;
		this.isMapInOffHand = null;
		this.isInVent = null;
		this.ventGroup = null;
		this.vent = null;
		this.isInCameras = null;
		this.activeCamera = null;
		this.playerDiedTemp = null;
		this.playerCamLocTemp = null;
		this.scoreboard = null;
		this.board = null;
		this.objective = null;
		this.imposterHolo = null;
		this.killCooldownBossBar = null;
		this.killCoolDownPaused = null;
		this.tempReducedVisBlocks = null;
		this.playersHidden = null;
		this.fakePlayerId = null;
		this.fakePlayerUUID = null;
		this.textureValue = null;
		this.textureSignature = null;
		this.head = null;
		this.fakePlayer = null;
		this.useItemState = null;
		this.useItemTask = null;
		this.useItemSabotage = null;
		this.useItemVent = null;
		this.useDistance = null;
		this.scanArmorStands = null;
		this.preferredColor = null;
		this.statsManager.delete();
		this.statsManager = null;
	}

	public void setColor(ColorInfo color) {
		this.color = color;
	}

	public Boolean isGhost() {
		return this.isGhost;
	}

	public void setIsGhost(Boolean is) {
		this.isGhost = is;
	}

	public ColorInfo getColor() {
		return this.color;
	}

	public Boolean getCanSabotage() {
		return this.canSabotage;
	}

	public void setCanSabotage(Boolean canSabotage) {
		this.canSabotage = canSabotage;
	}

	public Integer getKillCoolDown() {
		return this.killCoolDown;
	}

	public Integer getSabotageCoolDown() {
		return this.sabotageCoolDown;
	}

	public void setSabotageCoolDown(Integer sabotageCoolDown) {
		this.sabotageCoolDown = sabotageCoolDown;
	}

	public Integer getMeetingsLeft() {
		return this.meetingsLeft;
	}

	public void setMeetingsLeft(Integer meetingsLeft) {
		this.meetingsLeft = meetingsLeft;
	}

	public String getOriginalPlayerListName() {
		return this.originalPlayerListName;
	}

	public Hologram getImposterHolo() {
		return this.imposterHolo;
	}

	public Boolean getCanReportBody() {
		return this.canReportBody;
	}

	public DeadBody getPlayerDiedTemp() {
		return this.playerDiedTemp;
	}

	public void setMapId(short id) {
		this.currentMapId = id;
	}

	public short getMapId() {
		return this.currentMapId;
	}

	public Player getPlayer() {
		return this.player;
	}

	public Arena getArena() {
		return this.arena;
	}

	public Boolean getIsImposter() {
		return this.isImposter;
	}

	public int getOutOfAreaTimeOut() {
		return this.outOfAreaTimeOut;
	}

	public void setOutOfAreaTimeOut(int i) {
		this.outOfAreaTimeOut = i;
	}

	public Boolean getIsIngame() {
		return this.isInGame;
	}

	public BossBar getKillCooldownBossBar() {
		return this.killCooldownBossBar;
	}

	public Boolean getIsInVent() {
		return this.isInVent;
	}

	public void setIsInVent(Boolean isInVent) {
		this.isInVent = isInVent;
	}

	public VentGroup getVentGroup() {
		return this.ventGroup;
	}

	public void setVentGroup(VentGroup ventGroup) {
		this.ventGroup = ventGroup;
	}

	public Vent getVent() {
		return this.vent;
	}

	public void setVent(Vent vent) {
		this.vent = vent;
	}

	public Boolean getIsInCameras() {
		return this.isInCameras;
	}

	public void setIsInCameras(Boolean isInCameras) {
		this.isInCameras = isInCameras;
	}

	public Camera getActiveCamera() {
		return this.activeCamera;
	}

	public void setActiveCamera(Camera activeCamera) {
		this.activeCamera = activeCamera;
	}

	public Location getPlayerCamLocTemp() {
		return this.playerCamLocTemp;
	}

	public void setPlayerCamLocTemp(Location playerCamLocTemp) {
		this.playerCamLocTemp = playerCamLocTemp;
	}

	public ArrayList<Player> getPlayersHidden() {
		return this.playersHidden;
	}

	public void setPlayersHidden(ArrayList<Player> playersHidden) {
		this.playersHidden = playersHidden;
	}

	public Integer getVision() {
		return this.vision;
	}

	public void setVision(Integer vision) {
		this.vision = vision;
	}

	public Boolean getIsMapInOffHand() {
		return this.isMapInOffHand;
	}

	public void setIsMapInOffHand(Boolean isMapInOffHand) {
		this.isMapInOffHand = isMapInOffHand;
	}

	public Integer getFakePlayerId() {
		return this.fakePlayerId;
	}

	public UUID getFakePlayerUUID() {
		return this.fakePlayerUUID;
	}

	public String getTextureValue() {
		return this.textureValue;
	}

	public String getTextureSignature() {
		return this.textureSignature;
	}

	public void setTextureValue(String value) {
		this.textureValue = value;
	}

	public void setTextureSignature(String value) {
		this.textureSignature = value;
	}

	public FakePlayer getFakePlayer() {
		return this.fakePlayer;
	}

	public void setFakePlayer(FakePlayer fakePlayer) {
		this.fakePlayer = fakePlayer;
	}

	public Boolean getKillCoolDownPaused() {
		return this.killCoolDownPaused;
	}

	public void setKillCoolDownPaused(Boolean killCoolDownPaused) {
		this.killCoolDownPaused = killCoolDownPaused;
	}

	public Integer getUseItemState() {
		return this.useItemState;
	}

	public TaskPlayer getUseItemTask() {
		return this.useItemTask;
	}

	public void setUseItemTask(TaskPlayer useItemTask) {
		this.useItemTask = useItemTask;
	}

	public SabotageTask getUseItemSabotage() {
		return this.useItemSabotage;
	}

	public void setUseItemSabotage(SabotageTask useItemSabotage) {
		this.useItemSabotage = useItemSabotage;
	}

	public Vent getUseItemVent() {
		return this.useItemVent;
	}

	public void setUseItemVent(Vent useItemVent) {
		this.useItemVent = useItemVent;
	}

	public Integer getJoinedId() {
		return this.joinedId;
	}

	public void setJoinedId(Integer joinedId) {
		this.joinedId = joinedId;
	}

	public Boolean getIsScanning() {
		return this.isScanning;
	}

	public void setIsScanning(Boolean isScanning) {
		this.isScanning = isScanning;
	}

	public ArrayList<FakeArmorStand> getScanArmorStands() {
		return this.scanArmorStands;
	}

	public ItemStack getHead() {
		return this.head;
	}

	public void setHead(ItemStack head) {
		this.head = head;
	}

	public ColorInfo getPreferredColor() {
		return this.preferredColor;
	}

	public void setPreferredColor(ColorInfo preferredColor) {
		this.preferredColor = preferredColor;
	}

	public StatsManager getStatsManager() {
		return this.statsManager;
	}

	public long getPortalCooldown() {
		return this.portalCooldown;
	}

	public void setPortalCooldown(long portalCooldown) {
		this.portalCooldown = portalCooldown;
	}

	public ItemStack[] getInventoryBefore() {
		return this.inventoryBefore;
	}

	public void setInventoryBefore(ItemStack[] inventoryBefore) {
		this.inventoryBefore = inventoryBefore;
	}

	public ItemStack[] getInventoryExtraBefore() {
		return this.inventoryExtraBefore;
	}

	public void setInventoryExtraBefore(ItemStack[] inventoryExtraBefore) {
		this.inventoryExtraBefore = inventoryExtraBefore;
	}

	public ItemStack[] getInventoryArmorBefore() {
		return this.inventoryArmorBefore;
	}

	public void setInventoryArmorBefore(ItemStack[] inventoryArmorBefore) {
		this.inventoryArmorBefore = inventoryArmorBefore;
	}

	public GameMode getGameModeBefore() {
		return this.gameModeBefore;
	}

	public void setGameModeBefore(GameMode gameModeBefore) {
		this.gameModeBefore = gameModeBefore;
	}

	public Float getExpBefore() {
		return this.expBefore;
	}

	public void setExpBefore(Float expBefore) {
		this.expBefore = expBefore;
	}
}
