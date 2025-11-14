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
import necesse.entity.levelEvent.HuntIncursionEvent;
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

public class BiomeHuntIncursionData
extends BiomeMissionIncursionData {
    public BiomeHuntIncursionData() {
    }

    public BiomeHuntIncursionData(float difficulty, IncursionBiome biome, int tabletTier) {
        super(difficulty, biome, tabletTier);
    }

    @Override
    public GameMessage getDisplayName() {
        return new LocalMessage("biome", "incursionhunt", "incursion", this.biome.getLocalization());
    }

    @Override
    public GameMessage getIncursionMissionTypeName() {
        return new LocalMessage("biome", "hunt");
    }

    @Override
    public Collection<FairType> getObjectives(IncursionData incursionData, FontOptions fontOptions) {
        return Collections.singleton(new FairType().append(fontOptions, Localization.translate("ui", "incursionhuntobjective")));
    }

    @Override
    protected Iterable<FairType> getLoot(IncursionData incursionData, FontOptions fontOptions) {
        LootTable bossDrop;
        LootList list = new LootList();
        LootTable huntDrop = this.biome.getHuntDrop(incursionData);
        if (huntDrop != null) {
            huntDrop.addPossibleLoot(list, new Object[0]);
        }
        if ((bossDrop = this.biome.getBossDrop(incursionData)) != null) {
            bossDrop.addPossibleLoot(list, new Object[0]);
        }
        return list.streamItemsAndCustomItems().map(item -> this.getItemMessage((InventoryItem)item, fontOptions)).collect(Collectors.toList());
    }

    @Override
    public LootTable getExtraMobDrops(Mob mob) {
        if (!mob.isSummoned) {
            LootTable huntDrop;
            if (mob.isBoss()) {
                LootTable bossDrop = this.biome.getBossDrop(this);
                if (bossDrop != null) {
                    return new LootTable(bossDrop, this.getBossShardDrops());
                }
                return this.getBossShardDrops();
            }
            if (mob.isHostile && (huntDrop = this.biome.getHuntDrop(this)) != null) {
                return huntDrop;
            }
        }
        return super.getExtraMobDrops(mob);
    }

    @Override
    public IncursionLevel getNewIncursionLevel(FallenAltarObjectEntity altar, LevelIdentifier identifier, Server server, WorldEntity worldEntity, AltarData altarData) {
        IncursionLevel level = super.getNewIncursionLevel(altar, identifier, server, worldEntity, altarData);
        level.makeServerLevel(server);
        HuntIncursionEvent event = new HuntIncursionEvent(this.biome.bossMobStringID, 0, 150);
        level.entityManager.events.addHidden(event);
        level.incursionEventUniqueID = event.getUniqueID();
        return level;
    }
}

