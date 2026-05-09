export type SupplierStatus = "ACTIVE" | "INACTIVE";

export interface Supplier {
  id: number;
  code: string;
  name: string;
  contact: string;
  email: string;
  category: string;
  status: SupplierStatus;
  rating: number;
}
