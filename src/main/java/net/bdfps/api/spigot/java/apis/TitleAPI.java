package net.bdfps.api.spigot.java.apis;

import net.bdfps.api.spigot.java.packet.protocolLib.ProtocolLibPacket;
import org.bukkit.entity.Player;

public class TitleAPI {

	public static void sendTitle(Player player, int in, int stay, int out, String title, String sub){
		ProtocolLibPacket.sendTitle(player, title);
		ProtocolLibPacket.sendSubTitle(player, sub);
		ProtocolLibPacket.setTime(player, in, stay, out);
	}
}
