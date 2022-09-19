package com.nktfh100.amongus.info;

import com.nktfh100.AmongUs.main.Main;
import com.nktfh100.AmongUs.utils.Packets;
import java.util.ArrayList;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class FakePlayer {
	private Arena arena;
	private Player orgPlayer;
	private PlayerInfo orgPInfo;
	private String name;
	private String customName;
	private ColorInfo color;
	private int entityId;
	private UUID uuid;
	private String textureValue = "";
	private String textureSignature = "";

	private ArrayList<Player> tabShownTo = new ArrayList<>();
	private ArrayList<Player> playerShownTo = new ArrayList<>();

	public FakePlayer(Arena arena, PlayerInfo pInfo) {
		this.arena = arena;
		this.orgPlayer = pInfo.getPlayer();
		this.orgPInfo = pInfo;
		this.name = this.orgPlayer.getName();
		this.customName = pInfo.getCustomName();
		this.color = pInfo.getColor();
		this.entityId = pInfo.getFakePlayerId();
		this.uuid = pInfo.getFakePlayerUUID();
		this.textureValue = pInfo.getTextureValue();
		this.textureSignature = pInfo.getTextureSignature();
	}

	public FakePlayer(Arena arena, PlayerInfo pInfo, int entityId, UUID uuid) {
		this.arena = arena;
		this.orgPlayer = pInfo.getPlayer();
		this.orgPInfo = pInfo;
		this.name = this.orgPlayer.getName();
		this.customName = pInfo.getCustomName();
		this.color = pInfo.getColor();
		this.entityId = entityId;
		this.uuid = uuid;
		this.textureValue = pInfo.getTextureValue();
		this.textureSignature = pInfo.getTextureSignature();
	}

	public void showPlayerTo(PlayerInfo pInfo, Location loc, Boolean dead, Boolean register) {
		final Player player = pInfo.getPlayer();
		if (this.playerShownTo.contains(player)) {
			return;
		}
		Packets.sendPacket(player, Packets.ADD_PLAYER(this.uuid, this.name, this.customName, this.textureValue, this.textureSignature));

		Packets.sendPacket(player, Packets.SPAWN_PLAYER(loc, this.entityId, this.uuid));
		Packets.sendPacket(player, Packets.METADATA_SKIN(this.entityId, pInfo.getPlayer()));
		if (!dead) {
			Packets.sendPacket(player, Packets.ENTITY_HEAD_ROTATION(this.entityId, loc));
			Packets.sendPacket(player, Packets.ENTITY_LOOK(this.entityId, loc));
		}
		Packets.sendPacket(player, Packets.PLAYER_ARMOR(this.color, this.entityId));
		if (dead) {
			Packets.sendPacket(player, Packets.PLAYER_SLEEPING(this.entityId));
		}
		if (register) {
			this.playerShownTo.add(player);
		}
		final FakePlayer fp = this;
		(new BukkitRunnable() {
			public void run() {
				Packets.sendPacket(player, Packets.REMOVE_PLAYER(fp.getUuid(), fp.getName(), fp.getCustomName()));
			}
		}).runTaskLater(Main.getPlugin(), 2L);
	}

	public void hidePlayerFrom(Player player, Boolean register) {
		if (this.playerShownTo.contains(player) || !register) {
			Packets.sendPacket(player, Packets.DESTROY_ENTITY(this.entityId));

			if (register) {
				this.playerShownTo.remove(player);
			}
		}
	}

	public void resetAllPlayerVis() {
		for (Player p : this.playerShownTo) {
			hidePlayerFrom(p, Boolean.valueOf(false));
		}
		this.playerShownTo.clear();
	}

	public Boolean isTabShownTo(Player player) {
		return Boolean.valueOf(this.tabShownTo.contains(player));
	}

	public Boolean isPlayerShownTo(Player player) {
		return Boolean.valueOf(this.playerShownTo.contains(player));
	}

	public int getEntityId() {
		return this.entityId;
	}

	public UUID getUuid() {
		return this.uuid;
	}

	public String getTextureValue() {
		return this.textureValue;
	}

	public String getTextureSignature() {
		return this.textureSignature;
	}

	public Arena getArena() {
		return this.arena;
	}

	public Player getOrgPlayer() {
		return this.orgPlayer;
	}

	public PlayerInfo getOrgPInfo() {
		return this.orgPInfo;
	}

	public String getName() {
		return this.name;
	}

	public ColorInfo getColor() {
		return this.color;
	}

	public String getCustomName() {
		return this.customName;
	}

	public ArrayList<Player> getTabShownTo() {
		return this.tabShownTo;
	}

	public ArrayList<Player> getPlayerShownTo() {
		return this.playerShownTo;
	}
}
