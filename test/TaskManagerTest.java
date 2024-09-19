import manager.TaskManager;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest <T extends TaskManager> {

    protected T manager;

    protected Task task = new Task("Test Task", "Test Task description", Status.NEW, 10, LocalDateTime.of(2024, Month.SEPTEMBER, 8, 15, 0));
    protected Epic epic = new Epic("Epic for test", "Its description");
    protected Subtask subtask1 = new Subtask("First subtask of test epic", "Its description", Status.NEW, 10, LocalDateTime.of(2024, Month.SEPTEMBER, 9, 10, 0), epic.getId());


    @Test
    void addTask() {
        manager.add(task);
        final int taskId = task.getId();

        final Task savedTask = manager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = manager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.getFirst(), "Задачи не совпадают.");

    }

    @Test
    void addEpicAndSubtask() {
        Epic epic = new Epic("Epic for test", "Its description");
        manager.add(epic);
        Subtask subtask1 = new Subtask("First subtask of test epic", "Its description", Status.NEW, 10, LocalDateTime.of(2024, Month.SEPTEMBER, 9, 10, 0), epic.getId());
        manager.add(subtask1);
        Subtask subtask2 = new Subtask("Second subtask of test epic", "And its description", Status.NEW, 15, LocalDateTime.of(2024, Month.SEPTEMBER, 10, 11, 20), epic.getId());
        manager.add(subtask2);

        final Epic savedEpic = manager.getEpicById(epic.getId());
        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");

        final Subtask savedSubtask1 = manager.getSubtaskById(subtask1.getId());
        assertNotNull(savedSubtask1, "Подзадача не найдена.");
        assertEquals(subtask1, savedSubtask1, "Подзадачи не совпадают.");
        final Subtask savedSubtask2 = manager.getSubtaskById(subtask2.getId());
        assertNotNull(savedSubtask2, "Подзадача не найдена.");
        assertEquals(subtask2, savedSubtask2, "Подзадачи не совпадают.");

        final List<Epic> epics = manager.getAllEpics();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.getFirst(), "Эпики не совпадают.");

        final List<Subtask> subtasks = manager.getAllSubtasks();

        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(2, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask1, subtasks.getFirst(), "Подзадачи не совпадают.");
        assertEquals(subtask2, subtasks.get(1), "Подзадачи не совпадают.");

    }

    @Test
    void shouldNotAddEpicAsSubtask() {
        manager.add(epic);
        Epic epic2 = new Epic("One more epic for test", "Wow description", epic.getId());
        manager.add(epic2);

        final List<Integer> subtasksIds = epic.getSubTasksIds();

        assertTrue(subtasksIds.isEmpty(), "Задачи у эпика есть");
    }

    @Test
    void tasksWithSetIdAndGeneratedIdDoNotHaveConflict() {
        manager.add(task);
        Task task2 = new Task("One more test task", "Description", 2, Status.NEW, 14, LocalDateTime.of(2024, Month.SEPTEMBER, 1, 12, 0));
        manager.add(task2);

        final List<Task> allTasks1 = manager.getAllTasks();
        assertNotNull(allTasks1, "Задачи не возвращаются.");
        assertEquals(2, allTasks1.size(), "Неверное количество задач.");
        assertEquals(task, manager.getTaskById(task.getId()), "Задачи не совпадают");

        Task task3 = new Task("Task.Task for update", "Updated first task", task.getId(), Status.NEW, 20, LocalDateTime.of(2024, Month.SEPTEMBER, 5, 10, 0));
        manager.update(task3);

        final Task savedTask = manager.getTaskById(task3.getId());
        final List<Task> allTasks2 = manager.getAllTasks();
        assertNotNull(allTasks2, "Задачи не возвращаются.");
        assertEquals(2, allTasks2.size(), "Неверное количество задач.");
        assertEquals(allTasks2.getFirst().getTitle(), savedTask.getTitle(), "Названия задач не совпадают");
    }

    @Test
    void taskDoesNotChangeAfterAddingToTaskManager() {
        Task task1 = new Task("Купить овощи", "Картофель, морковь, лук", Status.NEW, 23, LocalDateTime.now());
        manager.add(task1);

        final Task savedTask = manager.getTaskById(task1.getId());

        assertEquals(task1.getTitle(), savedTask.getTitle(), "Названия задач не совпадают");
        assertEquals(task1.getDescription(), savedTask.getDescription(), "Описания задач не совпадают");
        assertEquals(task1.getStatus(), savedTask.getStatus(), "Статусы задач не совпадают");
        assertEquals(task1.getId(), savedTask.getId(), "ID задач не совпадают");
    }

    @Test
    void shouldReturnSubtasksOfEpic() {
        epic = new Epic("Epic for test", "Its description");
        manager.add(epic);
        Subtask subtask1 = new Subtask("First subtask of test epic", "Its description", Status.NEW, 10, LocalDateTime.of(2024, Month.SEPTEMBER, 9, 10, 0), epic.getId());
        manager.add(subtask1);
        Subtask subtask2 = new Subtask("One more test subtask", "Description", Status.NEW, 15, LocalDateTime.of(2024, Month.SEPTEMBER, 10, 11, 20), epic.getId());
        manager.add(subtask2);
        Epic epic2 = new Epic("Epic2 for test", "Its description");
        manager.add(epic2);
        Subtask subtask3 = new Subtask("task", "Does not belong to epic", Status.NEW, 20, LocalDateTime.of(2024, Month.SEPTEMBER, 11, 9, 15), epic2.getId());
        manager.add(subtask3);
        List<Subtask> subtasksOfEpic = manager.getSubtasksOfEpic(epic.getId());

        assertNotNull(subtasksOfEpic, "Не возвращает задачи эпика");
        assertEquals(2, subtasksOfEpic.size(), "Возвращает неверное количество задач");

    }

    @Test
    void shouldReturnEmptyListOfTasksAfterRemoval() {
        manager.add(task);
        Task task2 = new Task("One more test task", "Description", Status.NEW);
        manager.add(task2);
        Task task3 = new Task("task ", "Something about description", Status.NEW);
        manager.add(task3);

        manager.removeAllTasks();
        assertTrue(manager.getAllTasks().isEmpty(), "Задачи не удалены");
    }

    @Test
    void shouldReturnEmptyListOfEpicsAfterRemoval() {
        Epic epic1 = new Epic("Task.Task.Epic for test", "Its description");
        manager.add(epic1);
        Epic epic2 = new Epic("Epic2 for test", "Its description");
        manager.add(epic2);

        manager.removeAllEpics();
        assertTrue(manager.getAllEpics().isEmpty(), "Эпики не удалены");
    }

    @Test
    void shouldReturnEmptyListOfSubtasksAfterRemoval() {
        Epic epic1 = new Epic("Task test", "Its description");
        manager.add(epic1);
        Subtask subtask1 = new Subtask("Test subtask", "And description", Status.NEW, 10, LocalDateTime.of(2024, Month.SEPTEMBER, 9, 10, 0), epic1.getId());
        manager.add(subtask1);
        Subtask subtask2 = new Subtask("One more test subtask", "Description", Status.NEW, 15, LocalDateTime.of(2024, Month.SEPTEMBER, 10, 11, 20), epic1.getId());
        manager.add(subtask2);

        manager.removeAllSubtasks();
        assertTrue(manager.getAllSubtasks().isEmpty(), "Подзадачи не удалены");

    }

    @Test
    void shouldUpdateAnOldTask() {
        Task task1 = new Task("Test task", "And description", Status.NEW);
        manager.add(task1);
        Task task2 = new Task("One more test task", "Description", task1.getId(), Status.NEW);
        manager.update(task2);

        List<Task> allTasks = manager.getAllTasks();

        assertEquals(1, allTasks.size(), "Задача не обновилась, а добавилась");
        assertEquals(task2.getTitle(), allTasks.getFirst().getTitle(), "Задача не обновилась");
        assertNotEquals(task1.getTitle(), allTasks.getFirst().getTitle());
    }

    @Test
    void shouldUpdateAnOldEpic() {
        Epic epic1 = new Epic("Task.Task.Epic for test", "Its description");
        manager.add(epic1);
        Epic epic2 = new Epic("Epic2 for test", "Its description", epic1.getId());
        manager.update(epic2);

        List<Epic> allEpics = manager.getAllEpics();

        assertEquals(1, allEpics.size(), "Эпик не обновился, а добавился");
        assertEquals(epic2.getTitle(), allEpics.getFirst().getTitle(), "Эпик не обновился");
        assertNotEquals(epic1.getTitle(), allEpics.getFirst().getTitle());
    }

    @Test
    void shouldUpdateAnOldSubtask() {
        Epic epic1 = new Epic("Task.Task.Epic for test", "Its description");
        manager.add(epic1);
        Subtask subtask1 = new Subtask("Test subtask", "And description", Status.NEW, 10, LocalDateTime.of(2024, Month.SEPTEMBER, 9, 10, 0), epic1.getId());
        manager.add(subtask1);
        Subtask subtask2 = new Subtask("One more test subtask", "Description", subtask1.getId(), Status.NEW, 15, LocalDateTime.of(2024, Month.SEPTEMBER, 10, 11, 20), epic1.getId());
        manager.update(subtask2);

        List<Subtask> allSubtasks = manager.getAllSubtasks();

        assertEquals(1, allSubtasks.size(), "Эпик не обновился, а добавился");
        assertEquals(subtask2.getTitle(), allSubtasks.getFirst().getTitle(), "Эпик не обновился");
        assertNotEquals(subtask1.getTitle(), allSubtasks.getFirst().getTitle());
    }

    @Test
    void shouldRemoveTaskById() {
        Task task1 = new Task("Test task", "And description", Status.NEW);
        manager.add(task1);
        List<Task> tasksBeforeRemoval = manager.getAllTasks();
        assertNotNull(tasksBeforeRemoval, "Задача не добавилась");
        manager.removeTaskById(task1.getId());
        List<Task> tasksAfterRemoval = manager.getAllTasks();
        assertTrue(tasksAfterRemoval.isEmpty(), "Удалить задачу не удалось");
    }

    @Test
    void shouldRemoveEpicById() {
        Epic epic1 = new Epic("Task.Task.Epic for test", "Its description");
        manager.add(epic1);
        List<Epic> epicsBeforeRemoval = manager.getAllEpics();
        assertNotNull(epicsBeforeRemoval, "Эпик не добавился");
        manager.removeEpicById(epic1.getId());
        List<Epic> epicsAfterRemoval = manager.getAllEpics();
        assertTrue(epicsAfterRemoval.isEmpty(), "Удалить эпик не удалось");
    }

    @Test
    void shouldRemoveSubtaskById() {
        Epic epic1 = new Epic("Task.Task.Epic for test", "Its description");
        manager.add(epic1);
        Subtask subtask1 = new Subtask("Test subtask", "And description", Status.NEW, 10, LocalDateTime.of(2024, Month.SEPTEMBER, 9, 10, 0), epic1.getId());
        manager.add(subtask1);
        List<Subtask> subtasksBeforeRemoval = manager.getAllSubtasks();
        assertNotNull(subtasksBeforeRemoval, "Подзадача не добавилась");
        manager.removeSubtaskById(subtask1.getId());
        List<Subtask> subtasksAfterRemoval = manager.getAllSubtasks();
        assertTrue(subtasksAfterRemoval.isEmpty(), "Удалить подзадачу не удалось");
    }

    @Test
    void shouldNotStoreOldIdOfRemovedTask() {
        Task task1 = new Task("Test task", "And description", Status.NEW);
        manager.add(task1);
        manager.removeTaskById(task1.getId());
        Task task2 = new Task("One more test task", "Description", 2, Status.NEW);
        manager.add(task2);

        assertNotEquals(task1.getId(), task2.getId(), "ID совпадают");
    }

    @Test
    void shouldNotStoreRemovedIDsInsideEpic() {
        Epic epic1 = new Epic("Task.Task.Epic for test", "Its description");
        manager.add(epic1);
        Subtask subtask1 = new Subtask("Test subtask", "And description", Status.NEW, 10, LocalDateTime.now(), epic1.getId());
        manager.add(subtask1);
        Subtask subtask2 = new Subtask("One more test subtask", "Description", Status.NEW, 20, LocalDateTime.of(2024, Month.SEPTEMBER, 8, 16, 50), epic1.getId());
        manager.add(subtask2);

        ArrayList<Integer> idsOfSubtasks = epic1.getSubTasksIds();
        assertEquals(2, idsOfSubtasks.size(), "Подзадачи не сохранились");

        manager.removeSubtaskById(subtask1.getId());
        ArrayList<Integer> idsOfSubtasksAfterRemoval = epic1.getSubTasksIds();
        assertEquals(1, idsOfSubtasksAfterRemoval.size(), "Подзадача не удалилась.");
        assertEquals(subtask2.getId(), idsOfSubtasksAfterRemoval.getFirst());
        assertNotEquals(subtask1.getId(), idsOfSubtasksAfterRemoval.getFirst());
    }

    @Test
    void shouldCheckNEWStatusOfEPic() {
        Epic epic1 = new Epic("Task.Task.Epic for test", "Its description");
        manager.add(epic1);
        Subtask subtask1 = new Subtask("Test subtask", "And description", Status.NEW, 10, LocalDateTime.now(), epic1.getId());
        manager.add(subtask1);
        Subtask subtask2 = new Subtask("One more test subtask", "Description", Status.NEW, 20, LocalDateTime.of(2024, Month.SEPTEMBER, 8, 16, 50), epic1.getId());
        manager.add(subtask2);

        assertEquals(Status.NEW, epic1.getStatus(), "Не правильный статус эпика.");
    }

    @Test
    void shouldCheckDONEStatusOfEPic() {
        Epic epic1 = new Epic("Task.Task.Epic for test", "Its description");
        manager.add(epic1);
        Subtask subtask1 = new Subtask("Test subtask", "And description", Status.DONE, 10, LocalDateTime.now(), epic1.getId());
        manager.add(subtask1);
        Subtask subtask2 = new Subtask("One more test subtask", "Description", Status.DONE, 20, LocalDateTime.of(2024, Month.SEPTEMBER, 8, 16, 50), epic1.getId());
        manager.add(subtask2);

        assertEquals(Status.DONE, epic1.getStatus(), "Не правильный статус эпика.");
    }

    @Test
    void shouldCheckINPROGRESSStatusOfEPic() {
        Epic epic1 = new Epic("Task.Task.Epic for test", "Its description");
        manager.add(epic1);
        Subtask subtask1 = new Subtask("Test subtask", "And description", Status.DONE, 10, LocalDateTime.now(), epic1.getId());
        manager.add(subtask1);
        Subtask subtask2 = new Subtask("One more test subtask", "Description", Status.NEW, 20, LocalDateTime.of(2024, Month.SEPTEMBER, 8, 16, 50), epic1.getId());
        manager.add(subtask2);

        assertEquals(Status.IN_PROGRESS, epic1.getStatus(), "Не правильный статус эпика.");
    }

    @Test
    void shouldCheckINProgressStatusOfEPicOneMoreTime() {
        Epic epic1 = new Epic("Task.Task.Epic for test", "Its description");
        manager.add(epic1);
        Subtask subtask1 = new Subtask("Test subtask", "And description", Status.IN_PROGRESS, 10, LocalDateTime.now(), epic1.getId());
        manager.add(subtask1);
        Subtask subtask2 = new Subtask("One more test subtask", "Description", Status.IN_PROGRESS, 20, LocalDateTime.of(2024, Month.SEPTEMBER, 8, 16, 50), epic1.getId());
        manager.add(subtask2);

        assertEquals(Status.IN_PROGRESS, epic1.getStatus(), "Не правильный статус эпика.");
    }

}
