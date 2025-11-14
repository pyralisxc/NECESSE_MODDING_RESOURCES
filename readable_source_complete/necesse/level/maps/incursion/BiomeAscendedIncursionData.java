/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.incursion;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.server.Server;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.WorldEntity;
import necesse.entity.levelEvent.AscendedIncursionEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.objectEntity.FallenAltarObjectEntity;
import necesse.gfx.fairType.FairType;
import necesse.gfx.gameFont.FontOptions;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootList;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.incursion.AltarData;
import necesse.level.maps.incursion.BiomeMissionIncursionData;
import necesse.level.maps.incursion.IncursionBiome;
import necesse.level.maps.incursion.IncursionData;

public class BiomeAscendedIncursionData
extends BiomeMissionIncursionData {
    public BiomeAscendedIncursionData() {
    }

    public BiomeAscendedIncursionData(float difficulty, IncursionBiome biome, int tabletTier) {
        super(difficulty, biome, tabletTier);
    }

    @Override
    protected void initModifiers() {
        super.initModifiers();
        this.uniqueIncursionModifiers.clear();
        this.playerSharedIncursionCompleteRewards.clear();
        this.playerPersonalIncursionCompleteRewards.clear();
    }

    @Override
    public GameMessage getDisplayName() {
        return new LocalMessage("biome", "incursionascended");
    }

    @Override
    public GameMessage getIncursionMissionTypeName() {
        return new LocalMessage("biome", "incursionascended");
    }

    @Override
    public Collection<FairType> getObjectives(IncursionData incursionData, FontOptions fontOptions) {
        return Collections.singleton(new FairType().append(fontOptions, Localization.translate("ui", "incursionascendedobjective")));
    }

    @Override
    protected Iterable<FairType> getLoot(IncursionData incursionData, FontOptions fontOptions) {
        LootList list = new LootList();
        LootTable bossDrop = this.biome.getBossDrop(incursionData);
        if (bossDrop != null) {
            bossDrop.addPossibleLoot(list, new Object[0]);
        }
        return list.streamItemsAndCustomItems().map(item -> this.getItemMessage((InventoryItem)item, fontOptions)).collect(Collectors.toList());
    }

    @Override
    public LootTable getExtraMobDrops(Mob mob) {
        if (!mob.isSummoned && mob.isBoss()) {
            LootTable bossDrop = this.biome.getBossDrop(this);
            if (bossDrop != null) {
                return new LootTable(bossDrop, this.getBossShardDrops());
            }
            return this.getBossShardDrops();
        }
        return super.getExtraMobDrops(mob);
    }

    @Override
    public IncursionLevel getNewIncursionLevel(FallenAltarObjectEntity altar, LevelIdentifier identifier, Server server, WorldEntity worldEntity, AltarData altarData) {
        IncursionLevel level = super.getNewIncursionLevel(altar, identifier, server, worldEntity, altarData);
        level.makeServerLevel(server);
        AscendedIncursionEvent event = new AscendedIncursionEvent("ascendedwizard");
        level.entityManager.events.addHidden(event);
        level.incursionEventUniqueID = event.getUniqueID();
        return level;
    }
}

