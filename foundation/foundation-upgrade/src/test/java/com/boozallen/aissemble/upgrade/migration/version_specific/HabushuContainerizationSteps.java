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
    HabushuMonorepoDependencyMigrationTest habushuMonorepoDependencyMigration = new HabushuMonorepoDependencyMigrationTest();
    MlTrainPipelineDockerMigration trainingDockerPomMigration = new MlTrainPipelineDockerMigration();

    @Given("a POM with packaging habushu")
    public void aPomWithPackagingHabushu() throws IOException, XmlPullParserException {
        setTestFileToVersionMigration("HabushuMonorepoDependencyMigration", "pom.xml");
        Model model = new MavenXpp3Reader().read(new FileReader(testFile));
        MavenProject testProject = new MavenProject(model);
        habushuMonorepoDependencyMigration.setMavenProject(testProject);
    }

    @And("a pom-type dependency on another habushu-packaged module that is in the list of Maven projects")
    public void aPomTypeDependencyOnAnotherHabushuPackagedModuleThatIsInTheListOfMavenProjects() {
        //dependencies are set in the test POM file, so just add project
        habushuMonorepoDependencyMigration.addProject("habushu-library", "habushu");
    }

    @Given("an training POM without a Habushu containerize goal")
    public void anTrainingPOMWithoutAHabushuContainerizeGoal() throws IOException, XmlPullParserException {
        setTestFileToVersionMigration("MlTrainPipelineDockerMigration", "pom.xml");
        trainingDockerPomMigration.setMavenProject(createMavenProjectFromPom());
    }

    @Given("an training POM with a Habushu containerize goal")
    public void anTrainingPOMWithAHabushuContainerizeGoal() throws XmlPullParserException, IOException {
        setTestFileToVersionMigration("MlTrainPipelineDockerMigration", "pom-with-containerize.xml");
        trainingDockerPomMigration.setMavenProject(createMavenProjectFromPom());
    }

    @And("the fermenter-mda plugin has the profile aissemble-training-docker")
    public void theFermenterMdaPluginHasTheProfileAissembleTrainingDocker(){
        // Already covered by test POM file content
    }

    @When("the training docker pom migration executes")
    public void theTrainingDockerPomMigrationExecutes() {
        performMigration(trainingDockerPomMigration);
    }

    @When("the Habushu dependency type migration executes")
    public void theHabushuDependencyTypeMigrationExecutes() {
        performMigration(habushuMonorepoDependencyMigration);
    }

    @Then("the type of the monorepo dependency is updated to habushu")
    public void theTypeOfTheMonorepoDependencyIsUpdatedToHabushu() {
        assertMigrationSuccess();
        assertTestFileMatchesExpectedFile("POM Dependency type not updated correctly");
    }

    @Then("the POM is updated to use the habushu containerize goal")
    public void thePOMIsUpdatedToUseTheHabushuContainerizeGoal() {
        assertTestFileMatchesExpectedFile("Habushu containerize-dependencies goal was not added to POM file");
    }

    @Then("the training docker pom migration was skipped")
    public void theTrainingDockerPomMigrationWasSkipped() {
        assertMigrationSkipped();
    }

    private MavenProject createMavenProjectFromPom() throws IOException, XmlPullParserException {
        Model model = new MavenXpp3Reader().read(new FileReader(testFile));
        return new MavenProject(model);
    }
}
