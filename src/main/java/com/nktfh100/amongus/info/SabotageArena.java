package com.nktfh100.amongus.info;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.nktfh100.AmongUs.enums.SabotageLength;
import com.nktfh100.AmongUs.enums.SabotageType;
import com.nktfh100.AmongUs.main.Main;
import java.util.ArrayList;
import java.util.Arrays;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class SabotageArena {
	private Arena arena;
	private SabotageLength length;
	private Integer timer;
	private Boolean hasTimer;
	private SabotageTask task1;
	private SabotageTask task2 = null;

	private ArrayList<Boolean> isTaskDone = new ArrayList<>();

	private ArrayList<Boolean> isTaskActive = new ArrayList<>();
	private ArrayList<ArrayList<Player>> playersActive = new ArrayList<>();
	private BukkitTask runnable = null;

	public SabotageArena(Arena arena, SabotageTask task1, SabotageTask task2) {
		this.arena = arena;
		this.task1 = task1;
		this.length = task1.getSabotageType().getSabotageLength();
		this.timer = task1.getTimer();
		this.hasTimer = task1.getHasTimer();
		if (this.length != SabotageLength.SINGLE) {
			this.task2 = task2;
		}
		if (this.length == SabotageLength.DOUBLE_SAME_TIME) {
			this.isTaskActive = new ArrayList<Boolean>(Arrays.asList(false, false));
			this.playersActive = new ArrayList<ArrayList<Player>>(Arrays.asList(new ArrayList<Player>(), new ArrayList<Player>()));
		} else if (this.length == SabotageLength.DOUBLE) {
			this.isTaskDone.add(Boolean.valueOf(false));
			this.isTaskDone.add(Boolean.valueOf(false));
		}
	}

	public void taskDone(Player player) {
		if (this.length == SabotageLength.SINGLE) {
			hideHolo(Integer.valueOf(0));
		} else {
			hideHolo(Integer.valueOf(0));
			hideHolo(Integer.valueOf(1));
			this.isTaskActive.set(0, Boolean.valueOf(false));
			this.isTaskActive.set(1, Boolean.valueOf(false));
			this.playersActive.set(0, new ArrayList<>());
			this.playersActive.set(1, new ArrayList<>());
		}
		this.arena.getSabotageManager().endSabotage(Boolean.valueOf(true), Boolean.valueOf(false), player);
		this.arena.updateScoreBoard();
		this.arena.getSabotageManager().updateBossBar();
	}

	public void taskDone(Integer id, Player player) {
		if (this.length == SabotageLength.DOUBLE) {
			this.isTaskDone.set(id, Boolean.valueOf(true));
			hideHolo(id);
			if (((Boolean) this.isTaskDone.get(0)).booleanValue() && ((Boolean) this.isTaskDone.get(1)).booleanValue()) {
				this.arena.getSabotageManager().endSabotage(Boolean.valueOf(true), Boolean.valueOf(false), player);
				this.isTaskDone.set(0, Boolean.valueOf(false));
				this.isTaskDone.set(0, Boolean.valueOf(false));
				hideHolo(Integer.valueOf(0));
				hideHolo(Integer.valueOf(1));
			}
		}
		this.arena.updateScoreBoard();
		this.arena.getSabotageManager().updateBossBar();
	}

	public void hideHolo(Integer id) {
		if (id == 0) {
			Hologram task1Holo = this.task1.getHolo();
			if (task1Holo != null) {
				task1Holo.getVisibilityManager().resetVisibilityAll();
				task1Holo.getVisibilityManager().setVisibleByDefault(false);
			}
		} else if (this.task2 != null) {
			Hologram task2Holo = this.task2.getHolo();
			if (task2Holo != null) {
				task2Holo.getVisibilityManager().resetVisibilityAll();
				task2Holo.getVisibilityManager().setVisibleByDefault(false);
			}
		}
	}

	public void showHolos() {
		Hologram task1Holo = this.task1.getHolo();
		if (task1Holo != null) {
			task1Holo.getVisibilityManager().resetVisibilityAll();
			task1Holo.getVisibilityManager().setVisibleByDefault(true);
		}

		if (this.task2 != null) {
			Hologram task2Holo = this.task2.getHolo();
			if (task2Holo != null) {
				task2Holo.getVisibilityManager().resetVisibilityAll();
				task2Holo.getVisibilityManager().setVisibleByDefault(true);
			}
		}
	}

	public Boolean getTaskDone(Integer i) {
		return this.isTaskDone.get(i);
	}

	public Boolean getTaskActive(Integer i) {
		return this.isTaskActive.get(i);
	}

	private void setTaskActive(Integer i, Boolean is) {
		this.isTaskActive.set(i, is);
	}

	public void addPlayerActive(final Player player, Integer i) {
		if (!this.playersActive.get(i).contains(player)) {
			this.playersActive.get(i).add(player);
		}
		setTaskActive(i, Boolean.valueOf(true));
		final SabotageArena saboArena = this;
		if (getTaskActive(Integer.valueOf(0)).booleanValue() && getTaskActive(Integer.valueOf(1)).booleanValue()) {
			this.arena.getSabotageManager().setIsTimerPaused(Boolean.valueOf(true));
			this.runnable = (new BukkitRunnable() {
				public void run() {
					if (saboArena.getTaskActive(Integer.valueOf(0)).booleanValue() && saboArena.getTaskActive(Integer.valueOf(1)).booleanValue()) {
						saboArena.taskDone(player);
					}
				}
			}).runTaskLater(Main.getPlugin(), 30L);
		}
		this.arena.getSabotageManager().updateBossBar();
	}

	public void removePlayerActive(Player player, Integer i) {
		this.playersActive.get(i).remove(player);
		if (this.playersActive.get(i).size() == 0) {
			setTaskActive(i, Boolean.valueOf(false));
			if (this.runnable != null) {
				this.runnable.cancel();
				this.arena.getSabotageManager().setIsTimerPaused(Boolean.valueOf(false));
			}
		}
		this.arena.getSabotageManager().updateBossBar();
	}

	public ArrayList<Hologram> getHolos() {
		ArrayList<Hologram> holos = new ArrayList<>();
		holos.add(this.task1.getHolo());
		if (this.task2 != null) {
			holos.add(this.task2.getHolo());
		}
		return holos;
	}

	public SabotageType getType() {
		return getTask1().getSabotageType();
	}

	public SabotageLength getLength() {
		return this.length;
	}

	public SabotageTask getTask1() {
		return this.task1;
	}

	public SabotageTask getTask2() {
		return this.task2;
	}

	public Arena getArena() {
		return this.arena;
	}

	public Integer getTimer() {
		return this.timer;
	}

	public Boolean getHasTimer() {
		return this.hasTimer;
	}
}
