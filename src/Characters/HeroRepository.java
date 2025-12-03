package Characters;

import Abilities.Jobs.*;
import Characters.Base.Hero;
import java.util.ArrayList;
import java.util.List;

public class HeroRepository {

    public static List<CharacterDisplayData> getHeroes() {
        List<CharacterDisplayData> list = new ArrayList<>();

        list.add(new CharacterDisplayData(new Hero("Charlie", 150, 50, 100, 1, new Warrior())));
        list.add(new CharacterDisplayData(new Hero("Chaniy", 100, 60, 120, 1, new FireMage())));
        list.add(new CharacterDisplayData(new Hero("Sammy", 100, 60, 120, 1, new CryoMancer())));
        list.add(new CharacterDisplayData(new Hero("Ythan", 100, 60, 120, 1, new EarthMage())));
        list.add(new CharacterDisplayData(new Hero("Erick", 100, 60, 120, 1, new Cleric())));
        list.add(new CharacterDisplayData(new Hero("Gian", 80, 70, 100, 1, new Archer())));
        list.add(new CharacterDisplayData(new Hero("Kervs", 100, 60, 120, 1, new AeroMancer())));
        list.add(new CharacterDisplayData(new Hero("Antot", 150, 50, 100, 1, new Paladin())));
        list.add(new CharacterDisplayData(new Hero("Ely", 80, 50, 100, 1, new Rogue())));

        return list;
    }
}