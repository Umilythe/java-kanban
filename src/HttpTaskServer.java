import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import exeptions.NotFoundException;
import handlers.*;
import manager.Managers;
import manager.TaskManager;
import task.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.time.Month;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private HttpServer httpServer;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TasksHandler(taskManager));
        httpServer.createContext("/subtasks", new SubtasksHandler(taskManager));
        httpServer.createContext("/epics", new EpicsHandler(taskManager));
        httpServer.createContext("/history", new HistoryHandler(taskManager));
        httpServer.createContext("/prioritized", new PrioritizedTasksHandler(taskManager));
    }

    public void start() {
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(1);
    }

    public static void main(String[] args) throws IOException {
        TaskManager taskManager = Managers.getDefault();
        HttpTaskServer server = new HttpTaskServer(taskManager);
        System.out.println("Поехали!");
        Task task1 = new Task("Уборка", "Влажная уборка комнат, разобрать шкаф", Status.NEW, 30, LocalDateTime.now());
        taskManager.add(task1);
        Task task2 = new Task("Стирка", "Цветных вещей", Status.NEW, 80, LocalDateTime.of(2024, Month.MAY, 5, 10, 0));
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

        taskManager.removeTaskById(task2.getId());
        try {
            taskManager.getTaskById(2);
        } catch (NotFoundException e) {
            System.out.println(e.getMessage());
        }
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
        //taskManager.removeEpicById(epic1.getId());
        Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).serializeNulls().excludeFieldsWithoutExposeAnnotation().create();
        System.out.println(gson.toJson(subtask2));
        printAllTasks(taskManager);

        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
        server.start();
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

