/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.debug.tools;

import java.awt.Point;
import java.util.List;
import java.util.function.Function;
import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.PerformanceTimer;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.gameLoop.tickManager.PerformanceTimerUtils;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.input.MouseWheelBuffer;
import necesse.engine.network.client.Client;
import necesse.engine.util.GameMath;
import necesse.engine.util.pathfinding.Pathfinding;
import necesse.entity.mobs.PathDoorOption;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.path.PathOptions;
import necesse.entity.mobs.ai.path.TilePathFindingDrawOptions;
import necesse.entity.mobs.ai.path.TilePathfinding;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.forms.presets.debug.DebugForm;
import necesse.gfx.forms.presets.debug.tools.MouseDebugGameTool;
import necesse.level.maps.Level;
import necesse.level.maps.hudManager.HudDrawElement;

public class TilePathFindGameTool
extends MouseDebugGameTool {
    public Pathfinding.Process<TilePathfinding> process;
    public TilePathfinding.NodePriority nodePriority = TilePathfinding.NodePriority.TOTAL_COST;
    public DoorMode doorMode = DoorMode.CAN_OPEN;
    public boolean paused;
    public boolean biDirectional;
    public int speed = 2;
    public Point from;
    public Point to;
    public HudDrawElement hudElement;
    public double iterationsBuffer;
    public PerformanceTimerManager performanceTimer;
    private MouseWheelBuffer wheelBuffer = new MouseWheelBuffer(false);

    public TilePathFindGameTool(DebugForm parent) {
        super(parent, "Tile path finding");
    }

    @Override
    public void init() {
        this.from = null;
        this.to = null;
        this.process = null;
        this.performanceTimer = null;
        this.paused = false;
        this.onLeftClick(e -> {
            this.from = new Point(this.getMouseTileX(), this.getMouseTileY());
            this.updatePath();
            return true;
        }, "Select start tile");
        this.onRightClick(e -> {
            this.to = new Point(this.getMouseTileX(), this.getMouseTileY());
            this.updatePath();
            return true;
        }, "Select target tile");
        this.onKeyClick(80, e -> {
            this.paused = !this.paused;
            this.setKeyUsage(80, this.paused ? "Unpause" : "Pause");
            return true;
        }, this.paused ? "Unpause" : "Pause");
        this.onKeyClick(79, e -> {
            DoorMode[] values = DoorMode.values();
            this.doorMode = values[(this.doorMode.ordinal() + 1) % values.length];
            this.setKeyUsage(79, this.doorMode.displayName);
            this.updatePath();
            return true;
        }, this.doorMode.displayName);
        this.onKeyClick(73, e -> {
            TilePathfinding.NodePriority[] values = TilePathfinding.NodePriority.values();
            this.nodePriority = values[(this.nodePriority.ordinal() + 1) % values.length];
            this.setKeyUsage(73, "Priority: " + (Object)((Object)this.nodePriority));
            this.updatePath();
            return true;
        }, "Priority: " + (Object)((Object)this.nodePriority));
        this.onKeyClick(85, e -> {
            this.biDirectional = !this.biDirectional;
            this.setKeyUsage(85, "Bi-directional: " + this.biDirectional);
            this.updatePath();
            return true;
        }, "Bi-directional: " + this.biDirectional);
        this.onKeyClick(89, e -> {
            if (this.performanceTimer != null) {
                PerformanceTimer timer = (PerformanceTimer)this.performanceTimer.getCurrentRootPerformanceTimer().getPerformanceTimer("tilePathFindingTool");
                if (this.getLevel().isClient()) {
                    Client client = this.getLevel().getClient();
                    PerformanceTimerUtils.printPerformanceTimer(timer, client.chat::addMessage);
                } else {
                    PerformanceTimerUtils.printPerformanceTimer(timer);
                }
            }
            return true;
        }, "Print performance");
        this.onScroll(e -> {
            this.wheelBuffer.add((InputEvent)e);
            this.speed = GameMath.limit(this.speed + this.wheelBuffer.useAllScrollY(), 0, 5);
            this.scrollUsage = "Speed: " + (int)Math.pow(10.0, this.speed);
            return true;
        }, "Speed: " + (int)Math.pow(10.0, this.speed));
        this.onKeyClick(76, e -> {
            if (this.process != null && !this.process.isDone()) {
                Performance.runCustomTimer(this.getLevel().tickManager(), this.performanceTimer, () -> Performance.record((PerformanceTimerManager)this.getLevel().tickManager(), "tilePathFindingTool", () -> this.process.iterate(1)));
            }
            return true;
        }, "Iterate once");
        this.hudElement = new HudDrawElement(){

            @Override
            public void addDrawables(List<SortedDrawable> list, GameCamera camera, PlayerMob perspective) {
                final DrawOptions resultsDrawOptions = TilePathFindGameTool.this.process == null ? () -> {} : (TilePathFindGameTool.this.process.getResult() == null ? new TilePathFindingDrawOptions(TilePathFindGameTool.this.process, camera) : new TilePathFindingDrawOptions(TilePathFindGameTool.this.process.getResult(), camera));
                list.add(new SortedDrawable(){

                    @Override
                    public int getPriority() {
                        return -10000;
                    }

                    @Override
                    public void draw(TickManager tickManager) {
                        resultsDrawOptions.draw();
                    }
                });
            }
        };
        this.getLevel().hudManager.addElement(this.hudElement);
    }

    @Override
    public boolean inputEvent(InputEvent event) {
        return super.inputEvent(event);
    }

    @Override
    public void tick() {
        if (!this.paused && this.process != null && !this.process.isDone()) {
            int iterationsPerSecond = (int)Math.pow(10.0, this.speed);
            this.iterationsBuffer += 0.05 * (double)iterationsPerSecond;
            if (this.iterationsBuffer >= 1.0) {
                Performance.runCustomTimer(this.getLevel().tickManager(), this.performanceTimer, () -> Performance.record((PerformanceTimerManager)this.getLevel().tickManager(), "tilePathFindingTool", () -> this.process.iterate((int)this.iterationsBuffer)));
                this.iterationsBuffer -= (double)((int)this.iterationsBuffer);
            }
        }
    }

    public void updatePath() {
        if (this.from != null && this.to != null) {
            PlayerMob playerMob = new PlayerMob(0L, null);
            playerMob.setLevel(this.getLevel());
            PathOptions options = new PathOptions(this.nodePriority);
            PathDoorOption doorOption = this.doorMode.doorOptionGetter.apply(this.getLevel());
            TilePathfinding pathfinding = new TilePathfinding(this.getLevel().tickManager(), this.getLevel(), playerMob, null, options, doorOption);
            pathfinding.biDirectional = this.biDirectional;
            this.process = pathfinding.getProcess(this.from, this.to, HumanMob.humanPathIterations);
            this.performanceTimer = new PerformanceTimerManager();
        } else {
            this.process = null;
            this.performanceTimer = null;
        }
        this.iterationsBuffer = 0.0;
    }

    @Override
    public void isCancelled() {
        super.isCancelled();
        if (this.hudElement != null) {
            this.hudElement.remove();
        }
    }

    @Override
    public void isCleared() {
        super.isCleared();
        if (this.hudElement != null) {
            this.hudElement.remove();
        }
    }

    public static enum DoorMode {
        CAN_OPEN("Can open doors", l -> l.regionManager.CAN_OPEN_DOORS_OPTIONS),
        CAN_BREAK_DOWN("Can break down objects", l -> l.regionManager.CAN_BREAK_OBJECTS_OPTIONS),
        CANNOT_PASS("Cannot pass doors", l -> l.regionManager.CANNOT_PASS_DOORS_OPTIONS),
        CANNOT_OPEN("Cannot open doors", l -> l.regionManager.BASIC_DOOR_OPTIONS);

        public final String displayName;
        public final Function<Level, PathDoorOption> doorOptionGetter;

        private DoorMode(String displayName, Function<Level, PathDoorOption> doorOptionGetter) {
            this.displayName = displayName;
            this.doorOptionGetter = doorOptionGetter;
        }
    }
}

