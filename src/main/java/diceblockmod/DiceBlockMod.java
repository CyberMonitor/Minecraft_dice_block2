package diceblockmod;


import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.Level;

/*
   Each mod has a main class which is used by Forge to interact with the mod during startup.

   The major points:
   1) your mod is initialised in several phases.  Each phase is intended for different
      kinds of tasks your mod should do, such as registering new blocks for example.
      These phases occur in a defined order, and at each phase an Event is triggered.  You can use these Events to make
      sure your code gets called at the right time.
      How do you set this up?
      Register your function as a subscriber to the Event, on the correct Event bus (in this case - the ModEventBus).
      When the Event is triggered, it will call your function.
      There are basically three ways to do this; see the forge MDK ExampleMod for more details.
      I've just picked my favourite (@SubscribeEvent), to make it easier to split MBE into different (self-contained) examples.
      More information here: http://greyminecraftcoder.blogspot.com/2020/02/how-forge-starts-up-your-code-1144.html

   2) your mod may have to initialise itself differently depending on whether it is being run on a DedicatedServer or a CombinedClient distribution of Minecraft
     Some of the minecraft code is marked @OnlyIn(Dist.CLIENT), and this must not be called when
      the mod is installed in a dedicated server.  This is called ClientOnly code.  All the other code is Common code.
     (For more explanation, see here: http://greyminecraftcoder.blogspot.com/2020/02/the-client-server-division-1144.html).
     Generally speaking, this means that your mod will need to initialise itself in two different sections of code:
     1) code which needs to be called regardless of whether your mod is installed in a DedicatedServer or CombinedClient distribution.  This is usually related to game logic and
        any other things that the server needs to keep track of.
     2) code which is only relevant to the client distribution.  This is usually related to graphical rendering, user input, or similar.

      The basic order of events is
     CONSTRUCTION of the mod class
     CREATE_REGISTRIES
     LOAD_REGISTRIES  such as RegistryEvent.Register<Block> to register blocks.
     COMMON_SETUP (FMLCommonSetupEvent): code which is needed regardless of whether this is a DedicatedServer distribution or a CombinedClient distribution
     SIDED_SETUP --> either FMLDedicatedServerSetupEvent(for DedicatedServer distribution) or FMLClientSetupEvent (for CombinedClient distribution).
     INTERMOD COMMUNICATION (advanced stuff, don't worry about it until you've got more experience :))

     Most of the time, you'll only ever need the Registry events (to register your blocks, items, etc), the FMLCommonSetupEvent, and perhaps the FMLClientSetupEvent

 */

// The value here should match an entry in the META-INF/mods.toml file
@Mod(DiceBlockMod.MODID)
public class DiceBlockMod {
  // you also need to update the modid in two other places as well:
  //  build.gradle file (the version, group, and archivesBaseName parameters)
  //  resources/META-INF/mods.toml (the name, description, and version parameters)
  public static final String MODID = "diceblockmod";

  // get a reference to the event bus for this mod;  Registration events are fired on this bus.
  public static IEventBus MOD_EVENT_BUS;

  public DiceBlockMod() {
    final boolean HIDE_CONSOLE_NOISE = false;  // get rid of all the noise from the console (after mod is constructed) to show warnings more clearly.
    if (HIDE_CONSOLE_NOISE) {
      //ForgeLoggerTweaker.setMinimumLevel(Level.WARN);
      //ForgeLoggerTweaker.applyLoggerFilter();
    }

    MOD_EVENT_BUS = FMLJavaModLoadingContext.get().getModEventBus();

    // The event bus register method will search these classes for methods which are interested in startup events
    //    (i.e. methods that are decorated with the @SubscribeEvent annotation)

    // Beware - there are two event busses: the MinecraftForge.EVENT_BUS, and your own ModEventBus.
    //  If you subscribe your event to the wrong bus, it will never get called.
    // likewise, beware of the difference between static and non-static methods, i.e.
    //  If you register a class, but the @SubscribeEvent is on a non-static method, it won't be called.  e.g.
    //  MOD_EVENT_BUS.register(MyClass.class);
    //  public class ServerLifecycleEvents {
    //    @SubscribeEvent
    //      public void onServerStartingEvent(FMLServerStartingEvent event) { // missing static! --> never gets called}
    //  }

    // Based on my testing: ModEventBus is used for setup events only, in the following order:
    // * RegistryEvent of all types
    // * ColorHandlerEvent for blocks & items
    // * ParticleFactoryRegisterEvent
    // * FMLCommonSetupEvent
    // * TextureStitchEvent
    // * FMLClientSetupEvent or FMLDedicatedServerSetupEvent
    // * ModelRegistryEvent
    // * Other ModLifecycleEvents such as InterModEnqueueEvent, InterModProcessEvent

// ModelBakeEvent

    // Everything else: the MinecraftForge.EVENT_BUS

    MOD_EVENT_BUS.register(StartupClientOnly.class);
    MOD_EVENT_BUS.register(StartupCommon.class);

  }
}
