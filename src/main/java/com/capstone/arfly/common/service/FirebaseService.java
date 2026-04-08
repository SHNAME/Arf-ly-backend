package com.capstone.arfly.common.service;


import com.capstone.arfly.common.dto.MedicationAlarmDto;
import com.capstone.arfly.common.exception.EmptyTokenException;
import com.capstone.arfly.common.exception.InvalidTokenException;
import com.capstone.arfly.common.exception.MissingTokenException;
import com.capstone.arfly.common.exception.TokenExpiredException;
import com.capstone.arfly.common.exception.TokenRevokedException;
import com.capstone.arfly.member.dto.PhoneAuthInfoDto;
import com.google.firebase.auth.AuthErrorCode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.Notification;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class FirebaseService {
    private final FirebaseMessaging messaging;
    private final FirebaseMessaging firebaseMessaging;

    //토큰을 검증하고 사용자의 정보를 추출
    public PhoneAuthInfoDto verifyTokenAndGetInfo(String token) {
        try {
            // 토큰 확인 및 형식 검증
            if (token == null || !token.startsWith("Bearer ")) {
                throw new InvalidTokenException();
            }

            // Bearer 제거
            String idToken = token.substring(7);
            if (idToken.isBlank()) {
                throw new EmptyTokenException();
            }

            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);

            String uid = decodedToken.getUid();
            String phoneNumber = (String) decodedToken.getClaims().get("phone_number");

            if (uid == null || uid.isBlank()||phoneNumber == null || phoneNumber.isBlank()) {
                throw new MissingTokenException();
            }

            return PhoneAuthInfoDto.builder().uid(uid).phoneNumber(phoneNumber).build();

        } catch (FirebaseAuthException e) {
            if (e.getAuthErrorCode() == AuthErrorCode.REVOKED_ID_TOKEN) {
                throw new TokenRevokedException();
            }
            if (e.getAuthErrorCode() == AuthErrorCode.EXPIRED_ID_TOKEN) {
                throw new TokenExpiredException();
            }
            if (e.getAuthErrorCode() == AuthErrorCode.INVALID_ID_TOKEN) {
                throw new InvalidTokenException();
            }
            throw new InvalidTokenException();
        }
    }


    //여러 건의 알림을 리스트로 받아 실제 푸시 알림을 발송
    public List<Long> sendAllNotifications(List<MedicationAlarmDto> alarmList){
        List<Long> failedTokenList = new ArrayList<>();
        for (MedicationAlarmDto alarm : alarmList) {
            //알람 생성
            Notification notification = Notification.builder()
                    .setTitle(alarm.title())
                    .setBody(alarm.content())
                    .build();
            //푸시 알림 메세지 생성
            Message message = Message.builder().setToken(alarm.token())
                    .setNotification(notification)
                    .build();
            try{
                firebaseMessaging.send(message);
                log.info("알림 발송 성공 - Target: {}, Title: {}", alarm.token(), alarm.title());
            }catch(FirebaseMessagingException e){
                //사용자가 앱을 삭제 혹은 알림 비활성화 처리 혹은 토큰 만료 시 발생
                if(e.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED){
                    log.error("알림 발송 실패(UNREGISTERED): Token: {}, Title: {}",alarm.token(), alarm.title());
                    failedTokenList.add(alarm.fcmTokenId());
                }
                //토큰 형식이 맞지 않거나 비어있는 경우 해당 예외 발생
                else if(e.getMessagingErrorCode() == MessagingErrorCode.INVALID_ARGUMENT){
                    log.error("알림 발송 실패(INVALID_ARGUMENT): Token: {}, Title{}",alarm.token(), alarm.title());
                    failedTokenList.add(alarm.fcmTokenId());
                }
                else{
                    log.error("알림 발송 실패(FIREBASE_SERVER_ERROR): Token: {}, Title{}",alarm.token(), alarm.title());
                }
            }


        }
        return failedTokenList;
    }


}