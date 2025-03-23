package server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.NotFoundException;
import exceptions.TaskIntersectionException;
import taskmanagers.TaskManager;
import server.HttpTaskServer;
import tasks.Subtask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;
    private String requestBody;

    public SubtaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.gson = HttpTaskServer.getGson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Endpoint endpoint = Endpoint.endpointFromMethodAndPath(method, path);

        switch (endpoint) {
            case GET_SUBTASKS:
                List<Subtask> subtasks = taskManager.getSubtasksList();
                if (subtasks.isEmpty()) {
                    sendIfEmptyList(exchange);
                    return;
                }
                sendText(exchange, gson.toJson(subtasks), HttpStatusCode.OK);
                break;

            case GET_SUBTASK_BY_ID:
                try {
                    int idForGet = extractIdFromPath(path);
                    Subtask subtask = taskManager.getSubtask(idForGet);
                    if (subtask == null) {
                        sendNotFound(exchange);
                        return;
                    }
                    sendText(exchange, gson.toJson(subtask), HttpStatusCode.OK);

                } catch (NotFoundException exception) {
                    sendText(exchange, exception.getMessage(), HttpStatusCode.NOT_FOUND);
                } catch (IllegalArgumentException exception) {
                    sendText(exchange, "Ошибка: неверный путь или идентификатор", HttpStatusCode.BAD_REQUEST);
                }
                break;

            case CREATE_OR_UPDATE_SUBTASK:
                createOrUpdateSubtask(exchange);
                break;

            case DELETE_SUBTASK:
                try {
                    int idForDelete = extractIdFromPath(path);
                    if (taskManager.getSubtask(idForDelete) == null) {
                        sendNotFound(exchange);
                        return;
                    }
                    taskManager.deleteSubtask(idForDelete);
                    sendText(exchange, "Подзадача с Id: " + idForDelete + " успешно удалена", HttpStatusCode.NO_CONTENT);

                } catch (NotFoundException exception) {
                    sendText(exchange, exception.getMessage(), HttpStatusCode.NOT_FOUND);
                }
                break;

            default:
                new HttpTaskServer.UnknownPathHandler().handle(exchange);
        }
    }

    private void createOrUpdateSubtask(HttpExchange exchange) throws IOException {
        Subtask newSubtask;
        try {
            newSubtask = gson.fromJson(requestBody, Subtask.class);
            if (newSubtask.getID() == 0) {
                newSubtask = new Subtask(newSubtask.getEpicID(), newSubtask.getName(), newSubtask.getDescription(),
                        newSubtask.getStatus(), newSubtask.getStartTime(), newSubtask.getDuration());
            } else {
                newSubtask = new Subtask(newSubtask.getID(), newSubtask.getEpicID(), newSubtask.getName(),
                        newSubtask.getDescription(), newSubtask.getStatus(),
                        newSubtask.getStartTime(), newSubtask.getDuration());
            }

        } catch (Exception exception) {
            sendText(exchange, "Ошибка: некорректный формат подзадачи в теле запроса", HttpStatusCode.BAD_REQUEST);
            return;
        }

        if (newSubtask.getID() < -1) {
            sendText(exchange, "Ошибка: неверный Id подзадачи", HttpStatusCode.BAD_REQUEST);
            return;
        }

        if (taskManager.getEpicsList().isEmpty()) {
            sendText(exchange, "Ошибка: Список эпиков пуст", HttpStatusCode.BAD_REQUEST);
            return;
        }

        Subtask finalNewSubtask = newSubtask;
        if (newSubtask.getEpicID() <= 0 || taskManager.getEpicsList().stream()
                .filter(epic -> epic.getID() == finalNewSubtask.getEpicID()).findFirst().isEmpty()) {
            sendText(exchange, "Ошибка: неверный Id эпика", HttpStatusCode.BAD_REQUEST);
            return;
        }

        if (newSubtask.getID() > 0 && taskManager.getSubtasksList().isEmpty()) {
            sendText(exchange, "Ошибка: Список подзадач пуст", HttpStatusCode.BAD_REQUEST);
            return;
        }

        try {
            if (newSubtask.getID() == -1) {
                taskManager.addSubtask(newSubtask);
                int newSubtaskId = taskManager.getSubtasksList().getLast().getID();
                sendText(exchange, "Подзадача с Id: " + newSubtaskId + " успешно создана", HttpStatusCode.CREATED);
                return;
            }

            List<Subtask> list = taskManager.getSubtasksList().stream()
                    .filter(subtask -> subtask.getID() == finalNewSubtask.getID())
                    .toList();

            if (!list.isEmpty()) {
                taskManager.updateSubtask(newSubtask);
                sendText(exchange, "Подзадача с Id: " + newSubtask.getID() + " успешно обновлена", HttpStatusCode.CREATED);
            } else {
                sendNotFound(exchange);
            }

        } catch (TaskIntersectionException exception) {
            sendHasInteractions(exchange);
        } catch (Exception exception) {
            sendText(exchange, "Ошибка: неизвестная ошибка при добавлении подзадачи", HttpStatusCode.INTERNAL_SERVER_ERROR);
        }
    }

    private int extractIdFromPath(String path) {
        String[] pathParts = path.split("/");

        if (pathParts.length >= 3 && "subtasks".equals(pathParts[1])) {
            return Integer.parseInt(pathParts[2]);
        }

        throw new IllegalArgumentException("Неверный путь, идентификатор не найден");
    }
}