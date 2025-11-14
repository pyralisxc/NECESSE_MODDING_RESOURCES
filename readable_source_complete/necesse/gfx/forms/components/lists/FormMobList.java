/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components.lists;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.function.Consumer;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.localization.Localization;
import necesse.engine.network.packet.PacketSpawnMob;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.ItemCostList;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.leaves.FollowerAINode;
import necesse.entity.mobs.friendly.FriendlyRopableMob;
import necesse.gfx.forms.components.lists.FormGeneralList;
import necesse.gfx.forms.components.lists.FormListElement;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.presets.debug.DebugForm;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

public abstract class FormMobList
extends FormGeneralList<MobElement> {
    private String filter;
    private ArrayList<MobElement> allElements;

    public FormMobList(int x, int y, int width, int height) {
        super(x, y, width, height, 40);
        this.setFilter("");
    }

    @Override
    public void reset() {
        super.reset();
        this.allElements = new ArrayList();
    }

    public void populateIfNotAlready() {
        if (this.allElements.isEmpty()) {
            this.addElements(constructor -> this.allElements.add(new MobElement((MobConstructor)constructor)));
            this.allElements.sort(Comparator.comparing(m -> m.displayMob == null ? m.mobConstructor.getDisplayName() : m.displayMob.getStringID()));
            this.setFilter(this.filter);
            this.resetScroll();
        }
    }

    public void setFilter(String filter) {
        if (filter == null) {
            return;
        }
        this.filter = filter.toLowerCase();
        this.elements = new ArrayList();
        this.allElements.stream().filter(e -> e.mobConstructor.getDisplayName().toLowerCase().contains(filter) || e.displayMob != null && e.displayMob.getStringID().toLowerCase().contains(filter) || e.displayMob != null && e.displayMob.getDisplayName().toLowerCase().contains(filter)).forEach(this.elements::add);
        this.limitMaxScroll();
    }

    public abstract void addElements(Consumer<MobConstructor> var1);

    public abstract void onClicked(MobConstructor var1);

    public class MobElement
    extends FormListElement<FormMobList> {
        public MobConstructor mobConstructor;
        public Mob displayMob;

        public MobElement(MobConstructor mobConstructor) {
            this.mobConstructor = mobConstructor;
            this.displayMob = mobConstructor.construct(null, -1, -1);
        }

        @Override
        protected void draw(FormMobList parent, TickManager tickManager, PlayerMob perspective, int elementIndex) {
            Color col = this.isHovering() ? FormMobList.this.getInterfaceStyle().highlightTextColor : FormMobList.this.getInterfaceStyle().activeTextColor;
            FontOptions headerOptions = new FontOptions(16).color(col);
            String header = GameUtils.maxString(this.mobConstructor.getDisplayName(), headerOptions, parent.width - 20);
            FontManager.bit.drawString(10.0f, 0.0f, header, headerOptions);
            FontOptions subOptions = headerOptions.copy().size(12);
            String l2 = GameUtils.maxString("Armor: " + (this.displayMob == null ? "??" : Float.valueOf(this.displayMob.getArmor())) + ", HP: " + (this.displayMob == null ? "??" : Integer.valueOf(this.displayMob.getMaxHealth())), subOptions, parent.width - 20);
            FontManager.bit.drawString(10.0f, 16.0f, l2, subOptions);
            String l3 = GameUtils.maxString("Hostile: " + (this.displayMob == null ? "??" : (this.displayMob.isHostile ? "Yes" : "No")), subOptions, parent.width - 20);
            FontManager.bit.drawString(10.0f, 28.0f, l3, subOptions);
        }

        @Override
        protected void onClick(FormMobList parent, int elementIndex, InputEvent event, PlayerMob perspective) {
            if (event.getID() != -100) {
                return;
            }
            FormMobList.this.playTickSound();
            parent.onClicked(this.mobConstructor);
        }

        @Override
        protected void onControllerEvent(FormMobList parent, int elementIndex, ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
            if (event.getState() != ControllerInput.MENU_SELECT) {
                return;
            }
            FormMobList.this.playTickSound();
            parent.onClicked(this.mobConstructor);
            event.use();
        }

        @Override
        public void drawControllerFocus(ControllerFocus current) {
            super.drawControllerFocus(current);
            GameTooltipManager.addControllerGlyph(Localization.translate("ui", "selectbutton"), ControllerInput.MENU_SELECT);
        }
    }

    public static abstract class MobConstructor {
        public String displayName;

        public MobConstructor(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return this.displayName;
        }

        public abstract Mob construct(Level var1, int var2, int var3);

        public void spawn(DebugForm parent, Level level, int x, int y) {
            Mob mob = this.construct(level, x, y);
            if (mob == null) {
                return;
            }
            mob.resetUniqueID();
            mob.onSpawned(x, y);
            parent.client.network.sendPacket(new PacketSpawnMob(mob));
            if (level.isClient()) {
                mob.playAmbientSound();
            }
        }
    }

    public static interface ExtraConstructors {
        public Collection<MobConstructor> getConstructors();
    }

    public static class AnimalKeeperWithAnimalsMobConstructor
    extends MobWithAnimalsMobConstructor {
        public AnimalKeeperWithAnimalsMobConstructor(String displayName, String mobStringID, int mobs) {
            super(displayName, mobStringID, mobs);
        }

        @Override
        public Mob construct(Level level, int x, int y) {
            return MobRegistry.getMob("animalkeeperhuman", level);
        }
    }

    public static abstract class MobWithAnimalsMobConstructor
    extends MobConstructor {
        public String mobStringID;
        public int mobs;

        public MobWithAnimalsMobConstructor(String displayName, String mobStringID, int mobs) {
            super(displayName);
            this.mobStringID = mobStringID;
            this.mobs = mobs;
        }

        @Override
        public void spawn(DebugForm parent, Level level, int x, int y) {
            Mob mob = this.construct(level, x, y);
            if (mob == null) {
                return;
            }
            mob.resetUniqueID();
            mob.onSpawned(x, y);
            parent.client.network.sendPacket(new PacketSpawnMob(mob));
            for (int i = 0; i < this.mobs; ++i) {
                FriendlyRopableMob ropeMob = (FriendlyRopableMob)MobRegistry.getMob(this.mobStringID, level);
                Point animalPos = FollowerAINode.getTeleportCloseToPos(ropeMob, mob, 1);
                ropeMob.onSpawned(animalPos.x, animalPos.y);
                ropeMob.onRope(mob.getUniqueID(), new InventoryItem("rope"));
                ropeMob.removeIfRoperRemoved = true;
                ropeMob.buyPrice = new ItemCostList();
                ropeMob.buyPrice.addItem("coin", GameRandom.globalRandom.getIntBetween(500, 600));
                parent.client.network.sendPacket(new PacketSpawnMob(ropeMob));
            }
            if (level.isClient()) {
                mob.playAmbientSound();
            }
        }
    }
}

