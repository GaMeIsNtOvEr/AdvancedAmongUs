package com.nktfh100.amongus.inventory;

import com.nktfh100.AmongUs.info.Arena;
import com.nktfh100.AmongUs.info.ItemInfoContainer;
import com.nktfh100.AmongUs.info.TaskPlayer;
import com.nktfh100.AmongUs.main.Main;
import com.nktfh100.AmongUs.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class TaskInspectSampleInv extends TaskInvHolder {
	private Boolean isDone = Boolean.valueOf(false);
	private BukkitTask runnable = null;

	public TaskInspectSampleInv(Arena arena, TaskPlayer taskPlayer) {
		super(36, Main.getMessagesManager().getGameMsg("taskInvTitle", arena, Main.getMessagesManager().getTaskName(taskPlayer.getActiveTask().getTaskType().toString()),
				taskPlayer.getActiveTask().getLocationName().getName()), arena, taskPlayer);
		Utils.fillInv(this.inv);
		final TaskInspectSampleInv inv = this;
		this.runnable = (new BukkitRunnable() {
			public void run() {
				if (inv.getIsDone().booleanValue()) {
					cancel();
					return;
				}
				inv.update();
			}
		}).runTaskTimer(Main.getPlugin(), 20L, 20L);
		update();
	}

	public void handleStartClick() {
		if (this.isDone.booleanValue() || this.taskPlayer.getInspectIsRunning_().booleanValue()) {
			return;
		}
		Main.getSoundsManager().playSound("taskInspectSampleStartClick", this.pInfo.getPlayer(), this.pInfo.getPlayer().getLocation());
		this.taskPlayer.setInspectIsRunning_(Boolean.valueOf(true));

		update();
	}

	public void handleSelectClick(Integer clickedI) {
		if (this.isDone.booleanValue() || this.taskPlayer.getInspectIsRunning_().booleanValue()) {
			return;
		}

		if (clickedI == this.taskPlayer.getInspectAnomaly_()) {
			Main.getSoundsManager().playSound("taskInspectSampleSelectRight", this.pInfo.getPlayer(), this.pInfo.getPlayer().getLocation());
			this.isDone = Boolean.valueOf(true);
			checkDone();
		} else {
			Main.getSoundsManager().playSound("taskInspectSampleSelectWrong", this.pInfo.getPlayer(), this.pInfo.getPlayer().getLocation());
			this.taskPlayer.updateTasksVars();
		}

		update();
	}

	public Boolean checkDone() {
		if (this.isDone.booleanValue()) {
			this.taskPlayer.taskDone();
			final TaskInspectSampleInv taskInv = this;
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
		final TaskInspectSampleInv inv = this;
		Boolean isRunning = this.taskPlayer.getInspectIsRunning_();

		ItemInfoContainer bottleItem = Main.getItemsManager().getItem("inspectSample_tube");
		ItemInfoContainer buttonItem = Main.getItemsManager().getItem("inspectSample_selectButton");
		ItemStack buttonItemS = buttonItem.getItem().getItem();
		if (this.taskPlayer.getInspectTimer_() == 0) {
			buttonItemS = buttonItem.getItem3().getItem();
		} else if (isRunning.booleanValue()) {
			buttonItemS = buttonItem.getItem2().getItem();
		}

		ItemStack bottleItemS = Utils.createItem(Material.GLASS_BOTTLE, bottleItem.getItem().getTitle(), 1, bottleItem.getItem().getLore());
		if (isRunning.booleanValue() || this.taskPlayer.getInspectTimer_() == 0) {
			bottleItemS = new ItemStack(Material.POTION);
			Utils.addItemFlag(bottleItemS, new ItemFlag[] { ItemFlag.HIDE_POTION_EFFECTS });

			PotionMeta pm = (PotionMeta) bottleItemS.getItemMeta();
			pm.setBasePotionData(new PotionData(PotionType.AWKWARD));
			bottleItemS.setItemMeta((ItemMeta) pm);
			Utils.setItemName(bottleItemS, bottleItem.getItem2().getTitle(), bottleItem.getItem2().getLore());
		}
		int slot_ = 11;
		for (int i = 0; i < 5; i++) {

			if (this.taskPlayer.getInspectAnomaly_() == i) {
				ItemStack potion = new ItemStack(Material.POTION);
				PotionMeta pm = (PotionMeta) potion.getItemMeta();
				pm.setBasePotionData(new PotionData(PotionType.INSTANT_HEAL));
				potion.setItemMeta((ItemMeta) pm);
				Utils.setItemName(potion, bottleItem.getItem3().getTitle(), bottleItem.getItem3().getLore());
				Utils.addItemFlag(potion, new ItemFlag[] { ItemFlag.HIDE_POTION_EFFECTS });
				this.inv.setItem(slot_, potion);
			} else {
				this.inv.setItem(slot_, bottleItemS);
			}

			Icon btnIcon = new Icon(buttonItemS);
			if (!isRunning.booleanValue() && this.taskPlayer.getInspectTimer_() == 0) {
				final Integer clicked = Integer.valueOf(i);
				btnIcon.addClickAction(new ClickAction() {
					public void execute(Player player) {
						inv.handleSelectClick(clicked);
					}
				});
			}
			setIcon(slot_ + 9, btnIcon);
			slot_++;
		}

		Integer integer = this.taskPlayer.getInspectTimer_();
		ItemInfoContainer infoItem = Main.getItemsManager().getItem("inspectSample_bottomETA");
		ItemStack infoItemS = isRunning.booleanValue() ? infoItem.getItem2().getItem("" + integer, null) : infoItem.getItem().getItem("" + integer, null);
		if (this.taskPlayer.getInspectTimer_() == 0) {
			infoItemS = infoItem.getItem3().getItem("" + integer, null);
		}
		if (isRunning.booleanValue()) {
			infoItemS.setAmount(this.taskPlayer.getInspectTimer_());
		}
		this.inv.setItem(31, infoItemS);

		ItemInfoContainer startBtnItem = Main.getItemsManager().getItem("inspectSample_startButton");
		ItemStack startBtnItemS = isRunning.booleanValue() ? startBtnItem.getItem2().getItem() : startBtnItem.getItem().getItem();
		if (this.taskPlayer.getInspectTimer_() == 0) {
			startBtnItemS = startBtnItem.getItem3().getItem();
		}

		Icon icon = new Icon(startBtnItemS);
		if (!isRunning.booleanValue() && this.taskPlayer.getInspectTimer_() != 0) {
			icon.addClickAction(new ClickAction() {
				public void execute(Player player) {
					inv.handleStartClick();
				}
			});
		}
		setIcon(34, icon);

		this.inv.setItem(8, Main.getItemsManager().getItem("inspectSample_info").getItem().getItem());
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
