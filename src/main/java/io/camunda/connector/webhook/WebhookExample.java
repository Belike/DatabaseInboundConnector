package io.camunda.connector.webhook;

import io.camunda.connector.api.inbound.InboundConnectorContext;
import io.camunda.connector.api.inbound.webhook.WebhookConnectorExecutable;
import io.camunda.connector.api.inbound.webhook.WebhookHttpResponse;
import io.camunda.connector.api.inbound.webhook.WebhookProcessingPayload;
import io.camunda.connector.api.inbound.webhook.WebhookResult;

public class WebhookExample implements WebhookConnectorExecutable {
    @Override
    public WebhookHttpResponse verify(WebhookProcessingPayload payload) {
        return WebhookConnectorExecutable.super.verify(payload);
    }

    @Override
    public WebhookResult triggerWebhook(WebhookProcessingPayload payload) throws Exception {
        return null;
    }

    @Override
    public void activate(InboundConnectorContext context) throws Exception {
        WebhookConnectorExecutable.super.activate(context);
    }

    @Override
    public void deactivate() throws Exception {
        WebhookConnectorExecutable.super.deactivate();
    }
}
