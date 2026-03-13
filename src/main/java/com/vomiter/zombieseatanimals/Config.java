package com.vomiter.zombieseatanimals;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = ZombiesEatAnimals.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    // -------- Spec values --------
    private static final ForgeConfigSpec.BooleanValue ALWAYS_HUNTING_VALUE;
    private static final ForgeConfigSpec.BooleanValue BERSERKER_HUNTING_VALUE;

    private static final ForgeConfigSpec.IntValue HUNT_COOLDOWN_TICKS_VALUE;
    private static final ForgeConfigSpec.IntValue HUNT_CAP_FOR_A_DAY_VALUE;
    private static final ForgeConfigSpec.IntValue HUNT_CAP_FOR_A_DAY_TOTAL_VALUE;
    private static final ForgeConfigSpec.DoubleValue HUNT_FOLLOW_DISTANCE_FACTOR_VALUE;

    private static final ForgeConfigSpec.BooleanValue ENABLE_EAT_FOOD_ITEMS_VALUE;
    private static final ForgeConfigSpec.IntValue MAX_HEALTH_BOOST_CAP_VALUE;
    private static final ForgeConfigSpec.IntValue MAX_HEALTH_BOOST_CAP_HARD_MODE_ADDITION_VALUE;
    private static final ForgeConfigSpec.IntValue EAT_COOLDOWN_TICKS_VALUE;
    private static final ForgeConfigSpec.IntValue RECOVERY_PER_NUTRITION_VALUE;
    private static final ForgeConfigSpec.BooleanValue ROTTEN_FLESH_GIVE_RESISTANCE_VALUE;

    // -------- Cached primitives --------
    public static boolean ALWAYS_HUNTING = false;
    public static boolean BERSERKER_HUNTING = false;

    public static int HUNT_COOLDOWN_TICKS = 20 * 60; // 1 minute
    public static int HUNT_CAP_FOR_A_DAY = 10;
    public static int HUNT_CAP_FOR_A_DAY_TOTAL = 20; // shared by all zombies in the same level
    public static double HUNT_FOLLOW_DISTANCE_FACTOR = 1.0;

    public static boolean ENABLE_EAT_FOOD_ITEMS = true;
    public static int MAX_HEALTH_BOOST_CAP = 20;
    public static int MAX_HEALTH_BOOST_CAP_HARD_MODE_ADDITION = 20;
    public static int EAT_COOLDOWN_TICKS = 40;
    public static int RECOVERY_PER_NUTRITION = 1;
    public static boolean ROTTEN_FLESH_GIVE_RESISTANCE = true;

    public static final ForgeConfigSpec SPEC;

    static {
        BUILDER.push("general");

        ALWAYS_HUNTING_VALUE = BUILDER
                .comment(
                        "If true, zombies always have motivation to hunt animals, even when not hungry or injured.",
                        "This does NOT bypass hunt cooldown or hunt caps."
                )
                .define("alwaysHunting", false);

        BERSERKER_HUNTING_VALUE = BUILDER
                .comment(
                        "If true, zombies enter berserker hunting mode.",
                        "This bypasses normal hunt motivation checks, hunt cooldown, and hunt caps."
                )
                .define("berserkerHunting", false);

        BUILDER.pop();

        BUILDER.push("hunting");

        HUNT_COOLDOWN_TICKS_VALUE = BUILDER
                .comment(
                        "Cooldown between successful animal hunts for each zombie, in ticks.",
                        "20 ticks = 1 second.",
                        "Only used when berserkerHunting is false."
                )
                .defineInRange("huntCooldownTicks", 20 * 60, 0, 20 * 60 * 60);

        HUNT_CAP_FOR_A_DAY_VALUE = BUILDER
                .comment(
                        "Maximum number of animals one zombie may successfully hunt within one Minecraft day (24000 ticks).",
                        "Only used when berserkerHunting is false."
                )
                .defineInRange("huntCapForADay", 10, 0, 100000);

        HUNT_CAP_FOR_A_DAY_TOTAL_VALUE = BUILDER
                .comment(
                        "Maximum total number of successful hunts by all zombies within the same level during one Minecraft day (24000 ticks).",
                        "Only used when berserkerHunting is false."
                )
                .defineInRange("huntCapForADayTotal", 20, 0, 100000);

        HUNT_FOLLOW_DISTANCE_FACTOR_VALUE = BUILDER
                .comment(
                        "Multiplier applied to the zombie's base follow range when searching for animals to hunt.",
                        "Example: 1.0 = same as normal follow range.",
                        "2.0 = twice the normal follow range."
                )
                .defineInRange("huntFollowDistanceFactor", 1.0, 0.0, 1024.0);

        BUILDER.pop();

        BUILDER.push("eating");

        ENABLE_EAT_FOOD_ITEMS_VALUE = BUILDER
                .comment("If true, zombies can eat dropped food items (ItemEntity) as well.")
                .define("enableEatFoodItems", true);

        MAX_HEALTH_BOOST_CAP_VALUE = BUILDER
                .comment(
                        "Maximum bonus health zombies can gain from eating, in half-hearts.",
                        "Example: 20 = +10 hearts."
                )
                .defineInRange("maxHealthBoostCap", 20, 0, 1024);

        MAX_HEALTH_BOOST_CAP_HARD_MODE_ADDITION_VALUE = BUILDER
                .comment("Extra maxHealthBoostCap added in HARD difficulty, in half-hearts.")
                .defineInRange("maxHealthBoostCapHardModeAddition", 20, 0, 1024);

        EAT_COOLDOWN_TICKS_VALUE = BUILDER
                .comment(
                        "Cooldown after a successful eating action, in ticks.",
                        "20 ticks = 1 second."
                )
                .defineInRange("eatCooldownTicks", 40, 0, 20 * 60 * 60);

        RECOVERY_PER_NUTRITION_VALUE = BUILDER
                .comment(
                        "How much HP to recover per 1 nutrition point when eating food items.",
                        "1 = 0.5 heart."
                )
                .defineInRange("recoveryPerNutrition", 1, 0, 1024);

        ROTTEN_FLESH_GIVE_RESISTANCE_VALUE = BUILDER
                .comment("If true, eating rotten flesh grants a short Resistance effect.")
                .define("rottenFleshGiveResistance", true);

        BUILDER.pop();

        SPEC = BUILDER.build();
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        if (event.getConfig().getSpec() == SPEC) {
            bake();
        }
    }

    @SubscribeEvent
    static void onReload(final ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() == SPEC) {
            bake();
        }
    }

    private static void bake() {
        ALWAYS_HUNTING = ALWAYS_HUNTING_VALUE.get();
        BERSERKER_HUNTING = BERSERKER_HUNTING_VALUE.get();

        HUNT_COOLDOWN_TICKS = HUNT_COOLDOWN_TICKS_VALUE.get();
        HUNT_CAP_FOR_A_DAY = HUNT_CAP_FOR_A_DAY_VALUE.get();
        HUNT_CAP_FOR_A_DAY_TOTAL = HUNT_CAP_FOR_A_DAY_TOTAL_VALUE.get();

        ENABLE_EAT_FOOD_ITEMS = ENABLE_EAT_FOOD_ITEMS_VALUE.get();
        MAX_HEALTH_BOOST_CAP = MAX_HEALTH_BOOST_CAP_VALUE.get();
        MAX_HEALTH_BOOST_CAP_HARD_MODE_ADDITION = MAX_HEALTH_BOOST_CAP_HARD_MODE_ADDITION_VALUE.get();
        EAT_COOLDOWN_TICKS = EAT_COOLDOWN_TICKS_VALUE.get();
        RECOVERY_PER_NUTRITION = RECOVERY_PER_NUTRITION_VALUE.get();
        ROTTEN_FLESH_GIVE_RESISTANCE = ROTTEN_FLESH_GIVE_RESISTANCE_VALUE.get();
        HUNT_FOLLOW_DISTANCE_FACTOR = HUNT_FOLLOW_DISTANCE_FACTOR_VALUE.get();
    }
}