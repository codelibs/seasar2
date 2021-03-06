/*
 * Copyright 2004-2013 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.extension.component.impl;

import org.seasar.extension.component.ComponentInvoker;
import org.seasar.framework.container.ComponentCreator;
import org.seasar.framework.container.ComponentDef;
import org.seasar.framework.container.hotdeploy.HotdeployBehavior;
import org.seasar.framework.container.impl.S2ContainerBehavior;
import org.seasar.framework.convention.impl.NamingConventionImpl;
import org.seasar.framework.unit.S2FrameworkTestCase;
import org.seasar.framework.util.ClassUtil;

/**
 * @author higa
 * 
 */
public class ComponentInvokerCreatorTest extends S2FrameworkTestCase {

    private ClassLoader originalLoader;

    private HotdeployBehavior ondemand;

    /**
     * @throws Exception
     */
    public void testCoolDeploy() throws Exception {
        ComponentInvokerCreator creator = new ComponentInvokerCreator();
        ComponentDef cd = creator.createComponentDef(ComponentInvoker.class);
        assertNotNull(cd);
        assertEquals(ComponentInvokerImpl.class, cd.getComponentClass());
        assertEquals("componentInvoker", cd.getComponentName());

        cd = creator.createComponentDef(ComponentInvokerImpl.class);
        assertNotNull(cd);
        assertEquals(ComponentInvokerImpl.class, cd.getComponentClass());
        assertEquals("componentInvoker", cd.getComponentName());

        cd = creator.createComponentDef(getClass());
        assertNull(cd);
    }

    /**
     * @throws Exception
     */
    public void setUpHotDeploy() throws Exception {
        originalLoader = Thread.currentThread().getContextClassLoader();
        NamingConventionImpl convention = new NamingConventionImpl();
        convention.addRootPackageName(ClassUtil.getPackageName(getClass()));
        ondemand = new HotdeployBehavior();
        ondemand.setNamingConvention(convention);
        ondemand
                .setCreators(new ComponentCreator[] { new ComponentInvokerCreator() });
        S2ContainerBehavior.setProvider(ondemand);
        ondemand.start();
    }

    /**
     * @throws Exception
     */
    public void testHotDeploy() throws Exception {
        String name = "componentInvoker";
        ComponentDef cd = getComponentDef(name);
        assertNotNull("1", cd);
        assertEquals("2", name, cd.getComponentName());
    }

    /**
     * @throws Exception
     */
    public void tearDownHotDeploy() throws Exception {
        ondemand.stop();
        S2ContainerBehavior
                .setProvider(new S2ContainerBehavior.DefaultProvider());
        Thread.currentThread().setContextClassLoader(originalLoader);
    }

}