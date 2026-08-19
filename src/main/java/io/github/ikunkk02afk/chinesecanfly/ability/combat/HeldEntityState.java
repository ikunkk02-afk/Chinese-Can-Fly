package io.github.ikunkk02afk.chinesecanfly.ability.combat;

import java.util.UUID;

/** Runtime-only relationship; no player or entity NBT is changed. */
record HeldEntityState(UUID holderId, UUID targetId, boolean previousNoGravity) {
}
