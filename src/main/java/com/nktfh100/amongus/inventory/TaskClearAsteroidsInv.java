package com.nktfh100.amongus.inventory;

import com.nktfh100.AmongUs.info.Arena;
import com.nktfh100.AmongUs.info.ItemInfo;
import com.nktfh100.AmongUs.info.TaskPlayer;
import com.nktfh100.AmongUs.main.Main;
import com.nktfh100.AmongUs.utils.Utils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

public class TaskClearAsteroidsInv extends TaskInvHolder {
	private static final ArrayList<Integer> slots = new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(10), Integer.valueOf(11), Integer.valueOf(12), Integer.valueOf(13),
			Integer.valueOf(14), Integer.valueOf(15), Integer.valueOf(16), Integer.valueOf(19), Integer.valueOf(20), Integer.valueOf(21), Integer.valueOf(22), Integer.valueOf(23), Integer.valueOf(24),
			Integer.valueOf(25), Integer.valueOf(28), Integer.valueOf(29), Integer.valueOf(30), Integer.valueOf(31), Integer.valueOf(32), Integer.valueOf(33), Integer.valueOf(34), Integer.valueOf(37),
			Integer.valueOf(38), Integer.valueOf(39), Integer.valueOf(40), Integer.valueOf(41), Integer.valueOf(42), Integer.valueOf(43) }));
	private static final ArrayList<Integer> spawnSlots = new ArrayList<>(
			Arrays.asList(new Integer[] { Integer.valueOf(29), Integer.valueOf(30), Integer.valueOf(31), Integer.valueOf(32), Integer.valueOf(33), Integer.valueOf(34), Integer.valueOf(38),
					Integer.valueOf(39), Integer.valueOf(40), Integer.valueOf(41), Integer.valueOf(42), Integer.valueOf(43), Integer.valueOf(25), Integer.valueOf(24) }));
	private Boolean isDone = Boolean.valueOf(false);
	private HashMap<Integer, Integer> asteroids = new HashMap<>();
	private BukkitTask runnable = null;
	private Particle.DustOptions dust = new Particle.DustOptions(Main.getConfigManager().getAsteroidsParticleColor(), 1.5F);

	public TaskClearAsteroidsInv(Arena arena, TaskPlayer taskPlayer) {
		super(54, Main.getMessagesManager().getGameMsg("taskInvTitle", arena, Main.getMessagesManager().getTaskName(taskPlayer.getActiveTask().getTaskType().toString()),
				taskPlayer.getActiveTask().getLocationName().getName()), arena, taskPlayer);
		Utils.fillInv(this.inv);
		this.inv.setItem(8, Main.getItemsManager().getItem("clearAsteroids_info").getItem().getItem());
		final TaskClearAsteroidsInv inv = this;
		this.runnable = (new BukkitRunnable() {
			public void run() {
				if (inv.getIsDone().booleanValue() || inv.getPlayerInfo() == null || !inv.getPlayerInfo().getIsIngame().booleanValue()) {
					cancel();
					return;
				}
				inv.tick();
			}
		}).runTaskTimer(Main.getPlugin(), 5L, 16L);
		update();
	}

	public void handleClick(Player player, Integer id) {
		if (this.isDone.booleanValue()) {
			return;
		}
		Main.getSoundsManager().playSound("taskClearAsteroidsClick", player, player.getLocation());
		if (id != null && this.asteroids.get(id) != null) {
			this.asteroids.remove(id);
			this.taskPlayer.setAsteroidsDestroyed_(Integer.valueOf(this.taskPlayer.getAsteroidsDestroyed_() + 1));
			Main.getSoundsManager().playSound("taskClearAsteroidsDestroy", player, player.getLocation());
		}
		if (this.arena.getEnableVisualTasks().booleanValue() && this.taskPlayer.getActiveTask().getEnableVisuals().booleanValue() && !this.pInfo.isGhost().booleanValue()) {
			long finish = System.currentTimeMillis();
			long timeElapsed = finish - this.taskPlayer.getActiveTask().getAsteroidsLastTime().longValue();
			if (timeElapsed > 800L) {
				this.taskPlayer.getActiveTask().setAsteroidsLastTime(Long.valueOf(System.currentTimeMillis()));
				playVisuals();
			}
		}
		if (this.taskPlayer.getAsteroidsDestroyed_() >= 20) {
			this.isDone = Boolean.valueOf(true);
			checkDone();
		}
		update();
	}

	public void tick() {
		Iterator<Map.Entry<Integer, Integer>> iter = this.asteroids.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<Integer, Integer> entry = iter.next();
			Integer slot = entry.getValue();
			if (slot <= 16) {
				iter.remove();
				continue;
			}
			if (slot != 10 && slot != 19 && slot != 28 && slot != 37) {
				if (!this.asteroids.values().contains(Integer.valueOf(slot - 10)))
					this.asteroids.put(entry.getKey(), Integer.valueOf(slot - 10));
				continue;
			}
			iter.remove();
		}

		if (this.asteroids.size() <= 6 && (Math.random() >= 0.5D || this.asteroids.size() <= 2)) {
			for (int i = 0; i < 15; i++) {
				Integer slot = spawnSlots.get(Utils.getRandomNumberInRange(0, spawnSlots.size() - 1));
				if (!this.asteroids.values().contains(slot)) {
					this.asteroids.put(Integer.valueOf((int) (Math.random() * 9999.0D)), slot);

					break;
				}
			}
		}
		update();
	}

	public void playVisuals() {
		final Location canonnLoc = (this.taskPlayer.getActiveTask().getActiveCannon() == 0) ? this.taskPlayer.getActiveTask().getCannon1().clone()
				: this.taskPlayer.getActiveTask().getCannon2().clone();
		if (canonnLoc == null || canonnLoc.getWorld() == null) {
			return;
		}
		final Vector vector = canonnLoc.getDirection().multiply(1);

		canonnLoc.getWorld().spawnParticle(Particle.BLOCK_CRACK, canonnLoc.getX(), canonnLoc.getY(), canonnLoc.getZ(), 30, 0.3D, 0.3D, 0.3D, Main.getConfigManager().getAsteroidsParticleMaterial());

		if (this.taskPlayer.getActiveTask().getCannon2() != null) {
			this.taskPlayer.getActiveTask().setActiveCannon(Integer.valueOf((this.taskPlayer.getActiveTask().getActiveCannon() == 0) ? 1 : 0));
		}

		(new BukkitRunnable() {
			Double progress = Double.valueOf(0.0D);

			public void run() {
				if (this.progress.doubleValue() > 20.0D) {
					cancel();
					return;
				}
				for (int i = 0; i < 5; i++) {
					canonnLoc.getWorld().spawnParticle(Particle.REDSTONE, canonnLoc.getX(), canonnLoc.getY(), canonnLoc.getZ(), 5, 0.1D, 0.1D, 0.1D, TaskClearAsteroidsInv.this.dust);
					canonnLoc.add(vector);
					this.progress = Double.valueOf(this.progress.doubleValue() + 1.0D);
				}
			}
		}).runTaskTimer(Main.getPlugin(), 0L, 1L);
	}

	public Boolean checkDone() {
		if (this.isDone.booleanValue()) {
			this.taskPlayer.taskDone();
			final TaskClearAsteroidsInv taskInv = this;
			(new BukkitRunnable() {
				public void run() {
					Player player = taskInv.getTaskPlayer().getPlayerInfo().getPlayer();
					if (player.getOpenInventory().getTopInventory() == taskInv.getInventory()) {
						player.closeInventory();
					}
				}
			}).runTaskLater(Main.getPlugin(), 15L);
			return Boolean.valueOf(true);
		}
		return Boolean.valueOf(false);
	}

	public void update() {
		final TaskClearAsteroidsInv inv = this;
		ItemInfo asteroidItem = Main.getItemsManager().getItem("clearAsteroids_asteroid").getItem();
		ItemStack asteroidItemS = asteroidItem.getItem();
		ItemInfo backgroundItem = Main.getItemsManager().getItem("clearAsteroids_background").getItem();
		ItemStack backgroundItemS = backgroundItem.getItem();

		for (Integer slot : slots) {
			Icon icon = new Icon(backgroundItemS);
			icon.addClickAction(new ClickAction() {
				public void execute(Player player) {
					inv.handleClick(player, null);
				}
			});
			setIcon(slot, icon);
		}

		for (Integer id : this.asteroids.keySet()) {
			Icon icon = new Icon(asteroidItemS);
			icon.addClickAction(new ClickAction() {
				public void execute(Player player) {
					inv.handleClick(player, id);
				}
			});
			setIcon(((Integer) this.asteroids.get(id)), icon);
		}

		ItemInfo infoItem = Main.getItemsManager().getItem("clearAsteroids_destroyed").getItem();
		ItemStack infoItemS = infoItem.getItem("" + this.taskPlayer.getAsteroidsDestroyed_(), null);
		infoItemS.setAmount((this.taskPlayer.getAsteroidsDestroyed_() > 0) ? this.taskPlayer.getAsteroidsDestroyed_() : 1);
		setIcon(49, new Icon(infoItemS));
	}

	public void invClosed() {
		if (this.runnable != null) {
			this.runnable.cancel();
			this.runnable = null;
		}
	}

	public Boolean getIsDone() {
		return this.isDone;
	}

	public void setIsDone(Boolean isDone) {
		this.isDone = isDone;
	}
}
