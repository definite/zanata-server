/*
 * Copyright 2014, Red Hat, Inc. and individual contributors as indicated by the
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
package org.zanata.feature.editor;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.experimental.categories.Category;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.zanata.feature.DetailedTest;
import org.zanata.page.projects.ProjectSourceDocumentsPage;
import org.zanata.page.projects.ProjectVersionPage;
import org.zanata.page.webtrans.EditorPage;
import org.zanata.util.CleanDocumentStorageRule;
import org.zanata.util.SampleProjectRule;
import org.zanata.util.TestFileGenerator;
import org.zanata.workflow.BasicWorkFlow;
import org.zanata.workflow.LoginWorkFlow;
import org.zanata.workflow.ProjectWorkFlow;

import java.io.File;
import java.util.HashMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.zanata.util.FunctionalTestHelper.assumeFalse;

/**
 * @author Damian Jansen
 * <a href="mailto:djansen@redhat.com">djansen@redhat.com</a>
 */
@RunWith(Theories.class)
@Category(DetailedTest.class)
public class TranslateOpenOfficeTest {

    @Rule
    public SampleProjectRule sampleProjectRule = new SampleProjectRule();

    @Rule
    public CleanDocumentStorageRule documentStorageRule =
            new CleanDocumentStorageRule();

    private TestFileGenerator testFileGenerator = new TestFileGenerator();

    @DataPoint
    public static String TEST_ODT = "odt";
    @DataPoint
    public static String TEST_ODG = "odg";
    @DataPoint
    public static String TEST_ODP = "odp";

    @Before
    public void before() {
        new BasicWorkFlow().goToHome().deleteCookiesAndRefresh();
        assumeFalse("", new File(CleanDocumentStorageRule
                .getDocumentStoragePath()
                .concat(File.separator).concat("documents")
                .concat(File.separator)).exists());
        new LoginWorkFlow().signIn("admin", "admin");
    }

    @Theory
    public void translateBasicOpenOfficeFile(String extension) {
        File testfile = testFileGenerator.openTestFile(
                "test-" + extension + "." + extension);

        HashMap<String, String> projectSettings = ProjectWorkFlow.projectDefaults();
        projectSettings.put("Project ID", extension + "-project");
        projectSettings.put("Name", extension + "-project");
        projectSettings.put("Project Type", "File");

        ProjectSourceDocumentsPage projectSourceDocumentsPage = new
                ProjectWorkFlow()
                .createNewProject(projectSettings).clickCreateVersionLink()
                .inputVersionId(extension).saveVersion()
                .goToSourceDocuments().pressUploadFileButton()
                .enterFilePath(testfile.getAbsolutePath()).submitUpload();

        EditorPage editorPage = projectSourceDocumentsPage
                .clickBreadcrumb(extension, ProjectVersionPage.class)
                .translate("fr").clickDocumentLink("", testfile.getName());

        editorPage.setSyntaxHighlighting(false);

        assertThat("Item 1 shows Line One",
                editorPage.getTranslationSourceAtRowIndex(0),
                Matchers.equalTo("Line One"));
        assertThat("Item 2 shows Line Two",
                editorPage.getTranslationSourceAtRowIndex(1),
                Matchers.equalTo("Line Two"));
        assertThat("Item 3 shows Line Three",
                editorPage.getTranslationSourceAtRowIndex(2),
                Matchers.equalTo("Line Three"));

        editorPage = editorPage.translateTargetAtRowIndex(0, "Une Ligne")
                .approveSelectedTranslation();
        editorPage = editorPage.translateTargetAtRowIndex(1, "Deux Ligne")
                .approveSelectedTranslation();
        editorPage = editorPage.translateTargetAtRowIndex(2, "Ligne Trois")
                .approveSelectedTranslation();

        assertThat("Item 1 shows a translation of Line One",
                editorPage.getBasicTranslationTargetAtRowIndex(0),
                Matchers.equalTo("Une Ligne"));
        assertThat("Item 1 shows a translation of Line One",
                editorPage.getBasicTranslationTargetAtRowIndex(1),
                Matchers.equalTo("Deux Ligne"));
        assertThat("Item 1 shows a translation of Line One",
                editorPage.getBasicTranslationTargetAtRowIndex(2),
                Matchers.equalTo("Ligne Trois"));

        // Close and reopen the editor to test save, switches to CodeMirror
        editorPage.reload();

        assertThat("Item 1 shows a translation of Line One",
                editorPage.getTranslationTargetAtRowIndex(0),
                Matchers.equalTo("Une Ligne"));
        assertThat("Item 1 shows a translation of Line One",
                editorPage.getTranslationTargetAtRowIndex(1),
                Matchers.equalTo("Deux Ligne"));
        assertThat("Item 1 shows a translation of Line One",
                editorPage.getTranslationTargetAtRowIndex(2),
                Matchers.equalTo("Ligne Trois"));
    }
}
