package com.kneelawk.graphlib.impl.util;

import com.kneelawk.graphlib.api.graph.BlockGraph;
import com.kneelawk.graphlib.api.graph.GraphUniverse;
import com.kneelawk.graphlib.api.graph.LinkHolder;
import com.kneelawk.graphlib.api.graph.NodeHolder;
import com.kneelawk.graphlib.api.graph.user.BlockNode;
import com.kneelawk.graphlib.api.graph.user.LinkKey;
import com.kneelawk.graphlib.api.graph.user.SidedBlockNode;

import com.kneelawk.graphlib.api.util.ColorUtils;

import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;

public class GraphGizmo {
    public static boolean GRAPH_ENABLED = checkProp("GRAPH");
    public static boolean UPDATES_ENABLED = checkProp("UPDATES");

    private static boolean checkProp(String name) {
        var value = System.getProperty("GRAPHLIB_DEBUG_" + name);
        return "".equals(value) || "true".equalsIgnoreCase(value);
    }

    public static void renderGraph(BlockGraph graph, GraphUniverse universe) {
        var connections = new HashSet<LinkHolder<LinkKey>>();

        graph.getNodes().forEach(holder -> {
            var pos = holder.getPos().pos();

            var strokeColor = nodeColor(holder.getPos().node(), universe, 1f);
            var fillColor = nodeColor(holder.getPos().node(), universe, 0.5f);
            var style = holder.getNodeEntity() == null
                ? GizmoStyle.stroke(strokeColor)
                : GizmoStyle.strokeAndFill(strokeColor, 2.5f, fillColor);

            if (holder.getPos().node() instanceof SidedBlockNode sided) {
                var center = getNodePos(holder, 0.35);
                Gizmos.rect(center.add(-0.1), center.add(0.1), sided.getSide(), style).setAlwaysOnTop();
            } else {
                Gizmos.cuboid(AABB.ofSize(pos.getCenter(), 0.2, 0.2, 0.2), style).setAlwaysOnTop();
            }

            connections.addAll(holder.getConnections());
        });

        for (var connection : connections) {
            var pos1 = getNodePos(connection.getFirst(), 0.5);
            var pos2 = getNodePos(connection.getSecond(), 0.5);

            Gizmos.line(pos1, pos2, graphColor(graph.getId())).setAlwaysOnTop();
        }
    }

    public static Vec3 getNodePos(NodeHolder<BlockNode> holder, double sidedOffset) {
        Vec3 pos = Vec3.atCenterOf(holder.getPos().pos());
        if (holder.getPos().node() instanceof SidedBlockNode sided) {
            pos = pos.add(sided.getSide().getUnitVec3().scale(sidedOffset));
        }
        return pos;
    }

    private static int graphColor(long graphId) {
        return ColorUtils.hsba2Argb(graphId * Mth.HALF_PI / 10f, 1f, 0.75f, 1f);
    }

    private static int nodeColor(BlockNode node, GraphUniverse universe, float alpha) {
        return ColorUtils.hsba2Argb(universe.getNodeTypeIndex(node.getType().getId()) / (float) universe.getNodeTypeCount(), 1f, 1f, alpha);
    }
}
