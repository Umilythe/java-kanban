package manager;

import task.*;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {

    private int nextId = 1;
    private Map<Integer, Task> tasks = new HashMap<>();
    private Map<Integer, Epic> epics = new HashMap<>();
    private Map<Integer, Subtask> subtasks = new HashMap<>();
    private HistoryManager historyManager = Managers.getDefaultHistory();


    @Override
    public void add(Task task) {
        task.setId(nextId);
        nextId++;
        tasks.put(task.getId(), task);
    }

    @Override
    public void add(Epic epic) {
        epic.setId(nextId);
        nextId++;
        epics.put(epic.getId(), epic);
    }

    @Override
    public void add(Subtask subtask) {
        if (epics.containsKey(subtask.getEpicId())) {
            subtask.setId(nextId);
            nextId++;
            subtasks.put(subtask.getId(), subtask);

            Epic epic = epics.get(subtask.getEpicId());
            epic.setSubTasksIds(subtask.getId());
            changeEpicStatus(epic);
        } else {
            System.out.println("Эпика с таким id не существует");
        }
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public ArrayList<Subtask> getSubtasksOfEpic(Integer epicId) {
        ArrayList<Subtask> subTasksOfEpic = new ArrayList<>();
        if (epics.containsKey(epicId)) {
            for (Integer subtaskId : epics.get(epicId).getSubTasksIds()) {
                Subtask intermSubtask = subtasks.get(subtaskId);
                subTasksOfEpic.add(intermSubtask);
            }
        } else {
            System.out.println("Такого эпика не существует.");
        }
        return subTasksOfEpic;
    }

    @Override
    public void removeAllTasks() {
        for (Integer id: tasks.keySet()) {
            historyManager.remove(id);
        }
        tasks.clear();
    }

    @Override
    public void removeAllEpics() {
        for (Integer id: epics.keySet()) {
            historyManager.remove(id);
        }
        epics.clear();
        for (Integer id: subtasks.keySet()) {
            historyManager.remove(id);
        }
        subtasks.clear();
    }

    @Override
    public void removeAllSubtasks() {
        for (Integer id: subtasks.keySet()) {
            historyManager.remove(id);
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubtasksIds();
            changeEpicStatus(epic);
        }
    }

    @Override
    public Task getTaskById(Integer taskId) {
        Task task = tasks.get(taskId);
        historyManager.add(task);
        return tasks.get(taskId);

    }

    @Override
    public Epic getEpicById(Integer epicId) {
        Epic epic = epics.get(epicId);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtaskById(Integer subtaskId) {
        Subtask subtask = subtasks.get(subtaskId);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public void update(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void update(Epic epic) {
        epics.put(epic.getId(), epic);
        changeEpicStatus(epic);
    }

    @Override
    public void update(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        changeEpicStatus(epic);
    }

    @Override
    public void removeTaskById(Integer taskId) {
        historyManager.remove(taskId);
        tasks.remove(taskId);
    }

    @Override
    public void removeEpicById(Integer epicId) {
        if (epics.containsKey(epicId)) {
            ArrayList<Integer> idsOfSubtasks = epics.get(epicId).getSubTasksIds();
            for (Integer id : idsOfSubtasks) {
                subtasks.remove(id);
                historyManager.remove(id);
            }
            historyManager.remove(epicId);
            epics.remove(epicId);
        } else {
            System.out.println("Эпика с таким id не существует.");
        }
    }

    @Override
    public void removeSubtaskById(Integer subtaskId) {
        Subtask subtask = subtasks.get(subtaskId);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            epic.removeOneSubtaskId(subtaskId);
            historyManager.remove(subtaskId);
            subtasks.remove(subtaskId);
            changeEpicStatus(epic);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    public void changeEpicStatus(Epic epic) {
        int doneVariable = 0;
        int newVariable = 0;
        ArrayList<Integer> subtasksIds = epic.getSubTasksIds();
        for (Integer id : subtasksIds) {
            if (subtasks.get(id).getStatus() == Status.NEW) {
                newVariable++;
            }
            if (subtasks.get(id).getStatus() == Status.DONE) {
                doneVariable++;
            }
        }
        if (newVariable == subtasksIds.size()) {
            epic.setStatus(Status.NEW);
        } else if (doneVariable == subtasksIds.size()) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }


}
