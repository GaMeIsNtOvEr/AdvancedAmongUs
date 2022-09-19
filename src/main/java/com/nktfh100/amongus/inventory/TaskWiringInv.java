package com.nktfh100.amongus.inventory;

import com.nktfh100.AmongUs.info.Arena;
import com.nktfh100.AmongUs.info.ItemInfoContainer;
import com.nktfh100.AmongUs.info.TaskPlayer;
import com.nktfh100.AmongUs.main.Main;
import com.nktfh100.AmongUs.utils.Utils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class TaskWiringInv extends TaskInvHolder {
	private static final HashMap<String, Integer> wiresRightSlots = new HashMap<>();
	static {
		wiresRightSlots.put("red", Integer.valueOf(16));
		wiresRightSlots.put("blue", Integer.valueOf(25));
		wiresRightSlots.put("yellow", Integer.valueOf(34));
		wiresRightSlots.put("pink", Integer.valueOf(43));
	}

	private ArrayList<HashMap<String, Boolean>> wiresStates = new ArrayList<>();

	private String activeWire = "";

	public TaskWiringInv(Arena arena, TaskPlayer taskPlayer) {
		super(54, Main.getMessagesManager().getGameMsg("taskInvTitle", arena, Main.getMessagesManager().getTaskName(taskPlayer.getActiveTask().getTaskType().toString()),
				taskPlayer.getActiveTask().getLocationName().getName()), arena, taskPlayer);
		Utils.fillInv(this.inv);

		final TaskWiringInv inv = this;

		this.wiresStates.add(new HashMap<>());
		for (String colorStr : wiresRightSlots.keySet()) {
			((HashMap<String, Boolean>) this.wiresStates.get(0)).put(colorStr, Boolean.valueOf(false));
		}
		this.wiresStates.add(new HashMap<>());
		for (String colorStr : wiresRightSlots.keySet()) {
			((HashMap<String, Boolean>) this.wiresStates.get(1)).put(colorStr, Boolean.valueOf(false));
		}

		for (String key : wiresRightSlots.keySet()) {
			ItemInfoContainer wireItemInfo = Main.getItemsManager().getItem("wiring_" + key);
			Icon icon = new Icon(wireItemInfo.getItem().getItem());
			icon.addClickAction(new ClickAction() {
				public void execute(Player player) {
					inv.handleWireRightClick(key);
				}
			});
			setIcon(((Integer) wiresRightSlots.get(key)), icon);
		}

		update();
	}

	public Boolean checkDone() {
		for (Boolean wireState : this.wiresStates.get(0).values()) {
			if (wireState == false) {
				return false;
			}
		}
		final TaskWiringInv taskInv = this;
		(new BukkitRunnable() {
			public void run() {
				Player player = taskInv.getTaskPlayer().getPlayerInfo().getPlayer();
				if (player.getOpenInventory().getTopInventory() == taskInv.getInventory()) {
					player.closeInventory();
				}
			}
		}).runTaskLater(Main.getPlugin(), 15L);
		this.taskPlayer.taskDone();
		return Boolean.valueOf(true);
	}

	public void handleWireLeftClick(String clickedColor) {
		if (this.wiresStates.get(0).get(clickedColor)) {
			return;
		}
		this.activeWire = clickedColor;
		Main.getSoundsManager().playSound("taskWiringClick", this.taskPlayer.getPlayerInfo().getPlayer(), this.taskPlayer.getPlayerInfo().getPlayer().getLocation());
		update();
	}

	public void handleWireRightClick(String clickedColor) {
		if (this.activeWire.isEmpty()) {
			return;
		}
		if (!this.activeWire.equals(clickedColor)) {
			Main.getSoundsManager().playSound("taskWiringDisconnect", this.taskPlayer.getPlayerInfo().getPlayer(), this.taskPlayer.getPlayerInfo().getPlayer().getLocation());
			this.activeWire = "";
			update();

			return;
		}
		((HashMap<String, Boolean>) this.wiresStates.get(0)).put(clickedColor, Boolean.valueOf(true));
		((HashMap<String, Boolean>) this.wiresStates.get(1)).put(clickedColor, Boolean.valueOf(true));
		this.activeWire = "";
		Main.getSoundsManager().playSound("taskWiringConnect", this.taskPlayer.getPlayerInfo().getPlayer(), this.taskPlayer.getPlayerInfo().getPlayer().getLocation());

		update();
		checkDone();
	}

	public static ArrayList<String> generateWires() {
		ArrayList<String> out = new ArrayList<>(wiresRightSlots.keySet());
		Collections.shuffle(out);
		return out;
	}

	public void update() {
		final TaskWiringInv inv = this;
		this.inv.setItem(8, Main.getItemsManager().getItem("wiring_info").getItem().getItem());

		Integer sideSlot = Integer.valueOf(10);
		for (String colorStr : this.taskPlayer.getWires_()) {
			ItemInfoContainer wireItemInfo = Main.getItemsManager().getItem("wiring_" + colorStr);
			ItemStack item = wireItemInfo.getItem().getItem();
			if (this.activeWire.equals(colorStr)) {
				Utils.enchantedItem(item, Enchantment.DURABILITY, 1);
			}
			Icon icon = new Icon(item);
			icon.addClickAction(new ClickAction() {
				public void execute(Player player) {
					inv.handleWireLeftClick(colorStr);
				}
			});
			setIcon(sideSlot, icon);

			ItemStack middleItem = wireItemInfo.getItem2().getItem();

			Integer rightColorSlot = wiresRightSlots.get(colorStr);
			Integer slot_ = sideSlot;
			if (this.wiresStates.get(0).get(colorStr)) {

				Boolean isOk = Boolean.valueOf(false);
				int i = 0;
				while (!isOk.booleanValue()) {
					Integer nextMove = Integer.valueOf(1);

					if (slot_ > rightColorSlot) {

						nextMove = Integer.valueOf(0);
					} else if (slot_ < rightColorSlot) {

						if (rightColorSlot - slot_ <= 9) {

							nextMove = Integer.valueOf(1);
						} else {

							nextMove = Integer.valueOf(2);
						}
					}

					if (nextMove == 0) {

						slot_ = Integer.valueOf(slot_ - 8);
					} else if (nextMove == 1) {

						slot_ = Integer.valueOf(slot_ + 1);
					} else if (nextMove == 2) {

						slot_ = Integer.valueOf(slot_ + 10);
					}

					this.inv.setItem(slot_, middleItem);

					if (slot_ == 15 || slot_ == 24 || slot_ == 33 || slot_ == 42) {
						isOk = Boolean.valueOf(true);
						break;
					}
					if (i > 5) {
						break;
					}
					i++;
				}
			}

			sideSlot = Integer.valueOf(sideSlot + 9);
		}
	}

	public void invClosed() {
	}
}
