package social.bigbone.sample;

import social.bigbone.MastodonClient;
import social.bigbone.api.Pageable;
import social.bigbone.api.entity.Conversation;
import social.bigbone.api.exception.BigBoneRequestException;

public class GetConversations {
    public static void main(final String[] args) throws BigBoneRequestException {
        final String instance = args[0];

        // access token with at least Scope.READ.STATUSES
        final String accessToken = args[1];

        // Instantiate client
        final MastodonClient client = new MastodonClient.Builder(instance)
                .accessToken(accessToken)
                .build();

        // Get conversations
        final Pageable<Conversation> conversations = client.conversations().getConversations().execute();
        conversations.getPart().forEach(conversation -> {
            String lastStatusText = conversation.getLastStatus() != null ? conversation.getLastStatus().getContent() + "\n" : "n/a\n";
            System.out.print(conversation.getId() + " " + lastStatusText);
        });
    }
}
