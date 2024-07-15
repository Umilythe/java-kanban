import Manager.Managers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ManagersTest {

    @Test
    void shouldCreateInitializedInMemoryTaskManager() {
        Assertions.assertNotNull(Managers.getDefault(), "Объект не создан");
    }

    @Test
    void shouldCreateInitializedInMemoryHistoryManager() {
        Assertions.assertNotNull(Managers.getDefaultHistory(), "Объект не создан");
    }

}