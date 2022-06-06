package net.bdfps.api.spigot.java.packet.protocolLib;

import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import net.bdfps.api.spigot.java.packet.protocolLib.packetwrapper.*;
import net.bdfps.api.spigot.java.packet.protocolLib.packetwrapper.*;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.PacketPlayOutWindowItems;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 * Created by hsyhrs on 2017/05/23.
 */
public class ProtocolLibPacket {
    public static void sendResourcepack(Player player, String url){
        WrapperPlayServerResourcePackSend wrapperPlayServerResourcePackSend = new WrapperPlayServerResourcePackSend();
        wrapperPlayServerResourcePackSend.setUrl(url);
        wrapperPlayServerResourcePackSend.setHash("");
        wrapperPlayServerResourcePackSend.sendPacket(player);
    }

    public static void editTabList(Player player,String header, String footer) {
        WrapperPlayServerPlayerListHeaderFooter wrapperPlayServerPlayerListHeaderFooter = new WrapperPlayServerPlayerListHeaderFooter();
        wrapperPlayServerPlayerListHeaderFooter.setHeader(WrappedChatComponent.fromText(header));
        wrapperPlayServerPlayerListHeaderFooter.setFooter(WrappedChatComponent.fromText(footer));

        wrapperPlayServerPlayerListHeaderFooter.sendPacket(player);
    }

    public static void sendBreakPacket(World world, Location location,int packetid, int data, Block block) {
        WrapperPlayServerBlockBreakAnimation blockBreakAnimation = new WrapperPlayServerBlockBreakAnimation();
        BlockPosition blockPosition = new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        blockBreakAnimation.setLocation(blockPosition);
        blockBreakAnimation.setEntityID(packetid);
        blockBreakAnimation.setDestroyStage(data);
        for (Player player : Bukkit.getOnlinePlayers()) {
            blockBreakAnimation.sendPacket(player);
        }
    }

    public static void updateItem(Player player){
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        entityPlayer.playerConnection.sendPacket(new PacketPlayOutWindowItems(entityPlayer.activeContainer.windowId, entityPlayer.activeContainer.a()));
    }


    public static void sendTitle(Player player, String text){
        WrapperPlayServerTitle serverTitle = new WrapperPlayServerTitle();
        serverTitle.setTitle(WrappedChatComponent.fromJson("{\"text\": \"" + text + "\"}"));
        serverTitle.setAction(EnumWrappers.TitleAction.TITLE);

        serverTitle.sendPacket(player);
    }

    public static void sendSubTitle(Player player, String text){
        WrapperPlayServerTitle serverTitle = new WrapperPlayServerTitle();
        serverTitle.setTitle(WrappedChatComponent.fromJson("{\"text\": \"" + text + "\"}"));
        serverTitle.setAction(EnumWrappers.TitleAction.SUBTITLE);

        serverTitle.sendPacket(player);
    }

    public static void sendActionBar(Player player, String text){
        WrapperPlayServerChat wrapperPlayServerChat = new WrapperPlayServerChat();
        wrapperPlayServerChat.setMessage(WrappedChatComponent.fromJson("{\"text\": \"" + text + "\"}"));
        wrapperPlayServerChat.setPosition((byte)2);

        wrapperPlayServerChat.sendPacket(player);
    }

    public static void setTime(Player player, int in, int stay, int out){
        WrapperPlayServerTitle serverTitle = new WrapperPlayServerTitle();
        serverTitle.setFadeIn(in);
        serverTitle.setStay(stay);
        serverTitle.setFadeOut(out);
        serverTitle.setAction(EnumWrappers.TitleAction.TIMES);

        serverTitle.sendPacket(player);
    }

    public static void sendEntityDestroy(Entity entity){
        sendEntityDestroy(entity.getEntityId());
    }

    public static void sendEntityDestroy(int id){
        WrapperPlayServerEntityDestroy wrapperPlayServerEntityDestroy = new WrapperPlayServerEntityDestroy();
        wrapperPlayServerEntityDestroy.setEntityIds(new int[]{id});
        for (Player player : Bukkit.getOnlinePlayers()) {
            wrapperPlayServerEntityDestroy.sendPacket(player);
        }
    }
}
