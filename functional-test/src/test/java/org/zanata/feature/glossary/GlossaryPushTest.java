/*
 * Copyright 2013, Red Hat, Inc. and individual contributors as indicated by the
 * @author tags. See the copyright.txt file in the distribution for a full
 * listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.zanata.feature.glossary;

import java.io.File;
import java.util.List;

import org.concordion.api.extension.Extensions;
import org.concordion.ext.ScreenshotExtension;
import org.concordion.ext.TimestampFormatterExtension;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.Rule;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.zanata.concordion.CustomResourceExtension;
import org.zanata.feature.ConcordionTest;
import org.zanata.page.webtrans.EditorPage;
import org.zanata.util.SampleProjectRule;
import org.zanata.workflow.BasicWorkFlow;
import org.zanata.workflow.ClientPushWorkFlow;
import org.zanata.workflow.LoginWorkFlow;
import com.google.common.base.Joiner;

import static org.zanata.workflow.BasicWorkFlow.EDITOR_TEMPLATE;

/**
 * @see <a href="https://tcms.engineering.redhat.com/case/147311/">TCMS case</a>
 *
 * @author Patrick Huang <a
 *         href="mailto:pahuang@redhat.com">pahuang@redhat.com</a>
 */
@RunWith(ConcordionRunner.class)
@Extensions({ ScreenshotExtension.class, TimestampFormatterExtension.class,
        CustomResourceExtension.class })
@Category(ConcordionTest.class)
public class GlossaryPushTest {
    @Rule
    public TestRule sampleProjectRule = new SampleProjectRule();

    private ClientPushWorkFlow clientPushWorkFlow = new ClientPushWorkFlow();
    private File projectRootPath;
    private EditorPage editorPage;

    public String getUserConfigPath() {
        return ClientPushWorkFlow.getUserConfigPath("glossarist");
    }

    public String getProjectLocation(String project) {
        projectRootPath = clientPushWorkFlow.getProjectRootPath(project);
        return projectRootPath.getAbsolutePath();
    }

    public List<String> push(String command, String configPath)
            throws Exception {
        return clientPushWorkFlow.callWithTimeout(projectRootPath, command
                + configPath);
    }

    public boolean isPushSuccessful(List<String> output) {
        return clientPushWorkFlow.isPushSuccessful(output);
    }

    public String resultByLines(List<String> output) {
        return Joiner.on("\n").join(output);
    }

    public void translate() {
        new LoginWorkFlow().signIn("translator", "translator");
        editorPage =
                new BasicWorkFlow().goToPage(String.format(
                        EDITOR_TEMPLATE, "about-fedora",
                        "master", "fr", "About_Fedora"), EditorPage.class);
    }

    public void searchGlossary(String term) {
        editorPage.searchGlossary(term);
    }

    public String getFirstResult() {
        // 2 row 2 column is glossary target
        return editorPage.getGlossaryResultTable().get(1).get(1);
    }
}
