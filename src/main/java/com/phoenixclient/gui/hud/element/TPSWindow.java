package com.phoenixclient.gui.hud.element;

import com.phoenixclient.event.Event;
import com.phoenixclient.gui.hud.element.GuiWindow;
import com.phoenixclient.util.actions.StopWatch;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.DrawUtil;
import com.phoenixclient.event.events.PacketEvent;
import com.phoenixclient.util.render.TextBuilder;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.protocol.game.*;

import java.awt.*;

public class TPSWindow extends GuiWindow {

    private final SettingGUI<Boolean> label;
    private final StopWatch tpsWatch = new StopWatch();
    private int ticks = 0;
    private int tps;

    public TPSWindow(Screen screen) {
        super(screen, "TPSWindow","Displays the servers ticks per second (20 max)", Vector.NULL(),true);
        this.label = new SettingGUI<>(this, "Label","Show the label",true);
        addSettings(label);
        addEventSubscriber(Event.EVENT_PACKET,this::onPacket);
    }

    @Override
    protected void drawWindow(GuiGraphics graphics, Vector mousePos) {
        String label = (this.label.get() ? "TPS " : "");
        String text = String.valueOf(tps);

        setSize(new Vector((int) DrawUtil.getFontTextWidth(label + text) + 6,12));

        TextBuilder.start(label,getPos().getAdded(new Vector(2,2)),colorManager.getHudLabelColor()).draw(graphics).nextAdj().text(text).color(Color.WHITE).dynamic().draw(graphics);
    }

    //TODO: This is like actually stupid how this functions, make it better
    private void onPacket(PacketEvent event) {
        tpsWatch.run(20 * 1000, () -> {
            tps = ticks;
            ticks = 0;
        });
        if (event.getPacket() instanceof ClientboundSetTimePacket) {
            ticks++;
        }
    }

}
