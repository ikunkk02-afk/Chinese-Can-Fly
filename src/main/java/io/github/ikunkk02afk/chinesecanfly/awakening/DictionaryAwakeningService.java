package io.github.ikunkk02afk.chinesecanfly.awakening;

import io.github.ikunkk02afk.chinesecanfly.ability.PlayerFlightAbilityManager;
import io.github.ikunkk02afk.chinesecanfly.component.ChinesePowerComponent;
import io.github.ikunkk02afk.chinesecanfly.registry.ModComponents;
import io.github.ikunkk02afk.chinesecanfly.registry.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

/** Server-authoritative completion of the dictionary reading action. */
public final class DictionaryAwakeningService {
    private DictionaryAwakeningService() {
    }

    /**
     * Validates the final item-use state and performs the one unified awakening state transition.
     */
    public static boolean completeReading(ServerPlayerEntity player, ItemStack stack) {
        if (!player.isAlive() || !player.isUsingItem()
                || !player.getActiveItem().isOf(ModItems.CHINESE_DICTIONARY)
                || !stack.isOf(ModItems.CHINESE_DICTIONARY)) {
            return false;
        }

        ChinesePowerComponent component = ModComponents.CHINESE_POWER.get(player);
        if (component.hasReadDictionary()) {
            return false;
        }

        component.awaken();
        ModComponents.CHINESE_POWER.sync(player);
        PlayerFlightAbilityManager.grant(player);
        AwakeningEffectController.start(player);
        return true;
    }
}
