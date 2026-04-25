package journalApp.Service;

import journalApp.Entity.User;
import journalApp.Repository.JournalEntryRepository;
import journalApp.Entity.JournalEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class JournalEntryService {

    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @Autowired
    private UserService userService;

    @Transactional
    public void saveEntry(JournalEntry journalEntry, String name){
        User user=userService.findByName(name);
        journalEntry.setDate(LocalDateTime.now());
        journalEntry.setUser(user);

        JournalEntry saved=journalEntryRepository.save(journalEntry);
        user.getJournalEntries().add(saved);

        userService.saveUser(user);
    }



    @Transactional
    public JournalEntry updateEntry(Long myId, JournalEntry newEntry, String name) {
        User user=userService.findByName(name);

        List<JournalEntry> collect=user.getJournalEntries().stream()
                .filter(x ->x.getId().equals(myId))
                .collect(Collectors.toList());

        if (!collect.isEmpty()){
            Optional<JournalEntry>journalEntry=journalEntryRepository.findById(myId);
            if (journalEntry.isPresent()){
                JournalEntry old=journalEntry.get();
                old.setTitle(newEntry.getTitle()!=null && !newEntry.getTitle().equals("")?newEntry.getTitle():old.getTitle());
                old.setContent(newEntry.getContent()!=null && !newEntry.getContent().equals("")?newEntry.getContent():old.getContent());
                journalEntryRepository.save(old);
                return old;
            }
        }
        return null;
    }

    @Transactional
    public boolean deleteById(Long id, String name){
        User user=userService.findByName(name);
        boolean removed=user.getJournalEntries().removeIf(x ->x.getId().equals(id));

        if (removed){
            userService.saveUser(user);
            journalEntryRepository.deleteById(id);
        }
        return removed;
    }

    public List <JournalEntry> getAll(){
        return journalEntryRepository.findAll();
    }

    public Optional<JournalEntry>findById(Long id){
        return journalEntryRepository.findById(id);
    }

    public List<JournalEntry> findByName(String name){
        User user=userService.findByName(name);

        return user.getJournalEntries();
    }

    public Optional<JournalEntry>findByIdAndUser(Long myId,String name){
        User user=userService.findByName(name);

        boolean ownEntry=user.getJournalEntries().stream().anyMatch(x->x.getId().equals(myId));

        if (ownEntry){
            return journalEntryRepository.findById(myId);
        }
        return Optional.empty();
    }
}
