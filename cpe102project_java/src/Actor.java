import processing.core.PImage;

import java.util.ArrayList;
import java.util.concurrent.Callable;

public abstract class Actor extends WorldEntity {

    private ArrayList<ActTime> pending_actions;

    public Actor(String name, Point position, ArrayList<PImage> imgs) {
        super(name, position, imgs);
        this.pending_actions = new ArrayList<>();
    }

    public abstract void schedule_entity(WorldModel world, ImageStore i_store);

    public void remove_pending_action(ActOperation action) {
        for (ActTime actTime : pending_actions) {
            if (actTime.getActOperation() == action) {
                this.pending_actions.remove(actTime);
                break;
            }
        }
    }

    public void add_pending_action(ActTime actTime) {
        this.pending_actions.add(actTime);
    }

    public ArrayList<ActTime> get_pending_actions() {
        return pending_actions;
    }

    public void schedule_action(ActOperation action, long time) {
        ActTime actTime = new ActTime(action, time);
        this.add_pending_action(actTime);
    }

    public void clear_pending_actions(WorldModel world) {
        this.pending_actions = new ArrayList<>();
    }

    public boolean isTime(long current_time) {
        return (get_pending_actions().size() != 0) && (current_time >= get_pending_actions().get(0).getRun_time());
    }
}
