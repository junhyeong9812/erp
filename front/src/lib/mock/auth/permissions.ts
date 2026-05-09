import type { AppPermission } from "@/lib/types/auth";

export const PERMISSIONS: readonly AppPermission[] = [
  { code: "ORDER_READ",      name: "주문 조회",     description: "수주 목록과 상세를 조회" },
  { code: "ORDER_WRITE",     name: "주문 생성",     description: "수주 등록 및 상태 변경" },
  { code: "PAYMENT_PROCESS", name: "결제 처리",     description: "결제 확정 및 환불" },
  { code: "SHIPMENT_DISPATCH", name: "출고 발송",   description: "PREPARING 출고를 DISPATCH로 전환" },
  { code: "DELIVERY_COMPLETE", name: "배송 완료",   description: "배송 상태를 DELIVERED로 전환" },
  { code: "INVENTORY_READ",  name: "재고 조회",     description: "재고/이동 이력 조회" },
  { code: "INVENTORY_WRITE", name: "재고 변경",     description: "입고/예약/조정/이동" },
  { code: "PROCUREMENT",     name: "발주",          description: "PO 발행 및 입고 검수" },
  { code: "SETTLEMENT_OPS",  name: "정산 관리",     description: "기간 마감, 전표, 판매자 정산" },
  { code: "HR_OPS",          name: "인사",          description: "직원/근태/연차/급여" },
  { code: "APPROVAL_OPS",    name: "전자결재",      description: "결재 문서 기안/승인" },
  { code: "AUTH_ADMIN",      name: "권한 관리",     description: "사용자/역할/권한 관리" },
];
