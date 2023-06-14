package ru.practicum.shareit.item.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;


public interface CommentRepositoryJpa extends JpaRepository<Comment, Long> {

    List<Comment> findAllByItem_Id(Long itemId);

    List<Comment> findAllByItemOrderById(Item item);

    Comment save(Comment commentForSave);
}
