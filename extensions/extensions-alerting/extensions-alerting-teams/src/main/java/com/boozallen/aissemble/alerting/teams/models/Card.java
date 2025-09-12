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

import java.util.Objects;

public class Card {
    private String contentType;
    private String contentUrl;
    private CardContent content;

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }

    public CardContent getContent() {
        return content;
    }

    public void setContent(CardContent content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return Objects.equals(getContentType(), card.getContentType()) && Objects.equals(getContentUrl(), card.getContentUrl()) && Objects.equals(getContent(), card.getContent());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getContentType(), getContentUrl(), getContent());
    }
}
