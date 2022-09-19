package com.nktfh100.amongus.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.nktfh100.AmongUs.api.AmongUsApi;
import com.nktfh100.AmongUs.enums.GameState;
import com.nktfh100.AmongUs.info.ColorInfo;
import com.nktfh100.AmongUs.main.Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.UUID;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class Utils {
	public static final String ABC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	static final String colors_ = "123456789abcdfe";

	public static Material getStateBlock(GameState state) {
		switch (state) {
		case WAITING:
			return Material.LIME_STAINED_GLASS;
		case STARTING:
			return Material.BLUE_STAINED_GLASS;
		case RUNNING:
			return Material.RED_STAINED_GLASS;
		case FINISHING:
			return Material.YELLOW_STAINED_GLASS;
		}
		return Material.WHITE_STAINED_GLASS;
	}

	public static ChatColor getStateColor(GameState state) {
		switch (state) {
		case WAITING:
			return ChatColor.GREEN;
		case STARTING:
			return ChatColor.BLUE;
		case RUNNING:
			return ChatColor.RED;
		case FINISHING:
			return ChatColor.YELLOW;
		}
		return ChatColor.WHITE;
	}

	public static ArrayList<ColorInfo> getPlayersColors() {
		ArrayList<ColorInfo> colors = Main.getConfigManager().getAllColors();
		Collections.shuffle(colors);
		return colors;
	}

	public static ItemStack getArmorColor(ColorInfo color, Material type_) {
		ItemStack armor = new ItemStack(type_, 1);
		LeatherArmorMeta lch = (LeatherArmorMeta) armor.getItemMeta();
		lch.setColor(color.getArmorColor());
		armor.setItemMeta((ItemMeta) lch);
		return armor;
	}

	public static String locationToStringB(Location loc) {
		if (loc == null || loc.getWorld() == null) {
			return "()";
		}
		String output = "(" + loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + ")";
		return output;
	}

	public static ItemStack setItemName(ItemStack item, String name, ArrayList<String> lore) {
		if (item != null && item.getType() != Material.AIR) {
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
			ArrayList<String> metaLore = new ArrayList<>();

			for (String lore_ : lore) {
				metaLore.add(lore_);
			}
			meta.setLore(metaLore);
			item.setItemMeta(meta);
		}
		return item;
	}

	public static ItemStack setItemLore(ItemStack item, ArrayList<String> lore) {
		if (item != null && item.getType() != Material.AIR) {
			ItemMeta meta = item.getItemMeta();
			ArrayList<String> metaLore = new ArrayList<>();
			for (String lore_ : lore) {
				metaLore.add(ChatColor.translateAlternateColorCodes('&', lore_));
			}
			meta.setLore(metaLore);
			item.setItemMeta(meta);
		}
		return item;
	}

	public static ItemStack enchantedItem(ItemStack item, Enchantment ench, int lvl) {
		if (item != null && item.getType() != Material.AIR && item.getItemMeta() != null) {
			ItemMeta meta = item.getItemMeta();
			meta.addEnchant(ench, lvl, true);
			meta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ENCHANTS });
			item.setItemMeta(meta);
		}
		return item;
	}

	public static ItemStack createItem(Material mat, String name) {
		ItemStack item = new ItemStack(mat);
		if (item != null && item.getItemMeta() != null && item.getType() != Material.AIR) {
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(name);
			meta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ATTRIBUTES });
			item.setItemMeta(meta);
		}
		return item;
	}

	public static ItemStack createItem(Material mat, String name, int amount) {
		ItemStack item = createItem(mat, name);
		item.setAmount(amount);
		return item;
	}

	public static ItemStack createItem(Material mat, String name, int amount, String... lore) {
		ItemStack item = createItem(mat, name, amount);
		if (item != null && item.getItemMeta() != null && item.getType() != Material.AIR) {
			ItemMeta meta = item.getItemMeta();

			ArrayList<String> metaLore = new ArrayList<>();
			byte b;
			int i;
			String[] arrayOfString;
			for (i = (arrayOfString = lore).length, b = 0; b < i;) {
				String lorecomments = arrayOfString[b];
				metaLore.add(lorecomments);
				b++;
			}

			meta.setLore(metaLore);
			item.setItemMeta(meta);
		}
		return item;
	}

	public static ItemStack createItem(Material mat, String name, int amount, ArrayList<String> lore) {
		ItemStack item = createItem(mat, name, amount);
		if (item != null && item.getItemMeta() != null && item.getType() != Material.AIR) {
			ItemMeta meta = item.getItemMeta();

			meta.setLore(lore);
			item.setItemMeta(meta);
		}
		return item;
	}

	public static ItemStack createEnchantedItem(Material mat, String name, Enchantment ench, int lvl, String... lore) {
		ItemStack item = createItem(mat, name, 1);
		if (item != null && item.getItemMeta() != null && item.getType() != Material.AIR) {
			ItemMeta meta = item.getItemMeta();
			meta.addEnchant(ench, lvl, true);
			ArrayList<String> metaLore = new ArrayList<>();
			byte b;
			int i;
			String[] arrayOfString;
			for (i = (arrayOfString = lore).length, b = 0; b < i;) {
				String lorecomments = arrayOfString[b];
				metaLore.add(lorecomments);
				b++;
			}

			meta.setLore(metaLore);
			meta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ENCHANTS });
			item.setItemMeta(meta);
		}
		return item;
	}

	public static ItemStack addItemFlag(ItemStack item, ItemFlag... flag) {
		if (item != null && item.getItemMeta() != null && item.getType() != Material.AIR) {
			ItemMeta meta = item.getItemMeta();
			meta.addItemFlags(flag);
			item.setItemMeta(meta);
		}
		return item;
	}

	public static float getRandomFloat(float min, float max) {
		Random rand = new Random();
		return rand.nextFloat() * (max - min) + min;
	}

	public static int getRandomNumberInRange(int min, int max) {
		Random r = new Random();
		return r.nextInt(max - min + 1) + min;
	}

	public static String getRandomColors() {
		String str = "";
		for (int i = 0; i < 4; i++) {
			String char_ = String.valueOf("123456789abcdfe".charAt(getRandomNumberInRange(0, "123456789abcdfe".length() - 1)));
			str = String.valueOf(str) + "&" + char_;
		}
		return ChatColor.translateAlternateColorCodes('&', str);
	}

	static final char[] chars_ = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz01234567890".toCharArray();

	public static String getRandomString(int length) {
		String randomString = "";

		Random random = new Random();
		for (int i = 0; i < length; i++) {
			randomString = String.valueOf(randomString) + chars_[random.nextInt(chars_.length)];
		}

		return randomString;
	}

	public static String reverseString(String str) {
		StringBuilder strBuilder = new StringBuilder();

		strBuilder.append(str);
		strBuilder = strBuilder.reverse();
		return strBuilder.toString();
	}

	public static ItemStack createSkull(String url, String name, int amount, ArrayList<String> lore) {
		ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
		if (url.isEmpty()) {
			return head;
		}
		SkullMeta headMeta = (SkullMeta) head.getItemMeta();
		GameProfile profile = new GameProfile(UUID.randomUUID(), null);
		profile.getProperties().put("textures", new Property("textures", url));

		try {
			Field profileField = headMeta.getClass().getDeclaredField("profile");
			profileField.setAccessible(true);
			profileField.set(headMeta, profile);
		} catch (IllegalArgumentException | NoSuchFieldException | SecurityException | IllegalAccessException error) {
			error.printStackTrace();
		}
		headMeta.setDisplayName(name);
		head.setAmount(amount);
		ArrayList<String> metaLore = new ArrayList<>();

		for (String lorecomments : lore) {
			metaLore.add(lorecomments);
		}
		headMeta.setLore(metaLore);
		head.setItemMeta((ItemMeta) headMeta);
		return head;
	}

	public static ItemStack createSkull(String url, String name, int amount, String... lore) {
		ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
		if (url.isEmpty()) {
			return head;
		}
		SkullMeta headMeta = (SkullMeta) head.getItemMeta();
		GameProfile profile = new GameProfile(UUID.randomUUID(), null);
		profile.getProperties().put("textures", new Property("textures", url));

		try {
			Field profileField = headMeta.getClass().getDeclaredField("profile");
			profileField.setAccessible(true);
			profileField.set(headMeta, profile);
		} catch (IllegalArgumentException | NoSuchFieldException | SecurityException | IllegalAccessException error) {
			error.printStackTrace();
		}
		headMeta.setDisplayName(name);
		head.setAmount(amount);
		ArrayList<String> metaLore = new ArrayList<>();
		byte b;
		int i;
		String[] arrayOfString;
		for (i = (arrayOfString = lore).length, b = 0; b < i;) {
			String lorecomments = arrayOfString[b];
			metaLore.add(lorecomments);
			b++;
		}

		headMeta.setLore(metaLore);
		head.setItemMeta((ItemMeta) headMeta);
		return head;
	}

	@SuppressWarnings("deprecation")
	public static ItemStack getHead(String player) {
		ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1);
		try {
			SkullMeta skull = (SkullMeta) item.getItemMeta();
			skull.setOwner(player);
			item.setItemMeta((ItemMeta) skull);
			return item;
		} catch (Exception e) {
			Bukkit.getLogger().info("Couldnt get " + player + "'s head");
			e.printStackTrace();
			return item;
		}
	}

	public static void addBorder(Inventory inv, Integer slots, Material mat) {
		ItemStack border = createItem(mat, " ");
		if (slots == 45) {
			for (int slot = 0; slot < inv.getSize(); slot++) {
				if (inv.getItem(slot) == null && ((slot >= 0 && slot <= 8) || (slot >= 36 && slot <= 44) || slot % 9 == 0 || (slot - 8) % 9 == 0)) {
					inv.setItem(slot, border);
				}
			}

		} else if (slots == 54) {
			for (int slot = 0; slot < inv.getSize(); slot++) {
				if (inv.getItem(slot) == null && ((slot >= 0 && slot <= 8) || (slot >= 45 && slot <= 53) || slot % 9 == 0 || (slot - 8) % 9 == 0)) {
					inv.setItem(slot, border);
				}
			}
		}
	}

	public static void addBorder(Inventory inv, Integer slots) {
		addBorder(inv, slots, Material.BLUE_STAINED_GLASS_PANE);
	}

	public static void fillInv(Inventory inv) {
		fillInv(inv, Material.BLACK_STAINED_GLASS_PANE);
	}

	public static void fillInv(Inventory inv, Material mat) {
		ItemStack item = createItem(mat, " ");
		for (int slot = 0; slot < inv.getSize(); slot++) {
			inv.setItem(slot, item);
		}
	}

	public static ArrayList<Integer> generateLights() {
		ArrayList<Integer> out = new ArrayList<>();
		Integer offNum = Integer.valueOf(getRandomNumberInRange(2, 5));
		for (int i = 0; i < 5; i++) {
			if (offNum > 0) {
				out.add(Integer.valueOf(0));
				offNum = Integer.valueOf(offNum - 1);
			} else {
				out.add(Integer.valueOf(1));
			}
		}
		Collections.shuffle(out);
		return out;
	}

	public static void sendActionBar(Player player, String message) {
		if (!player.isOnline()) {
			return;
		}
		if (message == null) {
			message = "";
		}
		player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
	}

	public static ArmorStand createArmorStand(Location loc) {
		ArmorStand as = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
		as.setBasePlate(false);
		as.setArms(false);
		as.setGravity(false);
		as.setCollidable(false);
		as.setInvulnerable(true);
		as.setVisible(false);
		as.setSilent(true);
		as.setCustomNameVisible(false);
		return as;
	}

	public static ArrayList<Location> generateHollowCircle(Location loc, int radius, int height) {
		ArrayList<Location> coordinates = new ArrayList<>();
		int y = loc.getBlockY();
		for (int yi = 0; yi < height; yi++) {

			int x = radius;
			int z = 0;
			int err = 1 - x;

			while (x >= z) {

				coordinates.add(new Location(loc.getWorld(), (x + loc.getBlockX()), y, (-z + loc.getBlockZ())));
				coordinates.add(new Location(loc.getWorld(), (z + loc.getBlockX()), y, (-x + loc.getBlockZ())));
				coordinates.add(new Location(loc.getWorld(), (-z + loc.getBlockX()), y, (-x + loc.getBlockZ())));
				coordinates.add(new Location(loc.getWorld(), (-x + loc.getBlockX()), y, (-z + loc.getBlockZ())));
				coordinates.add(new Location(loc.getWorld(), (-x + loc.getBlockX()), y, (z + loc.getBlockZ())));
				coordinates.add(new Location(loc.getWorld(), (-z + loc.getBlockX()), y, (x + loc.getBlockZ())));
				coordinates.add(new Location(loc.getWorld(), (z + loc.getBlockX()), y, (x + loc.getBlockZ())));
				coordinates.add(new Location(loc.getWorld(), (x + loc.getBlockX()), y, (z + loc.getBlockZ())));

				z++;
				if (err <= 0) {
					err += 2 * z + 1;
					continue;
				}
				x--;
				err += 2 * (z - x) + 1;
			}

			y++;
		}
		return coordinates;
	}

	public static boolean hasChangedBlockCoordinates(Location fromLoc, Location toLoc) {
		return !(fromLoc.getWorld().equals(toLoc.getWorld()) && fromLoc.getBlockX() == toLoc.getBlockX() && fromLoc.getBlockZ() == toLoc.getBlockZ());
	}

	public static Integer isInsideCircle(Location center, Double r, Location loc) {
		Double output = Double.valueOf(Math.pow(loc.getX() - center.getX(), 2.0D) + Math.pow(loc.getZ() - center.getZ(), 2.0D));
		Double r1 = Double.valueOf(Math.pow(r.doubleValue(), 2.0D));
		if (output == r1) {
			return Integer.valueOf(1);
		}
		if (output.doubleValue() < r1.doubleValue()) {
			return Integer.valueOf(0);
		}
		return Integer.valueOf(2);
	}

	public static ArrayList<Block> blocksFromTwoPoints(Location loc1, Location loc2) {
		ArrayList<Block> blocks = new ArrayList<>();

		if (loc1 == null || loc2 == null) {
			return blocks;
		}
		if (loc1.getWorld() == null || loc2.getWorld() == null) {
			return blocks;
		}

		int topBlockX = (loc1.getBlockX() < loc2.getBlockX()) ? loc2.getBlockX() : loc1.getBlockX();
		int bottomBlockX = (loc1.getBlockX() > loc2.getBlockX()) ? loc2.getBlockX() : loc1.getBlockX();

		int topBlockY = (loc1.getBlockY() < loc2.getBlockY()) ? loc2.getBlockY() : loc1.getBlockY();
		int bottomBlockY = (loc1.getBlockY() > loc2.getBlockY()) ? loc2.getBlockY() : loc1.getBlockY();

		int topBlockZ = (loc1.getBlockZ() < loc2.getBlockZ()) ? loc2.getBlockZ() : loc1.getBlockZ();
		int bottomBlockZ = (loc1.getBlockZ() > loc2.getBlockZ()) ? loc2.getBlockZ() : loc1.getBlockZ();

		for (int x = bottomBlockX; x <= topBlockX; x++) {
			for (int z = bottomBlockZ; z <= topBlockZ; z++) {
				for (int y = bottomBlockY; y <= topBlockY; y++) {
					Block block = loc1.getWorld().getBlockAt(x, y, z);
					blocks.add(block);
				}
			}
		}

		return blocks;
	}

	public static Boolean isLocationZero(Location loc) {
		if (loc.getBlockX() == 0 && loc.getBlockY() == 0 && loc.getBlockZ() == 0) {
			return Boolean.valueOf(true);
		}

		return Boolean.valueOf(false);
	}

	public static String[] getSkinData(String name) {
		try {
			URL url_0 = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
			InputStreamReader reader_0 = new InputStreamReader(url_0.openStream());
			String uuid = new JsonParser().parse(reader_0).getAsJsonObject().get("id").getAsString();

			URL url_1 = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
			InputStreamReader reader_1 = new InputStreamReader(url_1.openStream());
			JsonObject textureProperty = new JsonParser().parse(reader_1).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();
			String texture = textureProperty.get("value").getAsString();
			String signature = textureProperty.get("signature").getAsString();
			return new String[] { texture, signature };
		} catch (IOException e) {
			System.err.println("Could not get skin data from session servers!");
			e.printStackTrace();
			return null;
		}
	}
}
