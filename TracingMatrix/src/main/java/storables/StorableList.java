package storables;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import utils.ILink;
import utils.LinkManager;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Класс списка объектов, создаваемых из json-файлов, имеющих в своих полях ссылки на другие объекты
 * и собственное представление в html формате.
 */
public abstract class StorableList <T extends StorableObject> implements ILink {
    protected abstract T getNewObject(JSONObject jsonObject) throws ParseException, IOException;

    /**
     * Получение пути к каталогу, в который [будут] помещены html представления объектов из списка.
     * @return путь к каталогу, в который [будут] помещены html представления объектов из списка
     */
    public abstract String getFolder();
    protected Map<String, T> map;
    protected final List<T> list = new ArrayList<>();
    protected int maxLevel;
    protected final List<T> leafsList = new ArrayList<>();
    public int getMaxLevel() {
        return maxLevel;
    }
    public List<T> getLeafsList() {
        return leafsList;
    }
    public List<T> getList() {
        return this.list;
    }
    /**
     * Чтение списка объектов из json-файла.
     *
     * @param fileName имя json-файла с объектами
     */
    public StorableList(String fileName) throws IOException {
        try (Reader reader = new FileReader(fileName)) {
            JSONParser parser = new JSONParser();
            JSONArray json = (JSONArray) parser.parse(reader);
            for (Object object : json) {
                JSONObject jsonObj = (JSONObject) object;
                T storableObject = getNewObject(jsonObj);
                list.add(storableObject);
            }
            processTree();
        } catch (ParseException e) {
            throw new RuntimeException("Ошибка чтения файла\"" + fileName + "\"", e);
        }
    }

    protected void processTree() {
        maxLevel = 0;
        map = new HashMap<>();
        for (StorableObject so: list) {
            maxLevel = Math.max(maxLevel, so.processTree(map, leafsList));
        }
    }

    /**
     * Создание HTML документа из списка с объектами.
     */
    public void createHTML (File rootDir) throws IOException {
        File folder = new File(rootDir, getFolder());
        folder.mkdirs();
        for (T so : leafsList) {
            if (!so.isGroup())
                so.createHTML(folder);
        }
    }


    public String getLink(String relativePath, String id) {
        T r = map.get(id);
        if (r == null)
            throw new RuntimeException("Не найден объект с идентификатором '" + id + "'");
        return r.getLink(relativePath);
    }

    public String getPath() {
        return getFolder() + File.separator;
    }

    public void resolveLinks() {
        for (String id : map.keySet()) {
            T obj = map.get(id);
            obj.resolveLinks(LinkManager.getLinkResolver(obj.getLinkType()));
        }
    }
}

