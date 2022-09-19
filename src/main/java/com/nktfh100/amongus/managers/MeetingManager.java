package com.nktfh100.amongus.managers;

import com.nktfh100.AmongUs.enums.GameState;
import com.nktfh100.AmongUs.enums.SabotageType;
import com.nktfh100.AmongUs.enums.StatInt;
import com.nktfh100.AmongUs.info.Arena;
import com.nktfh100.AmongUs.info.DeadBody;
import com.nktfh100.AmongUs.info.DoorGroup;
import com.nktfh100.AmongUs.info.ItemInfo;
import com.nktfh100.AmongUs.info.PlayerInfo;
import com.nktfh100.AmongUs.info.VitalsPlayerInfo;
import com.nktfh100.AmongUs.inventory.VotingInv;
import com.nktfh100.AmongUs.main.Main;
import com.nktfh100.AmongUs.utils.Packets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class MeetingManager {
	private Arena arena;

	public enum meetingState {
		DISCUSSION, VOTING, VOTING_RESULTS;
	}

	private Integer meetingCooldownTimer = Integer.valueOf(10);
	private Integer activeTimer = Integer.valueOf(0);

	private BukkitTask timerRunnable = null;

	private meetingState state;
	private ArrayList<Player> playersVoted = new ArrayList<>();
	private HashMap<String, ArrayList<PlayerInfo>> votes = new HashMap<>();
	private ArrayList<PlayerInfo> skipVotes = new ArrayList<>();

	private SabotageType activeSabotage = null;

	private Player whoCalled;
	private Boolean isSendingTitle = Boolean.valueOf(false);

	public MeetingManager(Arena arena) {
		this.arena = arena;
	}

	public void callMeeting(Player caller, Boolean isBodyFound, DeadBody db) {
		if (isBodyFound.booleanValue() && this.arena.getSabotageManager().getIsSabotageActive().booleanValue()) {
			SabotageType saboType = this.arena.getSabotageManager().getActiveSabotage().getType();
			if (saboType != SabotageType.COMMUNICATIONS) {
				this.arena.getSabotageManager().endSabotage(Boolean.valueOf(false), Boolean.valueOf(true), null);
				if (saboType == SabotageType.LIGHTS) {
					this.activeSabotage = saboType;
				}
			}
		}
		this.arena.setIsInMeeting(Boolean.valueOf(true));
		this.isSendingTitle = Boolean.valueOf(false);
		PlayerInfo callerInfo = Main.getPlayersManager().getPlayerInfo(caller);

		if (!isBodyFound.booleanValue()) {
			callerInfo.setMeetingsLeft(Integer.valueOf(callerInfo.getMeetingsLeft() - 1));
		}

		this.playersVoted.clear();
		this.skipVotes.clear();
		this.votes = new HashMap<>();
		int si = 0;
		for (PlayerInfo pInfo : this.arena.getPlayersInfo()) {
			Player player = pInfo.getPlayer();

			if (!pInfo.isGhost().booleanValue()) {
				for (PotionEffect pe : player.getActivePotionEffects()) {
					player.removePotionEffect(pe.getType());
				}

				for (PlayerInfo pInfo1 : this.arena.getPlayersInfo()) {
					if (pInfo1 == null) {
						continue;
					}
					if (pInfo1.isGhost().booleanValue() && pInfo1 != pInfo) {
						Packets.sendPacket(pInfo.getPlayer(), Packets.REMOVE_PLAYER(pInfo1.getPlayer().getUniqueId(), pInfo1.getPlayer().getName(), pInfo1.getCustomName()));
					}
				}
			} else {
				VitalsPlayerInfo vpi = this.arena.getVitalsManager().getVitalsPInfo(player);
				vpi.setIsDead(Boolean.valueOf(true));
				vpi.setIsDC(Boolean.valueOf(true));
			}

			pInfo.meetingStarted();
			this.votes.put(pInfo.getPlayer().getUniqueId().toString(), new ArrayList<>());

			Boolean showPlayerToEveryone = Boolean.valueOf(false);

			if (pInfo.getIsImposter().booleanValue()) {
				if (pInfo.getIsInVent().booleanValue()) {
					this.arena.getVentsManager().playerLeaveVent(pInfo, Boolean.valueOf(true), Boolean.valueOf(false));
					showPlayerToEveryone = Boolean.valueOf(true);
				}
				pInfo.setKillCoolDown(Integer.valueOf(0));
				this.arena.getSabotageManager().getSabotageCooldownBossBar(pInfo.getPlayer()).removePlayer(pInfo.getPlayer());
			}
			if (pInfo.getIsInCameras().booleanValue()) {
				this.arena.getCamerasManager().playerLeaveCameras(pInfo, Boolean.valueOf(true));
				showPlayerToEveryone = Boolean.valueOf(true);
			}

			if (si >= this.arena.getPlayerSpawns().size()) {
				si = 0;
			}

			pInfo.setCanReportBody(Boolean.valueOf(false), null);
			player.teleport(this.arena.getPlayerSpawns().get(si));
			player.setAllowFlight(false);

			if (isBodyFound.booleanValue() && db != null) {
				player.sendTitle(
						Main.getMessagesManager().getGameMsg("bodyFoundTitle", this.arena, db.getPlayer().getName(), "" + db.getColor().getChatColor(), db.getColor().getName(),
								"" + pInfo.getColor().getChatColor(), pInfo.getColor().getName()),
						Main.getMessagesManager().getGameMsg("bodyFoundSubTitle", this.arena, db.getPlayer().getName(), "" + db.getColor().getChatColor(), db.getColor().getName(),
								"" + pInfo.getColor().getChatColor(), pInfo.getColor().getName()),
						15, 80, 15);
			} else {

				player.sendTitle(
						Main.getMessagesManager().getGameMsg("emergencyMeetingTitle", this.arena, caller.getName(), "" + callerInfo.getColor().getChatColor(), callerInfo.getColor().getName(), null),
						Main.getMessagesManager().getGameMsg("emergencyMeetingSubTitle", this.arena, caller.getName(), "" + callerInfo.getColor().getChatColor(), callerInfo.getColor().getName(),
								null),
						15, 80, 15);
			}

			if (pInfo.getIsImposter().booleanValue()) {
				pInfo.teleportImposterHolo();
			}

			pInfo.removeVisionBlocks();

			if (isBodyFound.booleanValue()) {
				Main.getSoundsManager().playSound("bodyReported", player, player.getLocation());
			} else {
				Main.getSoundsManager().playSound("meetingStarted", player, player.getLocation());
			}

			if (!pInfo.isGhost().booleanValue()) {
				for (PlayerInfo pInfo1 : this.arena.getPlayersInfo()) {
					if (pInfo != pInfo1 && !pInfo1.isGhost().booleanValue()) {
						this.arena.getVisibilityManager().showPlayer(pInfo, pInfo1, Boolean.valueOf(true));
						if (showPlayerToEveryone.booleanValue()) {
							this.arena.getVisibilityManager().showPlayer(pInfo1, pInfo, Boolean.valueOf(true));
						}
					}
				}
			}

			ItemInfo voteItem = Main.getItemsManager().getItem("vote").getItem();
			player.getInventory().clear();
			pInfo.giveArmor();
			player.getInventory().setItem(voteItem.getSlot(), voteItem.getItem());
			si++;
		}

		this.arena.getDeadBodiesManager().deleteAll();

		this.arena.getDoorsManager().openDoorsForce();

		if (isBodyFound.booleanValue()) {
			Main.getConfigManager().executeCommands("reportedBody", caller);
			callerInfo.getStatsManager().plusOneStatInt(StatInt.BODIES_REPORTED);
			Main.getCosmeticsManager().addCoins("reportedBody", caller);
		} else {
			Main.getConfigManager().executeCommands("calledMeeting", caller);
			callerInfo.getStatsManager().plusOneStatInt(StatInt.EMERGENCIES_CALLED);
			Main.getCosmeticsManager().addCoins("calledMeeting", caller);
		}

		this.whoCalled = caller;
		setState(meetingState.DISCUSSION);
		setActiveTimer(this.arena.getDiscussionTime());
		if (this.timerRunnable != null) {
			this.timerRunnable.cancel();
		}
		final MeetingManager manager = this;
		this.timerRunnable = (new BukkitRunnable() {
			public void run() {
				if (manager.getActiveTimer() > 0) {
					manager.setActiveTimer(Integer.valueOf(manager.getActiveTimer() - 1));
				} else {
					switch (manager.getState()) {
					case DISCUSSION:
						manager.setState(meetingState.VOTING);
						manager.setActiveTimer(manager.getArena().getVotingTime());
						return;
					case VOTING:
						manager.setState(meetingState.VOTING_RESULTS);
						manager.setActiveTimer(MeetingManager.this.arena.getProceedingTime());
						for (PlayerInfo pInfo : manager.getArena().getPlayersInfo()) {
							if (pInfo.getPlayer().getOpenInventory().getTopInventory().getHolder() instanceof VotingInv) {
								manager.openVoteInv(pInfo);
							}
						}
						manager.getArena().sendMessage(manager.getVotingResults());
						return;

					case VOTING_RESULTS:
						manager.startEndMeetingTitle();
						cancel();
						return;
					}

				}
			}
		}).runTaskTimer(Main.getPlugin(), 20L, 20L);
		updateInv();
		this.arena.getBtnHolo().getVisibilityManager().resetVisibilityAll();
		this.arena.getBtnHolo().getVisibilityManager().setVisibleByDefault(false);
	}

	public void startEndMeetingTitle() {
		final MeetingManager manager = this;
		this.isSendingTitle = Boolean.valueOf(true);
		for (Player p : getArena().getPlayers()) {
			if (p.getOpenInventory().getTopInventory().getHolder() instanceof VotingInv) {
				p.closeInventory();
			}
		}

		Integer highestVotes = Integer.valueOf(this.skipVotes.size());
		String highestPlayer = "skip";

		Integer highestVotes2 = Integer.valueOf(0);

		for (Entry<String, ArrayList<PlayerInfo>> en : this.votes.entrySet()) {
			if (en.getValue().size() > highestVotes) {
				highestVotes = en.getValue().size();
				highestPlayer = en.getKey();
			} else if (en.getValue().size() > highestVotes2) {
				highestVotes2 = en.getValue().size();
			}
		}

		PlayerInfo pInfoEject_ = null;

		String title_ = "";
		String subTitle_ = "";

		if (highestVotes == highestVotes2 || highestPlayer == "skip") {
			String cause = "tie";
			if (highestPlayer == "skip") {
				cause = "skipped";
			}
			title_ = Main.getMessagesManager().getGameMsg("noOneEjectedTitle" + (this.arena.getConfirmEjects().booleanValue() ? "" : "1"), this.arena,
					Main.getMessagesManager().getGameMsg(cause, this.arena, null), "" + this.arena.getImpostersAlive().size());
			subTitle_ = Main.getMessagesManager().getGameMsg("noOneEjectedSubTitle" + (this.arena.getConfirmEjects().booleanValue() ? "" : "1"), this.arena,
					Main.getMessagesManager().getGameMsg(cause, this.arena, null), "" + this.arena.getImpostersAlive().size());
		} else if (highestPlayer != "skip") {
			pInfoEject_ = Main.getPlayersManager().getPlayerByUUID(highestPlayer);
			Integer numImposters = Integer.valueOf(this.arena.getImpostersAlive().size());
			if (pInfoEject_.getIsImposter().booleanValue()) {
				numImposters = Integer.valueOf(numImposters - 1);
			}
			String titleKey = "playerWasTheImposterTitle";
			String subTitleKey = "playerWasTheImposterSubTitle";
			if (this.arena.getConfirmEjects().booleanValue()) {
				if (this.arena.getNumImposters() == 1) {
					if (!pInfoEject_.getIsImposter().booleanValue()) {
						titleKey = "playerWasNotTheImposterTitle";
						subTitleKey = "playerWasNotTheImposterSubTitle";
					}

				} else if (pInfoEject_.getIsImposter().booleanValue()) {
					titleKey = "playerWasAnImposterTitle";
					subTitleKey = "playerWasAnImposterSubTitle";
				} else {
					titleKey = "playerWasNotAnImposterTitle";
					subTitleKey = "playerWasNotAnImposterSubTitle";
				}
			} else {

				titleKey = "playerEjectedTitle";
				subTitleKey = "playerEjectedSubTitle";
			}
			title_ = Main.getMessagesManager().getGameMsg(titleKey, this.arena, pInfoEject_.getPlayer().getName(), "" + pInfoEject_.getColor().getChatColor(), "" + numImposters, null);
			subTitle_ = Main.getMessagesManager().getGameMsg(subTitleKey, this.arena, pInfoEject_.getPlayer().getName(), "" + pInfoEject_.getColor().getChatColor(), "" + numImposters, null);
		}

		for (PlayerInfo pInfo : this.arena.getPlayersInfo()) {
			pInfo.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 2147483647, 1, false, false));
		}

		final PlayerInfo pInfoEject = pInfoEject_;
		final String title = title_;
		final String subTitle = subTitle_;

		(new BukkitRunnable() {
			int subTitleIndex = 0;

			public void run() {
				if (manager.getArena().getGameState() != GameState.RUNNING) {
					cancel();
					return;
				}
				if (this.subTitleIndex < (subTitle.toCharArray()).length) {
					manager.getArena().sendTitle("", subTitle.substring(0, this.subTitleIndex + 1), 0, 35, 0);
					this.subTitleIndex++;
				} else {
					(new BukkitRunnable() {
						public void run() {
							if (manager.getArena().getGameState() != GameState.RUNNING) {
								cancel();

								return;
							}
							manager.getArena().sendTitle(title, subTitle, 0, 40, 15);
							(new BukkitRunnable() {
								public void run() {
									if (manager.getArena().getGameState() != GameState.RUNNING) {
										cancel();
										return;
									}
									for (PlayerInfo pInfo : manager.getArena().getPlayersInfo()) {
										pInfo.getPlayer().removePotionEffect(PotionEffectType.BLINDNESS);
									}
									manager.endMeeting(Boolean.valueOf(false), pInfoEject);
								}
							}).runTaskLater(Main.getPlugin(), 40L);
						}
					}).runTaskLater(Main.getPlugin(), 30L);
					cancel();

					return;
				}
			}
		}).runTaskTimer(Main.getPlugin(), 0L, 2L);
	}

	public void endMeeting(Boolean isForce, PlayerInfo pInfoEject) {
		setState(meetingState.DISCUSSION);
		setActiveTimer(Integer.valueOf(0));
		if (this.timerRunnable != null) {
			this.timerRunnable.cancel();
		}

		this.arena.setIsInMeeting(Boolean.valueOf(false));
		setIsSendingTitle(Boolean.valueOf(false));

		for (PlayerInfo pInfo : this.arena.getPlayersInfo()) {
			this.arena.giveGameInventory(pInfo);
			pInfo.meetingEnded();
			if (!isForce.booleanValue()) {
				this.arena.getVisibilityManager().playerMoved(pInfo);
			}
			if (pInfo.isGhost().booleanValue() && Main.getConfigManager().getGhostsFly().booleanValue()) {
				pInfo.getPlayer().setAllowFlight(true);
			}
		}

		this.votes = new HashMap<>();
		this.playersVoted.clear();
		this.skipVotes.clear();
		this.whoCalled = null;
		this.arena.getBtnHolo().getVisibilityManager().resetVisibilityAll();
		this.arena.getBtnHolo().getVisibilityManager().setVisibleByDefault(true);

		if (pInfoEject != null && !isForce.booleanValue()) {
			this.arena.playerDeath(null, pInfoEject, Boolean.valueOf(false));
		}

		setMeetingCooldownTimer(this.arena.getMeetingCooldown());
		for (PlayerInfo pInfo_ : this.arena.getGameImposters()) {
			int s_ = 9;
			this.arena.getSabotageManager().setSabotageCoolDownTimer(pInfo_.getPlayer().getUniqueId().toString(), this.arena.getSabotageCooldown());
			for (DoorGroup dg : this.arena.getDoorsManager().getDoorGroups()) {
				dg.setCooldownTimer(pInfo_.getPlayer().getUniqueId().toString(), this.arena.getDoorCooldown());
				pInfo_.getPlayer().getInventory().setItem(s_, this.arena.getDoorsManager().getSabotageDoorItem(pInfo_.getPlayer(), dg.getId()));
				s_++;
			}
			if (!pInfo_.isGhost().booleanValue()) {
				pInfo_.setKillCoolDown(this.arena.getKillCooldown());
			}
		}

		if (pInfoEject != null) {
			Main.getConfigManager().executeCommands("ejected", pInfoEject.getPlayer());
			pInfoEject.getStatsManager().plusOneStatInt(StatInt.TIMES_EJECTED);
			Main.getCosmeticsManager().addCoins("ejected", pInfoEject.getPlayer());
		}

		if (!isForce.booleanValue()) {
			final Integer winState = this.arena.getWinState(Boolean.valueOf(false));
			if (winState != 0) {
				this.arena.setGameState(GameState.FINISHING);
				for (PlayerInfo pInfo : this.arena.getPlayersInfo()) {
					pInfo.removeVisionBlocks();
				}
				(new BukkitRunnable() {
					public void run() {
						MeetingManager.this.arena.gameWin(Boolean.valueOf((winState == 2)));
					}
				}).runTaskLater(Main.getPlugin(), 80L);
			} else if (this.activeSabotage != null) {
				this.arena.getSabotageManager().startSabotage(this.arena.getSabotageArena(this.activeSabotage));
				this.activeSabotage = null;
			}
		}
	}

	public String getVotingResults() {
		MessagesManager messagesManager = Main.getMessagesManager();
		StringBuilder outputB = new StringBuilder();
		for (String uuid1 : this.votes.keySet()) {
			PlayerInfo pInfo1 = Main.getPlayersManager().getPlayerByUUID(uuid1);
			if (this.votes.get(uuid1).size() > 0) {
				String symbols = "";
				for (PlayerInfo voterInfo : this.votes.get(pInfo1.getPlayer().getUniqueId().toString())) {
					String symbol_ = messagesManager.getGameMsg("voteSymbol", this.arena, "" + voterInfo.getColor().getChatColor());
					symbols = String.valueOf(symbols) + symbol_;
				}
				String line_ = messagesManager.getGameMsg("playerLine", this.arena, pInfo1.getPlayer().getName(), "" + pInfo1.getColor().getChatColor(), symbols, null);
				outputB.append(String.valueOf(line_) + "\n");
			}
		}

		String symbols = "";
		for (PlayerInfo voterInfo : this.skipVotes) {
			String symbol_ = messagesManager.getGameMsg("voteSymbol", this.arena, "" + voterInfo.getColor().getChatColor());
			symbols = String.valueOf(symbols) + symbol_;
		}
		outputB.append(messagesManager.getGameMsg("skipVoteLine", this.arena, symbols));
		return messagesManager.getGameMsg("votingResults", this.arena, outputB.toString());
	}

	public void updateInv() {
		for (Player player : this.arena.getPlayers()) {
			if (player.getOpenInventory().getTopInventory().getHolder() instanceof VotingInv) {
				((VotingInv) player.getOpenInventory().getTopInventory().getHolder()).update();
			}
		}
	}

	public void openVoteInv(PlayerInfo pInfo_) {
		if (getArena().getIsInMeeting().booleanValue()) {
			VotingInv votingInv = new VotingInv(this.arena, pInfo_);
			pInfo_.getPlayer().openInventory(votingInv.getInventory());
		}
	}

	public Boolean canVote(PlayerInfo pInfo) {
		return Boolean.valueOf(!this.playersVoted.contains(pInfo.getPlayer()));
	}

	public void didEveryoneVote() {
		Integer size = Integer.valueOf(0);
		for (PlayerInfo pInfo_ : this.arena.getPlayersInfo()) {
			if (!pInfo_.isGhost().booleanValue()) {
				size = Integer.valueOf(size + 1);
			}
		}
		if (this.playersVoted.size() >= size) {
			setState(meetingState.VOTING_RESULTS);
			setActiveTimer(Integer.valueOf(10));
			for (PlayerInfo pInfo : getArena().getPlayersInfo()) {
				if (pInfo.getPlayer().getOpenInventory().getTopInventory().getHolder() instanceof VotingInv) {
					openVoteInv(pInfo);
				}
			}
			getArena().sendMessage(getVotingResults());
		}
	}

	public void vote(PlayerInfo voter, PlayerInfo voted) {
		if (this.state == meetingState.VOTING && canVote(voter).booleanValue() && voter != null && voted != null && voted.getPlayer().isOnline()) {
			if (this.votes.get(voted.getPlayer().getUniqueId().toString()) != null) {
				((ArrayList<PlayerInfo>) this.votes.get(voted.getPlayer().getUniqueId().toString())).add(voter);
			} else {
				this.votes.put(voted.getPlayer().getUniqueId().toString(), new ArrayList<>());
				((ArrayList<PlayerInfo>) this.votes.get(voted.getPlayer().getUniqueId().toString())).add(voter);
			}
			this.playersVoted.add(voter.getPlayer());
			this.arena.sendMessage(Main.getMessagesManager().getGameMsg("playerVoted", this.arena, voter.getPlayer().getName(), "" + voter.getColor().getChatColor()));
			updateInv();
			didEveryoneVote();
			for (Player player : this.arena.getPlayers()) {
				Main.getSoundsManager().playSound("playerVoted", player, player.getLocation());
			}
		}
	}

	public void voteSkip(PlayerInfo voter) {
		if (this.state == meetingState.VOTING && canVote(voter).booleanValue()) {
			this.skipVotes.add(voter);
			this.playersVoted.add(voter.getPlayer());
			this.arena.sendMessage(Main.getMessagesManager().getGameMsg("playerVoted", this.arena, voter.getPlayer().getName(), "" + voter.getColor().getChatColor()));
			updateInv();
			didEveryoneVote();
			for (Player player : this.arena.getPlayers()) {
				Main.getSoundsManager().playSound("playerVoted", player, player.getLocation());
			}
		}
	}

	public void delete() {
		this.arena = null;
		this.meetingCooldownTimer = null;
		this.activeTimer = null;
		this.timerRunnable = null;
		this.state = null;
		this.playersVoted = null;
		this.votes = null;
		this.skipVotes = null;
		this.activeSabotage = null;
		this.whoCalled = null;
		this.isSendingTitle = null;
	}

	public Integer getActiveTimer() {
		return this.activeTimer;
	}

	public void setActiveTimer(Integer activeTimer) {
		this.activeTimer = activeTimer;
		updateInv();
	}

	public ArrayList<PlayerInfo> getVotes(Player p) {
		return this.votes.get(p.getUniqueId().toString());
	}

	public ArrayList<PlayerInfo> getSkippedVotes() {
		return this.skipVotes;
	}

	public meetingState getState() {
		return this.state;
	}

	public void setState(meetingState state) {
		this.state = state;
	}

	public Arena getArena() {
		return this.arena;
	}

	public Player getWhoCalled() {
		return this.whoCalled;
	}

	public void setWhoCalled(Player whoCalled) {
		this.whoCalled = whoCalled;
	}

	public Integer getMeetingCooldownTimer() {
		return this.meetingCooldownTimer;
	}

	public void setMeetingCooldownTimer(Integer meetingCooldownTimer) {
		this.meetingCooldownTimer = meetingCooldownTimer;
	}

	public Boolean getIsSendingTitle() {
		return this.isSendingTitle;
	}

	public void setIsSendingTitle(Boolean isSendingTitle) {
		this.isSendingTitle = isSendingTitle;
	}
}
