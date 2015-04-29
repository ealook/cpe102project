
public class Mover extends AnimatedActor {

    private final int rate;

    public Mover(String name, Point position, int rate) {
        super(name, position);
        this.rate = rate;
    }

    public int get_rate() {
        return rate;
    }

}
