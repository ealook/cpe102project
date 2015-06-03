import processing.core.PImage;

import java.util.ArrayList;

public class PineapplePiece extends WorldEntity {

    public PineapplePiece(String name, Point position, ArrayList<PImage> imgs) {
        super(name, position, imgs);
    }

    public String entity_string() {
        return "obstacle" + " " + this.getName() + " " + this.getPosition().getX() + " " + this.getPosition().getY();
    }
}
