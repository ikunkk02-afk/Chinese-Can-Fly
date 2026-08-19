package io.github.ikunkk02afk.chinesecanfly.ability.combat;

/** Mutable runtime controller state for a single descending holder. */
final class GroundSlamState {
    private final double startY;
    private int elapsedTicks;

    GroundSlamState(double startY) {
        this.startY = startY;
    }

    double startY() {
        return startY;
    }

    int incrementAndGetElapsedTicks() {
        return ++elapsedTicks;
    }
}
