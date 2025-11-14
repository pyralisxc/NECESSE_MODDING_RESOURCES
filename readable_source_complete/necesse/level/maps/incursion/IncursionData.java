/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.incursion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Stream;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.IDData;
import necesse.engine.registries.IncursionDataRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.LoadDataException;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.WorldEntity;
import necesse.entity.mobs.Mob;
import necesse.entity.objectEntity.FallenAltarObjectEntity;
import necesse.gfx.fairType.FairType;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.presets.containerComponent.object.FallenAltarContainerForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.inventory.container.object.fallenAltar.FallenAltarContainer;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.incursion.AltarData;
import necesse.level.maps.incursion.IncursionBiome;
import necesse.level.maps.incursion.IncursionDataModifierManager;
import necesse.level.maps.incursion.IncursionRewardGetter;
import necesse.level.maps.incursion.UniqueIncursionModifier;

public abstract class IncursionData {
    public static int MINIMUM_TIER = 1;
    public static int ITEM_TIER_UPGRADE_CAP = 10;
    public static int TABLET_TIER_UPGRADE_CAP = 10;
    public static int MODIFIERS_TO_ADD_FROM_TIER_1 = 1;
    public static int MODIFIERS_TO_ADD_FROM_TIER_4 = 2;
    public static int MODIFIERS_TO_ADD_FROM_TIER_8 = 3;
    public static int DROPS_TIER_CAP = 10;
    public static int UPGRADESHARDS_MIN_DROP = 10;
    public static int UPGRADESHARDS_MAX_DROP = 25;
    public static int ALCHEMYSHARDS_MIN_DROP = 10;
    public static int ALCHEMYSHARDS_MAX_DROP = 20;
    public static int ALTAR_DUST_MIN_DROP = 30;
    public static int ALTAR_DUST_MAX_DROP = 45;
    public final IDData idData = new IDData();
    private int uniqueID;
    public HashSet<Integer> currentIncursionPerkIDs = new HashSet();
    public HashSet<Integer> nextIncursionPerkIDs = new HashSet();
    public final IncursionDataModifierManager nextIncursionModifiers = new IncursionDataModifierManager(this);

    public final String getStringID() {
        return this.idData.getStringID();
    }

    public final int getID() {
        return this.idData.getID();
    }

    public IncursionData() {
        IncursionDataRegistry.applyIncursionDataIDData(this);
        while (this.uniqueID == 0 || this.uniqueID == -1) {
            this.uniqueID = GameRandom.getNewUniqueID();
        }
        this.nextIncursionModifiers.updateModifiers();
    }

    public void addSaveData(SaveData save) {
        save.addUnsafeString("stringID", this.getStringID());
        save.addInt("uniqueID", this.uniqueID);
        if (!this.currentIncursionPerkIDs.isEmpty()) {
            save.addIntCollection("currentIncursionPerkIDs", this.currentIncursionPerkIDs);
        }
        if (!this.nextIncursionPerkIDs.isEmpty()) {
            save.addIntCollection("nextIncursionPerkIDs", this.nextIncursionPerkIDs);
        }
    }

    public void applyLoadData(LoadData save) {
        this.uniqueID = save.getInt("uniqueID", -1, false);
        if (this.uniqueID == -1) {
            throw new LoadDataException("Could not load " + this.getStringID() + " incursionData uniqueID");
        }
        this.currentIncursionPerkIDs = new HashSet<Integer>(save.getIntCollection("currentIncursionPerkIDs", new ArrayList<Integer>(), false));
        this.nextIncursionPerkIDs = new HashSet<Integer>(save.getIntCollection("nextIncursionPerkIDs", new ArrayList<Integer>(), false));
        this.nextIncursionModifiers.updateModifiers();
    }

    public void writePacket(PacketWriter writer) {
        writer.putNextInt(this.uniqueID);
        writer.putNextCollection(this.currentIncursionPerkIDs, writer::putNextShortUnsigned);
        writer.putNextCollection(this.nextIncursionPerkIDs, writer::putNextShortUnsigned);
    }

    public void applyPacket(PacketReader reader) {
        this.uniqueID = reader.getNextInt();
        this.currentIncursionPerkIDs = reader.getNextCollection(HashSet::new, reader::getNextShortUnsigned);
        this.nextIncursionPerkIDs = reader.getNextCollection(HashSet::new, reader::getNextShortUnsigned);
        this.nextIncursionModifiers.updateModifiers();
    }

    public void init() {
    }

    public int getUniqueID() {
        return this.uniqueID;
    }

    public abstract GameMessage getDisplayName();

    public abstract IncursionBiome getIncursionBiome();

    public abstract GameMessage getIncursionMissionTypeName();

    public abstract GameSprite getTabletSprite();

    public abstract void setTabletTier(int var1);

    public abstract int getTabletTier();

    public abstract boolean isSameIncursion(IncursionData var1);

    public abstract Collection<FairType> getObjectives(IncursionData var1, FontOptions var2);

    public abstract void setUpDetails(FallenAltarContainer var1, FallenAltarContainerForm var2, FormContentBox var3, boolean var4);

    public abstract GameTooltips getOpenButtonTooltips(FallenAltarContainer var1);

    public abstract String getCanOpenError(FallenAltarContainer var1);

    public abstract void onOpened(FallenAltarContainer var1, ServerClient var2);

    public abstract void onCompleted(ServerClient var1);

    public abstract void onClosed(FallenAltarObjectEntity var1, ServerClient var2);

    public abstract IncursionLevel getNewIncursionLevel(FallenAltarObjectEntity var1, LevelIdentifier var2, Server var3, WorldEntity var4, AltarData var5);

    public abstract ArrayList<UniqueIncursionModifier> getUniqueIncursionModifiers();

    public abstract IncursionRewardGetter getPlayerPersonalIncursionCompleteRewards();

    public abstract IncursionRewardGetter getPlayerSharedIncursionCompleteRewards();

    public Stream<ModifierValue<?>> getDefaultLevelModifiers() {
        return Stream.empty();
    }

    public Stream<ModifierValue<?>> getMobModifiers(Mob mob) {
        return Stream.empty();
    }

    public LootTable getExtraMobDrops(Mob mob) {
        return new LootTable();
    }

    public LootTable getBossShardDrops() {
        return new LootTable(LootItem.between("upgradeshard", UPGRADESHARDS_MIN_DROP, UPGRADESHARDS_MAX_DROP), LootItem.between("alchemyshard", ALCHEMYSHARDS_MIN_DROP, ALCHEMYSHARDS_MAX_DROP), LootItem.between("altardust", ALTAR_DUST_MIN_DROP, ALTAR_DUST_MAX_DROP));
    }

    public LootTable getExtraPrivateMobDrops(Mob mob, ServerClient client) {
        return new LootTable();
    }

    public static IncursionData fromLoadData(LoadData save) {
        String stringID = save.getUnsafeString("stringID", null);
        if (stringID == null) {
            throw new LoadDataException("Could not load IncursionData because of missing stringID");
        }
        IncursionData incursionData = IncursionDataRegistry.getNewIncursionData(stringID);
        if (incursionData == null) {
            throw new LoadDataException("Could not load IncursionData with stringID " + stringID);
        }
        incursionData.applyLoadData(save);
        incursionData.init();
        return incursionData;
    }

    public static void writePacket(IncursionData data, PacketWriter writer) {
        writer.putNextShortUnsigned(data.getID());
        data.writePacket(writer);
    }

    public static IncursionData fromPacket(PacketReader reader) {
        int id = reader.getNextShortUnsigned();
        IncursionData incursionData = IncursionDataRegistry.getNewIncursionData(id);
        if (incursionData == null) {
            throw new IllegalArgumentException("Could not find IncursionData with ID " + id);
        }
        incursionData.applyPacket(reader);
        incursionData.init();
        return incursionData;
    }

    public static IncursionData makeCopy(IncursionData data) {
        Packet packet = new Packet();
        IncursionData.writePacket(data, new PacketWriter(packet));
        return IncursionData.fromPacket(new PacketReader(packet));
    }
}

