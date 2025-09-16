package com.boozallen.aissemble.alerting.teams.models;

/*-
 * #%L
 * aiSSEMBLE::Extensions::Alerting::Teams
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * This software package is licensed under the Booz Allen Public License. All Rights Reserved.
 * #L%
 */

import java.util.List;
import java.util.Objects;

public class CardMessage {
    private String type;
    private List<Card> attachments;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Card> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Card> attachments) {
        this.attachments = attachments;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CardMessage that = (CardMessage) o;
        return Objects.equals(getType(), that.getType()) && Objects.equals(getAttachments(), that.getAttachments());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), getAttachments());
    }
}
