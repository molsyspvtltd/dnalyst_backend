//package com.example.app_server.notifications;
//
//import com.google.api.client.util.Value;
//import com.google.firebase.messaging.*;
//import org.springframework.stereotype.Service;
//import software.amazon.awssdk.services.sns.SnsClient;
//import software.amazon.awssdk.services.sns.model.CreatePlatformEndpointRequest;
//import software.amazon.awssdk.services.sns.model.CreatePlatformEndpointResponse;
//import software.amazon.awssdk.services.sns.model.PublishRequest;
//import software.amazon.awssdk.services.sns.model.PublishResponse;
//
//@Service
//public class SnsPushNotificationService {
//
//    private final SnsClient snsClient;
//
//    @Value("${aws.sns.platformApplicationArn}")
//    private String platformApplicationArn;
//
//    public SnsPushNotificationService(SnsClient snsClient) {
//        this.snsClient = snsClient;
//    }
//
//    public String registerDevice(String deviceToken) {
//        CreatePlatformEndpointRequest request = CreatePlatformEndpointRequest.builder()
//                .platformApplicationArn(platformApplicationArn)
//                .token(deviceToken)
//                .build();
//
//        CreatePlatformEndpointResponse response = snsClient.createPlatformEndpoint(request);
//        return response.endpointArn();
//    }
//
//    public String sendNotification(String endpointArn, String title, String body) {
//        String payload = "{\n" +
//                "  \"default\": \"" + body + "\",\n" +
//                "  \"GCM\": \"{\\\"notification\\\":{\\\"title\\\":\\\"" + title + "\\\",\\\"body\\\":\\\"" + body + "\\\"}}\"\n" +
//                "}";
//
//        PublishRequest request = PublishRequest.builder()
//                .messageStructure("json")
//                .message(payload)
//                .targetArn(endpointArn)
//                .build();
//
//        PublishResponse response = snsClient.publish(request);
//        return response.messageId();
//    }
//}
//
