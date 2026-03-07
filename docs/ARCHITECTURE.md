# 아키텍처 구조

## 레이어 구조 (Layered + Hexagonal 절충)
```
com.erp.{domain}/
├── domain/                    # 핵심 비즈니스 (순수 자바)
│   ├── entity/               # 도메인 엔티티
│   ├── service/              # 도메인 서비스 (복잡한 비즈니스 로직)
│   ├── repository/           # Port - 인터페이스만 정의
│   └── event/                # 도메인 이벤트
│
├── application/               # 애플리케이션 계층 (유스케이스)
│   ├── usecase/              # 유스케이스 구현
│   └── dto/
│       ├── command/          # 명령 (쓰기 작업용)
│       └── query/            # 조회 (읽기 작업용)
│
├── infrastructure/            # 인프라 계층 (Adapter)
│   ├── persistence/          # JPA Repository 구현체
│   └── external/             # 외부 API 연동
│
└── presentation/              # 표현 계층
    ├── api/                  # REST Controller
    └── dto/
        ├── request/          # API 요청
        └── response/         # API 응답
```

## 의존성 방향
```
Presentation → Application → Domain ← Infrastructure
                                ↑
                          (Port/Interface)
```

- **Domain**은 어디에도 의존하지 않음 (순수)
- **Application**은 Domain만 의존
- **Infrastructure**는 Domain의 인터페이스를 구현
- **Presentation**은 Application을 호출

## 레이어별 역할

### Domain Layer
- 비즈니스 규칙의 핵심
- JPA 어노테이션은 최소화 (Entity에만)
- 외부 프레임워크 의존 없음
- Repository는 **인터페이스**만 정의

### Application Layer
- 유스케이스 조율
- 트랜잭션 경계
- 도메인 객체 조합
- DTO 변환

### Infrastructure Layer
- Domain Repository 인터페이스 구현
- JPA, Redis, 외부 API 연동
- 기술적 상세 구현

### Presentation Layer
- HTTP 요청/응답 처리
- 입력 검증
- API 문서화

## 예시: Inventory 모듈
```java
// Domain Layer - Port (Interface)
public interface StockRepository {
    Stock save(Stock stock);
    Optional<Stock> findByProductId(Long productId);
}

// Domain Layer - Entity
public class Stock {
    private Long id;
    private Long productId;
    private int quantity;
    
    public void decrease(int amount) {
        if (this.quantity < amount) {
            throw new IllegalStateException("재고 부족");
        }
        this.quantity -= amount;
    }
}

// Application Layer - UseCase
@Service
@Transactional
public class DecreaseStockUseCase {
    private final StockRepository stockRepository;
    
    public void execute(DecreaseStockCommand command) {
        Stock stock = stockRepository.findByProductId(command.productId())
            .orElseThrow(() -> new IllegalArgumentException("재고 없음"));
        stock.decrease(command.quantity());
        stockRepository.save(stock);
    }
}

// Infrastructure Layer - Adapter
@Repository
public class JpaStockRepository implements StockRepository {
    private final StockJpaRepository jpaRepository;
    
    @Override
    public Stock save(Stock stock) {
        return jpaRepository.save(stock);
    }
}

// Presentation Layer - Controller
@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    private final DecreaseStockUseCase decreaseStockUseCase;
    
    @PostMapping("/stocks/{productId}/decrease")
    public ResponseEntity<Void> decrease(...) {
        decreaseStockUseCase.execute(command);
        return ResponseEntity.ok().build();
    }
}
```

## 테스트 전략

| 레이어 | 테스트 종류 | 의존성 |
|--------|------------|--------|
| Domain | 단위 테스트 | 없음 (순수 자바) |
| Application | 단위 테스트 | Repository Mock |
| Infrastructure | 통합 테스트 | 실제 DB (H2) |
| Presentation | 슬라이스 테스트 | @WebMvcTest |
