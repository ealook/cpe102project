import java.util.ArrayList;

/**
 * Created by ethan on 5/13/15.
 */
public class OrderedList {

    ArrayList<ListItem> list;

    public OrderedList() {
        this.list = new ArrayList<>();
    }

    public void insert(Object item, int ord) {
        int size = this.list.size();
        int idx = 0;
        while (idx < size && this.list.get(idx).getOrd() < ord) {
            idx++;
        }
        this.list.add(idx, new ListItem(item, ord));
    }

    public void remove(Object item) {
        int size = this.list.size();
        int idx = 0;
        while(idx < size && this.list.get(idx).getItem() != item) {
            idx += 1;
        }
        if (idx < size) {
            this.list.remove(idx);
        }
    }
}
