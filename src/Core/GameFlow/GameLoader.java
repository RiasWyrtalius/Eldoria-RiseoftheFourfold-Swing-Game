package Core.GameFlow;

import Abilities.JobClass;
import Abilities.JobFactory;
import Characters.Base.Enemy;
import Characters.Base.Hero;
import Characters.Enemies.*;
import Characters.Party;
import Core.GameManager;
import Core.Story.StorySlide;
import Resource.Audio.AudioManager;
import Core.Utils.LogManager;
import Core.Utils.LogFormat;
import Items.*;

import javax.swing.*;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import UI.Views.BattleInterface;

import Core.Story.StoryContent;

/**
 * Loads and saves levels
 */
public class GameLoader {
    private final Queue<Level> campaignQueue;
    private long currentSeed;
    private int levelsCompleted;
    private final List<EnemySpawnRule> allEnemyTypes = new ArrayList<>();
    private BattleInterface battleInterface;

    public GameLoader() {
        this.campaignQueue = new LinkedList<>();
        registerAudioAssets();
        initializeAllEnemyTypes();
    }

    private record EnemySpawnRule(Function<Integer, Enemy> factory, int minLevel) {}

    private Map<Integer, Level> getPredefinedLevels() {
        Map<Integer, Level> fixedLevels = new HashMap<>();

        // =========================================================
        List<StorySlide> preCutsceneLevel1 = new ArrayList<>();
        preCutsceneLevel1.add(new StorySlide(
                "/Assets/Images/Backgrounds/eldoria_map.png",
                List.of(
                        "At the edge of the wilds, the first champion steps forward. Alone, yet bound by prophecy, the journey into corruption begins."
                )
        ));

        List<StorySlide> postCutsceneLevel1 = new ArrayList<>();
        postCutsceneLevel1.add(new StorySlide(
                "/Assets/Images/CutsceneImages/Floor1/Floor1_c1.png",
                List.of(
                        "The goblins fall. The land exhales. Your strength grows — and somewhere in Eldoria,"
                )
        ));
        postCutsceneLevel1.add(new StorySlide(
                "/Assets/Images/CutsceneImages/Floor1/Floor1_c2.png",
                List.of(
                        "another hero takes notice. Drawn by fate, a cloaked champion steps from the shadows, waiting for the one who will summon them."
                )
        ));

        fixedLevels.put(1, createSpecificLevel(
                1,
                "The Gates",
                "/Assets/Images/Backgrounds/Level_BG/Forest_Biome/sprite_0.png",
                "A swarm of goblins blocks your path!",
                buildEnemyGroup(Goblin::new,Goblin::new),
                buildLoot(
                        ItemFactory.smallHealthPotion(),
                        ItemFactory.smallManaPotion(),
                        ItemFactory.summoningScroll()
                ),
                preCutsceneLevel1,
                postCutsceneLevel1
        ));
        // =========================================================

        List<StorySlide> preCutsceneLevel5 = new ArrayList<>();
        preCutsceneLevel5.add(new StorySlide(
                "/Assets/Images/CutsceneImages/Floor5/Floor5_c1pre.png",
                List.of(
                        "Deep in the forest, ancient magic stirs — a new hero senses your rise, awaiting the scroll that will summon them."
                )
        ));

        List<StorySlide> postCutsceneLevel5 = new ArrayList<>();
        postCutsceneLevel5.add(new StorySlide(
                "/Assets/Images/CutsceneImages/Floor5/Floor5_c1.png",
                List.of(
                        "The forest stills. The vampires fade to crimson dust, drawn into the roots beneath your feet."
                )
        ));
        postCutsceneLevel5.add(new StorySlide(
                "/Assets/Images/CutsceneImages/Floor5/Floor5_c2.png",
                List.of(
                        "From the shadows, a hidden hunter watches… as spiders creep toward the same unseen call."
                )
        ));
        fixedLevels.put(5, createSpecificLevel(
                5,
                "The Coven",
                "/Assets/Images/Backgrounds/Level_BG/Forest_Biome/sprite_0.png",
                "Whispers spread through the forest. A hunter stalks the same darkness you fight.",
                buildEnemyGroup(Vampire::new, Vampire::new),
                buildLoot(
                        ItemFactory.smallHealthPotion(),
                        ItemFactory.smallHealthPotion(),
                        ItemFactory.smallManaPotion(),
                        ItemFactory.smallManaPotion(),
                        ItemFactory.mediumHealthPotion(),
                        ItemFactory.mediumManaPotion(),
                        ItemFactory.splashHealthPotion(),
                        ItemFactory.splashHealthPotion(),
                        ItemFactory.splashManaPotion(),
                        ItemFactory.splashManaPotion(),
                        ItemFactory.summoningScroll()
                ),
                preCutsceneLevel5,
                postCutsceneLevel5
        ));
        // =========================================================
        List<StorySlide> preCutsceneLevel10 = new ArrayList<>();
        preCutsceneLevel10.add(new StorySlide(
                "/Assets/Images/CutsceneImages/Floor10/Floor10_c1pre.png",
                List.of(
                        "The forest grows restless — threads of web and whispers of fang weave into a single rising threat."
                )
        ));

        List<StorySlide> postCutsceneLevel10 = new ArrayList<>();
        postCutsceneLevel10.add(new StorySlide(
                "/Assets/Images/CutsceneImages/Floor10/Floor10_c1.png",
                List.of(
                        "Beneath the trees, spiders swarm toward a pulsing glow in the roots, answered by lingering vampiric ash."
                )
        ));
        postCutsceneLevel10.add(new StorySlide(
                "/Assets/Images/CutsceneImages/Floor10/Floor10_c2.png",
                List.of(
                        "What erupts here will soon take shape — the horrors awaiting you deeper in the wild."
                )
        ));

        fixedLevels.put(10, createSpecificLevel(
                10,
                "The Critters and Fangs",
                "/Assets/Images/Backgrounds/Level_BG/Forest_Biome/sprite_0.png",
                "The brood gathers.",
                buildEnemyGroup(Vampire::new,Spider::new,Spider::new, Vampire::new),
                buildLoot(
                        ItemFactory.smallHealthPotion(),
                        ItemFactory.smallHealthPotion(),
                        ItemFactory.smallManaPotion(),
                        ItemFactory.smallManaPotion(),
                        ItemFactory.mediumHealthPotion(),
                        ItemFactory.mediumManaPotion(),
                        ItemFactory.summoningScroll(),
                        ItemFactory.revivePotion()
                ),
                preCutsceneLevel10,
                postCutsceneLevel10
        ));
        // =========================================================
        List<StorySlide> preCutsceneLevel15 = new ArrayList<>();
        preCutsceneLevel10.add(new StorySlide(
                "/Assets/Images/CutsceneImages/Floor15/Floor15_c1pre.png",
                List.of(
                        "The ground that once trembled now swells and splits — corruption rising in sludge and stone."
                )
        ));

        List<StorySlide> postCutsceneLevel15 = new ArrayList<>();
        postCutsceneLevel15.add(new StorySlide(
                "/Assets/Images/CutsceneImages/Floor15/Floor15_c1.png",
                List.of(
                        "The corrupted sludge recoils into the cracked earth, its twisted glow fading as the forest exhales in relief."
                )
        ));
        postCutsceneLevel15.add(new StorySlide(
                "/Assets/Images/CutsceneImages/Floor15/Floor15_c2.png",
                List.of(
                        "The fallen golem collapses to dust, revealing deeper fissures below — something colossal stirs in the depths, feeding on the corruption that remains."
                )
        ));

        fixedLevels.put(15, createSpecificLevel(
                15,
                "The fire and Rock",
                "/Assets/Images/Backgrounds/Level_BG/Forest_Biome/sprite_0.png",
                "Solid strength and shifting sludge",
                buildEnemyGroup((_) -> new GolemBoss(3),(_) -> new DragonBoss(3)),
                buildLoot(
                        ItemFactory.smallHealthPotion(),
                        ItemFactory.smallHealthPotion(),
                        ItemFactory.smallManaPotion(),
                        ItemFactory.smallManaPotion(),
                        ItemFactory.mediumHealthPotion(),
                        ItemFactory.mediumManaPotion(),
                        ItemFactory.revivePotion()
                ),
                preCutsceneLevel15,
                postCutsceneLevel15
        ));
        // =========================================================
        List<StorySlide> preCutsceneLevel20 = new ArrayList<>();
        preCutsceneLevel20.add(new StorySlide(
                "/Assets/Images/CutsceneImages/Floor20/Floor20_c1pre.png",
                List.of(
                        "From the deepest fissures, a presence awakens — purple flame twisting through rising stone, as a colossal shadow climbs toward the surface."
                )
        ));

        List<StorySlide> postCutsceneLevel20 = new ArrayList<>();
        postCutsceneLevel20.add(new StorySlide(
                "/Assets/Images/CutsceneImages/Floor20/Floor20_c1.png",
                List.of(
                        "Varoth’s inferno collapses inward, the purple flames sputtering out as the last pulse of corruption drains into the earth."
                )
        ));
        postCutsceneLevel20.add(new StorySlide(
                "/Assets/Images/CutsceneImages/Floor20/Floor20_c2.png",
                List.of(
                        "Silence settles over the ruin; the fissures cool, embers fade, and the forest stands still — free at last from the shadow that once devoured its light."
                )
        ));

        fixedLevels.put(20, createSpecificLevel(
                20,
                "The Rage of rock and flame",
                "/Assets/Images/Backgrounds/Level_BG/Forest_Biome/sprite_0.png",
                "The final ascent begins. Fire and stone guard the path to destiny.",
                buildEnemyGroup((_) -> new Varoth(15)),
                buildLoot(
                        ItemFactory.smallHealthPotion(),
                        ItemFactory.smallHealthPotion(),
                        ItemFactory.smallManaPotion(),
                        ItemFactory.smallManaPotion(),
                        ItemFactory.mediumHealthPotion(),
                        ItemFactory.mediumManaPotion(),
                        ItemFactory.revivePotion()
                ),
                preCutsceneLevel20,
                postCutsceneLevel20
        ));
        return fixedLevels;
    }

    @SafeVarargs
    private List<Function<Integer, Enemy>> buildEnemyGroup(Function<Integer, Enemy>... generators) {
        return new ArrayList<>(Arrays.asList(generators));
    }

    private List<Item> buildLoot(Item... items) {
        return new ArrayList<>(Arrays.asList(items));
    }

    private Level createSpecificLevel(int levelNum, String name, String bgKey, String intro,
                                      List<Function<Integer, Enemy>> enemies, List<Item> loot, List<StorySlide> preLevelCutscene, List<StorySlide> postLevelCutscene) {
        return new Level(
                levelNum, name, intro, bgKey,
                enemies,
                enemies.size(), enemies.size(),
                loot,
                100 * levelNum,
                0, // fixed seed
                true,
                preLevelCutscene, postLevelCutscene
        );
    }

    private Level createSpecificLevel(int levelNum, String name, String bgKey, String intro,
                                      List<Function<Integer, Enemy>> enemies, List<Item> loot) {
        return new Level(
                levelNum, name, intro, bgKey,
                enemies,
                enemies.size(), enemies.size(),
                loot,
                100 * levelNum,
                0, // fixed seed
                true,
                null, null
        );
    }

    /**
     *  Reconstructs the entire party from save state
     * @param state state from savefile
     * @return party
     */
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

            // TODO: what about xp ??
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
            String itemName = entry.getKey();
            int qty = entry.getValue();

            Item item = ItemFactory.getItemByName(itemName);

            if (item != null) {
                inv.addItem(item, qty);
            }
        }
    }

    // TODO: Link up with character creation later!!!
    // TODO: optimize character creation using factories (maybe)
    public static Party createInitialParty(String partyName) {
        Party heroParty = new Party(partyName);

//        heroParty.addPartyMember(new Hero("Charlie",150,50,100,1, JobFactory.getJob("Warrior")));
//        heroParty.addPartyMember(new Hero("Ythanny W", 100, 60, 120, 1, JobFactory.getJob("EarthMage")));
//        heroParty.addPartyMember(new Hero("Erick the cleric", 100, 60, 120, 1, JobFactory.getJob("Cleric")));
//        heroParty.addPartyMember(new Hero("Sammy", 100, 60, 120, 1, JobFactory.getJob("CryoMancer")));
//        heroParty.addPartyMember(new Hero("Gian Meni",80,70,100,1, JobFactory.getJob("Archer")));
//        heroParty.addPartyMember(new Hero("Kurtis", 100, 60, 120, 1, JobFactory.getJob("AeroMancer")));
//        heroParty.addPartyMember(new Hero("Chaniy the doubter",100,60,120,1, JobFactory.getJob("Fire Mage")));
//        heroParty.addPartyMember(new Hero("Ely",80,50,100,1, JobFactory.getJob("Rogue")));
//        heroParty.addPartyMember(new Hero("Antot",150,50,100,1, JobFactory.getJob("Paladin")));

        loadStartingInventory(heroParty);
        return heroParty;
    }

    private static void loadStartingInventory(Party party) {
        party.getInventory().addItem(ItemFactory.revivePotion(), 1);
        party.getInventory().addItem(ItemFactory.smallHealthPotion(), 3);
    }

    // TODO: hybrid procedural generation of levels according to seed
    public void generateCampaign(long seed, int totalLevels) {
        this.currentSeed = seed;
        this.campaignQueue.clear();

        Random campaignRng = new Random(seed);

        LogManager.log("Generating Campaign with Seed: " + seed, LogFormat.SYSTEM);

        Map<Integer, Level> fixedLevels = getPredefinedLevels();

        for (int i = 1; i <= totalLevels; i++) {
            // generates a unique seed for this level
            long uniqueLevelSeed = campaignRng.nextLong();

            if (fixedLevels.containsKey(i)) {
                campaignQueue.add(fixedLevels.get(i));
                LogManager.log("Loaded Fixed Level: " + i);
            }
//            else if (i % 5 == 0) {
//                campaignQueue.add(generateRandomBossLevel(campaignRng, i, uniqueLevelSeed));
//            }
            else {
                campaignQueue.add(generateRandomMobLevel(campaignRng, i, uniqueLevelSeed));
            }
        }
    }

    private Level generateRandomMobLevel(Random rng, int levelNum, long levelSeed) {
        LogManager.log("GENERATING LEVEL " + levelNum);
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

        // TODO: ADD MORE VARIETY

        String bg = getBackgroundForLevel(levelNum);

        // TODO: add cutscenes???
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
                false,null,null
        );

    }

    private List<Item> generateRandomLoot(Random rng, int level) {
        return LootManager.getInstance().generateLoot(rng, level);
    }

//    private Level generateRandomBossLevel(Random rng, int levelNum, long levelSeed) {
//        List<Function<Integer, Enemy>> generators = new ArrayList<>();
//
//        // Procedural Boss (Scale stats heavily by level)
//        generators.add((lvl) -> new Boss(
//                "Warlord of Floor " + lvl,
//                400 + (lvl * 60), // High HP Scaling
//                30 + (lvl * 5),   // Atk Scaling
//                100, lvl, "Boss",
//                1000, 1.5
//        ));
//
//        return new Level(
//                levelNum,
//                "Stage " + levelNum + ": Boss",
//                "A powerful presence fills the room...",
//                "Assets/Images/Backgrounds/boss_room.jpg",
//                generators,
//                1, 1, // Only 1 boss
//                generateRandomLoot(rng, levelNum * 2), // Better loot
//                500 * levelNum,
//                levelSeed,
//                false
//        );
//    }

    public static List<StorySlide> loadIntroSequence() {
        return StoryContent.getIntroSequence();
    }

    public void finishCampaign() {
        LogManager.log("You have cleared all stages!", LogFormat.VICTORY);
    }

    public Level loadNextLevel() {
        return campaignQueue.poll();
    }

    // TODO: Refine this stuff
    private void initializeAllEnemyTypes() {
        allEnemyTypes.add(new EnemySpawnRule(Goblin::new,1));
        allEnemyTypes.add(new EnemySpawnRule(Slime::new,1));
        allEnemyTypes.add(new EnemySpawnRule(Skull::new, 2));
        allEnemyTypes.add(new EnemySpawnRule(Spider::new, 3));
        allEnemyTypes.add(new EnemySpawnRule(Vampire::new, 5));
    }

    private void registerAudioAssets() {
        AudioManager am = AudioManager.getInstance();
        am.registerSound("VICTORY_MUSIC_1", "/Assets/Audio/SFX/victory_sound_1.wav");
    }

    public long getCurrentSeed() {
        return currentSeed;
    }

    public void setBattleInterface(BattleInterface battleInterface) {
        this.battleInterface = battleInterface;
    }
    private String getBackgroundForLevel(int levelNum) {
        List<String> bg_List = new ArrayList<>();
        bg_List.add("Assets/Images/Backgrounds/Level_BG/sample.jpg");
        bg_List.add("Assets/Images/Backgrounds/Level_BG/Forest_Biome/sprite_0.png");
        bg_List.add("Assets/Images/Backgrounds/Level_BG/Snow_Biome/sprite_0.png");
        bg_List.add("Assets/Images/Backgrounds/Level_BG/Dungeon_Biome/sprite_0.png");

        // Determine which background index to use
        int index = (levelNum - 1) / 5 % bg_List.size();
        return bg_List.get(index);
    }
}
