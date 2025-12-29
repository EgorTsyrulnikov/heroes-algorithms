package programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.SuitableForAttackUnitsFinder;

import java.util.*;

public class SuitableForAttackUnitsFinderImpl implements SuitableForAttackUnitsFinder {

    @Override
    public List<Unit> getSuitableUnits(List<List<Unit>> unitsByRow, boolean isLeftArmyTarget) {
        List<Unit> suitableUnits = new ArrayList<>();

        // Собираем всех юнитов (игрока или компьютера, в зависимости от isLeftArmyTarget) в один список
        List<Unit> allUnits = new ArrayList<>();
        for (List<Unit> row : unitsByRow) {
            if (row != null) {
                allUnits.addAll(row);
            }
        }

        if (allUnits.isEmpty()) {
            return suitableUnits;
        }

        for (Unit unit : allUnits) {
            if (unit == null || !unit.isAlive()) continue;

            boolean isBlocked = false;
            for (Unit other : allUnits) {
                if (other == null || !other.isAlive() || unit == other) continue;

                // Проверяем блокировку только если юниты на одной линии Y
                if (other.getyCoordinate() == unit.getyCoordinate()) {

                    if (isLeftArmyTarget) {
                        // Цель - ЛЕВАЯ армия (Компьютер, X=0..2).
                        // Атакующий справа (Игрок).
                        // Юнит закрыт, если СПРАВА от него (X больше) есть другой юнит.
                        if (other.getxCoordinate() > unit.getxCoordinate()) {
                            isBlocked = true;
                            break;
                        }
                    } else {
                        // Цель - ПРАВАЯ армия (Игрок, X=24..26).
                        // Атакующий слева (Компьютер).
                        // Юнит закрыт, если СЛЕВА от него (X меньше) есть другой юнит.
                        if (other.getxCoordinate() < unit.getxCoordinate()) {
                            isBlocked = true;
                            break;
                        }
                    }
                }
            }

            if (!isBlocked) {
                suitableUnits.add(unit);
            }
        }

        return suitableUnits;
    }
}
