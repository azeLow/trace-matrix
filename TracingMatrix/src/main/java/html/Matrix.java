package html;

import storables.StorableList;
import requirement.Requirement;
import requirement.RequirementList;
import template.Templates;
import testCase.TestCase;
import testCase.TestCaseList;
import utils.FileUtils;
import utils.ILink;
import utils.LinkManager;
import utils.LinkResolver;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Matrix implements ILink {
    public static final String CHECK_CHAR = "&#10004;";
    public static final String LINK_TYPE = "root";
    private static final String HEADER_PLACE = "<!--HEADER-->";
    private static final String BODY_PLACE = "<!--BODY-->";

    private final StorableList<TestCase> testList;
    private final StorableList<Requirement> requirementList;

    public Matrix(TestCaseList testList, RequirementList requirementList) throws Exception {
        this.testList = testList;
        this.requirementList = requirementList;
        validate();
    }

    /**
     * Проверка, относится ли тест-кейс к требованию.
     *
     * @return возвращение true, если тест-кейс относится к требованию, иначе - false
     */
    public static boolean isCheck(TestCase testCase, Requirement requirement) {
        return testCase.getRequirementIds().contains(requirement.getId());
    }

    /**
     * Получение html-кода тела матрицы-трассировки.
     *
     * @param linkResolver интерфейс для построения ссылок
     * @return HTML-код тела матрицы-трассировки
     */
    private String getBody(LinkResolver linkResolver) {
        StringBuilder html = new StringBuilder();
        int section = 0;
        for (TestCase test : testList.getList()) {
            section++;
            html.append(getTestRows(linkResolver, test, testList.getMaxLevel(), "" + section));
        }
        return html.toString();
    }

    private String getTestRows(LinkResolver linkResolver, TestCase testCase, int maxLevel, String section) {
        StringBuilder html = new StringBuilder();
        html.append("<tr")
                .append(testCase.isGroup() ? " class = \"inactive_row\">" : ">")
                .append(!testCase.isGroup() ? "<td colspan=" + maxLevel + ">" : "<td>")
                .append(section + ".\t");
        if (!testCase.isGroup())
            html.append(testCase.getIncompleteFields().isEmpty() ? "" : "<em class = \"incomplete\">")
                    .append(linkResolver.generateATag(TestCase.LINK_TYPE, testCase.getId()))
                    .append(testCase.getIncompleteFields().isEmpty() ? "" : "</em>");
        else html.append(linkResolver.generateATag(TestCase.LINK_TYPE, testCase.getId()));
        html.append("</td>");
        if (!testCase.isGroup())
            for (Requirement req : requirementList.getLeafsList()) {
                html.append("<td>");
                if (isCheck(testCase, req)) html.append(CHECK_CHAR);
                html.append("</td>");
            }
        html.append("</tr>");
        if (!testCase.isLeaf()) {
            int subsectionNum = 0;
            for (TestCase childTestCase : testCase.getChildren()) {
                subsectionNum++;
                StringBuilder subsection = new StringBuilder(section + "." + subsectionNum);
                html.append(getTestRows(linkResolver, childTestCase, maxLevel, subsection.toString()));
            }
        }
        return html.toString();
    }

    /**
     * Получение html-кода шапки с требованиями для матрицы-трассировки.
     *
     * @param linkResolver для построения ссылок
     * @return HTML-код шапки матрицы-трассировки
     */
    private String getHead(LinkResolver linkResolver) {
        StringBuilder html = new StringBuilder();

        html.append("<thead>");
        for (int level = 0; level < requirementList.getMaxLevel(); level++) {
            html.append("<tr>");
            if (level == 0)
                html.append("<th")
                        .append(" rowspan=")
                        .append(requirementList.getMaxLevel())
                        .append(" colspan=")
                        .append(testList.getMaxLevel())
                        .append(" class=\"firstbody\">");
            html.append(getRequirementRow(linkResolver, requirementList.getMaxLevel(), level, requirementList.getList()));
            html.append("</tr>");
        }
        html.append("</thead>");
        return html.toString();
    }

    private String getRequirementRow(LinkResolver linkResolver, int maxLevel, int level, List<Requirement> requirements) {
        StringBuilder html = new StringBuilder();
        for (Requirement r : requirements) {
            if (level == 0)
                html.append("<th")
                        .append((r.getBranchLeafsCount() > 1 ? " colspan=" + r.getBranchLeafsCount() : ""))
                        .append((r.isLeaf() && maxLevel > 1 ? " rowspan=" + maxLevel : ""))
                        .append(">")
                        .append(r.isGroup() ? "<p class=\"convert\">" : (r.getIncompleteFields().isEmpty() ? "" : "<p class = \"incomplete\">"))
                        .append(linkResolver.generateATag(Requirement.LINK_TYPE, r.getId()))
                        .append(r.isGroup() ? "</p>" : (r.getIncompleteFields().isEmpty() ? "" : "</p>"))
                        .append("</th>");
            if (!r.isLeaf())
                html.append(getRequirementRow(linkResolver, maxLevel - 1, level - 1, r.getChildren()));
        }
        return html.toString();
    }

    /**
     * Создание html-файла матрицы-трассировки.
     */
    public void createHTML(File dir) throws IOException {
        File matrix = new File(dir, "TracingMatrix.html");
        BufferedWriter bw = new BufferedWriter(new FileWriter(matrix, StandardCharsets.UTF_8));
        PrintWriter pwr = new PrintWriter(bw);
        Templates templates = Templates.get();
        FileUtils.copyFile(new File("src\\main\\resources\\Content\\styleMatrix.css"), new File(dir, "styleMatrix.css"));
        FileUtils.copyFile(new File("src\\main\\resources\\Content\\index.html"), new File(dir, "index.html"));
        FileUtils.copyFile(new File("src\\main\\resources\\Content\\via_image.png"), new File(dir, "via_image.png"));
        String ln = templates.getTemplateMatrix();
        LinkResolver linkResolver = LinkManager.getLinkResolver(LINK_TYPE);
        pwr.println(ln.replace(HEADER_PLACE, getHead(linkResolver)).replace(BODY_PLACE, getBody(linkResolver)));
        pwr.close();
    }

    /**
     * Проверка того, что заданные тест-кейсы могут покрывать только какие-либо из заданных требований
     * и ни одного "постороннего".
     */
    private void validate() throws Exception {
        for (TestCase testCase : testList.getList()) {
            validateSingleTestCase(testCase);
        }
    }

    private Set<String> getRequirementListIds (List<Requirement> requirements)
    {
        Set<String> ids = new TreeSet<>();
        for (Requirement req : requirements)
        {
            ids.add(req.getId());
            if (req.isGroup()) {
                ids.addAll(getRequirementListIds(req.getChildren()));
            }
        }
        return ids;
    }
    private void validateSingleTestCase(TestCase testCase) throws Exception {
        Set<String> allRequirementIds = getRequirementListIds(requirementList.getList());
        for (String reqId : testCase.getRequirementIds()) {
            if (!allRequirementIds.contains(reqId)) {
                throw new Exception("Тест-кейс с id = "
                        + testCase.getId()
                        + " покрывает требование с неопределенным идентификатором "
                        + reqId);
            }
        }
        if (testCase.isGroup()) {
            for (TestCase childTestCase : testCase.getChildren())
                validateSingleTestCase(childTestCase);
        }
    }

    public String getLink(String relativePath, String id) {
        return null;
    }

    public void resolveLinks() {
    }

    public String getPath() {
        return "";
    }
}