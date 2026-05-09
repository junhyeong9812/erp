/* global React, PageHead, Icon, StatusBadge, useToast, SidePanel */
/* global fmtKRW, fmtN, fmtDate, fmtDateTime */
/* global QUOTES, CUSTOMERS, PRODUCTS, SUPPLIERS, lookupCustomer, lookupProduct, lookupWh */
/* global useExtStore */

const { useState: uS5, useMemo: uM5, useEffect: uE5, useRef: uR5 } = React;

// ============================================================
// QUOTES PAGE V2 — list + detail panel + accept→convert flow
// ============================================================
function QuotesPageV2({ store, setStore, lang, onConverted }) {
  const ext = useExtStore();
  const [filter, setFilter] = uS5("ALL");
  const [openId, setOpenId] = uS5(null);
  const items = uM5(
    () => filter === "ALL" ? ext.quotes : ext.quotes.filter(q => q.status === filter),
    [filter, ext.quotes]
  );
  const counts = ext.quotes.reduce(
    (c, q) => ({ ...c, [q.status]: (c[q.status] || 0) + 1 }),
    { ALL: ext.quotes.length }
  );
  const STATUS = {
    ACTIVE:   { ko: "유효",     en: "Active",   tone: "info" },
    ACCEPTED: { ko: "수주전환", en: "Accepted", tone: "ok" },
    EXPIRED:  { ko: "만료",     en: "Expired",  tone: "neutral" },
    REJECTED: { ko: "거절",     en: "Rejected", tone: "danger" },
  };

  return (
    <>
      <PageHead title={lang === "en" ? "Quotes" : "견적 관리"}
        sub={lang === "en"
          ? `${ext.quotes.length} quotes · ${counts.ACTIVE || 0} active`
          : `총 ${ext.quotes.length}건 · 유효 ${counts.ACTIVE || 0}건`}
        actions={<button className="btn btn-accent"><Icon.Plus /> {lang === "en" ? "New quote" : "견적 발행"}</button>} />

      <div className="kpi-grid" style={{ gridTemplateColumns: "repeat(4, 1fr)" }}>
        <div className="kpi"><div className="kpi-label">{lang === "en" ? "Active value" : "유효 견적액"}</div><div className="kpi-num tnum">{fmtKRW(ext.quotes.filter(q => q.status === "ACTIVE").reduce((s, q) => s + q.total, 0))}</div><div className="kpi-meta"><span className="muted">{counts.ACTIVE || 0} {lang === "en" ? "quotes" : "건"}</span></div></div>
        <div className="kpi"><div className="kpi-label">{lang === "en" ? "Acceptance rate" : "수주 전환율"}</div><div className="kpi-num tnum">{Math.round(((counts.ACCEPTED || 0) / Math.max(1, ext.quotes.length)) * 100)}<span className="kpi-unit">%</span></div><div className="kpi-meta"><span className="kpi-delta up">▲ 4.2%</span><span className="vs">{lang === "en" ? "MoM" : "전월 대비"}</span></div></div>
        <div className="kpi"><div className="kpi-label">{lang === "en" ? "Avg. value" : "평균 견적액"}</div><div className="kpi-num tnum">{fmtKRW(Math.round(ext.quotes.reduce((s, q) => s + q.total, 0) / Math.max(1, ext.quotes.length)))}</div></div>
        <div className="kpi"><div className="kpi-label">{lang === "en" ? "Expiring soon" : "만료 임박"}</div><div className="kpi-num tnum">{ext.quotes.filter(q => q.status === "ACTIVE" && new Date(q.validUntil) - new Date("2026-05-04") < 7 * 86400000).length}<span className="kpi-unit">{lang === "en" ? "case" : "건"}</span></div><div className="kpi-meta"><span className="muted">{lang === "en" ? "within 7 days" : "7일 이내"}</span></div></div>
      </div>

      <div className="tbl-wrap">
        <div className="tbl-toolbar">
          <div className="filters">
            {["ALL", "ACTIVE", "ACCEPTED", "EXPIRED", "REJECTED"].map(f => (
              <button key={f} className={`chip ${filter === f ? "on" : ""}`} onClick={() => setFilter(f)}>
                {f === "ALL" ? (lang === "en" ? "All" : "전체") : (lang === "en" ? STATUS[f].en : STATUS[f].ko)}
                <span className="count">{counts[f] || 0}</span>
              </button>
            ))}
          </div>
        </div>
        <table className="tbl">
          <thead><tr>
            <th>ID</th><th>{lang === "en" ? "Customer" : "고객"}</th><th>{lang === "en" ? "Items" : "품목"}</th>
            <th className="num">{lang === "en" ? "Total" : "총액"}</th><th>{lang === "en" ? "Valid until" : "유효기한"}</th>
            <th>{lang === "en" ? "Created" : "발행일"}</th><th>{lang === "en" ? "Status" : "상태"}</th>
            <th></th>
          </tr></thead>
          <tbody>
            {items.map((q, i) => {
              const meta = STATUS[q.status];
              const daysLeft = Math.ceil((new Date(q.validUntil) - new Date("2026-05-04")) / 86400000);
              return (
                <tr key={q.id} className="stagger row-clickable" style={{ animationDelay: `${i * 22}ms` }}
                    onClick={() => setOpenId(q.id)}>
                  <td className="id">QT-{q.id}</td>
                  <td className="strong">{lookupCustomer(q.customerId).name}</td>
                  <td className="muted">{lookupProduct(q.lines[0].productId).name}{q.lines.length > 1 ? ` 외 ${q.lines.length - 1}` : ""}</td>
                  <td className="num strong">{fmtKRW(q.total)}</td>
                  <td className="muted">
                    {fmtDate(q.validUntil)}
                    {q.status === "ACTIVE" && daysLeft >= 0 && daysLeft <= 7 && (
                      <span className="badge danger" style={{ marginLeft: 6, fontSize: 10 }}>D-{daysLeft}</span>
                    )}
                  </td>
                  <td className="muted">{fmtDateTime(q.createdAt)}</td>
                  <td><span className={`badge ${meta.tone}`}>{lang === "en" ? meta.en : meta.ko}</span></td>
                  <td style={{ textAlign: "right" }}>
                    <button className="btn btn-sm btn-ghost" onClick={e => { e.stopPropagation(); setOpenId(q.id); }}>
                      {lang === "en" ? "View" : "상세"}
                    </button>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>

      <SidePanel open={!!openId} onClose={() => setOpenId(null)}>
        {openId && (
          <QuotePanel
            quoteId={openId}
            store={store} setStore={setStore}
            onClose={() => setOpenId(null)}
            onConverted={(orderId) => { setOpenId(null); onConverted && onConverted(orderId); }}
            lang={lang} />
        )}
      </SidePanel>
    </>
  );
}

// ============================================================
// QUOTE DETAIL PANEL — accept (convert to order) / reject
// ============================================================
function QuotePanel({ quoteId, store, setStore, onClose, onConverted, lang }) {
  const ext = useExtStore();
  const toast = useToast();
  const quote = ext.quotes.find(q => q.id === quoteId);
  const [phase, setPhase] = uS5("view"); // view | confirm | processing | done
  const [createdOrderId, setCreatedOrderId] = uS5(null);
  if (!quote) return null;
  const customer = lookupCustomer(quote.customerId);
  const daysLeft = Math.ceil((new Date(quote.validUntil) - new Date("2026-05-04")) / 86400000);

  const convert = () => {
    setPhase("processing");
    setTimeout(() => {
      const newOrderId = 2500 + store.orders.length + 1;
      const newOrder = {
        id: newOrderId,
        customerId: quote.customerId,
        productId: quote.lines[0].productId,
        warehouseId: 1,
        quantity: quote.lines.reduce((s, l) => s + l.quantity, 0),
        amount: quote.total,
        status: "PLACED",
        paid: false,
        createdAt: new Date().toISOString(),
        sourceQuoteId: quote.id,
      };
      setStore(prev => ({ ...prev, orders: [newOrder, ...prev.orders] }));
      ext.setQuotes(prev => prev.map(q => q.id === quote.id ? { ...q, status: "ACCEPTED", convertedOrderId: newOrderId } : q));
      setCreatedOrderId(newOrderId);
      setPhase("done");
      toast.push(lang === "en" ? `Order #${newOrderId} created from QT-${quote.id}` : `QT-${quote.id} → 주문 #${newOrderId} 자동 생성`);
    }, 800);
  };

  const reject = () => {
    ext.setQuotes(prev => prev.map(q => q.id === quote.id ? { ...q, status: "REJECTED" } : q));
    toast.push(lang === "en" ? "Quote rejected" : "견적이 거절되었습니다");
    onClose();
  };

  if (phase === "done") {
    return (
      <>
        <div className="sp-head">
          <div>
            <div className="sp-title">{lang === "en" ? "Conversion complete" : "수주 전환 완료"}</div>
            <div className="sp-sub">QT-{quote.id} → ORD-{createdOrderId}</div>
          </div>
          <button className="sp-close" onClick={onClose}><Icon.X /></button>
        </div>
        <div className="sp-body" style={{ alignItems: "center", paddingTop: 32 }}>
          <div className="success-mark">
            <svg viewBox="0 0 32 32"><circle cx="16" cy="16" r="14" /><path d="M9 16.5l5 5 9-10" /></svg>
          </div>
          <div style={{ textAlign: "center", marginTop: 18 }}>
            <div className="strong" style={{ fontSize: 16 }}>{lang === "en" ? "Order created" : "주문이 생성되었습니다"}</div>
            <div className="muted" style={{ fontSize: 12.5, marginTop: 4 }}>
              {customer.name} · {fmtKRW(quote.total)}
            </div>
          </div>
          <div className="convert-trace">
            <div className="trace-step done">
              <div className="trace-dot"><svg viewBox="0 0 24 24"><path d="M5 12 l4 4 l10 -11" /></svg></div>
              <div>
                <div className="strong" style={{ fontSize: 13 }}>QT-{quote.id}</div>
                <div className="muted" style={{ fontSize: 11.5 }}>{lang === "en" ? "Quote accepted" : "견적 수락"}</div>
              </div>
            </div>
            <div className="trace-line" />
            <div className="trace-step done">
              <div className="trace-dot accent"><svg viewBox="0 0 24 24"><path d="M5 12 l4 4 l10 -11" /></svg></div>
              <div>
                <div className="strong" style={{ fontSize: 13 }}>ORD-{createdOrderId}</div>
                <div className="muted" style={{ fontSize: 11.5 }}>{lang === "en" ? "Order placed · awaiting payment" : "수주 등록 · 결제 대기"}</div>
              </div>
            </div>
            <div className="trace-line dim" />
            <div className="trace-step wait">
              <div className="trace-dot"><span className="trace-dot-pip" /></div>
              <div>
                <div className="strong" style={{ fontSize: 13, color: "var(--text-3)" }}>{lang === "en" ? "Payment" : "결제"}</div>
                <div className="muted" style={{ fontSize: 11.5 }}>{lang === "en" ? "→ Shipment → Delivery" : "→ 출고 → 배송"}</div>
              </div>
            </div>
          </div>
        </div>
        <div className="sp-foot">
          <button className="btn" onClick={onClose}>{lang === "en" ? "Close" : "닫기"}</button>
          <button className="btn btn-accent" style={{ marginLeft: "auto" }}
            onClick={() => onConverted && onConverted(createdOrderId)}>
            {lang === "en" ? `Open Order #${createdOrderId}` : `주문 #${createdOrderId} 열기`}
          </button>
        </div>
      </>
    );
  }

  return (
    <>
      <div className="sp-head">
        <div>
          <div className="sp-title">{lang === "en" ? "Quote" : "견적"} QT-{quote.id}</div>
          <div className="sp-sub">{customer.name} · {customer.code}</div>
        </div>
        <button className="sp-close" onClick={onClose}><Icon.X /></button>
      </div>

      <div className="sp-body">
        <div>
          <div className="muted" style={{ fontSize: 11, fontWeight: 600, letterSpacing: "0.06em", marginBottom: 8 }}>
            {lang === "en" ? "SUMMARY" : "개요"}
          </div>
          <div className="kv-grid" style={{ gridTemplateColumns: "1fr 1fr" }}>
            <div><span className="muted">{lang === "en" ? "Customer" : "고객"}</span><span className="strong">{customer.name}</span></div>
            <div><span className="muted">{lang === "en" ? "Total" : "총액"}</span><span className="strong tnum">{fmtKRW(quote.total)}</span></div>
            <div><span className="muted">{lang === "en" ? "Valid until" : "유효기한"}</span><span>{fmtDate(quote.validUntil)} {quote.status === "ACTIVE" && (daysLeft <= 7 ? <span className="badge danger" style={{ marginLeft: 4, fontSize: 10 }}>D-{daysLeft}</span> : <span className="muted" style={{ fontSize: 11 }}>(D-{daysLeft})</span>)}</span></div>
            <div><span className="muted">{lang === "en" ? "Status" : "상태"}</span><span><StatusBadge status={quote.status} lang={lang} /></span></div>
          </div>
        </div>

        <div>
          <div className="muted" style={{ fontSize: 11, fontWeight: 600, letterSpacing: "0.06em", marginBottom: 8 }}>
            {lang === "en" ? "LINE ITEMS" : "품목"}
          </div>
          <div className="line-items">
            {quote.lines.map((l, i) => {
              const p = lookupProduct(l.productId);
              return (
                <div key={i} className="line-row">
                  <div style={{ flex: 1 }}>
                    <div className="strong" style={{ fontSize: 13 }}>{p.name}</div>
                    <div className="mono muted" style={{ fontSize: 11 }}>{p.sku}</div>
                  </div>
                  <div className="muted tnum" style={{ fontSize: 12, width: 70, textAlign: "right" }}>
                    {fmtN(l.quantity)} × {fmtKRW(l.unitPrice).replace("₩", "")}
                  </div>
                  <div className="strong tnum" style={{ width: 100, textAlign: "right" }}>{fmtKRW(l.quantity * l.unitPrice)}</div>
                </div>
              );
            })}
            <div className="line-row total">
              <div style={{ flex: 1 }} className="strong">{lang === "en" ? "Total" : "합계"}</div>
              <div className="strong tnum" style={{ fontSize: 14 }}>{fmtKRW(quote.total)}</div>
            </div>
          </div>
        </div>

        {quote.status === "ACTIVE" && (
          <div className="auto-note">
            <Icon.Coin />
            <span>{lang === "en"
              ? "Accepting will create a new Order in PLACED status with this customer and lines, and link it back to this quote."
              : "수락 시 동일한 고객·품목으로 신규 주문(수주 상태)이 자동 생성되며, 본 견적과 연결됩니다."}</span>
          </div>
        )}

        {quote.status === "ACCEPTED" && quote.convertedOrderId && (
          <div className="auto-note" style={{ background: "var(--ok-soft)", color: "var(--ok-ink)" }}>
            <Icon.Check />
            <span>{lang === "en"
              ? `Already converted → Order #${quote.convertedOrderId}`
              : `수주 전환 완료 → 주문 #${quote.convertedOrderId}`}</span>
          </div>
        )}

        {phase === "processing" && (
          <div style={{ display: "grid", placeItems: "center", padding: 16 }}>
            <div className="spinner" />
            <div className="muted" style={{ fontSize: 12, marginTop: 10 }}>{lang === "en" ? "Creating order…" : "주문 생성 중…"}</div>
          </div>
        )}
      </div>

      <div className="sp-foot">
        {quote.status === "ACTIVE" ? (
          <>
            <button className="btn" onClick={reject} disabled={phase !== "view"}>{lang === "en" ? "Reject" : "거절"}</button>
            <button className="btn btn-accent" onClick={convert} disabled={phase !== "view"}
              style={{ marginLeft: "auto" }}>
              {phase === "processing" ? (lang === "en" ? "Converting…" : "전환 중…") : (lang === "en" ? "Accept & convert" : "수락하고 수주 전환")}
            </button>
          </>
        ) : (
          <button className="btn" onClick={onClose} style={{ marginLeft: "auto" }}>{lang === "en" ? "Close" : "닫기"}</button>
        )}
      </div>
    </>
  );
}

// ============================================================
// PURCHASE ORDERS PAGE V2 — list + goods receipt panel
// ============================================================
function PurchaseOrdersPageV2({ store, setStore, lang, onCreate }) {
  const [openId, setOpenId] = uS5(null);
  const STATUS_LABEL = {
    ISSUED:    { ko: "발주",     en: "Issued",    tone: "info" },
    PARTIAL:   { ko: "부분입고", en: "Partial",   tone: "warn" },
    COMPLETED: { ko: "입고완료", en: "Completed", tone: "ok" },
  };
  const totalIssued = store.purchaseOrders.filter(po => po.status === "ISSUED").length;
  const totalPartial = store.purchaseOrders.filter(po => po.status === "PARTIAL").length;
  const inboundValue = store.purchaseOrders
    .filter(po => po.status !== "COMPLETED")
    .reduce((s, po) => s + po.unitPrice * (po.quantity - (po.received || 0)), 0);

  return (
    <>
      <PageHead
        title={lang === "en" ? "Purchase Orders" : "발주 관리"}
        sub={lang === "en"
          ? `${store.purchaseOrders.length} POs · ${totalIssued + totalPartial} awaiting receipt`
          : `총 ${store.purchaseOrders.length}건 · 입고 대기 ${totalIssued + totalPartial}건`}
        actions={<button className="btn btn-accent" onClick={onCreate}><Icon.Plus /> {lang === "en" ? "New PO" : "발주 등록"}</button>}
      />

      <div className="kpi-grid" style={{ gridTemplateColumns: "repeat(3, 1fr)" }}>
        <div className="kpi"><div className="kpi-label">{lang === "en" ? "Awaiting receipt" : "입고 대기"}</div><div className="kpi-num tnum">{totalIssued + totalPartial}<span className="kpi-unit">{lang === "en" ? "POs" : "건"}</span></div><div className="kpi-meta"><span className="muted">{totalPartial} {lang === "en" ? "partial" : "부분입고"}</span></div></div>
        <div className="kpi"><div className="kpi-label">{lang === "en" ? "Inbound value" : "입고 예정액"}</div><div className="kpi-num tnum">{fmtKRW(inboundValue)}</div></div>
        <div className="kpi"><div className="kpi-label">{lang === "en" ? "Auto-replenish" : "자동발주"}</div><div className="kpi-num tnum">ON</div><div className="kpi-meta"><span className="muted">{lang === "en" ? "12 SKUs monitored" : "12개 품목 모니터링"}</span></div></div>
      </div>

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
              <th></th>
            </tr>
          </thead>
          <tbody>
            {store.purchaseOrders.map((po, i) => {
              const p = lookupProduct(po.productId);
              const meta = STATUS_LABEL[po.status] || { ko: po.status, en: po.status, tone: "neutral" };
              const received = po.received || (po.status === "COMPLETED" ? po.quantity : 0);
              const pct = Math.round((received / po.quantity) * 100);
              const canReceive = po.status === "ISSUED" || po.status === "PARTIAL";
              return (
                <tr key={po.id} className={`stagger ${canReceive ? "row-clickable" : ""}`}
                    style={{ animationDelay: `${i * 22}ms` }}
                    onClick={() => canReceive && setOpenId(po.id)}>
                  <td className="id">PO-{po.id}</td>
                  <td className="strong">{po.supplier}</td>
                  <td>{p.name}<div className="muted mono" style={{ fontSize: 11 }}>{p.sku}</div></td>
                  <td className="num">
                    <div style={{ display: "flex", flexDirection: "column", alignItems: "flex-end", gap: 3 }}>
                      <span className="tnum">{received}/{po.quantity}</span>
                      <div className="po-bar"><div className="po-bar-fill" style={{ width: `${pct}%` }} /></div>
                    </div>
                  </td>
                  <td className="num muted">{fmtKRW(po.unitPrice)}</td>
                  <td className="num strong">{fmtKRW(po.unitPrice * po.quantity)}</td>
                  <td className="muted">{fmtDateTime(po.issuedAt)}</td>
                  <td><span className={`badge ${meta.tone}`}>{lang === "en" ? meta.en : meta.ko}</span></td>
                  <td style={{ textAlign: "right" }}>
                    {canReceive && (
                      <button className="btn btn-sm btn-accent"
                        onClick={e => { e.stopPropagation(); setOpenId(po.id); }}>
                        {lang === "en" ? "Receive" : "입고 검수"}
                      </button>
                    )}
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>

      <SidePanel open={!!openId} onClose={() => setOpenId(null)}>
        {openId && (
          <GoodsReceiptPanel
            poId={openId}
            store={store} setStore={setStore}
            onClose={() => setOpenId(null)}
            lang={lang} />
        )}
      </SidePanel>
    </>
  );
}

// ============================================================
// GOODS RECEIPT PANEL — checklist + qty + auto-update stock
// ============================================================
function GoodsReceiptPanel({ poId, store, setStore, onClose, lang }) {
  const toast = useToast();
  const ext = useExtStore();
  const po = store.purchaseOrders.find(p => p.id === poId);
  const product = po ? lookupProduct(po.productId) : null;
  const alreadyReceived = (po && po.received) || 0;
  const remaining = po ? po.quantity - alreadyReceived : 0;

  const [qty, setQty] = uS5(remaining);
  const [warehouseId, setWarehouseId] = uS5(1);
  const [checks, setChecks] = uS5({ count: false, condition: false, docs: false });
  const [defects, setDefects] = uS5(0);
  const [phase, setPhase] = uS5("form"); // form | processing | done
  const [resultStockBefore, setResultStockBefore] = uS5(0);
  const [resultStockAfter, setResultStockAfter] = uS5(0);
  const [createdClaimId, setCreatedClaimId] = uS5(null);

  if (!po || !product) return null;
  const allChecked = checks.count && checks.condition && checks.docs;
  const acceptedQty = qty - defects;

  const submit = () => {
    if (!allChecked || acceptedQty <= 0) return;
    setPhase("processing");
    setTimeout(() => {
      // 1) Update PO
      const newReceived = alreadyReceived + acceptedQty;
      const newStatus = newReceived >= po.quantity ? "COMPLETED" : "PARTIAL";

      // 2) Update stock for the product at chosen warehouse
      const stockBefore = store.stocks
        .filter(s => s.productId === po.productId && s.warehouseId === warehouseId)
        .reduce((s, x) => s + x.total, 0)
        || store.stocks.filter(s => s.productId === po.productId).reduce((s, x) => s + x.total, 0);
      setResultStockBefore(stockBefore);
      setResultStockAfter(stockBefore + acceptedQty);

      setStore(prev => {
        const next = {
          ...prev,
          purchaseOrders: prev.purchaseOrders.map(x =>
            x.id === po.id ? { ...x, received: newReceived, status: newStatus } : x
          ),
        };
        // bump stock — find matching row, else add
        let stockUpdated = false;
        next.stocks = prev.stocks.map(s => {
          if (s.productId === po.productId && s.warehouseId === warehouseId) {
            stockUpdated = true;
            return { ...s, total: s.total + acceptedQty };
          }
          return s;
        });
        if (!stockUpdated) {
          // fallback — bump first matching product row
          let bumped = false;
          next.stocks = next.stocks.map(s => {
            if (!bumped && s.productId === po.productId) { bumped = true; return { ...s, total: s.total + acceptedQty }; }
            return s;
          });
        }
        return next;
      });

      setPhase("done");
      // Auto-draft a supplier claim if defects > 0
      if (defects > 0) {
        const newClaimId = 5200 + ext.supplierClaims.length + 1;
        const newClaim = {
          id: newClaimId, poId: po.id, supplier: po.supplier, supplierId: null,
          productId: po.productId, defectQty: defects, totalReceived: acceptedQty + defects,
          defectType: "DEFECTIVE", status: "DRAFT",
          openedAt: new Date().toISOString(), priority: defects >= 5 ? "HIGH" : "MEDIUM",
          description: lang === "en" ? `Auto-drafted from receipt of PO-${po.id} · ${defects} defective units` : `PO-${po.id} 입고 검수에서 자동 초안 작성 · 불량 ${defects}개`,
          history: [
            { ts: new Date().toISOString(), actor: "system",
              event: "DRAFT_CREATED",
              note: lang === "en"
                ? `Auto-drafted from goods receipt · ${defects} defective units`
                : `입고 검수에서 자동 초안 작성 · 불량 ${defects}개` },
          ],
        };
        ext.setSupplierClaims(prev => [newClaim, ...prev]);
        setCreatedClaimId(newClaimId);
      }
      toast.push(lang === "en"
        ? `Received ${acceptedQty} units · stock updated${defects > 0 ? ` · claim drafted` : ""}`
        : `입고 ${acceptedQty}개 처리 · 재고 자동 반영${defects > 0 ? ` · 클레임 초안 생성` : ""}`);
    }, 900);
  };

  if (phase === "done") {
    return (
      <>
        <div className="sp-head">
          <div>
            <div className="sp-title">{lang === "en" ? "Receipt complete" : "입고 처리 완료"}</div>
            <div className="sp-sub">PO-{po.id} · {product.sku}</div>
          </div>
          <button className="sp-close" onClick={onClose}><Icon.X /></button>
        </div>
        <div className="sp-body" style={{ alignItems: "center", paddingTop: 28 }}>
          <div className="success-mark"><svg viewBox="0 0 32 32"><circle cx="16" cy="16" r="14" /><path d="M9 16.5l5 5 9-10" /></svg></div>
          <div style={{ textAlign: "center", marginTop: 14 }}>
            <div className="strong" style={{ fontSize: 15 }}>{lang === "en" ? "Goods received" : "입고가 완료되었습니다"}</div>
            <div className="muted" style={{ fontSize: 12.5, marginTop: 4 }}>{product.name} · {acceptedQty}{lang === "en" ? " ea" : "개"}</div>
          </div>

          <div className="stock-delta">
            <div className="muted" style={{ fontSize: 11, fontWeight: 600, letterSpacing: "0.06em", marginBottom: 8 }}>
              {lang === "en" ? "STOCK UPDATE" : "재고 변동"}
            </div>
            <div style={{ display: "flex", alignItems: "center", justifyContent: "center", gap: 14 }}>
              <div style={{ textAlign: "center" }}>
                <div className="muted" style={{ fontSize: 11 }}>{lang === "en" ? "Before" : "이전"}</div>
                <div className="tnum" style={{ fontSize: 20, fontWeight: 600, color: "var(--text-2)" }}>{fmtN(resultStockBefore)}</div>
              </div>
              <div className="stock-arrow">
                <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" strokeWidth="2"><path d="M5 12 h14 m-5 -5 l5 5 l-5 5" /></svg>
              </div>
              <div style={{ textAlign: "center" }}>
                <div className="muted" style={{ fontSize: 11 }}>{lang === "en" ? "After" : "이후"}</div>
                <div className="tnum" style={{ fontSize: 20, fontWeight: 700, color: "var(--ok-ink)" }}>
                  +{fmtN(resultStockAfter)}
                </div>
              </div>
            </div>
            <div style={{ textAlign: "center", marginTop: 10 }}>
              <span className="badge ok">+{fmtN(acceptedQty)} {lang === "en" ? "units" : "개"}</span>
              {defects > 0 && <span className="badge danger" style={{ marginLeft: 6 }}>{lang === "en" ? `${defects} defective` : `불량 ${defects}`}</span>}
            </div>
          </div>

          {createdClaimId && (
            <div className="claim-callout">
              <div className="claim-callout-head">
                <span className="badge danger">{lang === "en" ? "CLAIM DRAFTED" : "클레임 초안"}</span>
                <span className="mono muted" style={{ fontSize: 11.5 }}>SC-{createdClaimId}</span>
              </div>
              <div className="claim-callout-body">
                {lang === "en"
                  ? `A draft claim has been created for ${defects} defective units. Open the Suppliers · Claims page to review and send it to ${po.supplier}.`
                  : `불량 ${defects}개에 대한 클레임 초안이 자동 생성되었습니다. "공급자 · 클레임" 페이지에서 검토 후 ${po.supplier}에게 송부하세요.`}
              </div>
            </div>
          )}
        </div>
        <div className="sp-foot">
          <button className="btn btn-accent" onClick={onClose} style={{ marginLeft: "auto" }}>{lang === "en" ? "Done" : "확인"}</button>
        </div>
      </>
    );
  }

  return (
    <>
      <div className="sp-head">
        <div>
          <div className="sp-title">{lang === "en" ? "Goods Receipt" : "입고 검수"}</div>
          <div className="sp-sub">PO-{po.id} · {po.supplier}</div>
        </div>
        <button className="sp-close" onClick={onClose}><Icon.X /></button>
      </div>

      <div className="sp-body">
        <div>
          <div className="muted" style={{ fontSize: 11, fontWeight: 600, letterSpacing: "0.06em", marginBottom: 8 }}>
            {lang === "en" ? "ITEM" : "품목 정보"}
          </div>
          <div className="kv-grid" style={{ gridTemplateColumns: "1fr 1fr" }}>
            <div><span className="muted">{lang === "en" ? "Product" : "상품"}</span><span className="strong">{product.name}</span></div>
            <div><span className="muted">SKU</span><span className="mono">{product.sku}</span></div>
            <div><span className="muted">{lang === "en" ? "Ordered" : "발주 수량"}</span><span className="strong tnum">{fmtN(po.quantity)}</span></div>
            <div><span className="muted">{lang === "en" ? "Already received" : "기 입고"}</span><span className="strong tnum">{fmtN(alreadyReceived)}</span></div>
          </div>
          {alreadyReceived > 0 && (
            <div className="po-progress">
              <div className="po-progress-bar">
                <div className="po-progress-fill" style={{ width: `${(alreadyReceived / po.quantity) * 100}%` }} />
              </div>
              <div className="muted" style={{ fontSize: 11, marginTop: 4 }}>
                {Math.round((alreadyReceived / po.quantity) * 100)}% {lang === "en" ? "received" : "입고 완료"}
              </div>
            </div>
          )}
        </div>

        <div className="field">
          <label className="field-label">{lang === "en" ? "Receive into warehouse" : "입고 창고"}</label>
          <select className="select-input" value={warehouseId} onChange={e => setWarehouseId(Number(e.target.value))}>
            <option value={1}>{lang === "en" ? "Seoul DC" : "서울 물류센터"}</option>
            <option value={2}>{lang === "en" ? "Busan Hub" : "부산 허브"}</option>
            <option value={3}>{lang === "en" ? "Daejeon Center" : "대전 센터"}</option>
          </select>
        </div>

        <div className="field">
          <label className="field-label">{lang === "en" ? "Quantity received" : "입고 수량"}</label>
          <input type="number" className="input" value={qty} min={1} max={remaining}
            onChange={e => setQty(Math.max(0, Math.min(remaining, Number(e.target.value))))} />
          <div className="amount-chips">
            <button className="chip-sm" onClick={() => setQty(remaining)}>{lang === "en" ? "All remaining" : "잔여 전량"}</button>
            <button className="chip-sm" onClick={() => setQty(Math.round(remaining / 2))}>{lang === "en" ? "Half" : "반량"}</button>
            <span className="muted" style={{ fontSize: 11.5, marginLeft: "auto", alignSelf: "center" }}>
              {lang === "en" ? "Remaining" : "잔여"} {fmtN(remaining)}
            </span>
          </div>
        </div>

        <div className="field">
          <label className="field-label">{lang === "en" ? "Defective units" : "불량 수량"}</label>
          <input type="number" className="input" value={defects} min={0} max={qty}
            onChange={e => setDefects(Math.max(0, Math.min(qty, Number(e.target.value))))} />
          {defects > 0 && (
            <div className="muted" style={{ fontSize: 11.5, marginTop: 4 }}>
              {lang === "en" ? "Accepted" : "양품"}: <span className="strong">{fmtN(acceptedQty)}</span> · {lang === "en" ? "claim will be drafted" : "클레임 자동 초안 작성"}
            </div>
          )}
        </div>

        <div>
          <div className="muted" style={{ fontSize: 11, fontWeight: 600, letterSpacing: "0.06em", marginBottom: 8 }}>
            {lang === "en" ? "INSPECTION CHECKLIST" : "검수 체크리스트"}
          </div>
          <div className="check-list">
            {[
              { key: "count",     ko: "수량 확인 (검수원 카운트 일치)",     en: "Quantity matches packing list" },
              { key: "condition", ko: "외관·포장 상태 양호",                en: "Packaging & condition OK" },
              { key: "docs",      ko: "거래명세서·인수증 확인",             en: "Documents & receipt verified" },
            ].map(item => (
              <label key={item.key} className={`check-row ${checks[item.key] ? "checked" : ""}`}>
                <span className="check-box">
                  {checks[item.key] && (
                    <svg viewBox="0 0 24 24"><path d="M5 12 l4 4 l10 -11" /></svg>
                  )}
                </span>
                <input type="checkbox" checked={checks[item.key]}
                  onChange={e => setChecks(s => ({ ...s, [item.key]: e.target.checked }))} />
                <span>{lang === "en" ? item.en : item.ko}</span>
              </label>
            ))}
          </div>
        </div>

        <div className="auto-note">
          <Icon.Coin />
          <span>{lang === "en"
            ? `Confirming will add ${fmtN(acceptedQty)} units to stock and ${alreadyReceived + acceptedQty >= po.quantity ? "close the PO" : "mark the PO as PARTIAL"}.`
            : `확정 시 재고에 ${fmtN(acceptedQty)}개가 추가되고, ${alreadyReceived + acceptedQty >= po.quantity ? "발주가 완료 처리" : "발주 상태가 부분입고로 변경"}됩니다.`}</span>
        </div>

        {phase === "processing" && (
          <div style={{ display: "grid", placeItems: "center", padding: 12 }}>
            <div className="spinner" />
            <div className="muted" style={{ fontSize: 12, marginTop: 10 }}>{lang === "en" ? "Updating stock…" : "재고 반영 중…"}</div>
          </div>
        )}
      </div>

      <div className="sp-foot">
        <button className="btn" onClick={onClose}>{lang === "en" ? "Cancel" : "취소"}</button>
        <button className="btn btn-accent" onClick={submit}
          disabled={!allChecked || acceptedQty <= 0 || phase !== "form"}
          style={{ marginLeft: "auto" }}>
          {lang === "en"
            ? `Confirm receipt · ${fmtN(acceptedQty)}`
            : `입고 확정 · ${fmtN(acceptedQty)}개`}
        </button>
      </div>
    </>
  );
}

Object.assign(window, { QuotesPageV2, QuotePanel, PurchaseOrdersPageV2, GoodsReceiptPanel });
