package com.nktfh100.amongus.info;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.nktfh100.AmongUs.main.Main;
import java.util.ArrayList;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class DeadBody {
	private Arena arena;
	private Player player;
	private PlayerInfo pInfo;
	private ColorInfo color;
	private Hologram holo;
	private Location loc;
	private FakePlayer fakePlayer;
	private Boolean isDeleted = Boolean.valueOf(false);
	private ArrayList<Player> playersShownTo = new ArrayList<>();

	@SuppressWarnings("deprecation")
	public DeadBody(Arena arena, Player player) {
		this.player = player;
		PlayerInfo pInfo = Main.getPlayersManager().getPlayerInfo(player);
		this.pInfo = pInfo;
		this.color = pInfo.getColor();
		this.arena = arena;
		this.loc = player.getLocation();
		this.fakePlayer = new FakePlayer(arena, pInfo, (int) (Math.random() * 2.147483647E9D), UUID.randomUUID());

		if (!player.isOnGround()) {
			Location startLoc = player.getLocation().clone();
			int startLocY = startLoc.getBlockY();
			World world = startLoc.getWorld();

			for (int i = 0; i < 5; i++) {
				Block block = world.getBlockAt(startLoc.getBlockX(), startLocY - i, startLoc.getBlockZ());
				if (block == null || block.getType() != Material.AIR) {
					startLocY -= i;
					break;
				}
			}
			this.loc.setY((startLocY + 1));
		}
	}

	public void create() {
		this.holo = HologramsAPI.createHologram(Main.getPlugin(), this.loc.clone().add(0.0D, 1.8D, 0.0D));
		this.holo.appendItemLine(this.pInfo.getHead());
		this.arena.getVisibilityManager().resetBodyVis(this);
	}

	public void showTo(PlayerInfo toPInfo, Boolean register) {
		Player player = toPInfo.getPlayer();
		if (this.isDeleted.booleanValue()) {
			return;
		}
		if (register.booleanValue()) {
			this.playersShownTo.add(player);
		}
		if (this.holo != null) {
			this.holo.getVisibilityManager().showTo(player);
		}

		this.fakePlayer.showPlayerTo(toPInfo, this.loc, Boolean.valueOf(true), register);
	}

	public void hideFrom(Player player, Boolean register) {
		if (this.isDeleted.booleanValue()) {
			return;
		}
		if (register.booleanValue()) {
			this.playersShownTo.remove(player);
		}
		if (this.holo != null) {
			this.holo.getVisibilityManager().hideTo(player);
		}
		this.fakePlayer.hidePlayerFrom(player, register);
	}

	public Boolean isShownTo(Player p) {
		return Boolean.valueOf(this.playersShownTo.contains(p));
	}

	public void delete() {
		for (Player p : this.playersShownTo) {
			hideFrom(p, Boolean.valueOf(false));
		}
		this.playersShownTo.clear();
		this.holo.delete();
		this.isDeleted = Boolean.valueOf(true);
	}

	public Location getLocation() {
		return this.loc;
	}

	public Arena getArena() {
		return this.arena;
	}

	public Player getPlayer() {
		return this.player;
	}

	public Hologram getHolo() {
		return this.holo;
	}

	public Boolean getIsDeleted() {
		return this.isDeleted;
	}

	public ArrayList<Player> getPlayersShownTo() {
		return this.playersShownTo;
	}

	public ColorInfo getColor() {
		return this.color;
	}

	public PlayerInfo getPlayerInfo() {
		return this.pInfo;
	}
}
