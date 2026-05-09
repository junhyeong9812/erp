import type { Seller } from "@/lib/types";

export const SELLERS: readonly Seller[] = [
  { id: 1, code: "SLR-001", name: "테크원 코리아", tier: "ENTERPRISE", contact: "01012345678" },
  { id: 2, code: "SLR-002", name: "디바이스랩", tier: "PREMIUM", contact: "01098765432" },
  { id: 3, code: "SLR-003", name: "사운드앤픽셀", tier: "STANDARD", contact: "01055556666" },
];

export const DEFAULT_SELLER_ID = 1;
