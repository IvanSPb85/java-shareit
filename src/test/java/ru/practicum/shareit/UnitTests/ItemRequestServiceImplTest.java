package ru.practicum.shareit.UnitTests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestIncomingDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestOutComingDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    @Mock
    private UserService userService;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    ItemRequestServiceImpl itemRequestService;
    private final UserDto userDto = new UserDto(1L, "user", "user@mail.ru");
    private final ItemRequestIncomingDto itemRequestIncomingDto = ItemRequestIncomingDto
            .builder()
            .description("desc")
            .requestor(new User())
            .build();
    private final ItemRequest itemRequest = ItemRequest.builder()
            .id(1L)
            .requestor(UserMapper.toUser(userDto))
            .description("test")
            .created(LocalDateTime.now()).build();
    private final ItemRequest itemRequest2 = ItemRequest.builder()
            .id(2L)
            .requestor(UserMapper.toUser(userDto))
            .description("tes2")
            .created(LocalDateTime.now()).build();

    @Test
    void create_whenFoundUser_thenReturnedItemRequest() {
        when(userService.findUser(userDto.getId())).thenReturn(userDto);
        User user = UserMapper.toUser(userDto);
        ItemRequest expected = ItemRequest.builder()
                .description("desc")
                .created(itemRequestIncomingDto.getCreated()).build();
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestIncomingDto, user);
        when(itemRequestRepository.save(itemRequest)).thenReturn(expected);

        ItemRequestOutComingDto actual = itemRequestService.create(userDto.getId(), itemRequestIncomingDto);

        assertEquals(ItemRequestMapper.toItemRequestOutcomingDto(expected, Collections.emptyList()), actual);
        verify(itemRequestRepository).save(itemRequest);
    }

    @Test
    void create_whenNotFoundUser_thenInvalidParameterExceptionThrown() {
        when(userService.findUser(userDto.getId())).thenThrow(InvalidParameterException.class);

        InvalidParameterException exception = assertThrows(InvalidParameterException.class,
                () -> itemRequestService.create(userDto.getId(), itemRequestIncomingDto));

        assertEquals("Нельзя создать запрос от несуществующего пользователя с id = 1",
                exception.getMessage());
    }

    @Test
    void getOwnRequests_whenFoundUser_thenReturnedCollection() {
        when(userService.findUser(userDto.getId())).thenReturn(userDto);
        when(itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(userDto.getId()))
                .thenReturn(List.of(itemRequest));

        Collection<ItemRequestOutComingDto> requestCollection = itemRequestService.getOwnRequests(userDto.getId());

        assertEquals(1, requestCollection.size());
        assertTrue(requestCollection.contains(ItemRequestMapper
                .toItemRequestOutcomingDto(itemRequest, Collections.emptyList())));
    }

    @Test
    void getAllRequestsPagination_whenFrom1andSize1_thenReturnedItemRequest2() {
        when(itemRequestRepository.findAllByRequestorIdNotOrderByCreatedDesc(
                2L, PageRequest.of(1, 1))).thenReturn(List.of(itemRequest2));

        Collection<ItemRequestOutComingDto> request = itemRequestService
                .getAllRequestsPagination(2L, 1, 1);

        assertEquals(1, request.size());
        assertTrue(request.contains(ItemRequestMapper
                .toItemRequestOutcomingDto(itemRequest2, Collections.emptyList())));
    }

    @Test
    void getRequestById_whenItemRequestNotFound_thenInvalidParameterExceptionThrown() {
        when(userService.findUser(userDto.getId())).thenReturn(userDto);
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.empty());

        InvalidParameterException exception = assertThrows(InvalidParameterException.class,
                () -> itemRequestService.getRequestById(1L, 1L));

        assertEquals("Запрос с id = 1 не найден.", exception.getMessage());
    }

    @Test
    void getRequestById_whenItemRequestFound_thenReturnedItemRequestOutComingDto() {
        when(userService.findUser(userDto.getId())).thenReturn(userDto);
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findAllByRequestIdIn(List.of(itemRequest.getId())))
                .thenReturn(Collections.emptyList());

        ItemRequestOutComingDto request = itemRequestService.getRequestById(1l, itemRequest.getId());

        assertEquals(request, ItemRequestMapper.toItemRequestOutcomingDto(itemRequest, Collections.emptyList()));
    }
}