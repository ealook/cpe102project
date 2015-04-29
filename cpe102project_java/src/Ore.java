
public class Ore extends Actor {

    private final int rate;

    public Ore(String name, Point position) {
        super(name, position);
        this.rate = 5000;
    }

    public int get_rate() {
        return rate;
    }

    public String entity_string() {
        return "ore" + " " + this.getName() + " " + this.getPosition().getX()
                + " " + this.getPosition().getY() + " " + this.rate;
    }
}
