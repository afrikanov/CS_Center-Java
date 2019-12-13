package ru.compscicenter.java2019.collections;

import org.junit.Before;
import org.junit.Test;
import ru.compscicenter.java2019.collections.MultiSet;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.*;

import static org.fest.assertions.api.Assertions.*;

public class MultiSetTest {

    private Class<?> multiSetClass;

    @Before
    public void getInstance() throws ClassNotFoundException, IOException {
        Properties prop = new Properties();
        prop.load(MultiSetTest.class.getClassLoader().getResourceAsStream("build.properties"));
        Locale.setDefault(Locale.US);
        multiSetClass = Class.forName(prop.getProperty("IMPLEMENTATION_CLASS"));
    }

    /*
     * This is test example
     */
    @Test
    public void newMultiSetMustBeEmpty() throws Exception {
        assertThat(newMultiSet()).isEmpty();
        assertThat(newMultiSet()).hasSize(0);
    }

    /*
     * This is constructor without parameters for your MultiSet implementation.
     */
    private <E> MultiSet<E> newMultiSet() throws Exception {
        Constructor<?> constructor = getNoArgConstructor();
        constructor.setAccessible(true);
        return (MultiSet<E>) constructor.newInstance();
    }

    /*
     * This is constructor with Collection parameter for your MultiSet implementation.
     */
    private <E> MultiSet<E> newMultiSet(Collection c) throws Exception {
        Constructor<?> constructor = getCollectionConstructor();
        constructor.setAccessible(true);
        return (MultiSet<E>) constructor.newInstance(c);
    }

    private Constructor<?> getNoArgConstructor() throws Exception {
        return multiSetClass.getDeclaredConstructor();
    }

    private Constructor<?> getCollectionConstructor() throws Exception {
        return multiSetClass.getDeclaredConstructor(Collection.class);
    }

}
