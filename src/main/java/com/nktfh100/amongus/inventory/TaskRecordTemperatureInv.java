package com.nktfh100.amongus.inventory;

import com.nktfh100.AmongUs.info.Arena;
import com.nktfh100.AmongUs.info.ItemInfoContainer;
import com.nktfh100.AmongUs.info.TaskPlayer;
import com.nktfh100.AmongUs.main.Main;
import com.nktfh100.AmongUs.utils.Utils;
import java.util.ArrayList;
import java.util.Arrays;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class TaskRecordTemperatureInv extends TaskInvHolder {
	private static final ArrayList<Integer> leftBGSlots = new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(1), Integer.valueOf(3), Integer.valueOf(10), Integer.valueOf(12),
			Integer.valueOf(28), Integer.valueOf(30), Integer.valueOf(37), Integer.valueOf(38), Integer.valueOf(39) }));
	private static final ArrayList<Integer> rightBGSlots = new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(5), Integer.valueOf(7), Integer.valueOf(14), Integer.valueOf(15),
			Integer.valueOf(16), Integer.valueOf(32), Integer.valueOf(33), Integer.valueOf(34), Integer.valueOf(41), Integer.valueOf(42), Integer.valueOf(43) }));

	private Boolean isHot = Boolean.valueOf(false);
	private Integer targetNumber = Integer.valueOf(0);
	private Integer activeNumber = Integer.valueOf(0);
	private Boolean isDone = Boolean.valueOf(false);

	public TaskRecordTemperatureInv(Arena arena, TaskPlayer taskPlayer, Boolean isHot) {
		super(54, Main.getMessagesManager().getGameMsg("taskInvTitle", arena, Main.getMessagesManager().getTaskName(taskPlayer.getActiveTask().getTaskType().toString()),
				taskPlayer.getActiveTask().getLocationName().getName()), arena, taskPlayer);
		Utils.fillInv(this.inv);
		this.isHot = isHot;
		if (!this.isHot.booleanValue()) {
			this.targetNumber = Integer.valueOf(Utils.getRandomNumberInRange(10, 40) * -1);
			this.activeNumber = Integer.valueOf(Utils.getRandomNumberInRange(1, 20));
		} else {
			this.targetNumber = Integer.valueOf(Utils.getRandomNumberInRange(300, 340));
			this.activeNumber = Integer.valueOf(Utils.getRandomNumberInRange(280, 300));
		}
		ItemInfoContainer bgItems = Main.getItemsManager().getItem("recordTemperature_background");
		ItemStack bgLeftS = bgItems.getItem().getItem();
		ItemStack bgRightS = this.isHot.booleanValue() ? bgItems.getItem3().getItem() : bgItems.getItem2().getItem();
		for (Integer slot : leftBGSlots) {
			this.inv.setItem(slot, bgLeftS);
		}
		for (Integer slot : rightBGSlots) {
			this.inv.setItem(slot, bgRightS);
		}
		ItemInfoContainer topTextItems = Main.getItemsManager().getItem("recordTemperature_infoTop");
		this.inv.setItem(2, topTextItems.getItem().getItem());
		this.inv.setItem(6, topTextItems.getItem2().getItem());
		update();
	}

	public void handleButtonUpClick() {
		if (this.isDone.booleanValue()) {
			return;
		}
		Main.getSoundsManager().playSound("taskRecordTemperature_click", this.pInfo.getPlayer(), this.pInfo.getPlayer().getLocation());
		this.activeNumber = Integer.valueOf(this.activeNumber + 1);
		if (this.activeNumber > 99 && !this.isHot.booleanValue()) {
			this.activeNumber = Integer.valueOf(99);
		}
		if (this.isHot.booleanValue() && this.activeNumber > 999) {
			this.activeNumber = Integer.valueOf(999);
		}
		if (this.isHot.booleanValue() && this.activeNumber >= this.targetNumber) {
			this.activeNumber = this.targetNumber;
			this.isDone = Boolean.valueOf(true);
		} else if (!this.isHot.booleanValue() && this.activeNumber <= this.targetNumber) {
			this.activeNumber = this.targetNumber;
			this.isDone = Boolean.valueOf(true);
		}
		update();
		checkDone();
	}

	public void handleButtonDownClick() {
		if (this.isDone.booleanValue()) {
			return;
		}
		Main.getSoundsManager().playSound("taskRecordTemperature_click", this.pInfo.getPlayer(), this.pInfo.getPlayer().getLocation());
		this.activeNumber = Integer.valueOf(this.activeNumber - 1);
		if (this.activeNumber < -99 && !this.isHot.booleanValue()) {
			this.activeNumber = Integer.valueOf(-99);
		}
		if (this.isHot.booleanValue() && this.activeNumber < 100) {
			this.activeNumber = Integer.valueOf(100);
		}
		if (this.isHot.booleanValue() && this.activeNumber >= this.targetNumber) {
			this.activeNumber = this.targetNumber;
			this.isDone = Boolean.valueOf(true);
		} else if (!this.isHot.booleanValue() && this.activeNumber <= this.targetNumber) {
			this.activeNumber = this.targetNumber;
			this.isDone = Boolean.valueOf(true);
		}
		update();
		checkDone();
	}

	public Boolean checkDone() {
		if (this.isDone.booleanValue()) {
			this.taskPlayer.taskDone();
			final TaskRecordTemperatureInv inv = this;
			(new BukkitRunnable() {
				public void run() {
					Player player = inv.getTaskPlayer().getPlayerInfo().getPlayer();
					if (player.getOpenInventory().getTopInventory() == inv.getInventory()) {
						player.closeInventory();
					}
				}
			}).runTaskLater(Main.getPlugin(), 20L);
			return Boolean.valueOf(true);
		}
		return Boolean.valueOf(false);
	}

	public void update() {
		final TaskRecordTemperatureInv inv = this;

		this.inv.setItem(8, Main.getItemsManager().getItem("recordTemperature_info").getItem().getItem());

		ItemInfoContainer arrowsItem = Main.getItemsManager().getItem("recordTemperature_arrows");
		Icon icon = new Icon(arrowsItem.getItem().getItem("" + this.activeNumber, "" + this.targetNumber));
		icon.addClickAction(new ClickAction() {
			public void execute(Player player) {
				inv.handleButtonUpClick();
			}
		});
		setIcon(11, icon);

		icon = new Icon(arrowsItem.getItem2().getItem("" + this.activeNumber, "" + this.targetNumber));
		icon.addClickAction(new ClickAction() {
			public void execute(Player player) {
				inv.handleButtonDownClick();
			}
		});
		setIcon(29, icon);

		String activeNumberStr = this.activeNumber.toString();
		if (this.activeNumber == 0) {
			activeNumberStr = "n0n";
		} else if (this.activeNumber > 0) {
			if (!this.isHot.booleanValue()) {
				activeNumberStr = "+" + activeNumberStr;
			}
			if (this.activeNumber < 10) {
				activeNumberStr = "n" + activeNumberStr;
			}
		} else if (this.activeNumber < 0 && this.activeNumber > -10) {
			activeNumberStr = "n" + activeNumberStr;
		}
		Integer slot = Integer.valueOf(19);
		String numKey = "recordTemperature_num_";
		byte b;
		int i;
		String[] arrayOfString1;
		for (i = (arrayOfString1 = activeNumberStr.split("")).length, b = 0; b < i;) {
			String char_ = arrayOfString1[b];
			String keyEnd = char_;
			String str;
			switch ((str = char_).hashCode()) {
			case 43:
				if (str.equals("+")) {

					keyEnd = "plus";
				}
			case 45:
				if (str.equals("-"))
					keyEnd = "minus";
			case 110:
				if (str.equals("n")) {
					this.inv.setItem(slot, Main.getItemsManager().getItem("recordTemperature_background").getItem().getItem("" + this.activeNumber, "" + this.targetNumber));
					slot = Integer.valueOf(slot + 1);
					break;
				}

			default:
				this.inv.setItem(slot, Main.getItemsManager().getItem(String.valueOf(numKey) + keyEnd).getItem().getItem("" + this.activeNumber, "" + this.targetNumber));
				slot = Integer.valueOf(slot + 1);
				break;
			}
			b++;
		}

		String targetNumberStr = this.targetNumber.toString();
		if (this.targetNumber == 0) {
			targetNumberStr = "n0n";
		} else if (this.targetNumber > 0) {
			if (!this.isHot.booleanValue()) {
				targetNumberStr = "+" + targetNumberStr;
			}
			if (this.activeNumber < 10) {
				targetNumberStr = "n" + targetNumberStr;
			}
		} else if (this.targetNumber < 0 && this.targetNumber > -10) {
			targetNumberStr = "n" + targetNumberStr;
		}
		slot = Integer.valueOf(23);
		String[] arrayOfString2;
		for (int j = (arrayOfString2 = targetNumberStr.split("")).length; i < j;) {
			String char_ = arrayOfString2[i];
			String keyEnd = char_;
			String str;
			switch ((str = char_).hashCode()) {
			case 43:
				if (str.equals("+")) {

					keyEnd = "plus";
				}
			case 45:
				if (str.equals("-"))
					keyEnd = "minus";
			case 110:
				if (str.equals("n")) {
					this.inv.setItem(slot, Main.getItemsManager().getItem("recordTemperature_background").getItem2().getItem("" + this.activeNumber, "" + this.targetNumber));
					slot = Integer.valueOf(slot + 1);
					break;
				}

			default:
				this.inv.setItem(slot, Main.getItemsManager().getItem(String.valueOf(numKey) + keyEnd).getItem2().getItem("" + this.activeNumber, "" + this.targetNumber));
				slot = Integer.valueOf(slot + 1);
				break;
			}

			i++;
		}

	}

	public void invClosed() {
	}
}
