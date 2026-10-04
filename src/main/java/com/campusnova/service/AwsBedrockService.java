package com.campusnova.service;

import com.campusnova.config.AiProperties;
import com.campusnova.knowledge.RetrievalResult;
import java.time.Duration;
import java.util.*;
import org.slf4j.*;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.*;
import software.amazon.awssdk.services.bedrockruntime.model.*;

/** AWS SDK v2 implementation. Disabled or incomplete configuration is always a no-op. */
@Service public class AwsBedrockService implements BedrockService {
    private static final Logger log=LoggerFactory.getLogger(AwsBedrockService.class); private final AiProperties properties; private final PromptService prompts;
    public AwsBedrockService(AiProperties p,PromptService s){properties=p;prompts=s;}
    public Optional<String> generate(String question,RetrievalResult context){if(!properties.isEnabled()||blank(properties.getModelId())||blank(properties.getRegion()))return Optional.empty();try(BedrockRuntimeClient client=BedrockRuntimeClient.builder().region(Region.of(properties.getRegion())).overrideConfiguration(ClientOverrideConfiguration.builder().apiCallTimeout(Duration.ofSeconds(25)).apiCallAttemptTimeout(Duration.ofSeconds(20)).build()).build()){ConverseRequest.Builder request=ConverseRequest.builder().modelId(properties.getModelId()).system(SystemContentBlock.builder().text(prompts.systemPrompt()).build()).messages(Message.builder().role(ConversationRole.USER).content(ContentBlock.fromText(prompts.userPrompt(question,context))).build()).inferenceConfig(InferenceConfiguration.builder().maxTokens(400).temperature(.2f).build());if(!blank(properties.getGuardrailId())&&!blank(properties.getGuardrailVersion()))request.guardrailConfig(GuardrailConfiguration.builder().guardrailIdentifier(properties.getGuardrailId()).guardrailVersion(properties.getGuardrailVersion()).build());ConverseResponse response=client.converse(request.build());for(ContentBlock block:response.output().message().content())if(block.text()!=null)return Optional.of(block.text().trim());}catch(Exception ex){log.warn("Bedrock generation unavailable: {}",ex.getClass().getSimpleName());}return Optional.empty();}
    private boolean blank(String v){return v==null||v.trim().isEmpty();}
}
