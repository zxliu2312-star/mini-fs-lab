package filesystem.path;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class PathUtil {
    private PathUtil() {
    }

    public static boolean isValidAbsolutePath(String path) {
        if (path == null || path.isEmpty() || path.charAt(0) != '/') {
            return false;
        }
        if (path.contains("//")) {
            return false;
        }
        if (path.length() > 1 && path.endsWith("/")) {
            return false;
        }
        if ("/".equals(path)) {
            return true;
        }

        String[] rawSegments = path.substring(1).split("/");
        for (String segment : rawSegments) {
            if (segment.isEmpty() || ".".equals(segment) || "..".equals(segment)) {
                return false;
            }
        }
        return true;
    }

    public static List<String> segments(String path) {
        if (!isValidAbsolutePath(path)) {
            return Collections.emptyList();
        }
        if ("/".equals(path)) {
            return Collections.emptyList();
        }

        String[] rawSegments = path.substring(1).split("/");
        List<String> segments = new ArrayList<>(rawSegments.length);
        Collections.addAll(segments, rawSegments);
        return segments;
    }

    public static PathTarget splitParentAndName(String path) {
        if (!isValidAbsolutePath(path) || "/".equals(path)) {
            return null;
        }

        List<String> segments = segments(path);
        String name = segments.get(segments.size() - 1);
        if (segments.size() == 1) {
            return new PathTarget("/", name);
        }

        StringBuilder parentPath = new StringBuilder();
        for (int i = 0; i < segments.size() - 1; i++) {
            parentPath.append('/').append(segments.get(i));
        }
        return new PathTarget(parentPath.toString(), name);
    }

    public static final class PathTarget {
        private final String parentPath;
        private final String name;

        public PathTarget(String parentPath, String name) {
            this.parentPath = parentPath;
            this.name = name;
        }

        public String getParentPath() {
            return parentPath;
        }

        public String getName() {
            return name;
        }
    }
}
