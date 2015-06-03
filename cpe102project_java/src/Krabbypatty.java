import processing.core.PImage;

import java.util.ArrayList;

public class Krabbypatty extends WorldEntity {

    public Krabbypatty(String name, Point position, ArrayList<PImage> imgs) {
        super(name, position, imgs);
    }

    public String entity_string() {
        return "krabbypatty" + " " + this.getName() + " " + this.getPosition().getX() + " " + this.getPosition().getY();
    }
}
