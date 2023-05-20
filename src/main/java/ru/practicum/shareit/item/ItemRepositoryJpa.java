package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepositoryJpa extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerOrderById(User owner);

    Optional<Item> findById(Long itemId);

    Item save(Item itemFromDB);

    @Query("select item from Item item "
            + "where lower(item.name) like lower(concat('%', ?1, '%')) "
            + "or lower(item.description) like lower(concat('%', ?1, '%')) ")
    List<Item> searchItemsByText(String text, Pageable pageable);

    void deleteById(Long itemId);

}
