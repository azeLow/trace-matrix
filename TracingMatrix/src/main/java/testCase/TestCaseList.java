package testCase;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import storables.StorableList;

import java.io.IOException;

/**
 * Список тест-кейсов. Объект хранит список тест-кейсов и предоставляет методы для получения глубины
 * тест-кейсов и терминальных тест-кейсов.
 */
public class TestCaseList extends StorableList<TestCase> {
    private static final String FOLDER = "tests";

    /**
     * Чтение списка тест-кейсов из json-файла.
     *
     * @param fileName имя json-файла с тест-кейсами
     */
    public TestCaseList(String fileName) throws IOException {
        super(fileName);
    }

    @Override
    protected TestCase getNewObject(JSONObject json) throws ParseException, IOException {
        return new TestCase(json);
    }

    @Override
    public String getFolder() {
        return FOLDER;
    }
}
