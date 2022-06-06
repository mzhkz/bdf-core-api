package net.bdfps.api.spigot.java.packet.original;

import com.google.common.collect.Maps;
import net.bdfps.api.spigot.BDF;
import net.bdfps.api.spigot.support.BDFPlayer;
import net.bdfps.api.spigot.weapon.BDFWeapon;
import net.bdfps.api.spigot.BDF;
import net.bdfps.api.spigot.support.BDFPlayer;
import net.bdfps.api.spigot.weapon.BDFWeapon;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Created by hsyhrs on 2017/07/13.
 */
public class NMSFakeBow_v1_12_R1 {

    private List<NMSFakeBowData> fakebows;

    public NMSFakeBow_v1_12_R1() {
        fakebows = new ArrayList<NMSFakeBowData>();
        Bukkit.getServer().getScheduler()
                .scheduleSyncRepeatingTask(BDF.Companion.getInstance(), new Runnable() {
                    public void run() {
                        tick();
                    }
                }, 0L, 1L);
    }

    public void removeFakeBow(NMSFakeBowData data) {
        fakebows.remove(data);
        data.resetFakeBowToEveryOne();
    }

    public NMSFakeBowData fakebow(BDFPlayer player, int sec, boolean stopcount) {
        NMSFakeBowData data = null;
        for (int i = fakebows.size() - 1; i >=0; i--) {
            NMSFakeBowData fakeBowData = fakebows.get(i);
            if (fakeBowData.getEntityId() == player.getBukkitPlayer().getEntityId()) {
                data = fakeBowData;
            }
        }
        if (data == null)
            data = new NMSFakeBowData(player, sec);
        data.stopCount(true);
        return data;
    }

    public class NMSFakeBowData {
        private Map<Player, Boolean> canSee;
        private Map<Player, Integer> tickLater;
        private BDFPlayer player;
        private int entityId;
        private int ticksLeft;
        private boolean stopCount;

        public NMSFakeBowData(BDFPlayer dsukePlayer, int sec) {
            canSee = Maps.newHashMap();
            tickLater = Maps.newHashMap();
            player = dsukePlayer;
            entityId = dsukePlayer.getBukkitPlayer().getEntityId();
            ticksLeft = sec * 20;
        }

        public boolean stopCount() {
            return stopCount;
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

        public void stopTickingPlayer(Player p) {
            tickLater.remove(p);
        }

        public boolean isTickingPlayer(Player p) {
            return tickLater.containsKey(p);
        }

        public int getPlayerTicksLeft(Player p) {
            return tickLater.get(p);
        }

        public void stopCount(boolean count) {
            stopCount = count;
        }
        public Set<Player> getPlayersTicked() {
            return tickLater.keySet();
        }

        public int getEntityId() {
            return entityId;
        }

        public BDFPlayer getPlayer() {
            return player;
        }

        public PacketPlayOutEntityMetadata getEntityMetaPacket() {
            DataWatcher watcher = new DataWatcher(((CraftPlayer) player.getBukkitPlayer()).getHandle());
            watcher.register(new DataWatcherObject<>(6, DataWatcherRegistry.a), (byte)0x01);
            watcher.register(new DataWatcherObject<>(13, DataWatcherRegistry.a), (byte) 0x20);
            return new PacketPlayOutEntityMetadata(entityId, watcher, true);
        }

        public void tickPlayerLater(int ticks, Player p) {
            tickLater.put(p, Integer.valueOf(ticks));
        }

        public void resendFakeBowToEveryone() {
           for (Player player : Bukkit.getOnlinePlayers()) {
               resendFakeBowToPlayer(player);
           }
        }

        public void resendFakeBowToPlayer(Player p) {
            PacketPlayOutEntityMetadata metaPacket = getEntityMetaPacket();
            if (p.getEntityId() != entityId) {
                PlayerConnection conn = ((CraftPlayer) p).getHandle().playerConnection;
                conn.sendPacket(metaPacket);
            }
        }

        public ItemStack convertBukkitToMc(org.bukkit.inventory.ItemStack stack){
            if(stack == null){
                return new ItemStack(Item.getById(0));
            }
            ItemStack temp = CraftItemStack.asNMSCopy(stack.clone());
            return temp;
        }

        public PacketPlayOutEntityEquipment getEquipmentPacket(EnumItemSlot slot, ItemStack stack){
            return new PacketPlayOutEntityEquipment(entityId, slot, stack);
        }


        public void resetFakeBowToEveryOne() {
            for (Player player : Bukkit.getOnlinePlayers()) {
                resetFakeBowToPlayer(player);
            }
        }
        public void resetFakeBowToPlayer(Player p) {
            BDFWeapon weapon = getPlayer().getReadyWeapon();
            if (weapon == null)
                return;
            final PacketPlayOutEntityEquipment mainHandPacket = getEquipmentPacket(EnumItemSlot.MAINHAND,convertBukkitToMc(weapon.getItemStack().clone()));
            final PacketPlayOutEntityEquipment offHandPacket = getEquipmentPacket(EnumItemSlot.OFFHAND,new ItemStack(Item.getById(0)));
            if (p.getEntityId() != entityId) {
                PlayerConnection conn = ((CraftPlayer) p).getHandle().playerConnection;
                conn.sendPacket(mainHandPacket);
                conn.sendPacket(offHandPacket);
            }
        }
    }



    private void tick() {
        List<NMSFakeBowData> toRemovefakeBow = new ArrayList<NMSFakeBowData>();
        for (NMSFakeBowData data : fakebows) {
            List<Player> worldPlayers = data.getPlayer().getLocation().getWorld()
                    .getPlayers();
            for (Player p : worldPlayers) {
                if (data.isTickingPlayer(p)) {
                    int ticks = data.getPlayerTicksLeft(p);
                    if (ticks > 0 && !data.stopCount()) { //カウントダウン
                        data.tickPlayerLater(ticks - 1, p);
                        continue;
                    } else {
                        data.stopTickingPlayer(p);
                    }
                }
                if (data.mapContainsPlayer(p)) {
                    if (isInViewDistance(p, data) && !data.canSee(p)) {
                        data.resendFakeBowToPlayer(p);
                        data.setCanSee(p, true);
                    } else if (!isInViewDistance(p, data) && data.canSee(p)) {
                        data.resetFakeBowToPlayer(p);
                        data.setCanSee(p, false);
                    }
                } else if (isInViewDistance(p, data)) {
                    data.resendFakeBowToPlayer(p);
                    data.setCanSee(p, true);
                } else {
                    data.setCanSee(p, false);
                }
            }
            if (data.getTicksLeft() >= 0) {
                if (data.getTicksLeft() == 0) {
                    toRemovefakeBow.add(data);
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
        for (NMSFakeBowData data : toRemovefakeBow) {
            removeFakeBow(data);
        }
    }

    public List<NMSFakeBowData> getAllFakebow() {
        return fakebows;
    }

    /**
     * 半径45mの正方形の中にいるか
     * @param p
     * @param data
     * @return
     */
    private boolean isInViewDistance(Player p, NMSFakeBowData data) {
        Location p1loc = p.getLocation();
        Location p2loc = data.getPlayer().getLocation();
        double minX = p2loc.getX() - 50;
        double minY = p2loc.getY() - 30;
        double minZ = p2loc.getZ() - 50;
        double maxX = p2loc.getX() + 50;
        double maxY = p2loc.getY() + 30;
        double maxZ = p2loc.getZ() + 50;
        return p1loc.getX() >= minX && p1loc.getX() <= maxX
                && p1loc.getY() >= minY && p1loc.getY() <= maxY
                && p1loc.getZ() >= minZ && p1loc.getZ() <= maxZ;
    }
}
