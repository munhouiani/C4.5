import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

/**
 * Created by mhwong on 8/5/15.
 */
public class Column {

    String attribute;           // the name of the attribute
    String type;                // the imported value's type
    ArrayList<String> row;    // the row

    public Column(String attribute) {
        this.attribute = attribute;
        this.type = "String";
        this.row = new ArrayList<>();
    }

    public Column(Column column) { // copy constructor
        this.attribute = column.attribute;
        this.type = column.type;
        this.row = new ArrayList<>();
        for(String item: column.row) {
            this.row.add(item);
        }
    }

    public Column() {
        this("");
    }

    public int getRowSize() {
        return row.size();
    }

    public int getValueCount(String value) {
        int count = 0;
        for(String item: this.row) {
            if(value.equals(item)) {
                count++;
            }
        }
        return count;
    }

    public void addValue(String value) {
        this.row.add(value);
    }

    public HashMap<String, Integer> getFrequencyTable() {
        HashMap<String, Integer> frequencyTable = new HashMap<>();
        for(String item: row) {
            if(frequencyTable.containsKey(item)) {
                frequencyTable.put(item, frequencyTable.get(item) + 1);
            }
            else {
                frequencyTable.put(item, 1);
            }
        }
        return frequencyTable;
    }

    public TreeSet<String> getSetofValue() {
        TreeSet<String> set;

        set = new TreeSet<String>();
        for(String item: row) {
            set.add(item);
        }


        return set;
    }

    public String getRowValue(int i) {
        return this.row.get(i);
    }
}
