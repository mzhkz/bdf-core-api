package net.bdfps.api.spigot.java.packet.original;

import net.minecraft.server.v1_12_R1.PacketPlayOutEntityStatus;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * Created by hsyhrs on 2017/07/25.
 */
public class NMSHitBoxManager_v1_12_R1 {

    public void disableF3(Player player) {
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(getPacketPlayOutEntityStatusPacket(player, false));
    }

    public void enableF3(Player player) {
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(getPacketPlayOutEntityStatusPacket(player, true));
    }

    public PacketPlayOutEntityStatus getPacketPlayOutEntityStatusPacket(Player player, boolean enable) {
        byte object = 22;
        if (enable)
            object = 23;
        PacketPlayOutEntityStatus packet = new PacketPlayOutEntityStatus(((CraftPlayer)player).getHandle(), object);
        return packet;
    }
}
