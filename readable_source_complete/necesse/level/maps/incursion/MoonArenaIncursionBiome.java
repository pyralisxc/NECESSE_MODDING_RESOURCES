/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.incursion;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Supplier;
import necesse.engine.localization.Localization;
import necesse.engine.network.server.Server;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.util.TicketSystemList;
import necesse.engine.world.WorldEntity;
import necesse.entity.objectEntity.FallenAltarObjectEntity;
import necesse.gfx.fairType.FairItemGlyph;
import necesse.gfx.fairType.FairType;
import necesse.gfx.gameFont.FontOptions;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.incursion.AltarData;
import necesse.level.maps.incursion.BiomeMissionIncursionData;
import necesse.level.maps.incursion.BiomeTrialIncursionData;
import necesse.level.maps.incursion.IncursionBiome;
import necesse.level.maps.incursion.IncursionData;
import necesse.level.maps.incursion.MoonArenaIncursionLevel;

public class MoonArenaIncursionBiome
extends IncursionBiome {
    public MoonArenaIncursionBiome() {
        super("moonlightdancer");
        this.requiredPerkStringID = "moonarenatabletcandrop";
    }

    @Override
    public Collection<Item> getExtractionItems(IncursionData incursionData) {
        return Collections.EMPTY_LIST;
    }

    @Override
    public LootTable getHuntDrop(IncursionData incursionData) {
        return new LootTable();
    }

    @Override
    public ArrayList<FairType> getPrivateDropsDisplay(FontOptions fontOptions) {
        ArrayList<FairType> out = new ArrayList<FairType>();
        FairType fairType = new FairType();
        ArrayList<InventoryItem> inventoryItems = new ArrayList<InventoryItem>();
        InventoryItem helmet = new InventoryItem("duskhelmet", 1);
        helmet.item.setUpgradeTier(helmet, 1.0f);
        inventoryItems.add(helmet);
        InventoryItem chestplate = new InventoryItem("duskchestplate", 1);
        chestplate.item.setUpgradeTier(chestplate, 1.0f);
        inventoryItems.add(chestplate);
        InventoryItem boots = new InventoryItem("duskboots", 1);
        boots.item.setUpgradeTier(boots, 1.0f);
        inventoryItems.add(boots);
        InventoryItem keneticBootsTrinket = new InventoryItem("kineticboots", 1);
        inventoryItems.add(keneticBootsTrinket);
        fairType.append(new FairItemGlyph(fontOptions.getSize(), inventoryItems));
        fairType.append(fontOptions, " " + Localization.translate("incursion", "duskarmorloot"));
        out.add(fairType);
        return out;
    }

    @Override
    public TicketSystemList<Supplier<IncursionData>> getAvailableIncursions(int tabletTier, IncursionData incursionData) {
        TicketSystemList<Supplier<IncursionData>> system = new TicketSystemList<Supplier<IncursionData>>();
        system.addObject(100, () -> new BiomeTrialIncursionData(1.0f, this, tabletTier));
        return system;
    }

    @Override
    public IncursionLevel getNewIncursionLevel(FallenAltarObjectEntity altar, LevelIdentifier identifier, BiomeMissionIncursionData incursion, Server server, WorldEntity worldEntity, AltarData altarData) {
        return new MoonArenaIncursionLevel(identifier, incursion, worldEntity, altarData);
    }

    @Override
    public ArrayList<Color> getFallenAltarGatewayColorsForBiome() {
        ArrayList<Color> gatewayColors = new ArrayList<Color>();
        gatewayColors.add(new Color(156, 155, 239));
        gatewayColors.add(new Color(220, 212, 255));
        gatewayColors.add(new Color(156, 155, 239));
        gatewayColors.add(new Color(184, 174, 255));
        gatewayColors.add(new Color(220, 212, 255));
        gatewayColors.add(new Color(245, 247, 250));
        return gatewayColors;
    }
}

