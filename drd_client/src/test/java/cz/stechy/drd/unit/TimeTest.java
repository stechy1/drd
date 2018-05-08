package cz.stechy.drd.unit;

import static org.junit.Assert.assertEquals;

import cz.stechy.drd.model.DrDTime;
import org.junit.Before;
import org.junit.Test;

public class TimeTest {

    private DrDTime time;

    @Before
    public void setUp() throws Exception {
        time = new DrDTime();
    }

    // region Constructor testing

    // Test prázdného konstruktoru
    @Test
    public void constructorTest1() {
        final DrDTime time = new DrDTime();
        assertEquals("Chyba, instance času obsahuje špatný údaj.", DrDTime.DEFAULT_VALUE,
            time.getRaw());
    }

    // Test kopy konstruktoru
    @Test
    public void constructorTest2() {
        final int day = 6;
        time.setDay(day);
        final DrDTime copy = new DrDTime(time);
        assertEquals("Chyba, kopy konstruktor špatně zkopíroval hodnoty.", time, copy);
    }

    @Test
    public void constructorTest3() {
        final int expectedCycle = 6;
        final int expectedInning = 3;
        time.setCycle(expectedCycle);
        time.setInning(expectedInning);
        final int expectedRaw = time.getRaw();
        final DrDTime testTime = new DrDTime(expectedRaw);
        assertEquals("Chyba, konstruktor s parametrem surového času nefunguje.", expectedRaw,
            testTime.getRaw());
    }

    // endregion

    // region Value Set testing

    @Test
    public void setYearTest1() {
        final int year = 10;
        time.setYear(year);
        assertEquals("Chyba, počet nastavených let nesedí.", year, time.getYear());
    }

    @Test
    public void setMonthTest1() {
        final int month = 5;
        time.setMonth(month);
        assertEquals("Chyba, počet nastavených měsíců nesedí.", month, time.getMonth());
    }

    @Test
    public void setMonthTest2() {
        final int month = 15;
        final int expectedYear = 1;
        final int expectedMonth = 3;
        time.setMonth(month);
        assertEquals("Chyba, počet nasvatených let nesedí.", expectedYear, time.getYear());
        assertEquals("Chyba, počet nastavených měsíců nesedí.", expectedMonth, time.getMonth());
    }

    @Test
    public void setDayTest1() {
        final int day = 27;
        time.setDay(day);
        assertEquals("Chyba, počet nastavených dní nesedí.", day, time.getDay());
    }

    @Test
    public void setDayTest2() {
        final int day = 45;
        final int expectedMonth = 1;
        final int expectedDay = 15;
        time.setDay(day);
        assertEquals("Chyba, počet nastavených měsíců nesedí.", expectedMonth, time.getMonth());
        assertEquals("Chyba, počet nastavených dní nesedí.", expectedDay, time.getDay());
    }

    @Test
    public void setDayTest3() {
        final int day = 405;
        final int expectedYear = 1;
        final int expectedMonth = 1;
        final int expectedDay = 15;
        time.setDay(day);
        assertEquals("Chyba, počet nasvatených let nesedí.", expectedYear, time.getYear());
        assertEquals("Chyba, počet nastavených měsíců nesedí.", expectedMonth, time.getMonth());
        assertEquals("Chyba, počet nastavených dní nesedí.", expectedDay, time.getDay());
    }

    @Test
    public void setInningTest1() {
        final int inning = 3;
        time.setInning(inning);
        assertEquals("Chyba, počet nastavených směn nesedí.", inning, time.getInning());
    }

    @Test
    public void setInningTest2() {
        final int inning = 30;
        final int expectedDay = 1;
        final int expectedInning = 6;
        time.setInning(inning);
        assertEquals("Chyba, počet nastavených dní nesedí.", expectedDay, time.getDay());
        assertEquals("Chyba, počet nastavených směn nesedí.", expectedInning, time.getInning());
    }

    @Test
    public void setInningTest3() {
        final int inning = 762;
        final int expectedMonth = 1;
        final int expectedDay = 1;
        final int expectedInning = 18;
        time.setInning(inning);
        assertEquals("Chyba, počet nastavených měsíců nesedí.", expectedMonth, time.getMonth());
        assertEquals("Chyba, počet nastavených dní nesedí.", expectedDay, time.getDay());
        assertEquals("Chyba, počet nastavených směn nesedí.", expectedInning, time.getInning());
    }

    @Test
    public void setInningTest4() {
        final int inning = 9524;
        final int expectedYear = 1;
        final int expectedMonth = 1;
        final int expectedDay = 6;
        final int expectedInning = 20;
        time.setInning(inning);
        assertEquals("Chyba, počet nasvatených let nesedí.", expectedYear, time.getYear());
        assertEquals("Chyba, počet nastavených měsíců nesedí.", expectedMonth, time.getMonth());
        assertEquals("Chyba, počet nastavených dní nesedí.", expectedDay, time.getDay());
        assertEquals("Chyba, počet nastavených směn nesedí.", expectedInning, time.getInning());
    }

    @Test
    public void setCycleTest1() {
        final int cycle = 5;
        time.setCycle(cycle);
        assertEquals("Chyba, počet nastavených kol nesedí.", cycle, time.getCycle());
    }

    @Test
    public void setCycleTest2() {
        final int cycle = 15;
        final int expectedCycle = 5;
        final int expectedInning = 1;
        time.setCycle(cycle);
        assertEquals("Chyba, počet nastavených kol nesedí.", expectedCycle, time.getCycle());
        assertEquals("Chyba, počet nastavených směn nesedí.", expectedInning, time.getInning());
    }

    @Test
    public void setCycleTest3() {
        final int cycle = 365;
        final int expectedDay = 1;
        final int expectedInning = 12;
        final int expectedCycle = 5;
        time.setCycle(cycle);
        assertEquals("Chyba, počet nastavených dní nesedí.", expectedDay, time.getDay());
        assertEquals("Chyba, počet nastavených směn nesedí.", expectedInning, time.getInning());
        assertEquals("Chyba, počet nastavených kol nesedí.", expectedCycle, time.getCycle());
    }

    @Test
    public void setCycleTest4() {
        final int cycle = 9524;
        final int expectedMonth = 1;
        final int expectedDay = 9;
        final int expectedInning = 16;
        final int expectedCycle = 4;
        time.setCycle(cycle);
        assertEquals("Chyba, počet nastavených měsíců nesedí.", expectedMonth, time.getMonth());
        assertEquals("Chyba, počet nastavených dní nesedí.", expectedDay, time.getDay());
        assertEquals("Chyba, počet nastavených směn nesedí.", expectedInning, time.getInning());
        assertEquals("Chyba, počet nastavených kol nesedí.", expectedCycle, time.getCycle());
    }

    @Test
    public void setCycleTest5() {
        final int cycle = 97485;
        final int expectedYear = 1;
        final int expectedMonth = 1;
        final int expectedDay = 16;
        final int expectedInning = 4;
        final int expectedCycle = 5;
        time.setCycle(cycle);
        assertEquals("Chyba, počet nasvatených let nesedí.", expectedYear, time.getYear());
        assertEquals("Chyba, počet nastavených měsíců nesedí.", expectedMonth, time.getMonth());
        assertEquals("Chyba, počet nastavených dní nesedí.", expectedDay, time.getDay());
        assertEquals("Chyba, počet nastavených směn nesedí.", expectedInning, time.getInning());
        assertEquals("Chyba, počet nastavených kol nesedí.", expectedCycle, time.getCycle());
    }

    // endregion

    // region Value Add testing

    @Test
    public void addTest() {
        final DrDTime addedTime = new DrDTime(1, 3, 15, 6, 9);
        time.add(addedTime);
        assertEquals("Chyba, čas se špatně přičetl.", addedTime, time);
    }

    @Test
    public void addCycleTest1() {
        final int defaultCycle = 5;
        final int addedCycle = 3;
        time.setCycle(defaultCycle);
        time.addCycle(addedCycle);
        assertEquals("Chyba, cykly se špatně sečetly.", defaultCycle + addedCycle, time.getCycle());
    }

    @Test
    public void addCycleTest2() {
        final int defaultCycle = 5;
        final int addedCycle = 8;
        final int expectedCycle = 3;
        final int expectedInning = 1;
        time.setCycle(defaultCycle);
        time.addCycle(addedCycle);
        assertEquals("Chyba, cykly se špatně sečetly.", expectedInning, time.getInning());
        assertEquals("Chyba, cykly se špatně sečetly.", expectedCycle, time.getCycle());
    }

    // endregion

    // region Value Subtract testing

    @Test
    public void subtractTest() {
        final DrDTime subtractedTime = new DrDTime();
        final int subtractedCycle = 6;
        final int defaultInning = 1;
        final int expectedCycle = 4;
        final int expectedInning = 0;
        subtractedTime.setCycle(subtractedCycle);
        time.setInning(defaultInning);
        time.subtract(subtractedTime);
        assertEquals("Chyba, čas se špatně odečetl.", expectedCycle, time.getCycle());
        assertEquals("Chyba, čas se špatně odečetl.", expectedInning, time.getInning());
    }

    // endregion

    // region Value Multiply testing

    // endregion
}
