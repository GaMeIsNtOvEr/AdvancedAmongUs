package com.nktfh100.amongus.managers;

import com.nktfh100.AmongUs.info.ColorInfo;
import com.nktfh100.AmongUs.main.Main;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class ConfigManager {
	private FileConfiguration config;
	private String prefix;
	private Boolean bungeecord = Boolean.valueOf(false);
	private String bungeecordLobbyServer;
	private Boolean bungeecordIsLobby;
	private ArrayList<String> gameServers = new ArrayList<>();

	private String serverName;
	private Location mainLobby;
	private Boolean giveLobbyItems;
	private HashMap<String, Integer> lobbyItemsSlots = null;
	private Boolean showRunningArenas;
	private List<String> blockedCommands = new ArrayList<>();
	private HashMap<String, ColorInfo> colors = null;
	private Boolean ghostsFly = Boolean.valueOf(true);
	private Color asteroidsParticleColor = Color.RED;
	private BlockData asteroidsParticleMaterial;
	private Boolean hidePlayersOutSideArena = Boolean.valueOf(true);
	private Boolean enableGlassHelmet = Boolean.valueOf(true);
	private Boolean gameEndSendToLobby = Boolean.valueOf(true);
	private Boolean particlesOnTasks = Boolean.valueOf(true);
	private Particle particlesOnTasksType = Particle.VILLAGER_HAPPY;
	private Boolean enableLobbyScoreboard = Boolean.valueOf(false);
	private Boolean tpToLobbyOnJoin = Boolean.valueOf(true);
	private Material viewGlassMat = Material.BLACK_STAINED_GLASS;
	private Boolean enablePortalJoin = Boolean.valueOf(false);
	private Boolean scoreboardUsePackets = Boolean.valueOf(true);
	private Boolean enableDoubleImposterChance = Boolean.valueOf(false);
	private Boolean sneakToVent = Boolean.valueOf(false);
	private Boolean damageOnSabotage = Boolean.valueOf(false);
	private Boolean saveInventory = Boolean.valueOf(false);

	private HashMap<String, ArrayList<String>> commands = new HashMap<>();

	private Boolean mysql_enabled = Boolean.valueOf(false);
	private String mysql_host = "";
	private String mysql_port = "";
	private String mysql_database = "";
	private String mysql_username = "";
	private String mysql_password = "";
	private Connection mysql_connection = null;

	public ConfigManager(FileConfiguration config) {
		this.config = config;
	}

	public void loadConfig() {
		Main.getPlugin().saveDefaultConfig();
		loadConfigVars();
	}

	public void loadConfigVars() {
		this.lobbyItemsSlots = new HashMap<>();
		this.blockedCommands = new ArrayList<>();
		this.colors = new HashMap<>();

		Main.getPlugin().reloadConfig();
		this.config = Main.getPlugin().getConfig();

		if (this.config.getConfigurationSection("bungeecord") != null) {
			ConfigurationSection bungeSec = this.config.getConfigurationSection("bungeecord");
			this.bungeecord = Boolean.valueOf(bungeSec.getBoolean("enabled", false));
			this.bungeecordLobbyServer = bungeSec.getString("lobbyServer", "lobby");
			this.bungeecordIsLobby = Boolean.valueOf(bungeSec.getBoolean("lobby", false));
			this.gameServers = new ArrayList<>(bungeSec.getStringList("gameServers"));
			this.serverName = bungeSec.getString("serverName", "null");
		}

		World mainLobbyWorld = Bukkit.getServer().getWorld(this.config.getString("mainLobby.world"));
		if (mainLobbyWorld == null) {
			mainLobbyWorld = Bukkit.getWorlds().get(0);
		}
		this.mainLobby = new Location(mainLobbyWorld, this.config.getDouble("mainLobby.x"), this.config.getDouble("mainLobby.y"), this.config.getDouble("mainLobby.z"));

		this.prefix = ChatColor.translateAlternateColorCodes('&', this.config.getString("prefix"));

		this.giveLobbyItems = Boolean.valueOf(this.config.getBoolean("giveLobbyItems"));
		this.showRunningArenas = Boolean.valueOf(this.config.getBoolean("showRunningArenas"));

		this.enableGlassHelmet = Boolean.valueOf(this.config.getBoolean("enableGlassHelmet", true));

		this.blockedCommands = this.config.getStringList("blockedCommands");

		ConfigurationSection lobbyItemsSlotsSC = this.config.getConfigurationSection("lobbyItemsSlots");
		for (String key : lobbyItemsSlotsSC.getKeys(false)) {
			this.lobbyItemsSlots.put(key, Integer.valueOf(lobbyItemsSlotsSC.getInt(key)));
		}

		this.viewGlassMat = Material.getMaterial(this.config.getString("viewGlassMat", "BLACK_STAINED_GLASS"));
		this.ghostsFly = Boolean.valueOf(this.config.getBoolean("ghostsFly", true));
		this.gameEndSendToLobby = Boolean.valueOf(this.config.getBoolean("gameEndSendToLobby", true));
		this.particlesOnTasks = Boolean.valueOf(this.config.getBoolean("particlesOnTasks", true));
		this.enableLobbyScoreboard = Boolean.valueOf(this.config.getBoolean("enableLobbyScoreboard", false));
		this.tpToLobbyOnJoin = Boolean.valueOf(this.config.getBoolean("tpToLobbyOnJoin", true));
		this.enablePortalJoin = Boolean.valueOf(this.config.getBoolean("enablePortalJoin", false));
		this.scoreboardUsePackets = Boolean.valueOf(this.config.getBoolean("scoreboardUsePackets", true));
		this.enableDoubleImposterChance = Boolean.valueOf(this.config.getBoolean("enableDoubleImposterChance", false));
		this.sneakToVent = Boolean.valueOf(this.config.getBoolean("sneakToVent", false));
		this.damageOnSabotage = Boolean.valueOf(this.config.getBoolean("damageOnSabotage", false));
		this.saveInventory = Boolean.valueOf(this.config.getBoolean("saveInventory", false));

		try {
			this.particlesOnTasksType = Particle.valueOf(this.config.getString("particlesOnTasksType", "VILLAGER_HAPPY"));
		} catch (Exception e) {
			this.particlesOnTasksType = Particle.VILLAGER_HAPPY;
			Main.getPlugin().getLogger().warning("particlesOnTasksType: " + this.config.getString("particlesOnTasksType", "-") + " is not a valid particle type!");
		}

		this.hidePlayersOutSideArena = Boolean.valueOf(this.config.getBoolean("hidePlayersOutSideArena", true));
		String[] rgb_ = this.config.getString("asteroidsParticleColor", "255,0,0").split(",");

		this.asteroidsParticleColor = Color.fromRGB(Integer.parseInt(rgb_[0]), Integer.parseInt(rgb_[1]), Integer.parseInt(rgb_[2]));

		Material mat = Material.getMaterial(this.config.getString("asteroidsParticleMaterial", "REDSTONE_BLOCK"));
		this.asteroidsParticleMaterial = Bukkit.createBlockData(mat);

		if (this.config.getConfigurationSection("commands") != null) {
			ConfigurationSection commandsSC = this.config.getConfigurationSection("commands");
			for (String key : commandsSC.getKeys(false)) {
				this.commands.put(key, new ArrayList<>());
				for (String cmd : commandsSC.getStringList(key)) {
					((ArrayList<String>) this.commands.get(key)).add(cmd);
				}
			}
		}

		if (this.config.getConfigurationSection("mysql") != null) {
			ConfigurationSection mysqlSC = this.config.getConfigurationSection("mysql");
			if (mysqlSC.getBoolean("enabled", false)) {
				this.mysql_enabled = Boolean.valueOf(true);
				this.mysql_host = mysqlSC.getString("host", "");
				this.mysql_port = mysqlSC.getString("port", "");
				this.mysql_database = mysqlSC.getString("database", "");
				this.mysql_username = mysqlSC.getString("username", "");
				this.mysql_password = mysqlSC.getString("password", "");
				try {
					this.mysql_connection = getNewConnection(this.config);

					if (this.mysql_connection != null) {
						String sql = "CREATE TABLE IF NOT EXISTS stats(username VARCHAR(64) NOT NULL, UUID VARCHAR(64) NOT NULL UNIQUE, games_played INT(255) DEFAULT 0, imposter_wins INT(255) DEFAULT 0, crewmate_wins INT(255) DEFAULT 0, total_wins INT(255) DEFAULT 0, imposter_kills INT(255) DEFAULT 0, tasks_completed INT(255) DEFAULT 0, emergencies_called INT(255) DEFAULT 0, bodies_reported INT(255) DEFAULT 0, times_murdered INT(255) DEFAULT 0, times_ejected INT(255) DEFAULT 0, time_played INT(255) DEFAULT 0)";
						this.mysql_connection.createStatement().execute(sql);

						String sql1 = "CREATE TABLE IF NOT EXISTS selected_cosmetics(username VARCHAR(64) NOT NULL, UUID VARCHAR(64) NOT NULL, type VARCHAR(64) NOT NULL, selected VARCHAR(64) NOT NULL, PRIMARY KEY (UUID, type, selected))";
						this.mysql_connection.createStatement().execute(sql1);

						String sql2 = "CREATE TABLE IF NOT EXISTS unlocked_cosmetics(username VARCHAR(64) NOT NULL, UUID VARCHAR(64) NOT NULL, cosmetic VARCHAR(64) NOT NULL, PRIMARY KEY (UUID, cosmetic))";
						this.mysql_connection.createStatement().execute(sql2);
					}

				} catch (SQLException e) {
					e.printStackTrace();
					return;
				}
			}
		}
		if (!Main.getPlugin().isEnabled()) {
			return;
		}
		if (!this.mysql_enabled.booleanValue()) {
			try {
				File statsFolder = new File(Main.getPlugin().getDataFolder() + File.separator + "stats");
				if (!statsFolder.exists()) {
					statsFolder.mkdir();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		try {
			ConfigurationSection colorsSC = this.config.getConfigurationSection("colors");
			Integer colorI = Integer.valueOf(0);
			for (String key : colorsSC.getKeys(false)) {
				ConfigurationSection colorSC = colorsSC.getConfigurationSection(key);
				String name = colorSC.getString("name", "NULL");
				ChatColor chatColor = ChatColor.valueOf(colorSC.getString("chatColor", "WHITE"));
				Material glass = Material.getMaterial(colorSC.getString("helmetBlock", "BARRIER"));
				Material wool = Material.getMaterial(colorSC.getString("woolBlock", "BARRIER"));
				String colorStr = colorSC.getString("armorColor", "0,0,0").replaceAll("\\s+", "");

				String[] colorSplit = colorStr.split(",");
				if (colorSplit.length != 3) {
					Main.getPlugin().getLogger().warning("Color '" + key + "' armor color is wrong! (" + colorStr + ")");
					continue;
				}
				Color armorColor = Color.fromRGB(Integer.parseInt(colorSplit[0]), Integer.parseInt(colorSplit[1]), Integer.parseInt(colorSplit[2]));
				String id = colorSC.getString("id", "COLORP");
				String height = colorSC.getString("height", "3'6");
				String weight = colorSC.getString("weight", "92");
				String bloodType = colorSC.getString("bloodType", "O-");
				ColorInfo ci = new ColorInfo(colorI, name, chatColor, glass, wool, armorColor, id, height, weight, bloodType);
				this.colors.put(key, ci);
				colorI = Integer.valueOf(colorI + 1);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Main.getPlugin().getLogger().warning("Something is wrong with your colors section in config.yml");
		}
	}

	public void executeCommands(String key, Player player) {
		if (this.commands.get(key) == null || ((ArrayList) this.commands.get(key)).size() == 0) {
			return;
		}

		for (String cmd : this.commands.get(key)) {
			Main.getPlugin().getServer().dispatchCommand((CommandSender) Main.getPlugin().getServer().getConsoleSender(), cmd.replace("%player%", player.getName()));
		}
	}

	private Connection getNewConnection(FileConfiguration config) {
		try {
			if (this.mysql_connection != null && !this.mysql_connection.isClosed()) {
				return this.mysql_connection;
			}
			Class.forName("com.mysql.jdbc.Driver");
			this.mysql_connection = DriverManager.getConnection("jdbc:mysql://" + this.mysql_host + ":" + this.mysql_port + "/" + this.mysql_database, this.mysql_username, this.mysql_password);
			return this.mysql_connection;
		} catch (Exception e) {
			Bukkit.getLogger().info("Can't connect to database! Disabling..");
			e.printStackTrace();
			Bukkit.getServer().getPluginManager().disablePlugin(Main.getPlugin());

			return null;
		}
	}

	public Connection mysql_getConnection() {
		try {
			Boolean status = Boolean.valueOf(mysql_checkConnection());
			if (!status.booleanValue()) {
				System.out.println("Something is wrong with your MySQL server.");
				return null;
			}
			return this.mysql_connection;
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public boolean mysql_checkConnection() throws SQLException, ClassNotFoundException {
		if (this.mysql_connection == null || this.mysql_connection.isClosed()) {
			this.mysql_connection = getNewConnection(Main.getConfigManager().getConfig());

			if (this.mysql_connection == null || this.mysql_connection.isClosed()) {
				return false;
			}
		}
		return true;
	}

	public void delete() {
		this.mainLobby = null;
		this.giveLobbyItems = null;
		this.lobbyItemsSlots = null;
		this.showRunningArenas = null;
		this.blockedCommands = null;
		this.colors = null;
		this.ghostsFly = null;
		this.asteroidsParticleColor = null;
		this.asteroidsParticleMaterial = null;
		this.hidePlayersOutSideArena = null;
		this.enableGlassHelmet = null;
		this.gameEndSendToLobby = null;
		this.particlesOnTasks = null;
		this.asteroidsParticleColor = null;
		this.gameServers = null;
	}

	public ColorInfo getColorByI(Integer i) {
		for (ColorInfo c : this.colors.values()) {
			if (c.getI() == i) {
				return c;
			}
		}
		return null;
	}

	public ArrayList<ColorInfo> getAllColors() {
		return new ArrayList<>(this.colors.values());
	}

	public FileConfiguration getConfig() {
		return this.config;
	}

	public String getPrefix() {
		return this.prefix;
	}

	public Boolean getGiveLobbyItems() {
		return this.giveLobbyItems;
	}

	public Integer getLobbyItemSlot(String key) {
		Integer out = this.lobbyItemsSlots.get(key);
		if (out == null) {
			out = Integer.valueOf(1);
		}
		return out;
	}

	public Location getMainLobby() {
		return this.mainLobby;
	}

	public void setMainLobby(Location loc) {
		this.mainLobby = loc;
	}

	public Boolean getShowRunningArenas() {
		return this.showRunningArenas;
	}

	public List<String> getBlockedCommands() {
		return this.blockedCommands;
	}

	public Boolean getGhostsFly() {
		return this.ghostsFly;
	}

	public Color getAsteroidsParticleColor() {
		return this.asteroidsParticleColor;
	}

	public BlockData getAsteroidsParticleMaterial() {
		return this.asteroidsParticleMaterial;
	}

	public Boolean getHidePlayersOutSideArena() {
		return this.hidePlayersOutSideArena;
	}

	public Boolean getBungeecord() {
		return this.bungeecord;
	}

	public String getBungeecordLobbyServer() {
		return this.bungeecordLobbyServer;
	}

	public Boolean getEnableGlassHelmet() {
		return this.enableGlassHelmet;
	}

	public Boolean getGameEndSendToLobby() {
		return this.gameEndSendToLobby;
	}

	public Boolean getParticlesOnTasks() {
		return this.particlesOnTasks;
	}

	public Particle getParticlesOnTasksType() {
		return this.particlesOnTasksType;
	}

	public Boolean getMysql_enabled() {
		return this.mysql_enabled;
	}

	public String getMysql_host() {
		return this.mysql_host;
	}

	public String getMysql_database() {
		return this.mysql_database;
	}

	public String getMysql_username() {
		return this.mysql_username;
	}

	public String getMysql_password() {
		return this.mysql_password;
	}

	public String getMysql_port() {
		return this.mysql_port;
	}

	public Connection getMysql_connection() {
		return this.mysql_connection;
	}

	public Boolean getEnableLobbyScoreboard() {
		return this.enableLobbyScoreboard;
	}

	public Boolean getTpToLobbyOnJoin() {
		return this.tpToLobbyOnJoin;
	}

	public Boolean getBungeecordIsLobby() {
		return this.bungeecordIsLobby;
	}

	public ArrayList<String> getGameServers() {
		return this.gameServers;
	}

	public String getServerName() {
		return this.serverName;
	}

	public Material getViewGlassMat() {
		return this.viewGlassMat;
	}

	public Boolean getEnablePortalJoin() {
		return this.enablePortalJoin;
	}

	public Boolean getScoreboardUsePackets() {
		return this.scoreboardUsePackets;
	}

	public Boolean getEnableDoubleImposterChance() {
		return this.enableDoubleImposterChance;
	}

	public Boolean getSneakToVent() {
		return this.sneakToVent;
	}

	public Boolean getDamageOnSabotage() {
		return this.damageOnSabotage;
	}

	public Boolean getSaveInventory() {
		return this.saveInventory;
	}
}
