package net.engineeringdigest.journalApp.cache;

import net.engineeringdigest.journalApp.entity.ConfigJournalApp;
import net.engineeringdigest.journalApp.repository.ConfigJournalAppRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AppCache {

    public enum keys {
        WEATHER_API;
    }

    @Autowired
    private ConfigJournalAppRepository configJournalAppRepository;

    public Map<String, String> appCache;

    @PostConstruct
     public void init() {
        appCache = new HashMap<>();
        List<ConfigJournalApp> all = configJournalAppRepository.findAll();

        for (ConfigJournalApp configJournalApp: all) {
            appCache.put(configJournalApp.getKey(), configJournalApp.getValue());
        }
    }
}
