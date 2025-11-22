package Characters;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

public class Party {
    private String partyName;
    private List<Character> partyMembers;
    private final Random random;

    public String getPartyName() {
        return partyName;
    }

    public Party(String partyName) {
        this.partyName = partyName;
        this.partyMembers = new ArrayList<>();
        this.random = new Random();
    }

    public void addPartyMember(Character member) {
        // TODO: add Logging for character joining the party
        if (member != null) {
            this.partyMembers.add(member);
            System.out.println(member.getName() + " joined " + partyName + "!");
        }
    }

    public List<Character> getPartyMembers() {
        return partyMembers;
    }

    public boolean removePartyMember(Character member) {
        // TODO: add Logging for character getting removed from the party!
        boolean removed = this.partyMembers.remove(member);
        if (removed) {
            System.out.println(member.getName() + " was removed from " + partyName + "!");
        }
        return removed;
    }

    public Character getPartyMember(String name) {
        for (Character character : partyMembers) {
            if (character.getName().equalsIgnoreCase(name))
                return character;
        }
        return null;
    }

    public Character getRandomAliveMember() {
        if (this.partyMembers.isEmpty())
            return null;

        List<Character> aliveMembers = this.partyMembers.stream()
                .filter(Character::isAlive)
                .toList();

        if (aliveMembers.isEmpty())
            return null;

        int index = random.nextInt(aliveMembers.size());
        return aliveMembers.get(index);
    }

    public Character getRandomDeadMember() {
        if (this.partyMembers.isEmpty())
            return null;

        List<Character> deadMembers = this.partyMembers.stream()
                .filter(new Predicate<Character>() {
                    @Override
                    public boolean test(Character character) {
                        return !character.isAlive;
                    }
                }).toList();

        if (deadMembers.isEmpty())
            return null;

        int index = random.nextInt(deadMembers.size());
        return deadMembers.get(index);
    }

//   TODO: Front end party info display, might not be in this class
//    public void getPartyInfo()
}
