package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

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
    public ResponseEntity<ItemDto> create(@RequestHeader("X-Later-User-Id") long userId,
                                          @RequestBody @Valid ItemDto itemDto,
                                          HttpServletRequest request) {
        log.info(REQUEST_POST_LOG, request.getRequestURI());
        return new ResponseEntity<>(itemService.create(itemDto, userId), HttpStatus.OK);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> update(@RequestHeader("X-Later-User-Id") long userId, @PathVariable long itemId,
                                          @RequestBody ItemDto itemDto, HttpServletRequest request) {
        log.info(REQUEST_PATCH_LOG, request.getRequestURI());
        return new ResponseEntity<>(itemService.update(itemDto, userId), HttpStatus.OK);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> findItemById(@RequestHeader("X-Later-User-Id") long userId,
                                                @PathVariable long itemId, HttpServletRequest request) {
        log.info(REQUEST_GET_LOG, request.getRequestURI());
        return new ResponseEntity<>(itemService.findItemById(itemId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Collection<ItemDto>> findItemsByOwner(@RequestHeader("X-Later-User-Id") long userId,
                                                                HttpServletRequest request) {
        log.info(REQUEST_GET_LOG, request.getRequestURI());
        return new ResponseEntity<>(itemService.findItemsByOwner(userId), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<Collection<ItemDto>> findItemForRent(@RequestParam String itemName,
                                                               HttpServletRequest request) {
        log.info(REQUEST_GET_LOG, request.getRequestURI());
        return new ResponseEntity<>(itemService.findItemForRent(itemName), HttpStatus.OK);
    }
}
