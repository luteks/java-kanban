package server;

import com.google.gson.Gson;
import exceptions.NotFoundException;
import taskmanagers.InMemoryTaskManager;
import taskmanagers.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SubtaskHandlerTest {

    private TaskManager taskManager;
    private HttpTaskServer taskServer;
    private Gson gson;
    private HttpClient client;

    @BeforeEach
    public void setUp() throws IOException {
        taskManager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(taskManager);
        gson = HttpTaskServer.getGson();
        client = HttpClient.newHttpClient();
        taskServer.start();
    }

    @AfterEach
    public void tearDown() {
        taskServer.stop();
    }

    private HttpResponse<String> sendPostRequest(String url, String jsonBody) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> sendGetRequest(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> sendDeleteRequest(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).DELETE().build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Test
    public void testAddSubtask() throws IOException, InterruptedException {
        taskManager.addEpic(new Epic("Тестовый эпик", "Описание эпика"));
        int epicId = taskManager.getEpicsList().getLast().getID();
        Subtask subtask = new Subtask(epicId, "Тестовая подзадача", "Описание подзадачи", TaskStatus.NEW,
                LocalDateTime.now(), Duration.ofMinutes(30));
        String subtaskJson = gson.toJson(subtask);

        HttpResponse<String> response = sendPostRequest("http://localhost:8080/subtasks", subtaskJson);
        assertEquals(201, response.statusCode(), "Неверный код ответа при добавлении подзадачи.");

        List<Subtask> subtasks = taskManager.getSubtasksList();
        assertNotNull(subtasks, "Список подзадач не должен быть пуст.");
        assertEquals(1, subtasks.size(), "Количество подзадач должно быть равно 1.");
        assertEquals("Тестовая подзадача", subtasks.getFirst().getName(), "Имя подзадачи не совпадает.");
    }

    @Test
    public void testGetAllSubtasks() throws IOException, InterruptedException {
        taskManager.addEpic(new Epic("Эпик 1", "Описание 1"));
        int epicId = taskManager.getEpicsList().getLast().getID();
        taskManager.addSubtask(new Subtask(epicId, "Подзадача 1", "Описание 1", TaskStatus.NEW,
                LocalDateTime.now(), Duration.ofMinutes(30)));

        HttpResponse<String> response = sendGetRequest("http://localhost:8080/subtasks");
        assertEquals(200, response.statusCode(), "Неверный код ответа при запросе всех подзадач.");

        List<Subtask> subtasks = gson.fromJson(response.body(), List.class);
        assertNotNull(subtasks, "Список подзадач не должен быть пуст.");
        assertEquals(1, subtasks.size(), "Количество подзадач должно быть равно 1.");
    }

    @Test
    public void testGetSubtaskById() throws IOException, InterruptedException {
        taskManager.addEpic(new Epic("Эпик 1", "Описание 1"));
        int epicId = taskManager.getEpicsList().getLast().getID();
        taskManager.addSubtask(new Subtask(epicId, "Подзадача 1", "Описание 1",
                TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(30)));
        int subtaskId = taskManager.getSubtasksList().getLast().getID();

        HttpResponse<String> response = sendGetRequest("http://localhost:8080/subtasks/" + subtaskId);
        assertEquals(200, response.statusCode(), "Неверный код ответа при запросе подзадачи по ID.");

        Subtask subtask = gson.fromJson(response.body(), Subtask.class);
        assertNotNull(subtask, "Подзадача не должна быть null.");
        assertEquals("Подзадача 1", subtask.getName(), "Имя подзадачи не совпадает.");
    }

    @Test
    public void testGetSubtaskByIdNotFound() throws IOException, InterruptedException {
        HttpResponse<String> response = sendGetRequest("http://localhost:8080/subtasks/999");
        assertEquals(404, response.statusCode(), "Ожидается код 404 для несуществующей подзадачи.");
    }

    @Test
    public void testUpdateSubtask() throws IOException, InterruptedException {
        taskManager.addEpic(new Epic("Эпик 1", "Описание 1"));
        int epicId = taskManager.getEpicsList().getLast().getID();
        taskManager.addSubtask(new Subtask(epicId, "Подзадача 1", "Описание 1",
                TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(30)));
        int subtaskId = taskManager.getSubtasksList().getLast().getID();
        Subtask updatedSubtask = new Subtask(subtaskId, epicId, "Обновленная подзадача",
                "Обновленное описание", TaskStatus.DONE, LocalDateTime.now(), Duration.ofMinutes(45));
        String subtaskJson = gson.toJson(updatedSubtask);

        HttpResponse<String> response = sendPostRequest("http://localhost:8080/subtasks", subtaskJson);
        assertEquals(201, response.statusCode(), "Неверный код ответа при обновлении подзадачи.");

        Subtask subtask = taskManager.getSubtask(subtaskId);
        assertNotNull(subtask, "Подзадача не должна быть null.");
        assertEquals("Обновленная подзадача", subtask.getName(), "Имя подзадачи не совпадает после обновления.");
    }

    @Test
    public void testDeleteSubtaskById() throws IOException, InterruptedException {
        taskManager.addEpic(new Epic("Эпик 1", "Описание 1"));
        int epicId = taskManager.getEpicsList().getLast().getID();
        taskManager.addSubtask(new Subtask(epicId, "Подзадача 1", "Описание 1",
                TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(30)));
        int subtaskId = taskManager.getSubtasksList().getLast().getID();

        HttpResponse<String> response = sendDeleteRequest("http://localhost:8080/subtasks/" + subtaskId);
        assertEquals(204, response.statusCode(), "Неверный код ответа при удалении подзадачи.");

        assertThrows(NotFoundException.class, () -> taskManager.getSubtask(subtaskId),
                "Должно выбрасываться исключение NotFoundException после удаления подзадачи.");
    }

    @Test
    public void testDeleteSubtaskByIdNotFound() throws IOException, InterruptedException {
        HttpResponse<String> response = sendDeleteRequest("http://localhost:8080/subtasks/999");
        assertEquals(404, response.statusCode(), "Ожидается код 404 для удаления несуществующей подзадачи.");
    }
}
