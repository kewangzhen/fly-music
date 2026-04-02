package com.example.flymusic.config;

import com.example.flymusic.service.CoverExtractService;
import com.example.flymusic.repository.SongRepository;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@ConditionalOnProperty(name = "flymusic.cover-extraction.enabled", havingValue = "true", matchIfMissing = false)
public class CoverExtractionRunner implements CommandLineRunner {

    @Autowired
    private CoverExtractService coverExtractService;

    @Autowired
    private EntityManager entityManager;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        List<Object[]> songs = entityManager.createQuery(
            "SELECT s.id, s.url FROM Song s WHERE s.cover IS NULL AND s.url IS NOT NULL"
        ).getResultList();

        if (songs.isEmpty()) {
            System.out.println("No songs without cover found");
            return;
        }

        System.out.println("Found " + songs.size() + " songs without cover, starting extraction...");

        int updatedCount = 0;
        for (Object[] row : songs) {
            Long songId = (Long) row[0];
            String url = (String) row[1];
            
            if (url == null || url.isEmpty()) continue;
            
            String filename = url.substring(url.lastIndexOf('/') + 1);
            String coverUrl = coverExtractService.extractCoverFromMp3(filename, songId);

            if (coverUrl != null) {
                entityManager.createQuery("UPDATE Song s SET s.cover = :cover WHERE s.id = :id")
                    .setParameter("cover", coverUrl)
                    .setParameter("id", songId)
                    .executeUpdate();
                updatedCount++;
                System.out.println("Updated cover for song " + songId);
            }
        }

        entityManager.flush();
        entityManager.clear();
        System.out.println("Cover extraction completed! Updated " + updatedCount + " songs");
    }
}