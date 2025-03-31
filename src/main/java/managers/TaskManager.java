package main.java.managers;

import main.java.models.Epic;
import main.java.models.Subtask;
import main.java.models.Task;

import java.util.List;

public interface TaskManager {
    // Методы для класса main.java.models.Task
    List<Task> getAllTasks();

    void deleteAllTasks();

    Task getTaskById(int id);

    Task createTask(Task task);

    void updateTask(Task task);

    void deleteTaskById(int id);

    // Методы для класса Epics
    List<Epic> getAllEpics();

    void deleteAllEpics();

    Epic getEpicById(int id);

    Epic createEpic(Epic epic);

    void updateEpic(Epic epic);

    void deleteEpicById(int id);

    // Методы для класса main.java.models.Subtask
    List<Subtask> getAllSubtasks();

    void deleteAllSubtasks();

    Subtask getSubtaskById(int id);

    Subtask createSubtask(Subtask subtask);

    void updateSubtask(Subtask subtask);

    void deleteSubtaskById(int id);

    // Метод получения списка всех подзадач определённого эпика
    List<Subtask> getSubtaskByEpicId(int epicId);

    // Метод для получения истории

    List<Task> getHistory();

}
