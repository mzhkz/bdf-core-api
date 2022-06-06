package net.bdfps.api.spigot.java.apis;

import net.bdfps.api.spigot.BDF;
import net.bdfps.api.spigot.BDF;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

/**
 * @author chasechocolate
 * https://bukkit.org/threads/class-anvilgui-use-the-anvil-gui-to-retrieve-strings.211849/
 */
public class AnvilGUI {

    private class AnvilContainer extends ContainerAnvil {
        public AnvilContainer(EntityHuman entity) {
        	super(entity.inventory, entity.world,new BlockPosition(0, 0, 0), entity);
        }
    }

    public enum AnvilSlot {
        INPUT_LEFT(0),
        INPUT_RIGHT(1),
        OUTPUT(2);

        private int slot;

        AnvilSlot(int slot) {
            this.slot = slot;
        }

        public int getSlot() {
            return slot;
        }

        public static AnvilSlot bySlot(int slot) {
            for (AnvilSlot anvilSlot : values()) {
                if (anvilSlot.getSlot() == slot) {
                    return anvilSlot;
                }
            }

            return null;
        }
    }

    public class AnvilClickEvent {
    	private Inventory inventory;
        private AnvilSlot slot;
        private ItemStack currentItem;

        private boolean close = true;
        private boolean destroy = true;

        public AnvilClickEvent(AnvilSlot slot, Inventory inventory,ItemStack currentItem) {
            this.slot = slot;
            this.inventory = inventory;
            this.currentItem=currentItem;
        }

        public ItemStack getCurrentItem(){
        	return currentItem;
        }

        public AnvilSlot getSlot() {
            return slot;
        }

        public Inventory getInventory(){
        	return inventory;
        }

        public boolean getWillClose() {
            return close;
        }

        public void setWillClose(boolean close) {
            this.close = close;
        }

        public boolean getWillDestroy() {
            return destroy;
        }

        public void setWillDestroy(boolean destroy) {
            this.destroy = destroy;
        }

        public void setCancelled(boolean arg){
        	setWillClose(!arg);
        	setWillDestroy(!arg);
        }
    }

    public interface AnvilClickEventHandler {
        void onAnvilClick(AnvilClickEvent event);
    }

    private Player player;

    private AnvilClickEventHandler handler;

    private HashMap<AnvilSlot, ItemStack> items = new HashMap<AnvilSlot, ItemStack>();

    private Inventory inv;

    private Listener listener;

    public AnvilGUI(Player player, final AnvilClickEventHandler handler) {
        this.player = player;
        this.handler = handler;

        this.listener = new Listener() {
            @EventHandler
            public void onInventoryClick(InventoryClickEvent event) {
                if (event.getWhoClicked() instanceof Player) {
                    if (event.getInventory().equals(inv)) {
                        event.setCancelled(true);

                        int slot = event.getRawSlot();
                        AnvilClickEvent clickEvent = new AnvilClickEvent(AnvilSlot.bySlot(slot),event.getInventory(),event.getCurrentItem());
                        handler.onAnvilClick(clickEvent);

                        if (clickEvent.getWillClose()) {
                            event.getWhoClicked().closeInventory();
                        }

                        if (clickEvent.getWillDestroy()) {
                            destroy();
                        }
                    }
                }
            }

            @EventHandler
            public void onInventoryClose(InventoryCloseEvent event) {
                if (event.getPlayer() instanceof Player) {
                    Inventory inv = event.getInventory();

                    if (inv.equals(AnvilGUI.this.inv)) {
                        inv.clear();
                        destroy();
                    }
                }
            }

            @EventHandler
            public void onPlayerQuit(PlayerQuitEvent event) {
                if (event.getPlayer().equals(getPlayer())) {
                    destroy();
                }
            }
        };

        Bukkit.getPluginManager().registerEvents(listener, BDF.Companion.getInstance());
    }

    public Player getPlayer() {
        return player;
    }

    public void setSlot(AnvilSlot slot, ItemStack item) {
        items.put(slot, item);
    }

    public void open() {
        EntityPlayer p = ((CraftPlayer) player).getHandle();

        AnvilContainer container = new AnvilContainer(p);

        //Set the items to the items from the inventory given
        inv = container.getBukkitView().getTopInventory();

        for (AnvilSlot slot : items.keySet()) {
            inv.setItem(slot.getSlot(), items.get(slot));
        }

        //Counter stuff that the game uses to keep track of inventories
        int c = p.nextContainerCounter();

        //Send the packet
        p.playerConnection.sendPacket(new PacketPlayOutOpenWindow(c, "minecraft:anvil", new ChatMessage("Repairing"), 0));

        //Set their active container to the container
        p.activeContainer = container;

        //Set their active container window id to that counter stuff
        p.activeContainer.windowId = c;

        //Add the slot listener
        p.activeContainer.addSlotListener(p);
    }

    public void destroy() {
        player = null;
        handler = null;
        items = null;

        HandlerList.unregisterAll(listener);

        listener = null;
    }
}
