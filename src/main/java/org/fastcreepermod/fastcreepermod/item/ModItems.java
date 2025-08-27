package org.fastcreepermod.fastcreepermod.item;



import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import org.fastcreepermod.fastcreepermod.FastCreeperMod;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.function.Function;

import static org.fastcreepermod.fastcreepermod.FastCreeperMod.MOD_ID;


public class ModItems {
    public static final Item BISMITH = registerItem("bismith", Item::new, new Item.Settings());
    public static final Item RAW_BISMITH = registerItem("raw_bismith", Item::new, new Item.Settings());


    public static Item registerItem(String name, Function<Item.Settings, Item> itemFactory, Item.Settings settings) {
        // Create the item key.
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MOD_ID, name));

        // Create the item instance.
        Item item = itemFactory.apply(settings.registryKey(itemKey));

        // Register the item.
        Registry.register(Registries.ITEM, itemKey, item);

        return item;
    }

    public static void registerModItems() {
        FastCreeperMod.LOGGER.info("Register mod items for " + MOD_ID);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries->{
            entries.add(BISMITH);
            entries.add(RAW_BISMITH);
        });
    }

}