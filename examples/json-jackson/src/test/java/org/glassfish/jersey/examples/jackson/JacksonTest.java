/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2010-2012 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * http://glassfish.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package org.glassfish.jersey.examples.jackson;

import org.glassfish.jersey.media.json.JsonFeature;
import org.glassfish.jersey.media.json.JsonJacksonModule;
import org.glassfish.jersey.message.internal.MediaTypes;
import org.glassfish.jersey.server.JerseyApplication;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.glassfish.jersey.test.spi.TestContainer;
import org.junit.Ignore;
import org.junit.Test;

import javax.ws.rs.client.Target;
import javax.ws.rs.core.MediaType;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Jakub Podlesak
 */
public class JacksonTest extends JerseyTest {

    @Override
    protected JerseyApplication configure() {
        enable(TestProperties.LOG_TRAFFIC);

        return App.createApp();
    }

    @Override
    protected javax.ws.rs.client.Client getClient(TestContainer tc, JerseyApplication application) {
        javax.ws.rs.client.Client origClient = super.getClient(tc, application);
        origClient.configuration().enable(JsonFeature.getInstance());

        origClient.configuration().register(MyObjectMapperProvider.class);
        return origClient;
    }

    @Test
    public void testEmptyArrayPresent() {
        Target target = target();
        String responseMsg = target.path("emptyArrayResource").request(MediaType.APPLICATION_JSON).get(String.class);
        assertTrue(responseMsg.replaceAll("[ \t]*", "").contains("[]"));
    }

    @Test
    public void testJSONPPresent() {
        Target target = target();
        String responseMsg = target.path("nonJaxbResource").request("application/javascript").get(String.class);
        assertTrue(responseMsg.startsWith("callback("));
    }

    @Test
    public void testJSONDoesNotReflectJSONPWrapper() {
        Target target = target();
        String responseMsg = target.path("nonJaxbResource").request("application/json").get(String.class);
        assertTrue(!responseMsg.contains("jsonSource"));
    }

    @Test
    public void testCombinedAnnotationResource() {
        Target target = target();
        String responseMsg = target.path("combinedAnnotations").request("application/json").get(String.class);
        assertTrue(responseMsg.contains("account") && responseMsg.contains("value"));
    }

    @Test
    public void testEmptyArrayBean() {
        Target target = target();
        EmptyArrayBean responseMsg = target.path("emptyArrayResource").request(MediaType.APPLICATION_JSON).get(EmptyArrayBean.class);
        assertNotNull(responseMsg);
    }

    @Test
    public void testCombinedAnnotationBean() {
        Target target = target();
        CombinedAnnotationBean responseMsg = target.path("combinedAnnotations").request("application/json").get(CombinedAnnotationBean.class);
        assertNotNull(responseMsg);
    }

    @Test
    @Ignore
    // TODO un-ignore once a JSON reader for "application/javascript" is supported
    public void testJSONPBean() {
        Target target = target();
        NonJaxbBean responseMsg = target.path("nonJaxbResource").request("application/javascript").get(NonJaxbBean.class);
        assertNotNull(responseMsg);
    }

    /**
     * Test if a WADL document is available at the relative path
     * "application.wadl".
     * <p/>
     * TODO: un-ignore once WADL is supported.
     */
    @Test
    @Ignore
    public void testApplicationWadl() {
        Target target = target();
        String serviceWadl = target.path("application.wadl").request(MediaTypes.WADL).get(String.class);

        assertTrue(serviceWadl.length() > 0);
    }
}