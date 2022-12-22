package social.bigbone.sample;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import social.bigbone.MastodonClient;
import social.bigbone.api.Pageable;
import social.bigbone.api.Range;
import social.bigbone.api.entity.Status;
import social.bigbone.api.exception.BigboneRequestException;
import social.bigbone.api.method.Public;

public class GetPublicTimelines {
    public static void main(String[] args) {
        MastodonClient client = new MastodonClient.Builder("mstdn.jp", new OkHttpClient.Builder(), new Gson()).build();
        Public publicMethod = new Public(client);

        try {
            Pageable<Status> statuses = publicMethod.getLocalPublic(new Range())
                    .doOnJson(System.out::println)
                    .execute();
            statuses.getPart().forEach(status -> {
                System.out.println("=============");
                System.out.println(status.getAccount().getDisplayName());
                System.out.println(status.getContent());
                System.out.println(status.isReblogged());
            });
        } catch (BigboneRequestException e) {
            e.printStackTrace();
        }
    }
}