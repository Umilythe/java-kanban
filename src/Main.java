import Manager.Managers;
import Manager.TaskManager;
import Task.*;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();
        System.out.println("Поехали!");
        Task task1 = new Task("Уборка", "Влажная уборка комнат, разобрать шакаф", Status.NEW);
        //Manager.TaskManager taskManager = new Manager.InMemoryTaskManager();
        taskManager.add(task1);
        Task task2 = new Task("Стирка", "Цветных вещей", Status.NEW);
        taskManager.add(task2);
        taskManager.getTaskById(task1.getId());

        Epic epic1 = new Epic("Построить домик", "Для кошки");
        taskManager.add(epic1);
        Subtask subtask1 = new Subtask("Закупить материалы", "Доски, гвозди", Status.NEW, epic1.getId());
        taskManager.add(subtask1);
        Subtask subtask2 = new Subtask("Сделать чертеж", "В autocad", Status.NEW, epic1.getId());
        taskManager.add(subtask2);

        Epic epic2 = new Epic("Подготовиться к лекции", "По основам физики реакторов");
        taskManager.add(epic2);
        Subtask subtask3 = new Subtask("Собрать материалы", "На общем диске", Status.NEW, epic2.getId());
        taskManager.add(subtask3);

        Task task3 = new Task("548", "Цветных вещей", Status.IN_PROGRESS);
        taskManager.add(task3);


        Subtask subtask4 = new Subtask("Сделать чертеж", "В autocad", Status.IN_PROGRESS, epic1.getId());
        taskManager.add(subtask4);

       Task task4 = new Task("123", "456", 1, Status.NEW);
       taskManager.update(task4);

        taskManager.getTaskById(2);
        taskManager.getEpicById(3);
        taskManager.getEpicById(3);
        taskManager.getEpicById(6);
        taskManager.getSubtaskById(4);
        taskManager.getSubtaskById(5);
        taskManager.getSubtaskById(5);
        taskManager.getSubtaskById(7);
        taskManager.getTaskById(8);
        taskManager.getSubtaskById(9);
        taskManager.getTaskById(task4.getId());

        printAllTasks(taskManager);
    }
    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getAllEpics()) {
            System.out.println(epic);

            for (Task task : manager.getSubtasksOfEpic(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getAllSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}

