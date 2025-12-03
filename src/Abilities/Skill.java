package Abilities;

import Characters.Character;
import Core.Battle.TargetCondition;
import Core.Battle.TargetType;
import Core.Utils.LogFormat;
import Core.Utils.LogManager;

import java.util.List;
import java.util.stream.Collectors;

public class Skill {
    private final String name;
    private final String effect;
    private final int manaCost;
    private final int power;
    private final SkillType skillType;
    private final SkillAction skillAction;
    private final TargetType targetType;
    private final TargetCondition targetCondition;
    private final SkillLogicConsumer executeLogic;

    public Skill(String name, String effect, int manaCost, int power, SkillType skillType, SkillAction skillAction, TargetType targetType, TargetCondition targetCondition, SkillLogicConsumer executeLogic) {
        this.name = name;
        this.effect = effect;
        this.manaCost = manaCost;
        this.power = power;
        this.skillType = skillType;
        this.skillAction = skillAction;
        this.targetCondition = targetCondition;
        this.targetType = targetType;
        this.executeLogic = executeLogic;
    }

    public void execute(Character user, List<Character> target, Runnable onSkillComplete) {
        if (user.spendMana(this.manaCost)) {
            executeLogic.accept(this, user, target, onSkillComplete);
        }
    }

    public String getActionLog(Character user, String action, List<Character> targets) {
        String targetNames = formatTargetList(targets);

        return String.format("%s %s with %s on %s!",
                user.getName(),
                action,
                this.getName(),
                targetNames
        );
    }

    public static String formatTargetList(List<Character> targets) {
        if (targets.size() == 1) {
            return targets.get(0).getName();
        }

        String allButLast = targets.subList(0, targets.size() - 1).stream()
                .map(Character::getName)
                .collect(Collectors.joining(", "));

        String lastName = targets.get(targets.size()-1).getName();

        return allButLast + " and " + lastName ;
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

    public TargetType getTargetType() {
        return targetType;
    }

    public TargetCondition getTargetCondition() {
        return targetCondition;
    }

    public SkillLogicConsumer getExecuteLogic() {
        return executeLogic;
    }
}
