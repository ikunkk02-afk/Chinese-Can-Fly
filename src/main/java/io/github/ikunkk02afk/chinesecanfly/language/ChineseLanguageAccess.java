package io.github.ikunkk02afk.chinesecanfly.language;

import io.github.ikunkk02afk.chinesecanfly.registry.ModComponents;
import net.minecraft.entity.player.PlayerEntity;

public final class ChineseLanguageAccess {
    private ChineseLanguageAccess() {
    }

    public static boolean canUseChinese(PlayerEntity player) {
        return ModComponents.CHINESE_POWER.maybeGet(player)
                .map(component -> component.canUseChinese())
                .orElse(false);
    }
}
