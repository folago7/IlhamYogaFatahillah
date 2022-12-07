package net.oschina.gitapp.utils;

import java.util.HashMap;
import java.util.Map;

public final class CodeFileUtils {

    private static final String[] codeFileSuffix = new String[]{
            ".java", ".confg", ".ini", ".xml", ".json", ".txt", ".go", ".php", ".php3", ".php4", ".php5",
            ".js", ".css", ".html", ".properties", ".c", ".hpp", ".h", ".hh", ".cpp", ".cfg", ".rb", ".example",
            ".gitignore", ".project", ".classpath", ".m", ".md", ".rst", ".vm", ".cl", ".py", ".pl", ".haml",
            ".erb", ".scss", ".bat", ".coffee", ".as", ".sh", ".m", ".pas", ".cs", ".groovy", ".scala",
            ".sql", ".bas", ".xml", ".vb", ".xsl", ".swift", ".ftl", ".yml", ".ru", ".jsp", ".markdown",
            ".cshap", ".apsx", ".sass", ".less", ".ftl", ".haml", ".log", ".tx", ".csproj", ".sln", ".clj",
            ".scm", ".xhml", ".xaml", ".lua", ".sty", ".cls", ".thm", ".tex", ".bst", ".config", "Podfile",
            "Podfile.lock", ".plist", ".storyboard", "gradlew", ".gradle", ".pro", ".pbxproj", ".xcscheme",
            ".proto", ".wxss", ".wxml", ".vi", ".ctl", ".ts", ".kt", ".vue", ".babelrc", ".ashx", ".asm",
            ".cc", ".rkt", ".lisp", ".hs", ".props", ".editorconfig", ".dockerignore", ".gitattributes",
            ".s", ".ld"
    };

    private static final String[] fileNames = new String[]{
            "LICENSE", "TODO", "README", "readme", "makefile", "gemfile",
            "gemfile.*", "gemfile.lock", "CHANGELOG"
    };

    private static final String[] binFiles = new String[]{
            ".iso", ".rar", ".zip", ".exe", ".bin", ".pdf", ".word", ".doc", ".rm", ".avi", "mp3", ".mp4", ".xls",
            ".tmp", ".mdf", ".mid", ".7z", ".wav", ".aif", ".au", ".mp3", ".ram", ".wma", ".mmf", ".aac", ".amr",
            ".flac", ".mpg", ".mov", ".rmvb", ".swf"

    };

    private static final String[] imgFiles = new String[]{
            ".png", ".jpg", ".jpeg", ".jpe", ".bmp", ".exif", ".dxf", ".wbmp", ".ico", ".jpe", ".gif", ".pcx", ".fpx", ".ufo", ".tiff", ".svg", ".eps", ".ai", ".tga", ".pcd", ".hdri"
    };


    private static final Map<String, String> IMG_MAPS = new HashMap<>();
    private static final Map<String, String> BIN_MAPS = new HashMap<>();
    private static final Map<String, String> FILE_MAPS = new HashMap<>();
    private static final Map<String, String> CODE_MAPS = new HashMap<>();

    public static void init() {
        if (CODE_MAPS.size() > 0) {
            return;
        }
        for (String string : codeFileSuffix) {
            CODE_MAPS.put(string, string);
        }
        for (String string : fileNames) {
            FILE_MAPS.put(string, string);
        }
        for (String string : binFiles) {
            BIN_MAPS.put(string, string);
        }
        for (String string : imgFiles) {
            IMG_MAPS.put(string, string);
        }
    }

    // 判断是不是代码文件
    public static boolean isCodeTextFile(String fileName) {

        // 文件的后缀
        int index = fileName.lastIndexOf(".");
        if (index > 0) {
            fileName = fileName.substring(index).toLowerCase();
        }

        if (CODE_MAPS.containsKey(fileName)) {
            return true;
        }

        if (FILE_MAPS.containsKey(fileName)) {
            return true;
        }
        if (BIN_MAPS.containsKey(fileName)) {
            return false;
        }
        if (IMG_MAPS.containsKey(fileName)) {
            return false;
        }
        return true;
    }

    public static boolean isImage(String fileName) {
        int index = fileName.lastIndexOf(".");
        if (index > 0) {
            fileName = fileName.substring(index).toLowerCase();
        }
        return IMG_MAPS.containsKey(fileName);
    }

    public static boolean isPDF(String fileName) {
        int index = fileName.lastIndexOf(".");
        if (index > 0) {
            fileName = fileName.substring(index).toLowerCase();
        }
        return ".pdf".equals(fileName);
    }
}
