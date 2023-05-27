package ru.practicum.shareit.request.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collection;
import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    Collection<ItemRequest> findAllByRequestorIdOrderByCreatedDesc(long useId);

    List<ItemRequest> findAllByRequestorIdNotOrderByCreatedDesc(long requestorId, Pageable pageable);

    Collection<ItemRequest> findAllByRequestorIdNotOrderByCreatedDesc(long requestorId);
}
