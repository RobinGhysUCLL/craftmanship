package be.ucll.group8.craftmanshipgroep8.chats.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.ChatCompletionCreateParams;

import be.ucll.group8.craftmanshipgroep8.chats.domain.Message;

@Service
public class AiService {
    private static final String DEFAULT_ERROR_MESSAGE = "Er ging iets mis aan mijn kant. Probeer het later nog eens.";
    private final OpenAIClient client;

    public AiService(
            @Value("${openai.api.key}") String apiKey) {
        this.client = OpenAIOkHttpClient.builder()
                .apiKey(apiKey)
                .build();
    }

    public String generateReply(List<Message> messages, String newUserMessageText) {
        final var sb = new StringBuilder();
        for (var m : messages) {
            if (m.getMessage() == null)
                continue;

            if (Boolean.TRUE.equals(m.getAi())) {
                sb.append("AI: ");
            } else {
                sb.append("User: ");
            }
            sb.append(m.getMessage()).append("\n");
        }

        sb.append("User: ").append(newUserMessageText);

        final var fullPrompt = sb.toString();
        final var params = ChatCompletionCreateParams.builder()
                .model(ChatModel.GPT_4_1_MINI)
                .addUserMessage(fullPrompt)
                .build();

        try {
            final var completion = client.chat()
                    .completions()
                    .create(params);

            if (completion.choices().isEmpty())
                return DEFAULT_ERROR_MESSAGE;

            return completion.choices()
                    .get(0)
                    .message()
                    .content()
                    .orElse(DEFAULT_ERROR_MESSAGE);
        } catch (Exception e) {
            return DEFAULT_ERROR_MESSAGE;
        }
    }
}
