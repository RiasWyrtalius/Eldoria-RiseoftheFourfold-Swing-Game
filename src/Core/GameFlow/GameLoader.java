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
        List<StorySlide> preCutsceneFirstLevel = new ArrayList<>();
        preCutsceneFirstLevel.add(new StorySlide(
                "/Assets/Images/Backgrounds/eldoria_map.png",
                List.of(
                        "At the edge of the wilds, the first champion steps forward. Alone, yet bound by prophecy, the journey into corruption begins."
                )
        ));
        List<StorySlide> postCutsceneFirstLevel = new ArrayList<>();
        postCutsceneFirstLevel.add(new StorySlide(
                "/Assets/Images/Backgrounds/avendale_city.png",
                List.of(
                        "The goblins fall. The land exhales. Your strength grows — and somewhere in Eldoria, another hero takes notice."
                )
        ));

//        fixedLevels.put(1, createSpecificLevel(
//                1,
//                "The Gates",
//                "/Assets/Images/Backgrounds/Level_BG/Forest_Biome/sprite_0.png",
//                "A swarm of goblins blocks your path!",
//                buildEnemyGroup((_) -> new Varoth(1)),
//                buildLoot(
//                        ItemFactory.smallHealthPotion(),
//                        ItemFactory.smallManaPotion(),
//                        ItemFactory.summoningScroll()
//                ),
//                preCutsceneFirstLevel,postCutsceneFirstLevel
//        ));
        // =========================================================

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
                )
        ));
        fixedLevels.put(15, createSpecificLevel(
                15,
                "The Acid and Rock",
                "/Assets/Images/Backgrounds/Level_BG/Forest_Biome/sprite_0.png",
                "Solid strength and shifting sludge",
                buildEnemyGroup(Slime::new,(_) -> new GolemBoss(3),Slime::new),
                buildLoot(
                        ItemFactory.smallHealthPotion(),
                        ItemFactory.smallHealthPotion(),
                        ItemFactory.smallManaPotion(),
                        ItemFactory.smallManaPotion(),
                        ItemFactory.mediumHealthPotion(),
                        ItemFactory.mediumManaPotion(),
                        ItemFactory.revivePotion()
                )
        ));
        fixedLevels.put(20, createSpecificLevel(
                20,
                "The Rage of rock and flame",
                "/Assets/Images/Backgrounds/Level_BG/Forest_Biome/sprite_0.png",
                "The final ascent begins. Fire and stone guard the path to destiny.",
                buildEnemyGroup((_) -> new GolemBoss(5),(_) -> new DragonBoss(5)),
                buildLoot(
                        ItemFactory.smallHealthPotion(),
                        ItemFactory.smallHealthPotion(),
                        ItemFactory.smallManaPotion(),
                        ItemFactory.smallManaPotion(),
                        ItemFactory.mediumHealthPotion(),
                        ItemFactory.mediumManaPotion(),
                        ItemFactory.revivePotion()
                )
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
                    data.xp,
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
