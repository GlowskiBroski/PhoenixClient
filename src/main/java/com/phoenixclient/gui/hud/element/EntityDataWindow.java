package com.phoenixclient.gui.hud.element;

import com.phoenixclient.gui.hud.element.GuiWindow;
import com.phoenixclient.util.math.MathUtil;
import com.phoenixclient.util.math.Vector;
import com.phoenixclient.util.render.ColorManager;
import com.phoenixclient.util.render.TextBuilder;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;
import java.io.IOException;
import java.util.UUID;

import static com.phoenixclient.PhoenixClient.MC;

//TODO: Clean this class, its sloppy
public class EntityDataWindow extends GuiWindow {

    private final SettingGUI<Boolean> label;

    private String currentOwnerName;
    private Entity prevHoveredEntity;

    public EntityDataWindow(Screen screen) {
        super(screen, "EntityDataWindow", "Displays data about the hovered entity, including health, owner, name, etc.", Vector.NULL(),false);
        this.label = new SettingGUI<>(this,"Label","Show the label",true);
        addSettings(label);
    }


    @Override
    protected void drawWindow(GuiGraphics graphics, Vector mousePos) {
        setSize(new Vector(100, 12));

        Entity entity = MC.crosshairPickEntity;

        int yOff = 0;

        if (label.get()) {
            TextBuilder.start("Entity Data:", getPos().getAdded(2, 2), colorManager.getHudLabelColor()).draw(graphics);
            yOff += 12;
        }
        if (entity == null) return;

        if (entity instanceof LivingEntity living) {
            float healthPercent = living.getHealth() / living.getMaxHealth();
            Color healthColor = healthPercent <= 1 ? ColorManager.getRedGreenScaledColor(healthPercent) : new Color(255, 255, 0);
            TextBuilder
                    .start("\"" + entity.getName().getString() + "\"",getPos().getAdded(2, 2 + yOff),Color.WHITE).draw(graphics)
                    .nextAdj().text(" (" + MathUtil.roundDouble(living.getHealth(), 2) + "/" + MathUtil.roundDouble(living.getMaxHealth(),2) + ")").color(healthColor).dynamic().draw(graphics);
        } else {
            TextBuilder.start( "\"" + entity.getName().getString() + "\"", getPos().getAdded(2, 2 + yOff), Color.WHITE).dynamic().draw(graphics);
        }
        yOff += 12;
        TextBuilder.start( " " + entity.getType().toShortString(), getPos().getAdded(2, 2 + yOff), Color.WHITE).draw(graphics);
        yOff += 12;
        if (entity instanceof AbstractHorse horse) {
            double speed = MathUtil.roundDouble(horse.getAttributes().getInstance(Attributes.MOVEMENT_SPEED).getValue() * 42.16, 2);
            double jump = horse.getAttributes().getInstance(Attributes.JUMP_STRENGTH).getValue();
            double blockHeight = MathUtil.roundDouble(-0.1817584952 * Math.pow(jump, 3) + 3.689713992 * Math.pow(jump, 2) + 2.128599134 * jump - 0.343930367, 2);

            // Max Speed: 14.229m/s
            // Max Jump: 5.29m

            double speedPercent = (speed - (.1125 * 42.16)) / ((.3375 - .1125) * 42.16);
            double jumpPercent = (jump - .4) / (1 - .4);
            Color speedColor = ColorManager.getRedGreenScaledColor(speedPercent);
            Color jumpColor = ColorManager.getRedGreenScaledColor(jumpPercent);

            TextBuilder.start( " Speed: ",getPos().getAdded(2, 2 + yOff),Color.WHITE).draw(graphics).nextAdj().text(speed + " m/s").color(speedColor).dynamic().draw(graphics);
            yOff += 12;
            TextBuilder.start( " Jump: ",getPos().getAdded(2, 2 + yOff),Color.WHITE).draw(graphics).nextAdj().text(blockHeight + " m").color(jumpColor).dynamic().draw(graphics);
            yOff += 12;
        }

        UUID uuid = null;
        if (entity instanceof OwnableEntity e) uuid = e.getOwnerUUID();
        if (uuid != null) {
            if (entity != prevHoveredEntity) updateUsernameString(uuid);

            TextBuilder.start( " Owner: " + this.currentOwnerName, getPos().getAdded(2, 2 + yOff), Color.WHITE).dynamic().draw(graphics);
            yOff += 12;
        }

        this.prevHoveredEntity = entity;

        /* Debug Display Entity NBT code
        String nbt = entity.saveWithoutId(new CompoundTag()).getAsString();
        Vector pos = new Vector(2, 2 - 10);
        String[] layers = nbt.split("\\{");
        for (int i = 0; i < layers.length; i++) {
            String s = layers[i];
            if (!s.isEmpty()) s = "{" + s;
            TextBuilder.start(s,pos,Color.WHITE).defaultFont().scale(.5f).draw(graphics);
            pos.add(new Vector(0,10));
        }*/


        setSize(new Vector(100, yOff));
    }

    private void updateUsernameString(UUID uuid) {
        Thread thread = new Thread(() -> {
            try {
                CloseableHttpClient httpClient = HttpClients.createDefault();
                String url = "https://api.mojang.com/user/profile/" + uuid.toString();
                HttpGet httpGet = new HttpGet(url);
                HttpResponse response = httpClient.execute(httpGet);
                String jsonString = EntityUtils.toString(response.getEntity());
                this.currentOwnerName = (String)new JSONObject(jsonString).get("name");
            } catch (IOException | JSONException e) {
                this.currentOwnerName = uuid.toString();
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

}