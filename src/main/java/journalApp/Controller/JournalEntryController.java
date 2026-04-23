package journalApp.Controller;

import journalApp.Entity.User;
import journalApp.Repository.JournalEntryRepository;
import journalApp.Service.JournalEntryService;
import journalApp.Entity.JournalEntry;
import journalApp.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/journal")
public class JournalEntryController {

    @Autowired
    private JournalEntryService journalEntryService;

    @Autowired
    private JournalEntryRepository journalEntryRepository;
    
    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<?> getAllJournalEntriesOfUser(){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String name=authentication.getName();
        User user = userService.findByName(name);
        List<JournalEntry> all= user.getJournalEntries();
        if (all!=null && !all.isEmpty())
            return new ResponseEntity<>(all,HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<?> createEntry(@RequestBody JournalEntry myEntry){
        try {
            Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
            String name=authentication.getName();

            journalEntryService.saveEntry(myEntry,name);
            return new ResponseEntity<>(myEntry,HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/id/{myId}")
    public ResponseEntity<?> getJournalEntryById(@PathVariable Long myId)
    {
        Authentication authentication=SecurityContextHolder.createEmptyContext().getAuthentication();
        String name=authentication.getName();

        Optional<JournalEntry>journalEntry=journalEntryService.findByIdAndUser(myId, name);
        if (journalEntry.isPresent()){
            return new ResponseEntity<>(journalEntry.get(),HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/id/{myId}")
    public ResponseEntity<?> deleteJournalEntryByID(@PathVariable Long myId)
    {
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        String name= authentication.getName();
        boolean removed=journalEntryService.deleteById(myId,name);
        if (removed){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Transactional
    @PutMapping("id/{myId}")
    public ResponseEntity<?> updateJournalEntryById(@PathVariable Long myId,
                                                    @RequestBody JournalEntry newEntry)
    {
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        String name=authentication.getName();
        User user=userService.findByName(name);
        List<JournalEntry>collect=user.getJournalEntries().stream().filter(x->x.getId().equals(myId)).collect(Collectors.toList());

        if (collect != null){
            Optional<JournalEntry>journalEntry=journalEntryService.findById(myId);
            if (journalEntry.isPresent()){
                JournalEntry old=journalEntry.get();

                if (newEntry.getTitle()!=null && !newEntry.getTitle().equals("")){
                    old.setTitle(newEntry.getTitle());
                }
                old.setDate(LocalDateTime.now());

                if (newEntry.getContent()!=null && !newEntry.getContent().equals("")){
                    old.setContent(newEntry.getContent());
                }

                journalEntryService.saveEntry(old, name);
                return new ResponseEntity<>(old,HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
