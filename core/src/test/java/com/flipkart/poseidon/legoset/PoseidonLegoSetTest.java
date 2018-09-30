/*
 * Copyright 2016 Flipkart Internet, pvt ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.flipkart.poseidon.legoset;

import com.flipkart.poseidon.helper.CallableNameHelper;
import com.flipkart.poseidon.model.exception.MissingInformationException;
import flipkart.lego.api.exceptions.ElementNotFoundException;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by shrey.garg on 19/05/16.
 */
public class PoseidonLegoSetTest {
    public static final String NAMED_DS_NAME = "ThisIsNamed";
    public static final String PROPER_DS_NAME = "ThisIsProper";
    public static final String PROPER_INJECTABLE_DS_NAME = "ThisIsProperInjectable";
    public static final String QUALIFIER_INJECTABLE_DS_NAME = "ThisIsQualifierInjectable";

    ApplicationContext context = mock(ApplicationContext.class);

    @Test
    public void testVariousDataSourceStyles() throws Exception {
        when(context.getBean(any(Class.class))).thenReturn("injected");
        TestLegoSet legoSet = new TestLegoSet();
        legoSet.setContext(context);
        legoSet.init();

        legoSet.getDataSource(CallableNameHelper.versionedName(PROPER_DS_NAME, "4.1.6"), null);
        legoSet.getDataSource(CallableNameHelper.versionedName(PROPER_INJECTABLE_DS_NAME, "4.1.6"), null);

        try {
            legoSet.getDataSource(NAMED_DS_NAME, null);
            fail();
        } catch (Exception e) {
            assertEquals(ElementNotFoundException.class, e.getClass());
        }
    }

    @Test(expected = NoSuchBeanDefinitionException.class)
    public void testInjectableDataSourceUnmetDependency() throws Throwable {
        when(context.getBean(any(Class.class))).thenThrow(NoSuchBeanDefinitionException.class);
        TestLegoSet legoSet = new TestLegoSet();
        legoSet.setContext(context);
        legoSet.init();

        try {
            legoSet.getDataSource(CallableNameHelper.versionedName(PROPER_INJECTABLE_DS_NAME, "4.1.6"), null);
        } catch (Exception e) {
            throw e.getCause();
        }
    }

    @Test(expected = NoUniqueBeanDefinitionException.class)
    public void testInjectableDataSourceMultipleDependency() throws Throwable {
        when(context.getBean(any(Class.class))).thenThrow(NoUniqueBeanDefinitionException.class);
        TestLegoSet legoSet = new TestLegoSet();
        legoSet.setContext(context);
        legoSet.init();

        try {
            legoSet.getDataSource(CallableNameHelper.versionedName(PROPER_INJECTABLE_DS_NAME, "4.1.6"), null);
        } catch (Exception e) {
            throw e.getCause();
        }
    }

    @Test(expected = MissingInformationException.class)
    public void testInjectableDataSourceNullDependency() throws Throwable {
        when(context.getBean(any(Class.class))).thenReturn(null);
        TestLegoSet legoSet = new TestLegoSet();
        legoSet.setContext(context);
        legoSet.init();

        try {
            legoSet.getDataSource(CallableNameHelper.versionedName(PROPER_INJECTABLE_DS_NAME, "4.1.6"), null);
        } catch (Exception e) {
            throw e.getCause();
        }
    }

    @Test(expected = MissingInformationException.class)
    public void testQualifiedInjectableDataSourceWithoutQualifiedBinding() throws Throwable {
        when(context.getBean(any(Class.class))).thenReturn("will fail");
        TestLegoSet legoSet = new TestLegoSet();
        legoSet.setContext(context);
        legoSet.init();

        try {
            legoSet.getDataSource(CallableNameHelper.versionedName(QUALIFIER_INJECTABLE_DS_NAME, "4.1.6"), null);
        } catch (Exception e) {
            throw e.getCause();
        }
    }

    @Test
    public void testQualifiedInjectableDataSource() throws Throwable {
        when(context.getBean(anyString(), any(Class.class))).thenReturn("will not fail");
        TestLegoSet legoSet = new TestLegoSet();
        legoSet.setContext(context);
        legoSet.init();

        legoSet.getDataSource(CallableNameHelper.versionedName(QUALIFIER_INJECTABLE_DS_NAME, "4.1.6"), null);
    }

    private static class TestLegoSet extends PoseidonLegoSet {
        @Override
        public List<String> getPackagesToScan() {
            return Arrays.asList("com.flipkart.poseidon.legoset.test");
        }
    }
}