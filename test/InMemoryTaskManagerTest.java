import Manager.Managers;
import Manager.TaskManager;
import Task.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class InMemoryTaskManagerTest {
    private TaskManager taskManager;

    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getDefault();
    }

    @Test
    void addTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", Status.NEW);
        taskManager.add(task);
        final int taskId = task.getId();

        final Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.getFirst(), "Задачи не совпадают.");

    }

    @Test
    void addEpicAndSubtask() {
        Epic epic = new Epic("Task.Task.Epic for test", "Its description");
        taskManager.add(epic);
        Subtask subtask1 = new Subtask("First subtask of test epic", "Its description", Status.NEW, epic.getId());
        taskManager.add(subtask1);
        Subtask subtask2 = new Subtask("Second subtask of test epic", "And its description", Status.NEW, epic.getId());
        taskManager.add(subtask2);

        final Epic savedEpic = taskManager.getEpicById(epic.getId());
        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");

        final Subtask savedSubtask1 = taskManager.getSubtaskById(subtask1.getId());
        assertNotNull(savedSubtask1, "Подзадача не найдена.");
        assertEquals(subtask1, savedSubtask1, "Подзадачи не совпадают.");
        final Subtask savedSubtask2 = taskManager.getSubtaskById(subtask2.getId());
        assertNotNull(savedSubtask2, "Подзадача не найдена.");
        assertEquals(subtask2, savedSubtask2, "Подзадачи не совпадают.");

        final List<Epic> epics = taskManager.getAllEpics();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.getFirst(), "Эпики не совпадают.");

        final List<Subtask> subtasks = taskManager.getAllSubtasks();

        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(2, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask1, subtasks.getFirst(), "Подзадачи не совпадают.");
        assertEquals(subtask2, subtasks.get(1), "Подзадачи не совпадают.");

    }

    @Test
    void shouldNotAddEpicAsSubtask() {
        Epic epic1 = new Epic("Task.Task.Epic for test", "Its description");
        taskManager.add(epic1);
        Epic epic2 = new Epic("One more epic for test", "Wow description", epic1.getId());
        taskManager.add(epic2);

        final List<Integer> subtasksIds = epic1.getSubTasksIds();

        assertTrue(subtasksIds.isEmpty(), "Задачи у эпика есть");
    }

    @Test
    void shouldNotAddSubtaskAsEpic() {
        Epic epic = new Epic("Task.Task.Epic for test", "Its description");
        taskManager.add(epic);

    }

    @Test
    void tasksWithSetIdAndGeneratedIdDoNotHaveConflict() {
        Task task1 = new Task("Test task", "And description", Status.NEW);
        taskManager.add(task1);
        Task task2 = new Task("One more test task", "Description", 2, Status.NEW);
        taskManager.add(task2);

        final List<Task> allTasks1 = taskManager.getAllTasks();
        assertNotNull(allTasks1, "Задачи не возвращаются.");
        assertEquals(2, allTasks1.size(), "Неверное количество задач.");
        assertEquals(task1, taskManager.getTaskById(task1.getId()), "Задачи не совпадают");

        Task task3 = new Task("Task.Task for update", "Updated first task", task1.getId(), Status.NEW);
        taskManager.update(task3);

        final Task savedTask = taskManager.getTaskById(task3.getId());
        final List<Task> allTasks2 = taskManager.getAllTasks();
        assertNotNull(allTasks2, "Задачи не возвращаются.");
        assertEquals(2, allTasks2.size(), "Неверное количество задач.");
        assertEquals(allTasks2.getFirst().getTitle(), savedTask.getTitle(), "Названия задач не совпадают");
    }

    @Test
    void taskDoesNotChangeAfterAddingToTaskManager() {
        Task task = new Task("Купить овощи", "Картофель, морковь, лук", Status.NEW);
        taskManager.add(task);

        final Task savedTask = taskManager.getTaskById(task.getId());

        assertEquals(task.getTitle(), savedTask.getTitle(), "Названия задач не совпадают");
        assertEquals(task.getDescription(), savedTask.getDescription(), "Описания задач не совпадают");
        assertEquals(task.getStatus(), savedTask.getStatus(), "Статусы задач не совпадают");
        assertEquals(task.getId(), savedTask.getId(), "ID задач не совпадают");
    }

    @Test
    void shouldReturnSubtasksOfEpic() {
        Epic epic1 = new Epic("Task.Task.Epic for test", "Its description");
        taskManager.add(epic1);
        Subtask subtask1 = new Subtask("Test subtask", "And description", Status.NEW, epic1.getId());
        taskManager.add(subtask1);
        Subtask subtask2 = new Subtask("One more test subtask", "Description", Status.NEW, epic1.getId());
        taskManager.add(subtask2);
        Epic epic2 = new Epic("Epic2 for test", "Its description");
        taskManager.add(epic2);
        Subtask subtask3 = new Subtask("Task", "Does not belong to epic", Status.NEW, epic2.getId());
        taskManager.add(subtask3);
        List<Subtask> subtasksOfEpic = taskManager.getSubtasksOfEpic(epic1.getId());

        assertNotNull(subtasksOfEpic, "Не возвращает задачи эпика");
        assertEquals(2, subtasksOfEpic.size(), "Возвращает неверное количество задач");

    }

    @Test
    void shouldReturnEmptyListOfTasksAfterRemoval() {
        Task task1 = new Task("Test task", "And description", Status.NEW);
        taskManager.add(task1);
        Task task2 = new Task("One more test task", "Description", Status.NEW);
        taskManager.add(task2);
        Task task3 = new Task("Task ", "Something about description", Status.NEW);
        taskManager.add(task3);

        taskManager.removeAllTasks();
        assertTrue(taskManager.getAllTasks().isEmpty(), "Задачи не удалены");
    }

    @Test
    void shouldReturnEmptyListOfEpicsAfterRemoval() {
        Epic epic1 = new Epic("Task.Task.Epic for test", "Its description");
        taskManager.add(epic1);
        Epic epic2 = new Epic("Epic2 for test", "Its description");
        taskManager.add(epic2);

        taskManager.removeAllEpics();
        assertTrue(taskManager.getAllEpics().isEmpty(), "Эпики не удалены");
    }

    @Test
    void shouldReturnEmptyListOfSubtasksAfterRemoval() {
        Epic epic1 = new Epic("Task.Task.Epic for test", "Its description");
        taskManager.add(epic1);
        Subtask subtask1 = new Subtask("Test subtask", "And description", Status.NEW, epic1.getId());
        taskManager.add(subtask1);
        Subtask subtask2 = new Subtask("One more test subtask", "Description", Status.NEW, epic1.getId());
        taskManager.add(subtask2);

        taskManager.removeAllSubtasks();
        assertTrue(taskManager.getAllSubtasks().isEmpty(), "Подзадачи не удалены");

    }

    @Test
    void shouldUpdateAnOldTask() {
        Task task1 = new Task("Test task", "And description", Status.NEW);
        taskManager.add(task1);
        Task task2 = new Task("One more test task", "Description", task1.getId(), Status.NEW);
        taskManager.update(task2);

        List<Task> allTasks = taskManager.getAllTasks();

        assertEquals(1, allTasks.size(), "Задача не обновилась, а добавилась");
        assertEquals(task2.getTitle(), allTasks.getFirst().getTitle(), "Задача не обновилась");
        assertNotEquals(task1.getTitle(), allTasks.getFirst().getTitle());
    }

    @Test
    void shouldUpdateAnOldEpic() {
        Epic epic1 = new Epic("Task.Task.Epic for test", "Its description");
        taskManager.add(epic1);
        Epic epic2 = new Epic("Epic2 for test", "Its description", epic1.getId());
        taskManager.update(epic2);

        List<Epic> allEpics = taskManager.getAllEpics();

        assertEquals(1, allEpics.size(), "Эпик не обновился, а добавился");
        assertEquals(epic2.getTitle(), allEpics.getFirst().getTitle(), "Эпик не обновился");
        assertNotEquals(epic1.getTitle(), allEpics.getFirst().getTitle());
    }

    @Test
    void shouldUpdateAnOldSubtask() {
        Epic epic1 = new Epic("Task.Task.Epic for test", "Its description");
        taskManager.add(epic1);
        Subtask subtask1 = new Subtask("Test subtask", "And description", Status.NEW, epic1.getId());
        taskManager.add(subtask1);
        Subtask subtask2 = new Subtask("One more test subtask", "Description", subtask1.getId(), Status.NEW, epic1.getId());
        taskManager.update(subtask2);

        List<Subtask> allSubtasks = taskManager.getAllSubtasks();

        assertEquals(1, allSubtasks.size(), "Эпик не обновился, а добавился");
        assertEquals(subtask2.getTitle(), allSubtasks.getFirst().getTitle(), "Эпик не обновился");
        assertNotEquals(subtask1.getTitle(), allSubtasks.getFirst().getTitle());
    }

    @Test
    void shouldRemoveTaskById() {
        Task task1 = new Task("Test task", "And description", Status.NEW);
        taskManager.add(task1);
        List<Task> tasksBeforeRemoval = taskManager.getAllTasks();
        assertNotNull(tasksBeforeRemoval, "Задача не добавилась");
        taskManager.removeTaskById(task1.getId());
        List<Task> tasksAfterRemoval = taskManager.getAllTasks();
        assertTrue(tasksAfterRemoval.isEmpty(), "Удалить задачу не удалось");
    }

    @Test
    void shouldRemoveEpicById() {
        Epic epic1 = new Epic("Task.Task.Epic for test", "Its description");
        taskManager.add(epic1);
        List<Epic> epicsBeforeRemoval = taskManager.getAllEpics();
        assertNotNull(epicsBeforeRemoval, "Эпик не добавился");
        taskManager.removeEpicById(epic1.getId());
        List<Epic> epicsAfterRemoval = taskManager.getAllEpics();
        assertTrue(epicsAfterRemoval.isEmpty(), "Удалить эпик не удалось");
    }

    @Test
    void shouldRemoveSubtaskById() {
        Epic epic1 = new Epic("Task.Task.Epic for test", "Its description");
        taskManager.add(epic1);
        Subtask subtask1 = new Subtask("Test subtask", "And description", Status.NEW, epic1.getId());
        taskManager.add(subtask1);
        List<Subtask> subtasksBeforeRemoval = taskManager.getAllSubtasks();
        assertNotNull(subtasksBeforeRemoval, "Подзадача не добавилась");
        taskManager.removeSubtaskById(subtask1.getId());
        List<Subtask> subtasksAfterRemoval = taskManager.getAllSubtasks();
        assertTrue(subtasksAfterRemoval.isEmpty(), "Удалить подзадачу не удалось");
    }

    @Test
    void shouldNotStoreOldIdOfRemovedTask() {
        Task task1 = new Task("Test task", "And description", Status.NEW);
        taskManager.add(task1);
        taskManager.removeTaskById(task1.getId());
        Task task2 = new Task("One more test task", "Description", 2, Status.NEW);
        taskManager.add(task2);

        assertNotEquals(task1.getId(), task2.getId(), "ID совпадают");
    }

    @Test
    void shouldNotStoreRemovedIDsInsideEpic() {
        Epic epic1 = new Epic("Task.Task.Epic for test", "Its description");
        taskManager.add(epic1);
        Subtask subtask1 = new Subtask("Test subtask", "And description", Status.NEW, epic1.getId());
        taskManager.add(subtask1);
        Subtask subtask2 = new Subtask("One more test subtask", "Description", Status.NEW, epic1.getId());
        taskManager.add(subtask2);

        ArrayList<Integer> idsOfSubtasks = epic1.getSubTasksIds();
        assertEquals(2, idsOfSubtasks.size(), "Подзадачи не сохранились");

        taskManager.removeSubtaskById(subtask1.getId());
        ArrayList<Integer> idsOfSubtasksAfterRemoval = epic1.getSubTasksIds();
        assertEquals(1, idsOfSubtasksAfterRemoval.size(), "Подзадача не удалилась.");
        assertEquals(subtask2.getId(), idsOfSubtasksAfterRemoval.getFirst());
        assertNotEquals(subtask1.getId(), idsOfSubtasksAfterRemoval.getFirst());
    }
}