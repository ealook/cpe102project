import java.util.ArrayList;

/**
 * Created by ethan on 5/22/15.
 */
public class AStarNode {

    private AStarNode came_from;
    private Point pt;
    private int g_score;
    private double f_score;

    public AStarNode(Point pt) {
        this.pt = pt;

    }

    public Point getPt() {
        return pt;
    }

    public void setPt(Point pt) {
        this.pt = pt;
    }

    public AStarNode getOrigin() {
        return came_from;
    }

    public void setOrigin(AStarNode pt) {
        this.came_from = pt;
    }

    public int getG_score() {
        return g_score;
    }

    public void setG_score(int g_score) {
        this.g_score = g_score;
    }

    public double getF_score() {
        return f_score;
    }

    public void setF_score(double f_score) {
        this.f_score = f_score;
    }
}
