
public class Rect {

    private int left;
    private int top;
    private int width;
    private int height;

    public Rect(int left, int top, int width, int height) {
        this.left = left;
        this.top = top;
        this.width = width;
        this.height = height;
    }

    public int getLeft() {
        return left;
    }

    public int getTop() {
        return top;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean collidepoint(int x, int y) {
        return (x >= left) && (x < left + width) && (y >= top) && (y < top + height);
    }
}
