package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {
    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getUserItems(),
                user.getEmail(),
                user.getName(),
                user.getBookings(),
                user.getComments());
    }

    public static User toUser(UserDto userDto) {
        return new User(
                userDto.getId(),
                userDto.getUserItems(),
                userDto.getEmail(),
                userDto.getName(),
                userDto.getBookings(),
                userDto.getComments());
    }

    public static UserForResponseDto toUserForResponse(User user) {
        return new UserForResponseDto(
                user.getId(),
                user.getName());
    }

    public static UserOnlyWithIdDto toUserOnlyWithIdDto(User user) {
        return new UserOnlyWithIdDto(user.getId());
    }
}
