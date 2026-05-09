import type { Product } from "@/lib/types";

export const PRODUCTS: readonly Product[] = [
  { id: 100, sku: "SKU-NB-15", name: '노트북 15" Pro', price: 1_580_000, sellerId: 1 },
  { id: 101, sku: "SKU-MN-27", name: '모니터 27" 4K', price: 612_000, sellerId: 1 },
  { id: 102, sku: "SKU-KB-MX", name: "기계식 키보드 MX", price: 184_000, sellerId: 2 },
  { id: 103, sku: "SKU-MS-WL", name: "무선 마우스 v3", price: 58_000, sellerId: 2 },
  { id: 104, sku: "SKU-DS-1T", name: "외장 SSD 1TB", price: 142_000, sellerId: 2 },
  { id: 105, sku: "SKU-HP-NC", name: "노이즈캔슬링 헤드폰", price: 348_000, sellerId: 3 },
  { id: 106, sku: "SKU-WC-4K", name: "웹캠 4K Pro", price: 226_000, sellerId: 3 },
  { id: 107, sku: "SKU-DK-USB", name: "USB-C 도킹 스테이션", price: 198_000, sellerId: 3 },
];
