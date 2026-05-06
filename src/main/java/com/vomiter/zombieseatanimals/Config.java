package com.vomiter.zombieseatanimals;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = ZombiesEatAnimals.MOD_ID)
public final class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    // -------- Spec values --------
    private static final ModConfigSpec.BooleanValue ALWAYS_HUNTING_VALUE;
    private static final ModConfigSpec.BooleanValue BERSERKER_HUNTING_VALUE;

    private static final ModConfigSpec.BooleanValue HUNT_AND_ZOMBIFY_HORSE_VALUE;
    private static final ModConfigSpec.BooleanValue HUNT_AND_ZOMBIFY_TAMED_HORSE_VALUE;
    private static final ModConfigSpec.BooleanValue FIND_NEARBY_ZOMBIE_HORSE_AND_RIDE_VALUE;
    private static final ModConfigSpec.BooleanValue HUNT_BABIES_VALUE;
    private static final ModConfigSpec.IntValue HUNT_START_ON_VALUE;
    private static final ModConfigSpec.BooleanValue DO_NOT_ATTACK_ZOMBIE_HORSE_VALUE;

    private static final ModConfigSpec.IntValue HUNT_COOLDOWN_TICKS_VALUE;
    private static final ModConfigSpec.IntValue HUNT_CAP_FOR_A_DAY_VALUE;
    private static final ModConfigSpec.IntValue HUNT_CAP_FOR_A_DAY_TOTAL_VALUE;
    private static final ModConfigSpec.DoubleValue HUNT_FOLLOW_DISTANCE_FACTOR_VALUE;

    private static final ModConfigSpec.BooleanValue ENABLE_EAT_FOOD_ITEMS_VALUE;
    private static final ModConfigSpec.IntValue MAX_HEALTH_BOOST_CAP_VALUE;
    private static final ModConfigSpec.IntValue MAX_HEALTH_BOOST_CAP_HARD_MODE_ADDITION_VALUE;
    private static final ModConfigSpec.IntValue EAT_COOLDOWN_TICKS_VALUE;
    private static final ModConfigSpec.IntValue RECOVERY_PER_NUTRITION_VALUE;
    private static final ModConfigSpec.BooleanValue ROTTEN_FLESH_GIVE_RESISTANCE_VALUE;

    private static final ModConfigSpec.BooleanValue ZOMBIES_BECOME_PERSISTENT_AFTER_EATING_VALUE;
    private static final ModConfigSpec.BooleanValue ZOMBIES_DROP_MORE_LOOT_VALUE;
    private static final ModConfigSpec.DoubleValue HP_LOOT_RATIO_VALUE;

    // -------- Cached primitives --------
    public static boolean ZOMBIES_BECOME_PERSISTENT = false;
    public static boolean ZOMBIES_DROP_MORE_LOOT = false;
    public static double HP_LOOT_RATIO = 0.5;

    public static boolean ALWAYS_HUNTING = false;
    public static boolean BERSERKER_HUNTING = false;
    public static boolean HUNT_AND_ZOMBIFY_HORSE = false;
    public static boolean HUNT_AND_ZOMBIFY_TAMED_HORSE = false;
    public static boolean FIND_NEARBY_ZOMBIE_HORSE_AND_RIDE = false;
    public static boolean DO_NOT_ATTACK_ZOMBIE_HORSE = true;

    public static boolean HUNT_BABIES = false;
    public static int HUNT_START_ON = 0;

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

    public static final ModConfigSpec SPEC;

    static {
        BUILDER.push("general");

        ALWAYS_HUNTING_VALUE = BUILDER
                .comment(
                        "If true, zombies always have motivation to hunt animals, even when not hungry or injured.",
                        "This does NOT bypass hunt cooldown or hunt caps."
                )
                .define("alwaysHunting", false);

        HUNT_BABIES_VALUE = BUILDER
                .comment(
                        "If true, zombies may hunt baby animals too."
                )
                .define("huntBabies", false);

        BERSERKER_HUNTING_VALUE = BUILDER
                .comment(
                        "If true, zombies enter berserker hunting mode.",
                        "This bypasses normal hunt motivation checks, hunt cooldown, and hunt caps."
                )
                .define("berserkerHunting", false);

        BUILDER.pop();

        BUILDER.push("horse");

        HUNT_AND_ZOMBIFY_HORSE_VALUE = BUILDER
                .comment(
                        "If true, a horse killed by a zombie may turn into a zombie horse."
                )
                .define("huntAndZombifyHorse", false);

        HUNT_AND_ZOMBIFY_TAMED_HORSE_VALUE = BUILDER
                .comment(
                        "If true, tamed horses may also be zombified when killed by zombies.",
                        "Only used when huntAndZombifyHorse is true."
                )
                .define("huntAndZombifyTamedHorse", false);

        FIND_NEARBY_ZOMBIE_HORSE_AND_RIDE_VALUE = BUILDER
                .comment(
                        "If true, zombies may search for a nearby zombie horse and ride it."
                )
                .define("findNearbyZombieHorseAndRide", false);

        DO_NOT_ATTACK_ZOMBIE_HORSE_VALUE = BUILDER
                .comment(
                        "If true, zombies do not attack zombie horses."
                )
                        .define("doNotAttackZombieHorses", true);

        BUILDER.pop();

        BUILDER.push("hunting");

        HUNT_START_ON_VALUE = BUILDER
                .comment(
                        "The in-game day number when zombies start hunting animals.",
                        "0 = hunting allowed from the beginning.",
                        "1 = starts on day 1, 2 = starts on day 2, and so on."
                )
                .defineInRange("huntStartOn", 0, 0, Integer.MAX_VALUE);

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

        BUILDER.push("loot");

        ZOMBIES_BECOME_PERSISTENT_AFTER_EATING_VALUE = BUILDER
                .comment("If true, zombies that have eaten meat do not despawn naturally.")
                .define("zombiesBecomePersistentAfterEating", false);

        ZOMBIES_DROP_MORE_LOOT_VALUE = BUILDER
                .comment("If true, zombies drop more loot based on how many HP boost they gained from eating meat.")
                .define("zombiesDropMoreLoot", false);
        HP_LOOT_RATIO_VALUE = BUILDER
                .comment("If the value is X, then every X * basic max hp gained would convert to loot dropped by a zombie")
                .defineInRange("hpLootRatio", 1, 0.1, 1024);

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

        HUNT_AND_ZOMBIFY_HORSE = HUNT_AND_ZOMBIFY_HORSE_VALUE.get();
        HUNT_AND_ZOMBIFY_TAMED_HORSE = HUNT_AND_ZOMBIFY_TAMED_HORSE_VALUE.get();
        FIND_NEARBY_ZOMBIE_HORSE_AND_RIDE = FIND_NEARBY_ZOMBIE_HORSE_AND_RIDE_VALUE.get();
        DO_NOT_ATTACK_ZOMBIE_HORSE = DO_NOT_ATTACK_ZOMBIE_HORSE_VALUE.get();

        HUNT_BABIES = HUNT_BABIES_VALUE.get();
        HUNT_START_ON = HUNT_START_ON_VALUE.get();

        HUNT_COOLDOWN_TICKS = HUNT_COOLDOWN_TICKS_VALUE.get();
        HUNT_CAP_FOR_A_DAY = HUNT_CAP_FOR_A_DAY_VALUE.get();
        HUNT_CAP_FOR_A_DAY_TOTAL = HUNT_CAP_FOR_A_DAY_TOTAL_VALUE.get();
        HUNT_FOLLOW_DISTANCE_FACTOR = HUNT_FOLLOW_DISTANCE_FACTOR_VALUE.get();

        ENABLE_EAT_FOOD_ITEMS = ENABLE_EAT_FOOD_ITEMS_VALUE.get();
        MAX_HEALTH_BOOST_CAP = MAX_HEALTH_BOOST_CAP_VALUE.get();
        MAX_HEALTH_BOOST_CAP_HARD_MODE_ADDITION = MAX_HEALTH_BOOST_CAP_HARD_MODE_ADDITION_VALUE.get();
        EAT_COOLDOWN_TICKS = EAT_COOLDOWN_TICKS_VALUE.get();
        RECOVERY_PER_NUTRITION = RECOVERY_PER_NUTRITION_VALUE.get();
        ROTTEN_FLESH_GIVE_RESISTANCE = ROTTEN_FLESH_GIVE_RESISTANCE_VALUE.get();

        ZOMBIES_BECOME_PERSISTENT = ZOMBIES_BECOME_PERSISTENT_AFTER_EATING_VALUE.get();
        ZOMBIES_DROP_MORE_LOOT = ZOMBIES_DROP_MORE_LOOT_VALUE.get();
        HP_LOOT_RATIO = HP_LOOT_RATIO_VALUE.get();
    }
}