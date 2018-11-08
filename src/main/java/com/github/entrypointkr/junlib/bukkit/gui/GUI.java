package com.github.entrypointkr.junlib.bukkit.gui;

import com.github.entrypointkr.junlib.JunLibrary;
import com.github.entrypointkr.junlib.bukkit.event.EventListener;
import com.github.entrypointkr.junlib.bukkit.event.Events;
import com.github.entrypointkr.junlib.bukkit.gui.handler.GUIHandler;
import com.github.entrypointkr.junlib.bukkit.inventory.InventoryFactory;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

/**
 * Created by JunHyeong on 2018-10-28
 */
public final class GUI {
    private final HumanEntity owner;
    private final InventoryFactory factory;
    private final GUIHandler<InventoryEvent> handler;
    private final Plugin plugin;

    public GUI(HumanEntity owner, InventoryFactory factory, GUIHandler<InventoryEvent> handler, Plugin plugin) {
        this.owner = owner;
        this.factory = factory;
        this.handler = handler;
        this.plugin = plugin;
    }

    public GUI(HumanEntity owner, InventoryFactory factory, GUIHandler<InventoryEvent> handler) {
        this(owner, factory, handler, JunLibrary.getPlugin());
    }

    public GUI(HumanEntity owner, GUIComponent component, Plugin plugin) {
        this(owner, component, component, plugin);
    }

    public GUI(HumanEntity owner, GUIComponent component) {
        this(owner, component, JunLibrary.getPlugin());
    }

    public void open() {
        Bukkit.getScheduler().runTask(plugin, () -> {
            Inventory created = factory.create(owner);
            owner.openInventory(created);
            Events.registerListener(EventPriority.MONITOR, new Notifier(handler));
        });
    }

    class Notifier implements EventListener<Event> {
        private final GUIHandler<InventoryEvent> handler;

        Notifier(GUIHandler<InventoryEvent> handler) {
            this.handler = handler;
        }

        @Override
        public void onEvent(Event event) {
            if (event instanceof InventoryEvent) {
                InventoryEvent e = ((InventoryEvent) event);
                if (owner.equals(e.getView().getPlayer())) {
                    handler.onEvent(GUI.this, e);
                    if (e instanceof InventoryCloseEvent) {
                        Events.removeListener(this);
                    }
                }
            }
        }
    }
}
