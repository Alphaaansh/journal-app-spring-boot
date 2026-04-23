package journalApp.Entity;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true ,nullable = false)
    private String name;

    @NotNull
    private String password;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private List<JournalEntry> journalEntries=new ArrayList<>();

    @ElementCollection
    private List<String>roles =new ArrayList<>();
}
