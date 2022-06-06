package net.oschina.gitapp.bean;

import org.codehaus.jackson.annotate.JsonProperty;

@SuppressWarnings("serial")
public class CodeTree extends Entity {

    public final static String TYPE_TREE = "tree";
    public final static String TYPE_BLOB = "blob";

    @JsonProperty("name")
    private String _name;

    @JsonProperty("type")
    private String _type;

    @JsonProperty("mode")
    private String _mode;

    private String _path;

    public String getPath() {
        return _path;
    }

    public void setPath(String path) {
        this._path = path;
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        this._name = name;
    }

    public String getType() {
        return _type;
    }

    public void setType(String type) {
        this._type = type;
    }

    public String getMode() {
        return _mode;
    }

    public void setMode(String mode) {
        this._mode = mode;
    }


    // 判断是不是代码文件
    public boolean isCodeTextFile(String fileName) {
        boolean res = false;
        // 文件的后缀
        int index = fileName.lastIndexOf(".");
        if (index > 0) {
            fileName = fileName.substring(index);
        }
        String codeFileSuffix[] = new String[]
                {
                        ".java",
                        ".confg",
                        ".ini",
                        ".xml",
                        ".json",
                        ".txt",
                        ".go",
                        ".php",
                        ".php3",
                        ".php4",
                        ".php5",
                        ".js",
                        ".css",
                        ".html",
                        ".properties",
                        ".c",
                        ".hpp",
                        ".h",
                        ".hh",
                        ".cpp",
                        ".cfg",
                        ".rb",
                        ".example",
                        ".gitignore",
                        ".project",
                        ".classpath",
                        ".m",
                        ".md",
                        ".rst",
                        ".vm",
                        ".cl",
                        ".py",
                        ".pl",
                        ".haml",
                        ".erb",
                        ".scss",
                        ".bat",
                        ".coffee",
                        ".as",
                        ".sh",
                        ".m",
                        ".pas",
                        ".cs",
                        ".groovy",
                        ".scala",
                        ".sql",
                        ".bas",
                        ".xml",
                        ".vb",
                        ".xsl",
                        ".swift",
                        ".ftl",
                        ".yml",
                        ".ru",
                        ".jsp",
                        ".markdown",
                        ".cshap",
                        ".apsx",
                        ".sass",
                        ".less",
                        ".ftl",
                        ".haml",
                        ".log",
                        ".tx",
                        ".csproj",
                        ".sln",
                        ".clj",
                        ".scm",
                        ".xhml",
                        ".xaml",
                        ".lua",
                        ".sty",
                        ".cls",
                        ".thm",
                        ".tex",
                        ".bst",
                        ".config"
                };
        for (String string : codeFileSuffix) {
            if (fileName.equalsIgnoreCase(string)) {
                res = true;
            }
        }

        // 特殊的文件
        String fileNames[] = new String[]
                {
                        "LICENSE", "TODO", "README", "readme", "makefile", "gemfile", "gemfile.*", "gemfile.lock", "CHANGELOG"
                };

        for (String string : fileNames) {
            if (fileName.equalsIgnoreCase(string)) {
                res = true;
            }
        }

        return res;
    }

    // 判断是否是图片
    public boolean isImage(String fileName) {
        boolean res = false;
        // 图片后缀
        int index = fileName.lastIndexOf(".");
        if (index > 0) {
            fileName = fileName.substring(index);
        }
        String imageSuffix[] = new String[]
                {
                        ".png", ".jpg", ".jpeg", ".jpe", ".bmp", ".exif", ".dxf", ".wbmp", ".ico", ".jpe", ".gif", ".pcx", ".fpx", ".ufo", ".tiff", ".svg", ".eps", ".ai", ".tga", ".pcd", ".hdri"
                };
        for (String string : imageSuffix) {
            if (fileName.equalsIgnoreCase(string)) {
                res = true;
            }
        }
        return res;
    }

    /**
     * 调用Android系统注册该action的APP
     *
     * @param fileName
     * @return
     */
    public static String getMIME(String fileName) {
        String[][] MIME_MapTable = {
                //{后缀名， MIME类型}
                {".3gp", "video/3gpp"},
                {".apk", "application/vnd.android.package-archive"},
                {".asf", "video/x-ms-asf"},
                {".avi", "video/x-msvideo"},
                {".bin", "application/octet-stream"},
                {".bmp", "image/bmp"},
                {".c", "text/plain"},
                {".class", "application/octet-stream"},
                {".conf", "text/plain"},
                {".cpp", "text/plain"},
                {".doc", "application/msword"},
                {".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
                {".xls", "application/vnd.ms-excel"},
                {".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
                {".exe", "application/octet-stream"},
                {".gif", "image/gif"},
                {".gtar", "application/x-gtar"},
                {".gz", "application/x-gzip"},
                {".h", "text/plain"},
                {".htm", "text/html"},
                {".html", "text/html"},
                {".jar", "application/java-archive"},
                {".java", "text/plain"},
                {".jpeg", "image/jpeg"},
                {".jpg", "image/jpeg"},
                {".js", "application/x-javascript"},
                {".log", "text/plain"},
                {".m3u", "audio/x-mpegurl"},
                {".m4a", "audio/mp4a-latm"},
                {".m4b", "audio/mp4a-latm"},
                {".m4p", "audio/mp4a-latm"},
                {".m4u", "video/vnd.mpegurl"},
                {".m4v", "video/x-m4v"},
                {".mov", "video/quicktime"},
                {".mp2", "audio/x-mpeg"},
                {".mp3", "audio/x-mpeg"},
                {".mp4", "video/mp4"},
                {".mpc", "application/vnd.mpohun.certificate"},
                {".mpe", "video/mpeg"},
                {".mpeg", "video/mpeg"},
                {".mpg", "video/mpeg"},
                {".mpg4", "video/mp4"},
                {".mpga", "audio/mpeg"},
                {".msg", "application/vnd.ms-outlook"},
                {".ogg", "audio/ogg"},
                {".pdf", "application/pdf"},
                {".png", "image/png"},
                {".pps", "application/vnd.ms-powerpoint"},
                {".ppt", "application/vnd.ms-powerpoint"},
                {".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"},
                {".prop", "text/plain"},
                {".rc", "text/plain"},
                {".rmvb", "audio/x-pn-realaudio"},
                {".rtf", "application/rtf"},
                {".sh", "text/plain"},
                {".tar", "application/x-tar"},
                {".tgz", "application/x-compressed"},
                {".txt", "text/plain"},
                {".wav", "audio/x-wav"},
                {".wma", "audio/x-ms-wma"},
                {".wmv", "audio/x-ms-wmv"},
                {".wps", "application/vnd.ms-works"},
                {".xml", "text/plain"},
                {".z", "application/x-compress"},
                {".zip", "application/x-zip-compressed"},
                {"", "*/*"}
        };

        String mime = "*/*";
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex < 0) {
            return mime;
        }

        String end = fileName.substring(dotIndex, fileName.length()).toLowerCase();
        if (end == "") return mime;
        for (int i = 0; i < MIME_MapTable.length; i++) {
            if (end.equals(MIME_MapTable[i][0]))
                mime = MIME_MapTable[i][1];
        }
        return mime;
    }
}
