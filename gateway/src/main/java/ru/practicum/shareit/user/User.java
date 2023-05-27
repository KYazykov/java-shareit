package src.main.java.ru.practicum.shareit.user;

import lombok.*;
import src.main.java.ru.practicum.shareit.booking.Booking;
import src.main.java.ru.practicum.shareit.item.comment.Comment;
import src.main.java.ru.practicum.shareit.item.model.Item;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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

    @Email(message = "Почта не соответствует формату.")
    @NotBlank(message = "Поле имени не может быть пустым.")
    @NotNull
    @EqualsAndHashCode.Exclude
    @Column(name = "email", nullable = false)
    private String email;

    @NotBlank(message = "Поле имени не может быть пустым.")
    @EqualsAndHashCode.Exclude
    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "booker")
    private List<Booking> bookings;
    @OneToMany(mappedBy = "author")
    private List<Comment> comments;

}
