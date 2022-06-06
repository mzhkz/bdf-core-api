package net.bdfps.api.spigot.java.packet;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 *  @author hsyhrs
 */
public class PacketUtil {

    private static Set<PacketPlayOutPosition.EnumPlayerTeleportFlags> set = Sets.newHashSet(
            PacketPlayOutPosition.EnumPlayerTeleportFlags.X,
            PacketPlayOutPosition.EnumPlayerTeleportFlags.Y,
            PacketPlayOutPosition.EnumPlayerTeleportFlags.Z,
            PacketPlayOutPosition.EnumPlayerTeleportFlags.X_ROT,
            PacketPlayOutPosition.EnumPlayerTeleportFlags.Y_ROT
    );

    public static void setHeight(Player p, float height, float width, float length){
        EntityPlayer entityPlayer = getNMSEntityPlayer(p);
        entityPlayer.width = width;
        entityPlayer.length = length;
    }

    public static void setPosition(Player p, Double x, Double y, Double z, Float pitch, Float yaw) {
        PacketPlayOutPosition packet = new PacketPlayOutPosition(0.0, 0.0, 0.0, pitch, yaw, set, 0);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
    }


    /**
     * Ex:http://example.com HTTP connection only
     * @param
     * @param ChatMsg
     * @param ClickableMsg
     * @param HoverMsg
     * @param openURL
     */
    public static void sendCustomMessage(Player player, String ChatMsg, String ClickableMsg, String HoverMsg, String openURL, String runcmd) {
        IChatBaseComponent base;
        base = new ChatMessage(ChatMsg);
        base.setChatModifier(new ChatModifier());
        if ( runcmd != null) {
			base.getChatModifier().setChatClickable(new ChatClickable(ChatClickable.EnumClickAction.RUN_COMMAND, runcmd));
		}
        if( ClickableMsg != null){
            base.getChatModifier().setChatClickable(new ChatClickable(ChatClickable.EnumClickAction.SUGGEST_COMMAND, ClickableMsg));
        }
        if(HoverMsg != null) {
            base.getChatModifier().setChatHoverable(new ChatHoverable(ChatHoverable.EnumHoverAction.SHOW_TEXT, new ChatMessage(HoverMsg)));
        }
        if(openURL != null) {
            base.getChatModifier().setChatClickable(new ChatClickable(ChatClickable.EnumClickAction.OPEN_URL, openURL));
        }
        PlayerList list = MinecraftServer.getServer().getPlayerList();
        list.getPlayer(player.getName()).sendMessage(base);
    }


    public static void setElytraFlying(Player player) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            DataWatcher watcher = new DataWatcher(((CraftPlayer) player).getHandle());
//            watcher.register(new DataWatcherObject<>(5, DataWatcherRegistry.a), true); //0x80
            watcher.register(new DataWatcherObject<>(0, DataWatcherRegistry.a), (byte) 0x80); //0x80
            watcher.register(new DataWatcherObject<>(13, DataWatcherRegistry.a), (byte) 0x08); //0x80
            //watcher.register(new DataWatcherObject<>(0,DataWatcherRegistry.a), (byte)0x10);


           if (player != p) {
               ((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityMetadata(player.getEntityId(), watcher, true));
           }

        }
    }

    public static void sendFakeBowDraw(Player player) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p != player) {
                DataWatcher watcher = new DataWatcher(((CraftPlayer) player).getHandle());
                watcher.register(new DataWatcherObject<>(6, DataWatcherRegistry.a), (byte)0x01);
                watcher.register(new DataWatcherObject<>(13, DataWatcherRegistry.a), (byte) 0x20);
                ((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityMetadata(player.getEntityId(), watcher, true));
            }
        }
    }

    public static void resetFakeBowDraw(Player player) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p != player) {
                DataWatcher watcher = new DataWatcher(((CraftPlayer) player).getHandle());
                ((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityMetadata(player.getEntityId(), watcher, true));
            }
        }
    }



	/**
	 * NMS CraftWorldを消す
	 * @param world
	 * @return
	 */
	public static CraftWorld getNMSWorld(World world){ return (CraftWorld)world; }


	/**
	 * NMS EntityPlayerを帰す
	 * @param player
	 * @return
	 */
	public static EntityPlayer getNMSEntityPlayer(Player player){ return ((CraftPlayer) player).getHandle(); }


    /**
     * NMS CraftServerを帰す
     * @param server
     * @return
     */
	public static CraftServer getNMSServer(Server server){ return (CraftServer)server;}
}
