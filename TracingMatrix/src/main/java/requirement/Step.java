package requirement;

/**
 * Шаг в сценарии.
 */
public class Step {
    String description;

    public String getDescription() {
        return this.description;
    }

    Step(String description) {
        this.description = description;
    }
}
