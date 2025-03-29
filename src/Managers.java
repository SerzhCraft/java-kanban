public class Managers {
    public static TaskManager<Task> getDefault() {
        return new InMemoryTaskManager<>();
    }
}
