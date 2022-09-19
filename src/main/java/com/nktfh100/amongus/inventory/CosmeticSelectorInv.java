package com.nktfh100.amongus.inventory;

import com.nktfh100.AmongUs.enums.CosmeticType;
import com.nktfh100.AmongUs.info.CosmeticItem;
import com.nktfh100.AmongUs.info.ItemInfo;
import com.nktfh100.AmongUs.info.PlayerInfo;
import com.nktfh100.AmongUs.main.Main;
import com.nktfh100.AmongUs.managers.CosmeticsManager;
import com.nktfh100.AmongUs.utils.Utils;
import java.util.ArrayList;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CosmeticSelectorInv extends CustomHolder {
	private static final Integer pageSize = Integer.valueOf(28);

	private PlayerInfo pInfo;
	private Integer page = Integer.valueOf(1);

	public CosmeticSelectorInv(PlayerInfo pInfo) {
		super(Integer.valueOf(54), Main.getMessagesManager().getGameMsg("cosmeticsSelectorInvTitle", null, null));
		this.pInfo = pInfo;
		Utils.addBorder(this.inv, Integer.valueOf(54), Main.getItemsManager().getItem("cosmeticsSelector_border").getItem().getMat());
		update();
	}

	public void update() {
		final CosmeticSelectorInv inv = this;
		clearInv();
		Utils.addBorder(this.inv, Integer.valueOf(54), Main.getItemsManager().getItem("cosmeticsSelector_border").getItem().getMat());
		CosmeticsManager cosmeticsManager = Main.getCosmeticsManager();

		ArrayList<CosmeticItem> items_ = cosmeticsManager.getOrderedCosmetics(CosmeticType.KILL_SWORD);
		Integer totalItems = Integer.valueOf(items_.size());
		Integer totalPages = Integer.valueOf((int) Math.ceil((double) totalItems / (double) pageSize));

		if (totalPages > 1) {
			final Integer currentPage_ = this.page;
			if (this.page > 1) {
				ItemInfo item_ = Main.getItemsManager().getItem("cosmeticsSelector_prevPage").getItem();
				Icon icon = new Icon(item_.getItem("" + this.page,"" + totalPages));
				icon.addClickAction(new ClickAction() {
					public void execute(Player player) {
						inv.setPage(Integer.valueOf(currentPage_ - 1));
					}
				});
				setIcon(item_.getSlot(), icon);
			}
			if (this.page < totalPages) {
				ItemInfo item_ = Main.getItemsManager().getItem("cosmeticsSelector_nextPage").getItem();
				Icon icon = new Icon(item_.getItem("" + this.page, "" + totalPages));
				icon.addClickAction(new ClickAction() {
					public void execute(Player player) {
						inv.setPage(Integer.valueOf(currentPage_ + 1));
					}
				});
				setIcon(item_.getSlot(), icon);
			}
		}

		Integer startIndex = Integer.valueOf((this.page - 1) * pageSize);
		Integer endIndex = Integer.valueOf(Math.min(startIndex + pageSize - 1, totalItems - 1));
		Integer slot = Integer.valueOf(10);
		for (int i = startIndex; i <= endIndex; i++) {
			final CosmeticItem cosmeticItem = items_.get(i);
			Boolean isUnlocked = Boolean.valueOf(false);
			if (cosmeticItem.getPrice() == 0 && (cosmeticItem.getPermission().isEmpty() || this.pInfo.getPlayer().hasPermission(cosmeticItem.getPermission()))) {
				isUnlocked = Boolean.valueOf(true);
			} else if (cosmeticItem.getPrice() == 0 && this.pInfo.getPlayer().hasPermission(cosmeticItem.getPermission())) {
				isUnlocked = Boolean.valueOf(true);
			} else if (this.pInfo.getStatsManager().getUnlockedCosmetics().contains(cosmeticItem.getKey())) {
				isUnlocked = Boolean.valueOf(true);
			}
			Boolean isSelected = Boolean.valueOf(false);
			if (this.pInfo.getStatsManager().getSelectedCosmetic(CosmeticType.KILL_SWORD) != null
					&& this.pInfo.getStatsManager().getSelectedCosmetic(CosmeticType.KILL_SWORD).equals(cosmeticItem.getKey())) {
				isSelected = Boolean.valueOf(true);
			}
			if (this.pInfo.getStatsManager().getSelectedCosmetic(CosmeticType.KILL_SWORD) == null
					&& cosmeticItem.getKey().equals(Main.getCosmeticsManager().getDefaultCosmetic(CosmeticType.KILL_SWORD))) {
				isSelected = Boolean.valueOf(true);
			}

			ItemStack item = Utils.createItem(cosmeticItem.getMat(), cosmeticItem.getDisplayName());
			ArrayList<String> lore = cosmeticItem.getLore3();
			if (isUnlocked.booleanValue()) {
				lore = cosmeticItem.getLore2();
			}
			if (isSelected.booleanValue()) {
				lore = cosmeticItem.getLore();
				Utils.enchantedItem(item, Enchantment.DURABILITY, 1);
			}
			Utils.setItemLore(item, lore);

			Icon icon = new Icon(item);

			if (!isSelected.booleanValue()) {
				final Boolean isUnlocked_ = isUnlocked;
				icon.addClickAction(new ClickAction() {
					public void execute(Player player) {
						if (isUnlocked_.booleanValue()) {
							CosmeticSelectorInv.this.pInfo.getStatsManager().selectCosmetic(CosmeticType.KILL_SWORD, cosmeticItem.getKey());
							inv.update();
							player.sendMessage(Main.getMessagesManager().getGameMsg("selectedCosmetic", null, cosmeticItem.getName(), "" + Main.getPlayerPointsApi().look(player.getUniqueId())));
						} else if (cosmeticItem.getPermission().isEmpty()) {
							if (Main.getPlayerPointsApi().look(player.getUniqueId()) >= cosmeticItem.getPrice()) {
								CosmeticSelectorInv.this.pInfo.getStatsManager().unlockCosmetic(CosmeticType.KILL_SWORD, cosmeticItem.getKey());
								CosmeticSelectorInv.this.pInfo.getStatsManager().selectCosmetic(CosmeticType.KILL_SWORD, cosmeticItem.getKey());
								Main.getPlayerPointsApi().take(player.getUniqueId(), cosmeticItem.getPrice());
								inv.update();
								player.sendMessage(Main.getMessagesManager().getGameMsg("playerBoughtCosmetic", null, cosmeticItem.getName(),
										(new StringBuilder(String.valueOf(Main.getPlayerPointsApi().look(player.getUniqueId()) - cosmeticItem.getPrice()))).toString(),
										(new StringBuilder(String.valueOf(cosmeticItem.getPrice()))).toString(), null));
							} else {
								player.sendMessage(Main.getMessagesManager().getGameMsg("notEnoughCoins", null, cosmeticItem.getName(), "" + Main.getPlayerPointsApi().look(player.getUniqueId()),
										(new StringBuilder(String.valueOf(cosmeticItem.getPrice()))).toString(), null));
							}
						} else if (player.hasPermission(cosmeticItem.getPermission())) {
							if (cosmeticItem.getPrice() > 0) {
								if (Main.getPlayerPointsApi().look(player.getUniqueId()) >= cosmeticItem.getPrice()) {
									CosmeticSelectorInv.this.pInfo.getStatsManager().unlockCosmetic(CosmeticType.KILL_SWORD, cosmeticItem.getKey());
									CosmeticSelectorInv.this.pInfo.getStatsManager().selectCosmetic(CosmeticType.KILL_SWORD, cosmeticItem.getKey());
									Main.getPlayerPointsApi().take(player.getUniqueId(), cosmeticItem.getPrice());
									inv.update();
									player.sendMessage(Main.getMessagesManager().getGameMsg("playerBoughtCosmetic", null, cosmeticItem.getName(),
											(new StringBuilder(String.valueOf(Main.getPlayerPointsApi().look(player.getUniqueId()) - cosmeticItem.getPrice()))).toString(),
											(new StringBuilder(String.valueOf(cosmeticItem.getPrice()))).toString(), null));
								} else {
									player.sendMessage(Main.getMessagesManager().getGameMsg("notEnoughCoins", null, cosmeticItem.getName(), "" + Main.getPlayerPointsApi().look(player.getUniqueId()),
											(new StringBuilder(String.valueOf(cosmeticItem.getPrice()))).toString(), null));
								}
							} else {
								CosmeticSelectorInv.this.pInfo.getStatsManager().selectCosmetic(CosmeticType.KILL_SWORD, cosmeticItem.getKey());
								inv.update();
								player.sendMessage(Main.getMessagesManager().getGameMsg("selectedCosmetic", null, cosmeticItem.getName(), "" + Main.getPlayerPointsApi().look(player.getUniqueId())));
							}
						}
					}
				});
			}

			setIcon(slot, icon);
			slot = Integer.valueOf(slot + 1);
			if (slot == 17 || slot == 26 || slot == 35) {
				slot = Integer.valueOf(slot + 2);
			}
		}

		ItemInfo coinsItem = Main.getItemsManager().getItem("cosmeticsSelector_coins").getItem();
		Icon icon = new Icon(coinsItem.getItem((new StringBuilder(String.valueOf(Main.getPlayerPointsApi().look(this.pInfo.getPlayer().getUniqueId())))).toString(), null));
		setIcon(coinsItem.getSlot(), icon);
	}

	public Integer getPage() {
		return this.page;
	}

	public void setPage(Integer newPage) {
		this.page = newPage;
		update();
	}
}
