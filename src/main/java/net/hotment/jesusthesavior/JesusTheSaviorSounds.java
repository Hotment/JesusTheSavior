package net.hotment.jesusthesavior;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = "jesusthesavior", bus = Mod.EventBusSubscriber.Bus.MOD)
public class JesusTheSaviorSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, "jesusthesavior");

    public static final RegistryObject<SoundEvent> SAVE =
            SOUND_EVENTS.register("save", () ->
                    SoundEvent.createVariableRangeEvent(new ResourceLocation("jesusthesavior", "save")));

    @SubscribeEvent
    public static void onRegister(RegisterEvent event) {
        // This isn't even needed when you use DeferredRegister properly
    }

    public static void register(IEventBus modEventBus) {
        SOUND_EVENTS.register(modEventBus);
    }
}
