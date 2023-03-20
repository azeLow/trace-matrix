package utils;

import testCase.TestCase;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Класс, статические методы которого служат для обработки иерархической структуры тест-кейса.
 */
public class TestTreeUtils {

    /**
     * Создание директории с заданным именем и структурой, идентичной иерархической структуре передаваемых тест-кейсов.
     *
     * @param testCaseList - список тест-кейсов, для которых нужно реализовать иерархию директорий
     * @param testTreePath - путь к директории, в которой нужно реализовать иерархию директорий
     */
    public static void createFolder(List<TestCase> testCaseList, String testTreePath) {
        Set<String> testCasesPaths = getLeafTestCasesPaths(testCaseList, null);
        for (String leafTCPath : testCasesPaths) {
            File testTreeFolder = new File(testTreePath, leafTCPath);
            testTreeFolder.mkdirs();
        }
    }

    /**
     * Сравнение содержимого заданной директории с иерархической вложенностью заданных тест-кейсов (сравнение через
     * идентификаторы тест-кейсов).
     *
     * Например, если существует поддиректория (folderName/id1/id11), то должен существовать и тест-кейс с идентификатором
     * id11, являющийся непосредственным потомком тест-кейса с идентификатором id1. И наоборот.
     * @param folderName имя директории
     * @param testCaseList список тест-кейсов, иерархическая структрура структура которых сравнивается со структурой директории
     * @return true в том и только в том случае, если структура директории полностью идентична структуре тест-кейсов
     */
    public static boolean verifyFileTreeHierarchy(String folderName, List<TestCase> testCaseList) {
        File [] files = {new File(folderName)};
        Set<String> leafFolderPaths = getLeafFolderPaths(files, "");
        Set<String> leafTCPaths = getLeafTestCasesPaths(testCaseList, "test-tree");
        return leafFolderPaths.equals(leafTCPaths);
    }

    /**
     * Рекурсивный обход списка тест-кейсов со сбором иерархических путей всех найденных листовых (не являющихся группами)
     * тест-кесов в единое множество.
     *
     * @param testCaseList - список тест-кейсов, подлежащих обходу
     * @param prefix     - относительный путь (префикс), с которым конкатенируются имена всех переданных
     *                     тест-кейсов (если в дальнейшем предполагается сравнение структуры тест-кейсов со структурой
     *                     некоторой директории, следует передать имя такой директории)
     * @return множество всех путей, состоящих из идентификаторов тест-кейсов,
     * с началом, заданным как prefix, и концом в идентификаторах листовых тест-кейсов
     */
    private static Set<String> getLeafTestCasesPaths(List<TestCase> testCaseList, String prefix) {
        Set<String> leafTCPaths = new HashSet<>();
        for (TestCase tc : testCaseList) {
            String path = prefix + File.separator + tc.getId();
            if (tc.isGroup()) {
                leafTCPaths.addAll(getLeafTestCasesPaths(tc.getChildren(), path));
            } else {
                leafTCPaths.add(path);
            }
        }
        return leafTCPaths;
    }

    /**
     * Рекурсивный обход списка директорий со сбором относительных путей всех найденных листовых (не имеющих поддиректорий)
     * директорий в единое множество.
     * @param files - список файлов (директорий), подлежащих обходу
     * @param relativePath - текущий относительный путь (префикс) в иерархии тест-кейсов (следует задать пустым, если рассматривается массив
     *                     из одного файла)
     * @return множество относительных путей всех листовых директорий, найденных для заданного перечня директорий files
     */
    private static Set<String> getLeafFolderPaths(File[] files, String relativePath) {
        Set<String> leafFolderPaths = new HashSet<>();
        String rootPath = (CheckUtils.isEmpty(relativePath)) ? "" : relativePath + File.separator;
        for (File f : files) {
            String path = rootPath + f.getName();
            if (f.isDirectory()) {
                File [] fileChildren = f.listFiles();
                if (fileChildren.length != 0) {
                    leafFolderPaths.addAll(getLeafFolderPaths(fileChildren, path));
                }
                else {
                    leafFolderPaths.add(path);
                }
            }
        }
        return leafFolderPaths;
    }

}
