/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.gameTooltips;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import necesse.engine.Settings;
import necesse.engine.util.GameUtils;
import necesse.gfx.GameColor;
import necesse.gfx.fairType.FairType;
import necesse.gfx.fairType.FairTypeDrawOptions;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.GameTooltips;

public class StringTooltips
implements GameTooltips {
    private int currentWidth;
    private int currentHeight;
    private final LinkedList<PriorityString> list = new LinkedList();

    public StringTooltips() {
    }

    public StringTooltips(String string) {
        this();
        this.add(string);
    }

    public StringTooltips(String string, int maxWidth) {
        this();
        this.add(string, maxWidth);
    }

    public StringTooltips(String string, GameColor textColor) {
        this();
        this.add(string, textColor);
    }

    public StringTooltips(String string, GameColor textColor, int maxWidth) {
        this();
        this.add(string, textColor, maxWidth);
    }

    public StringTooltips(String string, Color textColor) {
        this();
        this.add(string, textColor);
    }

    public StringTooltips(String string, Color textColor, int maxWidth) {
        this();
        this.add(string, textColor, maxWidth);
    }

    public StringTooltips(String string, Supplier<Color> textColor, int maxWidth) {
        this();
        this.add(string, textColor, maxWidth, 0);
    }

    public StringTooltips(String ... strings) {
        this();
        for (String s : strings) {
            this.add(s);
        }
    }

    public StringTooltips(List<String> strings) {
        this();
        strings.forEach(this::add);
    }

    private StringTooltips add(PriorityString str) {
        GameUtils.insertSortedList(this.list, str, Comparator.comparingInt(p -> p.priority));
        Rectangle boundingBox = str.drawOptions.getBoundingBox();
        this.currentWidth = Math.max(this.currentWidth, boundingBox.x + boundingBox.width);
        this.currentHeight += boundingBox.height;
        return this;
    }

    public StringTooltips add(String str, Supplier<Color> color, int maxWidth, int priority) {
        FontOptions fontOptions = new FontOptions(Settings.tooltipTextSize).outline();
        FairType type = new FairType().append(fontOptions, str).applyParsers(TypeParsers.GAME_COLOR, TypeParsers.ItemIcon(fontOptions.getSize()), TypeParsers.MobIcon(fontOptions.getSize()), TypeParsers.InputIcon(fontOptions));
        PriorityString priorityString = new PriorityString(type, color, maxWidth, priority);
        return this.add(priorityString);
    }

    public StringTooltips add(String str, GameColor gameColor, int maxWidth, int priority) {
        return this.add(str, gameColor.color, maxWidth, priority);
    }

    public StringTooltips add(String str, Color color, int maxWidth, int priority) {
        return this.add(str, () -> color, maxWidth, priority);
    }

    public StringTooltips add(String str, GameColor gameColor, int maxWidth) {
        return this.add(str, gameColor.color, maxWidth, 0);
    }

    public StringTooltips add(String str, Color color, int maxWidth) {
        return this.add(str, () -> color, maxWidth, 0);
    }

    public StringTooltips add(String str, int maxWidth) {
        return this.add(str, (Supplier<Color>)null, maxWidth, 0);
    }

    public StringTooltips add(String str, Color color) {
        return this.add(str, () -> color, -1, 0);
    }

    public StringTooltips add(String str, GameColor gameColor) {
        return this.add(str, gameColor.color, -1, 0);
    }

    public StringTooltips add(String str) {
        return this.add(str, (Supplier<Color>)null, -1, 0);
    }

    public StringTooltips addAll(StringTooltips other) {
        other.list.forEach(this::add);
        return this;
    }

    public StringTooltips addAll(StringTooltips other, int priority) {
        other.list.forEach(p -> this.add(new PriorityString(p.drawOptions, p.color, priority)));
        return this;
    }

    public void clear() {
        this.list.clear();
        this.currentWidth = 0;
        this.currentHeight = 0;
    }

    public int getSize() {
        return this.list.size();
    }

    @Override
    public int getHeight() {
        return this.currentHeight;
    }

    @Override
    public int getWidth() {
        return this.currentWidth;
    }

    @Override
    public void draw(int x, int y, Supplier<Color> defaultColor) {
        if (this.list.isEmpty()) {
            return;
        }
        int currentY = y;
        for (PriorityString str : this.list) {
            str.draw(x, currentY, defaultColor);
            currentY += str.drawOptions.getBoundingBox().height;
        }
    }

    @Override
    public int getDrawOrder() {
        return 0;
    }

    @Override
    public boolean matchesSearch(String search) {
        return this.list.stream().anyMatch(s -> s.drawOptions.getType().matchesSearch(search));
    }

    private class PriorityString {
        public Supplier<Color> color;
        public FairTypeDrawOptions drawOptions;
        public int priority;

        public PriorityString(FairTypeDrawOptions drawOptions, Supplier<Color> color, int priority) {
            this.drawOptions = drawOptions;
            this.color = color;
            this.priority = priority;
        }

        public PriorityString(FairType type, Supplier<Color> color, int maxWidth, int priority) {
            this(type.getDrawOptions(FairType.TextAlign.LEFT, maxWidth, true, false), color, priority);
        }

        public int getWidth() {
            return this.drawOptions.getBoundingBox().width;
        }

        public void draw(int x, int y, Supplier<Color> defaultColor) {
            this.drawOptions.draw(x, y, this.color == null ? defaultColor.get() : this.color.get());
        }
    }
}

