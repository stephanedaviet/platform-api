/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2013] Codenvy, S.A.
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.api.tools.builder.maven;

import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.FileReader;
import java.io.IOException;

/** @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a> */
public class MavenProjectModelFactory {

    private static final MavenProjectModelFactory INSTANCE = new MavenProjectModelFactory();

    public static MavenProjectModelFactory getInstance() {
        return INSTANCE;
    }

    /**
     * Get description of maven project.
     *
     * @param sources
     *         maven project directory. NOte: Must contains pom.xml file.
     * @return description of maven project
     */
    public MavenProjectModel getMavenProjectModel(java.io.File sources) {
        final java.io.File pom = new java.io.File(sources, "pom.xml");
        if (pom.exists()) {
            final MavenXpp3Reader pomReader = new MavenXpp3Reader();
            try {
                final Model model = pomReader.read(new FileReader(new java.io.File(sources, "pom.xml")), true);
                final Parent parent = model.getParent();
                MavenProjectModel myParent = null;
                if (parent != null) {
                    myParent = new MavenProjectModel(parent.getGroupId(),
                                                     parent.getArtifactId(),
                                                     parent.getVersion(),
                                                     null,
                                                     null,
                                                     null,
                                                     null,
                                                     null,
                                                     null);

                }
                String groupId = model.getGroupId();
                if (groupId == null && parent != null) {
                    groupId = parent.getGroupId();
                }
                String version = model.getVersion();
                if (version == null && parent != null) {
                    version = parent.getVersion();
                }
                return new MavenProjectModel(groupId,
                                             model.getArtifactId(),
                                             version,
                                             model.getPackaging(),
                                             model.getName(),
                                             model.getDescription(),
                                             myParent,
                                             model.getPomFile(),
                                             model.getProjectDirectory());
            } catch (IOException | XmlPullParserException e) {
                throw new IllegalStateException(e);
            }
        }
        throw new IllegalStateException(String.format("There is no pom.xml file in this directory %s.", sources));
    }

    private MavenProjectModelFactory() {
    }
}
