package io.github.ikunkk02afk.chinesecanfly.client.combat;

import io.github.ikunkk02afk.chinesecanfly.network.HeldEntityStatePayload;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientWorldEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Client-only holder-to-target relationship and the re-entrancy guard for hand-bound entity rendering. */
public final class HeldEntityVisualManager {
    private static final Map<UUID, TargetBinding> TARGET_BY_HOLDER = new HashMap<>();
    private static boolean renderingHeldProxy;

    private HeldEntityVisualManager() {
    }

    public static void register() {
        ClientWorldEvents.AFTER_CLIENT_WORLD_CHANGE.register((client, world) -> clear());
    }

    public static void applyState(HeldEntityStatePayload payload) {
        if (payload.holding()) {
            TARGET_BY_HOLDER.put(payload.holderId(), new TargetBinding(payload.targetId(), payload.targetEntityId()));
        } else {
            TARGET_BY_HOLDER.remove(payload.holderId());
        }
    }

    public static LivingEntity getHeldTarget(AbstractClientPlayerEntity holder) {
        TargetBinding binding = TARGET_BY_HOLDER.get(holder.getUuid());
        MinecraftClient client = MinecraftClient.getInstance();
        if (binding == null || client.world == null) {
            return null;
        }
        Entity byId = client.world.getEntityById(binding.entityId());
        if (byId instanceof LivingEntity living && living.getUuid().equals(binding.targetId())) {
            return living;
        }
        return null;
    }

    public static boolean shouldHideWorldEntity(Entity entity) {
        if (renderingHeldProxy) {
            return false;
        }
        UUID targetId = entity.getUuid();
        return TARGET_BY_HOLDER.values().stream().anyMatch(binding -> binding.targetId().equals(targetId));
    }

    public static boolean isRenderingHeldProxy() {
        return renderingHeldProxy;
    }

    public static void renderProxy(Runnable renderAction) {
        boolean previous = renderingHeldProxy;
        renderingHeldProxy = true;
        try {
            renderAction.run();
        } finally {
            renderingHeldProxy = previous;
        }
    }

    public static void clear() {
        TARGET_BY_HOLDER.clear();
        renderingHeldProxy = false;
    }

    private record TargetBinding(UUID targetId, int entityId) {
    }
}
