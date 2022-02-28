package com.microserviceslearning.customer;

import com.microserviceslearning.amqp.RabbitMQMessageProducer;
import com.microserviceslearning.clients.fraud.FraudCheckResponse;
import com.microserviceslearning.clients.fraud.FraudClient;
import com.microserviceslearning.clients.notification.NotificationRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final RestTemplate restTemplate;
    private final FraudClient fraudClient;
    private final RabbitMQMessageProducer rabbitMQMessageProducer;

    public CustomerService(CustomerRepository customerRepository, RestTemplate restTemplate,
                           FraudClient fraudClient, RabbitMQMessageProducer rabbitMQMessageProducer) {

        this.customerRepository = customerRepository;
        this.restTemplate = restTemplate;
        this.fraudClient = fraudClient;
        this.rabbitMQMessageProducer = rabbitMQMessageProducer;
    }

    public void registerCustomer(CustomerRegistrationRequest request) {
        Customer customer = Customer.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .build();

        customerRepository.saveAndFlush(customer);
       /*  FraudCheckResponse fraudCheckResponse = restTemplate.getForObject(
               // "http://localhost:8082/api/v1/fraud-check/{customerId}", //Without Eureka Server
                 "http://FRAUD/api/v1/fraud-check/{customerId}",
                FraudCheckResponse.class,
                customer.getId()
        );*/

        FraudCheckResponse fraudCheckResponse = fraudClient.isFraudster(customer.getId());

        if (fraudCheckResponse.isFraudster()) {
            throw new IllegalStateException("fraudster");
        }

        NotificationRequest notificationRequest = new NotificationRequest(
                customer.getId(),
                customer.getEmail(),
                String.format("Hi %s, welcome to microserviceslearning...",
                        customer.getFirstName())
        );
        rabbitMQMessageProducer.publish(
                notificationRequest,
                "internal.exchanges",
                "internal.notification.routing-key"
        );
    }
}
