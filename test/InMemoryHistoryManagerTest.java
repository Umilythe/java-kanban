import manager.Managers;
import manager.TaskManager;
import task.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    TaskManager taskManager;

    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getDefault();
    }

    @Test
    void shouldAddTasksToHistory() {
        Task task = new Task("Слушать подкаст", "По истории", Status.NEW);
        taskManager.add(task);
        taskManager.getTaskById(task.getId());
        final List<Task> history = taskManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    @Test
    void shouldNotReturnOldTasksVersions() {
        Task task1 = new Task("Старая задача", "И описание у неё старое", Status.NEW);
        taskManager.add(task1);
        taskManager.getTaskById(task1.getId());
        Task task2 = new Task("Новая задача", "И описание новое", task1.getId(), Status.IN_PROGRESS);
        taskManager.update(task2);
        taskManager.getTaskById(task2.getId());
        List<Task> tasks = taskManager.getHistory();
        assertEquals("Новая задача", tasks.getFirst().getTitle());
        assertEquals(task2, tasks.getFirst());

    }

    @Test
    void shouldAddAnyTypeToHistory() {
        Epic epic = new Epic("Task.Task.Epic for test", "Its description");
        taskManager.add(epic);
        taskManager.getEpicById(epic.getId());
        Subtask subtask1 = new Subtask("First subtask of test epic", "Its description", Status.NEW, epic.getId());
        taskManager.add(subtask1);
        taskManager.getSubtaskById(subtask1.getId());
        Task task1 = new Task("Test task", "And description", Status.NEW);
        taskManager.add(task1);
        taskManager.getTaskById(task1.getId());

        List<Task> historyList = taskManager.getHistory();
        assertEquals(3, historyList.size());
        assertEquals(epic, historyList.getFirst());
        assertEquals(subtask1, historyList.get(1));
        assertEquals(task1, historyList.get(2));

    }

    @Test
    void shouldRemoveTask() {
        Task task = new Task("Test task", "And description", Status.NEW);
        taskManager.add(task);
        taskManager.getTaskById(task.getId());
        Epic epic = new Epic("Task.Task.Epic for test", "Its description");
        taskManager.add(epic);
        taskManager.getEpicById(epic.getId());
        Subtask subtask = new Subtask("First subtask of test epic", "Its description", Status.NEW, epic.getId());
        taskManager.add(subtask);
        taskManager.getSubtaskById(subtask.getId());
        List<Task> listBeforeRemoval = taskManager.getHistory();
        assertEquals(3, listBeforeRemoval.size(), "Не все задачи попали в истрию.");

        taskManager.removeTaskById(task.getId());
        List<Task> listAfterTaskRemoval = taskManager.getHistory();
        assertEquals(2, listAfterTaskRemoval.size(), "Задача не удалилась.");
        assertEquals(epic, listAfterTaskRemoval.getFirst(), "Удалилась не задача.");
        assertEquals(subtask, listAfterTaskRemoval.get(1), "Удалилась не задача.");
    }

    @Test
    void shouldRemoveEpic() {
        Task task = new Task("Test task", "And description", Status.NEW);
        taskManager.add(task);
        taskManager.getTaskById(task.getId());
        Epic epic = new Epic("Task.Task.Epic for test", "Its description");
        taskManager.add(epic);
        taskManager.getEpicById(epic.getId());
        Subtask subtask = new Subtask("First subtask of test epic", "Its description", Status.NEW, epic.getId());
        taskManager.add(subtask);
        taskManager.getSubtaskById(subtask.getId());
        List<Task> listBeforeRemoval = taskManager.getHistory();
        assertEquals(3, listBeforeRemoval.size(), "Не все задачи попали в истрию.");

        taskManager.removeEpicById(epic.getId());
        List<Task> listAfterEpicRemoval = taskManager.getHistory();
        assertEquals(1, listAfterEpicRemoval.size(), "Эпик не удалился.");
        assertEquals(task, listAfterEpicRemoval.getFirst(), "Удалился не эпик.");

    }

    @Test
    void shouldRemoveSubtask() {
        Task task = new Task("Test task", "And description", Status.NEW);
        taskManager.add(task);
        taskManager.getTaskById(task.getId());
        Epic epic = new Epic("Task.Task.Epic for test", "Its description");
        taskManager.add(epic);
        taskManager.getEpicById(epic.getId());
        Subtask subtask = new Subtask("First subtask of test epic", "Its description", Status.NEW, epic.getId());
        taskManager.add(subtask);
        taskManager.getSubtaskById(subtask.getId());
        List<Task> listBeforeRemoval = taskManager.getHistory();
        assertEquals(3, listBeforeRemoval.size(), "Не все задачи попали в истрию.");

        taskManager.removeTaskById(task.getId());
        List<Task> listAfterSubtaskRemoval = taskManager.getHistory();
        assertEquals(2, listAfterSubtaskRemoval.size(), "Подзадача не удалилась.");
        assertEquals(epic, listAfterSubtaskRemoval.getFirst(), "Удалилась не подзадача.");
        assertEquals(subtask, listAfterSubtaskRemoval.get(1), "Удалилась не подзадача.");
    }


}