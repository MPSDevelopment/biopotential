package com.mps.machine.dbs.arkdb;

import java.util.List;

class ArkDBNode {
    boolean isChildOf(ArkDBNode parent) {
        return this.parent == parent
            || (this.parent != null && this.parent.isChildOf(parent));
    }

    boolean isParentOf(ArkDBNode childNode) {
        if (this.children.contains(childNode)) {
            return true;
        }
        for (ArkDBNode child : this.children) {
            if (child.isParentOf(childNode)) {
                return true;
            }
        }
        return false;
    }

    int id;
    ArkDBNode parent;
    List<ArkDBNode> children;
}
