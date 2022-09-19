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
import org.bukkit.scheduler.BukkitTask;

public class TaskStartReactorInv extends TaskInvHolder {
	private static ArrayList<Integer> slotsLeft = new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(10), Integer.valueOf(11), Integer.valueOf(12), Integer.valueOf(19), Integer.valueOf(20),
			Integer.valueOf(21), Integer.valueOf(28), Integer.valueOf(29), Integer.valueOf(30) }));
	private static ArrayList<Integer> slotsRight = new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(14), Integer.valueOf(15), Integer.valueOf(16), Integer.valueOf(23),
			Integer.valueOf(24), Integer.valueOf(25), Integer.valueOf(32), Integer.valueOf(33), Integer.valueOf(34) }));

	private static ArrayList<Integer> slotsProgressLeft = new ArrayList<>(
			Arrays.asList(new Integer[] { Integer.valueOf(0), Integer.valueOf(9), Integer.valueOf(18), Integer.valueOf(27), Integer.valueOf(36) }));
	private static ArrayList<Integer> slotsProgressRight = new ArrayList<>(
			Arrays.asList(new Integer[] { Integer.valueOf(8), Integer.valueOf(17), Integer.valueOf(26), Integer.valueOf(35), Integer.valueOf(44) }));

	private Boolean isDone = Boolean.valueOf(false);
	private BukkitTask playRunnable = null;
	private ArrayList<Integer> moves = new ArrayList<>();
	private Integer activePlaySquare = Integer.valueOf(-1);
	private Integer activePlaySquareRight = Integer.valueOf(-1);

	private Integer activeClickingSquare = Integer.valueOf(0);
	private Boolean canClick = Boolean.valueOf(false);
	private Boolean showWrong = Boolean.valueOf(false);

	public TaskStartReactorInv(Arena arena, final TaskPlayer taskPlayer, ArrayList<Integer> moves_) {
		super(45, Main.getMessagesManager().getGameMsg("taskInvTitle", arena, Main.getMessagesManager().getTaskName(taskPlayer.getActiveTask().getTaskType().toString()),
				taskPlayer.getActiveTask().getLocationName().getName()), arena, taskPlayer);
		Utils.fillInv(this.inv);

		this.inv.setItem(7, Main.getItemsManager().getItem("startReactor_info").getItem().getItem());

		this.moves = moves_;
		update();
		final TaskStartReactorInv inv = this;
		(new BukkitRunnable() {
			public void run() {
				inv.playSequence(taskPlayer.getPlayerInfo().getPlayer());
			}
		}).runTaskLater(Main.getPlugin(), 20L);
	}

	public static ArrayList<Integer> generateMoves() {
		ArrayList<Integer> out = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			out.add(Integer.valueOf(Utils.getRandomNumberInRange(0, 8)));
		}
		return out;
	}

	public void playSequence(final Player player) {
		final TaskStartReactorInv inv = this;

		this.canClick = Boolean.valueOf(false);
		setActivePlaySquare(Integer.valueOf(-1));
		update();
		this.playRunnable = (new BukkitRunnable() {
			Integer playSquare = Integer.valueOf(0);

			public void run() {
				Main.getSoundsManager().playSound("taskStartReactorSquare" + (((Integer) inv.getTaskPlayer().getMoves_().get(this.playSquare)) + 1), player,
						player.getLocation());
				inv.setActivePlaySquare(this.playSquare);
				inv.update();

				(new BukkitRunnable() {
					public void run() {

						playSquare++;
						inv.setActivePlaySquare(-1);
						inv.setActiveClickingSquare(0);
						inv.update();
					}
				}).runTaskLater(Main.getPlugin(), 11L);

				if (this.playSquare + 1 > inv.getTaskPlayer().getReactorState_() || inv.getIsDone().booleanValue()) {
					inv.setCanClick(Boolean.valueOf(true));
					cancel();
				}
			}
		}).runTaskTimer(Main.getPlugin(), 0L, 20L);
	}

	public void squareClick(final Player player, Integer id) {
		final TaskStartReactorInv inv = this;
		if (!this.canClick.booleanValue() || this.isDone.booleanValue()) {
			return;
		}

		inv.setCanClick(Boolean.valueOf(false));
		if (this.moves.get(this.activeClickingSquare) == id) {
			Main.getSoundsManager().playSound("taskStartReactorSquare" + (id + 1), player, player.getLocation());
			this.activeClickingSquare = Integer.valueOf(this.activeClickingSquare + 1);
			setActivePlaySquareRight(id);
			(new BukkitRunnable() {
				public void run() {
					inv.setActivePlaySquareRight(Integer.valueOf(-1));
					if (inv.getActiveClickingSquare() > inv.getTaskPlayer().getReactorState_()) {
						inv.setCanClick(Boolean.valueOf(false));
						inv.getTaskPlayer().setReactorState_(Integer.valueOf(inv.getTaskPlayer().getReactorState_() + 1));
						inv.setActiveClickingSquare(Integer.valueOf(0));
						if (inv.getTaskPlayer().getReactorState_() >= 5) {
							inv.setIsDone(Boolean.valueOf(true));
							inv.checkDone();
						} else {
							(new BukkitRunnable() {
								public void run() {
									inv.playSequence(player);
								}
							}).runTaskLater(Main.getPlugin(), 12L);
						}
						inv.update();
					} else {
						inv.setCanClick(Boolean.valueOf(true));
					}
					inv.update();
				}
			}).runTaskLater(Main.getPlugin(), 10L);
		} else {

			Main.getSoundsManager().playSound("taskStartReactorClickWrong", player, player.getLocation());
			inv.setCanClick(Boolean.valueOf(false));
			this.showWrong = Boolean.valueOf(true);
			this.taskPlayer.updateTasksVars();
			this.moves = this.taskPlayer.getMoves_();

			this.activeClickingSquare = Integer.valueOf(0);
			this.activePlaySquareRight = Integer.valueOf(-1);
			(new BukkitRunnable() {
				Integer state = Integer.valueOf(0);

				public void run() {
					inv.setShowWrong(Boolean.valueOf(!inv.getShowWrong().booleanValue()));
					if (this.state >= 3) {
						inv.setShowWrong(Boolean.valueOf(false));
						inv.playSequence(player);
						cancel();
					}
					inv.update();
					this.state = Integer.valueOf(this.state + 1);
				}
			}).runTaskTimer(Main.getPlugin(), 10L, 10L);
		}

		checkDone();
		update();
	}

	public Boolean checkDone() {
		if (this.isDone.booleanValue()) {
			this.taskPlayer.taskDone();
			final TaskStartReactorInv inv = this;
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
		final TaskStartReactorInv inv = this;
		ItemInfoContainer squareItem = Main.getItemsManager().getItem("startReactor_square");
		ItemStack squareItemS = squareItem.getItem().getItem();
		ItemStack squareItemS3 = squareItem.getItem3().getItem();

		for (int i = 0; i < slotsLeft.size(); i++) {
			Integer slot = slotsLeft.get(i);
			ItemStack item_ = squareItemS;
			if (this.activePlaySquare != -1 && i == ((Integer) this.moves.get(this.activePlaySquare))) {
				item_ = squareItemS3;
			}

			this.inv.setItem(slot, item_);
		}

		ItemInfoContainer squareItemWrong = Main.getItemsManager().getItem("startReactor_squareWrong");
		ItemStack squareItemS2 = squareItem.getItem2().getItem();
		for (int i = 0; i < slotsRight.size(); i++) {
			ItemStack item_ = this.canClick.booleanValue() ? squareItemS2 : squareItemS;
			if (this.activePlaySquareRight != -1 && i == this.activePlaySquareRight) {
				item_ = squareItemS3;
			}

			if (this.showWrong.booleanValue()) {
				item_ = squareItemWrong.getItem().getItem();
			}
			Icon icon = new Icon(item_);
			final Integer id = Integer.valueOf(i);
			if (this.canClick.booleanValue()) {
				icon.addClickAction(new ClickAction() {
					public void execute(Player player) {
						inv.squareClick(player, id);
					}
				});
			}
			setIcon(((Integer) slotsRight.get(i)), icon);
		}

		ItemInfoContainer progressIndicatorItem = Main.getItemsManager().getItem("startReactor_progressIndicator");
		ItemStack progressIndicatorItemS = progressIndicatorItem.getItem().getItem();
		ItemStack progressIndicatorItem2S = progressIndicatorItem.getItem2().getItem();

		for (int i = 0; i < 5; i++) {
			if (i <= this.taskPlayer.getReactorState_()) {
				this.inv.setItem(((Integer) slotsProgressLeft.get(i)), progressIndicatorItem2S);
			} else {
				this.inv.setItem(((Integer) slotsProgressLeft.get(i)), progressIndicatorItemS);
			}

			if (i < getActiveClickingSquare()) {
				this.inv.setItem(((Integer) slotsProgressRight.get(i)), progressIndicatorItem2S);
			} else {
				this.inv.setItem(((Integer) slotsProgressRight.get(i)), progressIndicatorItemS);
			}
		}
	}

	public void invClosed() {
		if (this.playRunnable != null) {
			this.playRunnable.cancel();
			this.playRunnable = null;
		}
	}

	public void setIsDone(Boolean is) {
		this.isDone = is;
	}

	public Boolean getIsDone() {
		return this.isDone;
	}

	public Integer getActivePlaySquare() {
		return this.activePlaySquare;
	}

	public void setActivePlaySquare(Integer activePlaySquare) {
		this.activePlaySquare = activePlaySquare;
	}

	public Integer getActiveClickingSquare() {
		return this.activeClickingSquare;
	}

	public void setActiveClickingSquare(Integer activeClickingSquare) {
		this.activeClickingSquare = activeClickingSquare;
	}

	public Boolean getCanClick() {
		return this.canClick;
	}

	public void setCanClick(Boolean canClick) {
		this.canClick = canClick;
	}

	public Integer getActivePlaySquareRight() {
		return this.activePlaySquareRight;
	}

	public void setActivePlaySquareRight(Integer activePlaySquareRight) {
		this.activePlaySquareRight = activePlaySquareRight;
	}

	public Boolean getShowWrong() {
		return this.showWrong;
	}

	public void setShowWrong(Boolean showWrong) {
		this.showWrong = showWrong;
	}
}
