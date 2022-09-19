package com.nktfh100.amongus.inventory;

import com.nktfh100.AmongUs.info.Arena;
import com.nktfh100.AmongUs.info.ItemInfo;
import com.nktfh100.AmongUs.info.ItemInfoContainer;
import com.nktfh100.AmongUs.info.PlayerInfo;
import com.nktfh100.AmongUs.main.Main;
import com.nktfh100.AmongUs.managers.MeetingManager;
import com.nktfh100.AmongUs.utils.Utils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class VotingInv extends CustomHolder {
	private static final Integer pageSize = Integer.valueOf(10);

	private Arena arena;
	private PlayerInfo pInfo;
	private PlayerInfo activePInfo = null;
	private Boolean skipVoteActive = Boolean.valueOf(false);
	private Integer currentPage = Integer.valueOf(1);
	private ArrayList<PlayerInfo> players = new ArrayList<>();

	public VotingInv(Arena arena, PlayerInfo pInfo) {
		super(Integer.valueOf(54), (arena.getMeetingManager().getState() == MeetingManager.meetingState.VOTING_RESULTS) ? Main.getMessagesManager().getGameMsg("votingInvTitle1", arena, null)
				: Main.getMessagesManager().getGameMsg("votingInvTitle", arena, null));
		this.arena = arena;
		this.pInfo = pInfo;
		Utils.fillInv(this.inv);

		this.players = new ArrayList<>(arena.getPlayersInfo());
		ArrayList<PlayerInfo> playersGhosts = new ArrayList<>(arena.getPlayersInfo());
		playersGhosts.removeIf(n -> !n.isGhost().booleanValue());
		this.players.removeIf(n -> n.isGhost().booleanValue());

		this.players.remove(this.pInfo);
		Collections.sort(this.players, new Comparator<PlayerInfo>() {
			public int compare(PlayerInfo o1, PlayerInfo o2) {
				return o1.getJoinedId().compareTo(o2.getJoinedId());
			}
		});
		playersGhosts.remove(this.pInfo);
		Collections.sort(playersGhosts, new Comparator<PlayerInfo>() {
			public int compare(PlayerInfo o1, PlayerInfo o2) {
				return o1.getJoinedId().compareTo(o2.getJoinedId());
			}
		});
		this.players.add(0, this.pInfo);
		this.players.addAll(playersGhosts);
		update();
	}

	private static final ArrayList<Integer> playersSlots = new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(2), Integer.valueOf(6), Integer.valueOf(11), Integer.valueOf(15),
			Integer.valueOf(20), Integer.valueOf(24), Integer.valueOf(29), Integer.valueOf(33), Integer.valueOf(38), Integer.valueOf(42) }));

	public void update() {
		MeetingManager manager = getArena().getMeetingManager();
		final VotingInv votingInv = this;
		this.icons.clear();

		ItemInfoContainer fillItem = Main.getItemsManager().getItem("voting_fill");
		if (manager.getState() == MeetingManager.meetingState.VOTING && !this.pInfo.isGhost().booleanValue()) {
			if (manager.canVote(this.pInfo).booleanValue()) {
				Utils.fillInv(this.inv, fillItem.getItem2().getMat());
			} else {
				Utils.fillInv(this.inv, fillItem.getItem().getMat());
			}
		} else {
			Utils.fillInv(this.inv, fillItem.getItem().getMat());
		}

		Integer totalItems = Integer.valueOf(this.players.size());
		Integer totalPages = Integer.valueOf((int) Math.ceil((double) totalItems / (double) pageSize));

		if (totalPages > 1) {
			this.inv.setItem(49, Main.getItemsManager().getItem("voting_currentPage").getItem().getItem("" + this.currentPage, "" + totalPages));

			if (this.currentPage > 1) {
				Icon icon = new Icon(Main.getItemsManager().getItem("voting_prevPage").getItem().getItem("" + this.currentPage, "" + totalPages));
				icon.addClickAction(new ClickAction() {
					public void execute(Player player) {
						votingInv.setCurrentPage(Integer.valueOf(VotingInv.this.currentPage - 1));
					}
				});
				setIcon(48, icon);
			}
			if (this.currentPage < totalPages) {
				Icon icon = new Icon(Main.getItemsManager().getItem("voting_nextPage").getItem().getItem("" + this.currentPage, "" + totalPages));
				icon.addClickAction(new ClickAction() {
					public void execute(Player player) {
						votingInv.setCurrentPage(Integer.valueOf(VotingInv.this.currentPage + 1));
					}
				});
				setIcon(50, icon);
			}
		}

		if (manager.getState() != MeetingManager.meetingState.VOTING) {
			this.activePInfo = null;
			this.skipVoteActive = Boolean.valueOf(false);
		}

		String votedSkipLine = "";
		if (manager.getState() == MeetingManager.meetingState.VOTING_RESULTS) {
			ArrayList<PlayerInfo> votedForSkip = manager.getSkippedVotes();
			StringBuilder votesLine = new StringBuilder();
			for (PlayerInfo voterPInfo : votedForSkip) {
				if (voterPInfo != null && voterPInfo.getColor() != null) {
					votesLine.append(Main.getMessagesManager().getGameMsg("voteSymbol", this.arena, "" + voterPInfo.getColor().getChatColor()));
				}
			}
			votedSkipLine = votesLine.toString();
		}
		ItemInfo deadIndicatorItem = Main.getItemsManager().getItem("voting_player_dead").getItem();
		ItemInfo votedIndicatorItem = Main.getItemsManager().getItem("voting_player_voted").getItem();

		Integer startIndex = Integer.valueOf((this.currentPage - 1) * pageSize);
		Integer endIndex = Integer.valueOf(Math.min(startIndex + pageSize - 1, totalItems - 1));

		Integer slotI = Integer.valueOf(0);
		for (int i = startIndex; i <= endIndex; i++) {
			final PlayerInfo pInfo_ = this.players.get(i);
			if (pInfo_ != null) {

				ItemInfoContainer playerItem = Main.getItemsManager().getItem("voting_player");
				if (this.pInfo.isGhost().booleanValue() || !manager.canVote(this.pInfo).booleanValue()) {
					playerItem = Main.getItemsManager().getItem("voting_player_cantVote");
				}
				ArrayList<String> lore = new ArrayList<>();

				if (manager.getWhoCalled() == pInfo_.getPlayer()) {
					ItemInfo callerItem = Main.getItemsManager().getItem("voting_calledMeeting").getItem();
					this.inv.setItem(((Integer) playersSlots.get(slotI)) + 1, callerItem.getItem(pInfo_.getPlayer().getName(), null));
				}

				Object object1 = (this.pInfo.getIsImposter().booleanValue() && pInfo_.getIsImposter().booleanValue()) ? ChatColor.DARK_RED : "";
				String playerTitle = playerItem.getItem().getTitle(pInfo_.getPlayer().getName(), "" + pInfo_.getColor().getChatColor(), pInfo_.getColor().toString().toLowerCase(), "" + object1, null);
				Boolean didAddLore = Boolean.valueOf(false);
				if (manager.getState() == MeetingManager.meetingState.VOTING_RESULTS) {
					ArrayList<PlayerInfo> votedForP = manager.getVotes(pInfo_.getPlayer());
					StringBuilder votesLine = new StringBuilder();
					for (PlayerInfo voterPInfo : votedForP) {
						if (voterPInfo != null && voterPInfo.getColor() != null && this.arena != null) {
							votesLine.append(Main.getMessagesManager().getGameMsg("voteSymbol", this.arena, "" + voterPInfo.getColor().getChatColor()));
						}
					}
					lore.add(votesLine.toString());
				}
				if (!pInfo_.isGhost().booleanValue() && !manager.canVote(pInfo_).booleanValue()) {
					lore.addAll(playerItem.getItem2().getLore(pInfo_.getPlayer().getName(), "" + pInfo_.getColor().getChatColor(), pInfo_.getColor().toString().toLowerCase(), "" + object1, null));
					playerTitle = playerItem.getItem2().getTitle(pInfo_.getPlayer().getName(), "" + pInfo_.getColor().getChatColor(), pInfo_.getColor().toString().toLowerCase(), "" + object1, null);
					didAddLore = Boolean.valueOf(true);
					ItemStack votedIndicator_ = Utils.createItem(votedIndicatorItem.getMat(), votedIndicatorItem.getTitle(pInfo_.getPlayer().getName()), 1,
							votedIndicatorItem.getLore(pInfo_.getPlayer().getName()));
					this.inv.setItem(((Integer) playersSlots.get(slotI)) - 1, votedIndicator_);
				}
				if (pInfo_.isGhost().booleanValue()) {
					lore.addAll(playerItem.getItem3().getLore(pInfo_.getPlayer().getName(), "" + pInfo_.getColor().getChatColor(), pInfo_.getColor().toString().toLowerCase(), "" + object1, null));
					playerTitle = playerItem.getItem3().getTitle(pInfo_.getPlayer().getName(), "" + pInfo_.getColor().getChatColor(), pInfo_.getColor().toString().toLowerCase(), "" + object1, null);
					didAddLore = Boolean.valueOf(true);

					ItemStack deadIndicator_ = Utils.createItem(deadIndicatorItem.getMat(), deadIndicatorItem.getTitle(pInfo_.getPlayer().getName()), 1,
							deadIndicatorItem.getLore(pInfo_.getPlayer().getName()));
					this.inv.setItem(((Integer) playersSlots.get(slotI)) + 1, deadIndicator_);
					this.inv.setItem(((Integer) playersSlots.get(slotI)) - 1, deadIndicator_);
				}

				if (this.activePInfo == pInfo_ && !this.skipVoteActive.booleanValue()) {
					ItemInfo acceptItem = Main.getItemsManager().getItem("voting_vote_accept").getItem();
					ItemInfo cancelItem = Main.getItemsManager().getItem("voting_vote_cancel").getItem();

					Icon icon = new Icon(acceptItem.getItem(pInfo_.getPlayer().getName(), null));
					icon.addClickAction(new ClickAction() {
						public void execute(Player player) {
							votingInv.handleAcceptClick();
						}
					});
					setIcon(((Integer) playersSlots.get(slotI)) + 1, icon);

					Icon icon1 = new Icon(Utils.createItem(cancelItem.getMat(), cancelItem.getTitle(pInfo_.getPlayer().getName()), 1, cancelItem.getLore(pInfo_.getPlayer().getName())));
					icon1.addClickAction(new ClickAction() {
						public void execute(Player player) {
							votingInv.handleCancelClick();
						}
					});
					setIcon(((Integer) playersSlots.get(slotI)) + 2, icon1);
				}

				if (!didAddLore.booleanValue()) {
					lore.addAll(playerItem.getItem().getLore(pInfo_.getPlayer().getName(), "" + pInfo_.getColor().getChatColor(), pInfo_.getColor().toString().toLowerCase(), "" + object1, null));
				}

				Integer playerAmount = Integer.valueOf(1);
				if (manager.getState() == MeetingManager.meetingState.VOTING_RESULTS) {
					playerAmount = Integer.valueOf(manager.getVotes(pInfo_.getPlayer()).size());
					if (playerAmount == 0) {
						playerAmount = Integer.valueOf(1);
					}
				}

				ItemStack item = pInfo_.getHead().clone();
				Utils.setItemName(item, ChatColor.WHITE + playerTitle, lore);
				item.setAmount(playerAmount);

				Icon icon = new Icon(item);
				if (!this.pInfo.isGhost().booleanValue() && !pInfo_.isGhost().booleanValue() && manager.canVote(this.pInfo).booleanValue()) {
					icon.addClickAction(new ClickAction() {
						public void execute(Player player) {
							votingInv.handleHeadClick(pInfo_);
						}
					});
				}

				setIcon(((Integer) playersSlots.get(slotI)), icon);
				slotI = Integer.valueOf(slotI + 1);
			}
		}
		ItemInfoContainer skipItem = Main.getItemsManager().getItem("voting_skip");
		ItemStack skipItemS = null;
		if (manager.getState() == MeetingManager.meetingState.VOTING) {
			skipItemS = skipItem.getItem().getItem();
		} else if (manager.getState() == MeetingManager.meetingState.VOTING_RESULTS) {
			skipItemS = skipItem.getItem2().getItem();
		} else if (manager.getState() == MeetingManager.meetingState.DISCUSSION) {
			skipItemS = skipItem.getItem3().getItem();
		}

		ArrayList<String> skipLore = new ArrayList<>();
		if (manager.getState() == MeetingManager.meetingState.VOTING_RESULTS) {
			skipLore.add(votedSkipLine);
			skipLore.addAll(skipItem.getItem2().getLore());
		} else {
			skipLore = skipItem.getItem().getLore();
			if (manager.getState() == MeetingManager.meetingState.DISCUSSION || !manager.canVote(this.pInfo).booleanValue()) {
				skipLore = skipItem.getItem3().getLore();
			}
		}
		if (manager.getState() == MeetingManager.meetingState.DISCUSSION || !manager.canVote(this.pInfo).booleanValue()) {
			skipItemS = skipItem.getItem3().getItem();
		}

		Integer skipAmount = Integer.valueOf(1);
		if (manager.getState() == MeetingManager.meetingState.VOTING_RESULTS) {
			skipAmount = Integer.valueOf(manager.getSkippedVotes().size());
			if (skipAmount == 0) {
				skipAmount = Integer.valueOf(1);
			}
		}

		if (!this.pInfo.isGhost().booleanValue() || manager.getState() == MeetingManager.meetingState.VOTING_RESULTS) {
			Utils.setItemLore(skipItemS, skipLore);
			skipItemS.setAmount(skipAmount);
			Icon skipIcon = new Icon(skipItemS);
			if (manager.canVote(this.pInfo).booleanValue()) {
				skipIcon.addClickAction(new ClickAction() {
					public void execute(Player player) {
						votingInv.handleSkipClick();
					}
				});
			}
			setIcon(skipItem.getSlot(), skipIcon);
		}

		if (this.skipVoteActive.booleanValue()) {
			ItemInfoContainer acceptItem = Main.getItemsManager().getItem("voting_vote_accept");
			ItemInfoContainer cancelItem = Main.getItemsManager().getItem("voting_vote_cancel");

			Icon icon = new Icon(acceptItem.getItem2().getItem());
			icon.addClickAction(new ClickAction() {
				public void execute(Player player) {
					votingInv.handleAcceptClick();
				}
			});
			setIcon(skipItem.getSlot() + 1, icon);

			Icon icon1 = new Icon(cancelItem.getItem2().getItem());
			icon1.addClickAction(new ClickAction() {
				public void execute(Player player) {
					votingInv.handleCancelClick();
				}
			});
			setIcon(skipItem.getSlot() + 2, icon1);
		}

		ItemInfoContainer infoItem = Main.getItemsManager().getItem("voting_info");
		Integer integer = manager.getActiveTimer();
		Object object = "";
		if (manager.getActiveTimer() <= 10) {
			object = ChatColor.RED;
		}
		ItemStack infoItemS = infoItem.getItem().getItem("" + integer, "" + object);
		if (manager.getState() == MeetingManager.meetingState.VOTING) {
			infoItemS = infoItem.getItem2().getItem("" + integer, "" + object);
		} else if (manager.getState() == MeetingManager.meetingState.VOTING_RESULTS) {
			infoItemS = infoItem.getItem3().getItem("" + integer, "" + object);
		}
		Integer infoAmount = Integer.valueOf(1);
		if (getArena().getMeetingManager().getActiveTimer() > 0) {
			infoAmount = getArena().getMeetingManager().getActiveTimer();
		}
		infoItemS.setAmount(infoAmount);
		this.inv.setItem(infoItem.getSlot(), infoItemS);
	}

	public void handleHeadClick(PlayerInfo clickedPInfo) {
		if (this.arena.getMeetingManager().getState() == MeetingManager.meetingState.VOTING && !clickedPInfo.isGhost().booleanValue()) {
			this.activePInfo = clickedPInfo;
			update();
		}
	}

	public void handleSkipClick() {
		if (this.arena.getMeetingManager().getState() == MeetingManager.meetingState.VOTING) {
			this.skipVoteActive = Boolean.valueOf(true);
			this.activePInfo = null;
			update();
		}
	}

	public void handleAcceptClick() {
		if (this.arena.getMeetingManager().getState() == MeetingManager.meetingState.VOTING) {
			if (this.skipVoteActive.booleanValue()) {
				this.skipVoteActive = Boolean.valueOf(false);
				getArena().getMeetingManager().voteSkip(this.pInfo);
			} else {
				PlayerInfo toVote = this.activePInfo;
				this.activePInfo = null;
				getArena().getMeetingManager().vote(this.pInfo, toVote);
			}
		}
	}

	public void handleCancelClick() {
		if (this.arena.getMeetingManager().getState() == MeetingManager.meetingState.VOTING) {
			this.activePInfo = null;
			this.skipVoteActive = Boolean.valueOf(false);
			update();
		}
	}

	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
		update();
	}

	public Arena getArena() {
		return this.arena;
	}

	public PlayerInfo getpInfo() {
		return this.pInfo;
	}

	public ArrayList<PlayerInfo> getPlayers() {
		return this.players;
	}

	public Integer getCurrentPage() {
		return this.currentPage;
	}
}
