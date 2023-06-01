package ru.practicum.shareit.UnitTests.ControllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.InComingCommentDto;
import ru.practicum.shareit.item.dto.ItemBookingsDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.OutComingCommentDto;
import ru.practicum.shareit.item.service.ItemService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.constant.Constant.REQUEST_HEADER_USER_ID;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    private static final long userId = 1L;
    private final ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("itemDto")
            .description("test")
            .available(true).build();
    private final ItemBookingsDto itemBookingsDto = ItemMapper
            .toItemBookingsDto(ItemMapper.toItem(itemDto, new User()),
                    null, null, Collections.emptyList());


    @SneakyThrows
    @Test
    void create_whenItemDtoNotValid_thenBadRequest() {
        itemDto.setName("");

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).create(userId, itemDto);
    }

    @SneakyThrows
    @Test
    void create_whenItemDtoIsValid_thenItemDtoReturned() {
        when(itemService.create(userId, itemDto)).thenReturn(itemDto);

        String result = mockMvc.perform(post("/items")
                        .header(REQUEST_HEADER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemDto), result);
        verify(itemService).create(userId, itemDto);
    }

    @SneakyThrows
    @Test
    void update() {
        when(itemService.update(userId, itemDto.getId(), itemDto)).thenReturn(itemDto);

        String result = mockMvc.perform(patch("/items/{itemId}", itemDto.getId())
                        .header(REQUEST_HEADER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemDto), result);
        verify(itemService).update(userId, itemDto.getId(), itemDto);
    }

    @SneakyThrows
    @Test
    void findItemById() {

        when(itemService.findItemById(userId, itemDto.getId()))
                .thenReturn(itemBookingsDto);

        String result = mockMvc.perform(get("/items/{itemId}", itemDto.getId())
                        .header(REQUEST_HEADER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemBookingsDto), result);
        verify(itemService).findItemById(userId, itemDto.getId());
    }

    @SneakyThrows
    @Test
    void findItemsByOwner() {
        when(itemService.findItemsByOwner(userId, 1, 1)).thenReturn(List.of(itemBookingsDto));

        String result = mockMvc.perform(get("/items")
                        .header(REQUEST_HEADER_USER_ID, userId)
                        .param("from", "1")
                        .param("size", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(itemBookingsDto)), result);
        verify(itemService).findItemsByOwner(userId, 1, 1);
    }

    @SneakyThrows
    @Test
    void findItemForRent() {
        when(itemService.findItemForRent(userId, "text", 1, 1)).thenReturn(List.of(itemDto));

        String result = mockMvc.perform(get("/items/search")
                        .header(REQUEST_HEADER_USER_ID, userId)
                        .param("text", "text")
                        .param("from", "1")
                        .param("size", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(itemDto)), result);
        verify(itemService).findItemForRent(userId, "text", 1, 1);
    }

    @SneakyThrows
    @Test
    void createComment() {
        OutComingCommentDto outComingCommentDto = OutComingCommentDto.builder()
                .id(1L)
                .text("text")
                .authorName("author")
                .created(LocalDateTime.now()).build();
        InComingCommentDto inComingCommentDto = new InComingCommentDto();
        inComingCommentDto.setText("text");
        when(itemService.createComment(userId, itemDto.getId(), inComingCommentDto))
                .thenReturn(outComingCommentDto);

        String result = mockMvc.perform(post("/items/{itemId}/comment", itemDto.getId())
                        .header(REQUEST_HEADER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inComingCommentDto))
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(outComingCommentDto), result);
        verify(itemService).createComment(userId, itemDto.getId(), inComingCommentDto);

    }
}