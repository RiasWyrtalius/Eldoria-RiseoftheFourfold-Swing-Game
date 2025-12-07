package Core.GameFlow;

import Abilities.JobClass;
import Abilities.JobFactory;
import Characters.Base.Enemy;
import Characters.Base.Hero;
import Characters.Enemies.*;
import Characters.Party;
import Core.GameManager;
import Core.Story.StorySlide;
import Core.Utils.Dice;
import Resource.Audio.AudioManager;
import Core.Utils.LogManager;
import Core.Utils.LogFormat;
import Items.*;

import javax.swing.*;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import UI.Views.BattleInterface;

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

        fixedLevels.put(1, createSpecificLevel(
                1,
                "The Gates",
                "Assets/Images/Backgrounds/Level_BG/sample.jpg",
                "A swarm of goblins surround you!",
                buildEnemyGroup(Goblin::new,Goblin::new),
                buildLoot(
                        ItemFactory.smallHealthPotion(),
                        ItemFactory.smallManaPotion(),
                        ItemFactory.summoningScroll()
                )
        ));
        fixedLevels.put(5, createSpecificLevel(
                5,
                "The Coven",
                "Assets/Images/Backgrounds/Level_BG/Forest_Biome/sprite_0.png",
                "Two shadows peel from the darkness",
                buildEnemyGroup(Vampire::new, Vampire::new),
                buildLoot(
                        ItemFactory.smallHealthPotion(),
                        ItemFactory.smallHealthPotion(),
                        ItemFactory.smallManaPotion(),
                        ItemFactory.smallManaPotion(),
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
                "Assets/Images/Backgrounds/Level_BG/Forest_Biome/sprite_0.png",
                "The brood and the Coven",
                buildEnemyGroup(Vampire::new,Spider::new,Spider::new, Vampire::new),
                buildLoot(
                        ItemFactory.smallHealthPotion(),
                        ItemFactory.smallHealthPotion(),
                        ItemFactory.smallManaPotion(),
                        ItemFactory.smallManaPotion(),
                        ItemFactory.summoningScroll(),
                        ItemFactory.revivePotion()
                )
        ));
        fixedLevels.put(15, createSpecificLevel(
                15,
                "The Acid and Rock",
                "Assets/Images/Backgrounds/Level_BG/Forest_Biome/sprite_0.png",
                "Solid strength and shifting sludge",
                buildEnemyGroup(Slime::new,(_) -> new GolemBoss(3),Slime::new),
                buildLoot(
                        ItemFactory.smallHealthPotion(),
                        ItemFactory.smallHealthPotion(),
                        ItemFactory.smallManaPotion(),
                        ItemFactory.smallManaPotion(),
                        ItemFactory.revivePotion()
                )
        ));
        fixedLevels.put(20, createSpecificLevel(
                20,
                "The Rage of rock and flame",
                "Assets/Images/Backgrounds/Level_BG/Forest_Biome/sprite_0.png",
                "Wrath of sky and earth stops you",
                buildEnemyGroup((_) -> new GolemBoss(5),(_) -> new DragonBoss(5)),
                buildLoot(
                        ItemFactory.smallHealthPotion(),
                        ItemFactory.smallHealthPotion(),
                        ItemFactory.smallManaPotion(),
                        ItemFactory.smallManaPotion(),
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
                                      List<Function<Integer, Enemy>> enemies, List<Item> loot) {
        return new Level(
                levelNum, name, intro, bgKey,
                enemies,
                enemies.size(), enemies.size(),
                loot,
                100 * levelNum,
                0, // fixed seed
                true
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
                false
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
        List<StorySlide> slides = new ArrayList<>();

        // Slide 1: The World of Eldoria
        slides.add(new StorySlide(
                //Image should be Eldoria Map
                "Assets/Images/Backgrounds/eldoria_map.png",
                List.of(
                        "In the mythic land of ELDORIA, four elemental forces weave the fate of mortals. Fire, wind, earth, and water- these primal powers have always felt like magic."
                ),
                null
        ));

        // Slide 2: The Peaceful Era
        slides.add(new StorySlide(
                // Image should be like a city pwede ra we call it Avendale City
                "Assets/Images/Backgrounds/avendale_city.png",
                List.of(
                        "For generations, peace reigned in Avendale and beyond. But now, a shadow falls. The court mage VAROTH has turned to darkness. He has become DREADLORD VAROTH, and his corruption spreads."
                ),
                null
        ));

        // Slide 3: The Corruption Begins
        slides.add(new StorySlide(
                // Image should be the Blackspire Mountain
                "Assets/Images/Backgrounds/corrupted_forest.png",
                List.of(
                        "Forests wilt under unnatural frost. Fields lie fallow. Goblins, orcs, and undead now prowl freely. The kingdoms are divided, their champions scattered."
                ),
                null
        ));

        // Slide 4: The Darkness Spreads
        slides.add(new StorySlide(
                // Image should be the Corrupted Forest
                "Assets/Images/Backgrounds/ancient_scroll.png",
                List.of(
                        "In this dark hour, an ancient prophecy is remembered: when darkness ascends Blackspire's peak, FOURFOLD PARTY must unite to seek:",
                        "A WARRIOR, shield of the land. An ARCHER, swift as the wind. A CLERIC, healer and protector. and a MAGE, master of four elements. Alone they falter, together they fight."
                ),
                null
        ));

        // Slide 5: The Prophecy
        slides.add(new StorySlide(
                // Image should be the ancient scroll.
                "Assets/Images/Backgrounds/fourfolds_bg.png",
                List.of(
                        "But the four champions are scattered across Eldoria. The journey begins with ONE - the first to heed the call. Alone, you must brave the corrupted lands and find the others who share this fate."
                ),
                null
        ));

        // Slide 6: The Call to Action
        slides.add(new StorySlide(
                "Assets/Images/Backgrounds/fourfolds_bg.png",
                List.of(
                        "Your quest is twofold: First, survive the perils of the wilds and dungeons. Second, seek out the other prophesied heroes, earning their trust through shared trials."
                ),
                null
        ));

        // Slide 7: The Descent Begins
        slides.add(new StorySlide(
                "Assets/Images/Backgrounds/dungeon_entrance.png",
                List.of(
                        "Beneath Blackspire Mountain lies your proving ground: ancient dungeons twisted by Vargoth's magic. Here, you will grow stronger, level by level, and here, you will find allies worthy of the prophecy."
                ),
                null
        ));

        // Slide 8: The Final Challenge
        slides.add(new StorySlide(
                "Assets/Images/Backgrounds/throne_room.png",
                List.of(
                        "Only when the FOURFOLD PARTY is complete, each hero tested and tempered, can you ascend to the throne room above, and face Dreadlord Vargoth as one united force."
                ),
                null
        ));

        // Slide 9: The Player's Role
        slides.add(new StorySlide(
                "Assets/Images/Backgrounds/heroes.png",
                List.of(
                        "Your journey begins now. WHO WILL BE THE FIRST? Choose your starting hero wisely. The others will join you... in time.",
                        "Form your party and become the prophecy."
                ), null
        ));

        slides.add(new StorySlide(
                "Assets/Images/Backgrounds/heroes.png",
                List.of(
                        "Now, the fate of Eldoria rests with you."
                ),
                () -> {
                    BiConsumer<Hero, String> onCharacterPicked = (selectedHero, partyName) -> {
                        GameManager.getInstance().createPartyFromSelection(selectedHero, partyName);
                        GameManager.getInstance().closeOverlay();
                        GameManager.getInstance().startGameLoop();
                    };

                    GameManager.getInstance().showCharacterSelectionScreen(
                            CharacterSelectionMode.CREATE_NEW_PARTY,
                            onCharacterPicked
                    );
                }
        ));

//        slides.add(new StorySlide(
//                "Assets/Images/Backgrounds/heroes.png",
//                List.of(
//                        "Good luck."
//                ),
//                () -> {
//                    GameManager.getInstance().startGameLoop();
//                }
//        ));

        return slides;
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
        am.registerSound("VICTORY_MUSIC_1", "/Audio/SFX/victory_sound_1.wav");
    }
    
    // TODO: File handling stuff here
    public void saveGame() {
        // write to json or object buffer
        // save level completed
        // save current seed
    }

    public void loadGame() {
        // read seed
        // generateCampaign(seed)
        // pool
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
