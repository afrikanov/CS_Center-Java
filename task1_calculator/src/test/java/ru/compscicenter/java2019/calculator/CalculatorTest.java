package ru.compscicenter.java2019.calculator;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.*;

public class CalculatorTest {

    public static final double DELTA = 1E-8;
    Calculator calculator;

    @Before
    public void getInstance() throws ClassNotFoundException, IOException, IllegalAccessException, InstantiationException {
        Properties prop = new Properties();
        prop.load(CalculatorTest.class.getClassLoader().getResourceAsStream("build.properties"));
        Locale.setDefault(Locale.US);
        calculator = (Calculator) CalculatorTest.class.getClassLoader().loadClass(prop.getProperty("IMPLEMENTATION_CLASS")).newInstance();
    }

    @Test
    public void testSum() throws Exception {
        assertEquals(2 + 2, calculator.calculate("4"), DELTA);
    }
}