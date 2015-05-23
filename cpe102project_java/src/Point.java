public class Point {

    private final int x;
    private final int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean adjacent_to(Point other) {
        return (Math.abs(this.getX() - other.getX()) == 1 && this.getY() == other.getY())
                || (Math.abs(this.getY() - other.getY()) == 1 && this.getX() == other.getX());
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public double distanceTo(Point other) {
        return Math.sqrt((x - other.getX()) * (x - other.getX()) + (y - other.getY()) * (y - other.getY()));
    }
}
