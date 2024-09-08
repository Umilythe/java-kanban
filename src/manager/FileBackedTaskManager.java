package manager;

import exeptions.ManagerSaveException;
import task.Epic;
import task.Subtask;
import task.Task;
import task.Type;
import task.Status;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    private void save() {
        try (FileWriter writer = new FileWriter(file.getName())) {
            writer.write("id,type,name,status,description,epic\n");
            ArrayList<Task> allKindsOfTasks = new ArrayList<>();
            ArrayList<Task> allTasks = super.getAllTasks();
            ArrayList<Epic> allEpics = super.getAllEpics();
            ArrayList<Subtask> allSubTasks = super.getAllSubtasks();
            allKindsOfTasks.addAll(allTasks);
            allKindsOfTasks.addAll(allEpics);
            allKindsOfTasks.addAll(allSubTasks);
            for (Task task : allKindsOfTasks) {
                writer.write(task.toStringFromFile());
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Файл для записи не найден.");
        }

    }

    private static Task fromString(String value) {
        String[] characteristicsOfTask = value.split(",", 6);
        int id = Integer.parseInt(characteristicsOfTask[0]);
        Type type = Type.valueOf(characteristicsOfTask[1]);
        String title = characteristicsOfTask[2];
        Status status = Status.valueOf(characteristicsOfTask[3]);
        String description = characteristicsOfTask[4];
        int epicId = 0;
        if (type.equals(Type.SUBTASK)) {
            epicId = Integer.parseInt(characteristicsOfTask[5]);
        }
        switch (type) {
            case TASK:
                return new Task(title, description, id, status);
            case EPIC:
                return new Epic(title, description, id, status);
            case SUBTASK:
                return new Subtask(title, description, id, status, epicId);
            default:
                return null;
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        try {
            String fileContent = new String(Files.readString(Paths.get(file.getName())));
            String[] lines = fileContent.split("\n");
            int max = 0;
            for (String line : lines) {

                if (line.contains("id")) {
                    continue;
                }
                Task task = fromString(line);
                if (task.getId() > max) {
                    max = task.getId();
                }
                switch (task.getType()) {
                    case TASK:
                        fileBackedTaskManager.putTask(task);
                        break;
                    case EPIC:
                        fileBackedTaskManager.putEpic((Epic) task);
                        break;
                    case SUBTASK:
                        fileBackedTaskManager.putSubtask((Subtask) task);
                        break;
                }

            }
            fileBackedTaskManager.setNextId(max + 1);
        } catch (IOException e) {
            throw new ManagerSaveException("Не возможно прочитать файл.");
        }
        return fileBackedTaskManager;
    }

    @Override
    public void add(Task task) {
        super.add(task);
        save();
    }

    @Override
    public void add(Epic epic) {
        super.add(epic);
        save();
    }

    @Override
    public void add(Subtask subtask) {
        super.add(subtask);
        save();
    }


    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }


    @Override
    public void update(Task task) {
        super.update(task);
        save();
    }

    @Override
    public void update(Epic epic) {
        super.update(epic);
        save();
    }

    @Override
    public void update(Subtask subtask) {
        super.update(subtask);
        save();
    }

    @Override
    public void removeTaskById(Integer taskId) {
        super.removeTaskById(taskId);
        save();
    }

    @Override
    public void removeEpicById(Integer epicId) {
        super.removeEpicById(epicId);
        save();
    }

    @Override
    public void removeSubtaskById(Integer subtaskId) {
        super.removeSubtaskById(subtaskId);
        save();
    }
}





