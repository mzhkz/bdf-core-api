package net.bdfps.api.spigot.java.packet.original;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import net.bdfps.api.spigot.BDF;
import net.bdfps.api.spigot.support.BDFPlayer;
import net.bdfps.api.spigot.BDF;
import net.bdfps.api.spigot.support.BDFPlayer;
import net.minecraft.server.v1_12_R1.*;
import net.minecraft.server.v1_12_R1.EntityHuman;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.*;

public class NMSCorpses_v1_12_R1 {

    private List<NMSCorpseData> corpses;

    public NMSCorpses_v1_12_R1() {
        corpses = new ArrayList<NMSCorpseData>();
        int scheduleSyncRepeatingTask = Bukkit.getServer().getScheduler()
                .scheduleSyncRepeatingTask(BDF.Companion.getInstance(), new Runnable() {
                    public void run() {
                        tick();
                    }
                }, 0L, 1L);
    }

    private static DataWatcher clonePlayerDatawatcher(Player player, int currentEntId) {
        EntityHuman h = new EntityHuman(
                ((CraftWorld) player.getWorld()).getHandle(),
                ((CraftPlayer) player).getProfile()) {
            public void sendMessage(IChatBaseComponent arg0) {
                return;
            }

            public boolean a(int arg0, String arg1) {
                return false;
            }

            public BlockPosition getChunkCoordinates() {
                return null;
            }

            public boolean isSpectator() {
                return false;
            }

            @Override
            public boolean z() {
                return false;
            }
        };
        h.f(currentEntId);
        return h.getDataWatcher();
    }

    private GameProfile cloneProfile(GameProfile oldProf,UUID uuid,
                                     String name) {
        GameProfile newProf = new GameProfile(uuid, name);
        newProf.getProperties().putAll(oldProf.getProperties());
        return newProf;
    }

    private Location getNonClippableBlockUnderPlayer(Location loc, int addToYPos) {
        if (loc.getBlockY() < 0) {
            return null;
        }
        for (int y = loc.getBlockY(); y >= 0; y--) {
            Material m = loc.getWorld()
                    .getBlockAt(loc.getBlockX(), y, loc.getBlockZ()).getType();
            if (m.isSolid()) {
                return new Location(loc.getWorld(), loc.getX(), y + addToYPos,
                        loc.getZ());
            }
        }
        return null;
    }



    public NMSCorpseData spawnCorpse(BDFPlayer p, Location loc, int sec, int facing) {
        int entityId = getNextEntityId();
        GameProfile prof = cloneProfile(
                ((CraftPlayer) p.getBukkitPlayer()).getProfile(),p.getBukkitPlayer().getUniqueId(), "");

        DataWatcher dw = clonePlayerDatawatcher(p.getBukkitPlayer(), entityId);
        DataWatcherObject<Integer> obj = new DataWatcherObject<Integer>(10, DataWatcherRegistry.b);
        dw.set(obj, (int)0); //ハリネズミ防止
        DataWatcherObject<Byte> obj2 = new DataWatcherObject<Byte>(13, DataWatcherRegistry.a);
        dw.set(obj2, (byte)0x7F);

        Location locUnder = getNonClippableBlockUnderPlayer(loc, 1);
        Location used = locUnder != null ? locUnder : loc;
        used.setYaw(loc.getYaw());
        used.setPitch(loc.getPitch());

        NMSCorpseData data = new NMSCorpseData(prof, used, dw, entityId,
                sec * 20, facing);
        data.setPlayer(p); //プレイヤー適用
        corpses.add(data);
        return data;
    }

    /**
     * インベントリを殻にする謎仕様は消した
     * @param data
     */
    public void removeCorpse(NMSCorpseData data) {
        corpses.remove(data);
        data.destroyCorpseFromEveryone();

    }

    private int getNextEntityId() {
        try {
            Field entityCount = Entity.class.getDeclaredField("entityCount");
            entityCount.setAccessible(true);
            int id = entityCount.getInt(null);
            entityCount.setInt(null, id + 1);
            return id;
        } catch (Exception e) {
            e.printStackTrace();
            return (int) Math.round(Math.random() * Integer.MAX_VALUE * 0.25);
        }
    }

    public class NMSCorpseData {
        private Map<Player, Boolean> canSee;
        private Map<Player, Integer> tickLater;
        private GameProfile prof;
        private Location loc;
        private DataWatcher metadata;
        private int entityId;
        private int ticksLeft;
        private int slot;
        private boolean stopCount;

        private BDFPlayer playerData;

        private int rotation;

        public NMSCorpseData(GameProfile prof, Location loc,
                             DataWatcher metadata, int entityId, int ticksLeft, int rotation) {
            this.prof = prof;
            this.loc = loc;
            this.metadata = metadata;
            this.entityId = entityId;
            this.ticksLeft = ticksLeft;
            this.canSee = new HashMap<Player, Boolean>();
            this.tickLater = new HashMap<Player, Integer>();
            this.rotation = rotation;
            if(rotation >3 || rotation < 0) {
                this.rotation = 0;
            }
        }


        public int getRotation() {
            return rotation;
        }

        public ItemStack convertBukkitToMc(org.bukkit.inventory.ItemStack stack){
            if(stack == null){
                return new ItemStack(Item.getById(0));
            }
            ItemStack temp = CraftItemStack.asNMSCopy(stack.clone());
            return temp;
        }


        public void setCanSee(Player p, boolean canSee) {
            this.canSee.put(p, Boolean.valueOf(canSee));
        }

        public boolean canSee(Player p) {
            return canSee.get(p).booleanValue();
        }

        public void removeFromMap(Player p) {
            canSee.remove(p);
        }

        public boolean mapContainsPlayer(Player p) {
            return canSee.containsKey(p);
        }

        public Set<Player> getPlayersWhoSee() {
            return canSee.keySet();
        }

        public void removeAllFromMap(Collection<Player> players) {
            canSee.keySet().removeAll(players);
        }

        public void setTicksLeft(int ticksLeft) {
            this.ticksLeft = ticksLeft;
        }

        public int getTicksLeft() {
            return ticksLeft;
        }

        public PacketPlayOutNamedEntitySpawn getSpawnPacket() {
            PacketPlayOutNamedEntitySpawn packet = new PacketPlayOutNamedEntitySpawn();
            try {
                Field a = packet.getClass().getDeclaredField("a");
                a.setAccessible(true);
                a.set(packet, entityId);
                Field b = packet.getClass().getDeclaredField("b");
                b.setAccessible(true);
                b.set(packet, prof.getId());
                Field c = packet.getClass().getDeclaredField("c");
                c.setAccessible(true);
                c.setDouble(packet, loc.getX());
                Field d = packet.getClass().getDeclaredField("d");
                d.setAccessible(true);
                d.setDouble(packet, loc.getY()+ 1.0f/16.0f);
                Field e = packet.getClass().getDeclaredField("e");
                e.setAccessible(true);
                e.setDouble(packet, loc.getZ());
                Field f = packet.getClass().getDeclaredField("f");
                f.setAccessible(true);
                f.setByte(packet, (byte) (int) (loc.getYaw() * 256.0F / 360.0F));
                Field g = packet.getClass().getDeclaredField("g");
                g.setAccessible(true);
                g.setByte(packet,
                        (byte) (int) (loc.getPitch() * 256.0F / 360.0F));
                Field i = packet.getClass().getDeclaredField("h");
                i.setAccessible(true);
                i.set(packet, metadata);
            } catch (Exception e) {

                e.printStackTrace();
            }
            return packet;
        }

        public PacketPlayOutBed getBedPacket() {
            PacketPlayOutBed packet = new PacketPlayOutBed();
            try {
                Field a = packet.getClass().getDeclaredField("a");
                a.setAccessible(true);
                a.setInt(packet, entityId);
                Field b = packet.getClass().getDeclaredField("b");
                b.setAccessible(true);
                b.set(packet,
                        new BlockPosition(loc.getBlockX(), NMSCorpses_v1_12_R1.bedLocation(),
                                loc.getBlockZ()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return packet;
        }

        public PacketPlayOutEntity.PacketPlayOutRelEntityMove getMovePacket() {
            PacketPlayOutEntity.PacketPlayOutRelEntityMove packet = new PacketPlayOutEntity.PacketPlayOutRelEntityMove(
                    entityId, (byte) 0, (byte) (-60.8), (byte) 0, false);
            return packet;
        }

        public PacketPlayOutPlayerInfo getInfoPacket() {
            PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(
                    PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER);
            try {
                Field b = packet.getClass().getDeclaredField("b");
                b.setAccessible(true);
                @SuppressWarnings("unchecked")
                List<Object> data = (List<Object>) b.get(packet);
                Class<?> infoClass = Class.forName("net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo$PlayerInfoData"); //Intellijでコンパイル不可の為リフレクション
                Object infoData = infoClass.getConstructor(packet.getClass(),GameProfile.class, int.class, EnumGamemode.class,IChatBaseComponent.class)
                        .newInstance(packet, prof, 0, EnumGamemode.SURVIVAL, new ChatMessage(""));	 //インスタンス生成
                data.add(infoData);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return packet;
        }

        /**
         * Tabから消えるな
         * @return
         */
        public PacketPlayOutPlayerInfo getProductPlayerInfoPacket() {
            PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(
                    PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, ((CraftPlayer)playerData.getBukkitPlayer()).getHandle());

            return packet;
        }

        public PacketPlayOutPlayerInfo getRemoveInfoPacket() {
            PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(
                    PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER);
            try {
                Field b = packet.getClass().getDeclaredField("b");
                b.setAccessible(true);
                @SuppressWarnings("unchecked")
                List<Object> data = (List<Object>) b.get(packet);
                Class<?> infoClass = Class.forName("net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo$PlayerInfoData"); //Intellijでコンパイル不可の為リフレクション

                Object infoData = infoClass.getConstructor(packet.getClass(),GameProfile.class, int.class, EnumGamemode.class,IChatBaseComponent.class)
                        .newInstance(packet, prof, 0, EnumGamemode.SURVIVAL, new ChatMessage("")); //インスタンス生成
                data.add(infoData);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return packet;
        }

        public Location getTrueLocation() {
            return loc.clone().add(0, 0.1, 0);
        }

        public PacketPlayOutEntityEquipment getEquipmentPacket(EnumItemSlot slot, ItemStack stack){
            return new PacketPlayOutEntityEquipment(entityId, slot, stack);
        }

        @SuppressWarnings("deprecation")
        public void resendCorpseToPlayer(final Player p) {
            PacketPlayOutNamedEntitySpawn spawnPacket = getSpawnPacket();
            PacketPlayOutBed bedPacket = getBedPacket();
            PacketPlayOutEntity.PacketPlayOutRelEntityMove movePacket = getMovePacket();
            PacketPlayOutPlayerInfo infoPacket = getInfoPacket();
            final PacketPlayOutPlayerInfo addTabList = getProductPlayerInfoPacket();
            final PacketPlayOutPlayerInfo removeInfo = getRemoveInfoPacket();

            final List<Packet> packets = Lists.newArrayList(infoPacket, spawnPacket, bedPacket, movePacket); //TODO 武器パケ追加
            final List<Player> toSend = loc.getWorld().getPlayers();
            PlayerConnection conn = ((CraftPlayer) p).getHandle().playerConnection;
            p.sendBlockChange(NMSCorpses_v1_12_R1.bedLocation(loc),
                    Material.BED_BLOCK, (byte) rotation);
            for (Packet packet : packets) {
                conn.sendPacket(packet);
            }
            Bukkit.getServer().getScheduler()
                    .scheduleSyncDelayedTask(BDF.Companion.getInstance(), new Runnable() { //tabリスト戻す
                        public void run() {
                            PlayerConnection con = ((CraftPlayer) p).getHandle().playerConnection;
                            con.sendPacket(removeInfo);
                            con.sendPacket(addTabList);
                        }
                    }, 1L);
        }


        public void resendCorpseToEveryone() {
           for (Player player : Bukkit.getOnlinePlayers()) {
               resendCorpseToPlayer(player);
           }
        }

        @SuppressWarnings("deprecation")
        public void destroyCorpseFromPlayer(Player p) {
            PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(
                    entityId);
            Block b = NMSCorpses_v1_12_R1.bedLocation(loc).getBlock();
            boolean removeBed = true;
            for (NMSCorpseData cd : getAllCorpses()) {
                if (cd != this
                        && NMSCorpses_v1_12_R1.bedLocation(cd.getOrigLocation())
                        .getBlock().getLocation()
                        .equals(b.getLocation())) {
                    removeBed = false;
                    break;
                }
            }
            if (removeBed) {
                p.sendBlockChange(b.getLocation(), b.getType(), b.getData());
            }
            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
        }

        public Location getOrigLocation() {
            return loc;
        }

        @SuppressWarnings("deprecation")
        public void destroyCorpseFromEveryone() {
            PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(
                    entityId);
            Block b = NMSCorpses_v1_12_R1.bedLocation(loc).getBlock(); //モデルを取得
            boolean removeBed = true;
            for (NMSCorpseData cd : getAllCorpses()) {
                if (cd != this
                        && NMSCorpses_v1_12_R1.bedLocation(cd.getOrigLocation())
                        .getBlock().getLocation()
                        .equals(b.getLocation())) {
                    removeBed = false;
                    break;
                }
            }
            for (Player p : loc.getWorld().getPlayers()) {
                if (removeBed) { //先にベッドを消したほうが良さそう
                    p.sendBlockChange(b.getLocation(), b.getType(), b.getData());
                }
                ((CraftPlayer) p).getHandle().playerConnection
                        .sendPacket(packet);

            }
        }

        public void stopCount(boolean count) {
            stopCount = count;

        }

        public void tickPlayerLater(int ticks, Player p) {
            tickLater.put(p, Integer.valueOf(ticks));
        }

        public int getPlayerTicksLeft(Player p) {
            return tickLater.get(p);
        }

        public void stopTickingPlayer(Player p) {
            tickLater.remove(p);
        }

        public boolean isTickingPlayer(Player p) {
            return tickLater.containsKey(p);
        }

        public Set<Player> getPlayersTicked() {
            return tickLater.keySet();
        }

        public int getEntityId() {
            return entityId;
        }


        public BDFPlayer getPlayer() {
            return playerData;
        }

        public void setPlayer(BDFPlayer player) {
            this.playerData = player;
        }

        public int getSelectedSlot() {
            return slot;
        }

        public NMSCorpseData setSelectedSlot(int slot) {
            this.slot = slot;
            return this;
        }

        public boolean stopCount() {
            return stopCount;
        }

    }

    private void tick() {
        List<NMSCorpseData> toRemoveCorpses = new ArrayList<NMSCorpseData>();
        for (NMSCorpseData data : corpses) {
            List<Player> worldPlayers = data.getOrigLocation().getWorld()
                    .getPlayers();
            for (Player p : worldPlayers) {
                if (data.isTickingPlayer(p)) {
                    int ticks = data.getPlayerTicksLeft(p);
                    if (ticks > 0) { //カウントダウン
                        if (data.stopCount()){
                            data.tickPlayerLater(ticks, p);
                        } else {
                            data.tickPlayerLater(ticks - 1, p);
                        }
                        continue;
                    } else {
                        data.stopTickingPlayer(p);
                    }
                }
                if (data.mapContainsPlayer(p)) {
                    if (isInViewDistance(p, data) && !data.canSee(p)) {
                        data.resendCorpseToPlayer(p);
                        data.setCanSee(p, true);
                    } else if (!isInViewDistance(p, data) && data.canSee(p)) {
                        data.destroyCorpseFromPlayer(p);
                        data.setCanSee(p, false);
                    }
                } else if (isInViewDistance(p, data)) {
                    data.resendCorpseToPlayer(p);
                    data.setCanSee(p, true);
                } else {
                    data.setCanSee(p, false);
                }
            }
            if (data.getTicksLeft() >= 0) {
                if (data.getTicksLeft() == 0) {
                    toRemoveCorpses.add(data);
                } else {
                    data.setTicksLeft(data.getTicksLeft() - 1);
                }
            }
            List<Player> toRemove = new ArrayList<Player>(); //消すパケット受信者たち
            for (Player pl : data.getPlayersWhoSee()) {
                if (!worldPlayers.contains(pl)) { //ワールドから消えた
                    toRemove.add(pl);
                }
            }
            data.removeAllFromMap(toRemove);
            toRemove.clear();
            Set<Player> set = data.getPlayersTicked();
            for (Player pl : set) {
                if (!worldPlayers.contains(pl)) {
                    toRemove.add(pl);
                }
            }
            set.removeAll(toRemove);
            toRemove.clear();
        }
        for (NMSCorpseData data : toRemoveCorpses) {
            removeCorpse(data);
        }
    }

    /**
     * 半径45mの正方形の中にいるか
     * @param p
     * @param data
     * @return
     */
    private boolean isInViewDistance(Player p, NMSCorpseData data) {
        Location p1loc = p.getLocation();
        Location p2loc = data.getTrueLocation();
        double minX = p2loc.getX() - 45;
        double minY = p2loc.getY() - 45;
        double minZ = p2loc.getZ() - 45;
        double maxX = p2loc.getX() + 45;
        double maxY = p2loc.getY() + 45;
        double maxZ = p2loc.getZ() + 45;
        return p1loc.getX() >= minX && p1loc.getX() <= maxX
                && p1loc.getY() >= minY && p1loc.getY() <= maxY
                && p1loc.getZ() >= minZ && p1loc.getZ() <= maxZ;
    }

    public List<NMSCorpseData> getAllCorpses() {
        return corpses;
    }




    private static Location bedLocation(Location loc){
        Location l = loc.clone();
        l.setY(bedLocation());
        return l;
    }

    private static int bedLocation(){
        return 1;
    }

}
