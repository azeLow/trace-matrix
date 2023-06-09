package utils;

public interface LinkResolver {
    /**
     * Замена на гиперссылки подстрок вида <link type="тип" id="идентификатор-объекта"/>,
     * где
     * - "тип" - тип объекта, на который строится гиперссылка
     * - "идентификатор-объекта" - идентификатор объекта, на который строится гиперссылка
     *
     * @param line строка, в которой встречаются подстроки вида <link type="тип" id="идентификатор-объекта"/>
     * @return строка, в которой все конструкции вида <link type="тип" id="идентификатор-объекта"/>
     * заменены на гиперссылки
     */
    String resolveLinks(String line);

    String generateATag(String type, String id);
}
