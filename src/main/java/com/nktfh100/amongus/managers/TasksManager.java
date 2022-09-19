package com.nktfh100.amongus.managers;

import com.nktfh100.AmongUs.enums.SabotageType;
import com.nktfh100.AmongUs.enums.TaskLength;
import com.nktfh100.AmongUs.enums.TaskType;
import com.nktfh100.AmongUs.info.Arena;
import com.nktfh100.AmongUs.info.PlayerInfo;
import com.nktfh100.AmongUs.info.QueuedTasksVariant;
import com.nktfh100.AmongUs.info.Task;
import com.nktfh100.AmongUs.info.TaskPlayer;
import com.nktfh100.AmongUs.inventory.TaskAcceptDivertedPowerInv;
import com.nktfh100.AmongUs.inventory.TaskCalibrateDistributorInv;
import com.nktfh100.AmongUs.inventory.TaskChartCourseInv;
import com.nktfh100.AmongUs.inventory.TaskCleanO2Inv;
import com.nktfh100.AmongUs.inventory.TaskClearAsteroidsInv;
import com.nktfh100.AmongUs.inventory.TaskDataInv;
import com.nktfh100.AmongUs.inventory.TaskDivertPowerInv;
import com.nktfh100.AmongUs.inventory.TaskEmptyGarbageInv;
import com.nktfh100.AmongUs.inventory.TaskFillCanistersInv;
import com.nktfh100.AmongUs.inventory.TaskFixWeatherNodeInv;
import com.nktfh100.AmongUs.inventory.TaskFuelInv;
import com.nktfh100.AmongUs.inventory.TaskInsertKeysInv;
import com.nktfh100.AmongUs.inventory.TaskInspectSampleInv;
import com.nktfh100.AmongUs.inventory.TaskMonitorTreeInv;
import com.nktfh100.AmongUs.inventory.TaskOpenWaterwaysInv;
import com.nktfh100.AmongUs.inventory.TaskPrimeShieldsInv;
import com.nktfh100.AmongUs.inventory.TaskRebootWifiInv;
import com.nktfh100.AmongUs.inventory.TaskRecordTemperatureInv;
import com.nktfh100.AmongUs.inventory.TaskRefuelInv;
import com.nktfh100.AmongUs.inventory.TaskRepairDrillInv;
import com.nktfh100.AmongUs.inventory.TaskReplaceWaterJug;
import com.nktfh100.AmongUs.inventory.TaskScanBoardingPassInv;
import com.nktfh100.AmongUs.inventory.TaskScanInv;
import com.nktfh100.AmongUs.inventory.TaskStabilizeSteeringInv;
import com.nktfh100.AmongUs.inventory.TaskStartReactorInv;
import com.nktfh100.AmongUs.inventory.TaskStoreArtifactsInv;
import com.nktfh100.AmongUs.inventory.TaskSwipeCardInv;
import com.nktfh100.AmongUs.inventory.TaskSwitchWeatherNodeInv;
import com.nktfh100.AmongUs.inventory.TaskUnlockManifoldsInv;
import com.nktfh100.AmongUs.inventory.TaskWiringInv;
import com.nktfh100.AmongUs.main.Main;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import org.bukkit.entity.Player;

public class TasksManager {
	private Arena arena;
	private HashMap<String, ArrayList<TaskPlayer>> tasks = new HashMap<>();

	public TasksManager(Arena arena) {
		this.arena = arena;
	}

	public void giveTasks() {
		for (PlayerInfo pInfo : this.arena.getPlayersInfo()) {
			ArrayList<TaskPlayer> playerTasks = new ArrayList<>();
			byte b;
			int j;
			TaskLength[] arrayOfTaskLength;
			for (j = (arrayOfTaskLength = TaskLength.values()).length, b = 0; b < j;) {
				TaskLength tl = arrayOfTaskLength[b];
				ArrayList<String> allTasksOfLength = this.arena.getTasksLength(tl);
				Collections.shuffle(allTasksOfLength);
				Integer numOfTasksToAdd = this.arena.getTasksNum(tl);
				for (int i = 0; i < allTasksOfLength.size() && numOfTasksToAdd > 0; i++) {

					Task taskSelected = this.arena.getTask(allTasksOfLength.get(i));
					if (taskSelected.getIsEnabled().booleanValue()) {

						ArrayList<Task> tasksQueued = new ArrayList<>(Arrays.asList(new Task[] { taskSelected }));
						QueuedTasksVariant qtv = taskSelected.getRandomTaskVariant();
						if (qtv != null) {
							for (Task t : qtv.getQueuedTasksTasks()) {
								tasksQueued.add(t);
							}
						}

						if (isAddingTaskOk(playerTasks, tasksQueued).booleanValue()) {
							TaskPlayer tp = new TaskPlayer(pInfo, tasksQueued, Integer.valueOf((qtv == null) ? -1 : qtv.getId()));
							playerTasks.add(tp);
							tp.getActiveTask().getHolo().getVisibilityManager().showTo(pInfo.getPlayer());
							numOfTasksToAdd = Integer.valueOf(numOfTasksToAdd - 1);
						}
					}
				}
				b++;
			}
			this.tasks.put(pInfo.getPlayer().getUniqueId().toString(), playerTasks);
		}
	}

	public Boolean isAddingTaskOk(ArrayList<TaskPlayer> tasks_, ArrayList<Task> newTasks) {
		for (TaskPlayer oldTP : tasks_) {
			for (Task newT : newTasks) {
				for (Task oldT : oldTP.getTasks()) {
					if (oldT.getId().equals(newT.getId())) {
						return Boolean.valueOf(false);
					}
				}
			}
		}
		return Boolean.valueOf(true);
	}

	public void taskHoloClick(Player player, Task taskClicked) {
		PlayerInfo pInfo = Main.getPlayersManager().getPlayerInfo(player);
		if (pInfo.getIsImposter().booleanValue() || pInfo.getIsInCameras().booleanValue() || pInfo.getArena().getIsInMeeting().booleanValue()) {
			return;
		}

		TaskPlayer taskPlayer = null;
		for (TaskPlayer tp : getTasksForPlayer(player)) {
			if (tp.getActiveTask().getId().equals(taskClicked.getId())) {
				taskPlayer = tp;
				break;
			}
		}
		if (taskPlayer == null) {
			return;
		}
		if (taskPlayer.getIsDone().booleanValue()) {
			for (Task t : taskPlayer.getTasks()) {
				t.getHolo().getVisibilityManager().hideTo(player);
			}

			return;
		}
		Main.getSoundsManager().playSound("taskInvOpen", player, player.getLocation());

		switch (taskPlayer.getActiveTask().getTaskType()) {
		case WIRING:
			player.openInventory((new TaskWiringInv(this.arena, taskPlayer)).getInventory());
			return;
		case DOWNLOAD_DATA:
		case UPLOAD_DATA:
			player.openInventory((new TaskDataInv(this.arena, taskPlayer)).getInventory());
			return;
		case UNLOCK_MANIFOLDS:
			player.openInventory((new TaskUnlockManifoldsInv(this.arena, taskPlayer, taskPlayer.getNumbers_())).getInventory());
			return;
		case DIVERT_POWER:
			player.openInventory((new TaskDivertPowerInv(this.arena, taskPlayer, taskPlayer.getLocations_(), taskPlayer.getActiveLocation_(), taskPlayer.getActiveLever_())).getInventory());
			return;
		case ACCEPT_DIVERTED_POWER:
			player.openInventory((new TaskAcceptDivertedPowerInv(this.arena, taskPlayer)).getInventory());
			return;
		case PRIME_SHIELDS:
			player.openInventory((new TaskPrimeShieldsInv(this.arena, taskPlayer, taskPlayer.getSquares_())).getInventory());
			return;
		case CALIBRATE_DISTRIBUTOR:
			player.openInventory((new TaskCalibrateDistributorInv(this.arena, taskPlayer)).getInventory());
			return;
		case EMPTY_GARBAGE:
			player.openInventory((new TaskEmptyGarbageInv(this.arena, taskPlayer)).getInventory());
			return;
		case CLEAN_O2:
			player.openInventory((new TaskCleanO2Inv(this.arena, taskPlayer, taskPlayer.getLeaves_())).getInventory());
			return;
		case REFUEL:
			player.openInventory((new TaskRefuelInv(this.arena, taskPlayer, taskPlayer.getFuelProgress_())).getInventory());
			return;
		case FUEL:
			player.openInventory((new TaskFuelInv(this.arena, taskPlayer, taskPlayer.getFuelProgress_())).getInventory());
			return;
		case INSPECT_SAMPLE:
			player.openInventory((new TaskInspectSampleInv(this.arena, taskPlayer)).getInventory());
			return;
		case START_REACTOR:
			player.openInventory((new TaskStartReactorInv(this.arena, taskPlayer, taskPlayer.getMoves_())).getInventory());
			return;
		case SCAN:
			player.openInventory((new TaskScanInv(this.arena, taskPlayer)).getInventory());
			return;
		case CLEAR_ASTEROIDS:
			player.openInventory((new TaskClearAsteroidsInv(this.arena, taskPlayer)).getInventory());
			return;
		case SWIPE_CARD:
			player.openInventory((new TaskSwipeCardInv(this.arena, taskPlayer)).getInventory());
			return;
		case CHART_COURSE:
			player.openInventory((new TaskChartCourseInv(this.arena, taskPlayer)).getInventory());
			return;
		case STABILIZE_STEERING:
			player.openInventory((new TaskStabilizeSteeringInv(this.arena, taskPlayer)).getInventory());
			return;
		case FILL_CANISTERS:
			player.openInventory((new TaskFillCanistersInv(this.arena, taskPlayer)).getInventory());
			return;
		case INSERT_KEYS:
			player.openInventory((new TaskInsertKeysInv(this.arena, taskPlayer)).getInventory());
			return;
		case REPLACE_WATER_JUG:
			player.openInventory((new TaskReplaceWaterJug(this.arena, taskPlayer)).getInventory());
			return;
		case RECORD_TEMPERATURE:
			player.openInventory((new TaskRecordTemperatureInv(this.arena, taskPlayer, taskPlayer.getActiveTask().getIsHot())).getInventory());
			return;
		case REPAIR_DRILL:
			player.openInventory((new TaskRepairDrillInv(this.arena, taskPlayer, taskPlayer.getActiveTask().getIsHot())).getInventory());
			return;
		case MONITOR_TREE:
			player.openInventory((new TaskMonitorTreeInv(this.arena, taskPlayer)).getInventory());
			return;
		case OPEN_WATERWAYS:
			player.openInventory((new TaskOpenWaterwaysInv(this.arena, taskPlayer)).getInventory());
			return;
		case REBOOT_WIFI:
			player.openInventory((new TaskRebootWifiInv(this.arena, taskPlayer)).getInventory());
			return;
		case FIX_WEATHER_NODE:
			player.openInventory((new TaskFixWeatherNodeInv(this.arena, taskPlayer)).getInventory());
			return;
		case SWITCH_WEATHER_NODE:
			player.openInventory((new TaskSwitchWeatherNodeInv(this.arena, taskPlayer)).getInventory());
			return;
		case SCAN_BOARDING_PASS:
			player.openInventory((new TaskScanBoardingPassInv(this.arena, taskPlayer)).getInventory());
			return;
		case STORE_ARTIFACTS:
			player.openInventory((new TaskStoreArtifactsInv(this.arena, taskPlayer)).getInventory());
			return;
		}

		taskPlayer.taskDone();
		updateTasksDoneBar(Boolean.valueOf(true));
	}

	public void updateTasksDoneBar(Boolean canWin) {
		Integer totalTasks = Integer.valueOf(0);
		Integer tasksDone = Integer.valueOf(0);

		for (String tasks_key : this.tasks.keySet()) {
			PlayerInfo pInfo = Main.getPlayersManager().getPlayerByUUID(tasks_key);
			if (pInfo == null || pInfo.getIsImposter().booleanValue()) {
				continue;
			}
			ArrayList<TaskPlayer> tasks_ = this.tasks.get(tasks_key);
			for (TaskPlayer tp : tasks_) {
				if (tp.getIsDone().booleanValue()) {
					tasksDone = Integer.valueOf(tasksDone + 1);
				}
			}
			totalTasks = Integer.valueOf(totalTasks + tasks_.size());
		}

		double progress = 0;
		if (totalTasks > 0) {
			progress = (double) tasksDone / (double) totalTasks;
		}

		if (progress >= 0.0D && progress <= 1.0D) {
			if (this.arena.getSabotageManager().getIsSabotageActive().booleanValue() && this.arena.getSabotageManager().getActiveSabotage().getType() == SabotageType.COMMUNICATIONS) {
				this.arena.getTasksBossBar().setProgress(0.0D);
			} else {
				this.arena.getTasksBossBar().setProgress(progress);
			}
		}
		if (progress >= 1.0D && canWin.booleanValue()) {
			this.arena.gameWin(Boolean.valueOf(false));
		}
	}

	public void removeTasksForPlayer(Player p) {
		if (this.tasks.get(p.getUniqueId().toString()) != null) {
			this.tasks.get(p.getUniqueId().toString()).clear();
		}
	}

	public ArrayList<TaskPlayer> getTasksForPlayer(Player p) {
		ArrayList<TaskPlayer> tp_ = this.tasks.get(p.getUniqueId().toString());
		if (tp_ == null) {
			tp_ = new ArrayList<>();
		}
		return tp_;
	}

	public Collection<ArrayList<TaskPlayer>> getAllTasks() {
		return this.tasks.values();
	}

	public void delete() {
		for (ArrayList<TaskPlayer> tasks_ : this.tasks.values()) {
			for (TaskPlayer tp : tasks_) {
				tp.delete();
			}
		}
		this.tasks = null;
	}
}
