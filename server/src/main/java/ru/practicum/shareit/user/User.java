package ru.practicum.shareit.user;

import lombok.*;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.model.Item;

import javax.persistence.*;
import java.util.List;

@Entity
@Builder(toBuilder = true)
@Table(name = "users")
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private Long id;

    @OneToMany(mappedBy = "owner")
    private List<Item> userItems;


    @EqualsAndHashCode.Exclude
    @Column(name = "email", nullable = false)
    private String email;


    @EqualsAndHashCode.Exclude
    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "booker")
    private List<Booking> bookings;
    @OneToMany(mappedBy = "author")
    private List<Comment> comments;

}
