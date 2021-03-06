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


import com.codenvy.api.core.ConflictException;
import com.codenvy.api.core.NotFoundException;
import com.codenvy.api.core.ServerException;
import com.codenvy.api.core.UnauthorizedException;
import com.codenvy.api.core.rest.Service;
import com.codenvy.api.core.rest.annotations.GenerateLink;
import com.codenvy.api.core.rest.annotations.Required;
import com.codenvy.api.core.rest.shared.dto.Link;
import com.codenvy.api.user.server.dao.Profile;
import com.codenvy.api.user.server.dao.User;
import com.codenvy.api.user.server.dao.UserDao;
import com.codenvy.api.user.server.dao.UserProfileDao;
import com.codenvy.api.user.shared.dto.UserDescriptor;
import com.codenvy.commons.env.EnvironmentContext;
import com.codenvy.commons.lang.NameGenerator;
import com.codenvy.dto.server.DtoFactory;
import com.google.inject.Inject;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;


import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.codenvy.api.core.rest.shared.Links.createLink;
import static javax.ws.rs.core.Response.status;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED;
import static com.codenvy.api.user.server.Constants.LINK_REL_GET_USER_BY_ID;
import static com.codenvy.api.user.server.Constants.LINK_REL_CREATE_USER;
import static com.codenvy.api.user.server.Constants.LINK_REL_GET_CURRENT_USER;
import static com.codenvy.api.user.server.Constants.LINK_REL_UPDATE_PASSWORD;
import static com.codenvy.api.user.server.Constants.LINK_REL_GET_USER_BY_EMAIL;
import static com.codenvy.api.user.server.Constants.LINK_REL_REMOVE_USER_BY_ID;
import static com.codenvy.api.user.server.Constants.PASSWORD_LENGTH;
import static com.codenvy.api.user.server.Constants.LINK_REL_GET_USER_PROFILE_BY_ID;
import static com.codenvy.api.user.server.Constants.LINK_REL_GET_CURRENT_USER_PROFILE;

/**
 * User API
 *
 * @author Eugene Voevodin
 */
@Api(value = "/user",
     description = "User manager")
@Path("/user")
public class UserService extends Service {

    private final UserDao        userDao;
    private final UserProfileDao profileDao;
    private final TokenValidator tokenValidator;

    @Inject
    public UserService(UserDao userDao, UserProfileDao profileDao, TokenValidator tokenValidator) {
        this.userDao = userDao;
        this.profileDao = profileDao;
        this.tokenValidator = tokenValidator;
    }

    /**
     * Creates new user and profile.
     * Returns status code <strong>201 CREATED</strong> and {@link com.codenvy.api.user.shared.dto.UserDescriptor} entity.
     *
     * @param token
     *         authentication token
     * @param isTemporary
     *         if it is {@code true} creates temporary user
     * @return entity of created user
     * @throws UnauthorizedException
     *         when token is {@code null}
     * @throws ConflictException
     *         when token is not valid
     * @throws ServerException
     *         when some error occurred while persisting user or user profile
     * @see com.codenvy.api.user.shared.dto.UserDescriptor
     * @see #getCurrent(SecurityContext)
     * @see #updatePassword(String)
     * @see #getById(String, SecurityContext)
     * @see #getByEmail(String, SecurityContext)
     * @see #remove(String)
     * @see com.codenvy.api.user.server.UserProfileService#getCurrent(String, SecurityContext)
     */
    @ApiOperation(value = "Create a new user",
                  notes = "Create a new user in the system",
                  response = UserDescriptor.class,
                  position = 1)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created"),
            @ApiResponse(code = 401, message = "Missed token parameter"),
            @ApiResponse(code = 409, message = "Invalid token"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    @POST
    @Path("/create")
    @GenerateLink(rel = LINK_REL_CREATE_USER)
    @Produces(APPLICATION_JSON)
    public Response create(@ApiParam(value = "Authentication token", required = true)
                           @Required
                           @QueryParam("token")
                           String token,
                           @ApiParam(value = "User type")
                           @QueryParam("temporary")
                           boolean isTemporary,
                           @Context SecurityContext context) throws UnauthorizedException, ConflictException, ServerException {
        if (token == null) {
            throw new UnauthorizedException("Missed token parameter");
        }
        final String userEmail = tokenValidator.validateToken(token);
        final String userId = NameGenerator.generate(UserDescriptor.class.getSimpleName().toLowerCase(), Constants.ID_LENGTH);
        final User user = new User().withId(userId)
                                    .withEmail(userEmail)
                                    .withPassword(NameGenerator.generate("pass", PASSWORD_LENGTH));
        userDao.create(user);
        //creating profile
        final Map<String, String> attributes = new HashMap<>(4);
        attributes.put("temporary", String.valueOf(isTemporary));
        attributes.put("codenvy:created", Long.toString(System.currentTimeMillis()));
        final Profile profile = new Profile().withId(userId)
                                             .withUserId(userId)
                                             .withAttributes(attributes);
        profileDao.create(profile);
        return status(CREATED).entity(toDescriptor(user, context)).build();
    }

    /**
     * Returns current {@link com.codenvy.api.user.shared.dto.UserDescriptor}.
     *
     * @return entity of current user.
     * @throws ServerException
     *         when some error occurred while retrieving current user
     * @see com.codenvy.api.user.shared.dto.UserDescriptor
     * @see #updatePassword(String)
     */
    @ApiOperation(value = "Get current user",
                  notes = "Get user currently logged in the system",
                  response = UserDescriptor.class,
                  position = 2)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    @GET
    @GenerateLink(rel = LINK_REL_GET_CURRENT_USER)
    @RolesAllowed({"user", "temp_user"})
    @Produces(APPLICATION_JSON)
    public UserDescriptor getCurrent(@Context SecurityContext context) throws NotFoundException, ServerException {
        final User user = userDao.getById(currentUser().getId());
        return toDescriptor(user, context);
    }

    /**
     * Updates current user password.
     *
     * @param password
     *         new user password
     * @throws ConflictException
     *         when given password is {@code null}
     * @throws ServerException
     *         when some error occurred while updating profile
     * @see com.codenvy.api.user.shared.dto.UserDescriptor
     */
    @ApiOperation(value = "Update password",
                  notes = "Update current password",
                  position = 3)
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "OK"),
            @ApiResponse(code = 403, message = "Password required"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    @POST
    @Path("/password")
    @GenerateLink(rel = LINK_REL_UPDATE_PASSWORD)
    @RolesAllowed("user")
    @Consumes(APPLICATION_FORM_URLENCODED)
    public void updatePassword(@ApiParam(value = "New password", required = true)
                               @FormParam("password")
                               String password) throws NotFoundException, ServerException, ConflictException {
        if (password == null) {
            throw new ConflictException("Password required");
        }
        final User user = userDao.getById(currentUser().getId());
        user.setPassword(password);
        userDao.update(user);
    }

    /**
     * Searches for {@link com.codenvy.api.user.shared.dto.UserDescriptor} with given identifier.
     *
     * @param id
     *         identifier to search user
     * @return entity of found user
     * @throws NotFoundException
     *         when user with given identifier doesn't exist
     * @throws ServerException
     *         when some error occurred while retrieving user
     * @see com.codenvy.api.user.shared.dto.UserDescriptor
     * @see #getByEmail(String, SecurityContext)
     */
    @ApiOperation(value = "Get user by ID",
                  notes = "Get user by its ID in the system. Roles allowed: system/admin, system/manager.",
                  response = UserDescriptor.class,
                  position = 4)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    @GET
    @Path("/{id}")
    @GenerateLink(rel = LINK_REL_GET_USER_BY_ID)
    @RolesAllowed({"user", "system/admin", "system/manager"})
    @Produces(APPLICATION_JSON)
    public UserDescriptor getById(@ApiParam(value = "User ID")
                                  @PathParam("id")
                                  String id,
                                  @Context SecurityContext context) throws NotFoundException, ServerException {
        final User user = userDao.getById(id);
        return toDescriptor(user, context);
    }

    /**
     * Searches for {@link com.codenvy.api.user.shared.dto.UserDescriptor} with given email.
     *
     * @param email
     *         email to search user
     * @return entity of found user
     * @throws NotFoundException
     *         when user with given email doesn't exist
     * @throws ServerException
     *         when some error occurred while retrieving user
     * @see com.codenvy.api.user.shared.dto.UserDescriptor
     * @see #getById(String, SecurityContext)
     * @see #remove(String)
     */
    @ApiOperation(value = "Get user by email",
                  notes = "Get user by registration email. Roles allowed: system/admin, system/manager.",
                  response = UserDescriptor.class,
                  position = 5)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 403, message = "Missed parameter email"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    @GET
    @Path("/find")
    @GenerateLink(rel = LINK_REL_GET_USER_BY_EMAIL)
    @RolesAllowed({"user", "system/admin", "system/manager"})
    @Produces(APPLICATION_JSON)
    public UserDescriptor getByEmail(@ApiParam(value = "User email", required = true)
                                     @Required
                                     @QueryParam("email")
                                     String email,
                                     @Context SecurityContext context) throws NotFoundException, ServerException, ConflictException {
        if (email == null) {
            throw new ConflictException("Missed parameter email");
        }
        final User user = userDao.getByAlias(email);
        return toDescriptor(user, context);
    }

    /**
     * Removes user with given identifier.
     *
     * @param id
     *         identifier to remove user
     * @throws NotFoundException
     *         when user with given identifier doesn't exist
     * @throws ServerException
     *         when some error occurred while removing user
     * @throws ConflictException
     *         when some error occurred while removing user
     */
    @ApiOperation(value = "Delete user",
                  notes = "Delete a user from the system. Roles allowed: system/admin.",
                  position = 6)
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Deleted"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 409, message = "Impossible to remove user"),
            @ApiResponse(code = 500, message = "Internal Server Error")})
    @DELETE
    @Path("/{id}")
    @GenerateLink(rel = LINK_REL_REMOVE_USER_BY_ID)
    @RolesAllowed("system/admin")
    public void remove(@ApiParam(value = "User ID")
                       @PathParam("id") String id) throws NotFoundException, ServerException, ConflictException {
        userDao.remove(id);
    }

    private UserDescriptor toDescriptor(User user, SecurityContext context) {
        final List<Link> links = new LinkedList<>();
        final UriBuilder uriBuilder = getServiceContext().getServiceUriBuilder();
        if (context.isUserInRole("user")) {
            links.add(createLink("GET",
                                 getServiceContext().getBaseUriBuilder().path(UserProfileService.class)
                                                    .path(UserProfileService.class, "getCurrent")
                                                    .build()
                                                    .toString(),
                                 null,
                                 APPLICATION_JSON,
                                 LINK_REL_GET_CURRENT_USER_PROFILE));
            links.add(createLink("GET",
                                 uriBuilder.clone()
                                           .path(getClass(), "getCurrent")
                                           .build()
                                           .toString(),
                                 null,
                                 APPLICATION_JSON,
                                 LINK_REL_GET_CURRENT_USER));
            links.add(createLink("POST",
                                 uriBuilder.clone()
                                           .path(getClass(), "updatePassword")
                                           .build()
                                           .toString(),
                                 APPLICATION_FORM_URLENCODED,
                                 null,
                                 LINK_REL_UPDATE_PASSWORD));
        }
        if (context.isUserInRole("system/admin") || context.isUserInRole("system/manager")) {
            links.add(createLink("GET",
                                 uriBuilder.clone()
                                           .path(getClass(), "getById")
                                           .build(user.getId())
                                           .toString(),
                                 null,
                                 APPLICATION_JSON,
                                 LINK_REL_GET_USER_BY_ID));
            links.add(createLink("GET",
                                 getServiceContext().getBaseUriBuilder()
                                                    .path(UserProfileService.class).path(UserProfileService.class, "getById")
                                                    .build(user.getId())
                                                    .toString(),
                                 null,
                                 APPLICATION_JSON,
                                 LINK_REL_GET_USER_PROFILE_BY_ID));
            links.add(createLink("GET",
                                 uriBuilder.clone()
                                           .path(getClass(), "getByEmail")
                                           .queryParam("email", user.getEmail())
                                           .build()
                                           .toString(),
                                 null,
                                 APPLICATION_JSON,
                                 LINK_REL_GET_USER_BY_EMAIL));
        }
        if (context.isUserInRole("system/admin")) {
            links.add(createLink("DELETE",
                                 uriBuilder.clone()
                                           .path(getClass(), "remove")
                                           .build(user.getId())
                                           .toString(),
                                 null,
                                 null,
                                 LINK_REL_REMOVE_USER_BY_ID));
        }
        return DtoFactory.getInstance().createDto(UserDescriptor.class)
                         .withId(user.getId())
                         .withEmail(user.getEmail())
                         .withAliases(user.getAliases())
                         .withPassword("<none>")
                         .withLinks(links);
    }

    private com.codenvy.commons.user.User currentUser() {
        return EnvironmentContext.getCurrent().getUser();
    }
}