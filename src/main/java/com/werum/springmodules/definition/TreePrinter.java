/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.werum.springmodules.definition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Helper class allowing to print a tree structure in a folder-like layout into a string
 */
public class TreePrinter<E> {
    private final E instance;

    private final Set<TreePrinter<E>> children;

    public TreePrinter(E instance) {
        this.instance = instance;
        children = new HashSet<>();
    }

    public E getInstance() {
        return instance;
    }

    public Set<TreePrinter<E>> getChildren() {
        return children;
    }

    public String getDependencyView() {
        StringBuilder builder = new StringBuilder();
        appendDependencyView(builder, Collections.emptyList());

        return builder.toString();
    }

    private void appendDependencyView(StringBuilder builder, List<Boolean> offset) {
        builder.append(instance + "\n");

        int childCount = 0;
        for (TreePrinter<E> child : children) {
            childCount++;
            for (int i = 0; i < offset.size(); i++) {
                if (!offset.get(i)) {
                    builder.append("|");
                } else {
                    builder.append(" ");
                }
                builder.append("   ");
            }
            builder.append("+-> ");

            List<Boolean> newOffset = new ArrayList<>(offset);
            newOffset.add(childCount == children.size());
            child.appendDependencyView(builder, newOffset);
        }
    }
}
