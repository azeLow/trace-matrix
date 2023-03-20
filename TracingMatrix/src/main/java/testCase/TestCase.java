package testCase;

import storables.StorableObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import template.Templates;

import utils.FieldContentUtils;
import utils.FileUtils;
import utils.LinkResolver;

import java.io.*;
import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;


/**
 * Тест-кейс.
 */
public class TestCase extends StorableObject <TestCase> {
    public static final String LINK_TYPE = "test-case";
    private static final String VERSION_PLACE = "<!--VERSION-->";
    private static final String BODY_ACTION_PLACE = "<!--BODY-ACTION-->";
    private final Set<String> requirementIds;
    private final List<String> preconditions;
    private final List<Step> steps;
    private final String version;

    @Override
    public TestCase getNewObject(JSONObject json) throws RuntimeException {
        return new TestCase(json);
    }

    private final String comment;
    private static final String COMMENT_PLACE = "<!--COMMENT-->";


    @Override
    public String getLinkType() {
        return LINK_TYPE;
    }

    public String getVersion() {
        return this.version;
    }

    public String getComment() {
        return this.comment;
    }

    public List<String> getPreconditions() {
        return this.preconditions;
    }

    public Set<String> getRequirementIds() {
        return this.requirementIds;
    }

    public TestCase(JSONObject test) throws RuntimeException {
        super(test);
        markFieldIfIncomplete(preconditions = createPreconditions((JSONArray) test.get("preconditions")), "preconditions");
        markFieldIfIncomplete(steps= createSteps((JSONArray) test.get("steps")), "steps");
        markFieldIfIncomplete(version = (String) test.get("version"), "version");
        markFieldIfIncomplete(requirementIds = createRequirementIds((JSONArray) test.get("requirementIds")), "requirementIds");
        comment = (String) test.get("comment");
    }

    /**
     * Создание списка с объектами предусловий.
     *
     * @param preconditions массив с предусловиями;
     * @return список с объектами предусловия.
     */
    private List<String> createPreconditions(JSONArray preconditions) {
        List<String> preconditionList = new ArrayList<>();
        if (preconditions != null) {
            int pCnt = 0;
            for (Object object : preconditions) {
                pCnt++;
                try {
                    String precondition = ((String) object);
                    preconditionList.add(precondition);
                } catch (RuntimeException e) {
                    throw new RuntimeException("Ошибка парсинга " + pCnt + "-го элемента списка предусловий.");
                }
            }
        }
        return preconditionList;
    }

    private Set<String> createRequirementIds(JSONArray requirementIds) {
        Set<String> requirementIdsSet = new TreeSet<>();
        if (requirementIds != null) {
            for (Object object : requirementIds) {
                String requirementId = ((String) object);
                requirementIdsSet.add(requirementId);
            }
        }
        return requirementIdsSet;
    }

    /**
     * Создает список с шагами тест-кейса.
     *
     * @param steps массив с шагами тест-кейса
     * @return список с объектами шагов тест-кейса
     */
    private List<Step> createSteps(JSONArray steps) throws RuntimeException {
        List<Step> stepList = new ArrayList<>();
        if (steps != null) {
            int sCnt = 0;
            for (Object object : steps) {
                sCnt++;
                try {
                    Step step = new Step((JSONObject) object);
                    stepList.add(step);
                } catch (RuntimeException e) {
                    throw new RuntimeException("Ошибка парсинга " + sCnt + "-го элемента списка шагов.");
                }
            }

        }
        return stepList;
    }


    /**
     * Создание html-документа из объекта тест-кейс.
     *
     * @return html-документ объекта тест-кейс
     * @throws IOException ошибка ввода-вывода при чтении шаблонов для построения html-документа
     */
    public String getCreateHtmlTable() throws IOException {
        StringBuilder stepsHTML = createStepsHTML();
        String tab = "";
        Templates templates = Templates.get();
        StringBuilder pr = new StringBuilder();
        for (int j = 0; j < preconditions.size(); j++) {
            String precondition = preconditions.get(j);
            pr.append(String.format("<p> &ensp; %s. %s</p>", j + 1, precondition));
        }
        String ln9 = templates.getTemplateContentTestCase();
        tab += ln9.replace(ID_PLACE, FieldContentUtils.format(id))
                .replace(NAME_PLACE, FieldContentUtils.format(name))
                .replace(DESCRIPTION_PLACE, FieldContentUtils.format(description))
                .replace(VERSION_PLACE, FieldContentUtils.format(version))
                .replace(COMMENT_PLACE, comment == null ? "" : comment)
                .replace(PRECONDITIONS_PLACE, FieldContentUtils.format(pr.toString()))
                .replace(BODY_ACTION_PLACE, stepsHTML)
                .replace(STATUS_PLACE, FieldContentUtils.format(status));
        return tab;
    }

    @Override
    public void createHTML(File dir) throws IOException {
        File tc = new File(dir, id + ".html");
        BufferedWriter bw = new BufferedWriter(new FileWriter(tc, UTF_8));
        PrintWriter pw1 = new PrintWriter(bw);
        Templates templates = Templates.get();
        String ln6 = templates.getTemplateTestCase();
        FileUtils.copyFile(new File("src\\main\\resources\\Content\\styleTestCase.css"), new File(dir, "styleTestCase.css"));
        pw1.println(ln6.replace("<!--TABLE-->", getCreateHtmlTable()));
        pw1.close();
    }

    public void resolveLinks(LinkResolver linkResolver) {
        description = linkResolver.resolveLinks(description);
        for (Step st : steps) {
            st.description = linkResolver.resolveLinks(st.description);
            st.expectedResult = linkResolver.resolveLinks(st.expectedResult);
        }
    }

    public StringBuilder createStepsHTML () throws IOException {
        StringBuilder html = new StringBuilder();
        Stack<String> st = new Stack<>();
        for (Step step: steps) {
            html.append(step.getHTML(st, '#'));
        }
        return html;
    }
}
