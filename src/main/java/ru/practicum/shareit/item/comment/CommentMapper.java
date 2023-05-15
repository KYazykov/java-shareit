package ru.practicum.shareit.item.comment;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getItem().getId(),
                comment.getAuthor().getName(),
                comment.getCreated());
    }

    public static Comment toComment(CommentDto commentDto, Item item) {
        return new Comment(
                commentDto.getId(),
                commentDto.getText(),
                item,
                item.getOwner(),
                commentDto.getCreated());
    }
}
