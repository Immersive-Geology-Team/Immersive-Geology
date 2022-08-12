package igteam.immersive_geology.core.registration;

import igteam.api.IGApi;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
@Mod.EventBusSubscriber(
        modid = IGApi.MODID,
        bus = Mod.EventBusSubscriber.Bus.MOD
)
public class IGSounds {
    static Set<SoundEvent> registeredEvents = new HashSet();
    public static SoundEvent crystallizer = registerSound("crystallizer");

    public IGSounds(){

    }
    private static SoundEvent registerSound(String name) {
        ResourceLocation location = new ResourceLocation(IGApi.MODID, name);
        SoundEvent event = new SoundEvent(location);
        registeredEvents.add((SoundEvent)event.setRegistryName(location));
        return event;
    }

    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> evt) {
        Iterator var1 = registeredEvents.iterator();

        while(var1.hasNext()) {
            SoundEvent event = (SoundEvent)var1.next();
            evt.getRegistry().register(event);
        }

    }
}
