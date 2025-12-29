package programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.Edge;
import com.battle.heroes.army.programs.UnitTargetPathFinder;

import java.util.*;

public class UnitTargetPathFinderImpl implements UnitTargetPathFinder {
    private static final int WIDTH = 27;
    private static final int HEIGHT = 21;

    @Override
    public List<Edge> getTargetPath(Unit attackUnit, Unit targetUnit, List<Unit> existingUnitList) {
        if (attackUnit == null || targetUnit == null) return new ArrayList<>();

        int startX = attackUnit.getxCoordinate();
        int startY = attackUnit.getyCoordinate();
        int targetX = targetUnit.getxCoordinate();
        int targetY = targetUnit.getyCoordinate();

        // Препятствия
        Set<String> obstacles = new HashSet<>();
        for (Unit u : existingUnitList) {
            if (u.isAlive() && u != attackUnit && u != targetUnit) {
                obstacles.add(u.getxCoordinate() + "," + u.getyCoordinate());
            }
        }

        // BFS
        Queue<Edge> queue = new LinkedList<>();
        queue.add(new Edge(startX, startY));

        Map<String, Edge> cameFrom = new HashMap<>();
        cameFrom.put(startX + "," + startY, null);

        // Направления (включая диагонали)
        int[][] directions = {
                {-1, 0}, {1, 0}, {0, -1}, {0, 1},
                {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
        };

        Edge endNode = null;

        while (!queue.isEmpty()) {
            Edge current = queue.poll();

            if (current.getX() == targetX && current.getY() == targetY) {
                endNode = current;
                break;
            }

            for (int[] dir : directions) {
                int newX = current.getX() + dir[0];
                int newY = current.getY() + dir[1];

                if (newX >= 0 && newX < WIDTH && newY >= 0 && newY < HEIGHT) {
                    String key = newX + "," + newY;
                    if (!cameFrom.containsKey(key) && !obstacles.contains(key)) {
                        Edge next = new Edge(newX, newY);
                        cameFrom.put(key, current);
                        queue.add(next);
                    }
                }
            }
        }

        if (endNode == null) return new ArrayList<>();

        // Восстановление пути
        List<Edge> path = new ArrayList<>();
        Edge curr = endNode;
        while (curr != null) {
            path.add(curr);
            curr = cameFrom.get(curr.getX() + "," + curr.getY());
        }
        Collections.reverse(path);

        // ВАЖНО: Удаляем первую точку (где стоит сам юнит), чтобы он начал движение
        if (!path.isEmpty() && path.get(0).getX() == startX && path.get(0).getY() == startY) {
            path.remove(0);
        }

        return path;
    }
}
