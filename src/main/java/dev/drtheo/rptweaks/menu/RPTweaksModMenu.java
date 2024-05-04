package dev.drtheo.rptweaks.menu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.BooleanToggleBuilder;
import net.minecraft.text.Text;

public class RPTweaksModMenu extends TweaksModMenu {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> this.config(ConfigBuilder.create().setParentScreen(parent)).build();
    }

    @Override
    public void setTitle(ConfigBuilder builder, String text) {
        builder.setTitle(Text.literal(text));
    }

    @Override
    public BooleanToggleBuilder bool(ConfigEntryBuilder builder, String name, boolean value, String tooltip) {
        return builder.startBooleanToggle(Text.literal(name), value).setTooltip(Text.literal(tooltip));
    }

    @Override
    protected ConfigCategory category(ConfigBuilder builder, String name) {
        return builder.getOrCreateCategory(Text.literal(name));
    }
}
