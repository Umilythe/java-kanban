import task.Status;
import task.Subtask;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SubtaskTest {

    @Test
    void twoSubtasksWithSameIdShouldBeEqual() {
        Subtask subtask1 = new Subtask("Купить краски", "Масляные", 2, Status.NEW, 2);
        Subtask subtask2 = new Subtask("Купить холст", "Срочно", 2, Status.NEW, 2);
        Assertions.assertEquals(subtask2, subtask1, "Подзадачи не равны");
    }

}