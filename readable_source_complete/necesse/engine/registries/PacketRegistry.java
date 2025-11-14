/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.registries;

import java.lang.reflect.InvocationTargetException;
import java.util.NoSuchElementException;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.network.Packet;
import necesse.engine.network.packet.PacketAchievementUpdate;
import necesse.engine.network.packet.PacketActiveMountAbility;
import necesse.engine.network.packet.PacketActiveMountAbilityStopped;
import necesse.engine.network.packet.PacketActiveMountAbilityUpdate;
import necesse.engine.network.packet.PacketActiveSetBuffAbility;
import necesse.engine.network.packet.PacketActiveSetBuffAbilityStopped;
import necesse.engine.network.packet.PacketActiveSetBuffAbilityUpdate;
import necesse.engine.network.packet.PacketActiveTrinketBuffAbility;
import necesse.engine.network.packet.PacketActiveTrinketBuffAbilityStopped;
import necesse.engine.network.packet.PacketActiveTrinketBuffAbilityUpdate;
import necesse.engine.network.packet.PacketAddDeathLocation;
import necesse.engine.network.packet.PacketAddMapMarker;
import necesse.engine.network.packet.PacketAdventurePartyAdd;
import necesse.engine.network.packet.PacketAdventurePartyBuffPolicy;
import necesse.engine.network.packet.PacketAdventurePartyCompressInventory;
import necesse.engine.network.packet.PacketAdventurePartyRemove;
import necesse.engine.network.packet.PacketAdventurePartyRequestUpdate;
import necesse.engine.network.packet.PacketAdventurePartySync;
import necesse.engine.network.packet.PacketAdventurePartyUpdate;
import necesse.engine.network.packet.PacketBlinkScepter;
import necesse.engine.network.packet.PacketBounceGlyphTrap;
import necesse.engine.network.packet.PacketBuffAbility;
import necesse.engine.network.packet.PacketChangeBiome;
import necesse.engine.network.packet.PacketChangeObject;
import necesse.engine.network.packet.PacketChangeObjects;
import necesse.engine.network.packet.PacketChangeTile;
import necesse.engine.network.packet.PacketChangeWire;
import necesse.engine.network.packet.PacketChangeWorldTime;
import necesse.engine.network.packet.PacketCharacterSelectError;
import necesse.engine.network.packet.PacketCharacterStatsUpdate;
import necesse.engine.network.packet.PacketChatMessage;
import necesse.engine.network.packet.PacketClientInstalledDLC;
import necesse.engine.network.packet.PacketClientStats;
import necesse.engine.network.packet.PacketCloseContainer;
import necesse.engine.network.packet.PacketCmdAutocomplete;
import necesse.engine.network.packet.PacketConnectApproved;
import necesse.engine.network.packet.PacketConnectRequest;
import necesse.engine.network.packet.PacketContainerAction;
import necesse.engine.network.packet.PacketContainerCustomAction;
import necesse.engine.network.packet.PacketContainerEvent;
import necesse.engine.network.packet.PacketCraftAction;
import necesse.engine.network.packet.PacketCraftUseNearbyInventories;
import necesse.engine.network.packet.PacketCreativeEndRaid;
import necesse.engine.network.packet.PacketCreativeOpenTeleportToPlayer;
import necesse.engine.network.packet.PacketCreativePlayerSettings;
import necesse.engine.network.packet.PacketCreativeSetTime;
import necesse.engine.network.packet.PacketCreativeSetWorldSpawn;
import necesse.engine.network.packet.PacketCreativeStartRaid;
import necesse.engine.network.packet.PacketCreativeTeleport;
import necesse.engine.network.packet.PacketCreativeWorldSettings;
import necesse.engine.network.packet.PacketDeath;
import necesse.engine.network.packet.PacketDeepFrostAimUpdate;
import necesse.engine.network.packet.PacketDisconnect;
import necesse.engine.network.packet.PacketDownloadCharacter;
import necesse.engine.network.packet.PacketDownloadCharacterResponse;
import necesse.engine.network.packet.PacketFireArachnidWebBow;
import necesse.engine.network.packet.PacketFireDeathRipper;
import necesse.engine.network.packet.PacketFireEmeraldWand;
import necesse.engine.network.packet.PacketFireShardCannon;
import necesse.engine.network.packet.PacketFireSixShooter;
import necesse.engine.network.packet.PacketFishingStatus;
import necesse.engine.network.packet.PacketForceOfWind;
import necesse.engine.network.packet.PacketGhostBoots;
import necesse.engine.network.packet.PacketHitMob;
import necesse.engine.network.packet.PacketHitObject;
import necesse.engine.network.packet.PacketHumanWorkUpdate;
import necesse.engine.network.packet.PacketJournalChallengeCompleted;
import necesse.engine.network.packet.PacketJournalUpdated;
import necesse.engine.network.packet.PacketLevelData;
import necesse.engine.network.packet.PacketLevelEvent;
import necesse.engine.network.packet.PacketLevelEventAction;
import necesse.engine.network.packet.PacketLevelEventOver;
import necesse.engine.network.packet.PacketLevelGNDData;
import necesse.engine.network.packet.PacketLevelLayerData;
import necesse.engine.network.packet.PacketLifelineEvent;
import necesse.engine.network.packet.PacketLogicGateOutputUpdate;
import necesse.engine.network.packet.PacketLogicGateUpdate;
import necesse.engine.network.packet.PacketMapData;
import necesse.engine.network.packet.PacketMobAbility;
import necesse.engine.network.packet.PacketMobAbilityLevelEventHit;
import necesse.engine.network.packet.PacketMobAttack;
import necesse.engine.network.packet.PacketMobBuff;
import necesse.engine.network.packet.PacketMobBuffRemove;
import necesse.engine.network.packet.PacketMobChat;
import necesse.engine.network.packet.PacketMobDebugMove;
import necesse.engine.network.packet.PacketMobFollowUpdate;
import necesse.engine.network.packet.PacketMobHealth;
import necesse.engine.network.packet.PacketMobInventory;
import necesse.engine.network.packet.PacketMobInventoryUpdate;
import necesse.engine.network.packet.PacketMobJump;
import necesse.engine.network.packet.PacketMobMana;
import necesse.engine.network.packet.PacketMobMount;
import necesse.engine.network.packet.PacketMobMovement;
import necesse.engine.network.packet.PacketMobNetworkFields;
import necesse.engine.network.packet.PacketMobPathBreakDownHit;
import necesse.engine.network.packet.PacketMobResilience;
import necesse.engine.network.packet.PacketMobUseLife;
import necesse.engine.network.packet.PacketMobUseMana;
import necesse.engine.network.packet.PacketModsMismatch;
import necesse.engine.network.packet.PacketMountAbility;
import necesse.engine.network.packet.PacketMountMobJump;
import necesse.engine.network.packet.PacketNeedRequestSelf;
import necesse.engine.network.packet.PacketNetworkUpdate;
import necesse.engine.network.packet.PacketOEInventoryNameUpdate;
import necesse.engine.network.packet.PacketOEInventoryUpdate;
import necesse.engine.network.packet.PacketOEProgressUpdate;
import necesse.engine.network.packet.PacketOEUseUpdate;
import necesse.engine.network.packet.PacketOEUseUpdateFull;
import necesse.engine.network.packet.PacketOEUseUpdateFullRequest;
import necesse.engine.network.packet.PacketObjectDamage;
import necesse.engine.network.packet.PacketObjectEntity;
import necesse.engine.network.packet.PacketObjectEntityError;
import necesse.engine.network.packet.PacketObjectEntityEvent;
import necesse.engine.network.packet.PacketObjectEntityNetworkFields;
import necesse.engine.network.packet.PacketObjectInteract;
import necesse.engine.network.packet.PacketObjectSwitched;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.packet.PacketOpenJournal;
import necesse.engine.network.packet.PacketOpenPartyConfig;
import necesse.engine.network.packet.PacketOpenPvPTeams;
import necesse.engine.network.packet.PacketOpenQuests;
import necesse.engine.network.packet.PacketPerformanceResult;
import necesse.engine.network.packet.PacketPerformanceStart;
import necesse.engine.network.packet.PacketPermissionUpdate;
import necesse.engine.network.packet.PacketPickTicTacToeTile;
import necesse.engine.network.packet.PacketPickupEntityPickup;
import necesse.engine.network.packet.PacketPickupEntityTarget;
import necesse.engine.network.packet.PacketPing;
import necesse.engine.network.packet.PacketPlaceAttackHandlerUpdate;
import necesse.engine.network.packet.PacketPlaceLogicGate;
import necesse.engine.network.packet.PacketPlaceObject;
import necesse.engine.network.packet.PacketPlacePreset;
import necesse.engine.network.packet.PacketPlaceTile;
import necesse.engine.network.packet.PacketPlayObjectDamageSound;
import necesse.engine.network.packet.PacketPlayerAction;
import necesse.engine.network.packet.PacketPlayerAppearance;
import necesse.engine.network.packet.PacketPlayerAttack;
import necesse.engine.network.packet.PacketPlayerAttackHandler;
import necesse.engine.network.packet.PacketPlayerAutoOpenDoors;
import necesse.engine.network.packet.PacketPlayerBuff;
import necesse.engine.network.packet.PacketPlayerBuffs;
import necesse.engine.network.packet.PacketPlayerCollisionHit;
import necesse.engine.network.packet.PacketPlayerDie;
import necesse.engine.network.packet.PacketPlayerDropItem;
import necesse.engine.network.packet.PacketPlayerGeneral;
import necesse.engine.network.packet.PacketPlayerHotbarLocked;
import necesse.engine.network.packet.PacketPlayerHunger;
import necesse.engine.network.packet.PacketPlayerInventory;
import necesse.engine.network.packet.PacketPlayerInventoryAction;
import necesse.engine.network.packet.PacketPlayerInventoryPart;
import necesse.engine.network.packet.PacketPlayerInventorySetProxy;
import necesse.engine.network.packet.PacketPlayerInventorySlot;
import necesse.engine.network.packet.PacketPlayerItemInteract;
import necesse.engine.network.packet.PacketPlayerItemMobInteract;
import necesse.engine.network.packet.PacketPlayerJoinedTeam;
import necesse.engine.network.packet.PacketPlayerLatency;
import necesse.engine.network.packet.PacketPlayerLeftTeam;
import necesse.engine.network.packet.PacketPlayerLevelChange;
import necesse.engine.network.packet.PacketPlayerLoadedRegions;
import necesse.engine.network.packet.PacketPlayerMobInteract;
import necesse.engine.network.packet.PacketPlayerMovement;
import necesse.engine.network.packet.PacketPlayerPlaceItem;
import necesse.engine.network.packet.PacketPlayerPrivateSync;
import necesse.engine.network.packet.PacketPlayerPvP;
import necesse.engine.network.packet.PacketPlayerRespawn;
import necesse.engine.network.packet.PacketPlayerRespawnRequest;
import necesse.engine.network.packet.PacketPlayerStats;
import necesse.engine.network.packet.PacketPlayerStopAttack;
import necesse.engine.network.packet.PacketPlayerSync;
import necesse.engine.network.packet.PacketPlayerTeamInviteReceive;
import necesse.engine.network.packet.PacketPlayerTeamInviteReply;
import necesse.engine.network.packet.PacketPlayerTeamRequestReceive;
import necesse.engine.network.packet.PacketPlayerTeamRequestReply;
import necesse.engine.network.packet.PacketPlayerUseMount;
import necesse.engine.network.packet.PacketProjectileHit;
import necesse.engine.network.packet.PacketProjectilePositionUpdate;
import necesse.engine.network.packet.PacketProjectileTargetUpdate;
import necesse.engine.network.packet.PacketQuartzSetEvent;
import necesse.engine.network.packet.PacketQuest;
import necesse.engine.network.packet.PacketQuestAbandon;
import necesse.engine.network.packet.PacketQuestGiverRequest;
import necesse.engine.network.packet.PacketQuestGiverUpdate;
import necesse.engine.network.packet.PacketQuestRemove;
import necesse.engine.network.packet.PacketQuestRequest;
import necesse.engine.network.packet.PacketQuestShare;
import necesse.engine.network.packet.PacketQuestShareReceive;
import necesse.engine.network.packet.PacketQuestShareReply;
import necesse.engine.network.packet.PacketQuestTrack;
import necesse.engine.network.packet.PacketQuestUpdate;
import necesse.engine.network.packet.PacketQuests;
import necesse.engine.network.packet.PacketRedoClientPreset;
import necesse.engine.network.packet.PacketRefreshCombat;
import necesse.engine.network.packet.PacketRegionData;
import necesse.engine.network.packet.PacketRegionsData;
import necesse.engine.network.packet.PacketRemoveDeathLocation;
import necesse.engine.network.packet.PacketRemoveDeathLocations;
import necesse.engine.network.packet.PacketRemoveMob;
import necesse.engine.network.packet.PacketRemovePickupEntity;
import necesse.engine.network.packet.PacketRemoveProjectile;
import necesse.engine.network.packet.PacketRemoveSettlementData;
import necesse.engine.network.packet.PacketRequestActiveMountAbility;
import necesse.engine.network.packet.PacketRequestActiveSetBuffAbility;
import necesse.engine.network.packet.PacketRequestActiveTrinketBuffAbility;
import necesse.engine.network.packet.PacketRequestClientInstalledDLC;
import necesse.engine.network.packet.PacketRequestClientStats;
import necesse.engine.network.packet.PacketRequestLevelEvent;
import necesse.engine.network.packet.PacketRequestLogicGate;
import necesse.engine.network.packet.PacketRequestMobData;
import necesse.engine.network.packet.PacketRequestObjectChange;
import necesse.engine.network.packet.PacketRequestObjectEntity;
import necesse.engine.network.packet.PacketRequestPacket;
import necesse.engine.network.packet.PacketRequestPassword;
import necesse.engine.network.packet.PacketRequestPickupEntity;
import necesse.engine.network.packet.PacketRequestPlayerData;
import necesse.engine.network.packet.PacketRequestProjectile;
import necesse.engine.network.packet.PacketRequestQuests;
import necesse.engine.network.packet.PacketRequestRegionData;
import necesse.engine.network.packet.PacketRequestSession;
import necesse.engine.network.packet.PacketRequestSettlementData;
import necesse.engine.network.packet.PacketRequestTileChange;
import necesse.engine.network.packet.PacketRequestTravel;
import necesse.engine.network.packet.PacketSelectedCharacter;
import necesse.engine.network.packet.PacketServerLevelStats;
import necesse.engine.network.packet.PacketServerStatus;
import necesse.engine.network.packet.PacketServerStatusRequest;
import necesse.engine.network.packet.PacketServerWorldStats;
import necesse.engine.network.packet.PacketSettings;
import necesse.engine.network.packet.PacketSettlementData;
import necesse.engine.network.packet.PacketSettlementNotificationFull;
import necesse.engine.network.packet.PacketSettlementNotificationUpdate;
import necesse.engine.network.packet.PacketSettlementOpen;
import necesse.engine.network.packet.PacketSettlementRequestNotificationFull;
import necesse.engine.network.packet.PacketShopContainerUpdate;
import necesse.engine.network.packet.PacketShowAttack;
import necesse.engine.network.packet.PacketShowAttackOnlyItem;
import necesse.engine.network.packet.PacketShowDPS;
import necesse.engine.network.packet.PacketShowItemLevelInteract;
import necesse.engine.network.packet.PacketShowItemMobInteract;
import necesse.engine.network.packet.PacketShowPickupText;
import necesse.engine.network.packet.PacketSpawnCreativeItem;
import necesse.engine.network.packet.PacketSpawnFirework;
import necesse.engine.network.packet.PacketSpawnItem;
import necesse.engine.network.packet.PacketSpawnMob;
import necesse.engine.network.packet.PacketSpawnPickupEntity;
import necesse.engine.network.packet.PacketSpawnPlayer;
import necesse.engine.network.packet.PacketSpawnPlayerReceipt;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.network.packet.PacketStartCredits;
import necesse.engine.network.packet.PacketStartExpression;
import necesse.engine.network.packet.PacketStatusMessage;
import necesse.engine.network.packet.PacketSummonFocus;
import necesse.engine.network.packet.PacketSwapInventorySlots;
import necesse.engine.network.packet.PacketTileDamage;
import necesse.engine.network.packet.PacketTileDestroyed;
import necesse.engine.network.packet.PacketTotalStatsUpdate;
import necesse.engine.network.packet.PacketTrackNewQuests;
import necesse.engine.network.packet.PacketTrapTriggered;
import necesse.engine.network.packet.PacketTroughFeed;
import necesse.engine.network.packet.PacketUndoClientPreset;
import necesse.engine.network.packet.PacketUniqueFloatText;
import necesse.engine.network.packet.PacketUnloadRegion;
import necesse.engine.network.packet.PacketUnloadRegions;
import necesse.engine.network.packet.PacketUpdateSession;
import necesse.engine.network.packet.PacketUpdateTotalItemSets;
import necesse.engine.network.packet.PacketUpdateTrinketSlots;
import necesse.engine.network.packet.PacketVoidPhasingStaff;
import necesse.engine.network.packet.PacketWireHandlerUpdate;
import necesse.engine.network.packet.PacketWorldData;
import necesse.engine.network.packet.PacketWorldEvent;
import necesse.engine.platforms.Platform;
import necesse.engine.registries.ClassIDDataContainer;
import necesse.engine.registries.ClassedGameRegistry;

public class PacketRegistry
extends ClassedGameRegistry<Packet, PacketRegistryElement> {
    public static final PacketRegistry instance = new PacketRegistry();

    private PacketRegistry() {
        super("Packet", 65535);
    }

    @Override
    public void registerCore() {
        PacketRegistry.registerPacket(false, false, false, PacketServerStatus.class);
        PacketRegistry.registerPacket(true, false, false, PacketServerStatusRequest.class);
        PacketRegistry.registerPacket(true, true, false, PacketPing.class);
        PacketRegistry.registerPacket(false, false, false, PacketConnectRequest.class);
        PacketRegistry.registerPacket(PacketRequestPacket.class);
        PacketRegistry.registerPacket(PacketDisconnect.class);
        PacketRegistry.registerPacket(PacketConnectApproved.class);
        PacketRegistry.registerPacket(PacketRequestSession.class);
        PacketRegistry.registerPacket(false, false, false, PacketUpdateSession.class);
        PacketRegistry.registerPacket(PacketModsMismatch.class);
        PacketRegistry.registerPacket(PacketRequestPassword.class);
        PacketRegistry.registerPacket(PacketCharacterSelectError.class);
        PacketRegistry.registerPacket(PacketRequestClientStats.class);
        PacketRegistry.registerPacket(PacketClientStats.class);
        PacketRegistry.registerPacket(PacketTotalStatsUpdate.class);
        PacketRegistry.registerPacket(PacketPlayerStats.class);
        PacketRegistry.registerPacket(PacketCharacterStatsUpdate.class);
        PacketRegistry.registerPacket(PacketServerLevelStats.class);
        PacketRegistry.registerPacket(PacketServerWorldStats.class);
        PacketRegistry.registerPacket(PacketSettings.class);
        PacketRegistry.registerPacket(PacketPermissionUpdate.class);
        PacketRegistry.registerPacket(PacketRequestClientInstalledDLC.class);
        PacketRegistry.registerPacket(PacketLevelData.class);
        PacketRegistry.registerPacket(PacketRequestRegionData.class);
        PacketRegistry.registerPacket(PacketUnloadRegion.class);
        PacketRegistry.registerPacket(PacketUnloadRegions.class);
        PacketRegistry.registerPacket(PacketRegionData.class);
        PacketRegistry.registerPacket(PacketRegionsData.class);
        PacketRegistry.registerPacket(PacketLevelGNDData.class);
        PacketRegistry.registerPacket(PacketWorldData.class);
        PacketRegistry.registerPacket(PacketRequestTileChange.class);
        PacketRegistry.registerPacket(PacketRequestObjectChange.class);
        PacketRegistry.registerPacket(PacketTileDamage.class);
        PacketRegistry.registerPacket(PacketObjectDamage.class);
        PacketRegistry.registerPacket(PacketTileDestroyed.class);
        PacketRegistry.registerPacket(PacketHitObject.class);
        PacketRegistry.registerPacket(PacketChangeObject.class);
        PacketRegistry.registerPacket(PacketChangeObjects.class);
        PacketRegistry.registerPacket(PacketPlaceObject.class);
        PacketRegistry.registerPacket(PacketChangeTile.class);
        PacketRegistry.registerPacket(PacketPlaceTile.class);
        PacketRegistry.registerPacket(PacketPlaceLogicGate.class);
        PacketRegistry.registerPacket(PacketChangeBiome.class);
        PacketRegistry.registerPacket(PacketChangeWorldTime.class);
        PacketRegistry.registerPacket(PacketRequestLogicGate.class);
        PacketRegistry.registerPacket(PacketLogicGateUpdate.class);
        PacketRegistry.registerPacket(PacketLogicGateOutputUpdate.class);
        PacketRegistry.registerPacket(PacketLevelEvent.class);
        PacketRegistry.registerPacket(PacketLevelEventAction.class);
        PacketRegistry.registerPacket(PacketLevelEventOver.class);
        PacketRegistry.registerPacket(PacketRequestLevelEvent.class);
        PacketRegistry.registerPacket(PacketWorldEvent.class);
        PacketRegistry.registerPacket(PacketChangeWire.class);
        PacketRegistry.registerPacket(PacketObjectSwitched.class);
        PacketRegistry.registerPacket(PacketPlayObjectDamageSound.class);
        PacketRegistry.registerPacket(PacketLevelLayerData.class);
        PacketRegistry.registerPacket(PacketSettlementData.class);
        PacketRegistry.registerPacket(PacketRequestSettlementData.class);
        PacketRegistry.registerPacket(PacketRemoveSettlementData.class);
        PacketRegistry.registerPacket(PacketSettlementRequestNotificationFull.class);
        PacketRegistry.registerPacket(PacketSettlementNotificationFull.class);
        PacketRegistry.registerPacket(PacketSettlementNotificationUpdate.class);
        PacketRegistry.registerPacket(PacketMapData.class);
        PacketRegistry.registerPacket(PacketRequestPlayerData.class);
        PacketRegistry.registerPacket(PacketNeedRequestSelf.class);
        PacketRegistry.registerPacket(PacketAchievementUpdate.class);
        PacketRegistry.registerPacket(PacketPlayerLatency.class);
        PacketRegistry.registerPacket(PacketPlayerGeneral.class);
        PacketRegistry.registerPacket(PacketPlayerAppearance.class);
        PacketRegistry.registerPacket(PacketSelectedCharacter.class);
        PacketRegistry.registerPacket(PacketDownloadCharacter.class);
        PacketRegistry.registerPacket(PacketDownloadCharacterResponse.class);
        PacketRegistry.registerPacket(PacketPlayerInventory.class);
        PacketRegistry.registerPacket(PacketPlayerInventoryPart.class);
        PacketRegistry.registerPacket(PacketPlayerInventorySlot.class);
        PacketRegistry.registerPacket(PacketPlayerInventorySetProxy.class);
        PacketRegistry.registerPacket(true, PacketPlayerMovement.class);
        PacketRegistry.registerPacket(PacketSpawnPlayer.class);
        PacketRegistry.registerPacket(PacketSpawnPlayerReceipt.class);
        PacketRegistry.registerPacket(PacketPlayerSync.class);
        PacketRegistry.registerPacket(true, PacketPlayerPrivateSync.class);
        PacketRegistry.registerPacket(true, PacketPlayerLoadedRegions.class);
        PacketRegistry.registerPacket(PacketShowAttack.class);
        PacketRegistry.registerPacket(PacketShowItemLevelInteract.class);
        PacketRegistry.registerPacket(PacketShowItemMobInteract.class);
        PacketRegistry.registerPacket(PacketPlayerLevelChange.class);
        PacketRegistry.registerPacket(true, PacketChatMessage.class);
        PacketRegistry.registerPacket(PacketShowPickupText.class);
        PacketRegistry.registerPacket(PacketCmdAutocomplete.class);
        PacketRegistry.registerPacket(PacketPlayerAction.class);
        PacketRegistry.registerPacket(PacketPlayerAttack.class);
        PacketRegistry.registerPacket(PacketPlayerAttackHandler.class);
        PacketRegistry.registerPacket(PacketPlaceAttackHandlerUpdate.class);
        PacketRegistry.registerPacket(PacketPlayerStopAttack.class);
        PacketRegistry.registerPacket(PacketPlayerItemInteract.class);
        PacketRegistry.registerPacket(PacketPlayerItemMobInteract.class);
        PacketRegistry.registerPacket(PacketPlayerCollisionHit.class);
        PacketRegistry.registerPacket(PacketPlayerPlaceItem.class);
        PacketRegistry.registerPacket(PacketPlayerHunger.class);
        PacketRegistry.registerPacket(PacketContainerAction.class);
        PacketRegistry.registerPacket(PacketOpenContainer.class);
        PacketRegistry.registerPacket(PacketCloseContainer.class);
        PacketRegistry.registerPacket(PacketPlayerInventoryAction.class);
        PacketRegistry.registerPacket(PacketPlayerDropItem.class);
        PacketRegistry.registerPacket(PacketCraftAction.class);
        PacketRegistry.registerPacket(PacketContainerEvent.class);
        PacketRegistry.registerPacket(PacketStatusMessage.class);
        PacketRegistry.registerPacket(PacketPlayerRespawn.class);
        PacketRegistry.registerPacket(PacketPlayerRespawnRequest.class);
        PacketRegistry.registerPacket(PacketObjectInteract.class);
        PacketRegistry.registerPacket(PacketContainerCustomAction.class);
        PacketRegistry.registerPacket(PacketSpawnItem.class);
        PacketRegistry.registerPacket(PacketPlayerBuff.class);
        PacketRegistry.registerPacket(PacketPlayerBuffs.class);
        PacketRegistry.registerPacket(PacketRequestTravel.class);
        PacketRegistry.registerPacket(PacketPlayerDie.class);
        PacketRegistry.registerPacket(PacketPlayerPvP.class);
        PacketRegistry.registerPacket(PacketPlayerJoinedTeam.class);
        PacketRegistry.registerPacket(PacketPlayerLeftTeam.class);
        PacketRegistry.registerPacket(PacketUpdateTrinketSlots.class);
        PacketRegistry.registerPacket(PacketUpdateTotalItemSets.class);
        PacketRegistry.registerPacket(PacketShowAttackOnlyItem.class);
        PacketRegistry.registerPacket(PacketMountMobJump.class);
        PacketRegistry.registerPacket(PacketRequestQuests.class);
        PacketRegistry.registerPacket(PacketQuests.class);
        PacketRegistry.registerPacket(PacketQuest.class);
        PacketRegistry.registerPacket(PacketQuestUpdate.class);
        PacketRegistry.registerPacket(PacketQuestRequest.class);
        PacketRegistry.registerPacket(PacketQuestRemove.class);
        PacketRegistry.registerPacket(PacketQuestAbandon.class);
        PacketRegistry.registerPacket(PacketQuestTrack.class);
        PacketRegistry.registerPacket(PacketQuestShare.class);
        PacketRegistry.registerPacket(PacketQuestShareReceive.class);
        PacketRegistry.registerPacket(PacketQuestShareReply.class);
        PacketRegistry.registerPacket(PacketForceOfWind.class);
        PacketRegistry.registerPacket(PacketGhostBoots.class);
        PacketRegistry.registerPacket(PacketBlinkScepter.class);
        PacketRegistry.registerPacket(PacketPlayerTeamInviteReceive.class);
        PacketRegistry.registerPacket(PacketPlayerTeamInviteReply.class);
        PacketRegistry.registerPacket(PacketPlayerTeamRequestReceive.class);
        PacketRegistry.registerPacket(PacketPlayerTeamRequestReply.class);
        PacketRegistry.registerPacket(PacketBuffAbility.class);
        PacketRegistry.registerPacket(PacketMountAbility.class);
        PacketRegistry.registerPacket(PacketActiveSetBuffAbility.class);
        PacketRegistry.registerPacket(PacketActiveTrinketBuffAbility.class);
        PacketRegistry.registerPacket(PacketActiveMountAbility.class);
        PacketRegistry.registerPacket(PacketActiveSetBuffAbilityUpdate.class);
        PacketRegistry.registerPacket(PacketActiveTrinketBuffAbilityUpdate.class);
        PacketRegistry.registerPacket(PacketActiveMountAbilityUpdate.class);
        PacketRegistry.registerPacket(PacketActiveSetBuffAbilityStopped.class);
        PacketRegistry.registerPacket(PacketActiveTrinketBuffAbilityStopped.class);
        PacketRegistry.registerPacket(PacketActiveMountAbilityStopped.class);
        PacketRegistry.registerPacket(PacketRequestActiveSetBuffAbility.class);
        PacketRegistry.registerPacket(PacketRequestActiveTrinketBuffAbility.class);
        PacketRegistry.registerPacket(PacketRequestActiveMountAbility.class);
        PacketRegistry.registerPacket(PacketSummonFocus.class);
        PacketRegistry.registerPacket(PacketShowDPS.class);
        PacketRegistry.registerPacket(PacketCraftUseNearbyInventories.class);
        PacketRegistry.registerPacket(PacketTrackNewQuests.class);
        PacketRegistry.registerPacket(PacketPlayerAutoOpenDoors.class);
        PacketRegistry.registerPacket(PacketPlayerHotbarLocked.class);
        PacketRegistry.registerPacket(PacketPlayerUseMount.class);
        PacketRegistry.registerPacket(PacketAddDeathLocation.class);
        PacketRegistry.registerPacket(PacketRemoveDeathLocation.class);
        PacketRegistry.registerPacket(PacketRemoveDeathLocations.class);
        PacketRegistry.registerPacket(PacketAddMapMarker.class);
        PacketRegistry.registerPacket(PacketUniqueFloatText.class);
        PacketRegistry.registerPacket(PacketSwapInventorySlots.class);
        PacketRegistry.registerPacket(PacketAdventurePartySync.class);
        PacketRegistry.registerPacket(PacketAdventurePartyAdd.class);
        PacketRegistry.registerPacket(PacketAdventurePartyRemove.class);
        PacketRegistry.registerPacket(PacketAdventurePartyBuffPolicy.class);
        PacketRegistry.registerPacket(PacketAdventurePartyCompressInventory.class);
        PacketRegistry.registerPacket(PacketAdventurePartyUpdate.class);
        PacketRegistry.registerPacket(PacketAdventurePartyRequestUpdate.class);
        PacketRegistry.registerPacket(PacketFireDeathRipper.class);
        PacketRegistry.registerPacket(PacketFireArachnidWebBow.class);
        PacketRegistry.registerPacket(PacketFireSixShooter.class);
        PacketRegistry.registerPacket(PacketFireShardCannon.class);
        PacketRegistry.registerPacket(PacketFireEmeraldWand.class);
        PacketRegistry.registerPacket(PacketStartExpression.class);
        PacketRegistry.registerPacket(PacketPickTicTacToeTile.class);
        PacketRegistry.registerPacket(PacketBounceGlyphTrap.class);
        PacketRegistry.registerPacket(PacketClientInstalledDLC.class);
        PacketRegistry.registerPacket(PacketStartCredits.class);
        PacketRegistry.registerPacket(PacketVoidPhasingStaff.class);
        PacketRegistry.registerPacket(PacketRequestMobData.class);
        PacketRegistry.registerPacket(true, PacketSpawnMob.class);
        PacketRegistry.registerPacket(PacketRemoveMob.class);
        PacketRegistry.registerPacket(true, PacketMobMovement.class);
        PacketRegistry.registerPacket(PacketHitMob.class);
        PacketRegistry.registerPacket(PacketMobHealth.class);
        PacketRegistry.registerPacket(PacketMobResilience.class);
        PacketRegistry.registerPacket(PacketMobMana.class);
        PacketRegistry.registerPacket(PacketMobUseMana.class);
        PacketRegistry.registerPacket(PacketMobUseLife.class);
        PacketRegistry.registerPacket(PacketMobAttack.class);
        PacketRegistry.registerPacket(PacketMobNetworkFields.class);
        PacketRegistry.registerPacket(PacketMobAbility.class);
        PacketRegistry.registerPacket(PacketPlayerMobInteract.class);
        PacketRegistry.registerPacket(true, PacketMobMount.class);
        PacketRegistry.registerPacket(PacketMobChat.class);
        PacketRegistry.registerPacket(PacketMobBuff.class);
        PacketRegistry.registerPacket(PacketMobBuffRemove.class);
        PacketRegistry.registerPacket(PacketDeath.class);
        PacketRegistry.registerPacket(PacketMobFollowUpdate.class);
        PacketRegistry.registerPacket(PacketRefreshCombat.class);
        PacketRegistry.registerPacket(PacketTroughFeed.class);
        PacketRegistry.registerPacket(PacketMobDebugMove.class);
        PacketRegistry.registerPacket(PacketQuestGiverRequest.class);
        PacketRegistry.registerPacket(PacketQuestGiverUpdate.class);
        PacketRegistry.registerPacket(PacketLifelineEvent.class);
        PacketRegistry.registerPacket(PacketQuartzSetEvent.class);
        PacketRegistry.registerPacket(PacketMobJump.class);
        PacketRegistry.registerPacket(PacketMobInventory.class);
        PacketRegistry.registerPacket(PacketMobInventoryUpdate.class);
        PacketRegistry.registerPacket(PacketMobAbilityLevelEventHit.class);
        PacketRegistry.registerPacket(PacketHumanWorkUpdate.class);
        PacketRegistry.registerPacket(PacketMobPathBreakDownHit.class);
        PacketRegistry.registerPacket(PacketDeepFrostAimUpdate.class);
        PacketRegistry.registerPacket(PacketRequestPickupEntity.class);
        PacketRegistry.registerPacket(PacketSpawnPickupEntity.class);
        PacketRegistry.registerPacket(PacketRemovePickupEntity.class);
        PacketRegistry.registerPacket(PacketPickupEntityPickup.class);
        PacketRegistry.registerPacket(PacketPickupEntityTarget.class);
        PacketRegistry.registerPacket(PacketRequestObjectEntity.class);
        PacketRegistry.registerPacket(PacketObjectEntity.class);
        PacketRegistry.registerPacket(PacketObjectEntityNetworkFields.class);
        PacketRegistry.registerPacket(PacketObjectEntityEvent.class);
        PacketRegistry.registerPacket(PacketOEInventoryUpdate.class);
        PacketRegistry.registerPacket(PacketOEInventoryNameUpdate.class);
        PacketRegistry.registerPacket(PacketOEProgressUpdate.class);
        PacketRegistry.registerPacket(PacketSettlementOpen.class);
        PacketRegistry.registerPacket(PacketObjectEntityError.class);
        PacketRegistry.registerPacket(PacketWireHandlerUpdate.class);
        PacketRegistry.registerPacket(PacketOEUseUpdate.class);
        PacketRegistry.registerPacket(PacketOEUseUpdateFull.class);
        PacketRegistry.registerPacket(PacketOEUseUpdateFullRequest.class);
        PacketRegistry.registerPacket(PacketShopContainerUpdate.class);
        PacketRegistry.registerPacket(PacketSpawnFirework.class);
        PacketRegistry.registerPacket(PacketTrapTriggered.class);
        PacketRegistry.registerPacket(PacketSpawnProjectile.class);
        PacketRegistry.registerPacket(PacketRemoveProjectile.class);
        PacketRegistry.registerPacket(PacketProjectileHit.class);
        PacketRegistry.registerPacket(PacketProjectilePositionUpdate.class);
        PacketRegistry.registerPacket(true, PacketProjectileTargetUpdate.class);
        PacketRegistry.registerPacket(PacketRequestProjectile.class);
        PacketRegistry.registerPacket(PacketFishingStatus.class);
        PacketRegistry.registerPacket(PacketOpenPartyConfig.class);
        PacketRegistry.registerPacket(PacketOpenPvPTeams.class);
        PacketRegistry.registerPacket(PacketOpenQuests.class);
        PacketRegistry.registerPacket(PacketOpenJournal.class);
        PacketRegistry.registerPacket(PacketJournalUpdated.class);
        PacketRegistry.registerPacket(PacketJournalChallengeCompleted.class);
        PacketRegistry.registerPacket(true, true, false, PacketNetworkUpdate.class);
        PacketRegistry.registerPacket(PacketPerformanceStart.class);
        PacketRegistry.registerPacket(PacketPerformanceResult.class);
        PacketRegistry.registerPacket(PacketSpawnCreativeItem.class);
        PacketRegistry.registerPacket(PacketCreativePlayerSettings.class);
        PacketRegistry.registerPacket(PacketCreativeWorldSettings.class);
        PacketRegistry.registerPacket(PacketCreativeSetTime.class);
        PacketRegistry.registerPacket(PacketCreativeStartRaid.class);
        PacketRegistry.registerPacket(PacketCreativeEndRaid.class);
        PacketRegistry.registerPacket(PacketCreativeOpenTeleportToPlayer.class);
        PacketRegistry.registerPacket(PacketCreativeTeleport.class);
        PacketRegistry.registerPacket(PacketCreativeSetWorldSpawn.class);
        PacketRegistry.registerPacket(PacketPlacePreset.class);
        PacketRegistry.registerPacket(PacketUndoClientPreset.class);
        PacketRegistry.registerPacket(PacketRedoClientPreset.class);
        Platform.getNetworkManager().registerPacketAddSteamInvite();
    }

    @Override
    protected void onRegistryClose() {
    }

    public static String getPacketSimpleName(int type) {
        try {
            return ((PacketRegistryElement)PacketRegistry.instance.getElement((int)type)).simpleName;
        }
        catch (NoSuchElementException e) {
            return "UnknownPacket0x" + Integer.toHexString(type);
        }
    }

    public static int registerPacket(Class<? extends Packet> packetClass) {
        return PacketRegistry.registerPacket(false, packetClass);
    }

    public static int registerPacket(boolean timestamp, Class<? extends Packet> packetClass) {
        return PacketRegistry.registerPacket(false, true, timestamp, packetClass);
    }

    public static int registerPacket(boolean processInstantly, boolean onlyConnectedClients, boolean timestamp, Class<? extends Packet> packetClass) {
        if (LoadedMod.isRunningModClientSide()) {
            throw new IllegalStateException("Client/server only mods cannot register packets");
        }
        try {
            PacketRegistryElement e = new PacketRegistryElement(processInstantly, onlyConnectedClients, timestamp, packetClass);
            return instance.register(e.simpleName, e);
        }
        catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(packetClass.getSimpleName() + " does not have a constructor with byte[] parameter");
        }
    }

    public static int getPacketID(Class<? extends Packet> clazz) {
        return instance.getElementID(clazz);
    }

    public static boolean hasTimestamp(int type) {
        return ((PacketRegistryElement)PacketRegistry.instance.getElement((int)type)).hasTimestamp;
    }

    public static boolean onlyConnectedClients(int type) {
        return ((PacketRegistryElement)PacketRegistry.instance.getElement((int)type)).onlyConnectedClients;
    }

    public static boolean processInstantly(int type) {
        return ((PacketRegistryElement)PacketRegistry.instance.getElement((int)type)).processInstantly;
    }

    public static Packet createPacket(int type, byte[] data) throws NoSuchElementException, IllegalAccessException, InstantiationException, InvocationTargetException {
        return (Packet)((PacketRegistryElement)instance.getElement(type)).newInstance(new Object[]{data});
    }

    public static String getPacketClassName(int type) {
        PacketRegistryElement element = (PacketRegistryElement)instance.getElement(type);
        return element == null ? "NULL" : element.simpleName;
    }

    public static int getTotalRegistered() {
        return instance.size();
    }

    protected static class PacketRegistryElement
    extends ClassIDDataContainer<Packet> {
        public final boolean processInstantly;
        public final boolean onlyConnectedClients;
        public final boolean hasTimestamp;
        public final String simpleName;

        public PacketRegistryElement(boolean processInstantly, boolean onlyConnectedClients, boolean hasTimestamp, Class<? extends Packet> packetClass) throws NoSuchMethodException {
            super(packetClass, byte[].class);
            this.processInstantly = processInstantly;
            this.onlyConnectedClients = onlyConnectedClients;
            this.hasTimestamp = hasTimestamp;
            this.simpleName = packetClass.getSimpleName();
        }
    }
}

