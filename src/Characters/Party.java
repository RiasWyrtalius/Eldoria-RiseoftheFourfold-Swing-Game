package Characters;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Party {
    private String partyName;
    private List<Character> partyMembers;
    private final Random random;

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

    public Character getRandomPartyMember() {
        if (this.partyMembers.isEmpty())
            return null;
        int index = random.nextInt(this.partyMembers.size());
        return this.partyMembers.get(index);
    }

//   TODO: Front end party info display, might not be in this class
//    public void getPartyInfo()
}
