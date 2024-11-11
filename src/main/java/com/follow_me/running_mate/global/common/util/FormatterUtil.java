package com.follow_me.running_mate.global.common.util;

import java.time.Duration;
import org.springframework.stereotype.Component;

@Component
public class FormatterUtil {

    // location 포맷팅
    public static String formatLocation(String city, String district) {
        return String.format("(%s %s)", city, district);
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
        return rating != null ? Math.round(rating * 10) / 10.0 : 0.0;
    }
}
