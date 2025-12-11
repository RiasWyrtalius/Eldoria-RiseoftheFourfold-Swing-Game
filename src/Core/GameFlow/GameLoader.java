package Core.GameFlow;

import Abilities.JobClass;
import Abilities.JobFactory;
import Characters.Base.Enemy;
import Characters.Base.Hero;
import Characters.Enemies.*;
import Characters.Party;
import Core.Story.StorySlide;
import Resource.Audio.AudioManager;
import Core.Utils.LogManager;
import Core.Utils.LogFormat;
import Items.*;
import UI.Views.BattleInterface;
import Core.Story.StoryContent;

import java.util.*;
import java.util.function.Function;

/**
 * Loads and saves levels, generates the campaign, and handles asset registration.
 */
public class GameLoader {
    private final Queue<Level> campaignQueue;
    private long currentSeed;
    private final List<EnemySpawnRule> allEnemyTypes = new ArrayList<>();
    private BattleInterface battleInterface;

    public GameLoader() {
        this.campaignQueue = new LinkedList<>();
        registerAudioAssets();
        initializeAllEnemyTypes();
    }

    private record EnemySpawnRule(Function<Integer, Enemy> factory, int minLevel) {}

    /**
     * Defines the fixed Story levels (1, 5, 10, 15, 20).
     */
    private Map<Integer, Level> getPredefinedLevels() {
        Map<Integer, Level> fixedLevels = new HashMap<>();

        // ================= LEVEL 1: THE GATES =================
        List<StorySlide> preCutsceneLevel1 = new ArrayList<>();
        preCutsceneLevel1.add(new StorySlide(
                "/Assets/Images/Backgrounds/eldoria_map.png",
                List.of("At the edge of the wilds, the first champion steps forward. Alone, yet bound by prophecy, the journey into corruption begins.")
        ));

        List<StorySlide> postCutsceneLevel1 = new ArrayList<>();
        postCutsceneLevel1.add(new StorySlide(
                "/Assets/Images/CutsceneImages/Floor1/Floor1_c1.png",
                List.of("The goblins fall. The land exhales. Your strength grows — and somewhere in Eldoria,")
        ));
        postCutsceneLevel1.add(new StorySlide(
                "/Assets/Images/CutsceneImages/Floor1/Floor1_c2.png",
                List.of("another hero takes notice. Drawn by fate, a cloaked champion steps from the shadows, waiting for the one who will summon them.")
        ));

        fixedLevels.put(1, createSpecificLevel(
                1,
                "The Gates",
                "/Assets/Images/Backgrounds/Level_BG/sample.jpg",
                "A swarm of goblins blocks your path!",
                buildEnemyGroup(Goblin::new, Goblin::new),
                buildLoot(
                        ItemFactory.smallHealthPotion(),
                        ItemFactory.smallManaPotion(),
                        ItemFactory.summoningScroll()
                ),
                preCutsceneLevel1,
                postCutsceneLevel1
        ));

        // ================= LEVEL 5: THE COVEN =================
        List<StorySlide> preCutsceneLevel5 = new ArrayList<>();
        preCutsceneLevel5.add(new StorySlide(
                "/Assets/Images/CutsceneImages/Floor5/Floor5_c1pre.png",
                List.of("Deep in the forest, ancient magic stirs — a new hero senses your rise, awaiting the scroll that will summon them.")
        ));

        List<StorySlide> postCutsceneLevel5 = new ArrayList<>();
        postCutsceneLevel5.add(new StorySlide(
                "/Assets/Images/CutsceneImages/Floor5/Floor5_c1.png",
                List.of("The forest stills. The vampires fade to crimson dust, drawn into the roots beneath your feet.")
        ));
        postCutsceneLevel5.add(new StorySlide(
                "/Assets/Images/CutsceneImages/Floor5/Floor5_c2.png",
                List.of("From the shadows, a hidden hunter watches… as spiders creep toward the same unseen call.")
        ));

        fixedLevels.put(5, createSpecificLevel(
                5,
                "The Coven",
                "/Assets/Images/Backgrounds/Level_BG/Forest_Biome/sprite_0.png",
                "Whispers spread through the forest. A hunter stalks the same darkness you fight.",
                buildEnemyGroup(Vampire::new, Vampire::new),
                buildLoot(
                        ItemFactory.smallHealthPotion(),
                        ItemFactory.mediumHealthPotion(),
                        ItemFactory.splashHealthPotion(),
                        ItemFactory.splashManaPotion(),
                        ItemFactory.summoningScroll()
                ),
                preCutsceneLevel5,
                postCutsceneLevel5
        ));

        // ================= LEVEL 10: CRITTERS AND FANGS =================
        List<StorySlide> preCutsceneLevel10 = new ArrayList<>();
        preCutsceneLevel10.add(new StorySlide(
                "/Assets/Images/CutsceneImages/Floor10/Floor10_c1pre.png",
                List.of("The forest grows restless — threads of web and whispers of fang weave into a single rising threat.")
        ));

        List<StorySlide> postCutsceneLevel10 = new ArrayList<>();
        postCutsceneLevel10.add(new StorySlide(
                "/Assets/Images/CutsceneImages/Floor10/Floor10_c1.png",
                List.of("Beneath the trees, spiders swarm toward a pulsing glow in the roots, answered by lingering vampiric ash.")
        ));
        postCutsceneLevel10.add(new StorySlide(
                "/Assets/Images/CutsceneImages/Floor10/Floor10_c2.png",
                List.of("What erupts here will soon take shape — the horrors awaiting you deeper in the wild.")
        ));

        fixedLevels.put(10, createSpecificLevel(
                10,
                "The Critters and Fangs",
                "/Assets/Images/Backgrounds/Level_BG/Snow_Biome/sprite_0.png",
                "The brood gathers.",
                buildEnemyGroup(Vampire::new, Spider::new, Spider::new, Vampire::new),
                buildLoot(
                        ItemFactory.mediumHealthPotion(),
                        ItemFactory.mediumManaPotion(),
                        ItemFactory.summoningScroll(),
                        ItemFactory.revivePotion()
                ),
                preCutsceneLevel10,
                postCutsceneLevel10
        ));

        // ================= LEVEL 15: FIRE AND ROCK =================
        List<StorySlide> preCutsceneLevel15 = new ArrayList<>();
        preCutsceneLevel15.add(new StorySlide(
                "/Assets/Images/CutsceneImages/Floor15/Floor15_c1pre.png",
                List.of("The ground that once trembled now swells and splits — corruption rising in sludge and stone.")
        ));

        List<StorySlide> postCutsceneLevel15 = new ArrayList<>();
        postCutsceneLevel15.add(new StorySlide(
                "/Assets/Images/CutsceneImages/Floor15/Floor15_c1.png",
                List.of("The corrupted sludge recoils into the cracked earth, its twisted glow fading as the forest exhales in relief.")
        ));
        postCutsceneLevel15.add(new StorySlide(
                "/Assets/Images/CutsceneImages/Floor15/Floor15_c2.png",
                List.of("The fallen golem collapses to dust, revealing deeper fissures below — something colossal stirs in the depths, feeding on the corruption that remains.")
        ));

        fixedLevels.put(15, createSpecificLevel(
                15,
                "The Fire and Rock",
                "/Assets/Images/Backgrounds/Level_BG/Dungeon_Biome/sprite_0.png",
                "Stops you at the entrance",
                buildEnemyGroup((_) -> new GolemBoss(10), (_) -> new DragonBoss(10)),
                buildLoot(
                        ItemFactory.mediumHealthPotion(),
                        ItemFactory.mediumManaPotion(),
                        ItemFactory.revivePotion()
                ),
                preCutsceneLevel15,
                postCutsceneLevel15
        ));

        // ================= LEVEL 20: RAGE OF ROCK AND FLAME =================
        List<StorySlide> preCutsceneLevel20 = new ArrayList<>();
        preCutsceneLevel20.add(new StorySlide(
                "/Assets/Images/CutsceneImages/Floor20/Floor20_c1pre.png",
                List.of("From the deepest fissures, a presence awakens — purple flame twisting through rising stone, as a colossal shadow climbs toward the surface.")
        ));

        List<StorySlide> postCutsceneLevel20 = new ArrayList<>();
        postCutsceneLevel20.add(new StorySlide(
                "/Assets/Images/CutsceneImages/Floor20/Floor20_c1.png",
                List.of("Varoth’s inferno collapses inward, the purple flames sputtering out as the last pulse of corruption drains into the earth.")
        ));
        postCutsceneLevel20.add(new StorySlide(
                "/Assets/Images/CutsceneImages/Floor20/Floor20_c2.png",
                List.of("Silence settles over the ruin; the fissures cool, embers fade, and the forest stands still — free at last from the shadow that once devoured its light.")
        ));

        fixedLevels.put(20, createSpecificLevel(
                20,
                "Rage of Rock and Flame",
                "/Assets/Images/Backgrounds/Level_BG/Dungeon_Biome/sprite_0.png",
                "The final ascent begins. Fire and stone guard the path to destiny.",
                buildEnemyGroup((_) -> new Varoth(20)),
                buildLoot(
                        ItemFactory.mediumHealthPotion(),
                        ItemFactory.mediumManaPotion(),
                        ItemFactory.revivePotion()
                ),
                preCutsceneLevel20,
                postCutsceneLevel20
        ));

        return fixedLevels;
    }

    /**
     * Creates a level WITH cutscenes.
     */
    private Level createSpecificLevel(int levelNum, String name, String bgKey, String intro,
                                      List<Function<Integer, Enemy>> enemies, List<Item> loot,
                                      List<StorySlide> pre, List<StorySlide> post) {
        return new Level(
                levelNum, name, intro, bgKey,
                enemies, enemies.size(), enemies.size(),
                loot, 100 * levelNum, 0, true,
                pre, post,
                getMusicForLevel(levelNum) // Music Key
        );
    }

    /**
     * Creates a level WITHOUT cutscenes (Random Mobs).
     */
    private Level createSpecificLevel(int levelNum, String name, String bgKey, String intro,
                                      List<Function<Integer, Enemy>> enemies, List<Item> loot) {
        return new Level(
                levelNum, name, intro, bgKey,
                enemies, enemies.size(), enemies.size(),
                loot, 100 * levelNum, 0, true,
                null, null,
                getMusicForLevel(levelNum) // Music Key
        );
    }

    @SafeVarargs
    private List<Function<Integer, Enemy>> buildEnemyGroup(Function<Integer, Enemy>... generators) {
        return new ArrayList<>(Arrays.asList(generators));
    }

    private List<Item> buildLoot(Item... items) {
        return new ArrayList<>(Arrays.asList(items));
    }

    //CAMPAIGN GENERATION
    public void generateCampaign(long seed, int totalLevels) {
        this.currentSeed = seed;
        this.campaignQueue.clear();

        Random campaignRng = new Random(seed);
        LogManager.log("Generating Campaign with Seed: " + seed, LogFormat.SYSTEM);

        Map<Integer, Level> fixedLevels = getPredefinedLevels();

        for (int i = 1; i <= totalLevels; i++) {
            long uniqueLevelSeed = campaignRng.nextLong();

            if (fixedLevels.containsKey(i)) {
                campaignQueue.add(fixedLevels.get(i));
                LogManager.log("Loaded Fixed Level: " + i);
            } else {
                campaignQueue.add(generateRandomMobLevel(campaignRng, i, uniqueLevelSeed));
            }
        }
    }

    private Level generateRandomMobLevel(Random rng, int levelNum, long levelSeed) {
        List<Function<Integer, Enemy>> validGenerators = new ArrayList<>();

        for (EnemySpawnRule rule : allEnemyTypes) {
            if (levelNum >= rule.minLevel) {
                validGenerators.add(rule.factory);
            }
        }

        if (validGenerators.isEmpty()) {
            validGenerators.add(Goblin::new);
        }

        // SCALING DIFFICULTY
        int baseMin = 1;
        int baseMax = 2;
        int scalingBonus = levelNum / 4;
        int minEnemies = baseMin + (scalingBonus / 2);
        int maxEnemies = Math.min(baseMax + scalingBonus, 4);
        if (minEnemies > maxEnemies) minEnemies = maxEnemies;

        String bg = getBackgroundForLevel(levelNum);

        return new Level(
                levelNum,
                "Stage " + levelNum,
                "Wild monsters appear!",
                bg,
                validGenerators,
                minEnemies,
                maxEnemies,
                generateRandomLoot(rng, levelNum),
                100 * levelNum,
                levelSeed,
                false, null, null,
                getMusicForLevel(levelNum) // Music Key
        );
    }

    private List<Item> generateRandomLoot(Random rng, int level) {
        return LootManager.getInstance().generateLoot(rng, level);
    }

    public Level loadNextLevel() {
        return campaignQueue.poll();
    }

    //SAVE / LOAD LOGIC
    public Party loadPartyFromSave(GameState state) {
        Party party = new Party(state.partyName);

        for (HeroSaveData data : state.partyMembers) {
            JobClass job = JobFactory.getJob(data.jobClassName);
            Hero hero = new Hero(
                    data.name,
                    data.baseHP,
                    data.baseAtk,
                    data.baseMP,
                    data.level,
                    job
            );
            hero.setHealth(data.currentHP, null);
            hero.setMana(data.currentMP);
            party.addPartyMember(hero);
        }
        restoreInventory(party, state.inventoryCounts);
        return party;
    }

    private void restoreInventory(Party party, Map<String, Integer> counts) {
        Inventory inv = party.getInventory();
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            Item item = ItemFactory.getItemByName(entry.getKey());
            if (item != null) {
                inv.addItem(item, entry.getValue());
            }
        }
    }

    public static Party createInitialParty(String partyName) {
        Party heroParty = new Party(partyName);
        loadStartingInventory(heroParty);
        return heroParty;
    }

    private static void loadStartingInventory(Party party) {
        party.getInventory().addItem(ItemFactory.revivePotion(), 1);
        party.getInventory().addItem(ItemFactory.smallHealthPotion(), 3);
    }

    //ASSETS & CONFIGURATION
    private void initializeAllEnemyTypes() {
        allEnemyTypes.add(new EnemySpawnRule(Goblin::new, 1));
        allEnemyTypes.add(new EnemySpawnRule(Slime::new, 1));
        allEnemyTypes.add(new EnemySpawnRule(Skull::new, 2));
        allEnemyTypes.add(new EnemySpawnRule(Spider::new, 3));
        allEnemyTypes.add(new EnemySpawnRule(Vampire::new, 5));
    }

    private void registerAudioAssets() {
        AudioManager am = AudioManager.getInstance();
        am.registerSound("VICTORY_MUSIC_1", "/Assets/Audio/SFX/victory_sound_1.wav");
        am.registerSound("BGM_FOREST", "/Assets/Audio/SFX/BattleBGM/battle_bgm.wav");
        am.registerSound("BGM_BOSS", "/Assets/Audio/SFX/BattleBGM/battleboss_bgm.wav");
        am.registerSound("MAIN-THEME", "/Assets/Audio/SFX/MainUI/mainMenu_bgm.wav");
        am.registerSound("BUTTON_HOVER", "/Assets/Audio/SFX/MainUI/button_hover.wav");
        am.registerSound("BUTTON_SELECT", "/Assets/Audio/SFX/MainUI/button_select.wav");
        am.registerSound("STORYVIEW", "Assets/Audio/SFX/StoryView/storyview_bgm.wav");
    }

    private String getMusicForLevel(int levelNum) {
        if (levelNum == 15 || levelNum == 20) return "BGM_BOSS";
        return "BGM_FOREST";
    }

    private String getBackgroundForLevel(int levelNum) {
        List<String> bg_List = new ArrayList<>();
        bg_List.add("Assets/Images/Backgrounds/Level_BG/sample.jpg");
        bg_List.add("Assets/Images/Backgrounds/Level_BG/Forest_Biome/sprite_0.png");
        bg_List.add("Assets/Images/Backgrounds/Level_BG/Snow_Biome/sprite_0.png");
        bg_List.add("Assets/Images/Backgrounds/Level_BG/Dungeon_Biome/sprite_0.png");

        int index = (levelNum - 1) / 5 % bg_List.size();
        return bg_List.get(index);
    }

    public static List<StorySlide> loadIntroSequence() {
        return StoryContent.getIntroSequence();
    }

    public void finishCampaign() {
        LogManager.log("You have cleared all stages!", LogFormat.VICTORY);
    }

    public long getCurrentSeed() {
        return currentSeed;
    }

    public void setBattleInterface(BattleInterface battleInterface) {
        this.battleInterface = battleInterface;
    }
}