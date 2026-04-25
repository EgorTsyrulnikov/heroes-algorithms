package programs;

/**
 * Простой калькулятор урона для Heroes.
 * Формула приближена к оригинальной механике HoMM III.
 *
 * Базовый урон = количество_юнитов * средний_урон_юнита
 * Модификатор атаки/защиты:
 *   если атака > защита: +5% за каждую единицу разницы (макс +300%)
 *   если защита > атака: -2.5% за каждую единицу разницы (макс -87.5%)
 */
public class DamageCalculator {

    /**
     * Рассчитывает урон стака атакующих юнитов по защищающемуся стаку.
     *
     * @param unitCount      количество юнитов в атакующем стаке
     * @param minDamage      минимальный урон одного юнита
     * @param maxDamage      максимальный урон одного юнита
     * @param attackerAttack значение атаки атакующего
     * @param defenderDefense значение защиты защищающегося
     * @return массив [minTotalDamage, maxTotalDamage]
     */
    public static double[] calculateDamage(int unitCount, int minDamage, int maxDamage,
                                           int attackerAttack, int defenderDefense) {
        int diff = attackerAttack - defenderDefense;
        double modifier;

        if (diff > 0) {
            // Бонус за превосходство атаки: +5% за единицу, макс +300%
            modifier = 1.0 + Math.min(diff * 0.05, 3.0);
        } else if (diff < 0) {
            // Штраф за превосходство защиты: -2.5% за единицу, макс -87.5%
            modifier = Math.max(1.0 + diff * 0.025, 0.125);
        } else {
            modifier = 1.0;
        }

        double minTotal = unitCount * minDamage * modifier;
        double maxTotal = unitCount * maxDamage * modifier;

        return new double[]{Math.round(minTotal), Math.round(maxTotal)};
    }

    /**
     * Считает, сколько юнитов будет убито при данном уроне.
     *
     * @param damage  нанесённый урон
     * @param unitHP  здоровье одного юнита в стаке
     * @return количество убитых юнитов
     */
    public static int calculateKills(double damage, int unitHP) {
        return (int) (damage / unitHP);
    }

    // Демо
    public static void main(String[] args) {
        System.out.println("=== Heroes Damage Calculator ===\n");

        // Пример: 10 Архангелов (атака 30, урон 50-50) vs существо с защитой 20
        int count = 10;
        int minDmg = 50;
        int maxDmg = 50;
        int attack = 30;
        int defense = 20;
        int targetHP = 200;

        double[] dmg = calculateDamage(count, minDmg, maxDmg, attack, defense);
        System.out.printf("Атакующий: %d юнитов, урон %d-%d, атака %d%n", count, minDmg, maxDmg, attack);
        System.out.printf("Защитник:  защита %d, HP %d%n", defense, targetHP);
        System.out.printf("Урон:      %.0f - %.0f%n", dmg[0], dmg[1]);
        System.out.printf("Убито:     %d - %d юнитов%n",
                calculateKills(dmg[0], targetHP),
                calculateKills(dmg[1], targetHP));

        System.out.println("\n--- Таблица урона по разнице атака-защита ---");
        System.out.printf("%-10s %-12s %-10s%n", "Разница", "Модификатор", "Урон (10×50)");
        for (int d = -20; d <= 20; d += 5) {
            double[] result = calculateDamage(10, 50, 50, 20 + d, 20);
            System.out.printf("%-10d %-12.0f%% %-10.0f%n", d, (result[0] / 500.0) * 100, result[0]);
        }
    }
}
