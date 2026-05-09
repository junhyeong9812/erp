import type { Customer, Product, Warehouse, Seller } from "@/lib/types";
import { CUSTOMERS } from "./customers";
import { PRODUCTS } from "./products";
import { WAREHOUSES } from "./warehouses";
import { SELLERS } from "./sellers";

const PLACEHOLDER_CUSTOMER: Customer = {
  id: 0,
  code: "—",
  name: "—",
  grade: "NORMAL",
};

const PLACEHOLDER_PRODUCT: Product = {
  id: 0,
  sku: "—",
  name: "—",
  price: 0,
  sellerId: 0,
};

const PLACEHOLDER_WAREHOUSE: Warehouse = {
  id: 0,
  name: "—",
  location: "—",
  type: "SUB",
};

const PLACEHOLDER_SELLER: Seller = {
  id: 0,
  code: "—",
  name: "—",
  tier: "STANDARD",
  contact: "—",
};

export function lookupCustomer(id: number): Customer {
  return CUSTOMERS.find((c) => c.id === id) ?? PLACEHOLDER_CUSTOMER;
}

export function lookupProduct(id: number): Product {
  return PRODUCTS.find((p) => p.id === id) ?? PLACEHOLDER_PRODUCT;
}

export function lookupWarehouse(id: number): Warehouse {
  return WAREHOUSES.find((w) => w.id === id) ?? PLACEHOLDER_WAREHOUSE;
}

export function lookupSeller(id: number): Seller {
  return SELLERS.find((s) => s.id === id) ?? PLACEHOLDER_SELLER;
}
