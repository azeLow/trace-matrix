package html;

import requirement.Requirement;
import requirement.RequirementList;
import storables.StorableList;
import storables.StorableObject;
import template.Templates;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class RequirementCatalog <T extends StorableObject>{

    public static final String TARGET_PLACE = "<!--TARGET-->";
    public static final String REQUIREMENT_PLACE = "<!--REQUIREMENT-->";
    public static final String REQUIREMENT_LIST_PLACE = "<!--REQUIREMENT-LIST-->";
    public static final String REQUIREMENTS_PLACE = "<!--REQUIREMENTS-->";
    public static final String DESCRIPTION_PLACE = "<!--DESCRIPTION-->";
    public static final String CLASS_PLACE = "<!--CLASS_OPEN-->";
    private final StorableList<Requirement> requirementList;
    private static final String ID_TARGET_PLACE = "<!--ID2-->";
    private static final String ID_PLACE = "<!--ID-->";

    public RequirementCatalog(RequirementList requirementList)  {
        this.requirementList = requirementList;
    }

    private StringBuilder getRequirementRowsList(Requirement requirement, String section) throws IOException {
        StringBuilder html = new StringBuilder();
        Templates templates = Templates.get();
       String ln = templates.getTemplateContentRequirementList();
       if(!requirement.isGroup()){
           html.append(ln.replace(ID_PLACE, requirement.getId()).replace(DESCRIPTION_PLACE, section + ".\t"+requirement.getName()).replace(ID_PLACE, requirement.getId()));

       }
       else {
        html.append(ln.replace(CLASS_PLACE, "class=\"inc\" >").replace(ID_PLACE, requirement.getId()).replace(DESCRIPTION_PLACE, section + ".\t"+requirement.getName()).replace(ID_PLACE, requirement.getId()))
        ;

    }
        if (!requirement.isLeaf()){
            int subsectionNum = 0;
            for (Requirement req : requirement.getChildren()) {
                subsectionNum++;
                StringBuilder subsection = new StringBuilder(section + "." + subsectionNum);
                html.append(getRequirementRowsList(req, subsection.toString()));
            }
            }
        return html;

    }

    private StringBuilder getRequirementList() throws IOException {
        StringBuilder html = new StringBuilder();
        int section = 0;
        for (Requirement req : requirementList.getList()) {
            section++;
            html.append(getRequirementRowsList(req,"" + section));
        }
        return html;
    }

    public void createRequirementListHtml(File dir) throws IOException {
        File testList = new File(dir, "requirementList.html");
        BufferedWriter bw = new BufferedWriter(new FileWriter(testList, StandardCharsets.UTF_8));
        PrintWriter pwr = new PrintWriter(bw);
        createRequirementListCSS(dir);
        Templates templates = Templates.get();
        String ln = templates.getTemplateRequirementList();
        pwr.println(ln.replace(REQUIREMENT_LIST_PLACE, getRequirementList()).replace(REQUIREMENTS_PLACE, getRequirementRows()));
        pwr.close();
    }

    private StringBuilder getDivRequirementRows(Requirement requirement) throws IOException {
        StringBuilder all = new StringBuilder();
        Templates templates = Templates.get();
        String ln = templates.getTemplateDivTestCaseList();
        if(!requirement.isGroup()){
            all.append(ln.replace(ID_PLACE, requirement.getId()).replace(REQUIREMENT_PLACE, requirement.getRequirementBody()));
        }
        else {
            all.append(ln.replace(ID_PLACE, requirement.getId()).replace(REQUIREMENT_PLACE, requirement.getRequirementBody()));        }
        if (!requirement.isLeaf()){
            for (Requirement req : requirement.getChildren()) {
                all.append(getDivRequirementRows(req));

            }}
        return all;
    }

    private StringBuilder getRequirementRows() throws IOException {
        StringBuilder all = new StringBuilder();
        for (Requirement requirement : requirementList.getList()) {
            all.append(getDivRequirementRows(requirement));
        }
        return all;
    }

    private StringBuilder getTestListRowsCss(Requirement requirement) throws IOException {
        StringBuilder css = new StringBuilder();
        Templates templates = Templates.get();
        String ln = templates.getTemplateRequirementIdStyle();
        if (!requirement.isGroup()) {
            css.append(ln.replace(ID_PLACE, requirement.getId()).replace(ID_TARGET_PLACE, requirement.getId()));
        }
        else {
            css.append(ln.replace(ID_PLACE, requirement.getId()).replace(ID_TARGET_PLACE, requirement.getId()));

        }
        if (!requirement.isLeaf()) {
            for (Requirement req : requirement.getChildren()) {
                css.append(getTestListRowsCss(req));
            }
        }
        return css;
    }

    private StringBuilder getListRequirementCss() throws IOException {
        StringBuilder css = new StringBuilder();
        for (Requirement requirement : requirementList.getList()) {
            css.append(getTestListRowsCss(requirement));
        }
        return css;
    }

    public void createRequirementListCSS(File dir) throws IOException {
        File req = new File(dir, "styleRequirementCatalog.css");
        BufferedWriter bw = new BufferedWriter(new FileWriter(req, StandardCharsets.UTF_8));
        PrintWriter pwr = new PrintWriter(bw);
        Templates templates = Templates.get();
        String ln = templates.getTemplateStyleRequirementList();
        pwr.println(ln.replace(TARGET_PLACE, getListRequirementCss()));
        pwr.close();
    }
}
