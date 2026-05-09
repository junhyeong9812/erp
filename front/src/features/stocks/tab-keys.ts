export const STOCKS_TABS = [
  "inventory",
  "products",
  "warehouses",
  "movements",
] as const;
export type StocksTabKey = (typeof STOCKS_TABS)[number];

export function isStocksTab(value: string): value is StocksTabKey {
  return (STOCKS_TABS as readonly string[]).includes(value);
}
