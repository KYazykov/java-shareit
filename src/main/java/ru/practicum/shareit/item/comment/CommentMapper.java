package ru.practicum.shareit.item.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.ItemRepositoryJpa;

@Component
@RequiredArgsConstructor
public class CommentMapper {
    private final ItemRepositoryJpa itemRepositoryJpa;

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getItem().getId(),
                comment.getAuthor().getName(),
                comment.getCreated());
    }

    public Comment toComment(CommentDto commentDto) {
        return new Comment(
                commentDto.getId(),
                commentDto.getText(),
                itemRepositoryJpa.getReferenceById(commentDto.getItemId()),
                itemRepositoryJpa.getReferenceById(commentDto.getItemId()).getOwner(),
                commentDto.getCreated());
    }
}
