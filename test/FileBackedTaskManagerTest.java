import manager.FileBackedTaskManager;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.Subtask;
import task.Task;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class FileBackedTaskManagerTest {

    @Test
    void shouldCreateAndLoadToANdFromFile() {
        try {
            File tmpFile = File.createTempFile("test", null);
            FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(tmpFile);
            Task task1 = new Task("Test task", "And description", Status.NEW);
            fileBackedTaskManager.add(task1);
            Epic epic1 = new Epic("Epic for test", "Its description");
            fileBackedTaskManager.add(epic1);
            Subtask subtask1 = new Subtask("Test subtask", "And description", Status.NEW, epic1.getId());
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
