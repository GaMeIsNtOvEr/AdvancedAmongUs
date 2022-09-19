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

public class TaskCleanO2Inv extends TaskInvHolder {
	private static ArrayList<Integer> leavesSlots = new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(10), Integer.valueOf(39), Integer.valueOf(28), Integer.valueOf(37),
			Integer.valueOf(11), Integer.valueOf(12), Integer.valueOf(13), Integer.valueOf(14), Integer.valueOf(15), Integer.valueOf(19), Integer.valueOf(20), Integer.valueOf(21), Integer.valueOf(22),
			Integer.valueOf(23), Integer.valueOf(24), Integer.valueOf(29), Integer.valueOf(30), Integer.valueOf(31), Integer.valueOf(32), Integer.valueOf(33), Integer.valueOf(38), Integer.valueOf(39),
			Integer.valueOf(40), Integer.valueOf(41), Integer.valueOf(42), Integer.valueOf(16), Integer.valueOf(25), Integer.valueOf(34), Integer.valueOf(43) }));

	private ArrayList<Integer> leaves = new ArrayList<>();

	public TaskCleanO2Inv(Arena arena, TaskPlayer taskPlayer, ArrayList<Integer> leaves_) {
		super(54, Main.getMessagesManager().getGameMsg("taskInvTitle", arena, Main.getMessagesManager().getTaskName(taskPlayer.getActiveTask().getTaskType().toString()),
				taskPlayer.getActiveTask().getLocationName().getName()), arena, taskPlayer);
		Utils.fillInv(this.inv);
		this.leaves = leaves_;
		update();
	}

	public static ArrayList<Integer> generateLeaves() {
		ArrayList<Integer> out = new ArrayList<>();
		ArrayList<Integer> slotavailable = (ArrayList<Integer>) leavesSlots.clone();
		for (int i = 0; i < Utils.getRandomNumberInRange(9, 12); i++) {
			Integer id = Integer.valueOf(Utils.getRandomNumberInRange(0, slotavailable.size() - 1));
			if (!out.contains(id)) {
				out.add(id);
				slotavailable.remove(id);
			}
		}

		return out;
	}

	public void leafClick(Player player, Integer id) {
		Main.getSoundsManager().playSound("taskCleanO2LeafClick", player, player.getLocation());

		this.leaves.removeIf(s -> (s == id));

		checkDone();
		update();
	}

	public Boolean checkDone() {
		if (this.leaves.size() > 0) {
			return Boolean.valueOf(false);
		}
		this.taskPlayer.taskDone();
		final TaskCleanO2Inv inv = this;
		(new BukkitRunnable() {
			public void run() {
				Player player = inv.getTaskPlayer().getPlayerInfo().getPlayer();
				if (player.getOpenInventory().getTopInventory() == inv.getInventory()) {
					player.closeInventory();
				}
			}
		}).runTaskLater(Main.getPlugin(), 15L);
		return Boolean.valueOf(true);
	}

	public void update() {
		this.inv.setItem(8, Main.getItemsManager().getItem("cleanO2_info").getItem().getItem());

		ItemInfoContainer leafItem = Main.getItemsManager().getItem("cleanO2_leaf");
		ItemStack leafItemS = leafItem.getItem().getItem();
		ItemStack leafItem2S = leafItem.getItem2().getItem();

		final TaskCleanO2Inv inv = this;

		for (Integer slot : leavesSlots) {
			setIcon(slot, new Icon(leafItem2S));
		}

		for (int i = 0; i < this.leaves.size(); i++) {
			Integer slot = leavesSlots.get(((Integer) this.leaves.get(i)));
			Icon icon = new Icon(leafItemS);
			final Integer id_ = this.leaves.get(i);
			icon.addClickAction(new ClickAction() {
				public void execute(Player player) {
					inv.leafClick(player, id_);
				}
			});
			setIcon(slot, icon);
		}
	}

	public void invClosed() {
	}
}
