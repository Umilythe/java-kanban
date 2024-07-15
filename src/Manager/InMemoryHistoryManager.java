package Manager;

import Manager.HistoryManager;
import Task.Task;

import java.util.List;
import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {

    private final List<Task> historyList = new ArrayList<>();
    private  int lengthOfHistoryList = 10;
    @Override
    public void add(Task task) {
        if (historyList.size() >= lengthOfHistoryList) {
            historyList.removeFirst();
        }
            historyList.add(task);
        }


    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(historyList);
    }
}
