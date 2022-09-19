package com.nktfh100.amongus;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.nktfh100.amongus.main.SomeExpansion;
import org.bukkit.*;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Level;

public final class AmongUs extends JavaPlugin {
    private static final PacketType[] ENTITY_PACKETS = new PacketType[] { PacketType.Play.Server.ENTITY_EQUIPMENT, PacketType.Play.Server.ANIMATION, PacketType.Play.Server.COLLECT,
            PacketType.Play.Server.SPAWN_ENTITY_EXPERIENCE_ORB, PacketType.Play.Server.ENTITY_VELOCITY, PacketType.Play.Server.REL_ENTITY_MOVE, PacketType.Play.Server.ENTITY_LOOK,
            PacketType.Play.Server.ENTITY_TELEPORT, PacketType.Play.Server.ENTITY_HEAD_ROTATION, PacketType.Play.Server.ENTITY_STATUS, PacketType.Play.Server.ATTACH_ENTITY,
            PacketType.Play.Server.ENTITY_METADATA, PacketType.Play.Server.ENTITY_EFFECT, PacketType.Play.Server.REMOVE_ENTITY_EFFECT, PacketType.Play.Server.BLOCK_BREAK_ANIMATION,
            PacketType.Play.Server.REL_ENTITY_MOVE_LOOK };

    private static Plugin plugin;

    private static ConfigManager configManager;

    private static PlayersManager playersManager;
    private static ArenaManager arenaManager;
    private static BungeArenaManager bungeArenaManager;
    private static MessagesManager messagesManager;
    private static ItemsManager itemsManager;
    private static SoundsManager soundsManager;
    private static CosmeticsManager cosmeticsManager;
    private static Boolean isVentureChat = Boolean.valueOf(false);
    private static Boolean isPlaceHolderAPI = Boolean.valueOf(false);
    private static Boolean isPlayerPoints = Boolean.valueOf(false);
    private static PlayerPointsAPI playerPointsApi = null;

    public void onEnable() {
        plugin = (Plugin) this;

        String ver = getServer().getVersion();
        Boolean isOk = Boolean.valueOf(false);
        if (ver.contains("1.16")) {
            isOk = Boolean.valueOf(true);
        }
        if (!isOk.booleanValue()) {
            Bukkit.getLogger().log(Level.SEVERE, "Server version not supported! (1.16 - 1.16.5)");
            getPluginLoader().disablePlugin((Plugin) this);
            return;
        }
        if (getServer().getPluginManager().getPlugin("VentureChat") != null) {
            isVentureChat = Boolean.valueOf(true);
            getServer().getPluginManager().registerEvents((Listener) new VentureChatEvent_(), (Plugin) this);
        }

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            (new SomeExpansion((Plugin) this)).register();
            isPlaceHolderAPI = Boolean.valueOf(true);
        }

        if (Bukkit.getPluginManager().getPlugin("PlayerPoints") != null) {
            isPlayerPoints = Boolean.valueOf(true);
            playerPointsApi = ((PlayerPoints) Bukkit.getPluginManager().getPlugin("PlayerPoints")).getAPI();
        } else {
            Bukkit.getLogger().log(Level.WARNING, "The plugin 'PlayerPoints' is not preset, cosmetics will not work!");
        }
        cosmeticsManager = new CosmeticsManager();

        configManager = new ConfigManager(plugin.getConfig());

        configManager.loadConfig();
        if (!plugin.isEnabled()) {
            return;
        }
        if (configManager.getBungeecord().booleanValue()) {
            getServer().getMessenger().registerOutgoingPluginChannel((Plugin) this, "BungeeCord");
            if (configManager.getBungeecordIsLobby().booleanValue()) {
                bungeArenaManager = new BungeArenaManager(configManager.getGameServers());
                getServer().getMessenger().registerIncomingPluginChannel((Plugin) this, "BungeeCord", (PluginMessageListener) bungeArenaManager);
            }
        }

        if (!plugin.isEnabled()) {
            return;
        }
        messagesManager = new MessagesManager();
        itemsManager = new ItemsManager();
        soundsManager = new SoundsManager();
        messagesManager.loadAll();
        itemsManager.loadItems();
        soundsManager.loadSounds();
        cosmeticsManager.loadCosmetics();
        arenaManager = new ArenaManager();
        arenaManager.loadArenas();
        playersManager = new PlayersManager();

        if (configManager.getBungeecord().booleanValue() && configManager.getBungeecordIsLobby().booleanValue()) {
            bungeArenaManager.createInventory();
            bungeArenaManager.updateArenaSelectorInv();
            if (arenaManager.getAllArenas().size() > 0) {
                arenaManager.sendBungeUpdate(arenaManager.getAllArenas().iterator().next());
            }
        }
        getCommand("aua").setExecutor((CommandExecutor) new AdminCommand());
        getCommand("aua").setTabCompleter((TabCompleter) new AdminCommandTab());

        getCommand("au").setExecutor((CommandExecutor) new PlayersCommand());
        getCommand("au").setTabCompleter((TabCompleter) new PlayersCommandTab());
        getServer().getPluginManager().registerEvents((Listener) playersManager, (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new PlayerDamage(), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new BlockBreak(), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new BlockPlace(), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new HungerChange(), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new PlayerDrop(), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new PlayerRightClick(), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new PlayerChat(), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new InvClick(), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new PlayerPickUp(), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new PlayerSwapHand(), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new SignChange(), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new InvClose(), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new PlayerMove(), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new EntityInteract(), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new PlayerSneak(), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new PlayerCommand(), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new ArmorStandManipulate(), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new ServerPingEvent(), (Plugin) this);
        getServer().getPluginManager().registerEvents((Listener) new PlayerEnterPortal(), (Plugin) this);

        Bukkit.getLogger().info("[AmongUs] Plugin made by nktfh100");
        Bukkit.getLogger().info("[AmongUs] Made in israel!");

        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

        protocolManager.addPacketListener((PacketListener) new PacketAdapter((Plugin) this, ListenerPriority.NORMAL, new PacketType[] { PacketType.Play.Server.ENTITY_EQUIPMENT }) {
            public void onPacketSending(PacketEvent ev) {
                if (ev.getPlayer() == null) {
                    return;
                }
                PlayerInfo pInfo = Main.getPlayersManager().getPlayerInfo(ev.getPlayer());
                if (pInfo != null && pInfo.getIsIngame().booleanValue()) {
                    List<Pair<EnumWrappers.ItemSlot, ItemStack>> newSlotStack = new ArrayList<>();

                    for (Pair<ItemSlot, ItemStack> pair : ev.getPacket().getSlotStackPairLists().read(0)) {
                        if (pair.getFirst() == EnumWrappers.ItemSlot.MAINHAND) {
                            newSlotStack.add(new Pair<ItemSlot, ItemStack>(EnumWrappers.ItemSlot.MAINHAND, new ItemStack(Material.AIR, 1)));
                        } else if (pair.getFirst() == EnumWrappers.ItemSlot.OFFHAND) {
                            newSlotStack.add(new Pair<ItemSlot, ItemStack>(EnumWrappers.ItemSlot.OFFHAND, new ItemStack(Material.AIR, 1)));
                        } else {
                            newSlotStack.add(pair);
                        }
                    }
                    ev.getPacket().getSlotStackPairLists().write(0, newSlotStack);
                }
            }
        });

        protocolManager.addPacketListener((PacketListener) new PacketAdapter((Plugin) this, ListenerPriority.NORMAL, new PacketType[] { PacketType.Play.Client.USE_ENTITY }) {
            public void onPacketReceiving(PacketEvent ev) {
                final Player attacker = ev.getPlayer();
                final PlayerInfo attackerInfo = Main.getPlayersManager().getPlayerInfo(ev.getPlayer());
                if (attackerInfo == null || attackerInfo.getArena() == null || attackerInfo.getArena().getPlayersInfo() == null) {
                    return;
                }
                Entity victimEnt = (Entity) ev.getPacket().getEntityModifier(ev.getPlayer().getWorld()).read(0);
                Player victim = null;
                PlayerInfo victimInfo = null;
                if (victimEnt == null) {
                    int entityId = ((Integer) ev.getPacket().getIntegers().read(0));
                    for (PlayerInfo pInfo1 : attackerInfo.getArena().getPlayersInfo()) {
                        if (pInfo1 != attackerInfo) {
                            if (pInfo1.getIsInCameras().booleanValue() && pInfo1.getFakePlayerId().equals(Integer.valueOf(entityId))) {
                                victim = pInfo1.getPlayer();
                                victimInfo = pInfo1;
                                continue;
                            }
                            if (pInfo1.getIsScanning().booleanValue()) {
                                for (FakeArmorStand fas : pInfo1.getScanArmorStands()) {
                                    if (fas.getEntityId() == entityId) {
                                        victim = pInfo1.getPlayer();
                                        victimInfo = pInfo1;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    if (victim == null) {
                        return;
                    }
                } else if (!(victimEnt instanceof Player)) {
                    if (attackerInfo.getIsIngame().booleanValue() && ev.getPacket().getEntityUseActions().read(0) == EnumWrappers.EntityUseAction.ATTACK) {
                        ev.setCancelled(true);
                    }

                    return;
                }
                if (victim == null) {
                    victim = (Player) victimEnt;
                    victimInfo = Main.getPlayersManager().getPlayerInfo(victim);
                }

                if (ev.getPacket().getEntityUseActions().read(0) == EnumWrappers.EntityUseAction.ATTACK) {
                    if ((!attackerInfo.getIsIngame().booleanValue() && victimInfo.getIsIngame().booleanValue())
                            || (attackerInfo.getIsIngame().booleanValue() && !victimInfo.getIsIngame().booleanValue())) {
                        ev.setCancelled(true);
                        return;
                    }
                    if (!attackerInfo.getIsIngame().booleanValue() || !victimInfo.getIsIngame().booleanValue()) {
                        return;
                    }
                    ev.setCancelled(true);

                    if (attackerInfo.getArena().getGameState() != GameState.RUNNING || !attackerInfo.getIsImposter().booleanValue() || attackerInfo.isGhost().booleanValue()
                            || victimInfo.isGhost().booleanValue() || victimInfo.getIsImposter().booleanValue()) {
                        return;
                    }

                    if (attackerInfo.getArena().getIsInMeeting().booleanValue()) {
                        return;
                    }

                    if (attackerInfo.getKillCoolDown() > 0) {
                        return;
                    }
                    if (attacker.getItemInHand() == null || attacker.getItemInHand().getItemMeta() == null) {
                        ev.setCancelled(true);

                        return;
                    }
                    String itemName = attacker.getItemInHand().getItemMeta().getDisplayName();
                    ItemInfoContainer killItem = Main.getItemsManager().getItem("kill");

                    if (killItem.getItem2().getTitle(attackerInfo.getKillCoolDown().toString()).equals(itemName)) {

                        final Player victim_ = victim;
                        final PlayerInfo victimInfo_ = victimInfo;
                        (new BukkitRunnable() {
                            public void run() {
                                if (victimInfo_.getIsInCameras().booleanValue()) {
                                    victimInfo_.getArena().getCamerasManager().playerLeaveCameras(victimInfo_, Boolean.valueOf(false));
                                }

                                attackerInfo.setKillCoolDown(attackerInfo.getArena().getKillCooldown());
                                Location vicLoc = victim_.getLocation();
                                attacker.teleport(new Location(victim_.getWorld(), vicLoc.getX(), vicLoc.getY(), vicLoc.getZ(), attacker.getLocation().getYaw(), attacker.getLocation().getPitch()));
                                victim_.getWorld().spawnParticle(Particle.BLOCK_CRACK, victim_.getLocation().getX(), victim_.getLocation().getY() + 1.3D, victim_.getLocation().getZ(), 30, 0.4D, 0.4D,
                                        0.4D, Bukkit.createBlockData(Material.REDSTONE_BLOCK));

                                Main.getSoundsManager().playSound("playerDeathAttacker", attacker, victim_.getLocation());
                                Main.getSoundsManager().playSound("playerDeathVictim", victim_, victim_.getLocation());

                                attackerInfo.getArena().playerDeath(attackerInfo, victimInfo_, Boolean.valueOf(true));

                                for (PlayerInfo pInfo : attackerInfo.getArena().getPlayersInfo()) {
                                    if (!pInfo.isGhost().booleanValue()) {
                                        DeadBody db = attackerInfo.getArena().getDeadBodiesManager().isCloseToBody(pInfo.getPlayer().getLocation());
                                        if (db != null) {
                                            pInfo.setCanReportBody(Boolean.valueOf(true), db);
                                        }
                                    }

                                }
                            }
                        }).runTask(Main.getPlugin());
                    }
                }
            }
        });

        protocolManager.addPacketListener((PacketListener) new PacketAdapter((Plugin) this, ListenerPriority.NORMAL, new PacketType[] { PacketType.Play.Server.NAMED_SOUND_EFFECT }) {
            PlayersManager playersManager = Main.getPlayersManager();

            public void onPacketSending(PacketEvent ev) {
                Player player = ev.getPlayer();
                Sound sound = (Sound) ev.getPacket().getSoundEffects().read(0);
                if (ev.getPacket().getSoundCategories().read(0) == EnumWrappers.SoundCategory.PLAYERS) {
                    PlayerInfo pInfo = this.playersManager.getPlayerInfo(player);
                    if (pInfo == null) {
                        return;
                    }
                    if (pInfo.getIsIngame().booleanValue() && !pInfo.isGhost().booleanValue()) {
                        StructureModifier<Integer> ints = ev.getPacket().getIntegers();

                        double x = ((Integer) ints.read(0)) / 8.0D;
                        double y = ((Integer) ints.read(1)) / 8.0D;
                        double z = ((Integer) ints.read(2)) / 8.0D;

                        Predicate<Entity> predicate = i -> (i instanceof Player && this.playersManager.getPlayerInfo((Player) i).isGhost().booleanValue());
                        Collection<Entity> players_ = ev.getPlayer().getWorld().getNearbyEntities(new Location(player.getWorld(), x, y, z), 1.0D, 1.0D, 1.0D, predicate);
                        if (players_.size() > 0) {
                            ev.setCancelled(true);
                            return;
                        }
                    }
                }
                if (sound == Sound.ENTITY_PLAYER_ATTACK_NODAMAGE || sound == Sound.ITEM_ARMOR_EQUIP_GENERIC) {
                    PlayerInfo pInfo = this.playersManager.getPlayerInfo(player);
                    if (pInfo != null && pInfo.getIsIngame().booleanValue()) {
                        ev.setCancelled(true);
                    }
                }
            }
        });

        protocolManager.addPacketListener((PacketListener) new PacketAdapter((Plugin) this, ListenerPriority.NORMAL, new PacketType[] { PacketType.Play.Server.NAMED_ENTITY_SPAWN }) {
            public void onPacketSending(PacketEvent ev) {
                Entity entity = (Entity) ev.getPacket().getEntityModifier(ev.getPlayer().getWorld()).read(0);
                if (entity != null && entity instanceof Player) {
                    PlayerInfo pInfoSentTo = Main.getPlayersManager().getPlayerInfo(ev.getPlayer());
                    if (pInfoSentTo == null) {
                        return;
                    }
                    Arena arena = pInfoSentTo.getArena();
                    if (pInfoSentTo.getIsIngame().booleanValue() && arena.getGameState() == GameState.RUNNING) {
                        PlayerInfo pInfoSpawned = Main.getPlayersManager().getPlayerInfo((Player) entity);
                        if (pInfoSpawned == null) {
                            return;
                        }
                        if (pInfoSpawned.getArena() == arena) {
                            if (pInfoSpawned.getIsInVent().booleanValue() || pInfoSpawned.getIsInCameras().booleanValue()) {
                                ev.setCancelled(true);
                                return;
                            }
                            if (!pInfoSentTo.isGhost().booleanValue() && !pInfoSpawned.isGhost().booleanValue() && !arena.getIsInMeeting().booleanValue()) {
                                if (!arena.getVisibilityManager().canSee(pInfoSentTo, pInfoSpawned).booleanValue()) {
                                    ev.setCancelled(true);
                                    return;
                                }
                            } else if (!pInfoSentTo.isGhost().booleanValue() && pInfoSpawned.isGhost().booleanValue()) {
                                ev.setCancelled(true);
                                return;
                            }
                            if (pInfoSentTo.isGhost().booleanValue() && pInfoSpawned.isGhost().booleanValue()) {
                                ev.setCancelled(false);
                            }
                        }
                    }
                }
            }
        });

        protocolManager.addPacketListener((PacketListener) new PacketAdapter((Plugin) this, ListenerPriority.HIGHEST, ENTITY_PACKETS) {
            public void onPacketSending(PacketEvent ev) {
                try {
                    if (ev.getPlayer() == null || ev.getPlayer().getWorld() == null) {
                        return;
                    }
                    World world = ev.getPlayer().getWorld();
                    Entity entity = (Entity) ev.getPacket().getEntityModifier(world).read(0);
                    if (entity != null && entity instanceof Player) {
                        PlayerInfo sendPacketPlayerInfo = Main.getPlayersManager().getPlayerInfo((Player) entity);
                        if (sendPacketPlayerInfo != null && sendPacketPlayerInfo.getIsIngame().booleanValue() && sendPacketPlayerInfo.isGhost().booleanValue()) {
                            PlayerInfo pInfo = Main.getPlayersManager().getPlayerInfo(ev.getPlayer());
                            if (!pInfo.isGhost().booleanValue()) {
                                ev.setCancelled(true);
                            }
                        }

                    }
                } catch (Exception exception) {
                }
            }
        });

        new Metrics(this, 12109);
    }

    public static void sendPlayerToLobby(Player player) {
        if (!plugin.isEnabled()) {
            return;
        }
        if (configManager.getBungeecord().booleanValue()) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Connect");
            out.writeUTF(configManager.getBungeecordLobbyServer());
            player.sendPluginMessage(getPlugin(), "BungeeCord", out.toByteArray());
        }
    }

    public static void sendPlayerToArena(Player player, String server) {
        if (!plugin.isEnabled()) {
            return;
        }
        if (configManager.getBungeecord().booleanValue()) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Connect");
            out.writeUTF(server);
            player.sendPluginMessage(getPlugin(), "BungeeCord", out.toByteArray());
        }
    }

    public static void reloadConfigs() {
        for (Arena arena : arenaManager.getAllArenas()) {
            arena.deleteHolograms();
            arena.endGame(Boolean.valueOf(false));
            for (Camera cam : arena.getCamerasManager().getCameras()) {
                cam.deleteArmorStands();
            }
        }
        configManager.loadConfigVars();
        messagesManager.loadAll();
        itemsManager.loadItems();
        soundsManager.loadSounds();
        arenaManager.loadArenas();
        cosmeticsManager.loadCosmetics();
    }

    private static Boolean runAsync = Boolean.valueOf(true);

    public void onDisable() {
        if (arenaManager != null) {
            for (Arena arena : arenaManager.getAllArenas()) {
                arena.deleteHolograms();
                arena.endGame(Boolean.valueOf(true));
                arena.delete();
            }
        }
        if (configManager != null) {
            configManager.delete();
        }
        if (messagesManager != null) {
            messagesManager.delete();
        }
        if (itemsManager != null) {
            itemsManager.delete();
        }
        if (soundsManager != null) {
            soundsManager.delete();
        }
        if (playersManager != null) {
            playersManager.delete();
        }
        if (cosmeticsManager != null) {
            cosmeticsManager.delete();
        }
    }

    public static Plugin getPlugin() {
        return plugin;
    }

    public static Boolean shouldRunAsync() {
        return runAsync;
    }

    public static MessagesManager getMessagesManager() {
        return messagesManager;
    }

    public static ItemsManager getItemsManager() {
        return itemsManager;
    }

    public static PlayersManager getPlayersManager() {
        return playersManager;
    }

    public static ArenaManager getArenaManager() {
        return arenaManager;
    }

    public static ConfigManager getConfigManager() {
        return configManager;
    }

    public static SoundsManager getSoundsManager() {
        return soundsManager;
    }

    public static Boolean getIsVentureChat() {
        return isVentureChat;
    }

    public static Boolean getIsPlaceHolderAPI() {
        return isPlaceHolderAPI;
    }

    public static BungeArenaManager getBungeArenaManager() {
        return bungeArenaManager;
    }

    public static Boolean getIsPlayerPoints() {
        return isPlayerPoints;
    }

    public static PlayerPointsAPI getPlayerPointsApi() {
        return playerPointsApi;
    }

    public static CosmeticsManager getCosmeticsManager() {
        return cosmeticsManager;
    }
}
