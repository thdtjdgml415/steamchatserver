package com.example.steamchatserver.batch.writer;


import com.example.steamchatserver.domain.SteamGame;
import com.example.steamchatserver.repository.SteamGameRepository;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

/**
 * {@link SteamGame} 객체를 데이터베이스에 저장하는 Spring Batch ItemWriter입니다.
 */
@Component
public class SteamGameItemWriter implements ItemWriter<SteamGame> {
    private final SteamGameRepository repo;

    /**
     * SteamGameItemWriter의 생성자입니다.
     * @param repo SteamGame 엔티티를 저장하기 위한 {@link SteamGameRepository}
     */
    public SteamGameItemWriter(SteamGameRepository repo) { this.repo = repo; }

    /**
     * 주어진 {@link Chunk}에 포함된 {@link SteamGame} 객체들을 데이터베이스에 저장합니다.
     * @param items 저장할 {@link SteamGame} 객체들을 포함하는 {@link Chunk}
     */
    @Override
    public void write(Chunk<? extends SteamGame> items) {
        repo.saveAll(items.getItems());
    }

}