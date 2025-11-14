/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.fairType.parsers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveComponent;
import necesse.engine.save.SaveSyntaxException;
import necesse.gfx.fairType.FairGlyph;
import necesse.gfx.fairType.FairItemGlyph;
import necesse.gfx.fairType.parsers.TypeParser;
import necesse.gfx.fairType.parsers.TypeParserResult;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;

public class TypeItemParser
extends TypeParser<ItemParserResult> {
    public static final Pattern ITEM_PATTERN = Pattern.compile("\\[item=(\\w+)](\\{.+})?");
    public static final Pattern ITEMS_PATTERN = Pattern.compile("\\[items=(.+)]");
    public static final Pattern ITEM_IN_ITEMS_PATTERN = Pattern.compile("(\\w+)(\\{.+})?");
    public final int size;
    public final boolean allowGND;
    public final Function<FairItemGlyph, FairItemGlyph> modder;

    public TypeItemParser(int size, boolean allowGND, Function<FairItemGlyph, FairItemGlyph> modder) {
        this.size = size;
        this.allowGND = allowGND;
        this.modder = modder;
    }

    @Override
    public ItemParserResult getMatchResult(FairGlyph[] glyphs, int startIndex) {
        int end;
        StringBuilder sBuilder = new StringBuilder();
        for (FairGlyph glyph : glyphs) {
            sBuilder.append(glyph.getCharacter());
        }
        Matcher m = ITEMS_PATTERN.matcher(sBuilder.toString());
        if (m.find(startIndex)) {
            String itemsData = m.group(1);
            end = m.end(1) + 1;
            try {
                int sectionStop = SaveComponent.getSectionStop(itemsData, '[', ']', 0);
                itemsData = itemsData.substring(0, sectionStop + 1);
                end = m.start(2) + sectionStop + 1;
            }
            catch (SaveSyntaxException saveSyntaxException) {
                // empty catch block
            }
            Matcher itemMatcher = ITEM_IN_ITEMS_PATTERN.matcher(itemsData);
            ArrayList<InventoryItem> items = new ArrayList<InventoryItem>();
            int itemStartIndex = 0;
            while (itemMatcher.find(itemStartIndex)) {
                InventoryItem inventoryItem;
                String itemStringID = itemMatcher.group(1);
                int itemEnd = itemMatcher.end(1) + 1;
                String gndString = null;
                if (this.allowGND && itemMatcher.groupCount() > 1 && itemMatcher.group(2) != null) {
                    String gndGroup = itemMatcher.group(2);
                    try {
                        int itemGndSectionStop = SaveComponent.getSectionStop(gndGroup, '{', '}', 0);
                        gndString = gndGroup.substring(0, itemGndSectionStop + 1);
                        itemEnd = itemMatcher.start(2) + itemGndSectionStop + 1;
                    }
                    catch (SaveSyntaxException saveSyntaxException) {
                        // empty catch block
                    }
                }
                if ((inventoryItem = this.constructItem(itemStringID, gndString)) != null) {
                    items.add(inventoryItem);
                }
                if ((itemStartIndex = itemEnd) < itemsData.length()) continue;
                break;
            }
            return new ItemParserResult(m.start(), end, items);
        }
        m = ITEM_PATTERN.matcher(sBuilder.toString());
        if (m.find(startIndex)) {
            String stringID = m.group(1);
            end = m.end(1) + 1;
            String gndString = null;
            if (this.allowGND && m.groupCount() > 1 && m.group(2) != null) {
                String gndGroup = m.group(2);
                try {
                    int sectionStop = SaveComponent.getSectionStop(gndGroup, '{', '}', 0);
                    gndString = gndGroup.substring(0, sectionStop + 1);
                    end = m.start(2) + sectionStop + 1;
                }
                catch (SaveSyntaxException saveSyntaxException) {
                    // empty catch block
                }
            }
            InventoryItem inventoryItem = this.constructItem(stringID, gndString);
            return new ItemParserResult(m.start(), end, inventoryItem);
        }
        return null;
    }

    @Override
    public FairGlyph[] parse(ItemParserResult result, FairGlyph[] oldGlyphs) {
        if (result.items.isEmpty()) {
            return oldGlyphs;
        }
        FairItemGlyph glyph = new FairItemGlyph(this.size, result.items);
        if (this.modder != null) {
            glyph = this.modder.apply(glyph);
        }
        return new FairGlyph[]{glyph};
    }

    public InventoryItem constructItem(String itemStringId, String gndSection) {
        Item item = ItemRegistry.getItem(itemStringId);
        if (item == null) {
            return null;
        }
        InventoryItem inventoryItem = new InventoryItem(item);
        if (gndSection != null) {
            try {
                LoadData gndLoad = new LoadData(gndSection);
                GNDItemMap gndData = new GNDItemMap(gndLoad);
                if (gndData != null) {
                    inventoryItem.setGndData(gndData);
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return inventoryItem;
    }

    public static class ItemParserResult
    extends TypeParserResult {
        public final List<InventoryItem> items;

        public ItemParserResult(int start, int end, List<InventoryItem> items) {
            super(start, end);
            this.items = items;
        }

        public ItemParserResult(int start, int end, InventoryItem inventoryItem) {
            this(start, end, inventoryItem == null ? Collections.emptyList() : Collections.singletonList(inventoryItem));
        }
    }
}

