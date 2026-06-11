package filesystem;

import filesystem.context.SizeContext;
import filesystem.node.DirectoryNode;
import filesystem.node.LinkNode;
import filesystem.node.Node;
import filesystem.path.PathUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class FileSystem {
    private final DirectoryNode root = new DirectoryNode("");

    public void mkdir(String absPath) {
        String normalizedPath = PathUtil.normalize(absPath);
        if (normalizedPath == null || "/".equals(normalizedPath)) {
            return;
        }

        PathUtil.PathTarget target = PathUtil.splitParentAndName(normalizedPath);
        if (target == null) {
            return;
        }

        DirectoryNode parent = resolveDirectory(target.getParentPath());
        if (parent == null) {
            return;
        }

        parent.putDirectory(target.getName());
    }

    public void touch(String absPath, long size) {
        if (size < 0L) {
            return;
        }

        String normalizedPath = PathUtil.normalize(absPath);
        if (normalizedPath == null || "/".equals(normalizedPath)) {
            return;
        }

        PathUtil.PathTarget target = PathUtil.splitParentAndName(normalizedPath);
        if (target == null) {
            return;
        }

        DirectoryNode parent = resolveDirectory(target.getParentPath());
        if (parent == null) {
            return;
        }

        parent.putFile(target.getName(), size);
    }

    public List<String> ls(String absPath) {
        String normalizedPath = PathUtil.normalize(absPath);
        if (normalizedPath == null) {
            return Collections.emptyList();
        }

        Node node = resolve(normalizedPath);
        if (node == null) {
            return Collections.emptyList();
        }

        if (node instanceof LinkNode) {
            Node resolved = node.follow();
            if (resolved instanceof DirectoryNode) {
                return resolved.listOutput();
            }
            return Collections.singletonList(node.getName());
        }

        return node.listOutput();
    }

    public Long info(String absPath) {
        String normalizedPath = PathUtil.normalize(absPath);
        if (normalizedPath == null) {
            return null;
        }

        Node node = resolve(normalizedPath);
        if (node == null) {
            return null;
        }

        return node.size(new SizeContext());
    }

    public List<String> find(String absPath, String name) {
        String normalizedPath = PathUtil.normalize(absPath);
        if (normalizedPath == null || name == null || name.isEmpty()) {
            return Collections.emptyList();
        }

        Node node = resolve(normalizedPath);
        if (node == null) {
            return Collections.emptyList();
        }

        List<String> results = new ArrayList<>();
        Set<Node> expandedDirs = new HashSet<>();
        findRecursive(node, normalizedPath, name, expandedDirs, results);
        Collections.sort(results);
        return results;
    }

    private void findRecursive(Node node, String currentPath, String name,
            Set<Node> expandedDirs, List<String> results) {
        if (name.equals(node.getName())) {
            results.add(currentPath);
        }

        Node resolved = node.follow();
        if (!(resolved instanceof DirectoryNode)) {
            return;
        }
        if (!expandedDirs.add(resolved)) {
            return;
        }

        DirectoryNode dir = (DirectoryNode) resolved;
        for (String childName : dir.listOutput()) {
            Node child = dir.getChild(childName);
            if (child == null) {
                continue;
            }

            String childPath = currentPath.equals("/") ? "/" + childName : currentPath + "/" + childName;
            findRecursive(child, childPath, name, expandedDirs, results);
        }
    }

    public void rm(String absPath) {
        String normalizedPath = PathUtil.normalize(absPath);
        if (normalizedPath == null || "/".equals(normalizedPath)) {
            return;
        }

        PathUtil.PathTarget target = PathUtil.splitParentAndName(normalizedPath);
        if (target == null) {
            return;
        }

        DirectoryNode parent = resolveDirectory(target.getParentPath());
        if (parent == null) {
            return;
        }

        Node node = parent.getChild(target.getName());
        if (node == null) {
            return;
        }

        if (node instanceof DirectoryNode && !((DirectoryNode) node).isEmpty()) {
            return;
        }

        parent.removeChild(target.getName());
    }

    public void link(String srcAbsPath, String dstAbsPath) {
        String srcNormalized = PathUtil.normalize(srcAbsPath);
        String dstNormalized = PathUtil.normalize(dstAbsPath);

        if (srcNormalized == null || dstNormalized == null || "/".equals(dstNormalized)) {
            return;
        }

        Node srcNode = resolve(srcNormalized);
        if (srcNode == null) {
            return;
        }

        PathUtil.PathTarget dstTarget = PathUtil.splitParentAndName(dstNormalized);
        if (dstTarget == null) {
            return;
        }

        DirectoryNode parent = resolveDirectory(dstTarget.getParentPath());
        if (parent == null) {
            return;
        }

        parent.putLink(dstTarget.getName(), srcNode);
    }

    private Node resolve(String absPath) {
        if (!PathUtil.isValidAbsolutePath(absPath)) {
            return null;
        }
        if ("/".equals(absPath)) {
            return root;
        }

        Node current = root;
        for (String segment : PathUtil.segments(absPath)) {
            current = current.follow();
            DirectoryNode directory = current.asDirectory();
            if (directory == null) {
                return null;
            }
            current = directory.getChild(segment);
            if (current == null) {
                return null;
            }
        }
        return current;
    }

    private DirectoryNode resolveDirectory(String absPath) {
        Node node = resolve(absPath);
        if (node == null) {
            return null;
        }
        node = node.follow();
        return node.asDirectory();
    }
}
