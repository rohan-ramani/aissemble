package com.boozallen.aissemble.upgrade.migration.version_specific;

/*-
 * #%L
 * aiSSEMBLE::Foundation::Upgrade
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.upgrade.migration.AbstractMigrationTest;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.FileReader;
import java.io.IOException;

public class HabushuContainerizationSteps extends AbstractMigrationTest {
    HabushuMonorepoDependencyMigrationTest migration = new HabushuMonorepoDependencyMigrationTest();

    @Given("a POM with packaging habushu")
    public void aPomWithPackagingHabushu() throws IOException, XmlPullParserException {
        setTestFileToVersionMigration("HabushuMonorepoDependencyMigration", "pom.xml");
        Model model = new MavenXpp3Reader().read(new FileReader(testFile));
        MavenProject testProject = new MavenProject(model);
        migration.setMavenProject(testProject);
    }

    @And("a pom-type dependency on another habushu-packaged module that is in the list of Maven projects")
    public void aPomTypeDependencyOnAnotherHabushuPackagedModuleThatIsInTheListOfMavenProjects() {
        //dependencies are set in the test POM file, so just add project
        migration.addProject("habushu-library", "habushu");
    }

    @When("the Habushu dependency type migration executes")
    public void theHabushuDependencyTypeMigrationExecutes() {
        performMigration(migration);
    }

    @Then("the type of the monorepo dependency is updated to habushu")
    public void theTypeOfTheMonorepoDependencyIsUpdatedToHabushu() {
        assertMigrationSuccess();
        assertTestFileMatchesExpectedFile("POM Dependency type not updated correctly");
    }
}
