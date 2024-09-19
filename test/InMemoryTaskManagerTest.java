import manager.InMemoryTaskManager;
import manager.Managers;
import task.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;


class InMemoryTaskManagerTest extends TaskManagerTest {

    @BeforeEach
    void beforeEach() {
        manager = Managers.getDefault();
    }

    @Test
    void shouldCheckTimeIntersection() {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
        Task task1 = new Task("Task one", "Task for intersection", Status.NEW, 60, LocalDateTime.of(2024, Month.SEPTEMBER, 9, 12, 0));
        inMemoryTaskManager.add(task1);
        Task task2 = new Task("Task two", "Shouldn't intersect with the first one", Status.NEW, 15, LocalDateTime.of(2024, Month.SEPTEMBER, 9, 10, 0));
        Task task3 = new Task("Task two", "Shouldn't intersect with the first one", Status.NEW, 15, LocalDateTime.of(2024, Month.SEPTEMBER, 9, 12, 45));

        boolean tasksDontIntersect = inMemoryTaskManager.checkTaskTimeIntersection(task2);
        assertTrue(tasksDontIntersect, "Задачи пересекаются по времени.");

        boolean tasksIntersect = inMemoryTaskManager.checkTaskTimeIntersection(task3);
        assertFalse(tasksIntersect, "Задачи не пересекаются по времни");
    }
}
