package requirement;

/**
 * Предусловие в сценарии.
 */
public class Precondition {
    String description;

    /**
     * Получение описания.
     *
     * @return описание
     */

    public String getDescription() {
        return this.description;
    }

    /**
     * Создание предусловия.
     *
     * @param description описание предусловия
     */
    public Precondition(String description) {
        this.description = description;
    }

}



