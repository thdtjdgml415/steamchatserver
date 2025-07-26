package com.example.steamchatserver.config;

import com.example.steamchatserver.batch.processor.SteamGameItemProcessor;
import com.example.steamchatserver.batch.reader.SteamTop100ItemReader;
import com.example.steamchatserver.batch.writer.SteamGameItemWriter;
import com.example.steamchatserver.domain.SteamGame;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Spring Batch 작업을 설정하는 구성 클래스입니다.
 * Steam 상위 100개 게임 데이터를 가져와 처리하고 저장하는 배치 작업을 정의합니다.
 */
@Configuration
public class BatchConfig {

    /**
     * Steam 상위 100개 게임 데이터를 처리하는 Job을 정의합니다.
     * @param jobRepository Job의 메타데이터를 저장하고 관리하는 JobRepository
     * @param steamStep Job의 단일 단계를 나타내는 Step
     * @return 정의된 Job 객체
     */
    @Bean
    public Job steamTop100Batch(JobRepository jobRepository, Step steamStep) {
        return new JobBuilder("steamTop100Batch", jobRepository) // JobBuilder를 사용하여 Job을 생성합니다.
                .incrementer(new RunIdIncrementer()) // Job이 실행될 때마다 새로운 JobInstance를 생성하도록 설정합니다.
                .start(steamStep) // Job의 시작 Step을 지정합니다.
                .build(); // Job 객체를 빌드합니다.
    }

    /**
     * Steam 상위 100개 게임 데이터를 가져와 처리하고 저장하는 Step을 정의합니다.
     * @param jobRepository Step의 메타데이터를 저장하고 관리하는 JobRepository
     * @param transactionManager 트랜잭션 관리를 위한 PlatformTransactionManager
     * @param reader SteamTop100ItemReader (데이터 읽기)
     * @param processor SteamGameItemProcessor (데이터 처리)
     * @param writer SteamGameItemWriter (데이터 쓰기)
     * @return 정의된 Step 객체
     */
    @Bean
    public Step steamStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                          SteamTop100ItemReader reader,
                          SteamGameItemProcessor processor,
                          SteamGameItemWriter writer) {
        return new StepBuilder("fetchAndSaveSteamGames", jobRepository) // StepBuilder를 사용하여 Step을 생성합니다.
                .<Integer, SteamGame>chunk(10, transactionManager) // 10개 단위로 데이터를 처리하는 청크 기반 Step을 설정합니다.
                .reader(reader) // ItemReader를 설정합니다.
                .processor(processor) // ItemProcessor를 설정합니다.
                .writer(writer) // ItemWriter를 설정합니다.
                .build(); // Step 객체를 빌드합니다.
    }
}