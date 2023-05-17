package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constant.Constant;
import ru.practicum.shareit.item.dto.InComingCommentDto;
import ru.practicum.shareit.item.dto.ItemBookingsDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.OutComingCommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.service.ItemService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import java.util.Collection;

import static ru.practicum.shareit.constant.Constant.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemDto> create(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                                          @RequestBody @Valid ItemDto itemDto, HttpServletRequest request) {
        log.info(REQUEST_POST_LOG, request.getRequestURI());
        return new ResponseEntity<>(itemService.create(userId, itemDto), HttpStatus.OK);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> update(@RequestHeader(REQUEST_HEADER_USER_ID) long userId, @PathVariable long itemId,
                                          @RequestBody ItemDto itemDto, HttpServletRequest request) {
        log.info(REQUEST_PATCH_LOG, request.getRequestURI());
        return new ResponseEntity<>(itemService.update(userId, itemId, itemDto), HttpStatus.OK);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemBookingsDto> findItemById(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                                                        @PathVariable long itemId, HttpServletRequest request) {
        log.info(REQUEST_GET_LOG, request.getRequestURI());
        return new ResponseEntity<>(itemService.findItemById(userId, itemId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Collection<ItemBookingsDto>> findItemsByOwner(@RequestHeader(REQUEST_HEADER_USER_ID)
                                                                        long userId, HttpServletRequest request) {
        log.info(REQUEST_GET_LOG, request.getRequestURI());
        return new ResponseEntity<>(itemService.findItemsByOwner(userId), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<Collection<ItemDto>> findItemForRent(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                                                               @RequestParam(name = "text") String itemName,
                                                               HttpServletRequest request) {
        log.info(REQUEST_GET_LOG, request.getRequestURI());
        return new ResponseEntity<>(itemService.findItemForRent(userId, itemName), HttpStatus.OK);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<OutComingCommentDto> createComment(@RequestHeader(REQUEST_HEADER_USER_ID) long userId,
                                                             @PathVariable long itemId,
                                                             @RequestBody @Valid InComingCommentDto inComingCommentDto,
                                                             HttpServletRequest request) {
        log.info(REQUEST_POST_LOG, request.getRequestURI());
        return new ResponseEntity<OutComingCommentDto>(itemService.createComment(
                userId, itemId, inComingCommentDto), HttpStatus.OK);
    }
}
