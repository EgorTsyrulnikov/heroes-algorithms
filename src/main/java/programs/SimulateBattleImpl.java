package programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.PrintBattleLog;
import com.battle.heroes.army.programs.SimulateBattle;

import java.util.*;

public class SimulateBattleImpl implements SimulateBattle {
    private PrintBattleLog printBattleLog;

    @Override
    public void simulate(Army playerArmy, Army computerArmy) throws InterruptedException {
        // Проверка входных данных
        if (playerArmy == null || computerArmy == null) {
            return;
        }

        // Создаем общий список всех юнитов
        List<Unit> allUnits = new ArrayList<>();
        if (playerArmy.getUnits() != null) {
            allUnits.addAll(playerArmy.getUnits());
        }
        if (computerArmy.getUnits() != null) {
            allUnits.addAll(computerArmy.getUnits());
        }

        // Основной цикл боя
        while (true) {
            // 1. Получаем актуальный список живых юнитов
            List<Unit> aliveUnits = getAliveUnits(allUnits);

            // 2. Проверяем условие окончания боя
            boolean playerHasAlive = hasAliveUnitsFromArmy(aliveUnits, playerArmy);
            boolean computerHasAlive = hasAliveUnitsFromArmy(aliveUnits, computerArmy);

            if (!playerHasAlive || !computerHasAlive) {
                break; // Бой окончен
            }

            // 3. Сортируем живых юнитов по убыванию атаки
            aliveUnits.sort((u1, u2) -> {
                int attackDiff = u2.getBaseAttack() - u1.getBaseAttack();
                if (attackDiff != 0) return attackDiff;
                // При равенстве атаки сортируем по убыванию здоровья
                return u2.getHealth() - u1.getHealth();
            });

            // 4. Обрабатываем ходы юнитов в текущем раунде
            for (Unit unit : aliveUnits) {
                // Проверяем, жив ли ещё юнит (мог умереть в этом раунде)
                if (!unit.isAlive()) {
                    continue;
                }

                // Быстрая проверка: не закончился ли бой
                if (!hasAliveUnitsFromArmy(getAliveUnits(allUnits), playerArmy) ||
                        !hasAliveUnitsFromArmy(getAliveUnits(allUnits), computerArmy)) {
                    break; // Выходим из цикла ходов, бой окончен
                }

                // Выполняем ход юнита
                performUnitTurn(unit);

                // Короткая пауза для наглядности (если есть логгер)
                if (printBattleLog != null) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw e;
                    }
                }

                // Проверяем прерывание потока
                if (Thread.interrupted()) {
                    throw new InterruptedException("Бой прерван");
                }
            }

            // Пауза между раундами
            if (printBattleLog != null) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw e;
                }
            }
        }

        // Вывод результатов боя
        printBattleResults(playerArmy, computerArmy, allUnits);
    }

    private void performUnitTurn(Unit unit) {
        if (unit == null || unit.getProgram() == null) {
            return;
        }

        try {
            // Юнит пытается атаковать через свою программу
            Unit target = unit.getProgram().attack();

            if (target != null && printBattleLog != null) {
                // Логируем атаку
                printBattleLog.printBattleLog(unit, target);
            }
        } catch (Exception e) {
            // Логируем ошибку, но не прерываем симуляцию боя
            System.err.println("Ошибка при выполнении хода юнита " +
                    (unit != null ? unit.getName() : "null") + ": " + e.getMessage());
        }
    }

    private List<Unit> getAliveUnits(List<Unit> allUnits) {
        List<Unit> aliveUnits = new ArrayList<>();
        for (Unit unit : allUnits) {
            if (unit != null && unit.isAlive()) {
                aliveUnits.add(unit);
            }
        }
        return aliveUnits;
    }

    private boolean hasAliveUnitsFromArmy(List<Unit> units, Army army) {
        if (army == null || army.getUnits() == null) {
            return false;
        }

        Set<Unit> armySet = new HashSet<>(army.getUnits());
        for (Unit unit : units) {
            if (unit != null && unit.isAlive() && armySet.contains(unit)) {
                return true;
            }
        }
        return false;
    }

    private void printBattleResults(Army playerArmy, Army computerArmy, List<Unit> allUnits) {
        int playerAlive = countAliveUnits(playerArmy != null ? playerArmy.getUnits() : null);
        int computerAlive = countAliveUnits(computerArmy != null ? computerArmy.getUnits() : null);

        System.out.println("\n" + "=".repeat(50));
        System.out.println("БИТВА ЗАВЕРШЕНА!");
        System.out.println("=".repeat(50));

        if (playerAlive == 0 && computerAlive == 0) {
            System.out.println("РЕЗУЛЬТАТ: НИЧЬЯ!");
            System.out.println("Все юниты обеих армий погибли.");
        } else if (playerAlive > 0) {
            System.out.println("РЕЗУЛЬТАТ: ПОБЕДА ИГРОКА!");
            System.out.println("У игрока осталось юнитов: " + playerAlive);
        } else {
            System.out.println("РЕЗУЛЬТАТ: ПОБЕДА КОМПЬЮТЕРА!");
            System.out.println("У компьютера осталось юнитов: " + computerAlive);
        }
    }

    private int countAliveUnits(List<Unit> units) {
        if (units == null) return 0;

        int count = 0;
        for (Unit unit : units) {
            if (unit != null && unit.isAlive()) {
                count++;
            }
        }
        return count;
    }
}