package com.nktfh100.amongus.inventory;

import com.nktfh100.AmongUs.info.Arena;
import com.nktfh100.AmongUs.info.ItemInfoContainer;
import com.nktfh100.AmongUs.info.TaskPlayer;
import com.nktfh100.AmongUs.main.Main;
import com.nktfh100.AmongUs.utils.Utils;
import java.util.ArrayList;
import java.util.Arrays;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class TaskRebootWifiInv extends TaskInvHolder {
	private static final ArrayList<Integer> screenSlots = new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(29), Integer.valueOf(30), Integer.valueOf(31), Integer.valueOf(38),
			Integer.valueOf(39), Integer.valueOf(40), Integer.valueOf(47), Integer.valueOf(48), Integer.valueOf(49) }));

	private Boolean isLeverClicked = Boolean.valueOf(false);
	private Boolean isDone = Boolean.valueOf(false);
	private BukkitTask runnable = null;

	public TaskRebootWifiInv(Arena arena, TaskPlayer taskPlayer) {
		super(54, Main.getMessagesManager().getGameMsg("taskInvTitle", arena, Main.getMessagesManager().getTaskName(taskPlayer.getActiveTask().getTaskType().toString()),
				taskPlayer.getActiveTask().getLocationName().getName()), arena, taskPlayer);
		Utils.fillInv(this.inv);
		final TaskRebootWifiInv inv = this;
		this.runnable = (new BukkitRunnable() {
			public void run() {
				if (inv == null || inv.getIsDone().booleanValue()) {
					cancel();
					return;
				}
				inv.update();
			}
		}).runTaskTimer(Main.getPlugin(), 20L, 20L);
		update();
	}

	public void handleLeverClick() {
		if (this.isDone.booleanValue() || this.isLeverClicked.booleanValue()) {
			return;
		}
		if (!this.taskPlayer.getRebootIsRunning_().booleanValue()) {
			this.isLeverClicked = Boolean.valueOf(true);
		} else if (this.taskPlayer.getRebootTimer_() == 0) {
			this.isLeverClicked = Boolean.valueOf(true);
		}

		update();
	}

	public void handleLeverTargetClick() {
		if (this.isDone.booleanValue() || !this.isLeverClicked.booleanValue()) {
			return;
		}
		this.isLeverClicked = Boolean.valueOf(false);

		if (this.taskPlayer.getRebootIsRunning_().booleanValue() && this.taskPlayer.getRebootTimer_() == 0) {
			this.isDone = Boolean.valueOf(true);
		} else if (!this.taskPlayer.getRebootIsRunning_().booleanValue()) {
			this.taskPlayer.setRebootIsRunning_(Boolean.valueOf(true));
		}

		update();
		checkDone();
	}

	public Boolean checkDone() {
		if (this.isDone.booleanValue()) {
			this.taskPlayer.taskDone();
			final TaskRebootWifiInv taskInv = this;
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
		final TaskRebootWifiInv inv = this;
		Boolean isRunning = this.taskPlayer.getRebootIsRunning_();

		ItemInfoContainer leverItemInfo = Main.getItemsManager().getItem("rebootWifi_lever");

		ClickAction leverCA = new ClickAction() {
			public void execute(Player player) {
				inv.handleLeverClick();
			}
		};
		ClickAction leverTargetCA = new ClickAction() {
			public void execute(Player player) {
				inv.handleLeverTargetClick();
			}
		};
		if (!isRunning.booleanValue()) {
			ItemStack item_ = leverItemInfo.getItem().getItem();
			if (this.isLeverClicked.booleanValue()) {
				Utils.enchantedItem(item_, Enchantment.DURABILITY, 1);
			}
			Icon icon = new Icon(item_);
			icon.addClickAction(leverCA);
			setIcon(15, icon);
			setIcon(16, icon);

			icon = new Icon(Main.getItemsManager().getItem("rebootWifi_leverTarget").getItem().getItem());
			icon.addClickAction(leverTargetCA);
			setIcon(51, icon);
			setIcon(52, icon);
		} else {
			ItemStack item_ = null;
			if (this.taskPlayer.getRebootTimer_() > 0) {
				item_ = leverItemInfo.getItem2().getItem("" + this.taskPlayer.getRebootTimer_(), null);
			} else if (!this.isDone.booleanValue()) {
				item_ = leverItemInfo.getItem3().getItem();
			} else {
				item_ = Main.getItemsManager().getItem("rebootWifi_leverDone").getItem().getItem();
			}

			if (this.isLeverClicked.booleanValue()) {
				Utils.enchantedItem(item_, Enchantment.DURABILITY, 1);
			}
			Icon icon = new Icon(item_);
			icon.addClickAction(leverCA);
			setIcon(this.isDone.booleanValue() ? 15 : 51, icon);
			setIcon(this.isDone.booleanValue() ? 16 : 52, icon);

			if (!this.isDone.booleanValue()) {
				icon = new Icon((this.taskPlayer.getRebootTimer_() == 0) ? Main.getItemsManager().getItem("rebootWifi_leverTarget").getItem().getItem()
						: Utils.createItem(Material.BLACK_STAINED_GLASS_PANE, " "));
				icon.addClickAction(leverTargetCA);
				setIcon(15, icon);
				setIcon(16, icon);
			} else {
				icon = new Icon(Utils.createItem(Material.BLACK_STAINED_GLASS_PANE, " "));
				setIcon(51, icon);
				setIcon(52, icon);
			}
		}

		ItemInfoContainer topScreenItemInfo = Main.getItemsManager().getItem("rebootWifi_screenTop");
		ItemStack topScreenItemS = null;
		if (!isRunning.booleanValue()) {
			topScreenItemS = topScreenItemInfo.getItem().getItem();
		} else if (isRunning.booleanValue()) {
			if (this.isDone.booleanValue()) {
				topScreenItemS = Main.getItemsManager().getItem("rebootWifi_screenTopDone").getItem().getItem();
			} else if (this.taskPlayer.getRebootTimer_() == 0) {
				topScreenItemS = topScreenItemInfo.getItem3().getItem("" + this.taskPlayer.getRebootTimer_(), null);
			} else {
				topScreenItemS = topScreenItemInfo.getItem2().getItem("" + this.taskPlayer.getRebootTimer_(), null);
				topScreenItemS.setAmount((this.taskPlayer.getRebootTimer_() > 0) ? this.taskPlayer.getRebootTimer_() : 1);
			}
		}

		for (int i = 0; i < 3; i++) {
			this.inv.setItem(11 + i, topScreenItemS);
		}

		ItemInfoContainer bottomScreenItemInfo = Main.getItemsManager().getItem("rebootWifi_screenBottom");
		ItemStack bottomScreenItemS = null;
		if (!isRunning.booleanValue()) {
			bottomScreenItemS = bottomScreenItemInfo.getItem().getItem();
		} else if (isRunning.booleanValue()) {
			if (this.isDone.booleanValue()) {
				bottomScreenItemS = bottomScreenItemInfo.getItem3().getItem();
			} else {
				bottomScreenItemS = bottomScreenItemInfo.getItem2().getItem();
			}
		}

		for (Integer slot : screenSlots) {
			this.inv.setItem(slot, bottomScreenItemS);
		}
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
