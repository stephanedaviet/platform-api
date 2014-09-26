/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.api.user.server;

import sun.security.acl.PrincipalImpl;

import com.codenvy.api.core.rest.shared.dto.Link;
import com.codenvy.api.user.server.dao.PreferenceDao;
import com.codenvy.api.user.server.dao.Profile;
import com.codenvy.api.user.server.dao.User;
import com.codenvy.api.user.server.dao.UserDao;
import com.codenvy.api.user.server.dao.UserProfileDao;
import com.codenvy.api.user.shared.dto.ProfileDescriptor;
import com.codenvy.api.user.shared.dto.UserDescriptor;
import com.codenvy.commons.json.JsonHelper;

import org.everrest.core.impl.ApplicationContextImpl;
import org.everrest.core.impl.ApplicationProviderBinder;
import org.everrest.core.impl.ContainerRequest;
import org.everrest.core.impl.ContainerResponse;
import org.everrest.core.impl.EnvironmentContext;
import org.everrest.core.impl.EverrestConfiguration;
import org.everrest.core.impl.EverrestProcessor;
import org.everrest.core.impl.ProviderBinder;
import org.everrest.core.impl.ResourceBinderImpl;
import org.everrest.core.tools.DependencySupplierImpl;
import org.everrest.core.tools.ResourceLauncher;
import org.mockito.Mock;
import org.mockito.testng.MockitoTestNGListener;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.codenvy.api.user.server.Constants.LINK_REL_UPDATE_CURRENT_USER_PROFILE;
import static com.codenvy.api.user.server.Constants.LINK_REL_GET_CURRENT_USER_PROFILE;
import static com.codenvy.api.user.server.Constants.LINK_REL_GET_USER_PROFILE_BY_ID;
import static com.codenvy.api.user.server.Constants.LINK_REL_UPDATE_PREFERENCES;

import static com.codenvy.api.user.server.Constants.LINK_REL_UPDATE_USER_PROFILE_BY_ID;
import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static javax.ws.rs.core.Response.Status.OK;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * Tests for {@link UserProfileService}
 *
 * @author Max Shaposhnik
 * @author Eugene Voevodin
 */
@Listeners(value = {MockitoTestNGListener.class})
public class UserProfileServiceTest {

    private static final String BASE_URI     = "http://localhost/service";
    private static final String SERVICE_PATH = BASE_URI + "/profile";

    @Mock
    private UserProfileDao     profileDao;
    @Mock
    private UserDao            userDao;
    @Mock
    private PreferenceDao      preferenceDao;
    @Mock
    private User               testUser;
    @Mock
    private UriInfo            uriInfo;
    @Mock
    private EnvironmentContext environmentContext;
    @Mock
    private SecurityContext    securityContext;
    private ResourceLauncher   launcher;
    private UserProfileService service;

    @BeforeMethod
    public void setUp() throws Exception {
        final ResourceBinderImpl resources = new ResourceBinderImpl();
        resources.addResource(UserProfileService.class, null);
        final DependencySupplierImpl dependencies = new DependencySupplierImpl();
        dependencies.addComponent(UserDao.class, userDao);
        dependencies.addComponent(UserProfileDao.class, profileDao);
        dependencies.addComponent(PreferenceDao.class, preferenceDao);
        final URI uri = new URI(BASE_URI);
        final ContainerRequest req = new ContainerRequest(null, uri, uri, null, null, securityContext);
        final ApplicationContextImpl contextImpl = new ApplicationContextImpl(req, null, ProviderBinder.getInstance());
        contextImpl.setDependencySupplier(dependencies);
        ApplicationContextImpl.setCurrent(contextImpl);
        final EverrestProcessor processor = new EverrestProcessor(resources,
                                                                  new ApplicationProviderBinder(),
                                                                  dependencies,
                                                                  new EverrestConfiguration(),
                                                                  null);
        launcher = new ResourceLauncher(processor);
        service = (UserProfileService)resources.getMatchedResource("/profile", new ArrayList<String>())
                                               .getInstance(ApplicationContextImpl.getCurrent());
        //setup testUser
        final String id = "user123abc456def";
        final String email = "user@testuser.com";
        when(testUser.getEmail()).thenReturn(email);
        when(testUser.getId()).thenReturn(id);
        when(environmentContext.get(SecurityContext.class)).thenReturn(securityContext);
        when(securityContext.getUserPrincipal()).thenReturn(new PrincipalImpl(email));
        when(userDao.getByAlias(email)).thenReturn(testUser);
        when(userDao.getById(id)).thenReturn(testUser);
        com.codenvy.commons.env.EnvironmentContext.getCurrent().setUser(new com.codenvy.commons.user.User() {

            @Override
            public String getName() {
                return testUser.getEmail();
            }

            @Override
            public boolean isMemberOf(String s) {
                return false;
            }

            @Override
            public String getToken() {
                return null;
            }

            @Override
            public String getId() {
                return testUser.getId();
            }

            @Override
            public boolean isTemporary() {
                return false;
            }
        });
    }

    @Test
    public void shouldBeAbleToGetCurrentProfile() throws Exception {
        final Profile current = new Profile().withId(testUser.getId()).withUserId(testUser.getId());
        when(profileDao.getById(current.getId())).thenReturn(current);

        final ContainerResponse response = makeRequest("GET", SERVICE_PATH, null);

        assertEquals(response.getStatus(), OK.getStatusCode());
        final ProfileDescriptor descriptor = (ProfileDescriptor)response.getEntity();
        assertEquals(descriptor.getId(), current.getId());
        assertEquals(descriptor.getUserId(), current.getUserId());
        assertEquals(descriptor.getAttributes().get("email"), testUser.getEmail());
        assertTrue(descriptor.getPreferences().isEmpty());
    }

    @Test(enabled = false)
    public void shouldBeAbleToRemovePreferences() throws Exception {
        //TODO
    }

    @Test
    public void shouldBeAbleToRemoveAttributes() throws Exception {
        final Map<String, String> attributes = new HashMap<>(8);
        attributes.put("test", "test");
        attributes.put("test1", "test");
        attributes.put("test2", "test");
        final Profile profile = new Profile().withId(testUser.getId()).withAttributes(attributes);
        when(profileDao.getById(profile.getId())).thenReturn(profile);

        final ContainerResponse response = makeRequest("DELETE", SERVICE_PATH + "/attributes", asList("test", "test2"));

        assertEquals(response.getStatus(), NO_CONTENT.getStatusCode());
        verify(profileDao, times(1)).update(profile);
        assertEquals(attributes.size(), 1);
        assertNotNull(attributes.get("test1"));
    }

    @Test(enabled = false)
    public void shouldBeAbleToUpdatePreferences() throws Exception {
        //TODO
    }

    @Test
    public void shouldBeAbleToGetProfileById() throws Exception {
        final Profile profile = new Profile().withId(testUser.getId())
                                             .withUserId(testUser.getId());
        when(profileDao.getById(profile.getId())).thenReturn(profile);

        final ContainerResponse response = makeRequest("GET", SERVICE_PATH + "/" + profile.getId(), null);

        assertEquals(response.getStatus(), OK.getStatusCode());
        final ProfileDescriptor descriptor = (ProfileDescriptor)response.getEntity();
        assertEquals(descriptor.getUserId(), profile.getId());
        assertEquals(descriptor.getId(), profile.getId());
        assertEquals(descriptor.getAttributes().get("email"), testUser.getEmail());
        assertTrue(descriptor.getPreferences().isEmpty());
    }

    @Test
    public void shouldBeAbleToUpdateCurrentProfileAttributes() throws Exception {
        final Profile profile = new Profile().withId(testUser.getId())
                                             .withAttributes(new HashMap<>(singletonMap("existed", "old")));
        when(profileDao.getById(profile.getId())).thenReturn(profile);
        final Map<String, String> attributes = new HashMap<>(4);
        attributes.put("existed", "new");
        attributes.put("new", "value");

        final ContainerResponse response = makeRequest("POST", SERVICE_PATH, attributes);

        assertEquals(response.getStatus(), OK.getStatusCode());
        verify(profileDao, times(1)).update(profile);
        assertEquals(((ProfileDescriptor)response.getEntity()).getAttributes(), attributes);
    }

    @Test
    public void shouldBeAbleToUpdateProfileById() throws Exception {
        final Profile profile = new Profile().withId(testUser.getId())
                                             .withUserId(testUser.getId())
                                             .withAttributes(new HashMap<>(singletonMap("existed", "old")));
        when(profileDao.getById(testUser.getId())).thenReturn(profile);
        final Map<String, String> attributes = new HashMap<>(4);
        attributes.put("existed", "new");
        attributes.put("new", "value");

        final ContainerResponse response = makeRequest("POST", SERVICE_PATH + "/" + profile.getId(), attributes);

        assertEquals(response.getStatus(), OK.getStatusCode());
        assertEquals(((ProfileDescriptor)response.getEntity()).getAttributes(), attributes);
        verify(profileDao, times(1)).update(profile);
    }

    @Test
    public void testLinksForUser() {
        final Profile profile = new Profile().withId(testUser.getId());
        when(securityContext.isUserInRole("user")).thenReturn(true);

        final Set<String> expectedRels = new HashSet<>(asList(LINK_REL_GET_CURRENT_USER_PROFILE,
                                                              LINK_REL_UPDATE_CURRENT_USER_PROFILE,
                                                              LINK_REL_GET_USER_PROFILE_BY_ID,
                                                              LINK_REL_UPDATE_PREFERENCES));

        assertEquals(asRels(service.toDescriptor(profile, securityContext).getLinks()), expectedRels);
    }

    @Test
    public void testLinksForSystemAdmin() {
        final Profile profile = new Profile().withId(testUser.getId());
        when(securityContext.isUserInRole("system/admin")).thenReturn(true);

        final Set<String> expectedRels = new HashSet<>(asList(LINK_REL_UPDATE_USER_PROFILE_BY_ID,
                                                              LINK_REL_GET_USER_PROFILE_BY_ID));

        assertEquals(asRels(service.toDescriptor(profile, securityContext).getLinks()), expectedRels);
    }

    @Test
    public void testLinksForSystemManager() {
        final Profile profile = new Profile().withId(testUser.getId());
        when(securityContext.isUserInRole("system/manager")).thenReturn(true);

        assertEquals(asRels(service.toDescriptor(profile, securityContext).getLinks()), singleton(LINK_REL_GET_USER_PROFILE_BY_ID));
    }

    private Set<String> asRels(List<Link> links) {
        final Set<String> rels = new HashSet<>();
        for (Link link : links) {
            rels.add(link.getRel());
        }
        return rels;
    }

    private ContainerResponse makeRequest(String method, String path, Object entity) throws Exception {
        Map<String, List<String>> headers = null;
        byte[] data = null;
        if (entity != null) {
            headers = new HashMap<>();
            headers.put("Content-Type", singletonList("application/json"));
            data = JsonHelper.toJson(entity).getBytes();
        }
        return launcher.service(method, path, BASE_URI, headers, data, null, environmentContext);
    }
}
