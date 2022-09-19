package com.nktfh100.amongus.managers;

import com.nktfh100.AmongUs.enums.SabotageLength;
import com.nktfh100.AmongUs.enums.SabotageType;
import com.nktfh100.AmongUs.info.Arena;
import com.nktfh100.AmongUs.info.DoorGroup;
import com.nktfh100.AmongUs.info.ItemInfoContainer;
import com.nktfh100.AmongUs.info.PlayerInfo;
import com.nktfh100.AmongUs.info.SabotageArena;
import com.nktfh100.AmongUs.inventory.MeetingBtnInv;
import com.nktfh100.AmongUs.inventory.SabotageCommsInv;
import com.nktfh100.AmongUs.inventory.SabotageInvHolder;
import com.nktfh100.AmongUs.inventory.SabotageLightsInv;
import com.nktfh100.AmongUs.inventory.SabotageOxygenInv;
import com.nktfh100.AmongUs.inventory.SabotageReactorInv;
import com.nktfh100.AmongUs.main.Main;
import com.nktfh100.AmongUs.utils.Utils;
import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class SabotageManager {
	private Arena arena;
	private Integer sabotageTimer = Integer.valueOf(45);

	private HashMap<String, Integer> sabotageCoolDownTimer = new HashMap<>();

	private HashMap<String, BossBar> sabotageCooldownBossBar = new HashMap<>();
	private HashMap<String, Integer> sabotageCooldownBossBarMax = new HashMap<>();

	private SabotageArena activeSabotage = null;
	private Boolean isSabotageActive = Boolean.valueOf(false);
	private Boolean isTimerActive = Boolean.valueOf(false);
	private BossBar bossbar;
	private BukkitTask timerRunnable;
	private Boolean isTimerPaused = Boolean.valueOf(false);

	private SabotageInvHolder saboInvHolder = null;

	private ArrayList<Integer> oxygenCode = new ArrayList<>();

	public SabotageManager(Arena arena) {
		this.arena = arena;
		this.bossbar = Bukkit.createBossBar("", BarColor.RED, BarStyle.SEGMENTED_10, new org.bukkit.boss.BarFlag[0]);
		this.bossbar.setProgress(1.0D);
	}

	public void startSabotage(SabotageArena sabo) {
		if (this.isSabotageActive.booleanValue()) {
			return;
		}
		for (String uuid : this.sabotageCoolDownTimer.keySet()) {
			setSabotageCoolDownTimer(uuid, this.arena.getSabotageCooldown());
		}
		this.activeSabotage = sabo;
		String name = Main.getMessagesManager().getTaskName(getActiveSabotage().getType().toString());
		String saboTitle = Main.getMessagesManager().getGameMsg("sabotageTitle", this.arena, name);
		String saboSubTitle = Main.getMessagesManager().getGameMsg("sabotageSubTitle", this.arena, name);
		for (Player p : this.arena.getPlayers()) {
			this.bossbar.addPlayer(p);
			p.sendTitle(saboTitle, saboSubTitle, 15, 40, 15);
		}
		this.isSabotageActive = Boolean.valueOf(true);
		final SabotageManager manager = this;
		if (this.activeSabotage.getHasTimer().booleanValue()) {
			this.sabotageTimer = this.activeSabotage.getTimer();

			this.isTimerActive = Boolean.valueOf(true);
			if (this.timerRunnable != null) {
				this.timerRunnable.cancel();
			}
			this.timerRunnable = (new BukkitRunnable() {
				public void run() {
					manager.timerTick();
				}
			}).runTaskTimer(Main.getPlugin(), 20L, 20L);
		} else {
			this.isTimerActive = Boolean.valueOf(false);
		}
		updateBossBar();
		this.activeSabotage.showHolos();

		for (PlayerInfo pInfo : this.arena.getGameImposters()) {
			int s_ = 9;
			String uuid = pInfo.getPlayer().getUniqueId().toString();
			for (DoorGroup dg : this.arena.getDoorsManager().getDoorGroups()) {
				dg.setCooldownTimer(uuid, this.arena.getDoorCooldown());
				pInfo.getPlayer().getInventory().setItem(s_, this.arena.getDoorsManager().getSabotageDoorItem(pInfo.getPlayer(), dg.getId()));
				s_++;
			}
		}

		for (Player player : this.arena.getPlayers()) {
			Main.getSoundsManager().playSound("sabotageStarted", player, player.getLocation());
		}

		if (this.activeSabotage.getType() == SabotageType.LIGHTS) {

			this.saboInvHolder = (SabotageInvHolder) new SabotageLightsInv(getActiveSabotage());

			for (PlayerInfo pInfo : this.arena.getPlayersInfo()) {
				if (!pInfo.isGhost().booleanValue() && !pInfo.getIsImposter().booleanValue()) {
					pInfo.removeVisionBlocks();
					pInfo.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 2147483647, 1, true, false));
					continue;
				}
				if (pInfo.getIsImposter().booleanValue()) {
					pInfo.getPlayer().setFoodLevel(6);
				}
			}
			for (PlayerInfo pInfo : this.arena.getPlayersInfo()) {
				if (!pInfo.isGhost().booleanValue() && !pInfo.getIsImposter().booleanValue()) {
					for (PlayerInfo pInfo1 : this.arena.getPlayersInfo()) {
						if (pInfo != pInfo1 && !pInfo1.isGhost().booleanValue()) {
							this.arena.getVisibilityManager().showPlayer(pInfo, pInfo1, Boolean.valueOf(true));
						}
					}
				}
			}
		} else if (this.activeSabotage.getType() == SabotageType.COMMUNICATIONS) {
			this.arena.updateScoreBoard();
		} else if (this.activeSabotage.getType() == SabotageType.OXYGEN) {
			this.oxygenCode.clear();
			for (int i = 0; i < 5; i++) {
				this.oxygenCode.add(Integer.valueOf(Utils.getRandomNumberInRange(0, 9)));
			}
		}

		for (Player player : this.arena.getPlayers()) {
			if (player.getOpenInventory().getTopInventory().getHolder() instanceof MeetingBtnInv) {
				((MeetingBtnInv) player.getOpenInventory().getTopInventory().getHolder()).update();
			}
		}
	}

	public void endSabotage(Boolean didFix, Boolean isForce, Player playerFixed) {
		for (Player p : this.arena.getPlayers()) {
			this.bossbar.removePlayer(p);
		}
		if (this.timerRunnable != null) {
			this.timerRunnable.cancel();
		}

		if (playerFixed != null) {
			Main.getConfigManager().executeCommands("sabotageFix", playerFixed);
			Main.getCosmeticsManager().addCoins("sabotageFix", playerFixed);
		}

		this.isSabotageActive = Boolean.valueOf(false);

		for (PlayerInfo pInfo : this.arena.getPlayersInfo()) {
			if (pInfo.getPlayer().getOpenInventory().getTopInventory().getHolder() instanceof SabotageInvHolder) {
				pInfo.getPlayer().closeInventory();
			}
			if (!pInfo.isGhost().booleanValue()) {
				pInfo.getPlayer().removePotionEffect(PotionEffectType.BLINDNESS);
				if (pInfo.getIsImposter().booleanValue() && !this.arena.getDisableSprinting().booleanValue()) {
					pInfo.getPlayer().setFoodLevel(20);
				}
				if (this.arena.getEnableReducedVision().booleanValue()) {
					if (!pInfo.getIsImposter().booleanValue()) {
						pInfo.setVision(this.arena.getCrewmateVision());
						if (!isForce.booleanValue()) {
							this.arena.getVisibilityManager().playerMoved(pInfo);
						}
					}
				} else {
					pInfo.removeVisionBlocks();
					for (PlayerInfo pInfo1 : this.arena.getPlayersInfo()) {
						if (pInfo != pInfo1 && pInfo.isGhost().booleanValue()) {
							this.arena.getVisibilityManager().showPlayer(pInfo1, pInfo, Boolean.valueOf(true));
							this.arena.getVisibilityManager().showPlayer(pInfo, pInfo1, Boolean.valueOf(true));
						}
					}
				}
			}

			if (didFix.booleanValue()) {
				Main.getSoundsManager().playSound("sabotageFixed", pInfo.getPlayer(), pInfo.getPlayer().getLocation());
			}
		}

		if (didFix.booleanValue() && !isForce.booleanValue()) {
			String title = Main.getMessagesManager().getGameMsg("sabotageFixedTitle", this.arena, null);
			String subTitle = Main.getMessagesManager().getGameMsg("sabotageFixedSubTitle", this.arena, null);
			if (!title.isEmpty() || !subTitle.isEmpty()) {
				this.arena.sendTitle(title, subTitle);
			}
		}

		if (!didFix.booleanValue() && this.activeSabotage != null) {
			this.activeSabotage.hideHolo(Integer.valueOf(0));
			this.activeSabotage.hideHolo(Integer.valueOf(1));
		}

		this.saboInvHolder = null;
		this.isTimerActive = Boolean.valueOf(false);
		this.activeSabotage = null;
		this.isTimerPaused = Boolean.valueOf(false);
		for (PlayerInfo pInfo : this.arena.getGameImposters()) {
			int s_ = 9;
			String uuid = pInfo.getPlayer().getUniqueId().toString();
			setSabotageCoolDownTimer(uuid, this.arena.getSabotageCooldown());
			for (DoorGroup dg : this.arena.getDoorsManager().getDoorGroups()) {
				dg.setCooldownTimer(uuid, Integer.valueOf(0));
				pInfo.getPlayer().getInventory().setItem(s_, this.arena.getDoorsManager().getSabotageDoorItem(pInfo.getPlayer(), dg.getId()));
				s_++;
			}
		}
		this.arena.updateScoreBoard();

		for (Player player : this.arena.getPlayers()) {
			if (player.getOpenInventory().getTopInventory().getHolder() instanceof MeetingBtnInv) {
				((MeetingBtnInv) player.getOpenInventory().getTopInventory().getHolder()).update();
			}
		}

		if (!isForce.booleanValue()) {
			if (!didFix.booleanValue()) {
				this.arena.gameWin(Boolean.valueOf(true));
			} else {
				this.arena.getTasksManager().updateTasksDoneBar(Boolean.valueOf(true));
			}
		}
	}

	public void timerTick() {
		if (!this.isTimerPaused.booleanValue()) {
			this.sabotageTimer = Integer.valueOf(this.sabotageTimer - 1);
			updateBossBar();
			if (this.sabotageTimer < 0) {
				endSabotage(Boolean.valueOf(false), Boolean.valueOf(false), null);
			}
		}
	}

	public void updateBossBar() {
		if (this.activeSabotage != null) {
			String ext = "";
			String timeLeft = "";
			if (this.isTimerActive.booleanValue()) {
				double progress = this.sabotageTimer / this.activeSabotage.getTimer();
				if (progress >= 0.0D && progress <= 1.0D) {
					this.bossbar.setProgress(progress);
				}
				timeLeft = this.sabotageTimer.toString();
			} else {
				this.bossbar.setProgress(1.0D);
			}
			if (this.activeSabotage.getLength() == SabotageLength.DOUBLE) {
				ext = String.valueOf(ext) + "(";
				Integer num = Integer.valueOf(0);
				if (this.activeSabotage.getTaskDone(Integer.valueOf(0)).booleanValue()) {
					num = Integer.valueOf(num + 1);
				}
				if (this.activeSabotage.getTaskDone(Integer.valueOf(1)).booleanValue()) {
					num = Integer.valueOf(num + 1);
				}
				ext = String.valueOf(ext) + num + "/2)";
			} else if (this.activeSabotage.getLength() == SabotageLength.DOUBLE_SAME_TIME) {
				ext = String.valueOf(ext) + "(";
				Integer num = Integer.valueOf(0);
				if (this.activeSabotage.getTaskActive(Integer.valueOf(0)).booleanValue()) {
					num = Integer.valueOf(num + 1);
				}
				if (this.activeSabotage.getTaskActive(Integer.valueOf(1)).booleanValue()) {
					num = Integer.valueOf(num + 1);
				}
				ext = String.valueOf(ext) + num + "/2)";
			}
			String title_ = Main.getMessagesManager().getSabotageTitle(this.activeSabotage.getType());
			String name_ = Main.getMessagesManager().getTaskName(this.activeSabotage.getType().toString());
			if (this.isTimerActive.booleanValue()) {
				this.bossbar.setTitle(Main.getMessagesManager().getGameMsg("sabotageBossBarTimer", this.arena, title_, name_, timeLeft, ext));
			} else {
				this.bossbar.setTitle(Main.getMessagesManager().getGameMsg("sabotageBossBar", this.arena, title_, name_));
			}
		}
	}

	public void sabotageHoloClick(Player player, Integer clickedTaskId) {
		PlayerInfo pInfo = Main.getPlayersManager().getPlayerInfo(player);
		if (!this.isSabotageActive.booleanValue() || this.activeSabotage == null || pInfo == null || !pInfo.getIsIngame().booleanValue() || pInfo.isGhost().booleanValue()
				|| pInfo.getArena().getIsInMeeting().booleanValue() || pInfo.getIsInCameras().booleanValue()) {
			return;
		}

		if (pInfo.getIsImposter().booleanValue()) {
			pInfo.setKillCoolDownPaused(Boolean.valueOf(true));
		}

		Main.getSoundsManager().playSound("sabotageInvOpen", player, player.getLocation());

		switch (this.activeSabotage.getType()) {
		case LIGHTS:
			if (this.saboInvHolder == null) {
				this.saboInvHolder = (SabotageInvHolder) new SabotageLightsInv(getActiveSabotage());
			}

			player.openInventory(this.saboInvHolder.getInventory());
			return;
		case COMMUNICATIONS:
			player.openInventory((new SabotageCommsInv(getActiveSabotage())).getInventory());
			return;
		case REACTOR_MELTDOWN:
			player.openInventory((new SabotageReactorInv(getActiveSabotage(), clickedTaskId, player)).getInventory());
			return;
		case OXYGEN:
			player.openInventory((new SabotageOxygenInv(getActiveSabotage(), clickedTaskId, this.oxygenCode)).getInventory());
			return;
		}
		this.activeSabotage.taskDone(player);
	}

	public void setSabotageCoolDownTimer(String uuid, Integer sabotageCoolDownTimer) {
		PlayerInfo pInfo = Main.getPlayersManager().getPlayerByUUID(uuid);
		if (pInfo == null) {
			return;
		}
		if (!pInfo.getIsImposter().booleanValue()) {
			return;
		}
		Player player = pInfo.getPlayer();
		Integer sabotageCoolDownTimerP = getSabotageCoolDownTimer(player);
		BossBar sabotageCooldownBossBarP = getSabotageCooldownBossBar(player);
		if (sabotageCoolDownTimerP > 0 && sabotageCoolDownTimer == 0) {
			sabotageCooldownBossBarP.removePlayer(player);
		} else if (sabotageCoolDownTimerP == 0 && sabotageCoolDownTimer > 0) {
			sabotageCooldownBossBarP.addPlayer(player);
		}
		Integer maxSecs = (this.sabotageCooldownBossBarMax.get(uuid) == null) ? this.arena.getSabotageCooldown() : this.sabotageCooldownBossBarMax.get(uuid);
		if (sabotageCoolDownTimer > sabotageCoolDownTimerP) {
			maxSecs = sabotageCoolDownTimer;
			this.sabotageCooldownBossBarMax.put(uuid, maxSecs);
		}
		double progress = sabotageCoolDownTimer / maxSecs;

		if (progress >= 0.0D && progress <= 1.0D) {
			sabotageCooldownBossBarP.setProgress(progress);
			sabotageCooldownBossBarP.setTitle(Main.getMessagesManager().getGameMsg("sabotageCooldownBossBar", this.arena, "" + sabotageCoolDownTimer));
		}

		if (!this.arena.getIsInMeeting().booleanValue() && !pInfo.getIsInVent().booleanValue() && !pInfo.getIsInCameras().booleanValue()) {
			for (SabotageArena sa : this.arena.getSabotages()) {
				ItemInfoContainer saboInfo = getSabotageItemInfo(sa.getType());
				String name = Main.getMessagesManager().getTaskName(sa.getType().toString());
				pInfo.getPlayer().getInventory().setItem(saboInfo.getSlot(), getSabotageItem(sa.getType(), name, sabotageCoolDownTimer));
			}
		}

		this.sabotageCoolDownTimer.put(uuid, sabotageCoolDownTimer);
	}

	public ItemInfoContainer getSabotageItemInfo(SabotageType st) {
		String key = "sabotage_";
		switch (st) {
		case OXYGEN:
			key = String.valueOf(key) + "oxygen";
			return Main.getItemsManager().getItem(key);
		case REACTOR_MELTDOWN:
			key = String.valueOf(key) + "reactor";
			return Main.getItemsManager().getItem(key);
		case COMMUNICATIONS:
			key = String.valueOf(key) + "comms";
			return Main.getItemsManager().getItem(key);
		default:
			key = String.valueOf(key) + "lights";
			return Main.getItemsManager().getItem(key);
		}
	}

	public ItemStack getSabotageItem(SabotageType st, String name, Integer saboCoolDownTimer_) {
		if (saboCoolDownTimer_ == null) {
			saboCoolDownTimer_ = Integer.valueOf(0);
		}
		ItemInfoContainer sabotageItem = getSabotageItemInfo(st);
		String saboCoolDown = saboCoolDownTimer_.toString();
		Material mat = (saboCoolDownTimer_ == 0) ? sabotageItem.getItem2().getMat() : sabotageItem.getItem().getMat();
		String title = (saboCoolDownTimer_ == 0) ? sabotageItem.getItem2().getTitle(name, saboCoolDown) : sabotageItem.getItem().getTitle(name, saboCoolDown);
		ArrayList<String> lore = (saboCoolDownTimer_ == 0) ? sabotageItem.getItem2().getLore(name, saboCoolDown) : sabotageItem.getItem().getLore(name, saboCoolDown);
		return Utils.createItem(mat, title, (saboCoolDownTimer_ > 0) ? saboCoolDownTimer_ : 1, lore);
	}

	public void addImposter(Player player) {
		String uuid = player.getUniqueId().toString();
		this.sabotageCoolDownTimer.put(uuid, Integer.valueOf(0));
		this.sabotageCooldownBossBar.put(uuid,
				Bukkit.createBossBar(Main.getMessagesManager().getGameMsg("sabotageCooldownBossBar", this.arena, ""), BarColor.RED, BarStyle.SOLID, new org.bukkit.boss.BarFlag[0]));
	}

	public void removeImposter(String uuid) {
		this.sabotageCoolDownTimer.remove(uuid);
		if (this.sabotageCooldownBossBar.get(uuid) != null) {
			((BossBar) this.sabotageCooldownBossBar.get(uuid)).removeAll();
		}
		this.sabotageCooldownBossBar.remove(uuid);
		this.sabotageCooldownBossBarMax.remove(uuid);
	}

	public void resetImposters() {
		this.sabotageCoolDownTimer.clear();
		for (String uuid : this.sabotageCooldownBossBar.keySet()) {
			((BossBar) this.sabotageCooldownBossBar.get(uuid)).removeAll();
		}
		this.sabotageCooldownBossBar.clear();
		this.sabotageCooldownBossBarMax.clear();
	}

	public void delete() {
		this.arena = null;
		this.sabotageCoolDownTimer = null;
		this.sabotageCooldownBossBar = null;
		this.sabotageCooldownBossBarMax = null;
		this.activeSabotage = null;
		this.isSabotageActive = Boolean.valueOf(false);
		this.isTimerActive = Boolean.valueOf(false);
		this.bossbar = null;
		this.timerRunnable = null;
		this.isTimerPaused = null;
		this.saboInvHolder = null;
	}

	public void addPlayerToBossBar(Player player) {
		this.bossbar.addPlayer(player);
	}

	public void removePlayerFromBossBar(Player player) {
		this.bossbar.removePlayer(player);
	}

	public SabotageArena getActiveSabotage() {
		return this.activeSabotage;
	}

	public Arena getArena() {
		return this.arena;
	}

	public Integer getSabotageTimer() {
		return this.sabotageTimer;
	}

	public void setSabotageTimer(Integer sabotageTimer) {
		this.sabotageTimer = sabotageTimer;
	}

	public Boolean getIsSabotageActive() {
		return this.isSabotageActive;
	}

	public Boolean getIsTimerPaused() {
		return this.isTimerPaused;
	}

	public void setIsTimerPaused(Boolean isTimerPaused) {
		this.isTimerPaused = isTimerPaused;
	}

	public Integer getSabotageCoolDownTimer(Player player) {
		return this.sabotageCoolDownTimer.get(player.getUniqueId().toString());
	}

	public BossBar getSabotageCooldownBossBar(Player player) {
		return this.sabotageCooldownBossBar.get(player.getUniqueId().toString());
	}
}
