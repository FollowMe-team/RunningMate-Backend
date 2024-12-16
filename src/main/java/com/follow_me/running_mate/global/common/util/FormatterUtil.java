package com.follow_me.running_mate.global.common.util;

import com.follow_me.running_mate.domain.course.dto.request.CourseRequest;
import org.locationtech.jts.geom.*;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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

    public static String formatDuration(LocalDateTime startTime, LocalDateTime endTime) {
        Duration duration = Duration.between(startTime, endTime);

        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    // MemberCount 포맷팅
    public static String formatMemberCount(Long count) {
        if (count == null) {
            return "0";
        }

        if (count < 1000) {
            return count.toString();
        } else if (count < 1000000) {
            return String.format("%.1fK", count / 1000.0).replaceAll("\\.0K$", "K");
        } else if (count < 1000000000) {
            return String.format("%.1fM", count / 1000000.0).replaceAll("\\.0M$", "M");
        } else {
            return String.format("%.1fB", count / 1000000000.0).replaceAll("\\.0B$", "B");
        }
    }

    // Rating 포맷팅
    public static Double formatRating(Double rating) {
        return rating != null ? Double.parseDouble(String.format("%.1f", rating)) : 0.0;
    }

    // 시간 포맷팅
    public static String formatTime(LocalDateTime createdAt) {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
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

    // Point 포맷팅
    public static Point formatPoint(CourseRequest.GeoPoint geoPoint) {
        return new GeometryFactory(new PrecisionModel(), 4326)
            .createPoint(new Coordinate(geoPoint.getLongitude(), geoPoint.getLatitude()));
    }

    // LineString 포맷팅
    public static LineString formatLineString(List<? extends CourseRequest.GeoPoint> points) {
        Coordinate[] coordinates = points.stream()
            .map(p -> new Coordinate(p.getLongitude(), p.getLatitude()))
            .toArray(Coordinate[]::new);
        return new GeometryFactory(new PrecisionModel(), 4326)
            .createLineString(coordinates);
    }

    // Duration 포맷팅
    public static Duration formatDuration(Double distance) {
        double averageSpeed = 10.0; // TODO: 평균 속도를 어떻게 계산할지 고민해보기
        double durationInHours = distance / averageSpeed;

        long hours = (long) durationInHours;
        long minutes = (long) ((durationInHours - hours) * 60);
        long seconds = (long) ((((durationInHours - hours) * 60) - minutes) * 60);

        return Duration.ofHours(hours).plusMinutes(minutes).plusSeconds(seconds);
    }

    // String 포맷팅
    public static String formatString(String str) {
        if (str == null) {
            return null;
        }
        return str.length() > 50 ? str.substring(0, 50) + "..." : str;
    }
}
