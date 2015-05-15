
public class ListItem {

    private final Object item;
    private final int ord;

    public ListItem(Object item, int ord) {
        this.item = item;
        this.ord = ord;
    }

    public boolean equalTo(ListItem other) {
        return this.item == other.item && this.ord == ord;
    }

    public int getOrd() {
        return ord;
    }

    public Object getItem() {
        return item;
    }
}
