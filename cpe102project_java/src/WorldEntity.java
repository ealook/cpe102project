
public class WorldEntity extends WorldObject {

    private Point position;

    public WorldEntity(String name, Point position) {
        super(name);
        this.position = position;
    }

    public Point getPosition() {
        return this.position;
    }

    public void setPosition(Point point) {
        this.position = point;
    }
}
