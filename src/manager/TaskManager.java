package manager;

import task.Task;
import task.Subtask;
import task.Epic;

import java.util.List;


public interface TaskManager {
    void add(Task task);

   void add(Epic epic);

    void add(Subtask subtask);

    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<Subtask> getAllSubtasks();

    List<Subtask> getSubtasksOfEpic(Integer epicId);

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

    List<Task> getHistory();

}
