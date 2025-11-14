/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.worldPresets;

import java.util.ArrayList;
import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.util.GameRandom;
import necesse.engine.util.TicketSystemList;
import necesse.engine.world.biomeGenerator.BiomeGeneratorStack;
import necesse.engine.world.worldPresets.GenerationPreset;
import necesse.engine.world.worldPresets.LevelPresetsRegion;
import necesse.engine.world.worldPresets.WorldPreset;

public abstract class GenerationPresetsWorldPreset
extends WorldPreset {
    private final ArrayList<RegisteredGenerationPreset> presets = new ArrayList();
    public float presetsPerRegion;

    public GenerationPresetsWorldPreset(float presetsPerRegion) {
        this.presetsPerRegion = presetsPerRegion;
        this.shouldSaveGenerated = false;
    }

    public abstract void addCorePresets();

    @Override
    public void onRegistryClosed() {
        super.onRegistryClosed();
        this.addCorePresets();
    }

    public void addPreset(int tickets, GenerationPreset<?> preset) {
        this.presets.add(new RegisteredGenerationPreset(tickets * 100, preset));
        preset.init();
    }

    @Override
    public void addToRegion(GameRandom random, LevelPresetsRegion presetsRegion, BiomeGeneratorStack generatorStack, PerformanceTimerManager performanceTimer) {
        TicketSystemList ticketSystem = new TicketSystemList();
        int registeredTickets = Performance.record(performanceTimer, "setup", () -> {
            int ticketCounter = 0;
            for (RegisteredGenerationPreset preset : this.presets) {
                double presetBiomeWeight = 0.0;
                for (int biomeID : preset.preset.getBiomeIDs()) {
                    presetBiomeWeight += (double)presetsRegion.biomeIDWeights.getOrDefault(biomeID, 0).intValue();
                }
                double presetBiomeTicketMultiplier = presetBiomeWeight / (double)presetsRegion.totalBiomeWeight;
                int tickets = (int)((double)preset.tickets * presetBiomeTicketMultiplier);
                if (tickets < 1) {
                    tickets = 1;
                }
                ticketSystem.addObject(tickets, preset.preset);
                ticketCounter += tickets;
            }
            return ticketCounter;
        });
        int totalTickets = this.presets.stream().mapToInt(p -> p.tickets).sum();
        if (totalTickets - registeredTickets > 0) {
            ticketSystem.addObject(totalTickets - registeredTickets, (Object)null);
        }
        double totalRegions = (double)(presetsRegion.worldRegion.tileWidth * presetsRegion.worldRegion.tileHeight) / 256.0;
        for (double totalPoints = totalRegions * (double)this.presetsPerRegion; totalPoints > 0.0 && (!(totalPoints < 1.0) || random.getChance(totalPoints)); totalPoints -= 1.0) {
            GenerationPreset preset = (GenerationPreset)ticketSystem.getRandomObject(random);
            if (preset == null) continue;
            Performance.record(performanceTimer, preset.getClass().getSimpleName(), () -> preset.run(random, this, presetsRegion, generatorStack, performanceTimer));
        }
    }

    public static class RegisteredGenerationPreset {
        public final int tickets;
        public final GenerationPreset<?> preset;

        public RegisteredGenerationPreset(int tickets, GenerationPreset<?> preset) {
            this.tickets = tickets;
            this.preset = preset;
        }
    }
}

