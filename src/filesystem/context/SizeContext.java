package filesystem.context;

import filesystem.node.Node;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class SizeContext {
    private final Set<Node> visited = Collections.newSetFromMap(new java.util.IdentityHashMap<>());

    public boolean markVisited(Node node) {
        return visited.add(node);
    }

    public Set<Node> visitedNodes() {
        return Collections.unmodifiableSet(new HashSet<>(visited));
    }
}
