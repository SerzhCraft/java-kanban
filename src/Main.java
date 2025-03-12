import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        // Проверка на создание 2-х задач
        Task task1 = new Task("Task 1", "Task 1's description");
        Task task2 = new Task("Task 2", "Task 2's description");
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        // Проверка на создание эпика с 2-я задачами
        Epic epic1 = new Epic("Epic 1", "Epic 1's description");
        taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask("Subtask 1.1", "Subtask 1.1 's description", epic1);
        Subtask subtask2 = new Subtask("Subtask 1.2", "Subtask 1.2 's description", epic1);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        // Проверка на создание эпика с 1-й задачей
        Epic epic2 = new Epic("Epic 2", "Epic 2's description");
        taskManager.createEpic(epic2);
        Subtask subtask3 = new Subtask("Subtask 2.1", "Subtask 2.1's description", epic2);
        taskManager.createSubtask(subtask3);

        // Проверка на печать списка задач, эпиков и подзадач
        printAllTasks(taskManager.getAllTasks(), "\nTasks:");
        printAllTasks(taskManager.getAllEpics(), "\nEpics:");
        printAllTasks(taskManager.getAllSubtasks(), "\nSubtasks");

        // Проверка изменения статусов
        task1.setTaskStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(task1);

        subtask1.setTaskStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask1);

        subtask3.setTaskStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(subtask3);

        System.out.println("\nAfter update status:");
        printAllTasks(taskManager.getAllTasks(), "\nUpdated Tasks:");
        printAllTasks(taskManager.getAllEpics(), "\nUpdated Epics:");
        printAllTasks(taskManager.getAllSubtasks(), "\nUpdated Subtasks");

        // Проверка на удаление 1-й задачи и 1-го эпика
        taskManager.deleteTaskById(task1.getId());
        taskManager.deleteEpicById(epic1.getId());

        System.out.println("\nAfter delete Task 1 and Epic 1:");
        printAllTasks(taskManager.getAllTasks(), "\nUpdated Tasks:");
        printAllTasks(taskManager.getAllEpics(), "\nUpdated Epics:");
        printAllTasks(taskManager.getAllSubtasks(), "\nUpdated Subtasks");

    }

    static void printAllTasks(ArrayList<? extends Task> allTasks, String title) {
        System.out.println(title);
        for (Task task : allTasks) {
            System.out.println(task);
        }
    }

}
