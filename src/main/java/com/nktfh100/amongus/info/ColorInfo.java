package com.nktfh100.amongus.info;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;

public class ColorInfo implements Comparable<ColorInfo> {
	private Integer i;
	private String name;
	private ChatColor chatColor;
	private Material glass;
	private Material wool;
	private Color armorColor;
	private String id;
	private String height;
	private String weight;
	private String bloodType;

	public ColorInfo(Integer i, String name, ChatColor chatColor, Material glass, Material wool, Color armorColor, String id, String height, String weight, String bloodType) {
		this.i = i;
		this.name = name;
		this.chatColor = chatColor;
		this.glass = glass;
		this.wool = wool;
		this.armorColor = armorColor;
		this.id = id;
		this.height = height;
		this.weight = weight;
		this.bloodType = bloodType;
	}

	public String getName() {
		return this.name;
	}

	public ChatColor getChatColor() {
		return this.chatColor;
	}

	public Material getGlass() {
		return this.glass;
	}

	public Material getWool() {
		return this.wool;
	}

	public Color getArmorColor() {
		return this.armorColor;
	}

	public String toString() {
		return this.name;
	}

	public String getId() {
		return this.id;
	}

	public String getHeight() {
		return this.height;
	}

	public String getWeight() {
		return this.weight;
	}

	public String getBloodType() {
		return this.bloodType;
	}

	public Integer getI() {
		return this.i;
	}

	public int compareTo(ColorInfo o) {
		return this.i.compareTo(o.getI());
	}
}
