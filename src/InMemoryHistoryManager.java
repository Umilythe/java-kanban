import java.util.List;
import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {

    private final ArrayList<Task> historyList = new ArrayList<>();
    private  int lengthOfHistoryList = 10;
    @Override
    public void add(Task task) {
        if (historyList.size() >= lengthOfHistoryList) {
            historyList.removeFirst();
            historyList.add(task);
        } else {
            historyList.add(task);
        }
  }

    @Override
    public ArrayList<Task> getHistory() {
        return historyList;
    }
}
