package net.hotment.jesusthesavior;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import net.hotment.jesusthesavior.JesusTheSaviorSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Random;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(JesusTheSavior.MOD_ID)
public class JesusTheSavior
{
    public static final String MOD_ID = "jesusthesavior";
    private static final Logger LOGGER = LogUtils.getLogger();


    private int prevHealth = 20;
    private boolean triggered = false;
    private boolean flashed = false;
    private int ticks = 0;
    private static final int MAX_TICKS = 20;
    private static final int MAX_IMAGES = 5; // change this to however many you have
    private ResourceLocation currentImage = null;

    public JesusTheSavior()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        JesusTheSaviorSounds.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return;

        int health = (int) player.getHealth();

        if ((health == 1 || health == 2) && prevHealth >= 10 && !triggered) {
            triggerEffect();
        }

        prevHealth = health;

        if (triggered) {
            ticks++;

            if (!flashed && ticks >= 4) {
                flashed = true;
                ticks = 0; // reset to flash again
            }

            if (ticks > MAX_TICKS) {
                triggered = false;
                ticks = 0;
                currentImage = null;
            }
        }
    }

    private void triggerEffect() {
        triggered = true;
        flashed = false;
        ticks = 0;

        Minecraft mc = Minecraft.getInstance();

        int imageIndex = new Random().nextInt(MAX_IMAGES); // 0 to MAX_IMAGES-1
        currentImage = new ResourceLocation(MOD_ID, "textures/gui/save" + imageIndex + ".png");

        ResourceLocation saveSound = new ResourceLocation(MOD_ID, "save");
        SoundEvent soundEvent = ForgeRegistries.SOUND_EVENTS.getValue(saveSound);

        if (soundEvent != null) {
            mc.level.playLocalSound(mc.player.getX(), mc.player.getY(), mc.player.getZ(),
                    soundEvent, SoundSource.PLAYERS, 1.0f, 1.0f, false);
        } else {
            LOGGER.warn("SoundEvent not found for: {}", saveSound);
        }
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGuiOverlayEvent.Post event) {
        if (!triggered) return;

        Minecraft mc = Minecraft.getInstance();
        GuiGraphics gui = event.getGuiGraphics();
        int w = mc.getWindow().getGuiScaledWidth();
        int h = mc.getWindow().getGuiScaledHeight();

        // Calculate alpha from 0.2 (start) to 0.0 (end)
        float progress = ticks / (float) MAX_TICKS;
        float eased = (float) (1 - Math.pow(progress, 2)); // ease-out quadratic
        float alpha = 0.1f * eased;

        if (alpha <= 0f) return;

        // This is where the real magic happens
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1f, 1f, 1f, alpha); // Set transparency

        if (currentImage == null) return;
        gui.blit(currentImage, 0, 0, 0, 0, w, h, w, h); // Fullscreen image

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f); // Reset to full opacity
        RenderSystem.disableBlend();
    }
}
