import html.Matrix;
import html.RequirementCatalog;
import html.TestCaseCatalog;
import requirement.Requirement;
import requirement.RequirementList;
import testCase.TestCase;
import testCase.TestCaseList;
import utils.LinkManager;

import java.io.File;

/**
 * Построение матрицы-трассировки в виде html-документа.
 */
public class Main {
    private static final String DATA_FOLDER = "data";
    private static final String GENERATED_FOLDER = "generated";

    public static void main(String[] args) throws Exception {
        String FILE_PATH_TESTS = DATA_FOLDER + File.separator + "testcases.json";
        String FILE_PATH_REQUIREMENT = DATA_FOLDER + File.separator + "requirements.json";
        TestCaseList testCaseList = new TestCaseList(FILE_PATH_TESTS);
        RequirementList requirementList = new RequirementList(FILE_PATH_REQUIREMENT);
        Matrix matrix = new Matrix(testCaseList, requirementList);

        LinkManager.registerLinkType(TestCase.LINK_TYPE, testCaseList);
        LinkManager.registerLinkType(Requirement.LINK_TYPE, requirementList);
        LinkManager.registerLinkType(Matrix.LINK_TYPE, matrix);
        LinkManager.resolveLinks();

        TestCaseCatalog testCaseCatalog = new TestCaseCatalog(testCaseList,requirementList);
        RequirementCatalog requirementCatalog = new RequirementCatalog(requirementList);
        File generatedRootDir = new File(GENERATED_FOLDER);
        deleteDir(generatedRootDir);
        generatedRootDir.mkdirs();
        testCaseList.createHTML(generatedRootDir);
        requirementList.createHTML(generatedRootDir);
        matrix.createHTML(generatedRootDir);
        testCaseCatalog.createTestListHtml(generatedRootDir);
        requirementCatalog.createRequirementListHtml(generatedRootDir);

        ProcessBuilder p = new ProcessBuilder();
        p.command("explorer.exe", GENERATED_FOLDER + File.separator + "index.html");
        p.start();
    }

    private static void deleteDir(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                deleteDir(f);
            }
        }
        file.delete();
    }
}