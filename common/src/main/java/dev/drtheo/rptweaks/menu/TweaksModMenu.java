package dev.drtheo.rptweaks.menu;

import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.drtheo.rptweaks.TweaksMod;
import dev.drtheo.rptweaks.config.TweaksConfig;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.BooleanToggleBuilder;

public abstract class TweaksModMenu implements ModMenuApi {

    public ConfigBuilder config(ConfigBuilder builder) {
        TweaksConfig config = TweaksMod.get().config();
        builder = builder.setSavingRunnable(config::save);

        this.setTitle(builder, "Resource Pack Tweaks");

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory category = this.category(builder, "General");

        AbstractConfigListEntry<?> entry = this.bool(
                entryBuilder, "Preload latest resourcepack?", config.shouldPreload(),
                "Makes Minecraft preload the latest resource pack before the game had started!"
        ).setSaveConsumer(config::shouldPreload).setDefaultValue(true).build();

        category.addEntry(entry);
        return builder;
    }

    protected abstract void setTitle(ConfigBuilder builder, String text);

    protected abstract BooleanToggleBuilder bool(ConfigEntryBuilder builder, String name, boolean value, String tooltip);

    protected abstract ConfigCategory category(ConfigBuilder builder, String name);
}
