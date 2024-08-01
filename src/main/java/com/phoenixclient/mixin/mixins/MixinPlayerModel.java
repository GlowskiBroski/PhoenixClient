package com.phoenixclient.mixin.mixins;

import com.phoenixclient.PhoenixClient;
import com.phoenixclient.util.RotationManager;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PlayerModel.class)
public abstract class MixinPlayerModel<T extends LivingEntity> extends HumanoidModel<T> {

    @Shadow @Final public ModelPart leftPants;

    @Shadow @Final public ModelPart rightPants;

    @Shadow @Final public ModelPart leftSleeve;

    @Shadow @Final public ModelPart rightSleeve;

    @Shadow @Final public ModelPart jacket;

    @Shadow @Final private ModelPart cloak;

    public MixinPlayerModel(ModelPart modelPart) {
        super(modelPart);
    }

    /** TODO: Look into this more and make the head match the spoofed angles
     * @author
     * @reason
     */
    @Overwrite
    public void setupAnim(T livingEntity, float f, float g, float h, float i, float j) {
        super.setupAnim(livingEntity, f, g, h, i, j);

        RotationManager rm = PhoenixClient.getRotationManager();
        if (rm.isSpoofing()) {
            head.yRot = rm.getSpoofedYaw();
            head.xRot = rm.getSpoofedPitch();
        }


        this.leftPants.copyFrom(this.leftLeg);
        this.rightPants.copyFrom(this.rightLeg);
        this.leftSleeve.copyFrom(this.leftArm);
        this.rightSleeve.copyFrom(this.rightArm);
        this.jacket.copyFrom(this.body);
        if (((LivingEntity)livingEntity).getItemBySlot(EquipmentSlot.CHEST).isEmpty()) {
            if (((Entity)livingEntity).isCrouching()) {
                this.cloak.z = 1.4f;
                this.cloak.y = 1.85f;
            } else {
                this.cloak.z = 0.0f;
                this.cloak.y = 0.0f;
            }
        } else if (((Entity)livingEntity).isCrouching()) {
            this.cloak.z = 0.3f;
            this.cloak.y = 0.8f;
        } else {
            this.cloak.z = -1.1f;
            this.cloak.y = -0.85f;
        }
    }
}
