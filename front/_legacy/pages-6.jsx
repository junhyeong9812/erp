/* global React, PageHead, Icon, StatusBadge, useToast, SidePanel */
/* global fmtKRW, fmtN, fmtDate, fmtDateTime, fmtRelative */
/* global SUPPLIERS, lookupProduct */
/* global useExtStore */

const { useState: uS6, useMemo: uM6, useEffect: uE6, useRef: uR6 } = React;

// ============================================================
// SUPPLIERS PAGE V2 — tabs: directory · claims
// ============================================================
function SuppliersPageV2({ lang }) {
  const ext = useExtStore();
  const [tab, setTab] = uS6("directory");
  const [openClaimId, setOpenClaimId] = uS6(null);

  const openCount = ext.supplierClaims.filter(c => c.status !== "RESOLVED" && c.status !== "REJECTED").length;
  const draftCount = ext.supplierClaims.filter(c => c.status === "DRAFT").length;

  return (
    <>
      <PageHead
        title={lang === "en" ? "Suppliers" : "공급자"}
        sub={lang === "en"
          ? `${SUPPLIERS.length} suppliers · ${openCount} open claims${draftCount ? ` · ${draftCount} draft` : ""}`
          : `공급자 ${SUPPLIERS.length}곳 · 미해결 클레임 ${openCount}건${draftCount ? ` · 초안 ${draftCount}건` : ""}`}
        actions={
          <div style={{ display: "flex", gap: 0, border: "1px solid var(--border)", borderRadius: 6, overflow: "hidden" }}>
            <button className={`btn btn-sm ${tab === "directory" ? "btn-primary" : "btn-ghost"}`} style={{ borderRadius: 0 }} onClick={() => setTab("directory")}>{lang === "en" ? "Directory" : "공급자"}</button>
            <button className={`btn btn-sm ${tab === "claims" ? "btn-primary" : "btn-ghost"}`} style={{ borderRadius: 0 }} onClick={() => setTab("claims")}>
              {lang === "en" ? "Claims" : "클레임"}
              {openCount > 0 && <span className="badge danger" style={{ marginLeft: 5, fontSize: 10 }}>{openCount}</span>}
            </button>
          </div>
        } />

      {tab === "directory" && <SupplierDirectory lang={lang} />}
      {tab === "claims" && <SupplierClaimsList lang={lang} onOpen={setOpenClaimId} />}

      <SidePanel open={!!openClaimId} onClose={() => setOpenClaimId(null)}>
        {openClaimId && (
          <SupplierClaimPanel
            claimId={openClaimId}
            onClose={() => setOpenClaimId(null)}
            lang={lang} />
        )}
      </SidePanel>
    </>
  );
}

// ─── Directory tab ───────────────────────────────────────────
function SupplierDirectory({ lang }) {
  const ext = useExtStore();
  const claimCountBySupplier = useMemo6(() => {
    const m = {};
    ext.supplierClaims.forEach(c => {
      if (c.status !== "RESOLVED" && c.status !== "REJECTED") {
        m[c.supplier] = (m[c.supplier] || 0) + 1;
      }
    });
    return m;
  }, [ext.supplierClaims]);

  return (
    <div className="tbl-wrap">
      <table className="tbl">
        <thead><tr>
          <th>{lang === "en" ? "Code" : "코드"}</th>
          <th>{lang === "en" ? "Name" : "공급자명"}</th>
          <th>{lang === "en" ? "Category" : "카테고리"}</th>
          <th>{lang === "en" ? "Contact" : "연락처"}</th>
          <th>{lang === "en" ? "Grade" : "등급"}</th>
          <th className="num">{lang === "en" ? "Open claims" : "미해결 클레임"}</th>
        </tr></thead>
        <tbody>
          {SUPPLIERS.map((s, i) => (
            <tr key={s.id} className="stagger" style={{ animationDelay: `${i * 22}ms` }}>
              <td className="id">{s.code}</td>
              <td className="strong">{s.name}</td>
              <td>{s.category}</td>
              <td className="mono muted" style={{ fontSize: 11.5 }}>{s.contact}</td>
              <td><span className={`badge ${s.grade === "A" ? "ok" : "warn"}`}>{s.grade}</span></td>
              <td className="num">
                {claimCountBySupplier[s.name] > 0
                  ? <span className="badge danger">{claimCountBySupplier[s.name]}</span>
                  : <span className="muted">—</span>}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
const useMemo6 = React.useMemo;

// ─── Claims list tab ─────────────────────────────────────────
function SupplierClaimsList({ lang, onOpen }) {
  const ext = useExtStore();
  const [filter, setFilter] = uS6("ACTIVE");

  const counts = ext.supplierClaims.reduce(
    (c, x) => ({
      ...c,
      ALL: (c.ALL || 0) + 1,
      ACTIVE: (c.ACTIVE || 0) + (x.status !== "RESOLVED" && x.status !== "REJECTED" ? 1 : 0),
      [x.status]: (c[x.status] || 0) + 1,
    }), { ALL: 0, ACTIVE: 0 }
  );

  const items = useMemo6(() => {
    if (filter === "ALL") return ext.supplierClaims;
    if (filter === "ACTIVE") return ext.supplierClaims.filter(c => c.status !== "RESOLVED" && c.status !== "REJECTED");
    return ext.supplierClaims.filter(c => c.status === filter);
  }, [filter, ext.supplierClaims]);

  const STATUS_META = {
    DRAFT:           { ko: "초안",       en: "Draft",     tone: "neutral" },
    SENT:            { ko: "송부됨",     en: "Sent",      tone: "info" },
    SUPPLIER_REVIEW: { ko: "회신대기",   en: "Reviewing", tone: "warn" },
    RESOLVED:        { ko: "해결",       en: "Resolved",  tone: "ok" },
    REJECTED:        { ko: "기각",       en: "Rejected",  tone: "danger" },
  };
  const TYPE_LABEL = {
    DAMAGED:        { ko: "외관손상", en: "Damaged" },
    DEFECTIVE:      { ko: "기능불량", en: "Defective" },
    QUANTITY_SHORT: { ko: "수량부족", en: "Qty short" },
    WRONG_ITEM:     { ko: "오출고",   en: "Wrong item" },
  };

  // Open claims by SLA — older = more urgent
  const ageDays = (iso) => Math.floor((Date.now() - new Date(iso).getTime()) / 86400000);

  return (
    <>
      <div className="kpi-grid" style={{ gridTemplateColumns: "repeat(4, 1fr)" }}>
        <div className="kpi"><div className="kpi-label">{lang === "en" ? "Active claims" : "진행중 클레임"}</div><div className="kpi-num tnum">{counts.ACTIVE || 0}</div><div className="kpi-meta"><span className="muted">{counts.DRAFT || 0} {lang === "en" ? "draft" : "초안"} · {counts.SUPPLIER_REVIEW || 0} {lang === "en" ? "reviewing" : "회신대기"}</span></div></div>
        <div className="kpi"><div className="kpi-label">{lang === "en" ? "Resolved (90d)" : "해결 (90일)"}</div><div className="kpi-num tnum">{counts.RESOLVED || 0}</div></div>
        <div className="kpi"><div className="kpi-label">{lang === "en" ? "Avg. resolution" : "평균 해결시간"}</div><div className="kpi-num tnum">2.3<span className="kpi-unit">{lang === "en" ? "days" : "일"}</span></div><div className="kpi-meta"><span className="kpi-delta up">▲ 0.4</span><span className="vs">{lang === "en" ? "vs last month" : "전월 대비"}</span></div></div>
        <div className="kpi"><div className="kpi-label">{lang === "en" ? "Total impact" : "누적 손실"}</div><div className="kpi-num tnum">{fmtKRW(ext.supplierClaims.reduce((s, c) => s + c.defectQty * (lookupProduct(c.productId).price || 100_000), 0))}</div></div>
      </div>

      <div className="tbl-wrap">
        <div className="tbl-toolbar">
          <div className="filters">
            {["ACTIVE", "DRAFT", "SENT", "SUPPLIER_REVIEW", "RESOLVED", "ALL"].map(f => (
              <button key={f} className={`chip ${filter === f ? "on" : ""}`} onClick={() => setFilter(f)}>
                {f === "ALL" ? (lang === "en" ? "All" : "전체")
                  : f === "ACTIVE" ? (lang === "en" ? "Active" : "진행중")
                  : (lang === "en" ? STATUS_META[f].en : STATUS_META[f].ko)}
                <span className="count">{counts[f] || 0}</span>
              </button>
            ))}
          </div>
        </div>

        <table className="tbl">
          <thead><tr>
            <th>ID</th>
            <th>{lang === "en" ? "Supplier" : "공급자"}</th>
            <th>{lang === "en" ? "Product" : "품목"}</th>
            <th>{lang === "en" ? "Type" : "유형"}</th>
            <th className="num">{lang === "en" ? "Defect" : "불량"}</th>
            <th>{lang === "en" ? "Opened" : "발생일"}</th>
            <th>{lang === "en" ? "Status" : "상태"}</th>
            <th></th>
          </tr></thead>
          <tbody>
            {items.length === 0 ? (
              <tr><td colSpan={8} className="muted" style={{ textAlign: "center", padding: 32 }}>
                {lang === "en" ? "No claims in this view" : "해당 조건의 클레임이 없습니다"}
              </td></tr>
            ) : items.map((c, i) => {
              const meta = STATUS_META[c.status] || { ko: c.status, en: c.status, tone: "neutral" };
              const type = TYPE_LABEL[c.defectType] || { ko: c.defectType, en: c.defectType };
              const age = ageDays(c.openedAt);
              const isOld = age >= 3 && c.status !== "RESOLVED" && c.status !== "REJECTED";
              const product = lookupProduct(c.productId);
              return (
                <tr key={c.id} className="stagger row-clickable" style={{ animationDelay: `${i * 22}ms` }}
                    onClick={() => onOpen(c.id)}>
                  <td className="id">SC-{c.id}</td>
                  <td className="strong">{c.supplier}</td>
                  <td>
                    <div>{product.name}</div>
                    <div className="mono muted" style={{ fontSize: 11 }}>{product.sku} · PO-{c.poId}</div>
                  </td>
                  <td><span className="badge">{lang === "en" ? type.en : type.ko}</span></td>
                  <td className="num">
                    <span className="strong" style={{ color: "var(--danger-ink)" }}>{c.defectQty}</span>
                    <span className="muted" style={{ fontSize: 11 }}>/{c.totalReceived}</span>
                  </td>
                  <td className="muted">
                    <div>{fmtDateTime(c.openedAt)}</div>
                    {isOld && <span className="badge danger" style={{ fontSize: 10, marginTop: 2 }}>D+{age}</span>}
                  </td>
                  <td>
                    <span className={`badge ${meta.tone}`}>{lang === "en" ? meta.en : meta.ko}</span>
                    {c.status === "RESOLVED" && c.resolutionType && (
                      <div className="muted" style={{ fontSize: 10.5, marginTop: 3 }}>
                        {c.resolutionType === "REFUND" ? (lang === "en" ? "→ refund" : "→ 환불") : (lang === "en" ? "→ replacement" : "→ 교환")}
                      </div>
                    )}
                  </td>
                  <td style={{ textAlign: "right" }}>
                    <button className="btn btn-sm btn-ghost" onClick={e => { e.stopPropagation(); onOpen(c.id); }}>
                      {lang === "en" ? "Open" : "열기"}
                    </button>
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
// SUPPLIER CLAIM PANEL — workflow stepper + actions
// ============================================================
const CLAIM_STEPS = [
  { key: "DRAFT",           ko: "초안 작성",      en: "Draft" },
  { key: "SENT",            ko: "공급자 송부",    en: "Sent" },
  { key: "SUPPLIER_REVIEW", ko: "공급자 검토",    en: "Supplier review" },
  { key: "RESOLVED",        ko: "해결",          en: "Resolved" },
];

function SupplierClaimPanel({ claimId, onClose, lang }) {
  const ext = useExtStore();
  const toast = useToast();
  const claim = ext.supplierClaims.find(c => c.id === claimId);
  const [actionBusy, setActionBusy] = uS6(false);

  if (!claim) return null;
  const product = lookupProduct(claim.productId);
  const stepIdx = (() => {
    if (claim.status === "DRAFT") return 0;
    if (claim.status === "SENT") return 1;
    if (claim.status === "SUPPLIER_REVIEW") return 2;
    if (claim.status === "RESOLVED" || claim.status === "REJECTED") return 3;
    return 0;
  })();

  // Add an event to history
  const log = (event, note, actor = "현우") => {
    ext.setSupplierClaims(prev => prev.map(c => c.id === claim.id ? {
      ...c,
      history: [...(c.history || []), { ts: new Date().toISOString(), actor, event, note }],
    } : c));
  };

  const updateClaim = (patch) => {
    ext.setSupplierClaims(prev => prev.map(c => c.id === claim.id ? { ...c, ...patch } : c));
  };

  // ── ACTIONS ──────────────────────────────────────────
  const sendToSupplier = () => {
    setActionBusy(true);
    setTimeout(() => {
      updateClaim({ status: "SENT" });
      log("SENT_TO_SUPPLIER", lang === "en" ? "Sent to supplier" : "공급자에게 클레임 송부");
      toast.push(lang === "en" ? "Claim sent to supplier" : "공급자에게 클레임을 송부했습니다");
      setActionBusy(false);
      // simulate supplier ack after 2s
      setTimeout(() => {
        ext.setSupplierClaims(prev => prev.map(c => c.id === claim.id && c.status === "SENT" ? {
          ...c,
          status: "SUPPLIER_REVIEW",
          history: [...(c.history || []), {
            ts: new Date().toISOString(),
            actor: `${claim.supplier} (${lang === "en" ? "supplier" : "공급자"})`,
            event: "SUPPLIER_REVIEWING",
            note: lang === "en" ? "Acknowledged · investigating" : "수신 확인 · 검토 중"
          }],
        } : c));
        toast.push(lang === "en" ? `${claim.supplier} acknowledged the claim` : `${claim.supplier}이(가) 클레임을 수신했습니다`);
      }, 2000);
    }, 600);
  };

  const simulateSupplierResponse = (proposed) => {
    setActionBusy(true);
    setTimeout(() => {
      const note = proposed === "REFUND"
        ? (lang === "en" ? "Refund agreed for defective units" : "불량분 환불 합의")
        : (lang === "en" ? "Replacement units to be shipped" : "교환품 출하 예정");
      ext.setSupplierClaims(prev => prev.map(c => c.id === claim.id ? {
        ...c,
        supplierResponse: { ts: new Date().toISOString(), note, proposed },
        history: [...(c.history || []), {
          ts: new Date().toISOString(),
          actor: `${claim.supplier} (${lang === "en" ? "supplier" : "공급자"})`,
          event: "RESPONSE_RECEIVED",
          note,
        }],
      } : c));
      toast.push(lang === "en" ? `Supplier proposed ${proposed === "REFUND" ? "refund" : "replacement"}` : `공급자 회신 — ${proposed === "REFUND" ? "환불" : "교환"} 제안`);
      setActionBusy(false);
    }, 700);
  };

  const resolve = (resolutionType) => {
    setActionBusy(true);
    setTimeout(() => {
      // For REFUND: post a ledger entry
      if (resolutionType === "REFUND") {
        const refundAmount = claim.defectQty * (product.price || 100_000);
        const newLedgerId = 5200 + ext.ledgers.length + 1;
        ext.setLedgers(prev => [...prev, {
          id: newLedgerId, periodId: 32, type: "REFUND",
          referenceId: claim.id, amount: -refundAmount,
          description: `Supplier refund · SC-${claim.id} · ${claim.supplier}`,
          createdAt: new Date().toISOString(),
        }]);
        toast.push(lang === "en"
          ? `Resolved · refund posted (LDG-${newLedgerId})`
          : `클레임 해결 · 환불 정산 전표 발행 (LDG-${newLedgerId})`);
      } else {
        toast.push(lang === "en" ? "Resolved · replacement received" : "클레임 해결 · 교환품 입고 처리");
      }
      updateClaim({
        status: "RESOLVED",
        resolutionType,
        resolvedAt: new Date().toISOString(),
      });
      log("RESOLVED",
        resolutionType === "REFUND"
          ? (lang === "en" ? "Refund processed · ledger posted" : "환불 처리 · 정산 전표 발행")
          : (lang === "en" ? "Replacement units received · stock updated" : "교환품 입고 완료 · 재고 반영"));
      setActionBusy(false);
    }, 800);
  };

  const reject = () => {
    setActionBusy(true);
    setTimeout(() => {
      updateClaim({ status: "REJECTED", resolvedAt: new Date().toISOString() });
      log("REJECTED", lang === "en" ? "Claim rejected by supplier" : "공급자 측 기각");
      toast.push(lang === "en" ? "Claim rejected" : "클레임이 기각되었습니다");
      setActionBusy(false);
    }, 600);
  };

  const TYPE_LABEL = {
    DAMAGED:        { ko: "외관손상",  en: "Damaged" },
    DEFECTIVE:      { ko: "기능불량",  en: "Defective" },
    QUANTITY_SHORT: { ko: "수량부족",  en: "Quantity short" },
    WRONG_ITEM:     { ko: "오출고",    en: "Wrong item" },
  };
  const typeLabel = TYPE_LABEL[claim.defectType] || { ko: claim.defectType, en: claim.defectType };
  const isClosed = claim.status === "RESOLVED" || claim.status === "REJECTED";
  const hasResponse = !!claim.supplierResponse;

  return (
    <>
      <div className="sp-head">
        <div>
          <div className="sp-title">{lang === "en" ? "Supplier Claim" : "공급자 클레임"} SC-{claim.id}</div>
          <div className="sp-sub">{claim.supplier} · PO-{claim.poId}</div>
        </div>
        <button className="sp-close" onClick={onClose}><Icon.X /></button>
      </div>

      <div className="sp-body">
        <div>
          <div className="muted" style={{ fontSize: 11, fontWeight: 600, letterSpacing: "0.06em", marginBottom: 8 }}>
            {lang === "en" ? "DETAILS" : "클레임 정보"}
          </div>
          <div className="kv-grid" style={{ gridTemplateColumns: "1fr 1fr" }}>
            <div><span className="muted">{lang === "en" ? "Product" : "품목"}</span><span className="strong">{product.name}</span></div>
            <div><span className="muted">SKU</span><span className="mono">{product.sku}</span></div>
            <div><span className="muted">{lang === "en" ? "Type" : "유형"}</span><span><span className="badge">{lang === "en" ? typeLabel.en : typeLabel.ko}</span></span></div>
            <div><span className="muted">{lang === "en" ? "Priority" : "우선순위"}</span><span><span className={`badge ${claim.priority === "HIGH" ? "danger" : claim.priority === "MEDIUM" ? "warn" : "neutral"}`}>{claim.priority}</span></span></div>
            <div><span className="muted">{lang === "en" ? "Defective" : "불량 수량"}</span><span className="strong" style={{ color: "var(--danger-ink)" }}>{claim.defectQty} / {claim.totalReceived}</span></div>
            <div><span className="muted">{lang === "en" ? "Opened" : "발생일"}</span><span>{fmtDateTime(claim.openedAt)}</span></div>
          </div>
          {claim.description && (
            <div className="claim-desc">{claim.description}</div>
          )}
        </div>

        {/* Workflow stepper */}
        <div>
          <div className="muted" style={{ fontSize: 11, fontWeight: 600, letterSpacing: "0.06em", marginBottom: 12 }}>
            {lang === "en" ? "WORKFLOW" : "처리 진행"}
          </div>
          <div className="claim-flow">
            {CLAIM_STEPS.map((step, i) => {
              const state = claim.status === "REJECTED" && i === 3 ? "rejected"
                : i < stepIdx ? "done"
                : i === stepIdx ? (isClosed ? "done" : "now")
                : "wait";
              return (
                <React.Fragment key={step.key}>
                  <div className={`claim-step ${state}`}>
                    <div className="claim-step-dot">
                      {state === "done" && <svg viewBox="0 0 24 24"><path d="M5 12 l4 4 l10 -11" /></svg>}
                      {state === "now" && <span className="pulse-ring" />}
                      {state === "rejected" && <svg viewBox="0 0 24 24"><path d="M7 7 l10 10 M17 7 l-10 10" /></svg>}
                      {state === "wait" && <span style={{ width: 6, height: 6, borderRadius: 3, background: "var(--text-3)" }} />}
                    </div>
                    <div className="claim-step-label">
                      {lang === "en" ? step.en : step.ko}
                      {claim.resolutionType && i === 3 && state === "done" && (
                        <div className="muted" style={{ fontSize: 10.5, marginTop: 2 }}>
                          {claim.resolutionType === "REFUND" ? (lang === "en" ? "Refund" : "환불") : (lang === "en" ? "Replacement" : "교환")}
                        </div>
                      )}
                    </div>
                  </div>
                  {i < CLAIM_STEPS.length - 1 && (
                    <div className={`claim-step-line ${i < stepIdx ? "done" : ""}`} />
                  )}
                </React.Fragment>
              );
            })}
          </div>
        </div>

        {/* Supplier response */}
        {hasResponse && !isClosed && (
          <div className="supplier-response">
            <div className="supplier-response-head">
              <span className="cust-avatar" style={{ width: 28, height: 28, fontSize: 11, background: "linear-gradient(135deg, var(--warn-ink), color-mix(in oklch, var(--warn-ink) 70%, black))" }}>
                {claim.supplier.charAt(0)}
              </span>
              <div style={{ flex: 1 }}>
                <div className="strong" style={{ fontSize: 13 }}>{claim.supplier}</div>
                <div className="muted" style={{ fontSize: 11 }}>{fmtRelative ? fmtRelative(claim.supplierResponse.ts) : fmtDateTime(claim.supplierResponse.ts)}</div>
              </div>
              {claim.supplierResponse.proposed && (
                <span className={`badge ${claim.supplierResponse.proposed === "REFUND" ? "danger" : "info"}`}>
                  {lang === "en"
                    ? (claim.supplierResponse.proposed === "REFUND" ? "Refund proposed" : "Replacement proposed")
                    : (claim.supplierResponse.proposed === "REFUND" ? "환불 제안" : "교환 제안")}
                </span>
              )}
            </div>
            <div className="supplier-response-body">{claim.supplierResponse.note}</div>
          </div>
        )}

        {/* History timeline */}
        <div>
          <div className="muted" style={{ fontSize: 11, fontWeight: 600, letterSpacing: "0.06em", marginBottom: 8 }}>
            {lang === "en" ? "HISTORY" : "처리 이력"}
          </div>
          <div className="claim-timeline">
            {(claim.history || []).slice().reverse().map((ev, i) => (
              <div key={i} className="claim-tl-row">
                <div className={`claim-tl-dot ${ev.event === "RESOLVED" ? "ok" : ev.event === "REJECTED" ? "danger" : ev.event === "RESPONSE_RECEIVED" ? "warn" : ""}`} />
                <div style={{ flex: 1 }}>
                  <div style={{ display: "flex", justifyContent: "space-between", gap: 8 }}>
                    <span className="strong" style={{ fontSize: 12.5 }}>{ev.note}</span>
                    <span className="muted" style={{ fontSize: 11, whiteSpace: "nowrap" }}>{fmtDateTime(ev.ts)}</span>
                  </div>
                  <div className="muted" style={{ fontSize: 11 }}>{ev.actor}</div>
                </div>
              </div>
            ))}
          </div>
        </div>

        {claim.status === "RESOLVED" && claim.resolutionType === "REFUND" && (
          <div className="auto-note" style={{ background: "var(--ok-soft)", color: "var(--ok-ink)" }}>
            <Icon.Coin />
            <span>{lang === "en"
              ? "Refund posted to settlement period 32. Check Settlement → Ledgers."
              : "환불 정산 전표가 P-32 기간에 발행되었습니다. 정산 → 전표에서 확인하세요."}</span>
          </div>
        )}
      </div>

      <div className="sp-foot">
        {claim.status === "DRAFT" && (
          <>
            <button className="btn" onClick={onClose} disabled={actionBusy}>{lang === "en" ? "Cancel" : "취소"}</button>
            <button className="btn btn-accent" onClick={sendToSupplier} disabled={actionBusy} style={{ marginLeft: "auto" }}>
              {actionBusy ? (lang === "en" ? "Sending…" : "송부 중…") : (lang === "en" ? "Send to supplier" : "공급자에게 송부")}
            </button>
          </>
        )}
        {claim.status === "SENT" && (
          <>
            <button className="btn" onClick={onClose}>{lang === "en" ? "Close" : "닫기"}</button>
            <span className="muted" style={{ marginLeft: "auto", fontSize: 12, alignSelf: "center" }}>
              {lang === "en" ? "Awaiting supplier ack…" : "공급자 수신 대기 중…"}
            </span>
          </>
        )}
        {claim.status === "SUPPLIER_REVIEW" && !hasResponse && (
          <>
            <button className="btn" onClick={onClose}>{lang === "en" ? "Close" : "닫기"}</button>
            <div style={{ marginLeft: "auto", display: "flex", gap: 6 }}>
              <span className="muted" style={{ fontSize: 11.5, alignSelf: "center" }}>{lang === "en" ? "Simulate response:" : "회신 시뮬레이션:"}</span>
              <button className="btn btn-sm" onClick={() => simulateSupplierResponse("REFUND")} disabled={actionBusy}>{lang === "en" ? "Refund" : "환불"}</button>
              <button className="btn btn-sm" onClick={() => simulateSupplierResponse("REPLACEMENT")} disabled={actionBusy}>{lang === "en" ? "Replacement" : "교환"}</button>
            </div>
          </>
        )}
        {claim.status === "SUPPLIER_REVIEW" && hasResponse && (
          <>
            <button className="btn" onClick={reject} disabled={actionBusy}>{lang === "en" ? "Reject" : "기각"}</button>
            <div style={{ marginLeft: "auto", display: "flex", gap: 6 }}>
              <button className="btn btn-accent" onClick={() => resolve(claim.supplierResponse.proposed)} disabled={actionBusy}>
                {actionBusy ? "…" : (lang === "en"
                  ? `Accept ${claim.supplierResponse.proposed === "REFUND" ? "refund" : "replacement"}`
                  : `${claim.supplierResponse.proposed === "REFUND" ? "환불" : "교환"} 수락 · 종결`)}
              </button>
            </div>
          </>
        )}
        {(claim.status === "RESOLVED" || claim.status === "REJECTED") && (
          <button className="btn" onClick={onClose} style={{ marginLeft: "auto" }}>{lang === "en" ? "Close" : "닫기"}</button>
        )}
      </div>
    </>
  );
}

Object.assign(window, { SuppliersPageV2, SupplierClaimPanel, SupplierDirectory, SupplierClaimsList });
