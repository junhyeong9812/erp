/* global React, KPI, StatusBadge, Stepper, SkelRow, SidePanel, PageHead, Icon */
/* global fmtKRW, fmtN, fmtDate, fmtDateTime, fmtRelative */
/* global CUSTOMERS, PRODUCTS, WAREHOUSES, ORDER_STEPS, SHIPMENT_STEPS, DELIVERY_STEPS */
/* global lookupCustomer, lookupProduct, lookupWh, useToast */

const { useState, useEffect, useMemo, useRef } = React;

// ============================================================
// DASHBOARD
// ============================================================
function DashboardPage({ store, lang }) {
  const [loading, setLoading] = useState(true);
  useEffect(() => { const t = setTimeout(() => setLoading(false), 600); return () => clearTimeout(t); }, []);

  const today = useMemo(() => {
    const orders = store.orders;
    const todayKey = "2026-05-04";
    const todays = orders.filter((o) => o.placedAt.startsWith(todayKey));
    const sales = todays.filter((o) => o.paid).reduce((s, o) => s + o.total, 0);
    const shipments = store.shipments.filter((s) => s.status === "DISPATCHED" || s.status === "PREPARING");
    const deliveries = store.deliveries.filter((d) => d.status === "IN_TRANSIT" || d.status === "ASSIGNED");
    const lowStock = store.stocks.filter((s) => (s.total - s.reserved) <= 10).length;
    return { todayCount: todays.length, sales, shipmentsCount: shipments.length, deliveriesCount: deliveries.length, lowStock };
  }, [store]);

  const recentOrders = useMemo(() => [...store.orders].sort((a, b) => b.placedAt.localeCompare(a.placedAt)).slice(0, 5), [store.orders]);
  const activeShipments = useMemo(() => store.shipments.filter((s) => s.status !== "COMPLETED"), [store.shipments]);

  return (
    <>
      <PageHead
        title={lang === "en" ? "Logistics Overview" : "물류 대시보드"}
        sub={lang === "en" ? "Real-time view across orders, shipments and deliveries" : "오늘의 수주·출고·배송 현황을 한눈에"}
        actions={
          <>
            <button className="btn"><Icon.Refresh /> {lang === "en" ? "Refresh" : "새로고침"}</button>
            <button className="btn"><Icon.Download /> {lang === "en" ? "Export" : "내보내기"}</button>
          </>
        }
      />

      {loading ? (
        <div className="kpi-grid">
          {Array.from({ length: 4 }).map((_, i) => (
            <div className="kpi" key={i}>
              <div className="kpi-label"><span className="skel" style={{ width: 80, height: 10 }} /></div>
              <div className="kpi-num"><span className="skel" style={{ width: 120, height: 26 }} /></div>
              <div className="kpi-meta"><span className="skel" style={{ width: 80, height: 10 }} /></div>
            </div>
          ))}
        </div>
      ) : (
        <div className="kpi-grid fade-in">
          <KPI label={lang === "en" ? "Today's Sales" : "오늘 매출"} value={today.sales} format="krw" delta={12.4}
               vs={lang === "en" ? "vs yesterday" : "어제 대비"} spark={[18, 22, 20, 28, 26, 33, 38, 36, 42]} />
          <KPI label={lang === "en" ? "Orders Today" : "오늘 수주"} value={today.todayCount} unit={lang === "en" ? "orders" : "건"} delta={4.2}
               vs={lang === "en" ? "vs yesterday" : "어제 대비"} spark={[5, 6, 8, 7, 9, 10, 8, 12, 14]} />
          <KPI label={lang === "en" ? "In Transit" : "운송중"} value={today.deliveriesCount} unit={lang === "en" ? "deliveries" : "건"} delta={-1.1}
               vs={lang === "en" ? "vs avg" : "평균 대비"} spark={[4, 5, 4, 6, 5, 7, 6, 5, 4]} />
          <KPI label={lang === "en" ? "Low Stock" : "재고 경고"} value={today.lowStock} unit="SKU" delta={8.0}
               vs={lang === "en" ? "vs last week" : "지난주 대비"} spark={[2, 2, 3, 3, 4, 4, 5, 5, 6]} />
        </div>
      )}

      <div className="grid-3" style={{ gridTemplateColumns: "1.6fr 1fr", gap: 14, marginBottom: 14 }}>
        <div className="card">
          <div className="card-head">
            <div>
              <div className="card-title">{lang === "en" ? "Active shipments" : "진행중 출고"}</div>
              <div className="card-sub">{lang === "en" ? "Live status across the floor" : "현장의 실시간 상태"}</div>
            </div>
            <button className="btn btn-sm btn-ghost">{lang === "en" ? "View all" : "전체 보기"} →</button>
          </div>
          <div className="card-body" style={{ paddingTop: 0 }}>
            {loading ? (
              <div className="col" style={{ gap: 8 }}>{Array.from({ length: 3 }).map((_, i) => <div key={i} className="skel" style={{ height: 56, width: "100%" }} />)}</div>
            ) : (
              <div className="col fade-in" style={{ gap: 8 }}>
                {activeShipments.map((s) => {
                  const cust = lookupCustomer(s.customerId);
                  return (
                    <div key={s.id} className="card" style={{ padding: "12px 14px", boxShadow: "none" }}>
                      <div className="row" style={{ alignItems: "center", justifyContent: "space-between" }}>
                        <div style={{ display: "flex", alignItems: "center", gap: 10 }}>
                          <span className="mono muted" style={{ fontSize: 11.5 }}>SHP-{s.id}</span>
                          <span style={{ fontWeight: 600 }}>{cust.name}</span>
                          <span className="muted" style={{ fontSize: 12 }}>· {lookupWh(s.warehouseId).name}</span>
                        </div>
                        <StatusBadge status={s.status} live={s.status === "DISPATCHED"} lang={lang} />
                      </div>
                      <div style={{ marginTop: 10 }}><Stepper steps={SHIPMENT_STEPS} current={s.status} lang={lang} /></div>
                    </div>
                  );
                })}
                {activeShipments.length === 0 && <div className="muted" style={{ padding: 24, textAlign: "center" }}>{lang === "en" ? "No active shipments" : "진행중인 출고가 없습니다"}</div>}
              </div>
            )}
          </div>
        </div>

        <div className="card">
          <div className="card-head">
            <div className="card-title">{lang === "en" ? "Recent orders" : "최근 수주"}</div>
            <button className="btn btn-sm btn-ghost">→</button>
          </div>
          <div className="card-body" style={{ padding: 0 }}>
            {loading ? <div style={{ padding: 16 }} className="col">{Array.from({ length: 4 }).map((_, i) => <div key={i} className="skel" style={{ height: 36 }} />)}</div> :
            <div className="fade-in">
              {recentOrders.map((o) => (
                <div key={o.id} style={{ display: "flex", alignItems: "center", gap: 12, padding: "10px 18px", borderTop: "1px solid var(--divider)", fontSize: 12.5 }}>
                  <span className="mono muted" style={{ width: 56 }}>#{o.id}</span>
                  <span style={{ flex: 1, fontWeight: 500 }}>{lookupCustomer(o.customerId).name}</span>
                  <span className="tnum muted">{fmtKRW(o.total)}</span>
                  <StatusBadge status={o.status} lang={lang} />
                </div>
              ))}
            </div>}
          </div>
        </div>
      </div>

      <div className="card">
        <div className="card-head">
          <div>
            <div className="card-title">{lang === "en" ? "Today's flow" : "오늘의 플로우"}</div>
            <div className="card-sub">{lang === "en" ? "Order → Payment → Shipment → Delivery" : "수주 → 결제 → 출고 → 배송"}</div>
          </div>
        </div>
        <div className="card-body">
          <FunnelStrip orders={store.orders} shipments={store.shipments} deliveries={store.deliveries} lang={lang} />
        </div>
      </div>
    </>
  );
}

function FunnelStrip({ orders, shipments, deliveries, lang }) {
  const placed = orders.length;
  const paid = orders.filter((o) => o.paid).length;
  const dispatched = shipments.filter((s) => s.status === "DISPATCHED" || s.status === "COMPLETED").length;
  const delivered = deliveries.filter((d) => d.status === "DELIVERED").length;
  const stages = [
    { label: lang === "en" ? "Placed"     : "수주",     count: placed,     pct: 100 },
    { label: lang === "en" ? "Paid"       : "결제완료", count: paid,       pct: (paid / placed) * 100 },
    { label: lang === "en" ? "Dispatched" : "배차완료", count: dispatched, pct: (dispatched / placed) * 100 },
    { label: lang === "en" ? "Delivered"  : "배송완료", count: delivered,  pct: (delivered / placed) * 100 },
  ];
  return (
    <div style={{ display: "grid", gridTemplateColumns: "repeat(4, 1fr)", gap: 14 }}>
      {stages.map((s, i) => (
        <div key={s.label} style={{ position: "relative" }}>
          <div className="muted" style={{ fontSize: 11.5, fontWeight: 600, letterSpacing: "0.04em", textTransform: "uppercase" }}>{s.label}</div>
          <div style={{ display: "flex", alignItems: "baseline", gap: 6, marginTop: 6 }}>
            <div style={{ fontSize: 28, fontWeight: 700, letterSpacing: "-0.02em" }} className="tnum">{s.count}</div>
            <div className="muted" style={{ fontSize: 11.5 }}>{s.pct.toFixed(0)}%</div>
          </div>
          <div className="pbar" style={{ marginTop: 8 }}><div className="pbar-fill" style={{ width: `${s.pct}%`, transitionDelay: `${i * 80}ms` }} /></div>
        </div>
      ))}
    </div>
  );
}

// ============================================================
// SALES ORDERS
// ============================================================
function OrdersPage({ store, setStore, lang, onOpenOrder }) {
  const [filter, setFilter] = useState("ALL");
  const [search, setSearch] = useState("");
  const [sort, setSort] = useState({ key: "placedAt", dir: "desc" });
  const toast = useToast();

  const filtered = useMemo(() => {
    let rs = store.orders;
    if (filter !== "ALL") rs = rs.filter((o) => o.status === filter);
    if (search) {
      const q = search.toLowerCase();
      rs = rs.filter((o) => String(o.id).includes(q) || lookupCustomer(o.customerId).name.toLowerCase().includes(q));
    }
    rs = [...rs].sort((a, b) => {
      const av = a[sort.key], bv = b[sort.key];
      if (av < bv) return sort.dir === "asc" ? -1 : 1;
      if (av > bv) return sort.dir === "asc" ? 1 : -1;
      return 0;
    });
    return rs;
  }, [store.orders, filter, search, sort]);

  const counts = useMemo(() => {
    const c = { ALL: store.orders.length, PLACED: 0, CONFIRMED: 0, SHIPPED: 0, COMPLETED: 0 };
    store.orders.forEach((o) => { c[o.status] = (c[o.status] || 0) + 1; });
    return c;
  }, [store.orders]);

  const toggleSort = (k) => setSort((s) => s.key === k ? { key: k, dir: s.dir === "asc" ? "desc" : "asc" } : { key: k, dir: "desc" });

  // Pay an order → trigger automatic shipment creation (event-style)
  const payOrder = (orderId) => {
    setStore((prev) => {
      const order = prev.orders.find((o) => o.id === orderId);
      if (!order) return prev;
      const newShipmentId = Math.max(...prev.shipments.map((s) => s.id)) + 1;
      const totalItems = order.lines.reduce((s, l) => s + l.quantity, 0);
      const newShipment = {
        id: newShipmentId, orderId, customerId: order.customerId, status: "PREPARING",
        warehouseId: 1, items: totalItems, weightKg: Math.round(totalItems * 1.5),
        driver: null, tracking: null,
        preparedAt: new Date().toISOString(), dispatchedAt: null, deliveredAt: null,
      };
      return {
        ...prev,
        orders: prev.orders.map((o) => o.id === orderId ? { ...o, paid: true, status: "CONFIRMED", shipmentId: newShipmentId } : o),
        shipments: [...prev.shipments, newShipment],
      };
    });
    toast.push(lang === "en" ? `Payment completed → Shipment created` : `결제 완료 → 출고 지시 자동 생성됨`);
  };

  return (
    <>
      <PageHead
        title={lang === "en" ? "Sales Orders" : "수주 관리"}
        sub={lang === "en" ? `${store.orders.length} orders · Last 30 days` : `총 ${store.orders.length}건 · 최근 30일`}
        actions={<button className="btn btn-accent"><Icon.Plus /> {lang === "en" ? "New order" : "수주 등록"}</button>}
      />
      <div className="tbl-wrap">
        <div className="tbl-toolbar">
          <div className="filters">
            {[
              { v: "ALL", ko: "전체", en: "All" },
              { v: "PLACED", ko: "수주", en: "Placed" },
              { v: "CONFIRMED", ko: "결제완료", en: "Confirmed" },
              { v: "SHIPPED", ko: "출고됨", en: "Shipped" },
              { v: "COMPLETED", ko: "완료", en: "Completed" },
            ].map((f) => (
              <button key={f.v} className={`chip ${filter === f.v ? "on" : ""}`} onClick={() => setFilter(f.v)}>
                {lang === "en" ? f.en : f.ko}<span className="count">{counts[f.v] ?? 0}</span>
              </button>
            ))}
          </div>
          <div className="right">
            <div className="search" style={{ width: 220 }}>
              <span className="ico"><Icon.Search /></span>
              <input placeholder={lang === "en" ? "Search orders..." : "주문번호·고객명 검색"} value={search} onChange={(e) => setSearch(e.target.value)} />
            </div>
          </div>
        </div>
        <table className="tbl">
          <thead>
            <tr>
              <th onClick={() => toggleSort("id")}>ID</th>
              <th>{lang === "en" ? "Customer" : "고객"}</th>
              <th>{lang === "en" ? "Items" : "품목"}</th>
              <th className="num" onClick={() => toggleSort("total")}>{lang === "en" ? "Amount" : "금액"} {sort.key === "total" && (sort.dir === "asc" ? "↑" : "↓")}</th>
              <th onClick={() => toggleSort("placedAt")}>{lang === "en" ? "Placed at" : "수주일시"} {sort.key === "placedAt" && (sort.dir === "asc" ? "↑" : "↓")}</th>
              <th>{lang === "en" ? "Status" : "상태"}</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {filtered.map((o, i) => (
              <tr key={o.id} className="stagger" style={{ animationDelay: `${i * 22}ms` }} onClick={() => onOpenOrder(o.id)}>
                <td className="id">#{o.id}</td>
                <td className="strong">{lookupCustomer(o.customerId).name}</td>
                <td className="muted">{lookupProduct(o.lines[0].productId).name}{o.lines.length > 1 ? ` 외 ${o.lines.length - 1}` : ""}</td>
                <td className="num strong">{fmtKRW(o.total)}</td>
                <td className="muted">{fmtDateTime(o.placedAt)}</td>
                <td><StatusBadge status={o.status} lang={lang} /></td>
                <td onClick={(e) => e.stopPropagation()} style={{ textAlign: "right" }}>
                  {!o.paid ? (
                    <button className="btn btn-sm btn-accent" onClick={() => payOrder(o.id)}>
                      {lang === "en" ? "Pay" : "결제"}
                    </button>
                  ) : (
                    <button className="btn btn-sm btn-ghost"><Icon.More /></button>
                  )}
                </td>
              </tr>
            ))}
            {filtered.length === 0 && (
              <tr><td colSpan={7} className="muted" style={{ textAlign: "center", padding: 32 }}>{lang === "en" ? "No matching orders" : "조건에 맞는 수주가 없습니다"}</td></tr>
            )}
          </tbody>
        </table>
      </div>
    </>
  );
}

// ============================================================
// ORDER DETAIL PANEL
// ============================================================
function OrderPanel({ orderId, store, setStore, onClose, lang }) {
  const order = store.orders.find((o) => o.id === orderId);
  const toast = useToast();
  if (!order) return null;
  const cust = lookupCustomer(order.customerId);
  const shipment = store.shipments.find((s) => s.id === order.shipmentId);

  const payOrder = () => {
    setStore((prev) => {
      const o = prev.orders.find((x) => x.id === orderId);
      const newShipmentId = Math.max(...prev.shipments.map((s) => s.id)) + 1;
      const items = o.lines.reduce((s, l) => s + l.quantity, 0);
      return {
        ...prev,
        orders: prev.orders.map((x) => x.id === orderId ? { ...x, paid: true, status: "CONFIRMED", shipmentId: newShipmentId } : x),
        shipments: [...prev.shipments, {
          id: newShipmentId, orderId, customerId: o.customerId, status: "PREPARING",
          warehouseId: 1, items, weightKg: Math.round(items * 1.5),
          driver: null, tracking: null,
          preparedAt: new Date().toISOString(), dispatchedAt: null, deliveredAt: null,
        }],
      };
    });
    toast.push(lang === "en" ? "Payment completed" : "결제 완료 · 출고 지시 생성됨");
  };

  return (
    <>
      <div className="sp-head">
        <div>
          <div className="sp-title">{lang === "en" ? `Order #${order.id}` : `수주 #${order.id}`}</div>
          <div className="sp-sub">{cust.code} · {cust.name}</div>
        </div>
        <button className="sp-close" onClick={onClose}><Icon.X /></button>
      </div>
      <div className="sp-body">
        <div>
          <Stepper steps={ORDER_STEPS} current={order.status} lang={lang} />
        </div>
        <div>
          <div className="section-h">{lang === "en" ? "Summary" : "기본 정보"}</div>
          <dl className="kv">
            <dt>{lang === "en" ? "Customer" : "고객"}</dt><dd>{cust.name} <span className="muted">({cust.grade})</span></dd>
            <dt>{lang === "en" ? "Placed at" : "수주일시"}</dt><dd>{fmtDateTime(order.placedAt)}</dd>
            <dt>{lang === "en" ? "Total" : "총액"}</dt><dd className="strong">{fmtKRW(order.total)}</dd>
            <dt>{lang === "en" ? "Payment" : "결제"}</dt><dd>{order.paid ? <StatusBadge status="COMPLETED" lang={lang} /> : <StatusBadge status="PENDING" lang={lang} />}</dd>
          </dl>
        </div>
        <div>
          <div className="section-h">{lang === "en" ? "Line items" : "품목"}</div>
          <table className="tbl" style={{ fontSize: 12 }}>
            <thead><tr><th>{lang === "en" ? "SKU" : "품목"}</th><th className="num">{lang === "en" ? "Qty" : "수량"}</th><th className="num">{lang === "en" ? "Unit" : "단가"}</th><th className="num">{lang === "en" ? "Subtotal" : "소계"}</th></tr></thead>
            <tbody>
              {order.lines.map((l, i) => (
                <tr key={i}>
                  <td><div className="strong">{lookupProduct(l.productId).name}</div><div className="muted mono" style={{ fontSize: 11 }}>{lookupProduct(l.productId).sku}</div></td>
                  <td className="num">{fmtN(l.quantity)}</td>
                  <td className="num muted">{fmtKRW(l.unitPrice)}</td>
                  <td className="num strong">{fmtKRW(l.quantity * l.unitPrice)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        {shipment && (
          <div>
            <div className="section-h">{lang === "en" ? "Shipment" : "연결된 출고"}</div>
            <div className="card" style={{ padding: 12, boxShadow: "none" }}>
              <div className="row" style={{ alignItems: "center" }}>
                <span className="mono muted">SHP-{shipment.id}</span>
                <span className="spacer" />
                <StatusBadge status={shipment.status} lang={lang} live={shipment.status === "DISPATCHED"} />
              </div>
              <div style={{ marginTop: 10 }}><Stepper steps={SHIPMENT_STEPS} current={shipment.status} lang={lang} /></div>
            </div>
          </div>
        )}
      </div>
      <div className="sp-foot">
        <button className="btn" onClick={onClose}>{lang === "en" ? "Close" : "닫기"}</button>
        {!order.paid && <button className="btn btn-accent" onClick={payOrder}>{lang === "en" ? "Confirm payment" : "결제 확정"}</button>}
      </div>
    </>
  );
}

Object.assign(window, { DashboardPage, OrdersPage, OrderPanel });
