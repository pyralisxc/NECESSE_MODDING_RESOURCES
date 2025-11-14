/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.function.BiConsumer;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;

public class TableContentDraw {
    public final ColumnWidths colWidths;
    private int totalHeight;
    private final LinkedList<TableRow> rows = new LinkedList();

    public TableContentDraw(ColumnWidths colWidths) {
        this.colWidths = colWidths;
    }

    public TableContentDraw() {
        this(new ColumnWidths());
    }

    public void setMinimumColumnWidth(int column, int width) {
        this.colWidths.setMinimumWidth(column, width);
    }

    public TableRow newRow() {
        TableRow row = new TableRow();
        this.rows.add(row);
        return row;
    }

    public int getColumnWidth(int column) {
        return this.colWidths.getWidth(column);
    }

    public int getWidth() {
        return this.colWidths.getTotalWidth();
    }

    public int getHeight() {
        return this.totalHeight;
    }

    public void draw(int x, int y) {
        for (TableRow row : this.rows) {
            y += row.draw(x, y);
        }
    }

    public static void drawSeries(int x, int y, Iterable<TableContentDraw> tables) {
        for (TableContentDraw table : tables) {
            table.draw(x, y);
            y += table.getHeight();
        }
    }

    public static void drawSeries(int x, int y, TableContentDraw ... tables) {
        TableContentDraw.drawSeries(x, y, Arrays.asList(tables));
    }

    public static class ColumnWidths {
        private int totalWidth;
        private int[] widths = new int[0];

        public void setMinimumWidth(int column, int width) {
            if (column >= this.widths.length) {
                this.widths = Arrays.copyOf(this.widths, column + 1);
            }
            if (width > this.widths[column]) {
                this.totalWidth += width - this.widths[column];
                this.widths[column] = width;
            }
        }

        public int getWidth(int column) {
            if (column >= this.widths.length) {
                return 0;
            }
            return this.widths[column];
        }

        public int getTotalWidth() {
            return this.totalWidth;
        }
    }

    public class TableRow {
        private final LinkedList<Field> fields = new LinkedList();
        private int maxHeight;

        private TableRow() {
        }

        public TableRow setMinimumHeight(int height) {
            if (height > this.maxHeight) {
                TableContentDraw.this.totalHeight += height - this.maxHeight;
                this.maxHeight = height;
            }
            return this;
        }

        public TableRow addColumn(BiConsumer<Integer, Integer> drawLogic, int drawWidth, int drawHeight, boolean centeredX, boolean centeredY) {
            int column = this.fields.size();
            TableContentDraw.this.setMinimumColumnWidth(column, drawWidth);
            this.fields.add(new Field(drawLogic, drawWidth, drawHeight, centeredX, centeredY));
            return this.setMinimumHeight(drawHeight);
        }

        public TableRow addColumn(BiConsumer<Integer, Integer> drawLogic, int drawWidth, int drawHeight) {
            return this.addColumn(drawLogic, drawWidth, drawHeight, false, false);
        }

        public TableRow addTextColumn(String text, FontOptions options, boolean centeredX, boolean centeredY, int paddingX, int paddingY) {
            return this.addColumn((x, y) -> FontManager.bit.drawString(x.intValue(), y.intValue(), text, options), FontManager.bit.getWidthCeil(text, options) + paddingX, FontManager.bit.getHeightCeil(text, options) + paddingY, centeredX, centeredY);
        }

        public TableRow addTextColumn(String text, FontOptions options, boolean centeredX, boolean centeredY) {
            return this.addTextColumn(text, options, centeredX, centeredY, 0, 0);
        }

        public TableRow addTextColumn(String text, FontOptions options, int paddingX, int paddingY) {
            return this.addTextColumn(text, options, false, false, paddingX, paddingY);
        }

        public TableRow addTextColumn(String text, FontOptions options) {
            return this.addTextColumn(text, options, false, false);
        }

        public TableRow addEmptyColumn() {
            return this.addColumn((x, y) -> {}, 0, 0);
        }

        private int draw(int x, int y) {
            int column = 0;
            for (Field field : this.fields) {
                int boxWidth = TableContentDraw.this.getColumnWidth(column);
                field.draw(x, y, boxWidth, this.maxHeight);
                x += boxWidth;
                ++column;
            }
            return this.maxHeight;
        }
    }

    private static class Field {
        private final BiConsumer<Integer, Integer> drawLogic;
        private final int drawWidth;
        private final int drawHeight;
        private final boolean centeredX;
        private final boolean centeredY;

        public Field(BiConsumer<Integer, Integer> drawLogic, int drawWidth, int drawHeight, boolean centeredX, boolean centeredY) {
            this.drawLogic = drawLogic;
            this.drawWidth = drawWidth;
            this.drawHeight = drawHeight;
            this.centeredX = centeredX;
            this.centeredY = centeredY;
        }

        public void draw(int boxX, int boxY, int boxWidth, int boxHeight) {
            int offsetX = this.centeredX ? boxWidth / 2 - this.drawWidth / 2 : 0;
            int offsetY = this.centeredY ? boxHeight / 2 - this.drawHeight / 2 : 0;
            this.drawLogic.accept(boxX + offsetX, boxY + offsetY);
        }
    }
}

