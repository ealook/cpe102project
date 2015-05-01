public class Point {

    private final int x;
    private final int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean adjacent_to(Point other) {
        return (Math.abs(this.x - other.x) == 1) || (Math.abs(this.y - other.y) == 1);
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }
}
