/*
 * @author mozow470
 * ActionBar
 */
package net.bdfps.api.spigot.java.apis;

import net.bdfps.api.spigot.BDFConfig;
import net.bdfps.api.spigot.java.packet.protocolLib.ProtocolLibPacket;
import net.bdfps.api.spigot.managers.BDFManager;
import org.bukkit.entity.Player;

public class ActionBarAPI {


	public static void sendActionBar(Player send, String message){
		ProtocolLibPacket.sendActionBar(send, message);
	}

	private void test() {
	}

}
