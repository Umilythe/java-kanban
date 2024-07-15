package Manager;

import Manager.HistoryManager;
import Manager.InMemoryHistoryManager;
import Manager.InMemoryTaskManager;
import Manager.TaskManager;

public class Managers {

    public static TaskManager getDefault() {
       return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
