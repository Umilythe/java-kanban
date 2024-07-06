import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {

    int nextId = 1;
    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();
    HashMap<Integer, Subtask> subtasks = new HashMap<>();

    public void add(Task task) {
        task.setId(nextId);
        nextId++;
        tasks.put(task.getId(), task);
    }

    public void add(Epic epic) {
        epic.setId(nextId);
        nextId++;
        epics.put(epic.getId(), epic);
    }

    public void add(Subtask subtask) {
        subtask.setId(nextId);
        nextId++;
        subtasks.put(subtask.getId(), subtask);

        Epic epic = epics.get(subtask.getEpicId());
        epic.setSubTasksIds(subtask.getId());
        changeEpicStatus(epic);
    }

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public ArrayList<Subtask> getSubtasksOfEpic(Integer epicId) {
        ArrayList<Subtask> subTasksOfEpic = new ArrayList<>();
        if (epics.containsKey(epicId)) {
            for (Subtask subtask : subtasks.values()) {
                if (subtask.getEpicId() == epicId) {
                    subTasksOfEpic.add(subtask);
                }
            }
        } else {
            System.out.println("Такого эпика не существует");
        }
        return subTasksOfEpic;
    }

    public void removeAllTasks() {
        tasks.clear();
    }

    public void removeAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void removeAllSubtasks() {
        subtasks.clear();
        for (Epic epic: epics.values()) {
            epic.clearSubtasksIds();
            epic.setStatus(Status.NEW);
        }
    }

    public Task getTaskById(Integer taskId) {
        return tasks.get(taskId);
    }

    public Task getEpicById(Integer epicId) {
        return epics.get(epicId);
    }
    public Task getSubtaskById(Integer subtaskId) {
        return subtasks.get(subtaskId);
    }
    public void update(Task task) {
        tasks.put(task.getId(), task);
    }
    public void update(Epic epic) {
        epics.put(epic.getId(), epic);
        changeEpicStatus(epic);
    }
    public void update(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        changeEpicStatus(epic);
    }

    public void removeTaskById(Integer taskId) {
        tasks.remove(taskId);
    }

    public void removeEpicById(Integer epicId) {
        ArrayList <Integer> idsOfSubtasks = epics.get(epicId).getSubTasksIds();
        for (Integer id: idsOfSubtasks) {
            subtasks.remove(id);
        }
        epics.remove(epicId);
    }

    public void removeSubtaskById(Integer subtaskId) {
        Subtask subtask = subtasks.get(subtaskId);
        Epic epic = epics.get(subtask.getEpicId());
        ArrayList<Integer> subtasksIds = epic.getSubTasksIds();
        subtasksIds.remove(subtaskId);
        epic.setSubTasksIds(subtasksIds);
        subtasks.remove(subtaskId);
        changeEpicStatus(epic);
    }

    public void changeEpicStatus(Epic epic) {
        int doneVariable = 0;
        int newVaraible = 0;
        ArrayList<Integer> subtasksIds = epic.getSubTasksIds();
        for (Integer id: subtasksIds) {
            if (subtasks.get(id).getStatus() == Status.NEW) {
                newVaraible++;
            }
            if (subtasks.get(id).getStatus() == Status.DONE) {
                doneVariable++;
            }
        }
        if (newVaraible == subtasksIds.size()) {
            epic.setStatus(Status.NEW);
        } else if (doneVariable == subtasksIds.size()) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

}
