/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.objectEntity;

import necesse.engine.Settings;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.gfx.fairType.FairType;
import necesse.gfx.fairType.FairTypeDrawOptions;
import necesse.gfx.forms.presets.containerComponent.object.SignContainerForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.FairTypeTooltip;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.level.maps.Level;

public class SignObjectEntity
extends ObjectEntity {
    private String text;
    private FairTypeDrawOptions textDrawOptions;
    private int textDrawFontSize;

    public SignObjectEntity(Level level, int x, int y) {
        super(level, "sign", x, y);
        this.setText("");
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addSafeString("text", this.text);
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.setText(save.getSafeString("text", ""));
    }

    @Override
    public void setupContentPacket(PacketWriter writer) {
        super.setupContentPacket(writer);
        String str = this.getTextString();
        writer.putNextString(str);
    }

    @Override
    public void applyContentPacket(PacketReader reader) {
        super.applyContentPacket(reader);
        this.setText(reader.getNextString());
    }

    public void setText(String str) {
        String oldText = this.text;
        this.text = str.trim();
        if (!this.text.equals(oldText)) {
            this.textDrawOptions = null;
        }
    }

    private FairTypeDrawOptions getTextDrawOptions() {
        if (this.textDrawOptions == null || this.textDrawOptions.shouldUpdate() || this.textDrawFontSize != Settings.tooltipTextSize) {
            FairType type = new FairType();
            FontOptions fontOptions = new FontOptions(Settings.tooltipTextSize).outline();
            type.append(fontOptions, this.text);
            type.applyParsers(SignContainerForm.getParsers(fontOptions));
            this.textDrawOptions = type.getDrawOptions(FairType.TextAlign.LEFT, 400, true, true);
            this.textDrawFontSize = fontOptions.getSize();
        }
        return this.textDrawOptions;
    }

    public String getTextString() {
        return this.text;
    }

    @Override
    public void onMouseHover(PlayerMob perspective, boolean debug) {
        super.onMouseHover(perspective, debug);
        GameTooltipManager.addTooltip(new FairTypeTooltip(this.getTextDrawOptions()), TooltipLocation.INTERACT_FOCUS);
    }

    @Override
    public GameTooltips getMapTooltips() {
        ListGameTooltips tooltips = new ListGameTooltips(new StringTooltips(this.getObject().getDisplayName() + ":"));
        tooltips.add(new FairTypeTooltip(this.getTextDrawOptions()));
        return tooltips;
    }
}

