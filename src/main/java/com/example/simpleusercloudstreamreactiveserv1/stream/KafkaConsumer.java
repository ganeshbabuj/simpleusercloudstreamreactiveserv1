package com.example.simpleusercloudstreamreactiveserv1.stream;

import com.example.cloudstream.resource.Account;
import com.example.cloudstream.resource.AccountStatus;
import com.example.cloudstream.resource.RegistrationSummary;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
import org.apache.kafka.streams.state.Stores;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.util.function.Consumer;

@Configuration
public class KafkaConsumer {

    @Bean
    public Consumer<KStream<Long, Account>> updateUserStatus() {
        return kstream -> kstream.foreach((key, account) -> {
            System.out.println("RECEIVED ACCOUNT::::: " + key + " | " + account);
        });
    }

    @Bean
    public Consumer<KStream<Long, Account>> summarizeUserRegistrations() {
        KeyValueBytesStoreSupplier storeSupplier = Stores.persistentKeyValueStore(
                "all-registrations-store");
        return accounts -> accounts
                .peek((k, v) -> System.out.println("summarizeUserRegistrations - input: " + v))
                //KGroupedStream
                .groupBy((k, v) -> v.getAgeBand().toString(),
                        Grouped.with(Serdes.String(), new JsonSerde<>(Account.class)))
                //KTable
                .aggregate(
                        RegistrationSummary::new,
                        (k, v, a) -> {
                            a.setAgeBand(v.getAgeBand());
                            a.setTotalCount(a.getTotalCount() + 1);
                            if(v.getAccountStatus() == AccountStatus.ACTIVATED) {
                                a.setActivatedCount(a.getActivatedCount() + 1);
                            } else {
                                a.setRejectedCount(a.getRejectedCount() + 1);
                            }
                            return a;
                        },
                        Materialized.<String, RegistrationSummary> as(storeSupplier)
                                .withKeySerde(Serdes.String())
                                .withValueSerde(new JsonSerde<>(RegistrationSummary.class)))
                .toStream()
                .peek((k, v) -> System.out.println("summarizeUserRegistrations - processed: " + k + " : " + v));
    }


}
