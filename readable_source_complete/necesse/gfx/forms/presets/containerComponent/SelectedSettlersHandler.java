/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.Input;
import necesse.engine.network.client.Client;
import necesse.engine.util.GameUtils;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.ui.HUD;
import necesse.level.maps.Level;
import necesse.level.maps.hudManager.HudDrawElement;
import necesse.level.maps.levelData.settlementData.settler.CommandMob;

public abstract class SelectedSettlersHandler {
    private static final HashMap<LevelIdentifier, HashSet<Integer>> levelsSelectedSettlers = new HashMap();
    private static HashSet<Integer> lastSelected = new HashSet();
    public final Client client;
    public final Level level;
    private HashSet<Integer> selectedSettlers = new HashSet();
    private HudDrawElement selectedSettlersHudElement;

    public SelectedSettlersHandler(Client client) {
        this.client = client;
        this.level = client.getLevel();
    }

    public void init() {
        if (this.selectedSettlersHudElement != null) {
            this.selectedSettlersHudElement.remove();
        }
        this.selectedSettlersHudElement = new HudDrawElement(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void addDrawables(List<SortedDrawable> list, GameCamera camera, PlayerMob perspective) {
                if (!SelectedSettlersHandler.this.selectedSettlers.isEmpty()) {
                    final DrawOptionsList drawOptions = new DrawOptionsList();
                    SelectedSettlersHandler selectedSettlersHandler = SelectedSettlersHandler.this;
                    synchronized (selectedSettlersHandler) {
                        Iterator iterator = SelectedSettlersHandler.this.selectedSettlers.iterator();
                        while (iterator.hasNext()) {
                            int mobUniqueID = (Integer)iterator.next();
                            Mob mob = this.getLevel().entityManager.mobs.get(mobUniqueID, false);
                            if (!(mob instanceof CommandMob) || !((CommandMob)((Object)mob)).canBeCommanded(SelectedSettlersHandler.this.client)) continue;
                            Rectangle mobSelectBox = mob.getSelectBox();
                            if (!camera.getBounds().intersects(mobSelectBox)) continue;
                            drawOptions.add(HUD.levelBoundOptions(camera, new Color(255, 255, 255, 150), true, mobSelectBox));
                        }
                    }
                    list.add(new SortedDrawable(){

                        @Override
                        public int getPriority() {
                            return -1000000;
                        }

                        @Override
                        public void draw(TickManager tickManager) {
                            drawOptions.draw();
                        }
                    });
                }
            }
        };
        this.level.hudManager.addElement(this.selectedSettlersHudElement);
        this.selectedSettlers = levelsSelectedSettlers.compute(this.level.getIdentifier(), (k, v) -> v == null ? new HashSet() : v);
        this.selectedSettlers.addAll(lastSelected);
        lastSelected = this.selectedSettlers;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void cleanUp(Predicate<Integer> isValidUniqueID) {
        SelectedSettlersHandler selectedSettlersHandler = this;
        synchronized (selectedSettlersHandler) {
            if (this.selectedSettlers.removeIf(uniqueID -> !isValidUniqueID.test((Integer)uniqueID))) {
                this.updateSelectedSettlers(false);
            }
        }
    }

    public Collection<Integer> get() {
        return this.selectedSettlers;
    }

    public int getSize() {
        return this.selectedSettlers.size();
    }

    public boolean isEmpty() {
        return this.selectedSettlers.isEmpty();
    }

    public boolean contains(int mobUniqueID) {
        return this.selectedSettlers.contains(mobUniqueID);
    }

    public void clear() {
        this.selectSettlers(false, new Integer[0]);
    }

    public static boolean isShiftDown() {
        Input input = WindowManager.getWindow().getInput();
        return input.isKeyDown(340) || input.isKeyDown(344);
    }

    public void selectOrDeselectSettler(int mobUniqueID) {
        this.selectOrDeselectSettler(!SelectedSettlersHandler.isShiftDown(), mobUniqueID);
    }

    public void selectOrDeselectSettler(boolean switchToCommandForm, int mobUniqueID) {
        if (this.selectedSettlers.contains(mobUniqueID) && this.selectedSettlers.size() > 1) {
            if (SelectedSettlersHandler.isShiftDown()) {
                this.deselectSettlers(switchToCommandForm, mobUniqueID);
            } else {
                this.selectSettlers(switchToCommandForm, mobUniqueID);
            }
        } else {
            this.selectSettlers(switchToCommandForm, mobUniqueID);
        }
    }

    public void deselectSettlers(Integer ... mobUniqueIDs) {
        this.deselectSettlers(!SelectedSettlersHandler.isShiftDown(), mobUniqueIDs);
    }

    public void deselectSettlers(boolean switchToCommandForm, Integer ... mobUniqueIDs) {
        this.deselectSettlers(switchToCommandForm, () -> GameUtils.arrayIterator(mobUniqueIDs));
    }

    public void deselectSettlers(Iterable<Integer> mobUniqueIDs) {
        this.deselectSettlers(!SelectedSettlersHandler.isShiftDown(), mobUniqueIDs);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void deselectSettlers(boolean switchToCommandForm, Iterable<Integer> mobUniqueIDs) {
        SelectedSettlersHandler selectedSettlersHandler = this;
        synchronized (selectedSettlersHandler) {
            boolean update = false;
            for (int uniqueID : mobUniqueIDs) {
                update = this.selectedSettlers.remove(uniqueID) || update;
            }
            if (update) {
                this.updateSelectedSettlers(switchToCommandForm);
            }
        }
    }

    public void selectSettlers(Integer ... mobUniqueIDs) {
        this.selectSettlers(!SelectedSettlersHandler.isShiftDown(), mobUniqueIDs);
    }

    public void selectSettlers(boolean switchToCommandForm, Integer ... mobUniqueIDs) {
        this.selectSettlers(switchToCommandForm, () -> GameUtils.arrayIterator(mobUniqueIDs));
    }

    public void selectSettlers(Iterable<Integer> mobUniqueIDs) {
        this.selectSettlers(!SelectedSettlersHandler.isShiftDown(), mobUniqueIDs);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void selectSettlers(boolean switchToCommandForm, Iterable<Integer> mobUniqueIDs) {
        SelectedSettlersHandler selectedSettlersHandler = this;
        synchronized (selectedSettlersHandler) {
            if (!SelectedSettlersHandler.isShiftDown()) {
                this.selectedSettlers.clear();
            }
            for (int uniqueID : mobUniqueIDs) {
                this.selectedSettlers.add(uniqueID);
            }
        }
        this.updateSelectedSettlers(switchToCommandForm);
    }

    public abstract void updateSelectedSettlers(boolean var1);

    public void dispose() {
        if (this.selectedSettlersHudElement != null) {
            this.selectedSettlersHudElement.remove();
        }
        this.selectedSettlersHudElement = null;
    }
}

