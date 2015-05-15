import processing.core.PImage;

import java.util.ArrayList;

public class Obstacle extends WorldEntity {

    public Obstacle(String name, Point position, ArrayList<PImage> imgs) {
        super(name, position, imgs);
    }

    public String entity_string() {
        return "obstacle" + " " + this.getName() + " " + this.getPosition().getX() + " " + this.getPosition().getY();
    }
}
