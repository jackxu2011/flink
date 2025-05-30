/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.runtime.executiongraph;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

import static org.apache.flink.util.Preconditions.checkNotNull;

/**
 * This class describe the inputs(partitions and subpartitions that belong to the same intermediate
 * result) information of an execution vertex.
 */
public class ExecutionVertexInputInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int subtaskIndex;

    // The key is the partition index range, and the value is the subpartition index range.
    private final Map<IndexRange, IndexRange> consumedSubpartitionGroups;

    public ExecutionVertexInputInfo(
            final int subtaskIndex,
            final IndexRange partitionIndexRange,
            final IndexRange subpartitionIndexRange) {
        this(
                subtaskIndex,
                Collections.singletonMap(
                        checkNotNull(partitionIndexRange), checkNotNull(subpartitionIndexRange)));
    }

    public ExecutionVertexInputInfo(
            final int subtaskIndex, final Map<IndexRange, IndexRange> consumedSubpartitionGroups) {
        this.subtaskIndex = subtaskIndex;
        this.consumedSubpartitionGroups = checkNotNull(consumedSubpartitionGroups);
    }

    /** Get the subpartition groups this subtask should consume. */
    public Map<IndexRange, IndexRange> getConsumedSubpartitionGroups() {
        return consumedSubpartitionGroups;
    }

    /** Get the index of this subtask. */
    public int getSubtaskIndex() {
        return subtaskIndex;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj != null && obj.getClass() == getClass()) {
            ExecutionVertexInputInfo that = (ExecutionVertexInputInfo) obj;
            return that.subtaskIndex == this.subtaskIndex
                    && that.consumedSubpartitionGroups.equals(this.consumedSubpartitionGroups);
        } else {
            return false;
        }
    }
}
