package com.example.simpleusercloudstreamreactiveserv1.stream;

import com.example.cloudstream.resource.Account;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class AdAccountKafkaConsumer {

    @Bean
    public Consumer<KStream<String, Account>> updateUserStatus() {
        return kstream -> kstream.foreach((key, account) -> {
            System.out.println("RECEIVED ACCOUNT::::: " + account);
        });
    }

}
