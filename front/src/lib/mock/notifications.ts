import type { Notification } from "@/lib/types";

export const NOTIFICATIONS: readonly Notification[] = [
  { id: 1, recipientId: 1, title: "결제 완료",          body: "주문 #2406 결제가 완료되었습니다.",    channel: "EMAIL",  status: "SENT",   sentAt: "2026-05-04T13:42:35", failureReason: null },
  { id: 2, recipientId: 2, title: "출고 발송",          body: "출고 SHP-7012가 발송되었습니다.",      channel: "PUSH",   status: "SENT",   sentAt: "2026-05-04T10:08:10", failureReason: null },
  { id: 3, recipientId: 4, title: "결재 요청",          body: "출장비 정산 결재가 도착했습니다.",     channel: "SYSTEM", status: "SENT",   sentAt: "2026-05-03T15:00:30", failureReason: null },
  { id: 4, recipientId: 6, title: "정산 마감 임박",     body: "5월 정산 기간이 곧 마감됩니다(D-3).",  channel: "EMAIL",  status: "PENDING", sentAt: null,                  failureReason: null },
  { id: 5, recipientId: 5, title: "계정 잠금 알림",      body: "비밀번호 5회 오류로 계정이 잠겼습니다.", channel: "SMS",    status: "FAILED", sentAt: null,                  failureReason: "수신자 SMS 미동의" },
  { id: 6, recipientId: 3, title: "재고 부족 경고",     body: "헤드폰(SKU-HP-NC) 가용 4개 — 발주 권장.", channel: "EMAIL",  status: "SENT",   sentAt: "2026-05-04T08:00:00", failureReason: null },
];
