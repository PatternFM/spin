package fm.pattern.spin.junit;

public final class TestExecutionMonitor {

    private static Integer testClassesToRun = 0;
    private static Integer testClassesExecuted = 0;

    private TestExecutionMonitor() {

    }

    static void reset() {
        testClassesToRun = 0;
        testClassesExecuted = 0;
    }

    public static void testStarted() {
        testClassesToRun += 1;
    }

    public static void testCompleted() {
        testClassesExecuted += 1;
    }

    public static Integer getTestClassesToRun() {
        return testClassesToRun;
    }

    public static Integer getTestClassesExecuted() {
        return testClassesExecuted;
    }

    public static boolean allTestsHaveRun() {
        return testClassesToRun == testClassesExecuted;
    }

}
