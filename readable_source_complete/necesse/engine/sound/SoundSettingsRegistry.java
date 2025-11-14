/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.sound;

import necesse.engine.sound.SameNearSoundCooldown;
import necesse.engine.sound.SoundSettings;
import necesse.gfx.GameResources;

public class SoundSettingsRegistry {
    public static SoundSettings wind = new SoundSettings(GameResources.wind1, GameResources.wind2, GameResources.wind3).volume(0.1f).fallOffDistance(700);
    public static SoundSettings windSnow = new SoundSettings(GameResources.windSnow1, GameResources.windSnow2, GameResources.windSnow3).volume(0.2f).fallOffDistance(700);
    public static SoundSettings windDesert = new SoundSettings(GameResources.windDesert1, GameResources.windDesert2, GameResources.windDesert3).volume(0.2f).fallOffDistance(700);
    public static SoundSettings windPlains = new SoundSettings(GameResources.windPlains1, GameResources.windPlains2, GameResources.windPlains3).volume(0.2f).fallOffDistance(700);
    public static SoundSettings windSwamp = new SoundSettings(GameResources.windSwamp1, GameResources.windSwamp2, GameResources.windSwamp3).volume(0.2f).fallOffDistance(700);
    public static SoundSettings leavesBreakAmbient = new SoundSettings(GameResources.leavesbreak2, GameResources.leavesbreak3, GameResources.leavesbreak4).volume(0.1f).pitchVariance(0.1f).fallOffDistance(500).cooldown(new SameNearSoundCooldown(4000, 200));
    public static SoundSettings leavesBreakAction = new SoundSettings(GameResources.leavesbreak2, GameResources.leavesbreak3, GameResources.leavesbreak4).volume(0.1f).basePitch(1.5f).pitchVariance(0.1f);
    public static SoundSettings defaultOpen = new SoundSettings(GameResources.defaultOpen).volume(0.3f).basePitch(0.9f);
    public static SoundSettings workstationOpen = new SoundSettings(GameResources.workstationOpen).volume(0.4f);
    public static SoundSettings landscapingStationOpen = new SoundSettings(GameResources.landscapingStationOpen).volume(0.5f);
    public static SoundSettings anvilOpen = new SoundSettings(GameResources.anvilOpen).volume(0.4f).pitchVariance(0.01f);
    public static SoundSettings crawlerFootsteps = new SoundSettings(GameResources.blunthit).volume(0.16f).basePitch(4.6f).pitchVariance(0.1f).fallOffDistance(2000);
    public static SoundSettings humanFootsteps = new SoundSettings(GameResources.blunthit).volume(0.14f).basePitch(1.7f).pitchVariance(0.1f).fallOffDistance(1000);
    public static SoundSettings smallFootsteps = new SoundSettings(GameResources.blunthit).volume(0.05f).basePitch(2.4f).pitchVariance(0.1f);
    public static SoundSettings cryoQuakeProjectileMove = new SoundSettings(GameResources.jinglehit).volume(0.4f).basePitch(0.7f).pitchVariance(0.0f).fallOffDistance(350);
    public static SoundSettings crystalGlyph = new SoundSettings(GameResources.crystalGlyph).volume(0.6f);
}

