package Abilities;

import Characters.Character;
import Core.LogManager;

import java.time.temporal.Temporal;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class Skill {
    private String name;
    private String effect;
    private int manaCost;
    private int power;
    private SkillType skillType;
    private SkillAction skillAction;
    private SkillTarget skillTarget;
    private final FullExecuteConsumer executeLogic;

    public Skill(String name, String effect, int manaCost, int power, SkillType skillType, SkillAction skillAction, SkillTarget skillTarget, FullExecuteConsumer executeLogic) {
        this.name = name;
        this.effect = effect;
        this.manaCost = manaCost;
        this.power = power;
        this.skillType = skillType;
        this.skillAction = skillAction;
        this.skillTarget = skillTarget;
        this.executeLogic = executeLogic;
    }

    public void execute(Character user, List<Character> target, Runnable onSkillComplete) {
        if (user.spendMana(this.manaCost)) {
            executeLogic.accept(this, user, target, onSkillComplete);
        }
    }

    public String getActionLog(Character user, String action, List<Character> targets, int damage) {
        String targetNames = formatTargetList(targets);

        String logMessage = String.format("%s %s %s on %s!",
                user.getName(),
                action,
                this.getName(),
                targetNames
        );

        return logMessage;
    }

    public static String formatTargetList(List<Character> targets) {
        if (targets.size() == 1) {
            return targets.get(0).getName();
        }

        String allButLast = targets.subList(0, targets.size() - 1).stream()
                .map(Character::getName)
                .collect(Collectors.joining(", "));

        String lastName = targets.getLast().getName();

        return allButLast + " and " + lastName;
    }

    // =============== PUBLIC GETTERS FOR UI ===============
    public String getName() {
        return name;
    }
    public String getEffect() {
        return effect;
    }
    public int getManaCost() {
        return manaCost;
    }
    public int getPower() {
        return power;
    }
    public SkillType getSkillType() {
        return skillType;
    }

    public SkillAction getSkillAction() {
        return skillAction;
    }

    public SkillTarget getSkillTarget() {
        return skillTarget;
    }

    public FullExecuteConsumer getExecuteLogic() {
        return executeLogic;
    }
}
