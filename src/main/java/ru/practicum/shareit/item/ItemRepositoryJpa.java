package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
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


    void deleteById(Long itemId);

}
