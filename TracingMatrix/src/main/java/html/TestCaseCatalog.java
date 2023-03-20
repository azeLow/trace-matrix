package html;

import requirement.Requirement;
import requirement.RequirementList;
import storables.StorableList;
import storables.StorableObject;
import template.Templates;
import testCase.TestCase;
import testCase.TestCaseList;
import utils.LinkManager;
import utils.LinkResolver;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class TestCaseCatalog<T extends StorableObject> {
    public static final String LINK_TYPE = "root";
    private static final String ID_PLACE = "<!--ID-->";
    private static final String DESCRIPTION_PLACE = "<!--DESCRIPTION-->";
    private static final String TEST_CASE_LIST_PLACE = "<!--TEST-CASE-LIST-->";
    private static final String TEST_CASES_PLACE = "<!--TEST-CASES-->";
    private static final String ID_TARGET_PLACE = "<!--ID2-->";
    private static final String TARGET_CSS_PLACE = "<!--TARGET-->";
    public static final String NUMBER_PLACE = "<!--NUMBER-->";
    public static final String CLASS_OPEN_PLACE = "<!--CLASS_OPEN-->";
    public static final String REQUIREMENT_ID_PLACE = "<!--IDR-->";

    private final StorableList<TestCase> testList;
    private final StorableList<Requirement> requirementList;

    public TestCaseCatalog(TestCaseList testList, RequirementList requirementList) {
        this.testList = testList;
        this.requirementList = requirementList;
    }

    private StringBuilder getCheckRequirementIds(LinkResolver linkResolver, TestCase testCase) {
        StringBuilder id = new StringBuilder();
        for (Requirement req : requirementList.getLeafsList()) {
            if (html.Matrix.isCheck(testCase, req)) {
                id.append((linkResolver.generateATag(req.LINK_TYPE, req.getId())) + "<br>");
            }

        }
        return id;
    }

    private StringBuilder getTestRowsList(LinkResolver linkResolver, TestCase testCase, String section) throws IOException {
        StringBuilder html = new StringBuilder();
        Templates templates = Templates.get();
        String ln = templates.getTemplateContentTestCaseList();
        if (!testCase.isGroup()) {
            html.append(ln.replace(ID_PLACE, testCase.getId()).replace(NUMBER_PLACE, section + ".\t").replace(DESCRIPTION_PLACE, testCase.getName()).replace(ID_PLACE, testCase.getId()).replace(REQUIREMENT_ID_PLACE, getCheckRequirementIds(linkResolver, testCase)));

        } else {
            html.append(ln.replace(CLASS_OPEN_PLACE, "class=\"inc\" >").replace(ID_PLACE, testCase.getId()).replace(NUMBER_PLACE, section + ".\t").replace(DESCRIPTION_PLACE, testCase.getName()).replace(ID_PLACE, testCase.getId()));

        }
        if (!testCase.isLeaf()) {
            int subsectionNum = 0;
            for (TestCase t : testCase.getChildren()) {
                subsectionNum++;
                StringBuilder subsection = new StringBuilder(section + "." + subsectionNum);
                html.append(getTestRowsList(linkResolver, t, subsection.toString()));

            }
        }
        return html;
    }

    private StringBuilder getTestList(LinkResolver linkResolver) throws IOException {
        StringBuilder html = new StringBuilder();
        int section = 0;
        for (TestCase test : testList.getList()) {
            section++;
            html.append(getTestRowsList(linkResolver, test, "" + section));
        }
        return html;
    }

    public void createTestListHtml(File dir) throws IOException {
        File testList = new File(dir, "testList.html");
        BufferedWriter bw = new BufferedWriter(new FileWriter(testList, StandardCharsets.UTF_8));
        PrintWriter pwr = new PrintWriter(bw);
        Templates templates = Templates.get();
        createTestListCSS(dir);
        LinkResolver linkResolver = LinkManager.getLinkResolver(LINK_TYPE);
        String ln = templates.getTemplateTestCaseList();
        pwr.println(ln.replace(TEST_CASE_LIST_PLACE, getTestList(linkResolver)).replace(TEST_CASES_PLACE, getTestCaseRows()));
        pwr.close();
    }

    private StringBuilder getDivTestCaseRows(TestCase testCase) throws IOException {
        StringBuilder all = new StringBuilder();
        Templates templates = Templates.get();
        String ln = templates.getTemplateDivTestCaseList();

        if (!testCase.isGroup()) {
            all.append(ln.replace(ID_PLACE, testCase.getId()).replace(TEST_CASES_PLACE, testCase.getCreateHtmlTable()));

        } else {
            all.append(ln.replace(ID_PLACE, testCase.getId()).replace(TEST_CASES_PLACE, getParentTest(testCase)));
        }
        if (!testCase.isLeaf())
            for (TestCase t : testCase.getChildren()) {
                all.append(getDivTestCaseRows(t));

            }
        return all;
    }

    private StringBuilder getParentTest(TestCase test) throws IOException {
        StringBuilder all = new StringBuilder();
        Templates templates = Templates.get();
        String ln1 = templates.getTemplateParentTestCase();

        all.append(ln1
                .replace("<!--NAME-->", test.getName()).replace("<!--ID-->", test.getId())
                .replace("<!--STATUS-->", test.getStatus() == null ? "" : test.getStatus())
                .replace("<!--DESCRIPTION-->", test.getDescription())
                .replace("<!--COMMENT-->", test.getComment() == null ? "" : test.getComment()));
        return all;
    }

    private StringBuilder getTestCaseRows() throws IOException {
        StringBuilder all = new StringBuilder();
        for (TestCase test : testList.getList()) {
            all.append(getDivTestCaseRows(test));
        }
        return all;
    }

    private StringBuilder getTestListRowsCss(TestCase testCase) throws IOException {
        StringBuilder css = new StringBuilder();
        Templates templates = Templates.get();
        String ln = templates.getTemplateIdStyle();
        if (!testCase.isGroup()) {
            css.append(ln.replace(ID_PLACE, testCase.getId()).replace(ID_TARGET_PLACE, testCase.getId()));
        } else {
            css.append(ln.replace(ID_PLACE, testCase.getId()).replace(ID_TARGET_PLACE, testCase.getId()));
        }
        if (!testCase.isLeaf()) {
            for (TestCase t : testCase.getChildren()) {
                css.append(getTestListRowsCss(t));
            }
        }

        return css;
    }

    private String getListCss() throws IOException {
        StringBuilder css = new StringBuilder();
        for (TestCase test : testList.getList()) {
            css.append(getTestListRowsCss(test));
        }
        return css.toString();
    }

    public void createTestListCSS(File dir) throws IOException {
        File testCss = new File(dir, "styleTestCaseCatalog.css");
        BufferedWriter bw = new BufferedWriter(new FileWriter(testCss, StandardCharsets.UTF_8));
        PrintWriter pwr = new PrintWriter(bw);
        Templates templates = Templates.get();
        String ln = templates.getTemplateStyleTestCaseList();
        pwr.println(ln.replace(TARGET_CSS_PLACE, getListCss()));
        pwr.close();
    }

}
