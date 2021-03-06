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
package com.codenvy.api.project.shared.dto;

import com.codenvy.api.core.factory.FactoryParameter;
import com.codenvy.dto.shared.DTO;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.List;
import java.util.Map;

import static com.codenvy.api.core.factory.FactoryParameter.Obligation.OPTIONAL;

/**
 * Data transfer object (DTO) for update project.
 *
 * @author andrew00x
 */
@DTO
@ApiModel(description = "Update project")
public interface ProjectUpdate {
    /** Get unique ID of type of project. */
    @ApiModelProperty(value = "Unique ID of project's type", position = 1, required = true)
    @FactoryParameter(obligation = OPTIONAL, queryParameterName = "type")
    String getType();

    /** Set unique ID of type of project. */
    void setType(String type);

    ProjectUpdate withType(String type);

    //

    /** Gets builder configurations. */
    @ApiModelProperty(value = "Builders configuration for the project", position = 5)
    @FactoryParameter(obligation = OPTIONAL, queryParameterName = "builders")
    BuildersDescriptor getBuilders();

    /** Sets builder configurations. */
    void setBuilders(BuildersDescriptor builders);

    ProjectUpdate withBuilders(BuildersDescriptor builders);

    //

    /** Gets runner configurations. */
    @ApiModelProperty(value = "Runners configuration for the project", position = 6)
    @FactoryParameter(obligation = OPTIONAL, queryParameterName = "runners")
    RunnersDescriptor getRunners();

    /** Sets runner configurations. */
    void setRunners(RunnersDescriptor runners);

    ProjectUpdate withRunners(RunnersDescriptor runners);

    //

    /** Get optional description of project. */
    @ApiModelProperty(value = "Optional description for new project", position = 2)
    @FactoryParameter(obligation = OPTIONAL, queryParameterName = "description")
    String getDescription();

    /** Set optional description of project. */
    void setDescription(String description);

    ProjectUpdate withDescription(String description);

    //

    @ApiModelProperty(value = "Attributes for project", position = 4)
    @FactoryParameter(obligation = OPTIONAL, queryParameterName = "attributes")
    /** Get attributes of project. */
    Map<String, List<String>> getAttributes();

    /** Set attributes of project. */
    void setAttributes(Map<String, List<String>> attributes);

    ProjectUpdate withAttributes(Map<String, List<String>> attributes);

    //

    @ApiModelProperty(value = "Visibility for project", allowableValues = "public,private", position = 3)
    @FactoryParameter(obligation = OPTIONAL, queryParameterName = "visibility")
    /** Gets project visibility, e.g. private or public. */
    String getVisibility();

    /** Sets project visibility, e.g. private or public. */
    void setVisibility(String visibility);

    ProjectUpdate withVisibility(String visibility);
}
