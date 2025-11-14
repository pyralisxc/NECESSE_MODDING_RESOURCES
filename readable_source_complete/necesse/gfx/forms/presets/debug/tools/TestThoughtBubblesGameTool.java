/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.debug.tools;

import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.gfx.forms.presets.debug.DebugForm;
import necesse.gfx.forms.presets.debug.tools.MouseDebugGameTool;
import necesse.level.maps.hudManager.floatText.thoughtBubble.AgreeThoughtBubble;
import necesse.level.maps.hudManager.floatText.thoughtBubble.AngerThoughtBubble;
import necesse.level.maps.hudManager.floatText.thoughtBubble.DisagreeThoughtBubble;
import necesse.level.maps.hudManager.floatText.thoughtBubble.ExcitedThoughtBubble;
import necesse.level.maps.hudManager.floatText.thoughtBubble.ItemThoughtBubble;
import necesse.level.maps.hudManager.floatText.thoughtBubble.LoveThoughtBubble;
import necesse.level.maps.hudManager.floatText.thoughtBubble.MobThoughtBubble;
import necesse.level.maps.hudManager.floatText.thoughtBubble.OtherSettlerThoughtBubble;
import necesse.level.maps.hudManager.floatText.thoughtBubble.ThoughtBubble;

public class TestThoughtBubblesGameTool
extends MouseDebugGameTool {
    protected int currentTypeIndex = 0;

    public TestThoughtBubblesGameTool(DebugForm parent) {
        super(parent, "Thought bubble tester");
    }

    @Override
    public void init() {
        this.onLeftClick(e -> {
            int mouseX = this.getMouseX();
            int mouseY = this.getMouseY();
            Mob closestMob = this.getLevel().entityManager.mobs.getInRegionByTileRange(this.getMouseTileX(), this.getMouseTileY(), 10).stream().min(Comparator.comparingDouble(m -> m.getDistance(mouseX, mouseY))).orElse(null);
            if (closestMob != null) {
                ThoughtBubbleTestType currentType = ThoughtBubbleTestType.values()[this.currentTypeIndex];
                ThoughtBubble thoughtBubble = currentType.constructor.apply(this, closestMob);
                if (thoughtBubble != null) {
                    this.getLevel().hudManager.addElement(thoughtBubble);
                }
            }
            return true;
        }, "Test thought bubble");
        this.onScroll(e -> {
            this.currentTypeIndex = e.getMouseWheelY() < 0.0 ? Math.floorMod(this.currentTypeIndex - 1, ThoughtBubbleTestType.values().length) : Math.floorMod(this.currentTypeIndex + 1, ThoughtBubbleTestType.values().length);
            this.updateUsage();
            return true;
        }, "Change type");
        this.updateUsage();
    }

    protected void updateUsage() {
        ThoughtBubbleTestType currentType = ThoughtBubbleTestType.values()[this.currentTypeIndex];
        this.setLeftUsage("Test thought bubble: " + currentType.name());
    }

    protected <T extends Mob> T findRandomMob(Class<T> mobClass, int maxTileRange, Predicate<T> filter) {
        int mouseTileX = this.getMouseTileX();
        int mouseTileY = this.getMouseTileY();
        List list = this.getLevel().entityManager.mobs.getInRegionByTileRange(mouseTileX, mouseTileY, maxTileRange).stream().filter(m -> mobClass.isAssignableFrom(m.getClass())).map(m -> m).filter(filter).collect(Collectors.toList());
        return (T)((Mob)GameRandom.globalRandom.getOneOf(list));
    }

    protected static enum ThoughtBubbleTestType {
        ITEM((tool, target) -> new ItemThoughtBubble((Mob)target, 8000, ItemRegistry.getItem("blueberrycake"))),
        SETTLER((tool, target) -> {
            HumanMob closestHuman = tool.findRandomMob(HumanMob.class, 50, m -> m != target);
            if (closestHuman != null) {
                return new OtherSettlerThoughtBubble((Mob)target, 8000, closestHuman);
            }
            return null;
        }),
        MOB((tool, target) -> new MobThoughtBubble((Mob)target, 8000, MobRegistry.getMobID("cow"))),
        EXCITED((tool, target) -> new ExcitedThoughtBubble((Mob)target, 8000)),
        AGREE((tool, target) -> new AgreeThoughtBubble((Mob)target, 8000)),
        DISAGREE((tool, target) -> new DisagreeThoughtBubble((Mob)target, 8000)),
        LOVE((tool, target) -> new LoveThoughtBubble((Mob)target, 8000)),
        ANGER((tool, target) -> new AngerThoughtBubble((Mob)target, 8000));

        public final BiFunction<TestThoughtBubblesGameTool, Mob, ThoughtBubble> constructor;

        private ThoughtBubbleTestType(BiFunction<TestThoughtBubblesGameTool, Mob, ThoughtBubble> constructor) {
            this.constructor = constructor;
        }
    }
}

