package filesystem.node;

import filesystem.context.SizeContext;
import java.util.Collections;
import java.util.List;

public final class LinkNode extends Node {
    private final Node target;

    public LinkNode(String name, Node target) {
        super(name);
        this.target = target;
    }

    public Node getTarget() {
        return target;
    }

    @Override
    public long size(SizeContext context) {
        return target.size(context);
    }

    @Override
    public List<String> listOutput() {
        Node resolved = target;
        while (resolved instanceof LinkNode) {
            resolved = ((LinkNode) resolved).getTarget();
        }
        return resolved.listOutput();
    }

    @Override
    public Node follow() {
        Node resolved = target;
        while (resolved instanceof LinkNode) {
            resolved = ((LinkNode) resolved).getTarget();
        }
        return resolved;
    }
}