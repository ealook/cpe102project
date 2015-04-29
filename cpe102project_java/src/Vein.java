
public class Vein extends Actor {

    private final int rate;
    private final int resource_distance;

    public Vein(String name, Point position, int rate) {
        super(name, position);
        this.rate = rate;
        this.resource_distance = 1;
    }

    public int get_rate() {
        return rate;
    }

    public int get_resource_distance() {
        return resource_distance;
    }

    public String entity_string() {
        return "vein" + " " + this.getName() + " " + this.getPosition().getX()
                + " " + this.getPosition().getY() + " " + this.rate + " " + this.resource_distance;
    }

}
