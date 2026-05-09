/* global React, KPI, StatusBadge, Stepper, SkelRow, SidePanel, PageHead, Icon */
/* global fmtKRW, fmtN, fmtDate, fmtDateTime, fmtRelative */
/* global CUSTOMERS, PRODUCTS, WAREHOUSES, ORDER_STEPS, SHIPMENT_STEPS, DELIVERY_STEPS */
/* global lookupCustomer, lookupProduct, lookupWh, useToast */

const { useState: uS2, useEffect: uE2, useMemo: uM2, useRef: uR2 } = React;

// ============================================================
// SHIPMENTS
// ============================================================
function ShipmentsPage({ store, setStore, lang, onOpen }) {
  const [filter, setFilter] = uS2("ALL");
  const [search, setSearch] = uS2("");

  const filtered = uM2(() => {
    let rs = store.shipments;
    if (filter !== "ALL") rs = rs.filter((s) => s.status === filter);
    if (search) {
      const q = search.toLowerCase();
      rs = rs.filter((s) => String(s.id).includes(q) || lookupCustomer(s.customerId).name.toLowerCase().includes(q) || (s.tracking || "").toLowerCase().includes(q));
    }
    return [...rs].sort((a, b) => b.id - a.id);
  }, [store.shipments, filter, search]);

  const counts = uM2(() => {
    const c = { ALL: store.shipments.length, PREPARING: 0, DISPATCHED: 0, COMPLETED: 0 };
    store.shipments.forEach((s) => { c[s.status] = (c[s.status] || 0) + 1; });
    return c;
  }, [store.shipments]);

  return (
    <>
      <PageHead
        title={lang === "en" ? "Shipments" : "출고 관리"}
        sub={lang === "en" ? `${counts.PREPARING} preparing · ${counts.DISPATCHED} dispatched` : `출고준비 ${counts.PREPARING}건 · 배차완료 ${counts.DISPATCHED}건`}
        actions={<button className="btn"><Icon.Download /> {lang === "en" ? "Export" : "내보내기"}</button>}
      />
      <div className="tbl-wrap">
        <div className="tbl-toolbar">
          <div className="filters">
            {[
              { v: "ALL", ko: "전체", en: "All" },
              { v: "PREPARING", ko: "출고준비", en: "Preparing" },
              { v: "DISPATCHED", ko: "배차완료", en: "Dispatched" },
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
              <input placeholder={lang === "en" ? "ID · customer · tracking" : "ID·고객·송장 검색"} value={search} onChange={(e) => setSearch(e.target.value)} />
            </div>
          </div>
        </div>
        <table className="tbl">
          <thead>
            <tr>
              <th>ID</th>
              <th>{lang === "en" ? "Order" : "주문"}</th>
              <th>{lang === "en" ? "Customer" : "고객"}</th>
              <th>{lang === "en" ? "Warehouse" : "창고"}</th>
              <th className="num">{lang === "en" ? "Items" : "품목수"}</th>
              <th className="num">{lang === "en" ? "Weight" : "중량"}</th>
              <th>{lang === "en" ? "Tracking" : "송장번호"}</th>
              <th>{lang === "en" ? "Driver" : "배차"}</th>
              <th>{lang === "en" ? "Status" : "상태"}</th>
            </tr>
          </thead>
          <tbody>
            {filtered.map((s, i) => (
              <tr key={s.id} className="stagger" style={{ animationDelay: `${i * 22}ms` }} onClick={() => onOpen(s.id)}>
                <td className="id">SHP-{s.id}</td>
                <td className="muted">#{s.orderId}</td>
                <td className="strong">{lookupCustomer(s.customerId).name}</td>
                <td className="muted">{lookupWh(s.warehouseId).name}</td>
                <td className="num">{s.items}</td>
                <td className="num muted">{s.weightKg}kg</td>
                <td className="mono muted" style={{ fontSize: 11.5 }}>{s.tracking || "—"}</td>
                <td className="muted">{s.driver || "—"}</td>
                <td><StatusBadge status={s.status} live={s.status === "DISPATCHED"} lang={lang} /></td>
              </tr>
            ))}
            {filtered.length === 0 && <tr><td colSpan={9} className="muted" style={{ textAlign: "center", padding: 32 }}>{lang === "en" ? "No shipments" : "출고 내역이 없습니다"}</td></tr>}
          </tbody>
        </table>
      </div>
    </>
  );
}

// ============================================================
// SHIPMENT DETAIL PANEL — Dispatch action
// ============================================================
function ShipmentPanel({ shipmentId, store, setStore, onClose, lang }) {
  const ship = store.shipments.find((s) => s.id === shipmentId);
  const [driver, setDriver] = uS2("");
  const [tracking, setTracking] = uS2("");
  const toast = useToast();
  if (!ship) return null;
  const cust = lookupCustomer(ship.customerId);
  const order = store.orders.find((o) => o.id === ship.orderId);

  const dispatch = () => {
    if (!driver || !tracking) { toast.push(lang === "en" ? "Driver and tracking required" : "배차/송장번호를 입력하세요"); return; }
    setStore((prev) => {
      const newDeliveryId = Math.max(0, ...prev.deliveries.map((d) => d.id)) + 1;
      const updatedShipments = prev.shipments.map((s) =>
        s.id === shipmentId ? { ...s, status: "DISPATCHED", driver, tracking, dispatchedAt: new Date().toISOString() } : s
      );
      const updatedOrders = prev.orders.map((o) =>
        o.id === ship.orderId ? { ...o, status: "SHIPPED" } : o
      );
      const newDelivery = {
        id: newDeliveryId, shipmentId, orderId: ship.orderId, customerId: ship.customerId,
        status: "ASSIGNED", eta: new Date(Date.now() + 4 * 3600 * 1000).toISOString(),
        deliveredAt: null, courier: "CJ대한통운", driver, region: "서울",
      };
      return { ...prev, shipments: updatedShipments, orders: updatedOrders, deliveries: [...prev.deliveries, newDelivery] };
    });
    toast.push(lang === "en" ? "Dispatched · Delivery created" : "배차 완료 · 배송 추적 시작됨");
    setDriver(""); setTracking("");
  };

  const complete = () => {
    setStore((prev) => ({
      ...prev,
      shipments: prev.shipments.map((s) => s.id === shipmentId ? { ...s, status: "COMPLETED", deliveredAt: new Date().toISOString() } : s),
      orders: prev.orders.map((o) => o.id === ship.orderId ? { ...o, status: "COMPLETED" } : o),
      deliveries: prev.deliveries.map((d) => d.shipmentId === shipmentId ? { ...d, status: "DELIVERED", deliveredAt: new Date().toISOString() } : d),
    }));
    toast.push(lang === "en" ? "Shipment completed" : "출고 완료 처리됨");
  };

  return (
    <>
      <div className="sp-head">
        <div>
          <div className="sp-title">SHP-{ship.id}</div>
          <div className="sp-sub">#{ship.orderId} · {cust.name}</div>
        </div>
        <button className="sp-close" onClick={onClose}><Icon.X /></button>
      </div>
      <div className="sp-body">
        <Stepper steps={SHIPMENT_STEPS} current={ship.status} lang={lang} />

        <div>
          <div className="section-h">{lang === "en" ? "Details" : "출고 정보"}</div>
          <dl className="kv">
            <dt>{lang === "en" ? "Warehouse" : "창고"}</dt><dd>{lookupWh(ship.warehouseId).name}</dd>
            <dt>{lang === "en" ? "Items" : "품목수"}</dt><dd>{ship.items}{lang === "en" ? " items" : "건"}</dd>
            <dt>{lang === "en" ? "Weight" : "중량"}</dt><dd>{ship.weightKg} kg</dd>
            <dt>{lang === "en" ? "Prepared" : "준비완료"}</dt><dd className="muted">{fmtDateTime(ship.preparedAt)}</dd>
            {ship.dispatchedAt && <><dt>{lang === "en" ? "Dispatched" : "배차완료"}</dt><dd className="muted">{fmtDateTime(ship.dispatchedAt)}</dd></>}
            {ship.driver && <><dt>{lang === "en" ? "Driver" : "기사"}</dt><dd>{ship.driver}</dd></>}
            {ship.tracking && <><dt>{lang === "en" ? "Tracking" : "송장번호"}</dt><dd className="mono">{ship.tracking}</dd></>}
          </dl>
        </div>

        {ship.status === "PREPARING" && (
          <div>
            <div className="section-h">{lang === "en" ? "Dispatch" : "배차 등록"}</div>
            <div className="grid-2" style={{ gap: 10 }}>
              <div className="field">
                <label className="field-label">{lang === "en" ? "Driver" : "기사명"}</label>
                <input className="input" value={driver} onChange={(e) => setDriver(e.target.value)} placeholder="DRV-001" />
              </div>
              <div className="field">
                <label className="field-label">{lang === "en" ? "Tracking #" : "송장번호"}</label>
                <input className="input" value={tracking} onChange={(e) => setTracking(e.target.value)} placeholder="TRK-..." />
              </div>
            </div>
          </div>
        )}

        {order && (
          <div>
            <div className="section-h">{lang === "en" ? "Items" : "출고 품목"}</div>
            <table className="tbl" style={{ fontSize: 12 }}>
              <thead><tr><th>{lang === "en" ? "SKU" : "품목"}</th><th className="num">{lang === "en" ? "Qty" : "수량"}</th></tr></thead>
              <tbody>
                {order.lines.map((l, i) => (
                  <tr key={i}>
                    <td><div className="strong">{lookupProduct(l.productId).name}</div><div className="muted mono" style={{ fontSize: 11 }}>{lookupProduct(l.productId).sku}</div></td>
                    <td className="num">{l.quantity}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
      <div className="sp-foot">
        <button className="btn" onClick={onClose}>{lang === "en" ? "Close" : "닫기"}</button>
        {ship.status === "PREPARING" && <button className="btn btn-accent" onClick={dispatch}>{lang === "en" ? "Dispatch" : "배차 완료"}</button>}
        {ship.status === "DISPATCHED" && <button className="btn btn-accent" onClick={complete}>{lang === "en" ? "Mark delivered" : "배송 완료 처리"}</button>}
      </div>
    </>
  );
}

// ============================================================
// DELIVERIES — Kanban
// ============================================================
function DeliveriesPage({ store, setStore, lang }) {
  const [view, setView] = uS2("kanban"); // kanban | timeline
  const lanes = [
    { key: "ASSIGNED",   ko: "배정",   en: "Assigned",   tone: "info" },
    { key: "IN_TRANSIT", ko: "운송중", en: "In transit", tone: "warn" },
    { key: "DELIVERED",  ko: "배송완료", en: "Delivered", tone: "ok" },
    { key: "RETURNED",   ko: "반송",   en: "Returned",   tone: "danger" },
  ];
  const toast = useToast();

  const advance = (deliveryId) => {
    setStore((prev) => {
      const d = prev.deliveries.find((x) => x.id === deliveryId);
      if (!d) return prev;
      const next = d.status === "ASSIGNED" ? "IN_TRANSIT" : d.status === "IN_TRANSIT" ? "DELIVERED" : d.status;
      const deliveries = prev.deliveries.map((x) => x.id === deliveryId ? {
        ...x, status: next, deliveredAt: next === "DELIVERED" ? new Date().toISOString() : x.deliveredAt
      } : x);
      const shipments = next === "DELIVERED" ? prev.shipments.map((s) => s.id === d.shipmentId ? { ...s, status: "COMPLETED", deliveredAt: new Date().toISOString() } : s) : prev.shipments;
      const orders = next === "DELIVERED" ? prev.orders.map((o) => o.id === d.orderId ? { ...o, status: "COMPLETED" } : o) : prev.orders;
      return { ...prev, deliveries, shipments, orders };
    });
    toast.push(lang === "en" ? "Status updated" : "상태가 업데이트되었습니다");
  };

  return (
    <>
      <PageHead
        title={lang === "en" ? "Deliveries" : "배송 추적"}
        sub={lang === "en" ? `${store.deliveries.length} deliveries · live tracking` : `총 ${store.deliveries.length}건 · 실시간 추적`}
        actions={
          <div className="filters" style={{ display: "flex", gap: 0, border: "1px solid var(--border)", borderRadius: 6, overflow: "hidden" }}>
            <button className={`btn btn-sm ${view === "kanban" ? "btn-primary" : "btn-ghost"}`} style={{ borderRadius: 0, borderRight: "1px solid var(--border)" }} onClick={() => setView("kanban")}>{lang === "en" ? "Kanban" : "칸반"}</button>
            <button className={`btn btn-sm ${view === "timeline" ? "btn-primary" : "btn-ghost"}`} style={{ borderRadius: 0 }} onClick={() => setView("timeline")}>{lang === "en" ? "Timeline" : "타임라인"}</button>
          </div>
        }
      />

      {view === "kanban" ? (
        <div className="kanban">
          {lanes.map((lane) => {
            const items = store.deliveries.filter((d) => d.status === lane.key);
            return (
              <div className="lane" key={lane.key}>
                <div className="lane-head">
                  <span className={`badge ${lane.tone}`}>{lang === "en" ? lane.en : lane.ko}</span>
                  <span className="lane-count">{items.length}</span>
                </div>
                <div className="lane-body">
                  {items.map((d, i) => {
                    const cust = lookupCustomer(d.customerId);
                    return (
                      <div className="kard stagger" key={d.id} style={{ animationDelay: `${i * 35}ms` }} onClick={() => lane.key !== "DELIVERED" && lane.key !== "RETURNED" && advance(d.id)}>
                        <div className="kard-head">
                          <span className="kard-id">DLV-{d.id}</span>
                          <StatusBadge status={d.status} lang={lang} live={d.status === "IN_TRANSIT"} />
                        </div>
                        <div className="kard-title">{cust.name}</div>
                        <div className="kard-meta">
                          <span>{d.region}</span><span className="sep">·</span>
                          <span className="mono">{d.driver}</span>
                        </div>
                        <div className="kard-meta" style={{ marginTop: 4 }}>
                          <span><Icon.Clock /></span>
                          <span>ETA {fmtDateTime(d.eta)}</span>
                        </div>
                      </div>
                    );
                  })}
                  {items.length === 0 && <div className="muted" style={{ textAlign: "center", padding: 16, fontSize: 12 }}>{lang === "en" ? "Empty" : "비어있음"}</div>}
                </div>
              </div>
            );
          })}
        </div>
      ) : (
        <div className="card">
          <div className="card-body" style={{ paddingTop: 16 }}>
            <div className="timeline">
              {[...store.deliveries].sort((a, b) => (b.deliveredAt || b.eta).localeCompare(a.deliveredAt || a.eta)).map((d) => {
                const done = d.status === "DELIVERED";
                const now = d.status === "IN_TRANSIT";
                return (
                  <div className={`tl-item ${done ? "done" : now ? "now" : ""}`} key={d.id}>
                    <div className="tl-dot">{done ? "✓" : ""}</div>
                    <div className="tl-content">
                      <div className="tl-title">{lookupCustomer(d.customerId).name} <span className="muted" style={{ fontWeight: 400 }}>· DLV-{d.id}</span></div>
                      <div className="tl-meta">{d.region} · {d.driver} · ETA {fmtDateTime(d.eta)}{done && ` · 도착 ${fmtDateTime(d.deliveredAt)}`}</div>
                    </div>
                    <StatusBadge status={d.status} lang={lang} live={now} />
                  </div>
                );
              })}
            </div>
          </div>
        </div>
      )}
    </>
  );
}

// ============================================================
// STOCKS
// ============================================================
function StocksPage({ store, lang }) {
  const [search, setSearch] = uS2("");
  const [whFilter, setWhFilter] = uS2(0); // 0=all
  const rows = uM2(() => {
    let rs = store.stocks;
    if (whFilter > 0) rs = rs.filter((s) => s.warehouseId === whFilter);
    if (search) {
      const q = search.toLowerCase();
      rs = rs.filter((s) => lookupProduct(s.productId).name.toLowerCase().includes(q) || lookupProduct(s.productId).sku.toLowerCase().includes(q));
    }
    return rs;
  }, [store.stocks, search, whFilter]);

  return (
    <>
      <PageHead
        title={lang === "en" ? "Stock Levels" : "재고 현황"}
        sub={lang === "en" ? `${rows.length} SKUs across ${WAREHOUSES.length} warehouses` : `${rows.length}개 항목 · ${WAREHOUSES.length}개 창고`}
        actions={<button className="btn btn-accent"><Icon.Receive /> {lang === "en" ? "Receive" : "입고"}</button>}
      />
      <div className="tbl-wrap">
        <div className="tbl-toolbar">
          <div className="filters">
            <button className={`chip ${whFilter === 0 ? "on" : ""}`} onClick={() => setWhFilter(0)}>{lang === "en" ? "All warehouses" : "전체 창고"}</button>
            {WAREHOUSES.map((w) => (
              <button key={w.id} className={`chip ${whFilter === w.id ? "on" : ""}`} onClick={() => setWhFilter(w.id)}>{w.name}</button>
            ))}
          </div>
          <div className="right">
            <div className="search" style={{ width: 220 }}>
              <span className="ico"><Icon.Search /></span>
              <input placeholder={lang === "en" ? "Search SKU..." : "상품·SKU 검색"} value={search} onChange={(e) => setSearch(e.target.value)} />
            </div>
          </div>
        </div>
        <table className="tbl">
          <thead>
            <tr>
              <th>{lang === "en" ? "Product" : "상품"}</th>
              <th>SKU</th>
              <th>{lang === "en" ? "Warehouse" : "창고"}</th>
              <th className="num">{lang === "en" ? "Total" : "총량"}</th>
              <th className="num">{lang === "en" ? "Reserved" : "예약"}</th>
              <th className="num">{lang === "en" ? "Available" : "가용"}</th>
              <th style={{ width: 200 }}>{lang === "en" ? "Level" : "수준"}</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {rows.map((s, i) => {
              const p = lookupProduct(s.productId);
              const avail = s.total - s.reserved;
              const pct = s.total > 0 ? (avail / s.total) * 100 : 0;
              const tone = avail <= 5 ? "danger" : avail <= 20 ? "warn" : "";
              return (
                <tr key={`${s.productId}-${s.warehouseId}`} className="stagger" style={{ animationDelay: `${i * 18}ms` }}>
                  <td className="strong">{p.name}</td>
                  <td className="mono muted" style={{ fontSize: 11.5 }}>{p.sku}</td>
                  <td className="muted">{lookupWh(s.warehouseId).name}</td>
                  <td className="num">{fmtN(s.total)}</td>
                  <td className="num muted">{fmtN(s.reserved)}</td>
                  <td className="num strong">{tone === "danger" && <span style={{ marginRight: 6 }}><Icon.Warn /></span>}{fmtN(avail)}</td>
                  <td>
                    <div className={`pbar ${tone}`}><div className="pbar-fill" style={{ width: `${pct}%` }} /></div>
                  </td>
                  <td style={{ textAlign: "right" }}>
                    {avail <= 10 && <span className="badge warn">{lang === "en" ? "Low" : "부족"}</span>}
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>
    </>
  );
}

// ============================================================
// PURCHASE ORDERS
// ============================================================
function PurchaseOrdersPage({ store, lang, onCreate }) {
  return (
    <>
      <PageHead
        title={lang === "en" ? "Purchase Orders" : "발주 관리"}
        sub={lang === "en" ? `${store.purchaseOrders.length} POs · auto-replenishment enabled` : `총 ${store.purchaseOrders.length}건 · 자동발주 활성`}
        actions={<button className="btn btn-accent" onClick={onCreate}><Icon.Plus /> {lang === "en" ? "New PO" : "발주 등록"}</button>}
      />
      <div className="tbl-wrap">
        <table className="tbl">
          <thead>
            <tr>
              <th>ID</th>
              <th>{lang === "en" ? "Supplier" : "공급자"}</th>
              <th>{lang === "en" ? "Product" : "상품"}</th>
              <th className="num">{lang === "en" ? "Qty" : "수량"}</th>
              <th className="num">{lang === "en" ? "Unit price" : "단가"}</th>
              <th className="num">{lang === "en" ? "Total" : "총액"}</th>
              <th>{lang === "en" ? "Issued" : "발주일"}</th>
              <th>{lang === "en" ? "Status" : "상태"}</th>
            </tr>
          </thead>
          <tbody>
            {store.purchaseOrders.map((po, i) => {
              const p = lookupProduct(po.productId);
              return (
                <tr key={po.id} className="stagger" style={{ animationDelay: `${i * 22}ms` }}>
                  <td className="id">PO-{po.id}</td>
                  <td className="strong">{po.supplier}</td>
                  <td>{p.name}<div className="muted mono" style={{ fontSize: 11 }}>{p.sku}</div></td>
                  <td className="num">{po.status === "PARTIAL" ? `${po.received}/${po.quantity}` : po.quantity}</td>
                  <td className="num muted">{fmtKRW(po.unitPrice)}</td>
                  <td className="num strong">{fmtKRW(po.unitPrice * po.quantity)}</td>
                  <td className="muted">{fmtDateTime(po.issuedAt)}</td>
                  <td><StatusBadge status={po.status} lang={lang} /></td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>
    </>
  );
}

Object.assign(window, { ShipmentsPage, ShipmentPanel, DeliveriesPage, StocksPage, PurchaseOrdersPage });
