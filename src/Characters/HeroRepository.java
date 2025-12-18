package Characters;

import Abilities.Jobs.*;
import Characters.Base.Hero;
import java.util.ArrayList;
import java.util.List;

public class HeroRepository {

    public static List<CharacterDisplayData> getHeroes() {
        List<CharacterDisplayData> list = new ArrayList<>();

        list.add(new CharacterDisplayData(new Hero(
                "Warrior",
                150,
                50,
                18,
                100,
                1,
                new Warrior()
        )));

        list.add(new CharacterDisplayData(new Hero(
                "Fire Mage",
                100,
                60,
                5,
                120,
                1,
                new FireMage()
        )));

        list.add(new CharacterDisplayData(new Hero(
                "Cryomancer",
                100,
                60,
                12,
                120,
                1,
                new CryoMancer()
        )));

        list.add(new CharacterDisplayData(new Hero(
                "Earth Mage",
                100,
                60,
                5,
                120,
                1,
                new EarthMage()
        )));

        list.add(new CharacterDisplayData(new Hero(
                "Cleric",
                100,
                60,
                10,
                120,
                1,
                new Cleric()
        )));

        list.add(new CharacterDisplayData(new Hero(
                "Archer",
                80,
                70,
                8,
                100,
                1,
                new Archer()
        )));

        list.add(new CharacterDisplayData(new Hero(
                "Aeromancer",
                100,
                60,
                5,
                120,
                1,
                new AeroMancer()
        )));

        list.add(new CharacterDisplayData(new Hero(
                "Paladin",
                150,
                50,
                20,
                100,
                1,
                new Paladin()
        )));

        list.add(new CharacterDisplayData(new Hero(
                "Rogue",
                80,
                50,
                6,
                100,
                1,
                new Rogue()
        )));

        return list;
    }
}