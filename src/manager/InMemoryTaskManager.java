package manager;

import exeptions.ManagerCheckTimeException;
import task.*;


import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {

    private int nextId = 1;
    private Map<Integer, Task> tasks = new HashMap<>();
    private Map<Integer, Epic> epics = new HashMap<>();
    private Map<Integer, Subtask> subtasks = new HashMap<>();
    Comparator<Task> comparator = Comparator.comparing(Task::getStartTime);
    private Set<Task> prioritizedTasks = new TreeSet<>(comparator);
    private HistoryManager historyManager = Managers.getDefaultHistory();

    public void putTask(Task task) {
        if (task.getStartTime() != null) {
            if (checkTaskTimeIntersection(task)) {
                prioritizedTasks.add(task);
            } else {
                throw new ManagerCheckTimeException("Задача пересекается по времени выполнения с уже существующими.");
            }
        }
        tasks.put(task.getId(), task);

    }

    public void putEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    public void putSubtask(Subtask subtask) {
        if (epics.containsKey(subtask.getEpicId())) {
            if (subtask.getStartTime() != null) {
                if (checkTaskTimeIntersection(subtask)) {
                    prioritizedTasks.add(subtask);
                } else {
                    throw new ManagerCheckTimeException("Задача пересекается по времени выполнения с уже существующими.");
                }
            }
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getEpicId());
            epic.setSubTasksIds(subtask.getId());
            changeEpicStatus(epic);
            changeEpicTime(epic);
        } else {
            System.out.println("Эпика с таким id не существует");
        }
    }

    public void setNextId(int nextId) {
        this.nextId = nextId;
    }

    @Override
    public void add(Task task) {
        if (task.getStartTime() != null) {
            if (checkTaskTimeIntersection(task)) {
                prioritizedTasks.add(task);
            } else {
                throw new ManagerCheckTimeException("Задача пересекается по времени выполнения с уже существующими.");
            }
        }
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
            if (subtask.getStartTime() != null) {
                if (checkTaskTimeIntersection(subtask)) {
                    prioritizedTasks.add(subtask);
                } else {
                    throw new ManagerCheckTimeException("Задача пересекается по времени выполнения с уже существующими.");
                }
            }
            subtask.setId(nextId);
            nextId++;
            subtasks.put(subtask.getId(), subtask);

            Epic epic = epics.get(subtask.getEpicId());
            epic.setSubTasksIds(subtask.getId());
            changeEpicStatus(epic);
            changeEpicTime(epic);
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
    public List<Subtask> getSubtasksOfEpic(Integer epicId) {
        List<Subtask> subTasksOfEpic = new ArrayList<>();
        if (epics.containsKey(epicId)) {
            subTasksOfEpic = getAllSubtasks().stream()
                    .filter(subtask -> subtask.getEpicId() == epicId)
                    .collect(Collectors.toList());
        } else {
            System.out.println("Такого эпика не существует.");
        }
        return subTasksOfEpic;
    }

    @Override
    public void removeAllTasks() {
        for (Integer id : tasks.keySet()) {
            Task task = tasks.get(id);
            historyManager.remove(id);
            prioritizedTasks.remove(task);
        }
        tasks.clear();
    }

    @Override
    public void removeAllEpics() {
        for (Integer id : epics.keySet()) {
            historyManager.remove(id);
        }
        epics.clear();
        for (Integer id : subtasks.keySet()) {
            Subtask subtask = subtasks.get(id);
            historyManager.remove(id);
            prioritizedTasks.remove(subtask);
        }
        subtasks.clear();
    }

    @Override
    public void removeAllSubtasks() {
        for (Integer id : subtasks.keySet()) {
            Subtask subtask = subtasks.get(id);
            historyManager.remove(id);
            prioritizedTasks.remove(subtask);
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubtasksIds();
            changeEpicStatus(epic);
            changeEpicTime(epic);
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
        if (task.getStartTime() != null) {
            if (checkTaskTimeIntersection(task)) {
                Task oldTask = tasks.get(task.getId());
                prioritizedTasks.remove(oldTask);
                prioritizedTasks.add(task);
            } else {
                throw new ManagerCheckTimeException("Задача пересекается по времени выполнения с уже существующими.");
            }
        }
        tasks.put(task.getId(), task);
    }

    @Override
    public void update(Epic epic) {
        epics.put(epic.getId(), epic);
        changeEpicStatus(epic);
        changeEpicTime(epic);
    }

    @Override
    public void update(Subtask subtask) {
        if (subtask.getStartTime() != null) {
            if (checkTaskTimeIntersection(subtask)) {
                Subtask oldSubtask = subtasks.get(subtask.getId());
                prioritizedTasks.remove(oldSubtask);
                prioritizedTasks.add(subtask);
            } else {
                throw new ManagerCheckTimeException("Задача пересекается по времени выполнения с уже существующими.");
            }
        }
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        changeEpicStatus(epic);
        changeEpicTime(epic);
    }

    @Override
    public void removeTaskById(Integer taskId) {
        Task task = tasks.get(taskId);
        historyManager.remove(taskId);
        tasks.remove(taskId);
        prioritizedTasks.remove(task);
    }

    @Override
    public void removeEpicById(Integer epicId) {
        if (epics.containsKey(epicId)) {
            ArrayList<Integer> idsOfSubtasks = epics.get(epicId).getSubTasksIds();
            for (Integer id : idsOfSubtasks) {
                Subtask subtask = subtasks.get(id);
                subtasks.remove(id);
                historyManager.remove(id);
                prioritizedTasks.remove(subtask);
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
            prioritizedTasks.remove(subtask);
            changeEpicStatus(epic);
            changeEpicTime(epic);
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

    public void changeEpicTime(Epic epic) {
        List<Subtask> subtasksOfEpic = getSubtasksOfEpic(epic.getId());
        if (!subtasksOfEpic.isEmpty()) {
            Duration epicDuration = Duration.ofSeconds(0);
            LocalDateTime veryBeggining = subtasksOfEpic.getFirst().getStartTime();
            for (Subtask subtask : subtasksOfEpic) {
                if (veryBeggining == null) {
                    veryBeggining = subtask.getStartTime();
                } else if ((subtask.getStartTime() != null) && (subtask.getStartTime().isBefore(veryBeggining))) {
                    veryBeggining = subtask.getStartTime();
                }
                if (subtask.getDuration() != null) {
                    epicDuration = epicDuration.plus(subtask.getDuration());
                }
            }
            epic.setStartTime(veryBeggining);
            epic.setDuration(epicDuration);
            epic.setEndTime(epic.getStartTime().plus(epic.getDuration()));
        } else {
            epic.setStartTime(null);
            epic.setEndTime(null);
            epic.setDuration(null);
        }
    }

    public List<Task> getPrioritizedTasks() {
        return prioritizedTasks.stream().toList();
    }

    public boolean checkTaskTimeIntersection(Task task) {
        List<Task> existingTasks = getPrioritizedTasks();
        if (!existingTasks.isEmpty()) {
                for (Task existingTask : existingTasks) {
                    if (task.getStartTime().isBefore(existingTask.getStartTime()) && task.getEndTime().isBefore(existingTask.getStartTime())) {
                        return true;
                    } else if (task.getStartTime().isAfter(existingTask.getEndTime())) {
                        return true;
                    } else {
                        return false;
                    }
                }

        }
            return true;
    }


}



