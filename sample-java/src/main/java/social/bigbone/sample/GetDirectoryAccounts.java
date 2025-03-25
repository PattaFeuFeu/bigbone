package social.bigbone.sample;

import social.bigbone.MastodonClient;
import social.bigbone.api.entity.Account;
import social.bigbone.api.exception.BigBoneRequestException;
import social.bigbone.api.method.DirectoryMethods;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

public class GetDirectoryAccounts {
    public static void main(final String[] args) throws BigBoneRequestException {
        final String instance = args[0];

        // instantiate client
        final MastodonClient client = new MastodonClient.Builder(instance)
                .build();

        // get 40 local accounts that were recently active, skipping the first ten
        final List<Account> accounts = client.directories().listAccounts(
                true,
                DirectoryMethods.AccountOrder.ACTIVE,
                10,
                40
        ).execute();

        // do something with the result; here, we find the oldest account still active and output information about it
        final Account account = accounts.stream().min(Comparator.nullsLast(
                Comparator.comparing((Account acct) -> acct.getCreatedAt().mostPreciseInstantOrNull())
        )).orElseThrow(NoSuchElementException::new);

        System.out.println("@" + account.getAcct() + "@" + instance + " has posted "
                + account.getStatusesCount() + " times since " + account.getCreatedAt().mostPreciseInstantOrNull());
    }
}
