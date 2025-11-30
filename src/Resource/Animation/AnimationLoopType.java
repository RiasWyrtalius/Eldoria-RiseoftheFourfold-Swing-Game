package Resource.Animation;

public enum AnimationLoopType {
    INFINITE(-1),
    ONE_CYCLE(1),
    TWO_CYCLES(2);

    private final int loopCountValue;
    
    AnimationLoopType(int loopCountValue) {
        this.loopCountValue = loopCountValue;    
    }

    public int getLoopCountValue() {
        return loopCountValue;
    }
}
