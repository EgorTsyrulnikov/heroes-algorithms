package programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.GeneratePreset;

import java.util.*;

public class GeneratePresetImpl implements GeneratePreset {

    @Override
    public Army generate(List<Unit> unitList, int maxPoints) {
        Army computerArmy = new Army();
        List<Unit> selectedUnits = new ArrayList<>();

        if (unitList == null || unitList.isEmpty()) {
            computerArmy.setUnits(selectedUnits);
            return computerArmy;
        }

        // Сортировка по эффективности (атака + здоровье) / стоимость
        List<Unit> sortedUnits = new ArrayList<>(unitList);
        sortedUnits.sort((u1, u2) -> {
            double eff1 = ((double) u1.getBaseAttack() / u1.getCost()) + ((double) u1.getHealth() / u1.getCost());
            double eff2 = ((double) u2.getBaseAttack() / u2.getCost()) + ((double) u2.getHealth() / u2.getCost());
            return Double.compare(eff2, eff1);
        });

        int currentPoints = 0;
        Map<String, Integer> unitCounts = new HashMap<>();
        Set<String> occupiedCoords = new HashSet<>();
        Random random = new Random();

        boolean unitAdded = true;
        while (unitAdded && currentPoints < maxPoints) {
            unitAdded = false;

            for (Unit unitTemplate : sortedUnits) {
                int count = unitCounts.getOrDefault(unitTemplate.getUnitType(), 0);

                if (count < 11 && currentPoints + unitTemplate.getCost() <= maxPoints) {
                    // ИЩЕМ КООРДИНАТЫ СЛЕВА (0..2)
                    int coordX = -1;
                    int coordY = -1;
                    boolean positionFound = false;

                    for (int i = 0; i < 100; i++) {
                        // Исправлено: компьютер слева, координаты 0, 1 или 2
                        int tryX = random.nextInt(3);
                        int tryY = random.nextInt(21); // Высота поля 21

                        String key = tryX + "," + tryY;
                        if (!occupiedCoords.contains(key)) {
                            coordX = tryX;
                            coordY = tryY;
                            occupiedCoords.add(key);
                            positionFound = true;
                            break;
                        }
                    }

                    if (positionFound) {
                        Unit newUnit = new Unit(
                                unitTemplate.getUnitType() + " " + (count + 1),
                                unitTemplate.getUnitType(),
                                unitTemplate.getHealth(),
                                unitTemplate.getBaseAttack(),
                                unitTemplate.getCost(),
                                unitTemplate.getAttackType(),
                                unitTemplate.getAttackBonuses(),
                                unitTemplate.getDefenceBonuses(),
                                coordX,
                                coordY
                        );
                        newUnit.setName(unitTemplate.getUnitType() + " " + (count + 1));

                        selectedUnits.add(newUnit);
                        unitCounts.put(unitTemplate.getUnitType(), count + 1);
                        currentPoints += unitTemplate.getCost();
                        unitAdded = true;
                    }
                }
            }
        }

        computerArmy.setUnits(selectedUnits);
        computerArmy.setPoints(currentPoints);
        return computerArmy;
    }
}
