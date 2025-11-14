/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.incursion;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Supplier;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.server.Server;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.util.TicketSystemList;
import necesse.engine.world.WorldEntity;
import necesse.entity.mobs.Mob;
import necesse.entity.objectEntity.FallenAltarObjectEntity;
import necesse.inventory.item.Item;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.incursion.AltarData;
import necesse.level.maps.incursion.BiomeExtractionIncursionData;
import necesse.level.maps.incursion.BiomeHuntIncursionData;
import necesse.level.maps.incursion.BiomeMissionIncursionData;
import necesse.level.maps.incursion.ForestDeepCaveIncursionLevel;
import necesse.level.maps.incursion.IncursionBiome;
import necesse.level.maps.incursion.IncursionData;
import necesse.level.maps.incursion.IncursionDataModifiers;

public class ForestDeepCaveIncursionBiome
extends IncursionBiome {
    public ForestDeepCaveIncursionBiome() {
        super("reaper");
    }

    @Override
    public GameMessage getNewLocalization() {
        return new LocalMessage("biome", "deepcave", "biome", BiomeRegistry.getBiome("forest").getLocalization());
    }

    @Override
    public Collection<Item> getExtractionItems(IncursionData incursionData) {
        return Collections.singleton(ItemRegistry.getItem("tungstenore"));
    }

    @Override
    public LootTable getHuntDrop(IncursionData incursionData) {
        return new LootTable(new ChanceLootItem(0.66f, "ectoplasm"));
    }

    @Override
    public LootTable getBossDrop(IncursionData incursionData) {
        return new LootTable(super.getBossDrop(incursionData), LootItem.between("shadowessence", 20, 25));
    }

    @Override
    public TicketSystemList<Supplier<IncursionData>> getAvailableIncursions(int tabletTier, IncursionData incursionData) {
        TicketSystemList<Supplier<IncursionData>> system = new TicketSystemList<Supplier<IncursionData>>();
        int huntTickets = 100;
        int extractionTickets = 100;
        if (incursionData != null) {
            huntTickets = (int)((float)huntTickets * incursionData.nextIncursionModifiers.getModifier(IncursionDataModifiers.MODIFIER_HUNT_DROPS).floatValue());
            extractionTickets = (int)((float)extractionTickets * incursionData.nextIncursionModifiers.getModifier(IncursionDataModifiers.MODIFIER_EXTRACTION_DROPS).floatValue());
        }
        system.addObject(huntTickets, () -> new BiomeHuntIncursionData(1.0f, this, tabletTier));
        system.addObject(extractionTickets, () -> new BiomeExtractionIncursionData(1.0f, this, tabletTier));
        return system;
    }

    @Override
    public IncursionLevel getNewIncursionLevel(FallenAltarObjectEntity altar, LevelIdentifier identifier, BiomeMissionIncursionData incursion, Server server, WorldEntity worldEntity, AltarData altarData) {
        return new ForestDeepCaveIncursionLevel(identifier, incursion, worldEntity, altarData);
    }

    @Override
    public ArrayList<Color> getFallenAltarGatewayColorsForBiome() {
        ArrayList<Color> gatewayColors = new ArrayList<Color>();
        gatewayColors.add(new Color(62, 140, 50));
        gatewayColors.add(new Color(74, 255, 3));
        gatewayColors.add(new Color(31, 150, 13));
        gatewayColors.add(new Color(49, 255, 3));
        gatewayColors.add(new Color(161, 244, 152));
        gatewayColors.add(new Color(240, 253, 236));
        return gatewayColors;
    }

    @Override
    public LootTable getExtraIncursionDrops(Mob mob) {
        LootTable mobDrops = super.getExtraIncursionDrops(mob);
        if (mob.isBoss()) {
            return new LootTable(mobDrops, new LootItem("shadowessence", 1));
        }
        return mobDrops;
    }
}

