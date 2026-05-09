export type WarehouseType = "MAIN" | "SUB";

export interface Warehouse {
  id: number;
  name: string;
  location: string;
  type: WarehouseType;
}
