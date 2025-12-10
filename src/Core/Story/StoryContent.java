package Core.Story;

import Characters.Base.Hero;
import Core.GameFlow.CharacterSelectionMode;
import Core.GameManager;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class StoryContent {

    /**
     * Returns the list of slides for the game's intro.
     */
    public static List<StorySlide> getIntroSequence() {
        List<StorySlide> slides = new ArrayList<>();

        // Slide 1: The World of Eldoria
        slides.add(new StorySlide(
                "Assets/Images/Backgrounds/eldoria_map.png",
                List.of(
                        "In the mythic land of ELDORIA, four elemental forces weave the fate of mortals. Fire, wind, earth, and water- these primal powers have always felt like magic."
                )
        ));

        // Slide 2: The Peaceful Era
        slides.add(new StorySlide(
                "Assets/Images/Backgrounds/avendale_city.png",
                List.of(
                        "For generations, peace reigned in Avendale and beyond. But now, a shadow falls. The court mage VAROTH has turned to darkness. He has become DREADLORD VAROTH, and his corruption spreads."
                )
        ));

        // Slide 3: The Corruption Begins
        slides.add(new StorySlide(
                "Assets/Images/Backgrounds/corrupted_forest.png",
                List.of(
                        "Forests wilt under unnatural frost. Fields lie fallow. Goblins, orcs, and undead now prowl freely. The kingdoms are divided, their champions scattered."
                )
        ));

        // Slide 4: The Darkness Spreads
        slides.add(new StorySlide(
                "Assets/Images/Backgrounds/ancient_scroll.png",
                List.of(
                        "In this dark hour, an ancient prophecy is remembered: when darkness ascends Blackspire's peak, FOURFOLD PARTY must unite to seek:",
                        "A WARRIOR, shield of the land. An ARCHER, swift as the wind. A CLERIC, healer and protector. and a MAGE, master of four elements. Alone they falter, together they fight."
                )
        ));

        // Slide 5: The Prophecy
        slides.add(new StorySlide(
                "Assets/Images/Backgrounds/fourfolds_bg.png",
                List.of(
                        "But the four champions are scattered across Eldoria. The journey begins with ONE - the first to heed the call. Alone, you must brave the corrupted lands and find the others who share this fate."
                )
        ));

        // Slide 6: The Call to Action
        slides.add(new StorySlide(
                "Assets/Images/Backgrounds/fourfolds_bg.png",
                List.of(
                        "Your quest is twofold: First, survive the perils of the wilds and dungeons. Second, seek out the other prophesied heroes, earning their trust through shared trials."
                )
        ));

        // Slide 7: The Descent Begins
        slides.add(new StorySlide(
                "Assets/Images/Backgrounds/dungeon_entrance.png",
                List.of(
                        "Beneath Blackspire Mountain lies your proving ground: ancient dungeons twisted by Vargoth's magic. Here, you will grow stronger, level by level, and here, you will find allies worthy of the prophecy."
                )
        ));

        // Slide 8: The Final Challenge
        slides.add(new StorySlide(
                "Assets/Images/Backgrounds/throne_room.png",
                List.of(
                        "Only when the FOURFOLD PARTY is complete, each hero tested and tempered, can you ascend to the throne room above, and face Dreadlord Vargoth as one united force."
                )
        ));

        // Slide 9: The Player's Role
        slides.add(new StorySlide(
                "Assets/Images/Backgrounds/heroes.png",
                List.of(
                        "Your journey begins now. WHO WILL BE THE FIRST? Choose your starting hero wisely. The others will join you... in time.",
                        "Form your party and become the prophecy."
                )
        ));

        // Slide 10: Transition to Character Selection
        slides.add(new StorySlide(
                "Assets/Images/Backgrounds/heroes.png",
                List.of(
                        "Now, the fate of Eldoria rests with you."
                ),
                () -> {
                    // Logic to trigger character selection
                    BiConsumer<Hero, String> onCharacterPicked = (selectedHero, partyName) -> {
                        GameManager.getInstance().createPartyFromSelection(selectedHero, partyName);
                        GameManager.getInstance().closeOverlay();
                        GameManager.getInstance().startGameLoop();
                    };

                    GameManager.getInstance().showCharacterSelectionScreen(
                            CharacterSelectionMode.CREATE_NEW_PARTY,
                            onCharacterPicked
                    );
                },
                null
        ));

        return slides;
    }
}
