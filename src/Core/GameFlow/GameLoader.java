package Core.GameFlow;

import Abilities.Jobs.*;
import Characters.Base.Enemy;
import Characters.Base.Hero;
import Characters.Character;
import Characters.Enemies.Goblin;
import Characters.Enemies.Slime;
import Characters.Enemies.Spider;
import Characters.Enemies.Vampire;
import Characters.Party;
import Core.Utils.Dice;
import Resource.Audio.AudioManager;
import Core.Utils.LogManager;
import Core.Utils.LogFormat;
import Items.*;

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
                // TODO: this is gauranteed, not
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
                levelNum,
                name,
                intro,
                bgKey,
                enemies,
                enemies.size(),
                enemies.size(),
                loot,
                100 * levelNum,
                0 // fixed seed
        );
    }

    // TODO: Link up with character creation later!!!
    // TODO: optimize character creation using factories (maybe)
    public static Party loadStarterParty() {
        Party heroParty = new Party("The Godslayers");

        //TEMPORARY HERO SETUP
        heroParty = new Party("The Godslayers");
        Warrior warrior = new Warrior();
        Characters.Character charlie = new Hero("Charlie",150,50,100,1,warrior,"WARRIOR_IDLE"){
            @Override
            protected void onDeath() {
                super.onDeath();

            }

            @Override
            protected void onDefeat(Characters.Character defeatedTarget) {

            }
        };
        Paladin paladin = new Paladin();
        Characters.Character antot = new Hero("Antot",150,50,100,1,paladin,"PALADIN_IDLE"){
            @Override
            protected void onDeath() {

            }

            @Override
            protected void onDefeat(Characters.Character defeatedTarget) {

            }
        };
        Rogue rogue = new Rogue();
        Characters.Character elyi = new Hero("Ely",80,50,100,1,rogue,"ROGUE_IDLE"){
            @Override
            protected void onDeath() {

            }

            @Override
            protected void onDefeat(Characters.Character defeatedTarget) {

            }
        };

        FireMage fireMage = new FireMage();
        Characters.Character chaniy = new Hero("Chaniy the doubter",100,60,120,1,fireMage,"MAGE_IDLE"){
            @Override
            protected void onDeath() {

            }

            @Override
            protected void onDefeat(Characters.Character defeatedTarget) {

            }
        };

        CryoMancer iceMage = new CryoMancer();
        Characters.Character sammy = new Hero("Sammy", 100, 60, 120, 1, iceMage, "MAGE_ICE-IDLE") {
            @Override
            protected void onDeath() {

            }

            @Override
            protected void onDefeat(Characters.Character defeatedTarget) {

            }
        };

        EarthMage earthMage = new EarthMage();
        Characters.Character ythan = new Hero("Ythanny W", 100, 60, 120, 1, earthMage, "MAGE_EARTH-IDLE") {
            @Override
            protected void onDeath() {

            }

            @Override
            protected void onDefeat(Characters.Character defeatedTarget) {

            }
        };

        Cleric cleric = new Cleric();
        Characters.Character erick = new Hero("Erick the cleric", 100, 60, 120, 1, cleric, "CLERIC_IDLE") {
            @Override
            protected void onDeath() {

            }

            @Override
            protected void onDefeat(Characters.Character defeatedTarget) {

            }
        };

        Archer archer = new Archer();
        Characters.Character gianmeni = new Hero("Gian Meni",80,70,100,1,archer,"ARCHER_IDLE"){
            @Override
            protected void onDeath() {

            }

            @Override
            protected void onDefeat(Characters.Character defeatedTarget) {

            }
        };

        AeroMancer aeoroMancer = new AeroMancer();
        Characters.Character kervs = new Hero("Kurtis", 100, 60, 120, 1, aeoroMancer, "MAGE_WIND-IDLE"){
            @Override
            protected void onDeath() {

            }

            @Override
            protected void onDefeat(Character defeatedTarget) {

            }
        };

//        TODO: add max amount of party members
        heroParty.addPartyMember(charlie);
//        heroParty.addPartyMember(ythan);
        heroParty.addPartyMember(erick);
//        heroParty.addPartyMember(sammy);
//        heroParty.addPartyMember(gianmeni);
//        heroParty.addPartyMember(kervs);
//        heroParty.addPartyMember(chaniy);
        heroParty.addPartyMember(elyi);
//        heroParty.addPartyMember(antot);

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
                levelSeed
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
//                levelSeed
//        );
//    }

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
