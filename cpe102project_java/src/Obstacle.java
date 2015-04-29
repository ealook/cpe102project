
public class Obstacle extends WorldEntity {

    public Obstacle(String name, Point position) {
        super(name, position);
    }

    public String entity_string() {
        return "obstacle" + " " + this.getName() + " " + this.getPosition().getX() + " " + this.getPosition().getY();
    }
}
