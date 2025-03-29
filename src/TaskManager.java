import java.util.ArrayList;
import java.util.List;

public interface TaskManager <T extends Task>{
    // Методы для класса Task
    ArrayList<T> getAllTasks();

    void deleteAllTasks();

    T getTaskById(int id);

    T createTask(T task);

    void updateTask(T task);

    void deleteTaskById(int id);

    // Методы для класса Epics
    ArrayList<Epic> getAllEpics();

    void deleteAllEpics();

    Epic getEpicById(int id);

    Epic createEpic(Epic epic);

    void updateEpic(Epic epic);

    void deleteEpicById(int id);

    // Методы для класса Subtask
    ArrayList<Subtask> getAllSubtasks();

    void deleteAllSubtasks();

    Subtask getSubtaskById(int id);

    Subtask createSubtask(Subtask subtask);

    void updateSubtask(Subtask subtask);

    void deleteSubtaskById(int id);

    // Метод получения списка всех подзадач определённого эпика
    ArrayList<Subtask> getSubtaskByEpicId(int epicId);

    //Метод для получения истории просмотров
    List<T> getHistory();
}
