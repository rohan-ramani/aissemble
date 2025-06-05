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

import java.io.IOException;

import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import com.boozallen.aissemble.upgrade.migration.AbstractMigrationTest;
import com.boozallen.aissemble.upgrade.migration.extensions.HabushuMonorepoDependencyMigrationTest;
import com.boozallen.aissemble.upgrade.migration.utils.MigrationTestUtils;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class HabushuContainerizationSteps extends AbstractMigrationTest {
    HabushuMonorepoDependencyMigrationTest habushuMonorepoDependencyMigration = new HabushuMonorepoDependencyMigrationTest();
    MlTrainPipelineDockerMigration trainingDockerPomMigration = new MlTrainPipelineDockerMigration();
    InferenceDockerPomMigration inferenceDockerPomMigration = new InferenceDockerPomMigration();

    @Given("a POM with packaging habushu")
    public void aPomWithPackagingHabushu() throws IOException, XmlPullParserException {
        setTestFileToVersionMigration("HabushuMonorepoDependencyMigration", "pom.xml");
        habushuMonorepoDependencyMigration.setMavenProject(MigrationTestUtils.createMavenProjectFromPom(testFile));
    }

    @Given("a pom-type dependency on another habushu-packaged module that is in the list of Maven projects")
    public void aPomTypeDependencyOnAnotherHabushuPackagedModuleThatIsInTheListOfMavenProjects() {
        //dependencies are set in the test POM file, so just add project
        habushuMonorepoDependencyMigration.addProject("habushu-library", "habushu");
    }

    @Given("an training POM without a Habushu containerize goal")
    public void anTrainingPOMWithoutAHabushuContainerizeGoal() throws IOException, XmlPullParserException {
        setTestFileToVersionMigration("MlTrainPipelineDockerMigration", "pom.xml");
        trainingDockerPomMigration.setMavenProject(MigrationTestUtils.createMavenProjectFromPom(testFile));
    }

    @Given("an training POM with a Habushu containerize goal")
    public void anTrainingPOMWithAHabushuContainerizeGoal() throws XmlPullParserException, IOException {
        setTestFileToVersionMigration("MlTrainPipelineDockerMigration", "pom-with-containerize.xml");
        trainingDockerPomMigration.setMavenProject(MigrationTestUtils.createMavenProjectFromPom(testFile));
    }

    @Given("the fermenter-mda plugin has the profile aissemble-training-docker")
    public void theFermenterMdaPluginHasTheProfileAissembleTrainingDocker(){
        // Already covered by test POM file content
    }

    @When("the training docker pom migration executes")
    public void theTrainingDockerPomMigrationExecutes() {
        performMigration(trainingDockerPomMigration);
        habushuMonorepoDependencyMigration.addProject("habushu-library", "habushu");
    }

    @Given("an inference POM without a Habushu containerize goal")
    public void anInferencePOMWithoutAHabushuContainerizeGoal() throws IOException, XmlPullParserException {
        setTestFileToVersionMigration("InferenceDockerPomMigration", "pom.xml");
        inferenceDockerPomMigration.setMavenProject(MigrationTestUtils.createMavenProjectFromPom(testFile));
    }

    @Given("an inference POM with a Habushu containerize goal")
    public void anInferencePOMWithAHabushuContainerizeGoal() throws XmlPullParserException, IOException {
        setTestFileToVersionMigration("InferenceDockerPomMigration", "pom-with-containerize.xml");
        inferenceDockerPomMigration.setMavenProject(MigrationTestUtils.createMavenProjectFromPom(testFile));
    }

    @Given("the fermenter-mda plugin has the profile aissemble-inference-docker")
    public void theFermenterMdaPluginHasTheProfileAissembleInferenceDocker() {
        // Handled in test file
    }

    @Given("a non ML Inference docker POM files")
    public void aNonMLInferenceDockerPOMFiles() throws XmlPullParserException, IOException {
        setTestFileToVersionMigration("InferenceDockerPomMigration", "non-ml-inference-pom.xml");
        inferenceDockerPomMigration.setMavenProject(MigrationTestUtils.createMavenProjectFromPom(testFile));
    }

    @When("the inference docker pom migration executes")
    public void theInferenceDockerPomMigrationExecutes() {
        performMigration(inferenceDockerPomMigration);
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
        assertMigrationSuccess();
        assertTestFileMatchesExpectedFile("Habushu containerize-dependencies goal was not added to POM file");
    }

    @Then("the training docker pom migration was skipped")
    public void theTrainingDockerPomMigrationWasSkipped() {
        assertMigrationSkipped();
    }

    @Then("the inference docker pom migration was skipped")
    public void theInferenceDockerPomMigrationWasSkipped() {
        assertMigrationSkipped();
    }
}
