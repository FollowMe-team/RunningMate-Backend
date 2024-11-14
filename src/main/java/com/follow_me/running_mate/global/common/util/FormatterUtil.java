package com.follow_me.running_mate.global.common.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

@Component
public class FormatterUtil {

    // location 포맷팅
    public static String formatLocation(String city, String district) {
        return String.format("%s %s", city, district);
    }

    // Duration 포맷팅
    public static String formatDuration(Duration duration) {
        long minutes = duration.toMinutes();

        if (minutes < 10) {
            return "10분 미만";
        } else if (minutes < 20) {
            return "10 ~ 20분";
        } else if (minutes < 30) {
            return "20 ~ 30분";
        } else if (minutes < 40) {
            return "30 ~ 40분";
        } else if (minutes < 50) {
            return "40 ~ 50분";
        } else if (minutes < 60) {
            return "50 ~ 60분";
        } else if (minutes < 120) {
            return "1시간 이상";
        } else if (minutes < 180) {
            return "2시간 이상";
        } else {
            return "3시간 이상";
        }
    }

    // Rating 포맷팅
    public static Double formatRating(Double rating) {
        return rating != null ? Double.parseDouble(String.format("%.1f", rating)) : 0.0;
    }

    // 시간 포맷팅
    public static String formatTime(LocalDateTime createdAt) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(createdAt, now);

        if (duration.getSeconds() < 60) {
            return duration.getSeconds() + "초 전";
        } else if (duration.toMinutes() < 60) {
            return duration.toMinutes() + "분 전";
        } else if (duration.toHours() < 24) {
            return duration.toHours() + "시간 전";
        } else if (duration.toDays() < 7) {
            return duration.toDays() + "일 전";
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return createdAt.format(formatter);
        }
    }
}
