package com.boozallen.aissemble.alerting;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Alerting::Teams
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.alerting.teams.CardMessageFactory;
import com.boozallen.aissemble.alerting.teams.TeamsClient;
import com.boozallen.aissemble.alerting.teams.TeamsMessageService;

import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.Assert.assertTrue;

import org.mockito.Mockito;

import static org.mockito.Mockito.when;

public class TeamsAlertingTest {
    private final String MESSAGE = "Test List\r- Test Item 1\r- Test Item 2: ✅";
    private final TeamsClient testTeamsClient = Mockito.mock(TeamsClient.class);
    private final TeamsMessageService teamsMessageService = new TeamsMessageService(testTeamsClient);
    private boolean success;


    @Before
    public void setup() {
        when(testTeamsClient.sendMessage(CardMessageFactory.create(MESSAGE))).thenReturn("1");
    }

    @When("an alert is sent to teams")
    public void anAlertIsSentToTeams() {
        success = teamsMessageService.sendMessage(MESSAGE);
    }

    @Then("the alert is sent to the configured teams channel successfully")
    public void theAlertIsSentToTheConfiguredTeamsChannelSuccessfully() {
        assertTrue( "Message not sent successfully", success);
    }
}
