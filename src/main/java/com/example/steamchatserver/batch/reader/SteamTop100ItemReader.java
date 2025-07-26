package com.example.steamchatserver.batch.reader;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Steam Spy API에서 상위 100개 게임의 appid를 읽어오는 Spring Batch ItemReader입니다.
 * {@link ItemStreamReader}를 구현하여 배치 작업이 시작될 때마다 데이터를 새로 로드합니다.
 */
@Component
public class SteamTop100ItemReader implements ItemStreamReader<Integer> {
    private final RestTemplate restTemplate;
    // Steam Spy API의 상위 100개 게임 URL을 주입받습니다.
    @Value("${steam.spy.top100-url}")
    private String url;
    private List<Integer> appIds; // 읽어온 appid 목록
    private int index; // 현재 읽을 appid의 인덱스

    /**
     * SteamTop100ItemReader의 생성자입니다.
     * @param restTemplate Steam Spy API 호출에 사용될 RestTemplate
     */
    public SteamTop100ItemReader(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * 배치 작업이 시작될 때 호출됩니다.
     * Steam Spy API를 호출하여 상위 100개 게임의 appid를 가져와 appIds 목록을 초기화합니다.
     * @param executionContext 현재 실행 컨텍스트
     * @throws ItemStreamException 데이터 로드 중 오류 발생 시
     */
    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        // Steam Spy API를 호출하여 상위 100개 게임 데이터를 가져옵니다.
        var map = restTemplate.getForObject(url, Map.class);
        if (map != null) {
            // 가져온 데이터에서 appid만 추출하여 목록을 생성합니다.
            this.appIds = new ArrayList<>(map.values().stream()
                    .map(item -> ((Map<String, Object>) item).get("appid"))
                    .map(id -> (Integer) id)
                    .toList());
        } else {
            this.appIds = new ArrayList<>();
        }
        // 인덱스를 0으로 초기화하여 처음부터 읽을 수 있도록 합니다.
        this.index = 0;
    }

    /**
     * 다음 appid를 읽어옵니다.
     * @return 다음 appid 또는 더 이상 읽을 데이터가 없으면 null
     */
    @Override
    public Integer read() {
        // appIds 목록에 데이터가 남아있으면 다음 appid를 반환하고 인덱스를 증가시킵니다.
        if (appIds != null && index < appIds.size()) {
            return appIds.get(index++);
        }
        // 더 이상 읽을 데이터가 없으면 null을 반환합니다.
        return null;
    }

    /**
     * 배치 작업 중 상태를 업데이트할 때 호출됩니다. (현재는 구현되지 않음)
     * @param executionContext 현재 실행 컨텍스트
     * @throws ItemStreamException 상태 업데이트 중 오류 발생 시
     */
    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        // No state to update
    }

    /**
     * 배치 작업이 종료될 때 호출됩니다. (현재는 구현되지 않음)
     * @throws ItemStreamException 리소스 해제 중 오류 발생 시
     */
    @Override
    public void close() throws ItemStreamException {
        // No resources to close
    }
}