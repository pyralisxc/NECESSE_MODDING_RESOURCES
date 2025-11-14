/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.incursion;

import java.util.Collection;
import java.util.stream.Collectors;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.server.Server;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.WorldEntity;
import necesse.entity.levelEvent.ExtractionIncursionEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.objectEntity.FallenAltarObjectEntity;
import necesse.gfx.fairType.FairType;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.gameFont.FontOptions;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.lootTable.LootList;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.incursion.AltarData;
import necesse.level.maps.incursion.BiomeMissionIncursionData;
import necesse.level.maps.incursion.IncursionBiome;
import necesse.level.maps.incursion.IncursionData;

public class BiomeExtractionIncursionData
extends BiomeMissionIncursionData {
    public BiomeExtractionIncursionData() {
    }

    public BiomeExtractionIncursionData(float difficulty, IncursionBiome biome, int tabletTier) {
        super(difficulty, biome, tabletTier);
    }

    @Override
    public GameMessage getDisplayName() {
        return new LocalMessage("biome", "incursionextraction", "incursion", this.biome.getLocalization());
    }

    @Override
    public GameMessage getIncursionMissionTypeName() {
        return new LocalMessage("biome", "extraction");
    }

    @Override
    public Collection<FairType> getObjectives(IncursionData incursionData, FontOptions fontOptions) {
        return this.biome.getExtractionItems(incursionData).stream().map(item -> {
            FairType fairType = new FairType();
            fairType.append(fontOptions, Localization.translate("ui", "incursionextractionobjective", "ore", TypeParsers.getItemParseString(new InventoryItem((Item)item))));
            fairType.applyParsers(TypeParsers.ItemIcon(fontOptions.getSize()));
            return fairType;
        }).collect(Collectors.toList());
    }

    @Override
    protected Iterable<FairType> getLoot(IncursionData incursionData, FontOptions fontOptions) {
        LootList list = new LootList();
        for (Item item2 : this.biome.getExtractionItems(incursionData)) {
            list.add(item2);
        }
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
        ExtractionIncursionEvent event = new ExtractionIncursionEvent(this.biome.bossMobStringID);
        level.entityManager.events.addHidden(event);
        level.incursionEventUniqueID = event.getUniqueID();
        return level;
    }
}

