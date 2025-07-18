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

package org.apache.flink.table.planner.plan.nodes.exec.serde;

import org.apache.flink.annotation.Internal;
import org.apache.flink.table.catalog.TableDistribution;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.JsonParser;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.ObjectCodec;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.DeserializationContext;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.JsonNode;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.List;

import static org.apache.flink.table.planner.plan.nodes.exec.serde.CompiledPlanSerdeUtil.deserializeFieldOrNull;
import static org.apache.flink.table.planner.plan.nodes.exec.serde.CompiledPlanSerdeUtil.deserializeList;
import static org.apache.flink.table.planner.plan.nodes.exec.serde.CompiledPlanSerdeUtil.traverse;
import static org.apache.flink.table.planner.plan.nodes.exec.serde.TableDistributionJsonSerializer.BUCKET_COUNT;
import static org.apache.flink.table.planner.plan.nodes.exec.serde.TableDistributionJsonSerializer.BUCKET_KEYS;
import static org.apache.flink.table.planner.plan.nodes.exec.serde.TableDistributionJsonSerializer.KIND;

/**
 * JSON deserializer for {@link TableDistribution}.
 *
 * @see TableDistributionJsonSerializer for the reverse operation
 */
@Internal
final class TableDistributionJsonDeserializer extends StdDeserializer<TableDistribution> {
    private static final long serialVersionUID = 1L;

    TableDistributionJsonDeserializer() {
        super(TableDistribution.class);
    }

    @Override
    public TableDistribution deserialize(JsonParser jsonParser, DeserializationContext ctx)
            throws IOException {
        JsonNode jsonNode = jsonParser.readValueAsTree();

        if (!(jsonNode instanceof ObjectNode)) {
            return null;
        }

        ObjectNode objectNode = (ObjectNode) jsonNode;
        ObjectCodec codec = jsonParser.getCodec();

        TableDistribution.Kind kind =
                ctx.readValue(
                        traverse(jsonNode.required(KIND), codec), TableDistribution.Kind.class);
        Integer bucketCount =
                deserializeFieldOrNull(objectNode, BUCKET_COUNT, Integer.class, codec, ctx);
        List<String> bucketKeys =
                deserializeList(objectNode, BUCKET_KEYS, String.class, codec, ctx);

        return TableDistribution.of(kind, bucketCount, bucketKeys);
    }
}
