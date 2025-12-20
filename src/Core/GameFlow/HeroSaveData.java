package Core.GameFlow;

import Characters.Base.Hero;

import java.io.Serializable;
import java.util.Map;

public class HeroSaveData implements Serializable {
    private static final long serialVersionUID = 1L;

    public String name;
    public String jobClassName; // save the string and not the object itself
    public int level;
    public int xp;

    public int currentHP;
    public int currentMP;

    // level 1 stats, acts as DNA
    // for scaling logic
    public int baseHP;
    public int baseMP;
    public int baseAtk;
    public int baseDef;

    public HeroSaveData(Hero hero) {
        this.name = hero.getName();
        this.jobClassName = hero.getJob().getName();
        this.level = hero.getLevel();
        this.xp = hero.getXP();

        this.currentHP = hero.getHealth();
        this.currentMP = hero.getMana();

        this.baseHP = hero.getBaseMaxHealth();
        this.baseAtk = hero.getBaseAtk();
        this.baseDef = hero.getBaseDef();
        this.baseMP = hero.getBaseMaxMana();
    }
}
