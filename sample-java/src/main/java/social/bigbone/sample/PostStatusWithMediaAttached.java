package social.bigbone.sample;

import social.bigbone.MastodonClient;
import social.bigbone.api.entity.MediaAttachment;
import social.bigbone.api.entity.data.Visibility;
import social.bigbone.api.exception.BigBoneRequestException;
import social.bigbone.api.method.FileAsMediaAttachment;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class PostStatusWithMediaAttached {
    public static void main(final String[] args) throws BigBoneRequestException {
        final String instance = args[0];

        // access token with at least Scope.WRITE.MEDIA and Scope.WRITE.STATUSES
        final String accessToken = args[1];

        // Instantiate client
        final MastodonClient client = new MastodonClient.Builder(instance)
                .accessToken(accessToken)
                .build();

        // Read file from resources folder
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        final File uploadFile = new File(classLoader.getResource("castle.jpg").getFile());

        // Upload image to Mastodon
        final MediaAttachment uploadedFile = client.media().uploadMediaAsync(
                new FileAsMediaAttachment(uploadFile, "image/jpg")
        ).execute();
        final String mediaId = uploadedFile.getId();

        // Post status with media attached
        final String statusText = "Status posting test";
        final List<String> mediaIds = Collections.singletonList(mediaId);
        final Visibility visibility = Visibility.PRIVATE;
        final String inReplyToId = null;
        final boolean sensitive = false;
        final String spoilerText = "A castle";
        final String language = "en";
        client.statuses().postStatus(statusText, mediaIds, visibility, inReplyToId, sensitive, spoilerText, language).execute();
    }
}
