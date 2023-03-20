package utils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LinkManager {
    private static final Map<String, ILink> implementations = new HashMap<>();

    private LinkManager() {
    }

    public static void registerLinkType(String type, ILink implementation) {
        implementations.put(type, implementation);
    }

    public static void resolveLinks() {
        for (String type : implementations.keySet()) {
            ILink link = implementations.get(type);
            link.resolveLinks();
        }
    }

    /**
     * Получение интерфейса для преобразования шаблонов ссылок на объекты на реальные ссылки.
     *
     * @param type тип объекта, из которого будут строиться ссылки на другие типы объектов
     * @return интерфейс, через который будут преобразовываться шаблоны ссылок
     */
    public static LinkResolver getLinkResolver(String type) {
        return new LinkResolverImpl(type);
    }

    private static String getRelativePath(String typeFrom, String typeTo) {
        ILink linkFrom = implementations.get(typeFrom);
        if (linkFrom == null)
            throw new RuntimeException("Не найдет интерфейс генерации ссылок для типа '" + typeFrom + "'");
        ILink linkTo = implementations.get(typeTo);
        if (linkTo == null)
            throw new RuntimeException("Не найдет интерфейс генерации ссылок для типа '" + typeTo + "'");
        String pathFrom = linkFrom.getPath();
        String pathTo = linkTo.getPath();
        if ("".equals(pathFrom))
            return pathTo;
        if (pathFrom.equals(pathTo))
            return "";
        return ".."+File.separator + pathTo;
    }

    private static class LinkResolverImpl implements LinkResolver {
        private final String typeFrom;

        private LinkResolverImpl(String type) {
            this.typeFrom = type;
        }

        @Override
        public String resolveLinks(String line) {
            if (line==null) return null;
            String rx = "<link\\s+type='([^']+)'\\s+id='([^']+)'/>";

            StringBuilder sb = new StringBuilder();
            Pattern p = Pattern.compile(rx);
            Matcher m = p.matcher(line);

            while (m.find()) {
                String type = m.group(1);
                String id = m.group(2);
                ILink ilink = implementations.get(type);
                if (ilink == null)
                    throw new RuntimeException("Не найдет интерфейс генерации ссылок для типа '" + type + "'");
                if ("".equals(id))
                    throw new RuntimeException("Пустой идентификатор объекта в ссылке на объект. Строка: " + line);
                String replacement = ilink.getLink(getRelativePath(typeFrom, type), id)
                        .replace("\\", "\\\\")
                        .replace("$", "\\$");
                m.appendReplacement(sb, replacement);
            }
            m.appendTail(sb);
            return sb.toString();
        }

        @Override
        public String generateATag(String type, String id) {
            ILink ilink = implementations.get(type);
            if (ilink == null)
                throw new RuntimeException("Не найдет интерфейс генерации ссылок для типа '" + type + "'");
            return ilink.getLink(getRelativePath(typeFrom, type), id);
        }
    }
}
