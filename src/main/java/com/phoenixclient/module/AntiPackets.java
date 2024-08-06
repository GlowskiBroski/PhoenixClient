package com.phoenixclient.module;

import com.phoenixclient.event.Event;
import com.phoenixclient.event.EventAction;
import com.phoenixclient.event.events.PacketEvent;
import com.phoenixclient.util.setting.Setting;
import com.phoenixclient.util.setting.SettingGUI;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
//TODO: You NEED to make a modified GUI for this because the list is HUGE
public class AntiPackets extends Module {

    private final Class<?>[] clientBoundPacketList = new Class[]{
            ClientboundAddEntityPacket.class,
            ClientboundAddExperienceOrbPacket.class,
            ClientboundAnimatePacket.class,
            ClientboundAwardStatsPacket.class,
            ClientboundBlockChangedAckPacket.class,
            ClientboundBlockDestructionPacket.class,
            ClientboundBlockEntityDataPacket.class,
            ClientboundBlockEventPacket.class,
            ClientboundBlockUpdatePacket.class,
            ClientboundBossEventPacket.class,
            ClientboundBundleDelimiterPacket.class,
            ClientboundBundlePacket.class,
            ClientboundChangeDifficultyPacket.class,
            ClientboundChunkBatchFinishedPacket.class,
            ClientboundChunkBatchStartPacket.class,
            ClientboundChunksBiomesPacket.class,
            ClientboundClearTitlesPacket.class,
            ClientboundCommandsPacket.class,
            ClientboundCommandSuggestionsPacket.class,
            ClientboundContainerClosePacket.class,
            ClientboundContainerSetContentPacket.class,
            ClientboundContainerSetDataPacket.class,
            ClientboundContainerSetSlotPacket.class,
            ClientboundCooldownPacket.class,
            ClientboundCustomChatCompletionsPacket.class,
            ClientboundDamageEventPacket.class,
            ClientboundDebugSamplePacket.class,
            ClientboundDeleteChatPacket.class,
            ClientboundDisguisedChatPacket.class,
            ClientboundEntityEventPacket.class,
            ClientboundExplodePacket.class,
            ClientboundForgetLevelChunkPacket.class,
            ClientboundGameEventPacket.class,
            ClientboundHorseScreenOpenPacket.class,
            ClientboundHurtAnimationPacket.class,
            ClientboundInitializeBorderPacket.class,
            ClientboundLevelChunkPacketData.class,
            ClientboundLevelChunkWithLightPacket.class,
            ClientboundLevelEventPacket.class,
            ClientboundLevelParticlesPacket.class,
            ClientboundLightUpdatePacket.class,
            ClientboundLightUpdatePacketData.class,
            ClientboundLoginPacket.class,
            ClientboundMapItemDataPacket.class,
            ClientboundMerchantOffersPacket.class,
            ClientboundMoveEntityPacket.class,
            ClientboundMoveVehiclePacket.class,
            ClientboundOpenBookPacket.class,
            ClientboundOpenScreenPacket.class,
            ClientboundOpenSignEditorPacket.class,
            ClientboundPlaceGhostRecipePacket.class,
            ClientboundPlayerAbilitiesPacket.class,
            ClientboundPlayerChatPacket.class,
            ClientboundPlayerCombatEndPacket.class,
            ClientboundPlayerCombatEnterPacket.class,
            ClientboundPlayerCombatKillPacket.class,
            ClientboundPlayerInfoRemovePacket.class,
            ClientboundPlayerInfoUpdatePacket.class,
            ClientboundPlayerLookAtPacket.class,
            ClientboundPlayerPositionPacket.class,
            ClientboundProjectilePowerPacket.class,
            ClientboundRecipePacket.class,
            ClientboundRemoveEntitiesPacket.class,
            ClientboundRemoveMobEffectPacket.class,
            ClientboundResetScorePacket.class,
            ClientboundRespawnPacket.class,
            ClientboundRotateHeadPacket.class,
            ClientboundSectionBlocksUpdatePacket.class,
            ClientboundSelectAdvancementsTabPacket.class,
            ClientboundServerDataPacket.class,
            ClientboundSetActionBarTextPacket.class,
            ClientboundSetBorderCenterPacket.class,
            ClientboundSetBorderLerpSizePacket.class,
            ClientboundSetBorderSizePacket.class,
            ClientboundSetBorderWarningDelayPacket.class,
            ClientboundSetBorderWarningDistancePacket.class,
            ClientboundSetCameraPacket.class,
            ClientboundSetCarriedItemPacket.class,
            ClientboundSetChunkCacheCenterPacket.class,
            ClientboundSetChunkCacheRadiusPacket.class,
            ClientboundSetDefaultSpawnPositionPacket.class,
            ClientboundSetDisplayObjectivePacket.class,
            ClientboundSetEntityDataPacket.class,
            ClientboundSetEntityLinkPacket.class,
            ClientboundSetEntityMotionPacket.class,
            ClientboundSetEquipmentPacket.class,
            ClientboundSetExperiencePacket.class,
            ClientboundSetHealthPacket.class,
            ClientboundSetObjectivePacket.class,
            ClientboundSetPassengersPacket.class,
            ClientboundSetPlayerTeamPacket.class,
            ClientboundSetScorePacket.class,
            ClientboundSetSimulationDistancePacket.class,
            ClientboundSetSubtitleTextPacket.class,
            ClientboundSetTimePacket.class,
            ClientboundSetTitlesAnimationPacket.class,
            ClientboundSetTitleTextPacket.class,
            ClientboundSoundEntityPacket.class,
            ClientboundSoundPacket.class,
            ClientboundStartConfigurationPacket.class,
            ClientboundStopSoundPacket.class,
            ClientboundSystemChatPacket.class,
            ClientboundTabListPacket.class,
            ClientboundTagQueryPacket.class,
            ClientboundTakeItemEntityPacket.class,
            ClientboundTeleportEntityPacket.class,
            ClientboundTickingStatePacket.class,
            ClientboundTickingStepPacket.class,
            ClientboundUpdateAdvancementsPacket.class,
            ClientboundUpdateAttributesPacket.class,
            ClientboundUpdateMobEffectPacket.class,
            ClientboundUpdateRecipesPacket.class
    };

    private final Class<?>[] serverBoundPacketList = new Class[]{
            ServerboundAcceptTeleportationPacket.class,
            ServerboundBlockEntityTagQueryPacket.class,
            ServerboundChangeDifficultyPacket.class,
            ServerboundChatAckPacket.class,
            ServerboundChatCommandPacket.class,
            ServerboundChatCommandSignedPacket.class,
            ServerboundChatPacket.class,
            ServerboundChatSessionUpdatePacket.class,
            ServerboundChunkBatchReceivedPacket.class,
            ServerboundClientCommandPacket.class,
            ServerboundCommandSuggestionPacket.class,
            ServerboundConfigurationAcknowledgedPacket.class,
            ServerboundContainerButtonClickPacket.class,
            ServerboundContainerClickPacket.class,
            ServerboundContainerClosePacket.class,
            ServerboundContainerSlotStateChangedPacket.class,
            ServerboundDebugSampleSubscriptionPacket.class,
            ServerboundEditBookPacket.class,
            ServerboundEntityTagQueryPacket.class,
            ServerboundInteractPacket.class,
            ServerboundJigsawGeneratePacket.class,
            ServerboundLockDifficultyPacket.class,
            ServerboundMovePlayerPacket.class,
            ServerboundMoveVehiclePacket.class,
            ServerboundPaddleBoatPacket.class,
            ServerboundPickItemPacket.class,
            ServerboundPlaceRecipePacket.class,
            ServerboundPlayerAbilitiesPacket.class,
            ServerboundPlayerActionPacket.class,
            ServerboundPlayerCommandPacket.class,
            ServerboundPlayerInputPacket.class,
            ServerboundRecipeBookChangeSettingsPacket.class,
            ServerboundRecipeBookSeenRecipePacket.class,
            ServerboundRenameItemPacket.class,
            ServerboundSeenAdvancementsPacket.class,
            ServerboundSelectTradePacket.class,
            ServerboundSetBeaconPacket.class,
            ServerboundSetCarriedItemPacket.class,
            ServerboundSetCommandBlockPacket.class,
            ServerboundSetCommandMinecartPacket.class,
            ServerboundSetCreativeModeSlotPacket.class,
            ServerboundSetJigsawBlockPacket.class,
            ServerboundSetStructureBlockPacket.class,
            ServerboundSignUpdatePacket.class,
            ServerboundSwingPacket.class,
            ServerboundTeleportToEntityPacket.class,
            ServerboundUseItemOnPacket.class,
            ServerboundUseItemPacket.class,
    };

    private final SettingGUI<String> viewSettings = new SettingGUI<>(
            this,
            "View",
            "Shows the types of packets you can cancel",
            "Serverbound").setModeData("Serverbound","Clientbound");

    public AntiPackets() {
        super("AntiPackets", "Allows certain packets to be disabled", Category.SERVER, false, -1);
        addSettings(viewSettings);
        for (Class<?> c : clientBoundPacketList) {
            SettingGUI<Boolean> setting = new SettingGUI<>(
                    this,
                    c.getSimpleName().replace("Clientbound",""),
                    "",
                    false).setDependency(viewSettings,"Clientbound");
            addSettings(setting);
        }

        for (Class<?> c : serverBoundPacketList) {
            SettingGUI<Boolean> setting = new SettingGUI<>(
                    this,
                    c.getSimpleName().replace("Serverbound",""),
                    "",
                    false).setDependency(viewSettings,"Serverbound");
            addSettings(setting);
        }

        addEventSubscriber(Event.EVENT_PACKET,this::onPacket);
    }

    public void onPacket(PacketEvent event) {
        Packet<?> packet = event.getPacket();

        for (int i = 0; i < clientBoundPacketList.length; i++) {
            if (((SettingGUI<Boolean>)getSettings().get(i + 1)).get() && packet.getClass().equals(clientBoundPacketList[i]))
                event.setCancelled(true);
        }

        for (int i = clientBoundPacketList.length; i < clientBoundPacketList.length + serverBoundPacketList.length; i++) {
            if (((SettingGUI<Boolean>)getSettings().get(i + 1)).get() && packet.getClass().equals(serverBoundPacketList[i - clientBoundPacketList.length]))
                event.setCancelled(true);
        }
    }

}
