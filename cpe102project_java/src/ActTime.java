/**
 * Created by ethan on 5/13/15.
 */
public class ActTime {

    private long run_time;
    private ActOperation actOperation;

    public ActTime(ActOperation actOperation, long run_time) {
        this.actOperation = actOperation;
        this.run_time = run_time;
    }

    public long getRun_time() {
        return run_time;
    }

    public ActOperation getActOperation() {
        return actOperation;
    }
}
