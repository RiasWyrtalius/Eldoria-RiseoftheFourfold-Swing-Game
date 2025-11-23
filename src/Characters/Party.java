package Characters;

import Core.LogManager;

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
        if (member != null) {
            this.partyMembers.add(member);
            LogManager.log(member.getName() + " joined " + partyName + "!");
        }
    }

    public List<Character> getPartyMembers() {
        return partyMembers;
    }

    public boolean removePartyMember(Character member) {
        boolean removed = this.partyMembers.remove(member);
        if (removed) {
            LogManager.log(member.getName() + " was removed from " + partyName + "!");
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

    public List<Character> getAliveMembers() {
        if (this.partyMembers.isEmpty())
            return null;

        List<Character> aliveMembers = this.partyMembers.stream()
                .filter(Character::isAlive)
                .toList();

        if (aliveMembers.isEmpty())
            return null;

        return aliveMembers;
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

    public List<Character> getDeadMembers() {
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

        return deadMembers;
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

    public boolean isAllMembersExhausted() {
        return partyMembers.stream()
                .filter(Character::isAlive)
                .allMatch(Character::isExhausted);
    }

    public boolean isAllMembersAlive() {
        return partyMembers.stream()
                .allMatch(Character::isAlive);
    }

    public boolean isAllMembersDead() {
        return partyMembers.stream()
                .noneMatch(Character::isAlive);
    }

    public void setPartyExhaustion(boolean b) {
        partyMembers.forEach(c -> c.setExhausted(b));
    }

//   TODO: Front end party info display, might not be in this class
//    public void getPartyInfo()
}
