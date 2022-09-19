package com.nktfh100.amongus.managers;

import com.nktfh100.AmongUs.enums.CosmeticType;
import com.nktfh100.AmongUs.enums.StatInt;
import com.nktfh100.AmongUs.info.PlayerInfo;
import com.nktfh100.AmongUs.main.Main;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class StatsManager {
	private Player player;
	private PlayerInfo pInfo;
	private File statsFile;
	private YamlConfiguration statsConfig;
	private HashMap<StatInt, Integer> statsInt = new HashMap<>();

	private ArrayList<String> unlockedCosmetics = new ArrayList<>();
	private HashMap<CosmeticType, String> selectedCosmetics = new HashMap<>();

	public StatsManager(PlayerInfo pInfo) {
		this.pInfo = pInfo;
		this.player = pInfo.getPlayer();
		if (!Main.getConfigManager().getMysql_enabled().booleanValue()) {
			this.statsFile = new File(Main.getPlugin().getDataFolder() + File.separator + "stats", String.valueOf(this.player.getUniqueId().toString()) + ".yml");
			if (!this.statsFile.exists()) {
				try {
					this.statsFile.createNewFile();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void loadStats() {
		final StatsManager statsManager = this;
		if (Main.getConfigManager().getMysql_enabled().booleanValue()) {
			(new BukkitRunnable() {
				public void run() {
					if (StatsManager.this.pInfo != null) {

						Connection connection = Main.getConfigManager().getMysql_connection();
						try {
							PreparedStatement ps = connection.prepareStatement("SELECT * FROM stats WHERE UUID = ?");
							ps.setString(1, StatsManager.this.player.getUniqueId().toString());
							ResultSet rs = ps.executeQuery();
							rs.next();
							byte b;
							int i;
							StatInt[] arrayOfStatInt;
							for (i = (arrayOfStatInt = StatInt.values()).length, b = 0; b < i;) {
								StatInt statIntE = arrayOfStatInt[b];
								Integer stats_ = Integer.valueOf(rs.getInt(statIntE.getName()));
								statsManager.setStatInt(statIntE, Integer.valueOf((stats_ == null) ? 0 : stats_));
								b++;
							}

							rs.close();
							ps.close();
							statsManager.getpInfo().updateScoreBoard();

							if (Main.getIsPlayerPoints().booleanValue()) {

								PreparedStatement ps1 = connection.prepareStatement("SELECT * FROM unlocked_cosmetics WHERE UUID = ?");
								ps1.setString(1, StatsManager.this.player.getUniqueId().toString());
								ResultSet rs1 = ps1.executeQuery();
								statsManager.getUnlockedCosmetics().clear();
								while (rs1.next()) {
									statsManager.getUnlockedCosmetics().add(rs1.getString("cosmetic"));
								}
								rs1.close();
								ps1.close();

								PreparedStatement ps2 = connection.prepareStatement("SELECT * FROM selected_cosmetics WHERE UUID = ?");
								ps2.setString(1, StatsManager.this.player.getUniqueId().toString());
								ResultSet rs2 = ps2.executeQuery();
								while (rs2.next()) {
									statsManager.getSelectedCosmetics().put(CosmeticType.valueOf(rs2.getString("type").toUpperCase()), rs2.getString("selected"));
								}
								rs2.close();
								ps2.close();
							}
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}
			}).runTaskAsynchronously(Main.getPlugin());
		} else {
			if (!this.statsFile.exists()) {
				try {
					this.statsFile.createNewFile();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			this.statsConfig = YamlConfiguration.loadConfiguration(this.statsFile);
			(new BukkitRunnable() {
				public void run() {
					if (statsManager.getpInfo() != null) {
						byte b;
						int i;
						StatInt[] arrayOfStatInt;
						for (i = (arrayOfStatInt = StatInt.values()).length, b = 0; b < i;) {
							StatInt statIntE = arrayOfStatInt[b];
							statsManager.getStatsInt().put(statIntE, Integer.valueOf(statsManager.getStatsConfig().getInt(statIntE.getName(), 0)));

							b++;
						}

						statsManager.getUnlockedCosmetics().clear();
						for (String cosmetic_ : statsManager.getStatsConfig().getStringList("unlocked_cosmetics")) {
							statsManager.getUnlockedCosmetics().add(cosmetic_);
						}

						CosmeticType[] arrayOfCosmeticType;
						for (int ii = (arrayOfCosmeticType = CosmeticType.values()).length; b < ii;) {
							CosmeticType type = arrayOfCosmeticType[b];
							String val_ = statsManager.getStatsConfig().getString(type.getName());
							if (val_ == null || val_.isEmpty()) {
								val_ = Main.getCosmeticsManager().getDefaultCosmetic(type);
							}
							statsManager.getSelectedCosmetics().put(type, val_);
							b++;
						}

						statsManager.getpInfo().updateScoreBoard();
					}

				}
			}).runTaskAsynchronously(Main.getPlugin());
		}
	}

	public void saveStats(Boolean runAsync) {
		final File statsFile_ = this.statsFile;
		final YamlConfiguration statsConfig_ = this.statsConfig;
		String uuid = this.player.getUniqueId().toString();
		final HashMap<StatInt, Integer> statsInt_ = new HashMap<>();
		byte b;
		int i;
		StatInt[] arrayOfStatInt;
		for (i = (arrayOfStatInt = StatInt.values()).length, b = 0; b < i;) {
			StatInt statIntE = arrayOfStatInt[b];
			statsInt_.put(statIntE, this.statsInt.get(statIntE));
			b++;
		}

		final ArrayList<String> unlockedCosmetics_ = this.unlockedCosmetics;
		final HashMap<CosmeticType, String> selectedCosmetics_ = this.selectedCosmetics;
		if (Main.getConfigManager().getMysql_enabled().booleanValue()) {
			try {
				PreparedStatement ps = Main.getConfigManager().getMysql_connection().prepareStatement(
						"UPDATE stats SET games_played=?, imposter_wins=?, crewmate_wins=?, total_wins=?, imposter_kills=?, tasks_completed=?, emergencies_called=?, bodies_reported=?, times_murdered=?, times_ejected=?, time_played=? WHERE UUID=?");
				ps.setInt(1, ((Integer) statsInt_.get(StatInt.GAMES_PLAYED)));
				ps.setInt(2, ((Integer) statsInt_.get(StatInt.IMPOSTER_WINS)));
				ps.setInt(3, ((Integer) statsInt_.get(StatInt.CREWMATE_WINS)));
				ps.setInt(4, ((Integer) statsInt_.get(StatInt.TOTAL_WINS)));
				ps.setInt(5, ((Integer) statsInt_.get(StatInt.IMPOSTER_KILLS)));
				ps.setInt(6, ((Integer) statsInt_.get(StatInt.TASKS_COMPLETED)));
				ps.setInt(7, ((Integer) statsInt_.get(StatInt.EMERGENCIES_CALLED)));
				ps.setInt(8, ((Integer) statsInt_.get(StatInt.BODIES_REPORTED)));
				ps.setInt(9, ((Integer) statsInt_.get(StatInt.TIMES_MURDERED)));
				ps.setInt(10, ((Integer) statsInt_.get(StatInt.TIMES_EJECTED)));
				ps.setInt(11, ((Integer) statsInt_.get(StatInt.TIME_PLAYED)));
				ps.setString(12, uuid);
				ps.execute();
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			BukkitRunnable func = new BukkitRunnable() {
				public void run() {
					if (!statsFile_.exists()) {
						try {
							statsFile_.createNewFile();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					for (StatInt statIntE : statsInt_.keySet()) {
						statsConfig_.set(statIntE.getName(), statsInt_.get(statIntE));
					}

					statsConfig_.set("unlocked_cosmetics", unlockedCosmetics_);

					for (CosmeticType type : selectedCosmetics_.keySet()) {
						statsConfig_.set(type.getName(), selectedCosmetics_.get(type));
					}

					try {
						statsConfig_.save(statsFile_);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			};
			if (runAsync.booleanValue()) {
				func.runTaskAsynchronously(Main.getPlugin());
			} else {
				func.run();
			}
		}
	}

	public void mysql_registerPlayer(final Boolean loadStats) {
		final StatsManager statsManager = this;
		(new BukkitRunnable() {
			public void run() {
				try {
					ConfigManager configManager = Main.getConfigManager();
					Connection connection = configManager.mysql_getConnection();

					Boolean status = Boolean.valueOf(configManager.mysql_checkConnection());
					if (!status.booleanValue()) {
						System.out.println("Something is wrong with your MySQL server!");

						return;
					}

					PreparedStatement ps = connection.prepareStatement("SELECT UUID FROM stats WHERE UUID = ?");
					ps.setString(1, StatsManager.this.player.getUniqueId().toString());
					ResultSet rs = ps.executeQuery();
					Boolean doesExists = Boolean.valueOf(rs.next());
					rs.close();
					ps.close();
					if (!doesExists.booleanValue()) {
						String sql = "INSERT INTO stats(username, UUID) VALUES (?, ?)";

						PreparedStatement statement = connection.prepareStatement(sql);
						statement.setString(1, StatsManager.this.player.getName());
						statement.setString(2, StatsManager.this.player.getUniqueId().toString());

						statement.execute();
						statement.close();

						if (Main.getIsPlayerPoints().booleanValue()) {

							try {
								String sql1 = "INSERT INTO selected_cosmetics(username, UUID, type, selected) VALUES (?, ?, ?, ?)";

								PreparedStatement statement1 = connection.prepareStatement(sql1);
								statement1.setString(1, StatsManager.this.player.getName());
								statement1.setString(2, StatsManager.this.player.getUniqueId().toString());
								statement1.setString(3, CosmeticType.KILL_SWORD.getName());
								statement1.setString(4, Main.getCosmeticsManager().getDefaultCosmetic(CosmeticType.KILL_SWORD));

								statement1.execute();
								statement1.close();
							} catch (Exception exception) {
							}
						}
					}

					if (loadStats.booleanValue()) {
						statsManager.loadStats();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}).runTaskAsynchronously(Main.getPlugin());
	}

	public void unlockCosmetic(CosmeticType type, final String cosmetic) {
		this.unlockedCosmetics.add(cosmetic);
		if (Main.getConfigManager().getMysql_enabled().booleanValue()) {
			(new BukkitRunnable() {
				public void run() {
					try {
						String sql1 = "INSERT INTO unlocked_cosmetics(username, UUID, cosmetic) VALUES (?, ?, ?)";

						PreparedStatement statement1 = Main.getConfigManager().getMysql_connection().prepareStatement(sql1);
						statement1.setString(1, StatsManager.this.player.getName());
						statement1.setString(2, StatsManager.this.player.getUniqueId().toString());
						statement1.setString(3, cosmetic);

						statement1.execute();
						statement1.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}).runTaskAsynchronously(Main.getPlugin());
		} else {
			final File statsFile_ = this.statsFile;
			final YamlConfiguration statsConfig_ = this.statsConfig;
			final ArrayList<String> unlocked_ = this.unlockedCosmetics;
			(new BukkitRunnable() {
				public void run() {
					if (!statsFile_.exists()) {
						try {
							statsFile_.createNewFile();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					statsConfig_.set("unlocked_cosmetics", unlocked_);

					try {
						statsConfig_.save(statsFile_);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}).runTaskAsynchronously(Main.getPlugin());
		}
	}

	public void selectCosmetic(final CosmeticType type, final String selected) {
		this.selectedCosmetics.put(type, selected);
		if (Main.getConfigManager().getMysql_enabled().booleanValue()) {
			final String uuid_ = getPlayer().getUniqueId().toString();
			(new BukkitRunnable() {
				public void run() {
					try {
						PreparedStatement ps = Main.getConfigManager().getMysql_connection().prepareStatement("UPDATE selected_cosmetics SET selected=? WHERE UUID=? AND type=?");
						ps.setString(1, selected);
						ps.setString(2, uuid_);
						ps.setString(3, type.getName());
						ps.execute();
						ps.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}).runTaskAsynchronously(Main.getPlugin());
		} else {
			final File statsFile_ = this.statsFile;
			final YamlConfiguration statsConfig_ = this.statsConfig;
			(new BukkitRunnable() {
				public void run() {
					if (!statsFile_.exists()) {
						try {
							statsFile_.createNewFile();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					statsConfig_.set(type.getName(), selected);

					try {
						statsConfig_.save(statsFile_);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}).runTaskAsynchronously(Main.getPlugin());
		}
	}

	public void plusOneStatInt(StatInt key) {
		this.statsInt.put(key, Integer.valueOf(((Integer) this.statsInt.get(key)) + 1));
	}

	public void setStatInt(StatInt key, Integer value) {
		this.statsInt.put(key, value);
	}

	public Integer getStatInt(StatInt key) {
		return this.statsInt.get(key);
	}

	public Integer getCoins() {
		if (Main.getIsPlayerPoints().booleanValue()) {
			return Integer.valueOf(Main.getPlayerPointsApi().look(this.player.getUniqueId()));
		}
		return Integer.valueOf(0);
	}

	public void delete() {
		this.statsInt.clear();
	}

	public Player getPlayer() {
		return this.player;
	}

	public File getStatsFile() {
		return this.statsFile;
	}

	public YamlConfiguration getStatsConfig() {
		return this.statsConfig;
	}

	public HashMap<StatInt, Integer> getStatsInt() {
		return this.statsInt;
	}

	public PlayerInfo getpInfo() {
		return this.pInfo;
	}

	public String getSelectedCosmetic(CosmeticType key) {
		return this.selectedCosmetics.get(key);
	}

	public void setSelectedCosmetic(CosmeticType group, String value) {
		this.selectedCosmetics.put(group, value);
	}

	public ArrayList<String> getUnlockedCosmetics() {
		return this.unlockedCosmetics;
	}

	public HashMap<CosmeticType, String> getSelectedCosmetics() {
		return this.selectedCosmetics;
	}
}
