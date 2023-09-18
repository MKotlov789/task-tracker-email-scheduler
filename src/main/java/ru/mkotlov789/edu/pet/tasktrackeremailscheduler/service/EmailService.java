package ru.mkotlov789.edu.pet.tasktrackeremailscheduler.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.mkotlov789.edu.pet.tasktrackeremailscheduler.dto.EmailDto;

/**
 * EmailService is a service class responsible for sending email notifications using Kafka messaging.
 * It sends email messages with the provided subject and body to the specified email address.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {
    /**
     * The Kafka topic to which email messages will be sent.
     */
    @Value("${topic}")
    private String topic;

    private final KafkaTemplate<String , Object> kafkaTemplate;

    /**
     * Sends an email notification to the specified email address with the given message.
     *
     * @param emailAddress The recipient's email address.
     * @param message      The email message body.
     */

    public void sendEmail(String emailAddress, String message) {
        log.info("sending "+message+ "to "+emailAddress);
        EmailDto emailDto = new EmailDto();
        emailDto.setEmailAdress(emailAddress);
        emailDto.setSubject("Daily notification");
        emailDto.setBody(message);

        kafkaTemplate.send(topic,emailDto.getEmailAdress(),emailDto);


    }
}
