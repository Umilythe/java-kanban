import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        System.out.println("Поехали!");
        Task task1 = new Task("Уборка", "Влажная уборка комнат, разобрать шакаф", Status.NEW);
        TaskManager taskManager = new TaskManager();
        taskManager.add(task1);
        Task task2 = new Task("Стирка", "Цветных вещей", Status.NEW);
        taskManager.add(task2);

        Epic epic1 = new Epic("Построить домик", "Для кошки", Status.NEW);
        taskManager.add(epic1);
        Subtask subtask1 = new Subtask("Закупить материалы", "Доски, гвозди", Status.NEW, epic1.getId());
        taskManager.add(subtask1);
        Subtask subtask2 = new Subtask("Сделать чертеж", "В autocad", Status.NEW, epic1.getId());
        taskManager.add(subtask2);

        Epic epic2 = new Epic("Подготовиться к лекции", "По основам физики реакторов", Status.DONE);
        taskManager.add(epic2);
        Subtask subtask3 = new Subtask("Собрать материалы", "На общем диске", Status.NEW, epic2.getId());
        taskManager.add(subtask3);

        Task task3 = new Task("Стирка", "Цветных вещей", task2.getId(), Status.IN_PROGRESS);
        taskManager.update(task3);


        Subtask subtask4 = new Subtask("Сделать чертеж", "В autocad", subtask2.getId(), Status.IN_PROGRESS, epic1.getId());
        taskManager.update(subtask4);



    }
}

