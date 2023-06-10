package ru.practicum.shareit.UnitTests.BookingTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.ShortBookingItemDto;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;


@JsonTest
public class ShortBookingItemDtoTest {

    @Autowired
    private JacksonTester<ShortBookingItemDto> jacksonTester;
    private static final LocalDateTime DATE_TIME = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

    @Test
    void testShortBookingItemDto() throws Exception {
        ShortBookingItemDto shortBookingItemDto = new ShortBookingItemDto(
                1L, 2L, DATE_TIME.truncatedTo(ChronoUnit.SECONDS),
                DATE_TIME.plusHours(1).truncatedTo(ChronoUnit.SECONDS));

        JsonContent<ShortBookingItemDto> result = jacksonTester.write(shortBookingItemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo(DATE_TIME.truncatedTo(ChronoUnit.SECONDS).toString());
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo(DATE_TIME.plusHours(1).truncatedTo(ChronoUnit.SECONDS).toString());
    }
}
