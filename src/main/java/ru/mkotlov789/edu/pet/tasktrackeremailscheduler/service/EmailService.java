package ru.mkotlov789.edu.pet.tasktrackeremailscheduler.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.mkotlov789.edu.pet.tasktrackeremailscheduler.dto.EmailDto;
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    @Value("${topic}")
    private String topic;

    private final KafkaTemplate<String , Object> kafkaTemplate;


    public void sendEmail(String emailAdress, String message) {
        log.info("sending "+message+ "to "+emailAdress);
        EmailDto emailDto = new EmailDto();
        emailDto.setEmailAdress(emailAdress);
        emailDto.setSubject("Daily notification");
        emailDto.setBody(message);

        kafkaTemplate.send(topic,emailDto.getEmailAdress(),emailDto);


    }
}
