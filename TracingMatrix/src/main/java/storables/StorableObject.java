package storables;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import utils.CheckUtils;
import utils.DefaultIdUtils;
import utils.LinkResolver;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Класс сущностей, создаваемых из JSON объектов, поля которых могут хранить ссылки и для которых
 * возможно получение их html представлений.
 */
public abstract class StorableObject <T extends StorableObject> {

    public abstract T getNewObject(JSONObject jsonObject) throws ParseException, IOException;
    public abstract String getLinkType();
    protected static final String ID_PLACE = "<!--ID-->";
    protected static final String NAME_PLACE = "<!--NAME-->";
    protected static final String DESCRIPTION_PLACE = "<!--DESCRIPTION-->";
    protected static final String STATUS_PLACE = "<!--STATUS-->";
    protected static final String PRECONDITIONS_PLACE = "<!--PRECONDITIONS-->";

    protected final String id;
    protected final String name;
    protected String description;
    protected final String status;
    protected final List<T> children;
    protected int branchLeafsCount = 0;
    protected Set<String> incompleteFields = new HashSet<>();

    public int getBranchLeafsCount() {
        return branchLeafsCount;
    }

    public boolean isLeaf() {
        return children == null || children.size() == 0;
    }

    public List<T> getChildren() {
        return children;
    }

    public String getName() {
        return this.name;
    }

    public String getStatus() {
        return this.status;
    }

    public String getId() {
        return this.id;
    }

    public String getDescription() {
        return this.description;
    }

    public Set<String> getIncompleteFields() {
        return this.incompleteFields;
    }

    /**
     * Добавление имени поля в множество имен незаполненных полей в том случае, если поле пусто.
     * @param field проверяемое на пустоту поле (ялвяется списком объектов)
     * @param fieldName имя поля, которе добавится в множество незаполненных полей в том случае, если поле пусто
     * @return true, если имя поля было добавлено в множество незаполненных полей; иначе - false
     */
    protected boolean markFieldIfIncomplete(Collection<?> field, String fieldName) {
        if (CheckUtils.isEmpty(field)) {
            incompleteFields.add(fieldName);
            return true;
        }
        return false;
    }
    /**
     * Добавление имени поля в множество имен незаполненных полей в том случае, если поле пусто.
     * @param field проверяемое на пустоту поле (является строкой)
     * @param fieldName имя поля, которе добавится в множество незаполненных полей в том случае, если поле пусто
     * @return true, если имя поля было добавлено в множество незаполненных полей; иначе - false
     */
    protected boolean markFieldIfIncomplete(String field, String fieldName) {
        if (CheckUtils.isEmpty(field)) {
            incompleteFields.add(fieldName);
            return true;
        }
        return false;
    }

    public StorableObject(JSONObject jsonObj) throws RuntimeException {
        if (jsonObj != null) {
            String idStr = (String) jsonObj.get("id");
            id = (CheckUtils.isEmpty(idStr)) ? DefaultIdUtils.getId() : (idStr);
            markFieldIfIncomplete(name = (String) jsonObj.get("name"), "name");
            markFieldIfIncomplete(status = (String) jsonObj.get("status"), "status");
            markFieldIfIncomplete(description = (String) jsonObj.get("description"), "description");
            children = createChildren((JSONArray) jsonObj.get("children"));
            if (isGroup())
                for (T c : children)
                    branchLeafsCount += c.branchLeafsCount;
            branchLeafsCount = Math.max(branchLeafsCount, 1);
        } else {
            throw new RuntimeException("Ошибка парсинга JSON-объекта для StorableObject. Передан null-объект.");
        }
    }

    public boolean isGroup() {
        return children != null;
    }

    protected List<T> createChildren(JSONArray children) {
        List<T> childrenList = null;
        if (children != null && children.size() > 0) {
            childrenList = new ArrayList<>();
            for (Object child : children) {
                T so = null;
                try {
                    so = getNewObject((JSONObject) child);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                childrenList.add(so);
            }
        }
        return childrenList;
    }

    /**
     * Обработка дерева объектов: вычисление листовых элементов, глубины дерева
     * и списка всех узлов в виде карты id_объекта->объект
     * @param map карта id_объекта->объект
     * @param leafTests листовые элементы дерева
     * @return глубина ветки текущего элемента (от 1)
     */
    public int processTree(Map<String, StorableObject> map, List<StorableObject> leafTests) {
        int maxChildrenDepth = 0;
        map.put(id, this);
        if (children == null || children.size() == 0)
            leafTests.add(this);
        else
            for (T so : children) {
                maxChildrenDepth = Math.max(maxChildrenDepth, so.processTree(map, leafTests));
            }
        return maxChildrenDepth + 1;
    }

    public abstract void createHTML(File dir) throws IOException;

    public abstract void resolveLinks(LinkResolver linkResolver);

    public String getLink(String folder) {
        if (isGroup())
            return name;
        return String.format("<a href='" + folder + "%s.html' title='%s'>%s</a>",
                id, description, name);
    }
}

