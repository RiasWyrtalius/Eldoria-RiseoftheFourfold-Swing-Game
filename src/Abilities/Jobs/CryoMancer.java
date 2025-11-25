package Abilities.Jobs;

import Abilities.JobClass;
import Abilities.*;

import Characters.Character;
import Core.LogColor;
import Core.LogManager;
import Core.VisualEffectsManager;
import Resource.AnimationLoopType;
import Resource.AssetManager;

import java.util.List;
import java.util.function.BiConsumer;


public class CryoMancer extends JobClass {
    public CryoMancer(){
        super("Ice Mage","Wields snow and ice as its magic",0,0 );
        AssetManager.getInstance().registerAnimation(
                "MAGE_IDLE",
                "Assets/Animations/Mage-Fire/Idle/sprite_%d.png",
                5, 100, 100 , 300,
                AnimationLoopType.INFINITE
        );

        AssetManager.getInstance().registerAnimation(
                "ICE_SPIKE",
                "Assets/Animations/Mage-Ice/Effects/Ice_Spike/sprite_%d.png",
                7, 100, 100 , 200,
                AnimationLoopType.ONE_CYCLE
        );
    }
    public List<Skill> createSkills() {

        FullExecuteConsumer iceSpikeLogic = (self, user, targets, onSkillComplete) -> {
            int calculateDamage = (int)(30 + 15 + (user.getLevel() * 1.2) + (30 * (user.getLevel() * 0.05)));
            Character target = targets.getFirst();

            LogManager.log(self.getActionLog(user, self.getSkillAction().getActionVerb(), targets, calculateDamage), LogColor.HERO_ACTION);

            VisualEffectsManager.getInstance().playAnimationOnCharacter("ICE_SPIKE", target, () -> {

                target.takeDamage(calculateDamage, user, self);

                if (onSkillComplete != null) {
                    onSkillComplete.run();
                }
            }, true);

            if (onSkillComplete != null) {
                onSkillComplete.run();
            }
        };


        FullExecuteConsumer frostBiteLogic = (self, user, targets, onSkillComplete) -> {
            int calculateDamage = (int)(15 + 15 + (user.getLevel() * 1.2) + (15 * (user.getLevel() * 0.05)));
            Character target = targets.getFirst();

            LogManager.log(self.getActionLog(user, self.getSkillAction().getActionVerb(), targets, calculateDamage), LogColor.HERO_ACTION);

            target.takeDamage(calculateDamage, user, self);

            if (onSkillComplete != null) {
                onSkillComplete.run();
            }
        };


        Skill IceSpike = new Skill(
                "Ice Spike", "Unleashes spikes from the ground", 25, 30,
                SkillType.DAMAGE, SkillAction.MAGICAL, SkillTarget.SINGLE_TARGET,
                iceSpikeLogic
        );

        Skill FrostBite = new Skill(
                "Frost Bite", "Unleashes cold Air to enemy", 10, 15,
                SkillType.DAMAGE, SkillAction.MAGICAL, SkillTarget.SINGLE_TARGET,
                frostBiteLogic
        );

        return List.of(IceSpike,FrostBite);
    }
}


