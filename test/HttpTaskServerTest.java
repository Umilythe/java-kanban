import com.google.gson.*;
import handlers.LocalDateTimeAdapter;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import task.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskServerTest {

    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).serializeNulls().excludeFieldsWithoutExposeAnnotation().create();

    public HttpTaskServerTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        manager.removeAllTasks();
        manager.removeAllSubtasks();
        manager.removeAllEpics();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2",
                Status.NEW, 5, LocalDateTime.now());
        String taskJson = gson.toJson(task);


        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());


        List<Task> tasksFromManager = manager.getAllTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.get(0).getTitle(), "Некорректное имя задачи");
    }

    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic for test", "Epic 1", 0, Status.NEW, 20, LocalDateTime.now());

        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url1 = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url1).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Epic> epicsFromManager = manager.getAllEpics();

        assertNotNull(epicsFromManager, "Эпики не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество эпиков");
        assertEquals("Epic for test", epicsFromManager.get(0).getTitle(), "Некорректное имя эпика");
    }

    @Test
    public void testAddSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic for test", "Epic 1", 1, Status.NEW, 20, LocalDateTime.now());
        manager.add(epic);
        Subtask subtask = new Subtask("Test 3", "Testing subtask 3", 0, Status.NEW, 5, LocalDateTime.now(), 1);

        String subtaskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());


        List<Subtask> subtasksFromManager = manager.getAllSubtasks();

        assertNotNull(subtasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("Test 3", subtasksFromManager.get(0).getTitle(), "Некорректное имя подзадачи");
    }

    @Test
    public void testGetTask() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2", Status.NEW, 5, LocalDateTime.now());
        manager.add(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        String title = jsonObject.get("title").getAsString();

        assertEquals(title, task.getTitle(), "Некорректное имя задачи");
    }

    @Test
    public void testGetSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic for test", "Epic 1", 1, Status.NEW, 20, LocalDateTime.now());
        manager.add(epic);
        Subtask subtask = new Subtask("Test 3", "Testing subtask 3", 2, Status.NEW, 5, LocalDateTime.now(), 1);
        manager.add(subtask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        String title = jsonObject.get("title").getAsString();

        assertEquals(title, subtask.getTitle(), "Некорректное имя подзадачи");
    }

    @Test
    public void testGetEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic for test", "Epic 1", 1, Status.NEW, 20, LocalDateTime.now());
        manager.add(epic);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        String title = jsonObject.get("title").getAsString();

        assertEquals(title, epic.getTitle(), "Некорректное имя подзадачи");
    }

    @Test
    public void testDeleteSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic for test", "Epic 1", 1, Status.NEW, 20, LocalDateTime.now());
        manager.add(epic);
        Subtask subtask = new Subtask("Test 3", "Testing subtask 3", 2, Status.NEW, 5, LocalDateTime.now(), 1);
        manager.add(subtask);
        assertEquals(1, manager.getAllSubtasks().size(), "Неверное количество подзадач в начале");
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        assertEquals(0, manager.getAllSubtasks().size(), "Неверное количество подзадач в конце теста");
    }

    @Test
    public void testDeleteEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic for test", "Epic 1", 1, Status.NEW, 20, LocalDateTime.now());
        manager.add(epic);
        assertEquals(1, manager.getAllEpics().size(), "Неверное количество задач в начале");
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        assertEquals(0, manager.getAllEpics().size(), "Неверное количество задач в конце теста");
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2", Status.NEW, 5, LocalDateTime.now());
        manager.add(task);
        assertEquals(1, manager.getAllTasks().size(), "Неверное количество задач в начале");
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        assertEquals(0, manager.getAllTasks().size(), "Неверное количество задач в конце теста");
    }
}
