package filesystem.path;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class PathUtil {
    private PathUtil() {
    }

    public static boolean isValidAbsolutePath(String path) {
        return path != null && !path.isEmpty() && path.charAt(0) == '/';
    }

    public static String normalize(String path) {
        if (!isValidAbsolutePath(path)) {
            return null;
        }
        if ("/".equals(path)) {
            return "/";
        }

        List<String> segments = new ArrayList<>();
        String[] rawParts = path.substring(1).split("/");
        
        for (String part : rawParts) {
            if (part.isEmpty() || ".".equals(part)) {
                continue;
            } else if ("..".equals(part)) {
                if (!segments.isEmpty()) {
                    segments.remove(segments.size() - 1);
                }
            } else {
                segments.add(part);
            }
        }

        if (segments.isEmpty()) {
            return "/";
        }

        StringBuilder result = new StringBuilder();
        for (String segment : segments) {
            result.append('/').append(segment);
        }
        return result.toString();
    }

    public static List<String> segments(String normalizedPath) {
        if (!isValidAbsolutePath(normalizedPath) || "/".equals(normalizedPath)) {
            return Collections.emptyList();
        }

        String[] rawSegments = normalizedPath.substring(1).split("/");
        List<String> segments = new ArrayList<>(rawSegments.length);
        Collections.addAll(segments, rawSegments);
        return segments;
    }

    public static PathTarget splitParentAndName(String normalizedPath) {
        if (!isValidAbsolutePath(normalizedPath) || "/".equals(normalizedPath)) {
            return null;
        }

        List<String> segments = segments(normalizedPath);
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