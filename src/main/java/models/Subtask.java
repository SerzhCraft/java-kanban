package main.java.models;

public class Subtask extends Task {
    private Epic epic;

    public Subtask(String name, String description, Epic epic) {
        super(name, description);
        setEpic(epic);
    }

    public Epic getEpic() {
        return epic;
    }

    public void setEpic(Epic epic) {
        if (epic == null) {
            return;
        }
        if (epic.equals(this)) {
            return;
        }
        this.epic = epic;
    }

    @Override
    public Subtask copy() {
        Subtask copy = new Subtask(this.getName(), this.getDescription(), this.epic);
        copy.setId(this.getId());
        copy.setTaskStatus(this.getTaskStatus());
        return copy;
    }

    @Override
    public String toString() {
        return "Subtask{" + super.toString() +
                "epic=" + epic.getName() +
                '}';
    }
}
