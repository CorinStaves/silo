//package de.tum.bgu.msm.models.javascript;
//
//import de.tum.bgu.msm.data.person.Gender;
//import de.tum.bgu.msm.models.demography.death.DefaultDeathStrategy;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//
//import java.io.InputStreamReader;
//import java.io.Reader;
//
//public class DeathTest {
//    private DefaultDeathStrategy calculator;
//
//    @Before
//    public void setup() {
//        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("DeathProbabilityCalc"));
//        calculator = new DefaultDeathStrategy(reader);
//    }
//
//    @Test
//    public void testModelOne() {
//        Assert.assertEquals(0.00068366, calculator.calculateDeathProbability(31, Gender.MALE), 0.);
//    }
//
//    @Test
//    public void testModelTwo() {
//        Assert.assertEquals(0.410106, calculator.calculateDeathProbability(200, Gender.MALE), 0.);
//    }
//
//    @Test(expected = RuntimeException.class)
//    public void testModelFailures() {
//        calculator.calculateDeathProbability(-2, Gender.MALE);
//    }
//
//}
