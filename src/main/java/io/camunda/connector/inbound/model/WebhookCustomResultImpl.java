package io.camunda.connector.inbound.model;

import io.camunda.connector.api.inbound.webhook.MappedHttpRequest;
import io.camunda.connector.api.inbound.webhook.WebhookHttpResponse;
import io.camunda.connector.api.inbound.webhook.WebhookResult;
import io.camunda.connector.api.inbound.webhook.WebhookResultContext;

import java.net.http.HttpRequest;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public record WebhookCustomResultImpl(
        MappedHttpRequest request,
        Function<WebhookResultContext, WebhookHttpResponse> responseExpression,
        Map<String, Object> connectorData)
        implements WebhookResult {

    public WebhookCustomResultImpl {}

    @Override
    public MappedHttpRequest request() {
        return request;
    }

    @Override
    public Map<String, Object> connectorData() {
        return Optional.ofNullable(connectorData).orElse(Collections.emptyMap());
    }

    @Override
    public Function<WebhookResultContext, WebhookHttpResponse> response() {
        return responseExpression;
    }
}

