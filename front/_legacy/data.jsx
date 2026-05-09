/* global React */
// Shared state, mock data, and small utilities for the ERP prototype.

const { useState, useEffect, useRef, useMemo, useCallback, createContext, useContext } = React;

// ─── Format helpers ───────────────────────────────────────────────
const fmtKRW = (n) => {
  if (n == null) return "-";
  return "₩" + Math.round(n).toLocaleString("ko-KR");
};
const fmtN = (n) => (n == null ? "-" : Number(n).toLocaleString("ko-KR"));
const fmtDate = (iso) => {
  if (!iso) return "-";
  const d = new Date(iso);
  return `${d.getMonth() + 1}/${d.getDate()}`;
};
const fmtDateTime = (iso) => {
  if (!iso) return "-";
  const d = new Date(iso);
  const pad = (n) => String(n).padStart(2, "0");
  return `${d.getMonth() + 1}/${d.getDate()} ${pad(d.getHours())}:${pad(d.getMinutes())}`;
};
const fmtRelative = (iso) => {
  if (!iso) return "-";
  const t = new Date(iso).getTime();
  const diffMin = Math.round((Date.now() - t) / 60000);
  if (diffMin < 1) return "방금 전";
  if (diffMin < 60) return `${diffMin}분 전`;
  if (diffMin < 60 * 24) return `${Math.round(diffMin / 60)}시간 전`;
  return `${Math.round(diffMin / 60 / 24)}일 전`;
};

// ─── Mock seed data ───────────────────────────────────────────────
const CUSTOMERS = [
  { id: 1,  code: "C001", name: "ACME Corp",         grade: "VIP" },
  { id: 2,  code: "C002", name: "한솔로지스",         grade: "GOLD" },
  { id: 3,  code: "C003", name: "동방물산",           grade: "SILVER" },
  { id: 4,  code: "C004", name: "Bluestar Trading",  grade: "GOLD" },
  { id: 5,  code: "C005", name: "코리아테크",         grade: "VIP" },
  { id: 6,  code: "C006", name: "Yujin Industries",  grade: "NORMAL" },
  { id: 7,  code: "C007", name: "삼광유통",           grade: "SILVER" },
  { id: 8,  code: "C008", name: "Pacific Imports",   grade: "GOLD" },
];

const PRODUCTS = [
  { id: 100, sku: "SKU-NB-15",   name: "노트북 15\" Pro",     price: 1_580_000 },
  { id: 101, sku: "SKU-MN-27",   name: "모니터 27\" 4K",      price: 612_000 },
  { id: 102, sku: "SKU-KB-MX",   name: "기계식 키보드 MX",    price: 184_000 },
  { id: 103, sku: "SKU-MS-WL",   name: "무선 마우스 v3",       price: 58_000 },
  { id: 104, sku: "SKU-DS-1T",   name: "외장 SSD 1TB",         price: 142_000 },
  { id: 105, sku: "SKU-HP-NC",   name: "노이즈캔슬링 헤드폰", price: 348_000 },
  { id: 106, sku: "SKU-WC-4K",   name: "웹캠 4K Pro",          price: 226_000 },
  { id: 107, sku: "SKU-DK-USB",  name: "USB-C 도킹 스테이션", price: 198_000 },
];

const WAREHOUSES = [
  { id: 1, name: "서울 본사창고", location: "서울 성수",  type: "MAIN" },
  { id: 2, name: "경기 물류센터", location: "경기 이천",  type: "SUB" },
  { id: 3, name: "부산 거점창고", location: "부산 강서",  type: "SUB" },
];

// Stocks: keyed by productId/warehouseId
const initialStocks = [
  { productId: 100, warehouseId: 1, total: 142, reserved: 28 },
  { productId: 100, warehouseId: 2, total: 86,  reserved: 12 },
  { productId: 101, warehouseId: 1, total: 64,  reserved: 8 },
  { productId: 101, warehouseId: 2, total: 22,  reserved: 4 },
  { productId: 101, warehouseId: 3, total: 12,  reserved: 2 },
  { productId: 102, warehouseId: 1, total: 312, reserved: 40 },
  { productId: 103, warehouseId: 1, total: 480, reserved: 22 },
  { productId: 103, warehouseId: 2, total: 220, reserved: 0 },
  { productId: 104, warehouseId: 1, total: 58,  reserved: 6 },
  { productId: 104, warehouseId: 3, total: 18,  reserved: 2 },
  { productId: 105, warehouseId: 1, total: 8,   reserved: 4 },  // low
  { productId: 106, warehouseId: 2, total: 46,  reserved: 12 },
  { productId: 107, warehouseId: 1, total: 96,  reserved: 14 },
  { productId: 107, warehouseId: 3, total: 4,   reserved: 4 },  // critical
];

// Sales orders
const initialOrders = [
  {
    id: 2406, customerId: 1, status: "COMPLETED", placedAt: "2026-05-04T09:12:00",
    lines: [{ productId: 100, quantity: 4, unitPrice: 1_580_000 },
            { productId: 102, quantity: 4, unitPrice: 184_000 }],
    total: 4 * 1_580_000 + 4 * 184_000, paid: true, shipmentId: 7012,
  },
  {
    id: 2407, customerId: 2, status: "SHIPPED", placedAt: "2026-05-04T10:48:00",
    lines: [{ productId: 101, quantity: 12, unitPrice: 612_000 }],
    total: 12 * 612_000, paid: true, shipmentId: 7013,
  },
  {
    id: 2408, customerId: 5, status: "CONFIRMED", placedAt: "2026-05-04T11:25:00",
    lines: [{ productId: 105, quantity: 6, unitPrice: 348_000 },
            { productId: 103, quantity: 6, unitPrice: 58_000 }],
    total: 6 * 348_000 + 6 * 58_000, paid: true, shipmentId: 7014,
  },
  {
    id: 2409, customerId: 4, status: "PLACED", placedAt: "2026-05-04T13:02:00",
    lines: [{ productId: 107, quantity: 8, unitPrice: 198_000 },
            { productId: 104, quantity: 8, unitPrice: 142_000 }],
    total: 8 * 198_000 + 8 * 142_000, paid: false, shipmentId: null,
  },
  {
    id: 2410, customerId: 3, status: "PLACED", placedAt: "2026-05-04T13:34:00",
    lines: [{ productId: 106, quantity: 3, unitPrice: 226_000 }],
    total: 3 * 226_000, paid: false, shipmentId: null,
  },
  {
    id: 2411, customerId: 8, status: "CONFIRMED", placedAt: "2026-05-04T14:10:00",
    lines: [{ productId: 100, quantity: 2, unitPrice: 1_580_000 }],
    total: 2 * 1_580_000, paid: true, shipmentId: 7015,
  },
  {
    id: 2412, customerId: 6, status: "PLACED", placedAt: "2026-05-04T14:48:00",
    lines: [{ productId: 102, quantity: 20, unitPrice: 184_000 }],
    total: 20 * 184_000, paid: false, shipmentId: null,
  },
  {
    id: 2413, customerId: 7, status: "COMPLETED", placedAt: "2026-05-03T16:22:00",
    lines: [{ productId: 103, quantity: 50, unitPrice: 58_000 }],
    total: 50 * 58_000, paid: true, shipmentId: 7008,
  },
];

// Shipments
const initialShipments = [
  { id: 7008, orderId: 2413, customerId: 7, status: "COMPLETED",
    warehouseId: 1, items: 50, weightKg: 24, driver: "김철수", tracking: "TRK-202605030008",
    preparedAt: "2026-05-03T17:00:00", dispatchedAt: "2026-05-03T18:14:00",
    deliveredAt: "2026-05-04T09:55:00" },
  { id: 7012, orderId: 2406, customerId: 1, status: "COMPLETED",
    warehouseId: 1, items: 8, weightKg: 18, driver: "이민호", tracking: "TRK-202605040012",
    preparedAt: "2026-05-04T09:35:00", dispatchedAt: "2026-05-04T10:08:00",
    deliveredAt: "2026-05-04T13:42:00" },
  { id: 7013, orderId: 2407, customerId: 2, status: "DISPATCHED",
    warehouseId: 2, items: 12, weightKg: 84, driver: "박지훈", tracking: "TRK-202605040013",
    preparedAt: "2026-05-04T11:02:00", dispatchedAt: "2026-05-04T12:18:00",
    deliveredAt: null },
  { id: 7014, orderId: 2408, customerId: 5, status: "DISPATCHED",
    warehouseId: 1, items: 12, weightKg: 6, driver: "최유진", tracking: "TRK-202605040014",
    preparedAt: "2026-05-04T11:48:00", dispatchedAt: "2026-05-04T13:24:00",
    deliveredAt: null },
  { id: 7015, orderId: 2411, customerId: 8, status: "PREPARING",
    warehouseId: 1, items: 2, weightKg: 5, driver: null, tracking: null,
    preparedAt: "2026-05-04T14:32:00", dispatchedAt: null, deliveredAt: null },
];

// Deliveries
const initialDeliveries = [
  { id: 9008, shipmentId: 7008, orderId: 2413, customerId: 7, status: "DELIVERED",
    eta: "2026-05-04T10:00:00", deliveredAt: "2026-05-04T09:55:00",
    courier: "한진택배", driver: "김철수", region: "광주" },
  { id: 9012, shipmentId: 7012, orderId: 2406, customerId: 1, status: "DELIVERED",
    eta: "2026-05-04T14:00:00", deliveredAt: "2026-05-04T13:42:00",
    courier: "CJ대한통운", driver: "이민호", region: "서울" },
  { id: 9013, shipmentId: 7013, orderId: 2407, customerId: 2, status: "IN_TRANSIT",
    eta: "2026-05-04T16:30:00", deliveredAt: null,
    courier: "롯데택배", driver: "박지훈", region: "대전" },
  { id: 9014, shipmentId: 7014, orderId: 2408, customerId: 5, status: "ASSIGNED",
    eta: "2026-05-04T17:00:00", deliveredAt: null,
    courier: "CJ대한통운", driver: "최유진", region: "수원" },
];

// Purchase orders
const initialPurchaseOrders = [
  { id: 5008, supplier: "ABC상사",   productId: 100, quantity: 50,  unitPrice: 1_180_000, status: "COMPLETED",  issuedAt: "2026-04-28T09:00:00" },
  { id: 5009, supplier: "동성유통",   productId: 105, quantity: 40,  unitPrice: 248_000,   status: "PARTIAL",    issuedAt: "2026-05-01T11:00:00", received: 22 },
  { id: 5010, supplier: "글로벌소싱", productId: 107, quantity: 100, unitPrice: 138_000,   status: "ISSUED",     issuedAt: "2026-05-04T08:30:00" },
  { id: 5011, supplier: "ABC상사",   productId: 101, quantity: 30,  unitPrice: 480_000,   status: "ISSUED",     issuedAt: "2026-05-04T10:15:00" },
  { id: 5007, supplier: "한미테크",   productId: 102, quantity: 200, unitPrice: 124_000,   status: "COMPLETED",  issuedAt: "2026-04-22T14:00:00" },
];

// Status flows
const ORDER_STEPS = ["PLACED", "CONFIRMED", "SHIPPED", "COMPLETED"];
const SHIPMENT_STEPS = ["PREPARING", "DISPATCHED", "COMPLETED"];
const DELIVERY_STEPS = ["ASSIGNED", "IN_TRANSIT", "DELIVERED"];

// ─── Status meta ──────────────────────────────────────────────────
const STATUS_META = {
  // Order
  PLACED:     { label: "수주",    en: "Placed",     tone: "info" },
  CONFIRMED:  { label: "결제완료", en: "Confirmed",  tone: "accent" },
  SHIPPED:    { label: "출고됨",  en: "Shipped",    tone: "warn" },
  COMPLETED:  { label: "완료",    en: "Completed",  tone: "ok" },
  CANCELLED:  { label: "취소",    en: "Cancelled",  tone: "danger" },
  REFUNDED:   { label: "환불",    en: "Refunded",   tone: "danger" },
  PENDING:    { label: "대기",    en: "Pending",    tone: "warn" },
  // Shipment
  PREPARING:  { label: "출고준비", en: "Preparing",  tone: "info" },
  DISPATCHED: { label: "배차완료", en: "Dispatched", tone: "warn" },
  // Delivery
  ASSIGNED:   { label: "배정",    en: "Assigned",   tone: "info" },
  IN_TRANSIT: { label: "운송중",  en: "In transit", tone: "warn" },
  DELIVERED:  { label: "배송완료", en: "Delivered",  tone: "ok" },
  RETURNED:   { label: "반송",    en: "Returned",   tone: "danger" },
  // Payment
  PENDING:    { label: "대기",    en: "Pending",    tone: "neutral" },
  // PO
  ISSUED:     { label: "발주",    en: "Issued",     tone: "info" },
  PARTIAL:    { label: "부분입고", en: "Partial",    tone: "warn" },
};

// ─── Helpers ──────────────────────────────────────────────────────
const lookupCustomer = (id) => CUSTOMERS.find((c) => c.id === id) || { name: "—", code: "—" };
const lookupProduct  = (id) => PRODUCTS.find((p) => p.id === id) || { name: "—", sku: "—" };
const lookupWh       = (id) => WAREHOUSES.find((w) => w.id === id) || { name: "—" };

// ─── Toast hook ───────────────────────────────────────────────────
const ToastCtx = createContext({ push: () => {} });
function useToast() { return useContext(ToastCtx); }

function ToastProvider({ children }) {
  const [items, setItems] = useState([]);
  const push = useCallback((msg, opts = {}) => {
    const id = Math.random().toString(36).slice(2, 8);
    setItems((prev) => [...prev, { id, msg, ...opts }]);
    setTimeout(() => setItems((prev) => prev.filter((t) => t.id !== id)), opts.duration || 3200);
  }, []);
  return (
    <ToastCtx.Provider value={{ push }}>
      {children}
      <div className="toast-stack">
        {items.map((t) => (
          <div key={t.id} className="toast">
            <span className="check-mark"><svg viewBox="0 0 24 24"><path d="M5 12 l5 5 l9 -10" /></svg></span>
            <span>{t.msg}</span>
          </div>
        ))}
      </div>
    </ToastCtx.Provider>
  );
}

// ─── Count-up animation ───────────────────────────────────────────
function useCountUp(target, duration = 700) {
  const [val, setVal] = useState(target);
  const fromRef = useRef(target);
  const targetRef = useRef(target);
  useEffect(() => {
    if (target === targetRef.current) return;
    fromRef.current = val;
    targetRef.current = target;
    const start = performance.now();
    let raf;
    const tick = (t) => {
      const p = Math.min(1, (t - start) / duration);
      const eased = 1 - Math.pow(1 - p, 3);
      const next = fromRef.current + (target - fromRef.current) * eased;
      setVal(next);
      if (p < 1) raf = requestAnimationFrame(tick);
      else setVal(target);
    };
    raf = requestAnimationFrame(tick);
    return () => cancelAnimationFrame(raf);
  }, [target, duration]);
  return val;
}

// expose
Object.assign(window, {
  fmtKRW, fmtN, fmtDate, fmtDateTime, fmtRelative,
  CUSTOMERS, PRODUCTS, WAREHOUSES,
  initialStocks, initialOrders, initialShipments, initialDeliveries, initialPurchaseOrders,
  ORDER_STEPS, SHIPMENT_STEPS, DELIVERY_STEPS,
  STATUS_META, lookupCustomer, lookupProduct, lookupWh,
  useToast, ToastProvider, useCountUp,
});
