package io.github.ikunkk02afk.chinesecanfly.ability.superflight;

import java.util.List;

/** Detailed, server-only result of probing one predicted super-flight movement. */
record SuperFlightPathResult(
        SuperFlightPathStatus status,
        List<SuperFlightBlockCandidate> breakCandidates,
        SuperFlightBlockCandidate hardBlocker
) {
    SuperFlightPathResult {
        breakCandidates = List.copyOf(breakCandidates);
    }

    static SuperFlightPathResult clear() {
        return new SuperFlightPathResult(SuperFlightPathStatus.CLEAR, List.of(), null);
    }

    static SuperFlightPathResult unloaded() {
        return new SuperFlightPathResult(SuperFlightPathStatus.UNLOADED_CHUNK, List.of(), null);
    }

    boolean isClear() {
        return status == SuperFlightPathStatus.CLEAR;
    }
}
