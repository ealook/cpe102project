/**
 * Created by ethan on 5/13/15.
 */
public class FinderPair {

    private final Point pt;
    private final boolean found;

    public FinderPair(Point pt, boolean found) {
        this.pt = pt;
        this.found = found;
    }

    public Point getPt() {
        return pt;
    }

    public boolean isFound() {
        return found;
    }
}
