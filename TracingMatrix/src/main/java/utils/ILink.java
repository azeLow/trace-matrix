package utils;

public interface ILink {
    String getLink(String relativePath, String id);

    void resolveLinks();

    String getPath();
}
