package com.example.steamchatserver.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import java.time.LocalDate;

/**
 * Spring Batch 작업을 주기적으로 실행하도록 스케줄링을 설정하는 구성 클래스입니다.
 * {@link EnableScheduling} 어노테이션을 통해 스케줄링 기능을 활성화합니다.
 */
@Configuration
@EnableScheduling
public class SchedulingConfig {

    private final JobLauncher jobLauncher; // Spring Batch Job을 실행하는 데 사용됩니다.
    private final Job steamTop100BatchJob; // 실행할 Spring Batch Job입니다.

    /**
     * SchedulingConfig의 생성자입니다.
     * @param jobLauncher Job 실행을 위한 JobLauncher
     * @param steamTop100BatchJob 실행할 Job (BatchConfig에서 정의된 "steamTop100Batch" Job을 주입받습니다.)
     */
    public SchedulingConfig(JobLauncher jobLauncher, @Qualifier("steamTop100Batch") Job steamTop100BatchJob) {
        this.jobLauncher = jobLauncher;
        this.steamTop100BatchJob = steamTop100BatchJob;
    }

    /**
     * 지정된 cron 표현식에 따라 배치 작업을 주기적으로 실행합니다.
     * `schedule.cron` 프로퍼티에 정의된 cron 표현식을 사용합니다.
     * @throws Exception Job 실행 중 발생할 수 있는 예외
     */
    @Scheduled(cron = "${schedule.cron}")
    public void runBatch() throws Exception {
        // JobParametersBuilder를 사용하여 JobParameters를 생성합니다.
        // "runAt" 파라미터는 Job 인스턴스를 고유하게 식별하는 데 사용되며, 날짜를 기준으로 하루에 한 번만 실행되도록 합니다.
        jobLauncher.run(steamTop100BatchJob,
                new org.springframework.batch.core.JobParametersBuilder()
                        .addString("runAt", LocalDate.now().toString())
                        .toJobParameters());
    }
}
