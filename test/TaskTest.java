import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void tasksWithSameIdShouldBeEqual() {
        Task task1 = new Task("Найти материалы по насосам", "Центробежным и плунжерным", 1, Status.NEW);
        Task task2 = new Task("Полить цветы", "Алоэ, фиалки", 1, Status.NEW);
        Assertions.assertEquals(task1, task2, "Задачи не равны");
    }
}