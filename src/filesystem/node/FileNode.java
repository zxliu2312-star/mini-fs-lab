package filesystem.node;

import filesystem.context.SizeContext;
import java.util.Collections;
import java.util.List;

public final class FileNode extends Node {
    private final long fileSize;

    public FileNode(String name, long fileSize) {
        super(name);
        this.fileSize = fileSize;
    }

    @Override
    public long size(SizeContext context) {
        if (!context.markVisited(this)) {
            return 0L;
        }
        return fileSize;
    }

    @Override
    public List<String> listOutput() {
        return Collections.singletonList(getName());
    }
}