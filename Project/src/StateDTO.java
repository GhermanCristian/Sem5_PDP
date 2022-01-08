import java.io.Serializable;

public class StateDTO implements Serializable {
    private final State state;
    private final int bound;
    private final boolean isFinished;

    public StateDTO(State state, int bound, boolean isFinished) {
        this.state = state;
        this.bound = bound;
        this.isFinished = isFinished;
    }

    public State getState() {
        return this.state;
    }

    public int getBound() {
        return this.bound;
    }

    public boolean isFinished() {
        return this.isFinished;
    }
}
