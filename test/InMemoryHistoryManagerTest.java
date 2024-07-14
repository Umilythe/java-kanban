import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
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
        final List<Task> history = taskManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

@Test
    void shouldReturnOldTasksVersions() {
        Task task1 = new Task("Старая задача", "И описание у неё старое", Status.NEW);
        taskManager.add(task1);
        Task savedTask = taskManager.getTaskById(task1.getId());
    taskManager.getTaskById(task1.getId());
    Task task2 = new Task("Новая задача", "И описание новое", task1.getId(), Status.IN_PROGRESS);
    taskManager.update(task2);
    taskManager.getTaskById(task2.getId());
    final List<Task> tasks = taskManager.getHistory();
    assertEquals(savedTask.getTitle(), tasks.get(0).getTitle());
    assertEquals(savedTask, tasks.get(0));

}

@Test
    void shouldAddAnyTypeToHistory() {
    Epic epic = new Epic("Epic for test", "Its description");
    taskManager.add(epic);
    taskManager.getEpicById(epic.getId());
    Subtask subtask1 = new Subtask("First subtask of test epic", "Its description", Status.NEW, epic.getId());
    taskManager.add(subtask1);
    taskManager.getSubtaskById(subtask1.getId());
    Task task1 = new Task("Test task", "And description", Status.NEW);
    taskManager.add(task1);
    taskManager.getTaskById(task1.getId());

    ArrayList<Task> historyList = taskManager.getHistory();
    assertEquals(3, historyList.size());
    assertEquals(epic, historyList.getFirst());
    assertEquals(subtask1, historyList.get(1));
    assertEquals(task1, historyList.get(2));

}

}