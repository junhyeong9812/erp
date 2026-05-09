/* global React, PageHead, Icon, useToast, fmtKRW, fmtN, fmtDate, fmtDateTime, fmtRelative */
/* global APPROVALS, PAYMENTS, REFUNDS, LEDGERS, NOTIFICATIONS, CRM_CUSTOMERS, CONSULTATIONS, CLAIMS, QUOTES, SUPPLIER_CLAIMS */
/* global CUSTOMERS, PRODUCTS, lookupCustomer, lookupProduct */

const { useState: uS4, useMemo: uM4, useEffect: uE4 } = React;

// ============================================================
// EXT STORE — mutable state shared across new flows
// ============================================================
const ExtStoreContext = React.createContext(null);
function ExtStoreProvider({ children }) {
  const [payments, setPayments] = uS4(PAYMENTS);
  const [refunds, setRefunds]   = uS4(REFUNDS);
  const [ledgers, setLedgers]   = uS4(LEDGERS);
  const [approvals, setApprovals] = uS4(APPROVALS);
  const [notifications, setNotifications] = uS4(NOTIFICATIONS);
  const [quotes, setQuotes] = uS4(QUOTES);
  const [supplierClaims, setSupplierClaims] = uS4(SUPPLIER_CLAIMS);
  return (
    <ExtStoreContext.Provider value={{
      payments, setPayments, refunds, setRefunds, ledgers, setLedgers,
      approvals, setApprovals, notifications, setNotifications,
      quotes, setQuotes,
      supplierClaims, setSupplierClaims,
    }}>{children}</ExtStoreContext.Provider>
  );
}
const useExtStore = () => React.useContext(ExtStoreContext);

// ============================================================
// REFUND MODAL — payment → refund → auto ledger entry
// ============================================================
function RefundModal({ payment, onClose, lang }) {
  const ext = useExtStore();
  const toast = useToast();
  const [amount, setAmount] = uS4(payment.amount);
  const [reason, setReason] = uS4("");
  const [phase, setPhase] = uS4("form"); // form | processing | done
  const [createdLedgerId, setCreatedLedgerId] = uS4(null);

  const submit = () => {
    if (amount <= 0 || amount > payment.amount) return;
    setPhase("processing");
    setTimeout(() => {
      const newRefundId = 9100 + ext.refunds.length + 1;
      const newRefund = {
        id: newRefundId, paymentId: payment.id, orderId: payment.orderId,
        amount, reason: reason || (lang === "en" ? "Customer request" : "고객 요청"),
        status: "COMPLETED", createdAt: new Date().toISOString(),
      };
      const newLedgerId = 5100 + ext.ledgers.length + 1;
      const newLedger = {
        id: newLedgerId, periodId: 32, type: "REFUND",
        referenceId: newRefundId, amount: -amount,
        description: `Refund #${newRefundId} → Order #${payment.orderId}`,
        createdAt: new Date().toISOString(),
      };
      ext.setRefunds(prev => [newRefund, ...prev]);
      ext.setLedgers(prev => [...prev, newLedger]);
      ext.setPayments(prev => prev.map(p => p.id === payment.id
        ? { ...p, status: amount === p.amount ? "REFUNDED" : "PARTIAL_REFUND", refundedAmount: (p.refundedAmount || 0) + amount }
        : p));
      setCreatedLedgerId(newLedgerId);
      setPhase("done");
      toast.push(lang === "en" ? `Refund processed · LDG-${newLedgerId} created` : `환불 처리 완료 · LDG-${newLedgerId} 자동 생성`);
    }, 900);
  };

  return (
    <div className="modal-backdrop" onClick={onClose}>
      <div className="modal" onClick={e => e.stopPropagation()}>
        <div className="modal-head">
          <div>
            <div className="modal-title">{lang === "en" ? "Process Refund" : "환불 처리"}</div>
            <div className="modal-sub">PAY-{payment.id} · {lang === "en" ? "Order" : "주문"} #{payment.orderId}</div>
          </div>
          <button className="sp-close" onClick={onClose}><Icon.X /></button>
        </div>

        {phase === "form" && (
          <>
            <div className="modal-body">
              <div className="kv-grid">
                <div><span className="muted">{lang === "en" ? "Original" : "원 결제액"}</span><span className="strong tnum">{fmtKRW(payment.amount)}</span></div>
                <div><span className="muted">{lang === "en" ? "Method" : "결제수단"}</span><span>{payment.method}</span></div>
                <div><span className="muted">{lang === "en" ? "Paid at" : "결제일시"}</span><span className="muted">{fmtDateTime(payment.createdAt)}</span></div>
              </div>
              <div className="field">
                <label className="field-label">{lang === "en" ? "Refund amount (₩)" : "환불 금액 (₩)"}</label>
                <input type="number" className="input" value={amount} max={payment.amount}
                  onChange={e => setAmount(Math.min(Number(e.target.value), payment.amount))} />
                <div className="amount-chips">
                  <button className="chip-sm" onClick={() => setAmount(payment.amount)}>{lang === "en" ? "Full" : "전액"}</button>
                  <button className="chip-sm" onClick={() => setAmount(Math.round(payment.amount / 2))}>{lang === "en" ? "Half" : "반액"}</button>
                  <button className="chip-sm" onClick={() => setAmount(Math.round(payment.amount * 0.1))}>10%</button>
                </div>
              </div>
              <div className="field">
                <label className="field-label">{lang === "en" ? "Reason" : "사유"}</label>
                <textarea className="input" style={{ height: 64, padding: 8, resize: "vertical" }}
                  value={reason} onChange={e => setReason(e.target.value)}
                  placeholder={lang === "en" ? "e.g. quality issue" : "예: 품질 이슈"} />
              </div>
              <div className="auto-note">
                <Icon.Coin />
                <span>{lang === "en"
                  ? "An auto-ledger entry (REFUND) will be posted to the open period."
                  : "환불 처리 시 정산 전표(REFUND)가 자동 발행됩니다."}</span>
              </div>
            </div>
            <div className="modal-foot">
              <button className="btn" onClick={onClose}>{lang === "en" ? "Cancel" : "취소"}</button>
              <button className="btn btn-accent" onClick={submit}>{lang === "en" ? "Process refund" : "환불 처리"}</button>
            </div>
          </>
        )}

        {phase === "processing" && (
          <div className="modal-body" style={{ minHeight: 200, display: "grid", placeItems: "center" }}>
            <div className="proc">
              <div className="spinner" />
              <div style={{ marginTop: 14, fontSize: 13, color: "var(--text-2)" }}>{lang === "en" ? "Posting to ledger…" : "전표 발행 중…"}</div>
            </div>
          </div>
        )}

        {phase === "done" && (
          <>
            <div className="modal-body">
              <div className="success-mark"><svg viewBox="0 0 32 32"><circle cx="16" cy="16" r="14" /><path d="M9 16.5l5 5 9-10" /></svg></div>
              <div style={{ textAlign: "center" }}>
                <div className="strong" style={{ fontSize: 15 }}>{lang === "en" ? "Refund completed" : "환불이 완료되었습니다"}</div>
                <div className="muted" style={{ fontSize: 12, marginTop: 4 }}>{fmtKRW(amount)} · LDG-{createdLedgerId}</div>
              </div>
              <div className="ledger-preview">
                <div className="muted" style={{ fontSize: 11, fontWeight: 600, letterSpacing: "0.06em" }}>{lang === "en" ? "LEDGER ENTRY" : "정산 전표 미리보기"}</div>
                <div style={{ display: "flex", justifyContent: "space-between", marginTop: 10 }}>
                  <span className="mono muted">LDG-{createdLedgerId}</span>
                  <span className="badge danger">REFUND</span>
                </div>
                <div style={{ display: "flex", justifyContent: "space-between", marginTop: 6 }}>
                  <span className="muted">{lang === "en" ? "Amount" : "금액"}</span>
                  <span className="strong tnum" style={{ color: "var(--danger-ink)" }}>−{fmtKRW(amount)}</span>
                </div>
              </div>
            </div>
            <div className="modal-foot">
              <button className="btn btn-accent" onClick={onClose} style={{ marginLeft: "auto" }}>{lang === "en" ? "Done" : "확인"}</button>
            </div>
          </>
        )}
      </div>
    </div>
  );
}

// ============================================================
// PAYMENTS PAGE v2 — uses ExtStore + RefundModal
// ============================================================
function PaymentsPageV2({ lang }) {
  const ext = useExtStore();
  const [tab, setTab] = uS4("payments");
  const [refundTarget, setRefundTarget] = uS4(null);
  const todayVol = ext.payments.filter(p => p.status === "COMPLETED" || p.status === "PARTIAL_REFUND").reduce((s, p) => s + (p.amount - (p.refundedAmount || 0)), 0);
  const refundRate = ((ext.refunds.reduce((s, r) => s + r.amount, 0) / Math.max(1, ext.payments.reduce((s, p) => s + p.amount, 0))) * 100).toFixed(1);

  return (
    <>
      <PageHead title={lang === "en" ? "Payments" : "결제 관리"}
        sub={lang === "en" ? `${ext.payments.length} payments · ${ext.refunds.length} refunds` : `결제 ${ext.payments.length}건 · 환불 ${ext.refunds.length}건`}
        actions={
          <div style={{ display: "flex", gap: 0, border: "1px solid var(--border)", borderRadius: 6, overflow: "hidden" }}>
            <button className={`btn btn-sm ${tab === "payments" ? "btn-primary" : "btn-ghost"}`} style={{ borderRadius: 0 }} onClick={() => setTab("payments")}>{lang === "en" ? "Payments" : "결제"}</button>
            <button className={`btn btn-sm ${tab === "refunds" ? "btn-primary" : "btn-ghost"}`} style={{ borderRadius: 0 }} onClick={() => setTab("refunds")}>{lang === "en" ? "Refunds" : "환불"}</button>
          </div>
        } />

      <div className="kpi-grid" style={{ gridTemplateColumns: "repeat(3, 1fr)" }}>
        <div className="kpi"><div className="kpi-label">{lang === "en" ? "Net Volume" : "순결제액"}</div><div className="kpi-num tnum">{fmtKRW(todayVol)}</div><div className="kpi-meta"><span className="kpi-delta up">▲ 12.4%</span><span className="vs">{lang === "en" ? "vs yesterday" : "어제 대비"}</span></div></div>
        <div className="kpi"><div className="kpi-label">{lang === "en" ? "Refund Rate" : "환불률"}</div><div className="kpi-num tnum">{refundRate}<span className="kpi-unit">%</span></div><div className="kpi-meta"><span className="muted">{ext.refunds.length} {lang === "en" ? "refunds" : "건"}</span></div></div>
        <div className="kpi"><div className="kpi-label">{lang === "en" ? "Pending" : "결제 대기"}</div><div className="kpi-num tnum">{ext.payments.filter(p => p.status === "PENDING").length}<span className="kpi-unit">{lang === "en" ? "case" : "건"}</span></div></div>
      </div>

      {tab === "payments" ? (
        <div className="tbl-wrap">
          <table className="tbl">
            <thead><tr>
              <th>ID</th><th>{lang === "en" ? "Order" : "주문"}</th><th>{lang === "en" ? "Method" : "수단"}</th>
              <th className="num">{lang === "en" ? "Amount" : "금액"}</th><th className="num">{lang === "en" ? "Refunded" : "환불"}</th>
              <th>{lang === "en" ? "Created" : "결제일시"}</th><th>{lang === "en" ? "Status" : "상태"}</th><th></th>
            </tr></thead>
            <tbody>
              {ext.payments.map((p, i) => {
                const tone = p.status === "COMPLETED" ? "ok" : p.status === "PENDING" ? "warn" : p.status === "REFUNDED" ? "danger" : p.status === "PARTIAL_REFUND" ? "warn" : "neutral";
                return (
                  <tr key={p.id} className="stagger" style={{ animationDelay: `${i * 22}ms` }}>
                    <td className="id">PAY-{p.id}</td>
                    <td className="muted">#{p.orderId}</td>
                    <td><span className="badge">{p.method}</span></td>
                    <td className="num strong">{fmtKRW(p.amount)}</td>
                    <td className="num muted" style={{ color: p.refundedAmount ? "var(--danger-ink)" : "inherit" }}>{p.refundedAmount ? `−${fmtKRW(p.refundedAmount)}` : "—"}</td>
                    <td className="muted">{fmtDateTime(p.createdAt)}</td>
                    <td><span className={`badge ${tone}`}>{p.status}</span></td>
                    <td style={{ textAlign: "right" }}>
                      {(p.status === "COMPLETED" || p.status === "PARTIAL_REFUND") &&
                        <button className="btn btn-sm btn-ghost" onClick={() => setRefundTarget(p)}>{lang === "en" ? "Refund" : "환불"}</button>}
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      ) : (
        <div className="tbl-wrap">
          <table className="tbl">
            <thead><tr>
              <th>ID</th><th>{lang === "en" ? "Payment" : "결제"}</th><th>{lang === "en" ? "Reason" : "사유"}</th>
              <th className="num">{lang === "en" ? "Amount" : "금액"}</th><th>{lang === "en" ? "Date" : "일시"}</th><th>{lang === "en" ? "Status" : "상태"}</th>
            </tr></thead>
            <tbody>
              {ext.refunds.map((r, i) => (
                <tr key={r.id} className="stagger" style={{ animationDelay: `${i * 22}ms` }}>
                  <td className="id">RFD-{r.id}</td>
                  <td className="muted">PAY-{r.paymentId}</td>
                  <td>{r.reason}</td>
                  <td className="num strong" style={{ color: "var(--danger-ink)" }}>−{fmtKRW(r.amount)}</td>
                  <td className="muted">{fmtDateTime(r.createdAt)}</td>
                  <td><span className="badge ok">{r.status}</span></td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {refundTarget && <RefundModal payment={refundTarget} onClose={() => setRefundTarget(null)} lang={lang} />}
    </>
  );
}

// ============================================================
// SETTLEMENT PAGE v2 — reads from ExtStore so refunds appear live
// ============================================================
function SettlementPageV2({ lang }) {
  const ext = useExtStore();
  const [periodId, setPeriodId] = uS4(32);
  const ledgers = ext.ledgers.filter(l => l.periodId === periodId);
  const totalDr = ledgers.filter(l => l.amount > 0).reduce((s, l) => s + l.amount, 0);
  const totalCr = ledgers.filter(l => l.amount < 0).reduce((s, l) => s + Math.abs(l.amount), 0);
  const TYPE = { SALES: "ok", REFUND: "danger", PURCHASE: "warn", FEE: "neutral" };
  const PERIODS = [
    { id: 31, startDate: "2026-04-01", endDate: "2026-04-30", status: "CLOSED",  totalSales: 142_580_000, totalRefund: 3_240_000 },
    { id: 32, startDate: "2026-05-01", endDate: "2026-05-31", status: "OPEN",    totalSales:  21_896_000, totalRefund:   580_000 },
    { id: 30, startDate: "2026-03-01", endDate: "2026-03-31", status: "SETTLED", totalSales: 128_440_000, totalRefund: 2_180_000 },
  ];

  return (
    <>
      <PageHead title={lang === "en" ? "Settlement" : "정산 / 회계"}
        sub={lang === "en" ? "Periods, ledgers and seller settlements" : "정산 기간 · 전표 · 판매자 정산"}
        actions={<>
          <button className="btn"><Icon.Plus /> {lang === "en" ? "New period" : "기간 개설"}</button>
          <button className="btn btn-accent">{lang === "en" ? "Close period" : "기간 마감"}</button>
        </>} />

      <div className="card" style={{ marginBottom: 14 }}>
        <div className="card-head"><div className="card-title">{lang === "en" ? "Periods" : "정산 기간"}</div></div>
        <div className="card-body" style={{ padding: 0 }}>
          {PERIODS.map(p => (
            <div key={p.id} onClick={() => setPeriodId(p.id)}
              style={{ padding: "12px 18px", borderTop: "1px solid var(--divider)", display: "grid", gridTemplateColumns: "100px 1fr 1fr 120px 120px", gap: 16, alignItems: "center", cursor: "pointer", background: p.id === periodId ? "var(--accent-soft)" : "transparent", transition: "background 0.15s" }}>
              <span className="mono muted">P-{p.id}</span>
              <span className="strong">{p.startDate} ~ {p.endDate}</span>
              <span className="muted">{lang === "en" ? "Sales" : "매출"} <span className="strong tnum">{fmtKRW(p.totalSales)}</span></span>
              <span className="muted tnum">{lang === "en" ? "Refund" : "환불"} {fmtKRW(p.totalRefund)}</span>
              <span><span className={`badge ${p.status === "OPEN" ? "info" : p.status === "CLOSED" ? "warn" : "ok"}`}>{p.status}</span></span>
            </div>
          ))}
        </div>
      </div>

      <div className="card">
        <div className="card-head">
          <div>
            <div className="card-title">{lang === "en" ? `Ledgers — Period ${periodId}` : `전표 · 기간 ${periodId}`}</div>
            <div className="card-sub">{lang === "en" ? `Debit ${fmtKRW(totalDr)} · Credit ${fmtKRW(totalCr)} · Net ${fmtKRW(totalDr - totalCr)}` : `차변 ${fmtKRW(totalDr)} · 대변 ${fmtKRW(totalCr)} · 잔액 ${fmtKRW(totalDr - totalCr)}`}</div>
          </div>
        </div>
        <table className="tbl">
          <thead><tr><th>ID</th><th>{lang === "en" ? "Type" : "유형"}</th><th>{lang === "en" ? "Reference" : "참조"}</th><th>{lang === "en" ? "Description" : "내역"}</th><th className="num">{lang === "en" ? "Amount" : "금액"}</th><th>{lang === "en" ? "Created" : "발행일"}</th></tr></thead>
          <tbody>
            {ledgers.map((l, i) => (
              <tr key={l.id} className="stagger" style={{ animationDelay: `${i * 22}ms` }}>
                <td className="id">LDG-{l.id}</td>
                <td><span className={`badge ${TYPE[l.type] || "neutral"}`}>{l.type}</span></td>
                <td className="muted mono">#{l.referenceId}</td>
                <td>{l.description}</td>
                <td className="num strong" style={{ color: l.amount < 0 ? "var(--danger-ink)" : "inherit" }}>{l.amount > 0 ? "+" : ""}{fmtKRW(l.amount)}</td>
                <td className="muted">{fmtDateTime(l.createdAt)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </>
  );
}

// ============================================================
// APPROVAL DETAIL PANEL — vertical workflow stepper
// ============================================================
function ApprovalPanel({ approvalId, onClose, lang }) {
  const ext = useExtStore();
  const toast = useToast();
  const approval = ext.approvals.find(a => a.id === approvalId);
  const [comment, setComment] = uS4("");
  if (!approval) return null;

  const isMyTurn = approval.status === "IN_PROGRESS" &&
    approval.steps[approval.currentStep - 1]?.status === "PENDING";

  const act = (decision) => {
    const newSteps = approval.steps.map((s, i) => {
      if (i === approval.currentStep - 1) {
        return { ...s, status: decision, actedAt: new Date().toISOString(), comment: comment || (decision === "APPROVED" ? "승인" : "반려") };
      }
      return s;
    });
    let newStatus = approval.status;
    let newCurrent = approval.currentStep;
    if (decision === "REJECTED") {
      newStatus = "REJECTED";
    } else {
      // mark next as PENDING
      if (approval.currentStep < approval.totalSteps) {
        newSteps[approval.currentStep] = { ...newSteps[approval.currentStep], status: "PENDING" };
        newCurrent = approval.currentStep + 1;
      } else {
        newStatus = "APPROVED";
      }
    }
    ext.setApprovals(prev => prev.map(a => a.id === approvalId
      ? { ...a, steps: newSteps, status: newStatus, currentStep: newCurrent }
      : a));
    setComment("");
    toast.push(decision === "APPROVED"
      ? (lang === "en" ? `Approved · step ${approval.currentStep}` : `${approval.currentStep}단계 승인 완료`)
      : (lang === "en" ? "Rejected" : "반려 처리"));
  };

  const STATUS = { IN_PROGRESS: "warn", APPROVED: "ok", REJECTED: "danger", CANCELLED: "neutral" };

  return (
    <>
      <div className="sp-head">
        <div>
          <div className="sp-title">{approval.title}</div>
          <div className="sp-sub">AP-{approval.id} · {approval.documentType}</div>
        </div>
        <button className="sp-close" onClick={onClose}><Icon.X /></button>
      </div>
      <div className="sp-body">
        <div>
          <div className="section-h">{lang === "en" ? "Summary" : "기안 내용"}</div>
          <dl className="kv">
            <dt>{lang === "en" ? "Drafter" : "기안자"}</dt><dd>{approval.drafterName}</dd>
            <dt>{lang === "en" ? "Drafted" : "기안일시"}</dt><dd className="muted">{fmtDateTime(approval.createdAt)}</dd>
            {approval.amount > 0 && <><dt>{lang === "en" ? "Amount" : "금액"}</dt><dd className="strong tnum">{fmtKRW(approval.amount)}</dd></>}
            <dt>{lang === "en" ? "Status" : "상태"}</dt><dd><span className={`badge ${STATUS[approval.status]}`}>{approval.status}</span></dd>
          </dl>
          <div style={{ marginTop: 12, padding: 12, background: "var(--bg-elev)", borderRadius: 6, fontSize: 13, color: "var(--text-2)", lineHeight: 1.5 }}>
            {approval.summary}
          </div>
        </div>

        <div>
          <div className="section-h">{lang === "en" ? `Workflow (${approval.currentStep}/${approval.totalSteps})` : `결재선 (${approval.currentStep}/${approval.totalSteps})`}</div>
          <div className="vstepper">
            {approval.steps.map((s, i) => {
              const cls = s.status === "APPROVED" ? "done"
                        : s.status === "REJECTED" ? "rejected"
                        : s.status === "PENDING"  ? "now"
                        : "wait";
              return (
                <div key={s.order} className={`vstep ${cls}`}>
                  <div className="vstep-rail">
                    <div className="vstep-dot">
                      {s.status === "APPROVED" && <Icon.Check />}
                      {s.status === "REJECTED" && <Icon.X />}
                      {s.status === "PENDING"  && <span className="pulse-ring" />}
                      {s.status === "WAITING"  && s.order}
                    </div>
                    {i < approval.steps.length - 1 && <div className="vstep-line" />}
                  </div>
                  <div className="vstep-body">
                    <div style={{ display: "flex", justifyContent: "space-between", alignItems: "baseline" }}>
                      <div>
                        <span className="strong">{s.role}</span>
                        <span className="muted" style={{ marginLeft: 8, fontSize: 12 }}>{s.approver}</span>
                      </div>
                      {s.actedAt && <span className="muted" style={{ fontSize: 11 }}>{fmtRelative(s.actedAt)}</span>}
                    </div>
                    {s.comment && <div className="vstep-comment">{s.comment}</div>}
                    {s.status === "WAITING" && <div className="muted" style={{ fontSize: 11.5, marginTop: 3 }}>{lang === "en" ? "Awaiting previous step" : "이전 단계 대기"}</div>}
                  </div>
                </div>
              );
            })}
          </div>
        </div>

        {isMyTurn && (
          <div>
            <div className="section-h">{lang === "en" ? "Your decision" : "결재 의견"}</div>
            <textarea className="input" style={{ height: 70, padding: 10, resize: "vertical" }}
              value={comment} onChange={e => setComment(e.target.value)}
              placeholder={lang === "en" ? "Optional comment…" : "의견 (선택)"} />
          </div>
        )}
      </div>
      {isMyTurn && (
        <div className="sp-foot">
          <button className="btn" style={{ color: "var(--danger-ink)", borderColor: "var(--danger-soft)" }} onClick={() => act("REJECTED")}>
            <Icon.X /> {lang === "en" ? "Reject" : "반려"}
          </button>
          <button className="btn btn-accent" onClick={() => act("APPROVED")} style={{ marginLeft: "auto" }}>
            <Icon.Check /> {lang === "en" ? "Approve" : "승인"}
          </button>
        </div>
      )}
    </>
  );
}

// ============================================================
// APPROVALS PAGE v2 — opens ApprovalPanel
// ============================================================
function ApprovalsPageV2({ lang, onOpen }) {
  const ext = useExtStore();
  const [tab, setTab] = uS4("inbox");
  const STATUS = { IN_PROGRESS: "warn", APPROVED: "ok", REJECTED: "danger", CANCELLED: "neutral" };
  const items = tab === "inbox" ? ext.approvals.filter(a => a.status === "IN_PROGRESS") :
                tab === "drafted" ? ext.approvals.filter(a => a.drafterId === 1) : ext.approvals;
  return (
    <>
      <PageHead title={lang === "en" ? "Approvals" : "전자결재"}
        sub={lang === "en" ? `${ext.approvals.filter(a => a.status === "IN_PROGRESS").length} pending` : `진행중 ${ext.approvals.filter(a => a.status === "IN_PROGRESS").length}건`}
        actions={<button className="btn btn-accent"><Icon.Plus /> {lang === "en" ? "Draft" : "기안"}</button>} />
      <div className="tbl-wrap">
        <div className="tbl-toolbar">
          <div className="filters">
            <button className={`chip ${tab === "inbox" ? "on" : ""}`} onClick={() => setTab("inbox")}>{lang === "en" ? "Inbox" : "결재함"}</button>
            <button className={`chip ${tab === "drafted" ? "on" : ""}`} onClick={() => setTab("drafted")}>{lang === "en" ? "Drafted" : "내가 기안한"}</button>
            <button className={`chip ${tab === "all" ? "on" : ""}`} onClick={() => setTab("all")}>{lang === "en" ? "All" : "전체"}</button>
          </div>
        </div>
        <table className="tbl">
          <thead><tr><th>ID</th><th>{lang === "en" ? "Type" : "유형"}</th><th>{lang === "en" ? "Title" : "제목"}</th><th>{lang === "en" ? "Drafter" : "기안자"}</th><th className="num">{lang === "en" ? "Amount" : "금액"}</th><th>{lang === "en" ? "Step" : "단계"}</th><th>{lang === "en" ? "Created" : "기안일"}</th><th>{lang === "en" ? "Status" : "상태"}</th></tr></thead>
          <tbody>
            {items.map((a, i) => (
              <tr key={a.id} className="stagger row-clickable" style={{ animationDelay: `${i * 22}ms` }} onClick={() => onOpen(a.id)}>
                <td className="id">AP-{a.id}</td>
                <td><span className="badge info">{a.documentType}</span></td>
                <td className="strong">{a.title}</td>
                <td className="muted">{a.drafterName}</td>
                <td className="num muted">{a.amount > 0 ? fmtKRW(a.amount) : "—"}</td>
                <td className="tnum"><span className={a.status === "IN_PROGRESS" ? "step-bar" : "muted"}>{a.currentStep}/{a.totalSteps}</span></td>
                <td className="muted">{fmtDateTime(a.createdAt)}</td>
                <td><span className={`badge ${STATUS[a.status]}`}>{a.status}</span></td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </>
  );
}

// ============================================================
// CRM CUSTOMER DETAIL PANEL
// ============================================================
function CustomerPanel({ customerId, store, onClose, lang }) {
  const cust = CRM_CUSTOMERS.find(c => c.id === customerId);
  if (!cust) return null;
  const orders = (store?.orders || []).filter(o => o.customerId === customerId);
  const consultations = CONSULTATIONS.filter(c => c.customerId === customerId);
  const claims = CLAIMS.filter(c => c.customerId === customerId);
  // Generate a 6-month sales sparkline
  const monthly = [3.2, 4.1, 5.8, 4.6, 6.2, 7.4].map(v => Math.round(v * (cust.totalSales / 60_000_000) * 1_000_000));
  const peak = Math.max(...monthly, 1);

  const GRADE_TONE = { VIP: "accent", GOLD: "warn", SILVER: "info", NORMAL: "neutral" };

  return (
    <>
      <div className="sp-head">
        <div style={{ display: "flex", gap: 12, alignItems: "center" }}>
          <div className="cust-avatar">{cust.name.slice(0, 2)}</div>
          <div>
            <div className="sp-title">{cust.name}</div>
            <div className="sp-sub">{cust.code} · {cust.contact}</div>
          </div>
        </div>
        <button className="sp-close" onClick={onClose}><Icon.X /></button>
      </div>
      <div className="sp-body">
        <div className="kpi-grid" style={{ gridTemplateColumns: "1fr 1fr 1fr", gap: 10 }}>
          <div className="kpi" style={{ padding: 12 }}>
            <div className="kpi-label" style={{ fontSize: 10.5 }}>{lang === "en" ? "Grade" : "등급"}</div>
            <div style={{ marginTop: 6 }}><span className={`badge ${GRADE_TONE[cust.grade]}`}>{cust.grade}</span></div>
          </div>
          <div className="kpi" style={{ padding: 12 }}>
            <div className="kpi-label" style={{ fontSize: 10.5 }}>{lang === "en" ? "Total Sales" : "누적 매출"}</div>
            <div className="strong tnum" style={{ marginTop: 6, fontSize: 15 }}>{fmtKRW(cust.totalSales)}</div>
          </div>
          <div className="kpi" style={{ padding: 12 }}>
            <div className="kpi-label" style={{ fontSize: 10.5 }}>{lang === "en" ? "Open Claims" : "미해결 클레임"}</div>
            <div className="strong tnum" style={{ marginTop: 6, fontSize: 15, color: cust.openClaims > 0 ? "var(--danger-ink)" : "inherit" }}>{cust.openClaims}</div>
          </div>
        </div>

        <div>
          <div className="section-h">{lang === "en" ? "6-month sales" : "최근 6개월 매출"}</div>
          <div className="bar-chart">
            {monthly.map((v, i) => (
              <div key={i} className="bar-col">
                <div className="bar-fill" style={{ height: `${(v / peak) * 100}%`, animationDelay: `${i * 80}ms` }} />
                <div className="bar-label">{i + 1}</div>
              </div>
            ))}
          </div>
        </div>

        <div>
          <div className="section-h">{lang === "en" ? `Recent orders (${orders.length})` : `최근 주문 (${orders.length})`}</div>
          {orders.length === 0 ? <div className="muted" style={{ fontSize: 12 }}>{lang === "en" ? "No orders" : "주문 없음"}</div> :
            <div className="mini-list">
              {orders.slice(0, 5).map(o => (
                <div className="mini-row" key={o.id}>
                  <span className="mono muted">#{o.id}</span>
                  <span className="strong" style={{ flex: 1, marginLeft: 10 }}>{lookupProduct(o.lines[0].productId).name}{o.lines.length > 1 ? ` 외 ${o.lines.length - 1}` : ""}</span>
                  <span className="tnum strong">{fmtKRW(o.total)}</span>
                  <span className="muted" style={{ marginLeft: 10, fontSize: 11.5 }}>{fmtDate(o.placedAt)}</span>
                </div>
              ))}
            </div>}
        </div>

        <div>
          <div className="section-h">{lang === "en" ? `Consultations (${consultations.length})` : `상담 이력 (${consultations.length})`}</div>
          {consultations.length === 0 ? <div className="muted" style={{ fontSize: 12 }}>{lang === "en" ? "No consultations" : "상담 없음"}</div> :
            <div className="timeline-mini">
              {consultations.map(c => (
                <div key={c.id} className="tl-row">
                  <div className="tl-dot" />
                  <div style={{ flex: 1 }}>
                    <div style={{ display: "flex", justifyContent: "space-between" }}>
                      <span className="strong" style={{ fontSize: 13 }}>{c.title}</span>
                      <span className="muted" style={{ fontSize: 11 }}>{fmtRelative(c.createdAt)}</span>
                    </div>
                    <div className="muted" style={{ fontSize: 11.5, marginTop: 2 }}>{c.type} · {c.handler}</div>
                  </div>
                </div>
              ))}
            </div>}
        </div>

        <div>
          <div className="section-h">{lang === "en" ? `Claims (${claims.length})` : `클레임 (${claims.length})`}</div>
          {claims.length === 0 ? <div className="muted" style={{ fontSize: 12 }}>{lang === "en" ? "No claims" : "클레임 없음"}</div> :
            <div className="mini-list">
              {claims.map(c => {
                const tone = c.status === "OPEN" ? "danger" : c.status === "IN_PROGRESS" ? "warn" : "ok";
                return (
                  <div className="mini-row" key={c.id}>
                    <span className="mono muted">CL-{c.id}</span>
                    <span style={{ flex: 1, marginLeft: 10 }}>{c.title}</span>
                    <span className={`badge ${c.priority === "HIGH" ? "danger" : c.priority === "MEDIUM" ? "warn" : "neutral"}`}>{c.priority}</span>
                    <span className={`badge ${tone}`} style={{ marginLeft: 6 }}>{c.status}</span>
                  </div>
                );
              })}
            </div>}
        </div>
      </div>
      <div className="sp-foot">
        <button className="btn">{lang === "en" ? "New consultation" : "상담 등록"}</button>
        <button className="btn btn-accent" style={{ marginLeft: "auto" }}>{lang === "en" ? "Open quote" : "견적 발행"}</button>
      </div>
    </>
  );
}

// ============================================================
// CRM PAGE v2 — clickable rows that open CustomerPanel
// ============================================================
function CrmPageV2({ lang, onOpenCustomer }) {
  const [tab, setTab] = uS4("customers");
  const GRADE = { VIP: "accent", GOLD: "warn", SILVER: "info", NORMAL: "neutral" };
  const CLAIM_STATUS = { OPEN: "danger", IN_PROGRESS: "warn", RESOLVED: "ok", CLOSED: "neutral" };
  return (
    <>
      <PageHead title={lang === "en" ? "Customers" : "고객 관리"}
        sub={lang === "en" ? `${CRM_CUSTOMERS.length} customers · ${CLAIMS.filter(c => c.status !== "RESOLVED" && c.status !== "CLOSED").length} open claims` : `고객 ${CRM_CUSTOMERS.length}명 · 미해결 클레임 ${CLAIMS.filter(c => c.status !== "RESOLVED" && c.status !== "CLOSED").length}건`}
        actions={
          <div style={{ display: "flex", gap: 0, border: "1px solid var(--border)", borderRadius: 6, overflow: "hidden" }}>
            <button className={`btn btn-sm ${tab === "customers" ? "btn-primary" : "btn-ghost"}`} style={{ borderRadius: 0 }} onClick={() => setTab("customers")}>{lang === "en" ? "Customers" : "고객"}</button>
            <button className={`btn btn-sm ${tab === "consult" ? "btn-primary" : "btn-ghost"}`} style={{ borderRadius: 0 }} onClick={() => setTab("consult")}>{lang === "en" ? "Consultations" : "상담"}</button>
            <button className={`btn btn-sm ${tab === "claims" ? "btn-primary" : "btn-ghost"}`} style={{ borderRadius: 0 }} onClick={() => setTab("claims")}>{lang === "en" ? "Claims" : "클레임"}</button>
          </div>
        } />

      {tab === "customers" && (
        <div className="tbl-wrap">
          <table className="tbl">
            <thead><tr>
              <th>{lang === "en" ? "Code" : "코드"}</th><th>{lang === "en" ? "Name" : "고객명"}</th><th>{lang === "en" ? "Contact" : "연락처"}</th>
              <th>{lang === "en" ? "Grade" : "등급"}</th><th className="num">{lang === "en" ? "Total Sales" : "누적 매출"}</th>
              <th className="num">{lang === "en" ? "Open Claims" : "클레임"}</th><th>{lang === "en" ? "Sales rep" : "담당"}</th>
            </tr></thead>
            <tbody>
              {CRM_CUSTOMERS.map((c, i) => (
                <tr key={c.id} className="stagger row-clickable" style={{ animationDelay: `${i * 22}ms` }} onClick={() => onOpenCustomer(c.id)}>
                  <td className="id">{c.code}</td>
                  <td className="strong">{c.name}</td>
                  <td className="mono muted" style={{ fontSize: 11.5 }}>{c.contact}</td>
                  <td><span className={`badge ${GRADE[c.grade]}`}>{c.grade}</span></td>
                  <td className="num strong">{fmtKRW(c.totalSales)}</td>
                  <td className="num">{c.openClaims > 0 ? <span className="badge danger">{c.openClaims}</span> : <span className="muted">—</span>}</td>
                  <td className="muted">{c.assigned}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {tab === "consult" && (
        <div className="tbl-wrap">
          <table className="tbl">
            <thead><tr><th>ID</th><th>{lang === "en" ? "Customer" : "고객"}</th><th>{lang === "en" ? "Type" : "유형"}</th><th>{lang === "en" ? "Title" : "제목"}</th><th>{lang === "en" ? "Handler" : "담당자"}</th><th>{lang === "en" ? "Created" : "일시"}</th></tr></thead>
            <tbody>
              {CONSULTATIONS.map((c, i) => (
                <tr key={c.id} className="stagger row-clickable" style={{ animationDelay: `${i * 22}ms` }} onClick={() => onOpenCustomer(c.customerId)}>
                  <td className="id">CN-{c.id}</td>
                  <td className="strong">{lookupCustomer(c.customerId).name}</td>
                  <td><span className="badge info">{c.type}</span></td>
                  <td>{c.title}</td>
                  <td className="muted">{c.handler}</td>
                  <td className="muted">{fmtDateTime(c.createdAt)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {tab === "claims" && (
        <div className="tbl-wrap">
          <table className="tbl">
            <thead><tr><th>ID</th><th>{lang === "en" ? "Customer" : "고객"}</th><th>{lang === "en" ? "Title" : "제목"}</th><th>{lang === "en" ? "Priority" : "우선순위"}</th><th>{lang === "en" ? "Opened" : "접수"}</th><th>{lang === "en" ? "Status" : "상태"}</th></tr></thead>
            <tbody>
              {CLAIMS.map((c, i) => (
                <tr key={c.id} className="stagger row-clickable" style={{ animationDelay: `${i * 22}ms` }} onClick={() => onOpenCustomer(c.customerId)}>
                  <td className="id">CL-{c.id}</td>
                  <td className="strong">{lookupCustomer(c.customerId).name}</td>
                  <td>{c.title}</td>
                  <td><span className={`badge ${c.priority === "HIGH" ? "danger" : c.priority === "MEDIUM" ? "warn" : "neutral"}`}>{c.priority}</span></td>
                  <td className="muted">{fmtDateTime(c.openedAt)}</td>
                  <td><span className={`badge ${CLAIM_STATUS[c.status]}`}>{c.status}</span></td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </>
  );
}

Object.assign(window, {
  ExtStoreProvider, useExtStore,
  PaymentsPageV2, SettlementPageV2, ApprovalsPageV2, CrmPageV2,
  ApprovalPanel, CustomerPanel, RefundModal,
});
