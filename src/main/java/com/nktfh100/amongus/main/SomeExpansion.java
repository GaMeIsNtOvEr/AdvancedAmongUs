package com.nktfh100.amongus.main;

import com.nktfh100.AmongUs.enums.StatInt;
import com.nktfh100.AmongUs.info.PlayerInfo;
import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class SomeExpansion extends PlaceholderExpansion {
	private Plugin plugin;

	public SomeExpansion(Plugin plugin) {
		this.plugin = plugin;
	}

	public boolean persist() {
		return true;
	}

	public boolean canRegister() {
		return true;
	}

	public String getAuthor() {
		return this.plugin.getDescription().getAuthors().toString();
	}

	public String getIdentifier() {
		return "amongus";
	}

	public String getVersion() {
		return this.plugin.getDescription().getVersion();
	}

	private Integer getStatMySql(Player player, StatInt key) {
		Integer out = Integer.valueOf(0);
		try {
			Connection connection = Main.getConfigManager().getMysql_connection();
			PreparedStatement ps = connection.prepareStatement("SELECT * FROM stats WHERE UUID = ?");
			ps.setString(1, player.getUniqueId().toString());
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				out = Integer.valueOf(rs.getInt(key.getName()));
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return out;
	}

	private Integer getStatFlatFile(Player player, StatInt key) {
		File statsFile = new File(Main.getPlugin().getDataFolder() + File.separator + "stats", String.valueOf(player.getUniqueId().toString()) + ".yml");
		if (statsFile.exists()) {
			YamlConfiguration statsConfig = YamlConfiguration.loadConfiguration(statsFile);
			return Integer.valueOf(statsConfig.getInt(key.getName(), 0));
		}
		return Integer.valueOf(0);
	}

	public String onPlaceholderRequest(Player player, String identifier) {
		if (player == null) {
			return null;
		}
		identifier = identifier.toLowerCase();

		Boolean isValid = Boolean.valueOf(false);
		StatInt statInt_ = null;
		byte b;
		int i;
		StatInt[] arrayOfStatInt;
		for (i = (arrayOfStatInt = StatInt.values()).length, b = 0; b < i;) {
			StatInt statIntE = arrayOfStatInt[b];
			if (identifier.equals(statIntE.getName())) {
				isValid = Boolean.valueOf(true);
				statInt_ = statIntE;
				break;
			}
			b++;
		}

		if (identifier.contains("time_played_")) {
			Double out = Double.valueOf(0.0D);
			if (Main.getConfigManager().getMysql_enabled().booleanValue()) {
				out = Double.valueOf(getStatMySql(player, StatInt.TIME_PLAYED));
			} else {
				out = Double.valueOf(getStatFlatFile(player, StatInt.TIME_PLAYED));
			}
			if (identifier.contains("minutes")) {
				out = Double.valueOf(out.doubleValue() / 60.0D);
			} else if (identifier.contains("hours")) {
				out = Double.valueOf(out.doubleValue() / 60.0D / 60.0D);
			} else if (identifier.contains("days")) {
				out = Double.valueOf(out.doubleValue() / 60.0D / 60.0D / 24.0D);
			}
			BigDecimal a = new BigDecimal(out.doubleValue());
			BigDecimal roundOff = a.setScale(2, 6);
			out = Double.valueOf(roundOff.doubleValue());
			return "" + out;
		}
		if (identifier.equalsIgnoreCase("color")) {
			PlayerInfo pInfo_ = Main.getPlayersManager().getPlayerInfo(player);
			ChatColor chatColor = ChatColor.WHITE;
			if (pInfo_ != null && pInfo_.getIsIngame().booleanValue()) {
				chatColor = pInfo_.getColor().getChatColor();
			}
			return "" + chatColor;
		}
		if (isValid.booleanValue()) {
			if (Main.getConfigManager().getMysql_enabled().booleanValue()) {
				return "" + getStatMySql(player, statInt_);
			}
			return "" + getStatFlatFile(player, statInt_);
		}

		return "0";
	}
}
