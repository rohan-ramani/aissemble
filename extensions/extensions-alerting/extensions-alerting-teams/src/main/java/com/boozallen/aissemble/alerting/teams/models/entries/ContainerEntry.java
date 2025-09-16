package com.boozallen.aissemble.alerting.teams.models.entries;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Alerting::Teams
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import com.boozallen.aissemble.alerting.teams.models.CardBodyEntry;

import java.util.List;
import java.util.Objects;

public class ContainerEntry extends CardBodyEntry {

    private String type;
    private List<CardBodyEntry> items;

    // Needed for JSON serialization but should never change
    @Override
    public String getType() {
        return "Container";
    }

    // Needed for JSON serialization but should never change
    @Override
    public void setType(String type) {
        this.type = "Container";
    }

    public List<CardBodyEntry> getItems() {
        return items;
    }

    public void setItems(List<CardBodyEntry> items) {
        this.items = items;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ContainerEntry that = (ContainerEntry) o;
        return Objects.equals(getType(), that.getType()) && Objects.equals(getItems(), that.getItems());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), getItems());
    }
}
