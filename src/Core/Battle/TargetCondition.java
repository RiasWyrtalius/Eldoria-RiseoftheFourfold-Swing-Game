package Core.Battle;

import Characters.Character;

public enum TargetCondition {
    ALIVE {
        @Override
        public boolean isValid(Character target) {
            return target.isAlive();
        }
    },
    DEAD {
        @Override
        public boolean isValid(Character target) {
            return !target.isAlive();
        }
    },
    EXHAUSTED {
        @Override
        public boolean isValid(Character target) {
            return target.isAlive() && target.isExhausted();
        }
    },
    READY {
        @Override
        public boolean isValid(Character target) {
            return target.isAlive() && !target.isExhausted();
        }
    },
    ANY {
        @Override
        public boolean isValid(Character target) {
            return true;
        }
    };

    public abstract boolean isValid(Character target);
}