package ru.practicum.shareit.UnitTests.ControllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestIncomingDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestOutComingDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.constant.Constant.REQUEST_HEADER_USER_ID;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    private final ItemRequestIncomingDto itemRequestIncomingDto = ItemRequestIncomingDto
            .builder()
            .description("desc")
            .requestor(new User())
            .build();
    private final ItemRequest itemRequest = ItemRequest.builder()
            .id(1L)
            .requestor(new User())
            .description("desc")
            .created(LocalDateTime.now()).build();
    private static final long userId = 1L;
    private final ItemRequestOutComingDto itemRequestOutComingDto = ItemRequestMapper
            .toItemRequestOutcomingDto(itemRequest, Collections.emptyList());

    @SneakyThrows
    @Test
    void create_whenRequestNotValid_thenReturnedBadRequest() {
        itemRequestIncomingDto.setDescription("");

        mockMvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(itemRequestIncomingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemRequestService, never()).create(userId, itemRequestIncomingDto);
    }

    @SneakyThrows
    @Test
    void create_whenRequestValid_thenReturnedItemRequestOutComingDto() {
        when(itemRequestService.create(userId, itemRequestIncomingDto))
                .thenReturn(itemRequestOutComingDto);

        String result = mockMvc.perform(post("/requests")
                        .header(REQUEST_HEADER_USER_ID, userId)
                        .content(objectMapper.writeValueAsString(itemRequestIncomingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemRequestOutComingDto), result);
        verify(itemRequestService).create(userId, itemRequestIncomingDto);
    }

    @SneakyThrows
    @Test
    void getOwnRequests() {
        when(itemRequestService.getOwnRequests(userId))
                .thenReturn(List.of(itemRequestOutComingDto));

        String result = mockMvc.perform(get("/requests")
                        .header(REQUEST_HEADER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(itemRequestOutComingDto)), result);
        verify(itemRequestService).getOwnRequests(userId);
    }

    @SneakyThrows
    @Test
    void getAllRequests() {
        when(itemRequestService.getAllRequestsPagination(userId, 1, 1))
                .thenReturn(List.of(itemRequestOutComingDto));

        String result = mockMvc.perform(get("/requests/all")
                        .header(REQUEST_HEADER_USER_ID, userId)
                        .param("from", "1")
                        .param("size", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(itemRequestOutComingDto)), result);
        verify(itemRequestService).getAllRequestsPagination(userId, 1, 1);
    }

    @SneakyThrows
    @Test
    void getRequestById() {
        when(itemRequestService.getRequestById(userId, itemRequest.getId()))
                .thenReturn(itemRequestOutComingDto);

        String result = mockMvc.perform(get("/requests/{requestId}", itemRequest.getId())
                        .header(REQUEST_HEADER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemRequestOutComingDto), result);
        verify(itemRequestService).getRequestById(userId, itemRequest.getId());
    }
}