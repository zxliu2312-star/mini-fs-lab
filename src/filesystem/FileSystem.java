package filesystem;

import filesystem.context.SizeContext;
import filesystem.node.DirectoryNode;
import filesystem.node.Node;
import filesystem.path.PathUtil;
import java.util.Collections;
import java.util.List;

public final class FileSystem {
    private final DirectoryNode root = new DirectoryNode("");

    public void mkdir(String absPath) {
        PathUtil.PathTarget target = PathUtil.splitParentAndName(absPath);
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
        PathUtil.PathTarget target = PathUtil.splitParentAndName(absPath);
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
        Node node = resolve(absPath);
        if (node == null) {
            return Collections.emptyList();
        }
        return node.listOutput();
    }

    public long info(String absPath) {
        Node node = resolve(absPath);
        if (node == null) {
            return 0L;
        }
        return node.size(new SizeContext());
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
        return node == null ? null : node.asDirectory();
    }
}
