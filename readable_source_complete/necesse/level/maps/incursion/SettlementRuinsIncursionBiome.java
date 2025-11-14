/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.incursion;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import necesse.engine.Settings;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.server.Server;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.util.TicketSystemList;
import necesse.engine.world.WorldEntity;
import necesse.engine.world.worldData.SettlementsWorldData;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.FallenAltarObjectEntity;
import necesse.gfx.GameColor;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.object.fallenAltar.FallenAltarContainer;
import necesse.inventory.item.Item;
import necesse.inventory.item.miscItem.GatewayTabletItem;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.incursion.AltarData;
import necesse.level.maps.incursion.BiomeAscendedIncursionData;
import necesse.level.maps.incursion.BiomeMissionIncursionData;
import necesse.level.maps.incursion.IncursionBiome;
import necesse.level.maps.incursion.IncursionData;
import necesse.level.maps.incursion.SettlementRuinsIncursionLevel;
import necesse.level.maps.levelData.settlementData.NetworkSettlementData;

public class SettlementRuinsIncursionBiome
extends IncursionBiome {
    public SettlementRuinsIncursionBiome() {
        super("ascendedwizard");
    }

    @Override
    public void loadTextures() {
        try {
            this.tabletTexture = GameTexture.fromFileRaw("items/ascendedtablet");
        }
        catch (FileNotFoundException e) {
            this.tabletTexture = null;
        }
    }

    @Override
    public GameMessage getNewLocalization() {
        return new LocalMessage("biome", "incursionascended");
    }

    @Override
    public Collection<Item> getExtractionItems(IncursionData incursionData) {
        return Collections.emptyList();
    }

    @Override
    public LootTable getHuntDrop(IncursionData incursionData) {
        return new LootTable();
    }

    @Override
    public LootTable getBossDrop(IncursionData incursionData) {
        return new LootTable();
    }

    @Override
    public LootTable getExtraIncursionDrops(Mob mob) {
        return new LootTable();
    }

    @Override
    public void addDefaultTabletItems(GatewayTabletItem tabletItem, List<InventoryItem> list, PlayerMob player) {
        InventoryItem invItem = new InventoryItem(tabletItem);
        GatewayTabletItem.setIncursionData(invItem, new BiomeAscendedIncursionData(1.0f, this, 10));
        list.add(invItem);
    }

    @Override
    public TicketSystemList<Supplier<IncursionData>> getAvailableIncursions(int tabletTier, IncursionData incursionData) {
        TicketSystemList<Supplier<IncursionData>> system = new TicketSystemList<Supplier<IncursionData>>();
        system.addObject(100, () -> new BiomeAscendedIncursionData(1.0f, this, tabletTier));
        return system;
    }

    @Override
    public String getCanOpenError(BiomeMissionIncursionData incursionData, FallenAltarContainer container) {
        FallenAltarObjectEntity altar = container.altarEntity;
        SettlementsWorldData settlementsData = SettlementsWorldData.getSettlementsData(altar);
        NetworkSettlementData settlementData = settlementsData.getNetworkDataAtTile(altar.getLevel().getIdentifier(), altar.tileX, altar.tileY);
        if (settlementData == null) {
            return Localization.translate("ui", "incursionnosettlement");
        }
        return null;
    }

    @Override
    public IncursionLevel getNewIncursionLevel(FallenAltarObjectEntity altar, LevelIdentifier identifier, BiomeMissionIncursionData incursion, Server server, WorldEntity worldEntity, AltarData altarData) {
        int expansion;
        int minSize;
        int expansion2;
        NetworkSettlementData settlement = SettlementsWorldData.getSettlementsData(server).getNetworkDataAtTile(altar.getLevel().getIdentifier(), altar.tileX, altar.tileY);
        Rectangle tileRectangle = settlement == null ? new Rectangle(altar.tileX - 100, altar.tileY - 100, 200, 200) : settlement.getTileRectangle();
        int paddingAroundAltar = 30;
        if (altar.tileX - tileRectangle.x < paddingAroundAltar) {
            expansion2 = paddingAroundAltar - (altar.tileX - tileRectangle.x);
            tileRectangle.x -= expansion2;
            tileRectangle.width += expansion2;
        }
        if (tileRectangle.x + tileRectangle.width - altar.tileX < paddingAroundAltar) {
            expansion2 = paddingAroundAltar - (tileRectangle.x + tileRectangle.width - altar.tileX);
            tileRectangle.width += expansion2;
        }
        if (altar.tileY - tileRectangle.y < paddingAroundAltar) {
            expansion2 = paddingAroundAltar - (altar.tileY - tileRectangle.y);
            tileRectangle.y -= expansion2;
            tileRectangle.height += expansion2;
        }
        if (tileRectangle.y + tileRectangle.height - altar.tileY < paddingAroundAltar) {
            expansion2 = paddingAroundAltar - (tileRectangle.y + tileRectangle.height - altar.tileY);
            tileRectangle.height += expansion2;
        }
        if (tileRectangle.width < (minSize = 150)) {
            expansion = minSize - tileRectangle.width;
            tileRectangle.x -= expansion / 2;
            tileRectangle.width += expansion;
        }
        if (tileRectangle.height < minSize) {
            expansion = minSize - tileRectangle.height;
            tileRectangle.y -= expansion / 2;
            tileRectangle.height += expansion;
        }
        return new SettlementRuinsIncursionLevel(altar.getLevel(), tileRectangle, altar.tileX + 1, altar.tileY + 1, identifier, incursion, worldEntity);
    }

    @Override
    public ArrayList<Color> getFallenAltarGatewayColorsForBiome() {
        ArrayList<Color> gatewayColors = new ArrayList<Color>();
        gatewayColors.add(new Color(155, 17, 69));
        gatewayColors.add(new Color(187, 14, 80));
        gatewayColors.add(new Color(151, 13, 182));
        gatewayColors.add(new Color(27, 5, 157));
        gatewayColors.add(new Color(159, 13, 96));
        gatewayColors.add(new Color(18, 26, 80));
        return gatewayColors;
    }

    @Override
    public GameTooltips getKnownIncursionDataPreRewardsTooltip(IncursionData incursionData, GameColor color) {
        return new StringTooltips(Localization.translate("itemtooltip", "ascendedtablettip1"), color, 350);
    }

    @Override
    public GameTooltips getUnknownIncursionDataTooltip(GameColor color) {
        return new StringTooltips(Localization.translate("itemtooltip", "ascendedtablettip1"), color, 350);
    }

    @Override
    public void setupTypeAndTierLabels(BiomeMissionIncursionData data, int fontSize, FormContentBox content, FormFlow flow) {
        super.setupTypeAndTierLabels(data, fontSize, content, flow);
        flow.next(10);
        content.addComponent(flow.nextY(new FormLocalLabel("itemtooltip", "ascendedtablettip1", new FontOptions(fontSize), 0, content.getMinContentWidth() / 2, 0, content.getMinContentWidth() - 10), 4)).setColor(Settings.UI.incursionModifierOrange);
        flow.next(10);
        content.addComponent(flow.nextY(new FormLocalLabel("itemtooltip", "ascendedtablettip2", new FontOptions(fontSize), 0, content.getMinContentWidth() / 2, 0, content.getMinContentWidth() - 10), 4)).setColor(Settings.UI.incursionModifierOrange);
    }
}

