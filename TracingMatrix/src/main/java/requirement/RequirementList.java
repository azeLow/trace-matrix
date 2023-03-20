package requirement;

import org.json.simple.JSONObject;
import storables.StorableList;

import java.io.IOException;


/**
 * Список требований.
 */
public class RequirementList extends StorableList<Requirement> {
    private static final String FOLDER = "requirements";

    /**
     * Чтение списка требований из json-файла.
     *
     * @param fileName имя json-файла с требованиями
     */
    public RequirementList(String fileName) throws IOException {
        super(fileName);
    }

    @Override
    protected Requirement getNewObject(JSONObject json) throws RuntimeException{
        return new Requirement(json);
    }

    @Override
    public String getFolder() {
        return FOLDER;
    }
}
