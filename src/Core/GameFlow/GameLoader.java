package Core.GameFlow;

import Abilities.Jobs.*;
import Characters.Base.Enemy;
import Characters.Base.Hero;
import Characters.Character;
import Characters.Enemies.*;
import Characters.Party;
import Core.GameManager;
import Core.Story.StorySlide;
import Core.Utils.Dice;
import Resource.Audio.AudioManager;
import Core.Utils.LogManager;
import Core.Utils.LogFormat;
import Items.*;

import java.sql.Array;
import java.util.*;
import java.util.function.Function;

/**
 * Loads and saves levels
 */
public class GameLoader {
    private Queue<Level> campaignQueue;
    private long currentSeed;
    private int levelsCompleted;
    private final List<EnemySpawnRule> allEnemyTypes = new ArrayList<>();

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
                "Assets/Images/Backgrounds/sample.jpg",
                "A swarm of goblins surround you!",
                buildEnemyGroup(Goblin::new, Goblin::new),
                buildLoot(
                        ItemFactory.smallHealthPotion(),
                        ItemFactory.smallManaPotion()
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

    // TODO: Link up with character creation later!!!
    // TODO: optimize character creation using factories (maybe)
    public static Party loadStarterParty(String partyName) {
        Party heroParty = new Party(partyName);

        /*
         *TEMPORARY HERO SETUP
        heroParty = new Party("The Godslayers");

        Warrior warrior = new Warrior();
        Character charlie = new Hero("Charlie",150,50,100,1,warrior);

        Paladin paladin = new Paladin();
        Character antot = new Hero("Antot",150,50,100,1,paladin);

        Rogue rogue = new Rogue();
        Character elyi = new Hero("Ely",80,50,100,1,rogue);

        FireMage fireMage = new FireMage();
        Character chaniy = new Hero("Chaniy the doubter",100,60,120,1,fireMage);

        CryoMancer iceMage = new CryoMancer();
        Character sammy = new Hero("Sammy", 100, 60, 120, 1, iceMage);

        EarthMage earthMage = new EarthMage();
        Character ythan = new Hero("Ythanny W", 100, 60, 120, 1, earthMage);

        Cleric cleric = new Cleric();
        Character erick = new Hero("Erick the cleric", 100, 60, 120, 1, cleric);

        Archer archer = new Archer();
        Character gianmeni = new Hero("Gian Meni",80,70,100,1,archer);

        AeroMancer aeromancer = new AeroMancer();
        Character kervs = new Hero("Kurtis", 100, 60, 120, 1, aeromancer);

//        TODO: add max amount of party members
//        heroParty.addPartyMember(charlie);
//        heroParty.addPartyMember(ythan);
//        heroParty.addPartyMember(erick);
//        heroParty.addPartyMember(sammy);
//        heroParty.addPartyMember(gianmeni);
//        heroParty.addPartyMember(kervs);
        heroParty.addPartyMember(chaniy);
//        heroParty.addPartyMember(elyi);
//        heroParty.addPartyMember(antot);
*/

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

        String bg = "";
        List<String> bg_List = new ArrayList<String>();
        bg_List.add("Assets/Images/Backgrounds/Level_BG/Dungeon_Biome/sprite_0.png");
        bg_List.add("Assets/Images/Backgrounds/Level_BG/Forest_Biome/sprite_0.png");
        bg_List.add("Assets/Images/Backgrounds/Level_BG/Snow_Biome/sprite_0.png");
        bg = Dice.getInstance().pickRandom(bg_List);
//        for(int i = 0; i < 20 ; i++){
//            int separator = (i - 2) / 5;
//            bg = bg_List.get(separator % bg_List.size());
//        }

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
                "Assets/Images/Backgrounds/forest_bg.png",
                List.of(
                        "In the mythic land of ELDORIA, four elemental forces weave the fate of mortals.",
                        "Fire, wind, earth, and water...",
                        "These primal powers have always felt like magic."
                ),
                null
        ));

        // Slide 2: The Peaceful Era
        slides.add(new StorySlide(
                // Image should be like a city pwede ra we call it Avendale City
                "Assets/Images/Backgrounds/forest_bg.png",
                List.of(
                        "For generations, the great kingdoms knew peace. The Empire of AVENDALE stood strong at the center,",
                        "while elf academies and dwarf clans thrived. But even the calmest days whisper of storms ahead..."
                ),
                null
        ));

        // Slide 3: The Corruption Begins
        slides.add(new StorySlide(
                // Image should be the Blackspire Mountain
                "Assets/Images/Backgrounds/forest_bg.png",
                List.of(
                        "The peace ended when VAROTH, a court mage, ascended the BLACKSPIRE peak.",
                        "He communed with an ancient evil and returned as DREADLORD VAROTH."
                ),
                null
        ));

        // Slide 4: The Darkness Spreads
        slides.add(new StorySlide(
                // Image should be the Corrupted Forest
                "Assets/Images/Backgrounds/forest_bg.png",
                List.of(
                        "Now, a pall of dread covers the land. Forests wilt under unnatural frost.",
                        "Fields lie fallow beneath a cursed blight. Goblins, orcs, and undead prowl the borderlands."
                ),
                null
        ));

        // Slide 5: The Prophecy
        slides.add(new StorySlide(
                // Image should be the ancient scroll.
                "Assets/Images/Backgrounds/forest_bg.png",
                List.of(
                        "Yet an ancient prophecy endures. It speaks of a FOURFOLD PARTY:",
                        "• A WARRIOR, shield of the land",
                        "• An ARCHER, swift as the wind",
                        "• A CLERIC, healer and protector",
                        "• A MAGE, master of four elements"
                ),
                null
        ));

        // Slide 6: The Call to Action
        slides.add(new StorySlide(
                "Assets/Images/Backgrounds/forest_bg.png",
                List.of(
                        "Only when these four stand together can the balance of Eldoria be restored.",
                        "But first, they must brave the heart of darkness. The corrupted dungeons beneath Blackspire itself."
                ),
                null
        ));

        // Slide 7: The Descent Begins
        slides.add(new StorySlide(
                "Assets/Images/Backgrounds/forest_bg.png",
                List.of(
                        "The path now leads into ancient, twisted passages where dark magic chokes the air.",
                        "Every shadowed corridor is a trial, every chamber, a test of unity and strength."
                ),
                null
        ));

        // Slide 8: The Final Challenge
        slides.add(new StorySlide(
                "Assets/Images/Backgrounds/forest_bg.png",
                List.of(
                        "At the peak, in an obsidian throne room, Dreadlord Varoth awaits.",
                        "Victory depends on unity, strategy, and the combined gifts of the Fourfold Party."
                ),
                null
        ));

        // Slide 9: The Player's Role
        slides.add(new StorySlide(
                "Assets/Images/Backgrounds/forest_bg.png",
                List.of(
                        "Now, the fate of Eldoria rests with you. WHO WILL STAND AGAINST THE DARKNESS?",
                        "Form your party and become the prophecy."
                ),
                () ->  GameManager.getInstance().showCharacterSelectionScreen()
        ));


//        slides.add(new StorySlide(
//                "Assets/Images/Backgrounds/volcano_bg.png",
//                List.of(
//                        "now...",
//                        "leZGOOOO"
//                ),
//                null
//        ));

        return slides;
    }

    public Level loadNextLevel() {
        return campaignQueue.poll();
    }

    // TODO: end level
    public void finishCampaign() {
        LogManager.logHighlight("CAMPAIGN COMPLETE!", LogFormat.VICTORY, LogFormat.SIZE_HEADER, true);
        LogManager.log("You have cleared all stages!", LogFormat.VICTORY);
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
}
