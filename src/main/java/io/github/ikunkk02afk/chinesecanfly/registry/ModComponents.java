package io.github.ikunkk02afk.chinesecanfly.registry;

import io.github.ikunkk02afk.chinesecanfly.ChineseCanFly;
import io.github.ikunkk02afk.chinesecanfly.component.ChinesePowerComponent;
import io.github.ikunkk02afk.chinesecanfly.component.ChinesePowerComponentImpl;
import net.minecraft.util.Identifier;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistryV3;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy;

/** Registers the single Stage 6 player component with Cardinal Components API. */
public final class ModComponents implements EntityComponentInitializer {
    public static final ComponentKey<ChinesePowerComponent> CHINESE_POWER = ComponentRegistryV3.INSTANCE.getOrCreate(
            Identifier.of(ChineseCanFly.MOD_ID, "chinese_power"),
            ChinesePowerComponent.class
    );

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(CHINESE_POWER, player -> new ChinesePowerComponentImpl(), RespawnCopyStrategy.ALWAYS_COPY);
    }
}
