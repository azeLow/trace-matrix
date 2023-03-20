package requirement;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Сценарий в требовании.
 */
public class Scenario {
    String id;
    String name;
    List<Precondition> preconditions;
    List<Step> steps;

    public List<Precondition> getPreconditions() {
        return this.preconditions;
    }

    public List<Step> getSteps() {
        return this.steps;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    /**
     * Объявление объектов сценария.
     * Проверка на пустоту.
     */
    Scenario(JSONObject scenario) throws RuntimeException {
        if (scenario != null) {
            id = (String) scenario.get("id");
            name = (String) scenario.get("name");
            this.preconditions = createPreconditions((JSONArray) scenario.get("preconditions"));
            this.steps = createSteps((JSONArray) scenario.get("steps"));
        } else {
            throw new RuntimeException("Ошибка парсинга JSON-объекта сценария. Передан null-объект.");
        }
    }

    /**
     * Создание списка с объектами предусловия.
     *
     * @param preconditions массив с предусловиями;
     * @return список с объектами предусловия.
     */
    private List<Precondition> createPreconditions(JSONArray preconditions) {
        List<Precondition> stepList = new ArrayList<>();
        int i = 1;
        for (Object object : preconditions) {
            if (((String) object).charAt(0) != '•') {
                Precondition precondition = new Precondition(String.format("%d. %s", i, object));
                i++;
                stepList.add(precondition);
            }
            else {
                Precondition precondition = new Precondition(String.format("\t%s", object));
                stepList.add(precondition);
            }
        }
        return stepList;
    }

    /**
     * Создание списка из объектов шагов.
     *
     * @param steps массив с шагами;
     * @return список с объектами шагов.
     */
    private List<Step> createSteps(JSONArray steps) {
        List<Step> stepList = new ArrayList<>();
        int i = 1;
        for (Object object : steps) {
            if (((String) object).charAt(0) != '•') {
                Step step = new Step(String.format("%d. %s", i, object));
                i++;
                stepList.add(step);
            }
            else {
                Step step = new Step(String.format("\t%s", object));
                stepList.add(step);
            }
        }
        return stepList;
    }
}
