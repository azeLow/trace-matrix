package template;

import utils.FileUtils;


import java.io.*;

public class Templates {
    private final String templateSteps;
    private final String templateTestCase;
    private final String templateMatrix;
    private final String templateTestCaseList;
    private final String templateContentTestCaseList;
    private final String templateContentTestCase;
    private final String templateDivTestCaseList;
    private final String templateStyleTestCaseList;
    private final String templateIdStyle;
    private final String templateRequirement;
    private static Templates instance;
    private final String templateContentRequirement;
    private final String templateRequirementList;
    private final String templateContentRequirementList;
    private String templateScenario;
    private String templateStyleRequirementList;
    private String templateRequirementIdStyle;
    private String templateParentTestCase;


    public static Templates get() throws IOException {
        if (instance == null) {
            instance = new Templates();
        }
        return instance;
    }

    public Templates() throws IOException {
        this.templateTestCase = getFileContent("testcase.template.html");
        this.templateParentTestCase = getFileContent("parent.testcase.template.html");
        this.templateSteps = getFileContent("steps.testcase.template.html");
        this.templateDivTestCaseList = getFileContent("div.content.testcaseList.template.html");
        this.templateStyleTestCaseList = getFileContent("style.testcaseList.template.css");
        this.templateStyleRequirementList = getFileContent("style.requirement.List.template.css");
        this.templateContentRequirement = getFileContent("content.requirement.template.html");
        this.templateRequirement = getFileContent("requirement.template.html");
        this.templateIdStyle = getFileContent("id.style.testcaseList.template.css");
        this.templateRequirementIdStyle = getFileContent("id.style.requirement.List.template.css");
        this.templateTestCaseList = getFileContent("testcaseList.template.html");
        this.templateContentTestCaseList = getFileContent("content.testcaseList.template.html");
        this.templateContentTestCase = getFileContent("content.testcase.template.html");
        this.templateMatrix = getFileContent("tracing-matrix.template.html");
        this.templateScenario = getFileContent("scenario.template.html");
        this.templateRequirementList=getFileContent("requirementList.html");
        this.templateContentRequirementList=getFileContent("content.requirementList.html");
    }

    private static String getFileContent(String fileName) throws IOException {
        return FileUtils.getFileContent("src\\main\\resources\\Content\\" + fileName);
    }

    public String getTemplateTestCase() {
        return templateTestCase;
    }

    public String getTemplateDivTestCaseList() {
        return templateDivTestCaseList;
    }

    public String getTemplateStyleTestCaseList() {
        return templateStyleTestCaseList;
    }

    public String getTemplateIdStyle() {
        return templateIdStyle;
    }

    public String getTemplateSteps() {
        return templateSteps;
    }

    public String getTemplateTestCaseList() {
        return templateTestCaseList;
    }

    public String getTemplateContentTestCaseList() {
        return templateContentTestCaseList;
    }

    public String getTemplateContentTestCase() {
        return templateContentTestCase;
    }

    public String getTemplateMatrix() {
        return templateMatrix;
    }

    public String getTemplateRequirement() {
        return templateRequirement;
    }
    public String getTemplateContentRequirement() {
        return templateContentRequirement;
    }


    public String getTemplateScenario() {
        return templateScenario;
    }
    public String getTemplateRequirementList(){
        return templateRequirementList;
    }public String getTemplateContentRequirementList(){
        return templateContentRequirementList;
    }
    public String getTemplateStyleRequirementList() {
        return templateStyleRequirementList;
    }


    public String getTemplateRequirementIdStyle() {
        return templateRequirementIdStyle;
    }
    public String getTemplateParentTestCase() {
        return templateParentTestCase;
    }
}
