package dev.drtheo.rptweaks.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.drtheo.rptweaks.config.Config;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.Text;

public class RPTweaksMenu implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        Config config = Config.getConfig();
        ConfigBuilder builder = ConfigBuilder.create().setSavingRunnable(config::save);
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        ConfigCategory category = builder.getOrCreateCategory(Text.of("General"));

        category.addEntry(
                entryBuilder.startBooleanToggle(
                        Text.of("Preload Latest Resource Pack?"), config.shouldPreload()
                ).setTooltip(
                        Text.of("Makes Minecraft preload the latest resource pack before the game started!")
                ).setSaveConsumer(config::shouldPreload).setDefaultValue(true).build()
        );

        return parent -> ConfigBuilder.create().build();
    }
}
