package handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exeptions.ManagerCheckTimeException;
import exeptions.NotFoundException;
import manager.TaskManager;
import task.Epic;
import task.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {
    private TaskManager manager;
    private Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(LocalDateTime.class,
            new LocalDateTimeAdapter()).serializeNulls().excludeFieldsWithoutExposeAnnotation().create();

    public EpicsHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String url = exchange.getRequestURI().getPath();
        switch (method) {
            case "GET":
                handleGetEpics(exchange, url);
                break;
            case "POST":
                handlePostEpics(exchange);
                break;
            case "DELETE":
                handleDeleteEpics(exchange, url);
                break;
            default:
                super.writeResponse(exchange, "Неверный запрос, попробуйте еще раз.", 404);
        }
    }

    public void handleGetEpics(HttpExchange exchange, String url) throws IOException {
        String[] urlParts = url.split("/");
        if (urlParts.length == 2) {
            List<Epic> epics = manager.getAllEpics();
            if (epics.isEmpty()) {
                writeResponse(exchange, "Список эпиков пуст", 200);
            } else {
                sendText(exchange, gson.toJson(epics));
            }
        } else if (urlParts.length == 3) {
            try {
                int epicId = Integer.parseInt(urlParts[2]);
                Epic epic = manager.getEpicById(epicId);
                sendText(exchange, gson.toJson(epic));
            } catch (NotFoundException e) {
                writeResponse(exchange, "Not found.", 404);
            }
        } else if ((urlParts.length == 4) && urlParts[3].equals("subtasks")) {
            try {
                int epicId = Integer.parseInt(urlParts[2]);
                List<Subtask> subtasksOfEpic = manager.getSubtasksOfEpic(epicId);
                sendText(exchange, gson.toJson(subtasksOfEpic));
            } catch (NotFoundException e) {
                writeResponse(exchange, "Not Found", 404);
            }
        }
    }

    public void handlePostEpics(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        Epic epic = gson.fromJson(body, Epic.class);
        try {
            if (epic.getId() == 0) {
                manager.add(epic);
                List<Epic> epics = manager.getAllEpics();
                int requiredId = 0;
                for (Epic epic1 : epics) {
                    if (epic.equals(epic1)) {
                        requiredId = epic1.getId();
                    }
                }
                writeResponse(exchange, "Epic " + requiredId + " has been added.", 201);
            } else {
                manager.update(epic);
                writeResponse(exchange, "Epic " + epic.getId() + " has been updated.", 201);
            }
        } catch (ManagerCheckTimeException e) {
            writeResponse(exchange, "Not Acceptable", 406);
        }
    }

    public void handleDeleteEpics(HttpExchange exchange, String url) throws IOException {
        String[] urlParts = url.split("/");
        try {
            if (urlParts.length == 2) {
                manager.removeAllEpics();
                writeResponse(exchange, "All epics have been removed.", 200);
            } else if (urlParts.length == 3) {
                int epicId = Integer.parseInt(urlParts[2]);
                manager.removeEpicById(epicId);
                writeResponse(exchange, "Epic with " + epicId + " ID has been removed.", 200);
            }
        } catch (IOException e) {
            writeResponse(exchange, "Error", 500);
        }

    }
}
