import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    Epic epic1 = new Epic("Получить новую профессию", "В абсолютно новой сфере", 1);
    Epic epic2 = new Epic("Нарисовать картину", "Пейзаж", 1);

    @Test
    void twoEpicsWithSameIDShouldBeEqual() {
        Assertions.assertEquals(epic1, epic2, "Эпики не равны");
    }

}