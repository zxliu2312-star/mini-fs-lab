package filesystem.node;

import filesystem.context.SizeContext;
import java.util.List;

public abstract class Node {
    private final String name;

    protected Node(String name) {
        this.name = name;
    }

    public final String getName() {
        return name;
    }

    public DirectoryNode asDirectory() {
        return null;
    }

    public abstract long size(SizeContext context);

    public abstract List<String> listOutput();
}
