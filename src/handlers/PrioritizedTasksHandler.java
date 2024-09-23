package handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;

import java.io.IOException;
import java.time.LocalDateTime;

public class PrioritizedTasksHandler extends BaseHttpHandler implements HttpHandler {
    private TaskManager manager;
    private Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).serializeNulls().excludeFieldsWithoutExposeAnnotation().create();

    public PrioritizedTasksHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        switch (method) {
            case "GET":
                writeResponse(exchange, gson.toJson(manager.getPrioritizedTasks()), 200);
                break;
            default:
                super.writeResponse(exchange, "Неверный запрос, попробуйте еще раз.", 404);
        }
    }

}
