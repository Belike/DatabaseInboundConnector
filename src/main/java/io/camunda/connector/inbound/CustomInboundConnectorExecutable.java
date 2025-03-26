package io.camunda.connector.inbound;

import io.camunda.connector.api.annotation.InboundConnector;
import io.camunda.connector.api.inbound.Health;
import io.camunda.connector.api.inbound.InboundConnectorContext;
import io.camunda.connector.api.inbound.webhook.*;
import io.camunda.connector.generator.dsl.BpmnType;
import io.camunda.connector.generator.java.annotation.ElementTemplate;
import io.camunda.connector.generator.java.annotation.ElementTemplate.ConnectorElementType;
import io.camunda.connector.generator.java.annotation.ElementTemplate.PropertyGroup;
import io.camunda.connector.inbound.model.CustomWebHookProperties;
import io.camunda.connector.inbound.model.CustomWebHookProperties.CustomWebHookPropertiesWrapper;
import io.camunda.connector.inbound.model.WebhookCustomResultImpl;
import io.camunda.connector.inbound.util.CustomWebHookUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.function.Function;

@InboundConnector(name = "Custom Connector", type = "io.camunda.connector.custom:1")
@ElementTemplate(
        id = "io.camunda.connector.custom",
        name = "Custom connector",
        version = 1,
        description = "This is a custom WebHook Connector",
        inputDataClass = CustomWebHookPropertiesWrapper.class,
        icon = "magician.svg",
        documentationRef = "https://docs.camunda.io/docs/components/connectors/out-of-the-box-connectors/available-connectors-overview/",
        propertyGroups = {
                @PropertyGroup(id = "endpoint", label = "Webhook Configuration"),
                @PropertyGroup(id = "webhookResponse", label = "Webhook Response")
        },
        elementTypes = {
                @ConnectorElementType(
                        appliesTo = BpmnType.START_EVENT,
                        elementType = BpmnType.START_EVENT,
                        templateIdOverride = "io.camunda.connectors.webhook.CustomConnector.v1",
                        templateNameOverride = "Custom WebHook Start Event Connector"
                ),
                @ConnectorElementType(
                        appliesTo = BpmnType.START_EVENT,
                        elementType = BpmnType.MESSAGE_START_EVENT,
                        templateIdOverride = "io.camunda.connectors.webhook.CustomConnectorStartMessage.v1",
                        templateNameOverride = "Custom Webhook Message Start Event Connector"
                )
        }
        )
@Slf4j
public class CustomInboundConnectorExecutable implements WebhookConnectorExecutable {

    private InboundConnectorContext context;
    private CustomWebHookProperties properties;
    private Function<WebhookResultContext, WebhookHttpResponse> responseExpression;
    @Override
    public WebhookHttpResponse verify(WebhookProcessingPayload payload) {
        WebhookHttpResponse result = new WebhookHttpResponse(payload.rawBody(), payload.headers(), 200);
        if(properties.verificationExpression() != null) {
            result = properties.verificationExpression().apply(
                    Map.of("request",
                            Map.of("body",
                                    CustomWebHookUtil.transformRawBodyToObject(payload.rawBody(),
                                            CustomWebHookUtil.extractContentType(payload.headers())),
                                    "headers", payload.headers(),
                                    "params", payload.params()))
            );
        }
        return result;
    }

    @Override
    public WebhookResult triggerWebhook(WebhookProcessingPayload payload) throws Exception {
        log.info("Triggering Webhook!!");
        MappedHttpRequest mappedHttpRequest = new MappedHttpRequest(CustomWebHookUtil.transformRawBodyToObject(payload.rawBody(), CustomWebHookUtil.extractContentType(payload.headers())), payload.headers(), payload.params());
        return new WebhookCustomResultImpl(mappedHttpRequest, responseExpression, null);
    }

    @Override
    public void activate(InboundConnectorContext context) throws Exception {
        log.info("Activating InboundConnector");
        this.context = context;
        var wrappedProps = context.bindProperties(CustomWebHookProperties.CustomWebHookPropertiesWrapper.class);
        properties = new CustomWebHookProperties(wrappedProps);
        context.reportHealth(Health.up());
    }

    @Override
    public void deactivate() throws Exception {
        log.debug("Deactivating Custom Webhook Connector");
        WebhookConnectorExecutable.super.deactivate();
    }
}
