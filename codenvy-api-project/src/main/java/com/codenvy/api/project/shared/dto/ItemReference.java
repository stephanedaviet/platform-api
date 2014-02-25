package com.codenvy.api.project.shared.dto;

import com.codenvy.api.core.rest.shared.dto.Link;
import com.codenvy.dto.shared.DTO;

import java.util.List;

/**
 * @author andrew00x
 */
@DTO
public interface ItemReference {
    /** Get name of item. */
    String getName();

    /** Set name of item. */
    void setName(String name);

    ItemReference withName(String name);

    /** Get type of item, e.g. "file" or "folder". */
    String getType();

    /** Set type of item, e.g. "file" or "folder". */
    void setType(String type);

    /** Get path of item. */
    String getPath();

    /** Set path of item. */
    void setPath(String path);

    ItemReference withPath(String path);

    ItemReference withType(String type);

    List<Link> getLinks();

    ItemReference withLinks(List<Link> links);

    void setLinks(List<Link> links);
}
