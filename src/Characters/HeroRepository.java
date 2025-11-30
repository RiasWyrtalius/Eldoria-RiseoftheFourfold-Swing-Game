package Characters;

import Abilities.Jobs.*;
import Characters.Base.Hero;
import java.util.ArrayList;
import java.util.List;

public class HeroRepository {

    public static List<CharacterDisplayData> getHeroes() {
        List<CharacterDisplayData> list = new ArrayList<>();

        list.add(new CharacterDisplayData(new Hero("Charlie", 150, 50, 100, 1, new Warrior(), "WARRIOR_IDLE")));
        list.add(new CharacterDisplayData(new Hero("Chaniy", 100, 60, 120, 1, new FireMage(), "MAGE_IDLE")));
        list.add(new CharacterDisplayData(new Hero("Sammy", 100, 60, 120, 1, new CryoMancer(), "MAGE_ICE-IDLE")));
        list.add(new CharacterDisplayData(new Hero("Ythan", 100, 60, 120, 1, new EarthMage(), "MAGE_EARTH-IDLE")));
        list.add(new CharacterDisplayData(new Hero("Erick", 100, 60, 120, 1, new Cleric(), "CLERIC_IDLE")));
        list.add(new CharacterDisplayData(new Hero("Gian", 80, 70, 100, 1, new Archer(), "ARCHER_IDLE")));
        list.add(new CharacterDisplayData(new Hero("Kervs", 100, 60, 120, 1, new AeroMancer(), "MAGE_WIND-IDLE")));
        list.add(new CharacterDisplayData(new Hero("Antot", 150, 50, 100, 1, new Paladin(), "PALADIN_IDLE")));
        list.add(new CharacterDisplayData(new Hero("Ely", 80, 50, 100, 1, new Rogue(), "MAGE_IDLE")));

        return list;
    }
}