package com.nktfh100.amongus.managers;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.nktfh100.AmongUs.info.Arena;
import com.nktfh100.AmongUs.info.PlayerInfo;
import com.nktfh100.AmongUs.info.Vent;
import com.nktfh100.AmongUs.info.VentGroup;
import com.nktfh100.AmongUs.main.Main;
import com.nktfh100.AmongUs.utils.Utils;
import java.util.ArrayList;
import java.util.Collections;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class VentsManager {
	private Arena arena;
	private ArrayList<VentGroup> ventGroups = new ArrayList<>();
	private ArrayList<Hologram> holos = new ArrayList<>();

	public VentsManager(Arena arena) {
		this.arena = arena;
	}

	public void ventHoloClick(PlayerInfo pInfo, Integer vgId, Integer vId) {
		Player player = pInfo.getPlayer();
		pInfo.setIsInVent(Boolean.valueOf(true));
		pInfo.setVentGroup(this.ventGroups.get(vgId));
		pInfo.setVent(((VentGroup) this.ventGroups.get(vgId)).getVent(vId));
		hideAllHolos(player);

		this.arena.giveGameInventory(pInfo);
		player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 2147483647, 1));
		player.setVelocity(new Vector(0, 0, 0));
		player.getWorld().spawnParticle(Particle.BLOCK_CRACK, player.getLocation().add(0.0D, 1.3D, 0.0D), 40, 0.3D, 0.3D, 0.3D, Material.IRON_BLOCK.createBlockData());

		Main.getSoundsManager().playSound("playerGetInVent", player, pInfo.getVent().getLoc());

		for (PlayerInfo pInfo1 : this.arena.getPlayersInfo()) {
			if (pInfo1 != pInfo) {
				this.arena.getVisibilityManager().hidePlayer(pInfo1, pInfo, Boolean.valueOf(true));
			}
		}
		pInfo.getPlayer().teleport(pInfo.getVent().getPlayerLoc());
		this.arena.getVisibilityManager().playerMoved(pInfo, pInfo.getVent().getPlayerLoc());
	}

	public void playerLeaveVent(PlayerInfo pInfo, Boolean isForce, Boolean endGame) {
		Player player = pInfo.getPlayer();
		pInfo.setIsInVent(Boolean.valueOf(false));
		showAllHolos(player);

		if (!pInfo.isGhost().booleanValue()) {
			player.removePotionEffect(PotionEffectType.INVISIBILITY);
		}
		if (!isForce.booleanValue()) {
			player.getWorld().spawnParticle(Particle.BLOCK_CRACK, player.getLocation().add(0.0D, 1.3D, 0.0D), 40, 0.3D, 0.3D, 0.3D, Material.IRON_BLOCK.createBlockData());
		}

		if (!isForce.booleanValue()) {
			Main.getSoundsManager().playSound("playerLeaveVent", player, pInfo.getVent().getLoc());
			this.arena.giveGameInventory(pInfo);
		}

		for (PlayerInfo pInfo1 : this.arena.getPlayersInfo()) {
			if (pInfo != pInfo1 && (!pInfo.isGhost().booleanValue() || (pInfo.isGhost().booleanValue() && pInfo1.isGhost().booleanValue()))) {
				this.arena.getVisibilityManager().showPlayer(pInfo1, pInfo, Boolean.valueOf(true));
			}
		}

		if (!endGame.booleanValue()) {
			pInfo.getArena().getVisibilityManager().playerMoved(pInfo);
		}

		pInfo.setVentGroup(null);
		pInfo.setVent(null);
	}

	public void playerPrevVent(final PlayerInfo pInfo) {
		int id = pInfo.getVent().getId();
		if (id == 0) {
			if (!pInfo.getVentGroup().getLoop().booleanValue()) {
				return;
			}
			id = pInfo.getVentGroup().getVents().size() - 1;
		} else {
			id--;
		}
		pInfo.getPlayer().getInventory().clear();
		pInfo.setVent(pInfo.getVentGroup().getVent(Integer.valueOf(id)));
		pInfo.getPlayer().teleport(pInfo.getVent().getPlayerLoc());
		Main.getSoundsManager().playSound("playerNextVent", pInfo.getPlayer(), pInfo.getVent().getLoc());
		(new BukkitRunnable() {
			public void run() {
				if (pInfo.getIsIngame().booleanValue()) {
					pInfo.getArena().giveGameInventory(pInfo);
				}
			}
		}).runTaskLater(Main.getPlugin(), 5L);
		Utils.sendActionBar(pInfo.getPlayer(), this.arena.getVentsManager().getVentActionBar(pInfo.getVent()));
		pInfo.getArena().getVisibilityManager().playerMoved(pInfo, pInfo.getVent().getPlayerLoc());
	}

	public void playerNextVent(final PlayerInfo pInfo) {
		int id = pInfo.getVent().getId();
		if (id == pInfo.getVentGroup().getVents().size() - 1) {
			if (!pInfo.getVentGroup().getLoop().booleanValue()) {
				return;
			}
			id = 0;
		} else {
			id++;
		}
		pInfo.getPlayer().getInventory().clear();
		pInfo.setVent(pInfo.getVentGroup().getVent(Integer.valueOf(id)));
		pInfo.getPlayer().teleport(pInfo.getVent().getPlayerLoc());
		Main.getSoundsManager().playSound("playerNextVent", pInfo.getPlayer(), pInfo.getVent().getLoc());
		(new BukkitRunnable() {
			public void run() {
				if (pInfo.getIsIngame().booleanValue()) {
					pInfo.getArena().giveGameInventory(pInfo);
				}
			}
		}).runTaskLater(Main.getPlugin(), 5L);
		Utils.sendActionBar(pInfo.getPlayer(), this.arena.getVentsManager().getVentActionBar(pInfo.getVent()));
		pInfo.getArena().getVisibilityManager().playerMoved(pInfo, pInfo.getVent().getPlayerLoc());
	}

	public void hideAllHolos(Player player) {
		for (VentGroup vg : this.ventGroups) {
			for (Vent v : vg.getVents()) {
				if(v.getHolo() != null) {
					v.getHolo().getVisibilityManager().hideTo(player);					
				}
			}
		}
	}

	public void showAllHolos(Player player) {
		for (VentGroup vg : this.ventGroups) {
			for (Vent v : vg.getVents()) {
				if (v.getHolo() != null) {
					v.getHolo().getVisibilityManager().showTo(player);
				}
			}
		}
	}

	public String getVentActionBar(Vent vent) {
		if (vent.getLocName() == null) {
			return Main.getMessagesManager().getGameMsg("ventActionBar1", this.arena, null);
		}
		return Main.getMessagesManager().getGameMsg("ventActionBar", this.arena, vent.getLocName().getName());
	}

	public void addVentGroup(VentGroup vg) {
		this.ventGroups.add(vg);
		Collections.sort(this.ventGroups);
	}

	public VentGroup getVentGroup(Integer id) {
		return this.ventGroups.get(id);
	}

	public void addVent(Integer vgId, Vent v) {
		((VentGroup) this.ventGroups.get(vgId)).addVent(v);
	}

	public void delete() {
		this.arena = null;
		for (VentGroup vg : this.ventGroups) {
			vg.delete();
		}
		this.ventGroups = null;
		for (Hologram holo : this.holos) {
			holo.delete();
		}
		this.holos = null;
	}

	public Arena getArena() {
		return this.arena;
	}

	public ArrayList<VentGroup> getVentGroups() {
		return this.ventGroups;
	}

	public ArrayList<Hologram> getHolos() {
		return this.holos;
	}
}
