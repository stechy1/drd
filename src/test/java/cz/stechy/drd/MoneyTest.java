package cz.stechy.drd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Test;

/**
 * Testovací třída pro otestování funkčnosti třídy {@link Money}
 */
public class MoneyTest {

    private Money money;

    @Before
    public void setUp() throws Exception {
        money = new Money();
    }

    @Test
    public void addTest1() throws Exception {
        final Money addedMoney = new Money(1, 15, 63);
        money.add(addedMoney);
        assertEquals("Chyba, peníze nebyly přidány správně.", addedMoney, money);
    }

    @Test
    public void subtractTest1() throws Exception {
        final Money subtractedMoney = new Money(1, 25, 52);
        money.setGold(2).setSilver(50).setCopper(95);
        money.subtract(subtractedMoney);
        assertSame("Chyba, zlaťáky se neodečetly správně.", 2-1, money.getGold());
        assertSame("Chyba, stříbrňáky se neodečetly správně.", 50-25, money.getSilver());
        assertSame("Chyba, měďáky se neodečetly správně.", 95-52, money.getCopper());
    }

    @Test
    public void setGoldTest1() throws Exception {
        final int gold = 10;
        money.setGold(gold);
        assertEquals("Chyba, počet nastavených zlaťáků nesedí.", gold, money.getGold());
    }

    @Test
    public void addGoldTest1() throws Exception {
        final int gold = 1;
        final int addition = 5;
        final int expectedGold = gold + addition;
        money.setGold(gold);
        money.addGold(addition);
        assertEquals("Chyba, počet zlaťáků po sečtení nesedí.", expectedGold, money.getGold());
    }

    @Test
    public void subtractGoldTest1() throws Exception {
        final int gold = 10;
        final int subtraction = 1;
        final int expectedGold = gold - subtraction;
        money.setGold(gold);
        money.subtractGold(subtraction);
        assertEquals("Chyba, počet zlaťáků po odečtení nesedí.", expectedGold, money.getGold());
    }

    @Test
    public void silverTest1() throws Exception {
        final int silver = 10;
        money.setSilver(silver);
        assertEquals("Chyba, počet nastavených stříbrňáků nesedí.", silver, money.getSilver());
    }

    @Test
    public void silverTest2() throws Exception {
        final int silver = 105;
        final int expectedSilver = 5;
        final int expectedGold = 1;
        money.setSilver(silver);
        assertEquals("Chyba, počet nastavených stříbrňáků nesedí", expectedSilver, money.getSilver());
        assertEquals("Chyba, počet nastavených zlaťáků nesedí", expectedGold, money.getGold());
    }

    @Test
    public void addSilverTest1() throws Exception {
        final int silver = 1;
        final int addition = 5;
        final int expectedSilver = silver + addition;
        money.setSilver(silver);
        money.addSilver(addition);
        assertEquals("Chyba, počet střábrňáků po sečtení nesedí.", expectedSilver, money.getSilver());
    }

    @Test
    public void addSilverTest2() throws Exception {
        final int silver = 15;
        final int addition = 95;
        final int expectedSilver = 10;
        money.setSilver(silver);
        money.addSilver(addition);
        assertEquals("Chyba, počet střábrňáků po sečtení nesedí.", expectedSilver, money.getSilver());
    }

    @Test
    public void subtractSilverTest1() throws Exception {
        final int silver = 3;
        final int subtraction = 1;
        final int expectedSilver = silver - subtraction;
        money.setSilver(silver);
        money.subtractSilver(subtraction);
        assertEquals("Chyba, počet stříbrňáků po odečtení nesedí.", expectedSilver,
            money.getSilver());
    }

    @Test
    public void subtractSilverTest2() throws Exception {
        final int silver = 105;
        final int subtraction = 10;
        final int expectedSilver = 95;
        money.setSilver(silver);
        assertEquals("Chyba, počet nastavených zlaťáků nesedí", silver / 100, money.getGold());
        money.subtractSilver(subtraction);
        assertEquals("Chyba, počet stříbrňáků po odečtení nesedí.", expectedSilver,
            money.getSilver());
        assertEquals("Chyba, počet nastavených zlaťáků nesedí", expectedSilver / 100, money.getGold());
    }

    @Test
    public void copperTest1() throws Exception {
        final int copper = 10;
        money.setCopper(copper);
        assertEquals("Chyba, počet nastavených měďáků nesedí.", copper, money.getCopper());
    }

    @Test
    public void addCopperTest1() throws Exception {
        final int copper = 1;
        final int addition = 5;
        final int expectedCopper = copper + addition;
        money.setCopper(copper);
        money.addCopper(addition);
        assertEquals("Chyba, počet měďáků po sečtení nesedí.", expectedCopper, money.getCopper());
    }

    @Test
    public void addCopperTest2() throws Exception {
        final int copper = 15;
        final int addition = 95;
        final int expectedCopper = 10;
        money.setCopper(copper);
        money.addCopper(addition);
        assertEquals("Chyba, počet měďáků po sečtení nesedí.", expectedCopper, money.getCopper());
    }

    @Test
    public void subtractCopperTest1() throws Exception {
        final int copper = 3;
        final int subtraction = 1;
        final int expectedCopper = copper - subtraction;
        money.setCopper(copper);
        money.subtractCopper(subtraction);
        assertEquals("Chyba, počet stříbrňáků po odečtení nesedí.", expectedCopper,
            money.getCopper());
    }

    @Test
    public void subtractCopperTest2() throws Exception {
        final int copper = 105;
        final int subtraction = 10;
        final int expectedCopper = 95;
        money.setCopper(copper);
        assertEquals("Chyba, počet nastavených stříbrňáků nesedí", copper / 100, money.getSilver());
        money.subtractCopper(subtraction);
        assertEquals("Chyba, počet měďáků po odečtení nesedí.", expectedCopper,
            money.getCopper());
        assertEquals("Chyba, počet nastavených stříbrňáků nesedí", expectedCopper / 100, money.getSilver());
    }

}
