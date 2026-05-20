package filesystem.node;

import filesystem.context.SizeContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public final class DirectoryNode extends Node {
    private final TreeMap<String, Node> children = new TreeMap<>();

    public DirectoryNode(String name) {
        super(name);
    }

    @Override
    public DirectoryNode asDirectory() {
        return this;
    }

    public Node getChild(String childName) {
        return children.get(childName);
    }

    public void putChild(Node node) {
        children.put(node.getName(), node);
    }

    public void putDirectory(String childName) {
        Node existing = children.get(childName);
        if (existing != null && existing.asDirectory() != null) {
            return;
        }
        children.put(childName, new DirectoryNode(childName));
    }

    public void putFile(String childName, long fileSize) {
        children.put(childName, new FileNode(childName, fileSize));
    }

    @Override
    public long size(SizeContext context) {
        long total = 0L;
        for (Map.Entry<String, Node> entry : children.entrySet()) {
            total += entry.getValue().size(context);
        }
        return total;
    }

    @Override
    public List<String> listOutput() {
        return new ArrayList<>(children.keySet());
    }
}
