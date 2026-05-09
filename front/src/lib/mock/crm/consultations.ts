import type { Consultation } from "@/lib/types/crm";

export const CONSULTATIONS: readonly Consultation[] = [
  {
    id: 1,
    customerId: 1,
    channel: "EMAIL",
    summary: "정기 발주 일정 조정 문의",
    status: "RESOLVED",
    occurredAt: "2026-05-02T10:15:00",
    agentName: "최유진",
  },
  {
    id: 2,
    customerId: 2,
    channel: "PHONE",
    summary: "출고 지연 안내 및 조정",
    status: "RESOLVED",
    occurredAt: "2026-05-03T13:42:00",
    agentName: "최유진",
  },
  {
    id: 3,
    customerId: 5,
    channel: "CHAT",
    summary: "신규 모니터 모델 문의",
    status: "OPEN",
    occurredAt: "2026-05-04T09:30:00",
    agentName: "김지연",
  },
  {
    id: 4,
    customerId: 4,
    channel: "VISIT",
    summary: "현장 방문 — 제품 시연 요청",
    status: "OPEN",
    occurredAt: "2026-05-04T14:00:00",
    agentName: "최유진",
  },
];
