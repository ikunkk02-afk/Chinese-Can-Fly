package io.github.ikunkk02afk.chinesecanfly.client.effect;

import net.minecraft.client.MinecraftClient;

import java.util.UUID;

/** Client-only timing state for one short floating-Unicode-glyph presentation. */
public final class AwakeningGlyphEffect {
    public static final int DURATION_TICKS = 70;

    private final UUID playerId;
    private final long startWorldTime;

    private AwakeningGlyphEffect(UUID playerId, long startWorldTime) {
        this.playerId = playerId;
        this.startWorldTime = startWorldTime;
    }

    public static AwakeningGlyphEffect create(UUID playerId) {
        MinecraftClient client = MinecraftClient.getInstance();
        long worldTime = client.world == null ? 0L : client.world.getTime();
        return new AwakeningGlyphEffect(playerId, worldTime);
    }

    public static void trigger(UUID playerId) {
        AwakeningGlyphRenderer.trigger(create(playerId));
    }

    public UUID playerId() {
        return playerId;
    }

    public int ageAt(long worldTime) {
        return (int) Math.max(0L, worldTime - startWorldTime);
    }

    public boolean hasExpired(long worldTime) {
        return ageAt(worldTime) >= DURATION_TICKS;
    }
}
