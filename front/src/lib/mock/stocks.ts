import type { Stock } from "@/lib/types";

export const initialStocks: readonly Stock[] = [
  { productId: 100, warehouseId: 1, total: 142, reserved: 28 },
  { productId: 100, warehouseId: 2, total: 86, reserved: 12 },
  { productId: 101, warehouseId: 1, total: 64, reserved: 8 },
  { productId: 101, warehouseId: 2, total: 22, reserved: 4 },
  { productId: 101, warehouseId: 3, total: 12, reserved: 2 },
  { productId: 102, warehouseId: 1, total: 312, reserved: 40 },
  { productId: 103, warehouseId: 1, total: 480, reserved: 22 },
  { productId: 103, warehouseId: 2, total: 220, reserved: 0 },
  { productId: 104, warehouseId: 1, total: 58, reserved: 6 },
  { productId: 104, warehouseId: 3, total: 18, reserved: 2 },
  { productId: 105, warehouseId: 1, total: 8, reserved: 4 },
  { productId: 106, warehouseId: 2, total: 46, reserved: 12 },
  { productId: 107, warehouseId: 1, total: 96, reserved: 14 },
  { productId: 107, warehouseId: 3, total: 4, reserved: 4 },
];
