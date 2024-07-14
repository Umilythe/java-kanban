import java.util.ArrayList;

public interface TaskManager {
    void add(Task task);

   void add(Epic epic);

    void add(Subtask subtask);

    ArrayList<Task> getAllTasks();

    ArrayList<Epic> getAllEpics();

    ArrayList<Subtask> getAllSubtasks();

    ArrayList<Subtask> getSubtasksOfEpic(Integer epicId);

    void removeAllTasks();

    void removeAllEpics();

    void removeAllSubtasks();

    Task getTaskById(Integer taskId);

    Epic getEpicById(Integer epicId);

    Subtask getSubtaskById(Integer subtaskId);

    void update(Task task);

    void update(Epic epic);

    void update(Subtask subtask);

    void removeTaskById(Integer taskId);

    void removeEpicById(Integer epicId);

    void removeSubtaskById(Integer subtaskId);

    ArrayList<Task> getHistory();

}
