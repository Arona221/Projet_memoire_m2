package connect.event.organisateur.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic ticketPurchasesTopic() {
        return TopicBuilder.name("ticket-purchases")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic salesAnalyticsTopic() {
        return TopicBuilder.name("sales-analytics")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
