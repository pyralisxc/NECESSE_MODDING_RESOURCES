/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.miscItem;

import java.io.FileNotFoundException;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.sound.GameMusic;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;

public class VinylItem
extends Item {
    public GameMusic music;

    public VinylItem(GameMusic music) {
        super(1);
        this.music = music;
        this.rarity = Item.Rarity.UNCOMMON;
        this.setItemCategory("misc", "vinyls");
    }

    @Override
    protected void loadItemTextures() {
        try {
            this.itemTexture = GameTexture.fromFileRaw("items/" + this.getStringID());
        }
        catch (FileNotFoundException e) {
            this.itemTexture = this.music.loadVinylTexture();
        }
    }

    @Override
    public GameMessage getNewLocalization() {
        return new LocalMessage("item", "vinyl", "name", this.music.trackName);
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        if (this.music.optionalTooltip != null) {
            tooltips.add(this.music.optionalTooltip);
        }
        tooltips.add(Localization.translate("itemtooltip", "vinyltip"));
        return tooltips;
    }

    @Override
    public String getTranslatedTypeName() {
        return Localization.translate("item", "unknownvinyl");
    }
}

