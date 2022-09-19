package com.nktfh100.amongus.inventory;

import com.nktfh100.AmongUs.info.Arena;
import com.nktfh100.AmongUs.info.ColorInfo;
import com.nktfh100.AmongUs.info.FakeArmorStand;
import com.nktfh100.AmongUs.info.ItemInfo;
import com.nktfh100.AmongUs.info.ItemInfoContainer;
import com.nktfh100.AmongUs.info.PlayerInfo;
import com.nktfh100.AmongUs.info.TaskPlayer;
import com.nktfh100.AmongUs.main.Main;
import com.nktfh100.AmongUs.utils.Utils;
import java.util.ArrayList;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class TaskScanInv extends TaskInvHolder {
	private ColorInfo color;
	private Integer secondsLeft = Integer.valueOf(10);
	private Boolean isQueue = Boolean.valueOf(false);
	private Boolean isDone = Boolean.valueOf(false);
	private BukkitTask runnable = null;
	private BukkitTask runnable1 = null;

	private Boolean removeFromQueue = Boolean.valueOf(true);
	private Integer dir;

	public TaskScanInv(Arena arena, TaskPlayer taskPlayer) {
		super((arena.getScanQueue().size() == 0) ? 54 : 9, Main.getMessagesManager().getGameMsg("taskInvTitle", arena,
				Main.getMessagesManager().getTaskName(taskPlayer.getActiveTask().getTaskType().toString()), taskPlayer.getActiveTask().getLocationName().getName()), arena, taskPlayer);

		this.dir = Integer.valueOf(0);
		Utils.fillInv(this.inv);
		this.color = this.pInfo.getColor();
		if (arena.getScanQueue().size() == 0 || this.pInfo.isGhost().booleanValue()) {
			startScanning();
		} else {
			this.isQueue = Boolean.valueOf(true);
		}
		if (!this.pInfo.isGhost().booleanValue())
			arena.getScanQueue().add(this.pInfo);
		final TaskScanInv inv = this;
		this.runnable = (new BukkitRunnable() {
			public void run() {
				if (inv.getIsDone().booleanValue() || inv.getSecondsLeft() == 0 || TaskScanInv.this.pInfo == null || !TaskScanInv.this.pInfo.getIsIngame().booleanValue()) {
					cancel();
					return;
				}
				if (!inv.getIsQueue().booleanValue()) {
					inv.tick();
				} else if (inv.getArena().getScanQueue().get(0) == inv.getPlayerInfo()) {
					inv.startScanning();
				}
			}
		}).runTaskTimer(Main.getPlugin(), 20L, 20L);
		update();
	}

	public Boolean checkDone() {
		if (this.isDone.booleanValue()) {
			this.arena.getScanQueue().remove(this.pInfo);
			this.taskPlayer.taskDone();
			final TaskScanInv taskInv = this;
			(new BukkitRunnable() {
				public void run() {
					Player player = taskInv.getTaskPlayer().getPlayerInfo().getPlayer();
					if (player.getOpenInventory().getTopInventory() == taskInv.getInventory())
						player.closeInventory();
				}
			}).runTaskLater(Main.getPlugin(), 20L);
			return Boolean.valueOf(true);
		}
		return Boolean.valueOf(false);
	}

	public void updateArmorStands() {
		Double toAdd = Double.valueOf((this.dir == 0) ? -0.25D : 0.25D);
		Double armorStandY = Double.valueOf(((FakeArmorStand) this.pInfo.getScanArmorStands().get(0)).getLoc().getY());
		if (armorStandY.doubleValue() > this.pInfo.getPlayer().getLocation().getY() - 0.2D) {
			this.dir = Integer.valueOf(0);
		} else if (armorStandY.doubleValue() < this.pInfo.getPlayer().getLocation().getY() - 1.2D) {
			this.dir = Integer.valueOf(1);
		}
		for (FakeArmorStand fa : this.pInfo.getScanArmorStands()) {
			fa.updateLocation(fa.getLoc().add(0.0D, toAdd.doubleValue(), 0.0D));
		}
	}

	public void tick() {
		if (this.secondsLeft > 0) {
			this.secondsLeft = Integer.valueOf(this.secondsLeft - 1);
		}
		if (this.secondsLeft == 0) {
			if (this.runnable != null) {
				this.runnable.cancel();
				this.runnable = null;
			}
			this.isDone = Boolean.valueOf(true);
			checkDone();
		}

		update();
	}

	public void startScanning() {
		this.pInfo.setIsScanning(Boolean.valueOf(true));
		this.isQueue = Boolean.valueOf(false);
		if (this.inv.getSize() == 9) {
			this.removeFromQueue = Boolean.valueOf(false);
			changeSize(54);
			this.pInfo.getPlayer().openInventory(this.inv);
			Utils.fillInv(this.inv);
			this.removeFromQueue = Boolean.valueOf(true);
		}
		if (this.arena.getEnableVisualTasks().booleanValue() && this.taskPlayer.getActiveTask().getEnableVisuals().booleanValue() && !this.pInfo.isGhost().booleanValue()) {
			ArrayList<FakeArmorStand> fakeArmorStands = this.pInfo.getScanArmorStands();
			for (FakeArmorStand fa : fakeArmorStands)
				fa.resetAllShownTo();
			Double extraDis = Double.valueOf(0.8D);
			((FakeArmorStand) fakeArmorStands.get(0)).updateLocation(this.pInfo.getPlayer().getLocation().add(-0.3D, -1.0D, 0.3D - extraDis.doubleValue()));
			((FakeArmorStand) fakeArmorStands.get(1)).updateLocation(this.pInfo.getPlayer().getLocation().add(0.3D, -1.0D, -0.3D - extraDis.doubleValue()));
			((FakeArmorStand) fakeArmorStands.get(2)).updateLocation(this.pInfo.getPlayer().getLocation().add(0.3D, -1.0D, 0.3D - extraDis.doubleValue()));
			((FakeArmorStand) fakeArmorStands.get(3)).updateLocation(this.pInfo.getPlayer().getLocation().add(-0.3D, -1.0D, -0.3D - extraDis.doubleValue()));
			for (PlayerInfo pInfo_ : this.arena.getPlayersInfo()) {
				if (this.pInfo != pInfo_) {
					if (this.arena.getEnableReducedVision().booleanValue()) {
						if (pInfo_.isGhost().booleanValue() || !pInfo_.getPlayersHidden().contains(this.pInfo.getPlayer()))
							for (FakeArmorStand fa : fakeArmorStands)
								fa.showTo(pInfo_.getPlayer(), Boolean.valueOf(true));
						continue;
					}
					for (FakeArmorStand fa : fakeArmorStands)
						fa.showTo(pInfo_.getPlayer(), Boolean.valueOf(true));
				}
			}
			for (FakeArmorStand fa : fakeArmorStands)
				fa.showTo(this.pInfo.getPlayer(), Boolean.valueOf(true));
			final TaskScanInv inv = this;
			this.runnable1 = (new BukkitRunnable() {
				public void run() {
					if (inv.getIsDone().booleanValue() || inv.getSecondsLeft() == 0 || TaskScanInv.this.pInfo == null || !TaskScanInv.this.pInfo.getIsIngame().booleanValue()) {
						cancel();
						return;
					}
					inv.updateArmorStands();
				}
			}).runTaskTimer(Main.getPlugin(), 5L, 5L);
		}
		update();
		this.arena.getVisibilityManager().playerMoved(this.pInfo);
	}

	public void update() {
		if (!this.isQueue.booleanValue()) {

			this.inv.setItem(8, Main.getItemsManager().getItem("scan_info").getItem().getItem());

			ItemInfo idItem = Main.getItemsManager().getItem("scan_infoId").getItem();
			ItemInfo heightItem = Main.getItemsManager().getItem("scan_infoHeight").getItem();
			ItemInfo weightItem = Main.getItemsManager().getItem("scan_infoWeight").getItem();
			ItemInfo colorItem = Main.getItemsManager().getItem("scan_infoColor").getItem();
			ItemInfo bloodItem = Main.getItemsManager().getItem("scan_infoBloodType").getItem();
			if (this.secondsLeft < 10) {
				this.inv.setItem(20, idItem.getItem(this.color.getId(), "" +  this.taskPlayer.getPlayerInfo().getJoinedId()));
			}
			if (this.secondsLeft < 8) {
				this.inv.setItem(21, heightItem.getItem(this.color.getHeight(), null));
			}
			if (this.secondsLeft < 6) {
				this.inv.setItem(22, weightItem.getItem(this.color.getWeight(), null));
			}
			if (this.secondsLeft < 4) {
				this.inv.setItem(23, colorItem.getItem(this.color.getName(), null));
			}
			if (this.secondsLeft < 3) {
				this.inv.setItem(24, bloodItem.getItem(this.color.getBloodType(), null));
			}

			ItemInfoContainer progressItem = Main.getItemsManager().getItem("scan_progress");
			ItemStack progressItemS = progressItem.getItem().getItem();
			ItemStack progressItem2S = progressItem.getItem2().getItem();
			Integer slot = Integer.valueOf(37);
			for (int i = 1; i < 8; i++) {
				if (i * 1.25D <= (10 - this.secondsLeft)) {
					this.inv.setItem(slot, progressItem2S);
				} else {
					this.inv.setItem(slot, progressItemS);
				}
				slot = Integer.valueOf(slot + 1);
			}

			ItemInfoContainer infoItem = Main.getItemsManager().getItem("scan_seconds");
			ItemStack infoItemS = (this.secondsLeft == 0) ? infoItem.getItem2().getItem() : infoItem.getItem().getItem("" + this.secondsLeft, null);
			if (this.secondsLeft > 0) {
				infoItemS.setAmount(this.secondsLeft);
			}
			this.inv.setItem(49, infoItemS);
		} else {
			PlayerInfo playerScanning = this.arena.getScanQueue().get(0);
			if (playerScanning == this.pInfo) {
				startScanning();
				return;
			}
			ItemInfo infoItem = Main.getItemsManager().getItem("scan_queue_info").getItem();
			this.inv.setItem(4, infoItem.getItem(playerScanning.getPlayer().getName(), "" + playerScanning.getColor().getChatColor(), playerScanning.getColor().getName()));
		}
	}

	public void invClosed() {
		if (this.removeFromQueue.booleanValue()) {
			this.pInfo.setIsScanning(Boolean.valueOf(false));
			this.arena.getScanQueue().remove(this.pInfo);
			for (FakeArmorStand fas : this.pInfo.getScanArmorStands()) {
				fas.resetAllShownTo();
			}
			if (this.runnable != null) {
				this.runnable.cancel();
				this.runnable = null;
			}
			if (this.runnable1 != null) {
				this.runnable1.cancel();
				this.runnable1 = null;
			}
		}
	}

	public Boolean getIsDone() {
		return this.isDone;
	}

	public void setIsDone(Boolean isDone) {
		this.isDone = isDone;
	}

	public Integer getSecondsLeft() {
		return this.secondsLeft;
	}

	public void setSecondsLeft(Integer secondsLeft) {
		this.secondsLeft = secondsLeft;
	}

	public Boolean getIsQueue() {
		return this.isQueue;
	}

	public void setIsQueue(Boolean isQueue) {
		this.isQueue = isQueue;
	}
}
