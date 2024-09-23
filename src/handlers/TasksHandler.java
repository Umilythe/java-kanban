package handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exeptions.ManagerCheckTimeException;
import exeptions.NotFoundException;
import manager.TaskManager;
import task.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {
    private TaskManager manager;
    private Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).serializeNulls().create();

    public TasksHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String url = exchange.getRequestURI().getPath();
        switch (method) {
            case "GET":
                handleGetTasks(exchange, url);
                break;
            case "POST":
                handlePostTasks(exchange);
                break;
            case "DELETE":
                handleDeleteTasks(exchange, url);
                break;
            default:
                super.writeResponse(exchange, "Неверный запрос, попробуйте еще раз.", 404);
        }
    }

    public void handleGetTasks(HttpExchange exchange, String url) throws IOException {
        String[] urlParts = url.split("/");
        if (urlParts.length == 2) {
            List<Task> tasks = manager.getAllTasks();
            if (tasks.isEmpty()) {
                writeResponse(exchange, "Список задач пуст", 200);
            } else {
                sendText(exchange, gson.toJson(tasks));
            }
        } else if (urlParts.length == 3) {
            try {
                int taskId = Integer.parseInt(urlParts[2]);
                Task task = manager.getTaskById(taskId);
                sendText(exchange, gson.toJson(task));
            } catch (NotFoundException e) {
                writeResponse(exchange, "Not found.", 404);
            }
        }
    }

    public void handlePostTasks(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        Task task = gson.fromJson(body, Task.class);
        try {
            if (task.getId() == 0) {
                manager.add(task);
                List<Task> tasks = manager.getAllTasks();
                int requiredId = 0;
                for (Task task1 : tasks) {
                    if (task.equals(task1)) {
                        requiredId = task1.getId();
                    }
                }
                writeResponse(exchange, "Task " + requiredId + " has been successfully added.", 201);
            } else {
                manager.update(task);
                writeResponse(exchange, "Task " + task.getId() + " has been successfully updated.", 201);
            }
        } catch (ManagerCheckTimeException e) {
            writeResponse(exchange, "Not Acceptable", 406);
        }
    }

    public void handleDeleteTasks(HttpExchange exchange, String url) throws IOException {
        String[] urlParts = url.split("/");
        try {
            if (urlParts.length == 2) {
                manager.removeAllTasks();
                writeResponse(exchange, "All tasks have been removed.", 200);
            } else if (urlParts.length == 3) {
                int taskId = Integer.parseInt(urlParts[2]);
                manager.removeTaskById(taskId);
                writeResponse(exchange, "Task with " + taskId + " ID has been removed.", 200);
            }
        } catch (IOException e) {
            writeResponse(exchange, "Error", 500);
        }

    }
}
