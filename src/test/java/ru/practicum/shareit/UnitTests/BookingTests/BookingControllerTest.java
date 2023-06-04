package ru.practicum.shareit.UnitTests.BookingTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.constant.State;
import ru.practicum.shareit.constant.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.constant.Constant.REQUEST_HEADER_USER_ID;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    private static final long userId = 1L;
    private final User user = new User(1L, "user", "user@mail");
    private final Item item = Item.builder()
            .id(1L)
            .name("item")
            .description("testItem")
            .owner(user)
            .available(true).build();
    private final BookingDto bookingDto = BookingDto.builder()
            .itemId(1L)
            .start(LocalDateTime.now().plusHours(1L))
            .end(LocalDateTime.now().plusHours(2L)).build();
    private final Booking booking = BookingMapper.toBooking(bookingDto, item, user, Status.WAITING);
    private static final LocalDateTime DATE_TIME = LocalDateTime.now();


    @SneakyThrows
    @Test
    void create_whenNotValidBookingDto_thenBadRequest() {
        bookingDto.setStart(LocalDateTime.now().minusHours(1L));

        mockMvc.perform(post("/bookings")
                        .header(REQUEST_HEADER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).create(userId, bookingDto);
    }

    @SneakyThrows
    @Test
    void create_whenValidBookingDto_thenBookingItemDtoReturned() {
        when(bookingService.create(userId, bookingDto)).thenReturn(BookingMapper.toBookingItemDto(booking));

        String result = mockMvc.perform(post("/bookings")
                        .header(REQUEST_HEADER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(BookingMapper.toBookingItemDto(booking)), result);
        verify(bookingService).create(userId, bookingDto);
    }

    @SneakyThrows
    @Test
    void approve() {
        booking.setId(1L);
        when(bookingService.approve(userId, booking.getId(), true))
                .thenReturn(BookingMapper.toBookingItemDto(booking));

        String result = mockMvc.perform(patch("/bookings/{bookingId}", userId)
                        .param("approved", "true")
                        .header(REQUEST_HEADER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(BookingMapper.toBookingItemDto(booking)), result);
        verify(bookingService).approve(userId, booking.getId(), true);
    }

    @SneakyThrows
    @Test
    void getBookingById() {
        booking.setId(1L);
        when(bookingService.findBookingById(userId, booking.getId()))
                .thenReturn(BookingMapper.toBookingItemDto(booking));

        String result = mockMvc.perform(get("/bookings/{bookingId}", userId)
                        .header(REQUEST_HEADER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(BookingMapper.toBookingItemDto(booking)), result);
        verify(bookingService).findBookingById(userId, booking.getId());
    }

    @SneakyThrows
    @Test
    void getAllBookingsByOwner() {
        booking.setId(1L);
        when(bookingService.findAllBookingsByOwner(userId, String.valueOf(State.ALL), 1, 1))
                .thenReturn(List.of(BookingMapper.toBookingItemDto(booking)));

        String result = mockMvc.perform(get("/bookings/owner")
                        .header(REQUEST_HEADER_USER_ID, userId)
                        .param("state", String.valueOf(State.ALL))
                        .param("from", "1")
                        .param("size", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(BookingMapper.toBookingItemDto(booking))), result);
        verify(bookingService).findAllBookingsByOwner(userId, String.valueOf(State.ALL), 1, 1);
    }
}