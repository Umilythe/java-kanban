import manager.FileBackedTaskManager;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;

public class FileBackedTaskManagerTest extends TaskManagerTest {
    @BeforeEach
    public void beforeEach() {
        try {
            File tmpFile = File.createTempFile("test", null);
            manager = new FileBackedTaskManager(tmpFile);
        } catch (IOException exception) {
            System.out.println("файл не может быть создан");
        }
    }

    @Test
    void shouldCreateAndLoadToANdFromFile() {
        try {
            File tmpFile = File.createTempFile("test", null);
            FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(tmpFile);
            Task task1 = new Task("Test task", "And description", Status.NEW, 15, LocalDateTime.of(2024, Month.AUGUST, 31, 0, 0));
            fileBackedTaskManager.add(task1);
            Epic epic1 = new Epic("Epic for test", "Its description");
            fileBackedTaskManager.add(epic1);
            Subtask subtask1 = new Subtask("Test subtask", "And description", Status.NEW, 10, LocalDateTime.of(2024, Month.SEPTEMBER, 9, 9, 0), epic1.getId());
            fileBackedTaskManager.add(subtask1);

            ArrayList<Task> tasksToFile = fileBackedTaskManager.getAllTasks();
            ArrayList<Epic> epicsToFile = fileBackedTaskManager.getAllEpics();
            ArrayList<Subtask> subtasksToFile = fileBackedTaskManager.getAllSubtasks();

            FileBackedTaskManager fBTaskManager = fileBackedTaskManager.loadFromFile(tmpFile);
            ArrayList<Task> tasksFromFile = fBTaskManager.getAllTasks();
            ArrayList<Epic> epicsFromFile = fBTaskManager.getAllEpics();
            ArrayList<Subtask> subtasksFromFile = fBTaskManager.getAllSubtasks();

            assertNotNull(tasksToFile, "Задачи не записались.");
            assertNotNull(epicsToFile, "Эпики не записались.");
            assertNotNull(subtasksToFile, "Подзадачи не записались.");

            assertNotNull(tasksFromFile, "Задачи не загрузились.");
            assertNotNull(epicsFromFile, "Эпики не загрузились.");
            assertNotNull(subtasksFromFile, "Подзадачи не загрузились.");

            assertEquals(tasksToFile, tasksFromFile, "Сохранились не одинаковые задачи.");
            assertEquals(epicsToFile, epicsFromFile, "Сохранились не одинаковые эпики");
            assertEquals(subtasksToFile, subtasksFromFile, "Сохранились не одинаковые подзадачи.");
        } catch (IOException exception) {
            System.out.println("файл не может быть создан");
        }
    }
}
