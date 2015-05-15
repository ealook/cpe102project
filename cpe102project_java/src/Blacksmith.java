import processing.core.PImage;

import java.util.ArrayList;

public class Blacksmith extends WorldEntity {

    private int resource_count;
    private final int resource_limit;
    private final int rate;
    private final int resource_distance;

    public Blacksmith(String name, Point position, int resource_limit, int rate, ArrayList<PImage> imgs) {
        super(name, position, imgs);
        this.resource_limit = resource_limit;
        this.rate = rate;
        this.resource_count = 0;
        this.resource_distance = 1;
    }

    public int get_resource_count() {
        return resource_count;
    }

    public void set_resource_count(int n) {
        this.resource_count = n;
    }

    public int get_resource_limit() {
        return resource_limit;
    }

    public int get_rate() {
        return rate;
    }

    public int get_resource_distance() {
        return resource_distance;
    }

    public String entity_string() {
        return "blacksmith" + " " + this.getName() + " " + this.getPosition().getX()
                + " " + this.getPosition().getY() + " " + this.resource_limit
                + " " + this.rate + " " + this.resource_distance;
    }
}
