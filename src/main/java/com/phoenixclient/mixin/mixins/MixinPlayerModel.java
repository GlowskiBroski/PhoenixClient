package com.phoenixclient.mixin.mixins;

import com.phoenixclient.PhoenixClient;
import com.phoenixclient.event.Event;
import com.phoenixclient.event.events.PacketEvent;
import com.phoenixclient.mixin.MixinHooks;
import com.phoenixclient.util.RotationManager;
import com.phoenixclient.util.math.Angle;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.phoenixclient.PhoenixClient.MC;

@Mixin(PlayerModel.class)
public abstract class MixinPlayerModel<T extends LivingEntity> extends HumanoidModel<T> {

    @Shadow protected abstract Iterable<ModelPart> bodyParts();

    public MixinPlayerModel(ModelPart modelPart) {
        super(modelPart);
    }

    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/model/PlayerModel;leftPants:Lnet/minecraft/client/model/geom/ModelPart;"))
    protected void setupAnimationInject(T livingEntity, float f, float g, float h, float i, float j, CallbackInfo ci) {
        RotationManager rm = PhoenixClient.getRotationManager();
        if (livingEntity.equals(MC.player) && rm.isSpoofing()) {
            float pitch = (float) new Angle(rm.getSpoofedPitch() - livingEntity.getXRot(),true).getRadians();
            float yaw = (float) new Angle(rm.getSpoofedYaw() - livingEntity.getYRot(),true).getRadians();

            //for (ModelPart p : bodyParts()) p.yRot = yaw;
            //body.yRot = yaw;

            head.yRot = yaw;
            head.xRot = pitch;

        }

        if ((livingEntity instanceof Player) && MixinHooks.renderAsBaby) young = true;

    }

}
