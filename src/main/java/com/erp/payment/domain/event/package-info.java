/**
 * payment 모듈의 공개 도메인 이벤트.
 *
 * <p>다른 모듈(settlement, sales 등) 이 import 할 수 있도록 NamedInterface 로 노출한다.
 * 모듈 경계를 벗어나는 단일 통로는 이벤트뿐이며, 엔티티/서비스는 여전히 비공개다.
 */
@org.springframework.modulith.NamedInterface("events")
package com.erp.payment.domain.event;
