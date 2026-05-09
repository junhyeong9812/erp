import type { Supplier } from "@/lib/types";

export const SUPPLIERS: readonly Supplier[] = [
  { id: 1, code: "SUP-001", name: "ABC상사",     contact: "010-1234-5678", email: "sales@abc.co.kr",      category: "전자제품",  status: "ACTIVE",   rating: 4.6 },
  { id: 2, code: "SUP-002", name: "동성유통",     contact: "010-2345-6789", email: "biz@dongseong.kr",     category: "주변기기",  status: "ACTIVE",   rating: 4.2 },
  { id: 3, code: "SUP-003", name: "글로벌소싱",   contact: "010-3456-7890", email: "ops@globalsource.com", category: "음향/카메라", status: "ACTIVE",   rating: 4.8 },
  { id: 4, code: "SUP-004", name: "한미테크",     contact: "010-4567-8901", email: "sales@hanmitech.kr",   category: "주변기기",  status: "ACTIVE",   rating: 4.4 },
  { id: 5, code: "SUP-005", name: "중부물산",     contact: "010-5678-9012", email: "info@joongbu.co.kr",   category: "보조용품",  status: "INACTIVE", rating: 3.5 },
];
