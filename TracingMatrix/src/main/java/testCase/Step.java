package testCase;

import org.json.simple.JSONObject;
import template.Templates;
import utils.FieldContentUtils;

import java.io.IOException;
import java.util.Stack;

/**
 * шаг в тест-кейсе.
 */
public class Step {
    String description;
    String expectedResult;
    public static final String NUMBER_PLACE = "<!--NUMBER-->";
    private static final String STEP_DESCRIPTION_PLACE = "<!--STEP-DESCRIPTION-->";
    private static final String STEP_RESULT_PLACE = "<!--STEP-RESULT-->";

    /**
     * Объявление объектов сценария.
     * Проверка на пустоту значений ключей шагов.
     */
    public Step(JSONObject step) throws RuntimeException {
        if (step != null) {
            description = (String) step.get("description");
            expectedResult = (String) step.get("expected-result");
        } else {
            throw new RuntimeException("Ошибка парсинга JSON-объекта. Передан null-объект.");
        }
    }

    public String getDescription() {
        return this.description;
    }

    public String getExpectedResult() {
        return this.expectedResult;
    }

    /**
     * Получение html-формата для объекта "шгг тест-кейса".
     *
     * @param numStack стек, содержащийся в себе номера все значимых (для определения номера следующего шага в иерархии)
     *                 ранее присвоенных номеров шагов - требуется для поддержания связности нумерации;
     *                 при необходимости ведения новой нумераци требуется передавать пустой стек
     * @param hierarchyMarker символ, определяющий уровень данного шага в общей иерархии шагов;
     *                        уровень шага в иерархии определяется количеством таких символов, последоватльно расположенных
     *                        друг за другом в начале описания (description) шага
     * @return html-представление шага со следкющим присвоенным номером
     * @throws IOException
     */
    public String getHTML (Stack<String> numStack, char hierarchyMarker ) throws IOException {
        String stepHTML = Templates.get().getTemplateSteps();
        int num = 0;
        while (hierarchyMarker == description.charAt(num)) {
            num++;
        }
        String predecessor = getPredecessor(numStack, num);
        String stepNum;
        if (predecessor.chars().filter(ch->ch=='.').count() < num)
            stepNum = predecessor + ".1.";
        else {
            int predLastPointInd = predecessor.lastIndexOf(".");
            String predToLastPoint = predecessor.substring(predLastPointInd + 1);
            int subStep = (predToLastPoint == "") ? 1 : Integer.valueOf(predToLastPoint) + 1;
            stepNum = predecessor.substring(0, predLastPointInd + 1)
                    + subStep + ".";
        }
        numStack.push(stepNum);
        return stepHTML
                .replace(NUMBER_PLACE, stepNum)
                .replace(STEP_DESCRIPTION_PLACE, FieldContentUtils.format(description.substring(num)))
                .replace(STEP_RESULT_PLACE, FieldContentUtils.format(expectedResult, "-"));
    }

    private String getPredecessor(Stack<String> numbers, int maxDepth) {
        if (maxDepth < 0)
            return "";
        while (!numbers.isEmpty() && numbers.peek().chars().filter(ch->ch == '.').count() - 1 > maxDepth) {
            numbers.pop();
        }
        if (numbers.isEmpty())
            return "";
        else {
            String predecessor = numbers.peek();
            return predecessor.substring(0, predecessor.length() - 1);
        }
    }
}

