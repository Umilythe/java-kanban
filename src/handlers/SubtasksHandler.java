package handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exeptions.ManagerCheckTimeException;
import exeptions.NotFoundException;
import manager.TaskManager;
import task.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {
    private TaskManager manager;
    private Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).serializeNulls().excludeFieldsWithoutExposeAnnotation().create();

    public SubtasksHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String url = exchange.getRequestURI().getPath();
        switch (method) {
            case "GET":
                handleGetSubtasks(exchange, url);
                break;
            case "POST":
                handlePostSubtasks(exchange, url);
                break;
            case "DELETE":
                handleDeleteSubtasks(exchange, url);
                break;
            default:
                super.writeResponse(exchange, "Неверный запрос, попробуйте еще раз.", 404);
        }
    }

    public void handleGetSubtasks(HttpExchange exchange, String url) throws IOException {
        String[] urlParts = url.split("/");
        if (urlParts.length == 2) {
            List<Subtask> subtasks = manager.getAllSubtasks();
            if (subtasks.isEmpty()) {
                writeResponse(exchange, "Список подзадач пуст", 200);
            } else {
                sendText(exchange, gson.toJson(subtasks));
            }
        } else if (urlParts.length == 3) {
            try {
                int subtaskId = Integer.parseInt(urlParts[2]);
                Subtask subtask = manager.getSubtaskById(subtaskId);
                sendText(exchange, gson.toJson(subtask));
            } catch (NotFoundException e) {
                writeResponse(exchange, "Not found.", 404);
            }
        }
    }

    public void handlePostSubtasks(HttpExchange exchange, String url) throws IOException {
        String[] urlParts = url.split("/");
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        Subtask subtask = gson.fromJson(body, Subtask.class);
        if (urlParts.length == 2) {
            try {
                manager.add(subtask);
                writeResponse(exchange, "Subtask has been successfully added.", 201);
            } catch (ManagerCheckTimeException e) {
                writeResponse(exchange, "Not Acceptable", 406);
            }
        } else if (urlParts.length == 3) {
            try {
                manager.update(subtask);
                writeResponse(exchange, "Subtask has been successfully updated.", 201);
            } catch (ManagerCheckTimeException e) {
                writeResponse(exchange, "Not Acceptable", 406);
            }
        }
    }

    public void handleDeleteSubtasks(HttpExchange exchange, String url) throws IOException {
        String[] urlParts = url.split("/");
        try {
            if (urlParts.length == 2) {
                manager.removeAllSubtasks();
                writeResponse(exchange, "All subtasks have been removed.", 200);
            } else if (urlParts.length == 3) {
                int subtaskId = Integer.parseInt(urlParts[2]);
                manager.removeSubtaskById(subtaskId);
                writeResponse(exchange, "Subtask with " + subtaskId + " ID has been removed.", 200);
            }
        } catch (IOException e) {
            writeResponse(exchange, "Error", 500);
        }

    }
}
