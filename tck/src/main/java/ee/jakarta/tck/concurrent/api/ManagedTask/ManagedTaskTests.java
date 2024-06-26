/*
 * Copyright (c) 2013, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package ee.jakarta.tck.concurrent.api.ManagedTask;

import java.util.HashMap;
import java.util.Map;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.testng.annotations.Test;

import ee.jakarta.tck.concurrent.common.RunnableTask;
import ee.jakarta.tck.concurrent.common.managedTaskListener.ManagedTaskListenerImpl;
import ee.jakarta.tck.concurrent.framework.ArquillianTests;
import jakarta.enterprise.concurrent.ManagedExecutors;
import jakarta.enterprise.concurrent.ManagedTask;

public class ManagedTaskTests extends ArquillianTests {
	
	//TODO deploy as EJB and JSP artifacts
	@Deployment(name="ManagedTaskTests")
	public static WebArchive createDeployment() {
		return ShrinkWrap.create(WebArchive.class)
				.addPackages(true, getFrameworkPackage(), getCommonPackage(), getCommonManagedTaskListener(), ManagedTaskTests.class.getPackage());
	}

	private ManagedTaskListenerImpl managedTaskListener = new ManagedTaskListenerImpl();
	
	private RunnableTask createRunnableTask() {
		//Task never actually run
		return new RunnableTask("java:comp/env/StringValue", "FakeValue", this.getClass().getName());
	}

	/*
	 * @testName: GetExecutionProperties
	 * 
	 * @assertion_ids: CONCURRENCY:JAVADOC:36
	 * 
	 * @test_Strategy: Get ManagedTask to provides additional information to the
	 * ManagedExecutorService or ManagedScheduledExecutorService when executing this
	 * task.
	 */
	@Test
	public void GetExecutionProperties() {
		Map<String, String> properties = new HashMap<String, String>();
		properties.put("key", "value");
		Runnable runnableTask = createRunnableTask();
		Runnable task = ManagedExecutors.managedTask(runnableTask, properties, managedTaskListener);
		boolean pass = false;

		if (task instanceof ManagedTask) {
			ManagedTask managedTask = (ManagedTask) task;
			assertTrue(testName + " failed to get expected property", managedTask.getExecutionProperties().get("key") == "value");
		}
		
	}

	/*
	 * @testName: GetManagedTaskListener
	 * 
	 * @assertion_ids: CONCURRENCY:JAVADOC:37
	 * 
	 * @test_Strategy: Get ManagedTask with ManagedTaskListener to receive
	 * notification of life cycle events of this task.
	 */
	@Test
	public void GetManagedTaskListener() {
		Map<String, String> properties = new HashMap<String, String>();
		properties.put("key", "value");
		RunnableTask runnableTask = createRunnableTask();
		Runnable task = ManagedExecutors.managedTask(runnableTask, properties, managedTaskListener);

		if (task instanceof ManagedTask) {
			ManagedTask managedTask = (ManagedTask) task;
			assertTrue(testName + " failed to get expected managedTaskListener", managedTask.getManagedTaskListener() == managedTaskListener);
		}
	}
}
