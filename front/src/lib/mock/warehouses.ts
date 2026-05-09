import type { Warehouse } from "@/lib/types";

export const WAREHOUSES: readonly Warehouse[] = [
  { id: 1, name: "서울 본사창고", location: "서울 성수", type: "MAIN" },
  { id: 2, name: "경기 물류센터", location: "경기 이천", type: "SUB" },
  { id: 3, name: "부산 거점창고", location: "부산 강서", type: "SUB" },
];
