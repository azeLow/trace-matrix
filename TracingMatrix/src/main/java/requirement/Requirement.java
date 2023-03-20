package requirement;

import storables.StorableObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import template.Templates;
import utils.FieldContentUtils;
import utils.FileUtils;
import utils.LinkResolver;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;


/**
 * Требование.
 */
public class Requirement extends StorableObject<Requirement> {
    public static final String LINK_TYPE = "requirement";
    private static final String EXPECTED_RESULT_PLACE = "<!--EXPECTED-RESULT-->";
    private static final String SCENARIO_ID_PLACE = "<!--SCENARIO-ID-->";
    private static final String SCENARIO_NAME_PLACE = "<!--SCENARIO-NAME-->";
    private static final String SCENARIO_STEP_PLACE = "<!--SCENARIO-STEP-->";
    private static final String VERSION_PLACE = "<!--VERSION-->";
    private static final String COMMENT_PLACE = "<!--COMMENT-->";
    private static final String SCENARIO_PRECONDITIONS_PLACE = "<!--SCENARIO-PRECONDITIONS-->";
    private static final String SCENARIOS_PLACE = "<!--SCENARIOS-->";
    private String expectedResult;
    private List<Scenario> scenarios;
    private final String version;
    private final String comment;
    public String getVersion() { return  this.version; }
    public String getComment() { return this.comment; }

    @Override
    public Requirement getNewObject(JSONObject json) throws RuntimeException {
        return new Requirement(json);
    };

    public Requirement(JSONObject requirement) throws RuntimeException {
        super(requirement);
        expectedResult = (String) requirement.get("expected-result");
        scenarios = createScenarios((JSONArray) requirement.get("scenarios"));
        markFieldIfIncomplete(version = (String) requirement.get("version"), "version");
        comment = (String) requirement.get("comment");
    }

    /**
     * Создание списка из объектов сценария.
     *
     * @param scenarios массив со сценариями
     * @return список из объектов сценария
     */
    private List<Scenario> createScenarios(JSONArray scenarios) throws RuntimeException {
        List<Scenario> scenarioList = new ArrayList<>();
        if (scenarios != null)
            for (Object object : scenarios) {
                try {
                    Scenario scenario = new Scenario((JSONObject) object);
                    scenarioList.add(scenario);
                } catch (RuntimeException e) {
                    throw new RuntimeException("Ошибка парсинга JSON-объекта scenario. Передан null-объект.");
                }

            }
        return scenarioList;
    }

    @Override
    public String getLinkType() {
        return LINK_TYPE;
    }

    /**
     * Создание HTML документа из объектов требований.
     *
     * @throws IOException ошибка ввода-вывода HTML кода
     */
    public String getRequirementBody() throws IOException {
     String html="";
     String scenariosHTML = "";
     Templates templates = Templates.get();
     String ln = templates.getTemplateContentRequirement();
     List<Scenario> scenarios = this.scenarios;
     int i = 1;
     for (Scenario scenario : scenarios) {
         String scenarioTemplate = templates.getTemplateScenario();
         List<Precondition> preconditions = scenario.getPreconditions();
         List<Step> steps = scenario.getSteps();
         StringBuilder preconditionsDescription = new StringBuilder();
         for (Precondition precondition : preconditions) {
             preconditionsDescription.append(precondition.getDescription()).append("<br>");
         }
         StringBuilder stepsDescription = new StringBuilder();
         for (Step step : steps) {
             stepsDescription.append(step.getDescription()).append("<br>");
         }
         scenarioTemplate = scenarioTemplate.replace(SCENARIO_ID_PLACE, FieldContentUtils.format((scenario.getId())))
                 .replace(SCENARIO_NAME_PLACE, FieldContentUtils.format(scenario.getName()))
                 .replace(SCENARIO_PRECONDITIONS_PLACE, FieldContentUtils.format(preconditionsDescription.toString()))
                 .replace(SCENARIO_STEP_PLACE, FieldContentUtils.format(stepsDescription.toString()));
         scenariosHTML += scenarioTemplate;
     }
     html+=ln.replace(ID_PLACE, getId())
                .replace(NAME_PLACE, FieldContentUtils.format(name))
                .replace(STATUS_PLACE, FieldContentUtils.format(status))
                .replace(VERSION_PLACE, FieldContentUtils.format(version))
                .replace(DESCRIPTION_PLACE, FieldContentUtils.format(description))
                .replace(EXPECTED_RESULT_PLACE, FieldContentUtils.format (expectedResult, "-"))
                .replace(SCENARIOS_PLACE, scenariosHTML)
                .replace(COMMENT_PLACE, FieldContentUtils.format(getComment(), ""));
     return html;
 }
    @Override
    public void createHTML(File dir) throws IOException {
        Templates templates = Templates.get();
        FileUtils.copyFile(new File("src\\main\\resources\\Content\\styleRequirement.css"), new File(dir, "styleRequirement.css"));
        String ln = templates.getTemplateRequirement();
        File req = new File(dir, id + ".html");
        BufferedWriter bw = new BufferedWriter(new FileWriter(req,UTF_8));
        PrintWriter pw = new PrintWriter(bw);
        pw.println(ln.replace("<!--BODY-->",getRequirementBody()));
        pw.close();

    }

    @Override
    public void resolveLinks(LinkResolver linkResolver) {
        description = linkResolver.resolveLinks(description);
        expectedResult = linkResolver.resolveLinks(expectedResult);
        for (Scenario s: scenarios) {
            for (Precondition p: s.preconditions) {
                p.description = linkResolver.resolveLinks(p.description);
            }
            for (Step st: s.steps) {
                st.description = linkResolver.resolveLinks(st.description);
            }
        }
    }
}
