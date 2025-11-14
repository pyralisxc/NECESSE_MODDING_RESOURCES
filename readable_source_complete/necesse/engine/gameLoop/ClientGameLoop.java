/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.gameLoop;

import necesse.engine.CriticalGameException;
import necesse.engine.GameExceptionHandler;
import necesse.engine.GlobalData;
import necesse.engine.eventStatusBars.EventStatusBarManager;
import necesse.engine.gameLoop.GameLoop;
import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.gameTool.GameToolManager;
import necesse.engine.loading.ClientLoader;
import necesse.engine.localization.Localization;
import necesse.engine.platforms.Platform;
import necesse.engine.platforms.PlatformManager;
import necesse.engine.postProcessing.PostProcessingEffects;
import necesse.engine.sound.SoundManager;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.gfx.Renderer;
import necesse.gfx.gameTooltips.GameTooltipManager;

public class ClientGameLoop
extends GameLoop {
    private final GameExceptionHandler exceptionHandler = new GameExceptionHandler("ticking");
    private SoundManager soundManager;
    private WindowManager windowManager;
    private ClientLoader clientLoader;
    private boolean firstTick = true;

    public ClientGameLoop(String name, int maxFPS, ClientLoader clientLoader) {
        super(name, maxFPS);
        this.clientLoader = clientLoader;
    }

    @Override
    public void init() {
        super.init();
        this.soundManager = Platform.getSoundManager();
        this.windowManager = Platform.getWindowManager();
    }

    @Override
    public void update() {
        try {
            if (this.firstTick) {
                this.clientLoader.processStartGameLaunchParameters();
                this.firstTick = false;
            }
            this.soundManager.preGameTick(this);
            this.windowManager.preGameTick(this);
            PostProcessingEffects.preGameTick(this);
            Renderer.preGameTick(this);
            GameWindow window = WindowManager.getWindow();
            Performance.record((PerformanceTimerManager)this, "other", () -> {
                Performance.record((PerformanceTimerManager)this, "inputManager", () -> Platform.getInputManager().tick(this));
                Performance.record((PerformanceTimerManager)this, "eventStatusBarPreTick", () -> EventStatusBarManager.preGameTick(this));
                Performance.record((PerformanceTimerManager)this, "tooltipPreTick", () -> GameTooltipManager.preGameTick(this));
                Performance.record((PerformanceTimerManager)this, "gameToolPreTick", () -> GameToolManager.preGameTick(this));
            });
            Performance.recordConstant((PerformanceTimerManager)this, "tickTime", () -> {
                this.runGameLoopListenersFrameTick(window);
                GlobalData.getCurrentState().frameTick(this, window);
            });
            this.windowManager.postGameTick(this);
            PostProcessingEffects.postGameTick(this);
            this.runGameLoopListenersDrawTick();
            Renderer.drawTick(this);
            Performance.record((PerformanceTimerManager)this, "other", () -> {
                Performance.record((PerformanceTimerManager)this, "gameToolLateTick", () -> GameToolManager.lateTick(this));
                if (this.isGameTick()) {
                    Performance.record((PerformanceTimerManager)this, "platformTick", () -> PlatformManager.tick(this));
                }
                Performance.record((PerformanceTimerManager)this, "soundPostTick", () -> this.soundManager.postGameTick(this));
            });
            this.exceptionHandler.clear(this.isGameTick());
        }
        catch (Exception e) {
            this.exceptionHandler.submitException(this.isGameTick(), e, () -> {
                if (GameExceptionHandler.crashAfterConsecutiveExceptions > 1) {
                    System.err.println("Stuck in error loop, exiting game");
                }
                GlobalData.getCurrentState().onCrash(this.exceptionHandler.getSavedExceptions());
            });
        }
        catch (Error e) {
            this.exceptionHandler.submitException(this.isGameTick(), new CriticalGameException(e), () -> {
                if (GameExceptionHandler.crashAfterConsecutiveExceptions > 1) {
                    System.err.println("Stuck in error loop, exiting game");
                }
                GlobalData.getCurrentState().onCrash(this.exceptionHandler.getSavedExceptions());
            });
        }
    }

    @Override
    public void updateSecond() {
        Performance.record((PerformanceTimerManager)this, "second", () -> {
            GlobalData.getCurrentState().secondTick(this);
            Localization.cleanListeners();
        });
    }
}

