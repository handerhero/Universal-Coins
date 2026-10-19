package aspadien.universalcoins.util;

public final class EmissionThrottle {
    private static final double FREE_THRESHOLD = 1_000_000.0;   // ниже — throttle не действует
    private static final double AGGRESSION_SCALE = 1_000_000_000.0; // масштаб "агрессивной" зоны
    private static final double POWER = 2.5;                    // резкость нарастания давления

    private EmissionThrottle() {}

    public static double multiplier(long referenceBalance) {
        if (referenceBalance <= FREE_THRESHOLD) return 1.0;
        double ratio = (referenceBalance - FREE_THRESHOLD) / AGGRESSION_SCALE;
        return 1.0 / (1.0 + Math.pow(ratio, POWER));
    }

    public static int apply(int rawAmount, long referenceBalance) {
        int throttled = (int) Math.round(rawAmount * multiplier(referenceBalance));
        return Math.max(1, throttled);
    }
}