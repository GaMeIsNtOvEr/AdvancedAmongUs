package com.nktfh100.amongus.inventory;

import com.nktfh100.AmongUs.info.Arena;
import com.nktfh100.AmongUs.info.ColorInfo;
import com.nktfh100.AmongUs.info.ItemInfoContainer;
import com.nktfh100.AmongUs.info.PlayerInfo;
import com.nktfh100.AmongUs.main.Main;
import com.nktfh100.AmongUs.utils.Utils;
import java.util.ArrayList;
import org.bukkit.entity.Player;

public class ColorSelectorInv extends CustomHolder {
	private Arena arena;

	public ColorSelectorInv(Arena arena) {
		super(Integer.valueOf(18), Main.getMessagesManager().getGameMsg("colorSelectorInvTitle", null, null));
		this.arena = arena;
		Utils.fillInv(this.inv);
	}

	public void handleColorClick(PlayerInfo pInfo, ColorInfo color) {
		this.arena.updatePlayerColor(pInfo, color);
		Main.getSoundsManager().playSound("playerChangeColor", pInfo.getPlayer(), pInfo.getPlayer().getLocation());
		pInfo.setPreferredColor(color);
		update();
	}

	public void update() {
		final ColorSelectorInv inv_ = this;
		clearInv();
		Utils.fillInv(this.inv);
		ItemInfoContainer colorItem = Main.getItemsManager().getItem("colorSelector_color");
		int i = 0;
		for (ColorInfo color : this.arena.getColors_()) {
			String title = colorItem.getItem().getTitle(color.getName(), "" + color.getChatColor());
			ArrayList<String> lore = colorItem.getItem().getLore(color.getName(), "" + color.getChatColor());
			Icon icon = new Icon(Utils.createItem(color.getWool(), title, 1, lore));

			icon.addClickAction(new ClickAction() {
				public void execute(Player player) {
					inv_.handleColorClick(Main.getPlayersManager().getPlayerInfo(player), color);
				}
			});

			setIcon(i, icon);
			i++;
		}
	}
}
