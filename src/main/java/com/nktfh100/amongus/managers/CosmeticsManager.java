package com.nktfh100.amongus.managers;

import com.nktfh100.AmongUs.enums.CosmeticType;
import com.nktfh100.AmongUs.info.CosmeticItem;
import com.nktfh100.AmongUs.main.Main;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class CosmeticsManager {
	private HashMap<CosmeticType, ArrayList<CosmeticItem>> cosmeticsOrder = new HashMap<>();

	private HashMap<CosmeticType, HashMap<String, CosmeticItem>> cosmetics = new HashMap<>();
	private HashMap<CosmeticType, String> defaultCosmetics = new HashMap<>();
	private HashMap<String, Integer> coins = new HashMap<>();

	public void loadCosmetics() {
		File configFIle = new File(Main.getPlugin().getDataFolder(), "cosmetics.yml");
		if (!configFIle.exists()) {
			try {
				Main.getPlugin().saveResource("cosmetics.yml", false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		YamlConfiguration config = YamlConfiguration.loadConfiguration(configFIle);
		try {
			this.cosmetics = new HashMap<>();
			byte b;
			int i;
			CosmeticType[] arrayOfCosmeticType;
			for (i = (arrayOfCosmeticType = CosmeticType.values()).length, b = 0; b < i;) {
				CosmeticType cosmetic_ = arrayOfCosmeticType[b];
				String type = cosmetic_.getName();
				ConfigurationSection typeSec = config.getConfigurationSection(type);
				if (typeSec != null) {

					this.cosmetics.put(CosmeticType.valueOf(type.toUpperCase()), new HashMap<>());
					this.defaultCosmetics.put(CosmeticType.valueOf(type.toUpperCase()), config.getString("default_" + type));
					this.cosmeticsOrder.put(CosmeticType.valueOf(type.toUpperCase()), new ArrayList<>());
					for (String itemKey : typeSec.getKeys(false)) {
						ConfigurationSection itemSec = typeSec.getConfigurationSection(itemKey);
						Material mat = Material.getMaterial(itemSec.getString("material", "BARRIER"));
						String displayName = ChatColor.translateAlternateColorCodes('&', itemSec.getString("display_name", "display name"));
						String name = itemSec.getString("name", "cosmetic name");
						int slot = itemSec.getInt("slot", 0);
						ArrayList<String> lore = (ArrayList<String>) itemSec.getStringList("lore");
						ArrayList<String> lore2 = (ArrayList<String>) itemSec.getStringList("lore2");
						ArrayList<String> lore3 = (ArrayList<String>) itemSec.getStringList("lore3");
						int price = itemSec.getInt("price", 0);
						String permission = itemSec.getString("permission", "");
						CosmeticItem cosmeticItem = new CosmeticItem(itemKey, mat, displayName, name, slot, lore, lore2, lore3, price, permission);
						((HashMap<String, CosmeticItem>) this.cosmetics.get(CosmeticType.valueOf(type.toUpperCase()))).put(itemKey, cosmeticItem);
						((ArrayList<CosmeticItem>) this.cosmeticsOrder.get(CosmeticType.valueOf(type.toUpperCase()))).add(cosmeticItem);
					}
				}
				b++;
			}

			if (config.getConfigurationSection("coins") != null) {
				ConfigurationSection coinsSC = config.getConfigurationSection("coins");
				for (String key : coinsSC.getKeys(false)) {
					this.coins.put(key, Integer.valueOf(coinsSC.getInt(key, 0)));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			Bukkit.getLogger().log(Level.SEVERE, "Something is wrong with your cosmetics.yml file!");
			Main.getPlugin().getPluginLoader().disablePlugin(Main.getPlugin());
		}
	}

	public void addCoins(String key, Player player) {
		if (Main.getIsPlayerPoints().booleanValue() && this.coins.get(key) != null && ((Integer) this.coins.get(key)) != 0) {
			Main.getPlayerPointsApi().give(player.getUniqueId(), ((Integer) this.coins.get(key)));
			player.sendMessage(Main.getMessagesManager().getGameMsg("playerCoins", null, "" + this.coins.get(key)));
		}
	}

	public String getDefaultCosmetic(CosmeticType key) {
		return this.defaultCosmetics.get(key);
	}

	public ArrayList<CosmeticItem> getAllCosmeticsFor(CosmeticType key) {
		return new ArrayList<CosmeticItem>(this.cosmetics.get(key).values());
	}

	public CosmeticItem getCosmeticItem(CosmeticType group, String key) {
		return this.cosmetics.get(group).get(key);
	}

	public ArrayList<CosmeticItem> getOrderedCosmetics(CosmeticType key) {
		return this.cosmeticsOrder.get(key);
	}

	public void delete() {
		this.cosmetics = null;
		this.defaultCosmetics = null;
		this.cosmeticsOrder = null;
	}
}
